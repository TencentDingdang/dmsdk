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

import com.tencent.ai.tvs.base.log.DMLog;
import com.tencent.ai.tvs.tskm.thirdpartyauth.CPAuthType;
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

import androidx.annotation.NonNull;

/**
 * 使用QQ音乐APP和QQ音乐SDK进行授权的处理逻辑，点击授权页面H5上的“QQ音乐APP授权”，将会调用此类中的requestCpAuthCredential方法
 */
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
    private static final String QQ_MUSIC_DOWNLOAD_URL = "https://sj.qq.com/myapp/detail.htm?apkName=com.tencent.qqmusic";
    private static final long[] BIND_SERVICE_RETRY_INTERVALS = new long[]{ 100, 200, 300, 500 };
	private final String mAppId;
    private final String mPrivateKey;
    private final String mCallbackUrl;
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

    protected ThirdPartyAuthCallback mCallback;
    private boolean mBound = false;
    private boolean mInitialized = false;
    private IQQMusicApi mQQMusicApi = null;

    public QQMusicAuthAgent(Context context, String appId, String privateKey, String callbackUrl) {
        mAppId = appId;
        mPrivateKey = privateKey;
        mCallbackUrl = callbackUrl;
        mRandom = new Random();
        mContext = context.getApplicationContext();
        mPackageManager = context.getPackageManager();
        mUiHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public CPAuthType getAuthType() {
        return CPAuthType.APP;
    }

    @Override
    public boolean checkCpAppInstallation() {
        // 判定QQ音乐APP是否已经安装
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
        intent.setData(Uri.parse(QQ_MUSIC_DOWNLOAD_URL));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    public void requestCpAuthCredential(ThirdPartyAuthCallback callback) {
        // 在授权页面上点击“QQ音乐APP授权”，本方法将被页面调用
        mCallback = callback;
        bindServiceIfNeeded(0);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestory() {

    }

    /**
     * 尝试绑定QQ音乐后台服务，若失败会多次重试。
     * @param retry 当前重试次数，第1次是100ms后，第2次是200ms后，第3次是300ms后，第4次是500ms后，第4次失败则返回失败。
     */
    private void bindServiceIfNeeded(int retry) {
        DMLog.i(TAG, "bindServiceIfNeeded: retry = " + retry);
        if (mBound) {
            initSdkIfNeeded();
            return;
        }
        Intent intent = new Intent("com.tencent.qqmusic.third.api.QQMusicApiService");
        intent.setPackage(PN_QQ_MUSIC);
        if (!mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)) {
            if (retry < 4) {
                DMLog.i(TAG, "bindServiceIfNeeded: Failed on retry #" + retry);
                CommonCmd.startQQMusicProcess(mContext, PN_QQ_MUSIC);
                mUiHandler.postDelayed(() -> bindServiceIfNeeded(retry + 1), BIND_SERVICE_RETRY_INTERVALS[retry]);
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
        // 按照QQ音乐SDK的流程进行授权，可以查阅QQ音乐平台文档 4 Android接入，链接请查阅QQ音乐授权接入文档
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
            // QQ音乐SDK或者QQ音乐APP的后台服务返回错误码，请联系云小微FTO
            onFailure(CpAuthError.FAILED_TO_CONNECT_TO_APP, "QQ音乐授权初始化失败，无法连接QQ音乐APP后台服务" + e);
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
            // QQ音乐SDK或者QQ音乐APP的后台服务返回错误码，请联系云小微FTO
            onFailure(CpAuthError.FAILED_TO_REQUEST_AUTH, "QQ音乐授权初始化失败，从QQ音乐SDK返回了错误：" + code);
            return;
        }
        mInitialized = true;
        requestAuth();
    }

    // QQ音乐APP的后台服务，返回了授权结果
    void onAuthResultFromQqMusic(int ret) {
        switch (ret) {
            case AUTH_RESULT_OK:
                // 授权成功
                mInitialized = true;
                requestAuth();
                break;
            case AUTH_RESULT_ERROR:
                // 授权失败，一般是QQ音乐的问题，请联系云小微FTO
                onFailure(CpAuthError.FAILED_TO_REQUEST_AUTH, "获取QQ音乐授权结果失败");
                break;
            case AUTH_RESULT_CANCEL:
                // 在QQ音乐授权界面点击了取消
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
            // 调用QQ音乐SDK进行授权
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

                    // 从QQ音乐SDK的回应中解析授权结果
                    if (code != ErrorCodes.ERROR_OK) {
                        // SDK返回错误码，需要查询QQ音乐SDK的错误码表，并联系下云小微FTO
                        onFailure(CpAuthError.FAILED_TO_REQUEST_AUTH, "获取授权结果失败，错误的回应" + code);
                        return;
                    }
                    // 从SDK的回应中获取授权结果，这是一个加密后的字符串，必须得先解密
                    String decrypted = OpenIDHelper.decryptQQMEncryptString(bundle.getString(Keys.API_RETURN_KEY_ENCRYPT_STRING), mPrivateKey);
                    if (decrypted == null) {
                        // 解密失败的情况，如果出现这个问题，需要联系云小微FTO
                        onFailure(CpAuthError.FAILED_TO_REQUEST_AUTH, "授权结果解码失败");
                        return;
                    }

                    // 解密后的授权结果是一个json字符串，需要解析出open id、open token等
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
                        // 解析授权结果失败，如果出现这个问题，需要联系云小微FTO
                        onFailure(CpAuthError.FAILED_TO_REQUEST_AUTH, "授权结果解析错误");
                        return;
                    }
                    if (!OpenIDHelper.checkQMSign(sign, nonce)) {
                        // 对结果进行校验失败，如果出现这个问题，需要联系云小微FTO
                        onFailure(CpAuthError.FAILED_TO_REQUEST_AUTH, "授权结果校验出错");
                        return;
                    }
                    DMLog.i(TAG, "requestAuth: OpenID = [" + openId + "]");
                    DMLog.d(TAG, "requestAuth: OpenToken = [" + openToken + "]");
                    // 授权成功，将授权结果提交给H5页面
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
        // 通知H5页面，授权成功
        mUiHandler.post(() -> {
            if (mCallback != null) {
                mCallback.onSuccess(credential);
                mCallback = null;
            }
        });
    }

    protected void onFailure(int code, String displayMessage) {
        // 通知H5页面，授权失败
        DMLog.e(TAG, "[QQMusicAuth]onFailure, code " + code + ", " + displayMessage);
        mUiHandler.post(() -> {
            if (mCallback != null) {
                mCallback.onFailure(code, displayMessage);
                mCallback = null;
            }
        });
    }
}
