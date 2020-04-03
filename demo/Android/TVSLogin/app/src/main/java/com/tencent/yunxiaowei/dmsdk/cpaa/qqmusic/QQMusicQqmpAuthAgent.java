package com.tencent.yunxiaowei.dmsdk.cpaa.qqmusic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.tencent.ai.tvs.base.log.DMLog;
import com.tencent.ai.tvs.dmsdk.demo.DemoConstant;
import com.tencent.ai.tvs.dmsdk.demo.DemoPreference;
import com.tencent.ai.tvs.tskm.TVSTSKM;
import com.tencent.ai.tvs.tskm.thirdpartyauth.CPAuthType;
import com.tencent.ai.tvs.tskm.thirdpartyauth.CpAuthAgent;
import com.tencent.ai.tvs.tskm.thirdpartyauth.CpAuthError;
import com.tencent.ai.tvs.tskm.thirdpartyauth.CpCredential;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用于使用QQ音乐小程序，进行QQ音乐授权的流程
 */
public class QQMusicQqmpAuthAgent implements CpAuthAgent {
    private static final String TAG = "QQMusicQqmpAuthAgent";
    private final Context mContext;
    private final String mAppId;
    private final String mPrivateKey;
    private final String mCallbackUrl;
    private DemoPreference preference = new DemoPreference();
    private final Handler mUiHandler;
    protected ThirdPartyAuthCallback mCallback;

    private String mAuthCode;
    private static final String STR_DOMAIN = "fcg_music_custom";
    private static final String STR_GET_URL_INTENT = "sdk_get_qr_code";
    
    private static final String PN_QQ = "com.tencent.mobileqq";

    // 为了支持QQ小程序授权，QQ APP的版本号必须大于等于8.0.5
    private static final int REQUIRED_MAJOR = 8;
    private static final int REQUIRED_MINOR = 0;
    private static final int REQUIRED_PATCH = 5;
    
    private QqmpAuthHelper qqmpAuthHelper;

