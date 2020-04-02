package com.tencent.yunxiaowei.dmsdk.cpaa.qqmusic;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.tencent.ai.tvs.base.log.DMLog;
import com.tencent.ai.tvs.tskm.thirdpartyauth.CPAuthType;
import com.tencent.ai.tvs.tskm.thirdpartyauth.CpAuthAgent;
import com.tencent.ai.tvs.tskm.thirdpartyauth.CpAuthError;
import com.tencent.ai.tvs.tskm.thirdpartyauth.CpCredential;
import com.tencent.ai.tvs.tskm.thirdpartyauth.ThirdPartyCp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.qqmusic.third.api.contract.Keys;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 使用微信小程序进行QQ音乐授权的逻辑
 */
public class QQMusicWcmpAuthAgent implements CpAuthAgent {
    private static final String TAG = "QQMusicWcmpAuthAgent";
    private final String mAppId;
    private final String mPrivateKey;
    private final String mPackageName;
    private final IWXAPI mApi;
    private final Handler mUiHandler;
    protected ThirdPartyAuthCallback mCallback;
    private Context context;

    private static final String PACKAGE_NAME = "com.tencent.mm";

    // 为了支持微信小程序授权，微信APP的版本号必须大于等于6.6.7
    private static final int REQUIRED_MAJOR = 6;
    private static final int REQUIRED_MINOR = 6;
    private static final int REQUIRED_PATCH = 7;

    public QQMusicWcmpAuthAgent(Context context, String appId, String privateKey, String packageName, IWXAPI api) {
        mAppId = appId;
        mPrivateKey = privateKey;
        mPackageName = packageName;
        mApi = api;
        mUiHandler = new Handler(Looper.getMainLooper());
        this.context = context;
    }

    @Override
    public CPAuthType getAuthType() {
        return CPAuthType.Weixin;
    }

    @Override
    public boolean checkCpAppInstallation() {
        return AppUtils.checkAppInstallation(context, PACKAGE_NAME, REQUIRED_MAJOR, REQUIRED_MINOR, REQUIRED_PATCH, TAG);
    }

    @Override
    public void jumpToAppDownload() {
        AppUtils.jumpToDownload(context, PACKAGE_NAME);
    }

    protected void onFailure(int code) {
        String errorMsg;
        switch (code) {
            case CpAuthError.WXMp.ERROR_LAUNCH_WXMP:
                errorMsg = "拉起小程序失败";
                break;
            case CpAuthError.WXMp.ERROR_INVALID_RESPONSE:
                errorMsg = "无效的回应数据";
                break;
            case CpAuthError.WXMp.ERROR_DECRYPT_FAILED:
                errorMsg = "授权结果解码失败";
                break;
            case CpAuthError.WXMp.ERROR_RESULT_UNPACK_FAILED:
                errorMsg = "授权结果解析失败";
                break;
            case CpAuthError.WXMp.ERROR_CHECK_SIGN_FAILED:
                errorMsg = "授权结果校验失败";
                break;
            default:
                errorMsg = "";
        }

        onFailure(code, errorMsg);
    }

    @Override
    public void requestCpAuthCredential(ThirdPartyAuthCallback callback) {
        // 按照QQ音乐规定的流程，开始授权，可以参考QQ音乐平台参考文档 6.1节（可以从云小微QQ音乐接入文档中跳转）
        DMLog.i(TAG, "requestCpAuthCredential to weixin APP");
        mCallback = callback;
        String encryptString = OpenIDHelper.getEncryptString(String.valueOf(System.currentTimeMillis()), mPrivateKey, null);
        try {
            encryptString = URLEncoder.encode(encryptString, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
            // We are hardcoding the encoding, so the exception should never be thrown
        }
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = "gh_1dac5028a5dd";
        req.path = "pages/auth/auth?appId=" + mAppId + "&packageName=" + mPackageName + "&encryptString=" + encryptString;
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;
        if (!mApi.sendReq(req)) {
            onFailure(CpAuthError.WXMp.ERROR_LAUNCH_WXMP);
        }
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestory() {

    }

    public void onWeChatResp(String extraData) {
        // 微信小程序关闭后，返回授权结果
        DMLog.i(TAG, "requestAuth: onWeChatResp, extraData = [" + (extraData == null ? "" : extraData) + "]");
        if (TextUtils.isEmpty(extraData)) {
            onFailure(CpAuthError.WXMp.ERROR_DECRYPT_FAILED);
        }
        // 对授权结果进行解码
        String decrypted = OpenIDHelper.decryptQQMEncryptString(extraData, mPrivateKey);
        if (TextUtils.isEmpty(decrypted)) {
            // 解码失败的情况
            onFailure(CpAuthError.WXMp.ERROR_DECRYPT_FAILED);
            return;
        }
        String sign;
        String nonce;
        String openId;
        String openToken;
        long expireTime;

        DMLog.i(TAG, "requestAuth: decrypted = [" + decrypted + "]");

        // 解码后的字符串是JSON，需要进行解析
        try {
            JSONObject jo = new JSONObject(decrypted);
            sign = jo.getString(Keys.API_RETURN_KEY_SIGN);
            nonce = jo.getString(Keys.API_RETURN_KEY_NONCE);
            openId = jo.getString(Keys.API_RETURN_KEY_OPEN_ID);
            openToken = jo.getString(Keys.API_RETURN_KEY_OPEN_TOKEN);
            expireTime = jo.getLong(Keys.API_PARAM_KEY_SDK_EXPIRETIME) * 1000; // Must be millisecond
        } catch (JSONException e) {
            onFailure(CpAuthError.WXMp.ERROR_RESULT_UNPACK_FAILED);
            return;
        }

        // 对授权结果进行校验
        if (!OpenIDHelper.checkQMSign(sign, nonce)) {
            // 失败
            onFailure(CpAuthError.WXMp.ERROR_CHECK_SIGN_FAILED);
            return;
        }
        DMLog.i(TAG, "requestAuth: OpenID = [" + openId + "]");
        DMLog.d(TAG, "requestAuth: OpenToken = [" + openToken + "]");
        // 校验成功，授权成功，获取open id、open token和过期时间
        onSuccess(new CpCredential(ThirdPartyCp.QQ_MUSIC, mAppId, openId, openToken, expireTime));
    }

    private void onSuccess(CpCredential credential) {
        mUiHandler.post(() -> {
            if (mCallback != null) {
                mCallback.onSuccess(credential);
                mCallback = null;
            }
        });
    }

    protected void onFailure(int code, String displayMessage) {

        DMLog.e(TAG, "[QQMusicAuth]onFailure, code " + code + ", " + displayMessage);

        mUiHandler.post(() -> {
            if (mCallback != null) {
                mCallback.onFailure(code, displayMessage);
                mCallback = null;
            }
        });
    }
}
