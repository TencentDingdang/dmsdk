package com.tencent.yunxiaowei.dmsdk.cpaa.qqmusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import com.tencent.ai.tvs.base.log.DMLog;
import com.tencent.ai.tvs.tskm.thirdpartyauth.CpAuthAgent;
import com.tencent.ai.tvs.tskm.thirdpartyauth.CpAuthError;
import com.tencent.ai.tvs.tskm.thirdpartyauth.CpCredential;
import com.tencent.ai.tvs.tskm.thirdpartyauth.ThirdPartyCp;
import com.tencent.qqmusic.third.api.contract.CommonCmd;
import com.tencent.qqmusic.third.api.contract.ErrorCodes;
import com.tencent.qqmusic.third.api.contract.IQQMusicApi;
import com.tencent.qqmusic.third.api.contract.IQQMusicApiCallback;
import com.tencent.qqmusic.third.api.contract.Keys;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QQMusicAuthAgent implements CpAuthAgent {
    public static final int AUTH_RESULT_OK = 0;
    public static final int AUTH_RESULT_ERROR = 1;
    public static final int AUTH_RESULT_CANCEL = 2;
    private static final String TAG = "QQMusicAuthAgent";
    private static final String PN_QQ_MUSIC = "com.tencent.qqmusic";
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    // We require QQ Music v8.9.6 or higher
    private static final Pattern PATTERN_QQ_MUSIC_VERSION_NAME = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+).*");
    private static final int REQUIRED_QQ_MUSIC_MAJOR = 8;
    private static final int REQUIRED_QQ_MUSIC_MINOR = 9;
    private static final int REQUIRED_QQ_MUSIC_PATCH = 6;
    private final String mAppId;
    private final String mPrivateKey;
    private final String mCallbackUrl;
    private final String mDownloadUrl;
    private final Random mRandom;
    private final Context mContext;
    private final PackageManager mPackageManager;
    private final Handler mUiHandler;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DMLog.d(TAG, "onServiceConnected: name = " + name);
            onQqMusicServiceConnected(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            mQQMusicApi = null;
        }
    };

    private ThirdPartyAuthCallback mCallback;
    private boolean mBound = false;
    private boolean mInitialized = false;
    private IQQMusicApi mQQMusicApi = null;

    public QQMusicAuthAgent(Context context, String appId, String privateKey, String callbackUrl, String downloadUrl) {
        mAppId = appId;
        mPrivateKey = privateKey;
        mCallbackUrl = callbackUrl;
        mDownloadUrl = downloadUrl;
        mRandom = new Random();
        mContext = context.getApplicationContext();
        mPackageManager = context.getPackageManager();
        mUiHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public boolean checkCpAppInstallation() {
        try {
            PackageInfo packageInfo = mPackageManager.getPackageInfo(PN_QQ_MUSIC, 0);
            String versionName = packageInfo.versionName;
            DMLog.i(TAG, "checkCpAppInstallation: versionName = [" + versionName + "]");
            Matcher matcher = PATTERN_QQ_MUSIC_VERSION_NAME.matcher(versionName);
            if (!matcher.matches()) {
                return false;
            }
            int major = Integer.parseInt(matcher.group(1));
            int minor = Integer.parseInt(matcher.group(2));
            int patch = Integer.parseInt(matcher.group(3));
            return major > REQUIRED_QQ_MUSIC_MAJOR ||
                    (major == REQUIRED_QQ_MUSIC_MAJOR && minor > REQUIRED_QQ_MUSIC_MINOR) ||
                    (major == REQUIRED_QQ_MUSIC_MAJOR && minor == REQUIRED_QQ_MUSIC_MINOR && patch >= REQUIRED_QQ_MUSIC_PATCH);
        } catch (PackageManager.NameNotFoundException | NumberFormatException e) {
            DMLog.i(TAG, "checkCpAppInstallation: Fail to check installed version: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void jumpToAppDownload() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(mDownloadUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    public void requestCpAuthCredential(ThirdPartyAuthCallback callback) {
        mCallback = callback;
        bindServiceIfNeeded(true);
    }

    private void bindServiceIfNeeded(boolean retry) {
        if (mBound) {
            initSdkIfNeeded();
            return;
        }
        Intent intent = new Intent("com.tencent.qqmusic.third.api.QQMusicApiService");
        intent.setPackage(PN_QQ_MUSIC);
        if (!mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)) {
            if (retry) {
                CommonCmd.startQQMusicProcess(mContext, PN_QQ_MUSIC);
                mUiHandler.postDelayed(() -> bindServiceIfNeeded(false), 100);
            } else {
                onFailure(CpAuthError.FAILED_TO_CONNECT_TO_APP, "绑定QQ音乐后台服务失败");
            }
        }
    }

    private void onQqMusicServiceConnected(IBinder service) {
        mBound = true;
        mInitialized = false;
        mQQMusicApi = IQQMusicApi.Stub.asInterface(service);
        CommonCmd.init(CommonCmd.AIDL_PLATFORM_TYPE_PHONE);
        // Init SDK via sayHi
        initSdkIfNeeded();
    }

    private void initSdkIfNeeded() {
        if (mInitialized) {
            requestAuth();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(Keys.API_PARAM_KEY_SDK_VERSION, CommonCmd.SDK_VERSION);
        Bundle result;
        try {
            result = mQQMusicApi.execute("hi", bundle);
        } catch (RemoteException e) {
            onFailure(CpAuthError.FAILED_TO_CONNECT_TO_APP, "QQ音乐授权初始化失败");
            return;
        }
        int code = result.getInt(Keys.API_RETURN_KEY_CODE);
        DMLog.d(TAG, "initSdkIfNeeded: result.code = " + code);
        if (code == ErrorCodes.ERROR_API_NO_PERMISSION) {
            // Launch QQ Music for authorization
            CommonCmd.verifyCallerIdentity(mContext, mAppId, mContext
                    .getPackageName(), OpenIDHelper.getEncryptString(generateNonce(), mPrivateKey,
                    mCallbackUrl), mCallbackUrl);
            return;
        }
        if (code != ErrorCodes.ERROR_OK) {
            // Other error cases
            onFailure(CpAuthError.FAILED_TO_REQUEST_AUTH, "QQ音乐授权初始化失败：错误" + code);
            return;
        }
        mInitialized = true;
        requestAuth();
    }

    void onAuthResultFromQqMusic(int ret) {
        switch (ret) {
            case AUTH_RESULT_OK:
                mInitialized = true;
                requestAuth();
                break;
            case AUTH_RESULT_ERROR:
                onFailure(CpAuthError.FAILED_TO_REQUEST_AUTH, "QQ音乐授权失败");
                break;
            case AUTH_RESULT_CANCEL:
                onFailure(CpAuthError.USER_CANCEL, "");
                break;
            default:
                break;
        }
    }

    private void requestAuth() {
        Bundle bundle = new Bundle();
        bundle.putString(Keys.API_RETURN_KEY_ENCRYPT_STRING, OpenIDHelper.getEncryptString(generateNonce(), mPrivateKey, mCallbackUrl));
        try {
            mQQMusicApi.executeAsync("requestAuth", bundle, new IQQMusicApiCallback.Stub() {
                @Override
                public void onReturn(Bundle bundle) {
                    int code = bundle.getInt(Keys.API_RETURN_KEY_CODE);
                    DMLog.d(TAG, "requestAuth: code = " + code);
                    if (code == ErrorCodes.ERROR_NEED_USER_AUTHENTICATION) {
                        // Ask user to login
                        CommonCmd.loginQQMusic(mContext, mCallbackUrl);
                        return;
                    }
                    if (code != ErrorCodes.ERROR_OK) {
                        onFailure(CpAuthError.FAILED_TO_REQUEST_AUTH, "请求用户Token信息失败：错误" + code);
                        return;
                    }
                    String decrypted = OpenIDHelper.decryptQQMEncryptString(bundle.getString(Keys.API_RETURN_KEY_ENCRYPT_STRING), mPrivateKey);
                    if (decrypted == null) {
                        onFailure(CpAuthError.FAILED_TO_REQUEST_AUTH, "请求用户Token信息请求返回数据解析失败");
                        return;
                    }
                    String sign;
                    String nonce;
                    String openId;
                    String openToken;
                    long expireTime;
                    try {
                        JSONObject jo = new JSONObject(decrypted);
                        sign = jo.getString(Keys.API_RETURN_KEY_SIGN);
                        nonce = jo.getString(Keys.API_RETURN_KEY_NONCE);
                        openId = jo.getString(Keys.API_RETURN_KEY_OPEN_ID);
                        openToken = jo.getString(Keys.API_RETURN_KEY_OPEN_TOKEN);
                        expireTime = jo.getLong(Keys.API_PARAM_KEY_SDK_EXPIRETIME) * 1000; // Must be millisecond
                    } catch (JSONException e) {
                        onFailure(CpAuthError.FAILED_TO_REQUEST_AUTH, "请求用户Token信息请求返回数据解析失败");
                        return;
                    }
                    if (!OpenIDHelper.checkQMSign(sign, nonce)) {
                        onFailure(CpAuthError.FAILED_TO_REQUEST_AUTH, "请求用户Token信息请求返回数据非法");
                        return;
                    }
                    DMLog.i(TAG, "requestAuth: OpenID = [" + openId + "]");
                    DMLog.d(TAG, "requestAuth: OpenToken = [" + openToken + "]");
                    onSuccess(new CpCredential(ThirdPartyCp.QQ_MUSIC, mAppId, openId, openToken, expireTime));
                }
            });
        } catch (RemoteException e) {
            onFailure(CpAuthError.FAILED_TO_CONNECT_TO_APP, "请求用户Token信息失败");
        }
    }

    @NonNull
    private String generateNonce() {
        // Create random 32-char string
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; ++i) {
            sb.append(ALPHABET.charAt(mRandom.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    private void onSuccess(CpCredential credential) {
        mUiHandler.post(() -> {
            if (mCallback != null) {
                mCallback.onSuccess(credential);
            }
        });
    }

    private void onFailure(int code, String displayMessage) {
        mUiHandler.post(() -> {
            if (mCallback != null) {
                mCallback.onFailure(code, displayMessage);
            }
        });
    }
}
