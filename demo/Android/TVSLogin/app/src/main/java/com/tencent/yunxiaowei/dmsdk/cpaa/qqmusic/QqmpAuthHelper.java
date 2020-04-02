package com.tencent.yunxiaowei.dmsdk.cpaa.qqmusic;

import android.os.Handler;
import android.os.HandlerThread;

import com.tencent.ai.tvs.base.log.DMLog;
import com.tencent.ai.tvs.dmsdk.demo.DemoConstant;
import com.tencent.ai.tvs.tskm.TVSTSKM;
import com.tencent.ai.tvs.tskm.thirdpartyauth.CpAuthError;
import com.tencent.ai.tvs.tskm.thirdpartyauth.CpCredential;
import com.tencent.ai.tvs.tskm.thirdpartyauth.ThirdPartyCp;
import com.tencent.qqmusic.third.api.contract.Keys;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用于使用QQ小程序进行QQ音乐授权之后，验证授权结果的流程
 */
public class QqmpAuthHelper {
    private Handler mH;
    private HandlerThread mThread;

    private boolean mStarted;
    private QQMusicQqmpAuthAgent mAgent;
    private String mAppId;
    private String mAuthCode;
    private String mPrivateKey;
    private boolean mFinished;

    private static final String TAG = "QqmpAuthHelper";
    private static final String STR_DOMAIN = "fcg_music_custom";
    private static final String STR_GET_RESULT_INTENT = "qrcode_auth_poll";

    private static final int TIME_RETRY = 3000;
    private long mStartTime;
    String productId;
    String dsn;

    public void init(QQMusicQqmpAuthAgent agent, String appId, String authCode, String privateKey, String productId, String dsn) {
        mThread = new HandlerThread("QqmpAuthHelper");
        mThread.start();
        mH = new Handler(mThread.getLooper());
        mAgent = agent;
        mAppId = appId;
        mAuthCode = authCode;
        mPrivateKey = privateKey;
        mStarted = false;
        mFinished = true;
        this.productId = productId;
        this.dsn = dsn;
    }

    private void doLog(String log) {
        DMLog.i(TAG + this, log);
    }

    public synchronized void start() {
        if (mStarted) {
            return;
        }

        mStarted = true;
        mFinished = false;
        mStartTime = System.currentTimeMillis();
        mH.post(() -> {
            boolean start = checkAuthResult();
            if (!start) {
                mH.postDelayed(() -> checkAuthResult(), TIME_RETRY);
            }
        });
    }

    public synchronized void release() {
        mFinished = true;
        mAgent = null;
    }

    protected void onFailureEx(int code, String moreErrorMsg, Throwable e) {
        String errorMsg = "";
        switch (code) {
            case CpAuthError.QQMp.ERROR_GET_QQMP_AUTH_INVALID_REQUEST:
                errorMsg = "发送请求失败 " + moreErrorMsg;
                break;
            case CpAuthError.QQMp.ERROR_GET_QQMP_AUTH_INVALID_RESPONSE:
                errorMsg = "获取授权结果失败，错误的服务器回应 " + moreErrorMsg;
                break;
            case CpAuthError.QQMp.ERROR_GET_QQMP_AUTH_DECRYPT_FAILED:
                errorMsg = "授权信息解密失败" + moreErrorMsg;
                break;
            case CpAuthError.QQMp.ERROR_GET_QQMP_AUTH_CHECK_SIGN_FAILED:
                errorMsg = "授权信息校验失败" + moreErrorMsg;
                break;
            case CpAuthError.QQMp.ERROR_GET_QQMP_AUTH_RESULT_UNPACK_FAILED:
                errorMsg = "授权信息解析失败，无法判断是否授权成功" + moreErrorMsg;
                break;
        }

        onFailure(code, errorMsg, e);
    }