    public QQMusicQqmpAuthAgent(Context context, String appId, String privateKey, String callbackUrl) {
        mContext = context.getApplicationContext();
        mAppId = appId;
        mPrivateKey = privateKey;
        mCallbackUrl = callbackUrl;
        mUiHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public CPAuthType getAuthType() {
        return CPAuthType.QQ;
    }

    @Override
    public boolean checkCpAppInstallation() {
        return AppUtils.checkAppInstallation(mContext, PN_QQ, REQUIRED_MAJOR, REQUIRED_MINOR, REQUIRED_PATCH, TAG);
    }

    @Override
    public void jumpToAppDownload() {
        AppUtils.jumpToDownload(mContext, PN_QQ);
    }

    protected QqmpAuthHelper creatQqmpAuthHelper() {
        return new QqmpAuthHelper();
    }

    protected void onFailureEx(int code, String moreErrorMsg, Throwable e) {
        String errorMsg = "";
        switch (code) {
            case CpAuthError.QQMp.ERROR_GET_QR_CODE_INVALID_RESPONSE:
                errorMsg = "QQ小程序二维码解析失败，错误的服务器回应：" + moreErrorMsg;
                break;
            case CpAuthError.QQMp.ERROR_GET_QR_CODE_INVALID_URL:
                errorMsg = "获取QQ小程序二维码URL失败：" + moreErrorMsg;
                break;
            case CpAuthError.QQMp.ERROR_GET_QR_CODE_INVALID_REQUEST:
                errorMsg = "发送请求失败：" + moreErrorMsg;
                break;
        }

        onFailure(code, errorMsg, e);
    }

    // 解析二维码回应
    protected void  parseCpAuthCredentialJsonBlob(String body, String productId, String dsn) {
        try {
            JSONObject bodyJson = new JSONObject(body);
            int ret = bodyJson.getInt("ret");
            if (ret != 0) {
                // 这里是QQ音乐回应了错误码，根据ret、sub_ret和Msg进行初步分析，向云小微FTO反馈
                int subRet = bodyJson.optInt("sub_ret");
                String msg = bodyJson.optString("msg");
                onFailureEx(CpAuthError.QQMp.ERROR_GET_QR_CODE_INVALID_RESPONSE,
                        "ret " + ret + ", sub ret " + subRet + ", message " + msg, null);
                return;
            }
            String qrCode = bodyJson.getString("sdk_qr_code");
            mAuthCode = bodyJson.getString("auth_code");
            // 获取到了二维码，开启QQ小程序
            DMLog.i(TAG, "send uri " + qrCode);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(qrCode));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);

            mUiHandler.post(() -> {
                if (qqmpAuthHelper != null) {
                    // 如果上一个任务还在查询授权结果，先停止
                    qqmpAuthHelper.release();
                }

                // 等resume之后可以开始查询授权结果
                qqmpAuthHelper = creatQqmpAuthHelper();
                qqmpAuthHelper.init(this, mAppId, mAuthCode, mPrivateKey, productId, dsn);
            });
        } catch (Exception e) {
            // 获取二维码失败，可以跟进一下Exception的类型
            onFailureEx(CpAuthError.QQMp.ERROR_GET_QR_CODE_INVALID_URL, e.toString(), e);
        }
    }

    // 获取小程序二维码
    private void requestCpAuthCredentialByUniaccess(ThirdPartyAuthCallback callback) {
        mCallback = callback;

        // 用私钥对请求进行加密
        String encryptString = OpenIDHelper.getEncryptString(String.valueOf(System.currentTimeMillis()), mPrivateKey, mCallbackUrl);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("qqmusic_open_appid", mAppId);
            jsonObject.put("qqmusic_package_name", mContext.getPackageName());
            jsonObject.put("qqmusic_dev_name", "腾讯叮当");
            jsonObject.put("qqmusic_encrypt_auth", encryptString);
            jsonObject.put("qqmusic_qrcode_type", "qq");
        } catch (JSONException e) {
            e.printStackTrace();
            // 检查一下appid和private key是否正确
            onFailureEx(CpAuthError.QQMp.ERROR_GET_QR_CODE_INVALID_REQUEST, "请求体组装失败，" + e, e);
            return;
        }

        final String productId = preference.loadProductID(mContext);
        final String dsn = preference.loadDSN(mContext);

        DMLog.i(TAG, "send qm request productid " + productId);
        DMLog.i(TAG, "send qm request " + jsonObject.toString());

        TVSTSKM tvstskm = new TVSTSKM(productId, dsn);

        // 发送获取二维码请求
        tvstskm.sendUniAccessRequest(STR_DOMAIN, STR_GET_URL_INTENT, jsonObject.toString(), (code, errorMessage, jsonBlobInfoResponse) -> {
            DMLog.i(TAG, "code " + code + " error " + errorMessage + " jsonBlobInfoResponse: " + jsonBlobInfoResponse);
            if (code != 0) {
                // 云小微后台返回失败，可以检查一下网络是否正常，Product ID和DSN是否正确，如果均正常，查看一下errorMessage，向云小微FTO反馈
                onFailureEx(CpAuthError.QQMp.ERROR_GET_QR_CODE_INVALID_REQUEST, "code:" + code + ", " + errorMessage, null);
                return;
            }

            parseCpAuthCredentialJsonBlob(jsonBlobInfoResponse, productId, dsn);
        });
    }

    @Override
    public void requestCpAuthCredential(ThirdPartyAuthCallback callback) {
        requestCpAuthCredentialByUniaccess(callback);
    }

    void onSuccess(CpCredential credential) {
        DMLog.i(TAG, "onSuccess");
        mUiHandler.post(() -> {
            if (mCallback != null) {
                mCallback.onSuccess(credential);
                mCallback = null;
            }
        });
    }

    void onFailure(int code, String displayMessage, Throwable e) {
        if (e != null) {
            DMLog.e(TAG, "[QQMusicAuth]onFailure, code " + code + ", " + displayMessage, e);
        } else {
            DMLog.e(TAG, "[QQMusicAuth]onFailure, code " + code + ", " + displayMessage);
        }
        mUiHandler.post(() -> {
            if (mCallback != null) {
                mCallback.onFailure(code, displayMessage);
                //mCallback = null;
            }
        });
    }

    @Override
    public void onResume() {
        mUiHandler.post(() -> {
            if (qqmpAuthHelper == null) {
                return;
            }

            qqmpAuthHelper.start();
        });
    }

    @Override
    public void onDestory() {
        mCallback = null;
        mUiHandler.post(() -> {
            if (qqmpAuthHelper == null) {
                return;
            }

            qqmpAuthHelper.release();
            qqmpAuthHelper = null;
        });
    }
}