    protected boolean checkAuthResultInner() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("qqmusic_openid_appId", mAppId);
            jsonObject.put("qqmusic_openid_authCode", mAuthCode);

        } catch (JSONException e) {
            e.printStackTrace();
            onFailureEx(CpAuthError.QQMp.ERROR_GET_QQMP_AUTH_INVALID_REQUEST, "请求体组装失败 " + e, e);
            return false;
        }

        doLog("check auth result productid " + productId + ", dsn " + dsn);
        doLog("check auth result, request " + jsonObject.toString());

        TVSTSKM tvstskm = new TVSTSKM(productId, dsn);
        tvstskm.sendUniAccessRequest(STR_DOMAIN, STR_GET_RESULT_INTENT, jsonObject.toString(), (code, errorMessage, jsonBlobInfoResponse) -> {
            doLog("code " + code + " error " + errorMessage + " jsonBlobInfoResponse: " + jsonBlobInfoResponse);
            synchronized (QqmpAuthHelper.this) {
                boolean ret = parseAuthResultBody(jsonBlobInfoResponse, code, errorMessage);

                if (!ret) {
                    // 失败，重试
                    mH.postDelayed(() -> checkAuthResult(), TIME_RETRY);
                } else {
                    // 成功
                    release();
                }
            }
        });

        return true;
    }

    synchronized boolean checkAuthResult() {
        if (mFinished) {
            return true;
        }

        if (mAgent == null) {
            return true;
        }

        long costTime = System.currentTimeMillis() - mStartTime;
        if (costTime > 60 * 1000) {
            // 超时，停止重试
            mFinished = true;
            return true;
        }
        return checkAuthResultInner();
    }

    private void onFailure(int code, String error, Throwable e) {
        if (mAgent == null) {
            return;
        }

        mAgent.onFailure(code, error, e);
    }

    private void onSuccess(CpCredential credential) {
        if (mAgent == null) {
            return;
        }

        mAgent.onSuccess(credential);
    }

    protected boolean parseAuthResultBody(String body, int code, String errorMessage) {
        if (mAgent == null) {
            return true;
        }
        if (code != 0) {
            // 云小微后台返回失败，可以检查一下网络是否正常，Product ID和DSN是否正确，如果均正常，查看一下errorMessage，向云小微FTO反馈
            onFailureEx(CpAuthError.QQMp.ERROR_GET_QQMP_AUTH_INVALID_REQUEST, " code:" + code + ", " + errorMessage, null);
            return false;
        }

        try {
            JSONObject bodyJson = new JSONObject(body == null ? "" : body);
            int ret = bodyJson.getInt("ret");
            if (ret != 0) {
                int subRet = bodyJson.optInt("sub_ret");
                String msg = bodyJson.optString("msg");
                // 这里是QQ音乐回应了错误码，根据ret、sub_ret和Msg进行初步分析，向云小微FTO反馈
                onFailureEx(CpAuthError.QQMp.ERROR_GET_QQMP_AUTH_INVALID_RESPONSE,
                        " ret " + ret + ", sub ret " + subRet + ", message " + msg, null);
                return false;
            }

            String encryptString = bodyJson.getString("encryptString");
            String decrypted = OpenIDHelper.decryptQQMEncryptString(encryptString, mPrivateKey);
            if (decrypted == null) {
                // 对授权结果进行解密失败，检查一下私钥mPrivateKey是否正确，也可能是返回的encryptString为空，如果为空，需要联系云小微FTO
                onFailureEx(CpAuthError.QQMp.ERROR_GET_QQMP_AUTH_DECRYPT_FAILED,
                        "", null);
                return false;
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
                onFailureEx(CpAuthError.QQMp.ERROR_GET_QQMP_AUTH_RESULT_UNPACK_FAILED,
                        "", null);
                return false;
            }
            if (!OpenIDHelper.checkQMSign(sign, nonce)) {
                // 校验失败，需要联系云小微FTO
                onFailureEx(CpAuthError.QQMp.ERROR_GET_QQMP_AUTH_CHECK_SIGN_FAILED,
                        "", null);
                return false;
            }
            doLog("requestAuth: OpenID = [" + openId + "]");
            doLog("requestAuth: OpenToken = [" + openToken + "]");
            onSuccess(new CpCredential(ThirdPartyCp.QQ_MUSIC, mAppId, openId, openToken, expireTime));
            return true;
        } catch (JSONException e) {
            onFailureEx(CpAuthError.QQMp.ERROR_GET_QQMP_AUTH_INVALID_AUTH_RESULT,
                    "", e);
        }

        return false;
    }
}
