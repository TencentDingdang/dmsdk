package com.tencent.ai.dobbydemo.proxy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.base.log.DMLog;
import com.tencent.ai.tvs.base.report.ExceptionCode;
import com.tencent.ai.tvs.base.report.ExceptionReport;
import com.tencent.ai.tvs.base.report.NewReportManager;
import com.tencent.ai.tvs.core.account.IQQSdkProxy;
import com.tencent.ai.tvs.core.account.QQLoginInfo;
import com.tencent.ai.tvs.core.common.ErrCode;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;

public final class QqSdkProxy implements IQQSdkProxy {
    private static final String TAG = "QqSdkProxy";
    private Tencent mTencent;

    // perqinxie: QQ登录的SDK只会持有这个Listener的弱引用，如果外部调用方不持有强引用的话，会导致概
    // 率性被GC掉，从而导致无法收到回调，猜测这也是`Tencent.onActivityResultData`需要再传一次回调的
    // 原因。所以这个成员变量不能改为局部变量！
    @SuppressWarnings("FieldCanBeLocal")
    private QQOpenLoginListener mQqOpenLoginListener;

    private LoginCallback mLoginCallback;

    public static QqSdkProxy getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public boolean registerApp(Context context, String appID, @NonNull LoginCallback loginCallback) {
        DMLog.i(TAG, "registerApp: context = [" + context + "], appID = [" + appID + "], loginCallback = [" + loginCallback + "]");
        try {
            mTencent = Tencent.createInstance(appID, context);
            mLoginCallback = loginCallback;
            return true;
        } catch (NoClassDefFoundError e) {
            DMLog.w(TAG, "registerApp: Failed to register QQOpen SDK due to SDK dependency not found."
                    + " If you need QQ Login feature of DMSDK, you should add dependency "
                    + "`open_sdk_rxxxx_lite.jar` to your app.");
            mTencent = null;
            return false;
        }
    }

    public void clearToken(Context context) {
        DMLog.i(TAG, "clearToken: context = [" + context + "]");
        if (mTencent == null) {
            return;
        }
        mTencent.logout(context);
    }

    public void requestLogin(Activity activity) {
        DMLog.i(TAG, "requestLogin: activity = [" + activity + "]");
        if (mTencent == null) {
            return;
        }
        // Step 1: invoke QQOpen Login SDK to login locally
        mQqOpenLoginListener = new QQOpenLoginListener();
        // 这里本来回调用mTencent.checkSessionValid，但是看起来没有必要，所以这里把它删掉。如果登录出现问题，再恢复并深入调查
        mTencent.login(activity, "all", mQqOpenLoginListener);
    }

    public boolean handleQQOpenIntent(int requestCode, int resultCode, Intent data) {
        DMLog.i(TAG, "handleQQOpenIntent: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "]");
        if (mTencent == null) {
            return false;
        }
        // NOTE: perqinxie: QQOpen SDK's Tencent#onActivityResultData(int, int, Intent, IUiListener)
        //       doesn't check requestCode, so we have to check here for it.
        //       We currently only use Login
        if (requestCode != Constants.REQUEST_LOGIN) {
            NewReportManager.getInstance().send(new ExceptionReport(
                    ExceptionCode.AUTH_SDK_ERROR_QQ,
                    "requestCode = " + requestCode
            ));
            return false;
        }
        // NOTE: perqinxie: 根据反编译的SDK源码，调用Tencent#login()的时候传递的IUiListener优先级更高，
        //       只有那个接口没有传入Listener的时候才会用这里的，所以我们大胆地传入null。如果挂了，QQ互联团队出来挨打。
        // 19/05/14: 佛了，怎么还有登录问题，行吧，加上就加上，虽然我觉得并没有什么用……
        return Tencent.onActivityResultData(requestCode, resultCode, data, mQqOpenLoginListener);
    }

    @Override
    public boolean isRequestLogin(int requestCode) {
        return requestCode == Constants.REQUEST_LOGIN;
    }

    private class QQOpenLoginListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            DMLog.i(TAG, "QQOpenLoginListener.onComplete: o = [" + o + "]");
            String openId;
            String accessToken;
            String expires;
            long expireTime;
            JSONObject obj = (JSONObject)o;
            try {
                openId = obj.getString("openid");
                accessToken = obj.getString("access_token");
                expires = obj.getString("expires_in");
                expireTime = Long.valueOf(expires);
            } catch (JSONException | NumberFormatException e) {
                e.printStackTrace();
                DMLog.e(TAG, "QQOpenLoginListener.onComplete: code = [" + ErrCode.ERR_SDK_FAILED + "], message = [" + e.getMessage() + "]");
                NewReportManager.getInstance().send(new ExceptionReport(
                        ExceptionCode.AUTH_SDK_ERROR_QQ,
                        "Login fail to parse result from QQ sdk: " + obj
                ));
                if (mLoginCallback != null) {
                    mLoginCallback.onError(ErrCode.ERR_SDK_FAILED);
                }
                return;
            }
            if (TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(expires)
                    || TextUtils.isEmpty(openId) || expireTime == 0L) {
                DMLog.e(TAG, "QQOpenLoginListener.onComplete: code = [" + ErrCode.ERR_SDK_FAILED + "], message = [invalid result]");
                NewReportManager.getInstance().send(new ExceptionReport(
                        ExceptionCode.AUTH_SDK_ERROR_QQ,
                        "Login fail to get valid result from QQ sdk: " + obj
                ));
                if (mLoginCallback != null) {
                    mLoginCallback.onError(ErrCode.ERR_SDK_FAILED);
                }
                return;
            }
            mTencent.setOpenId(openId);
            mTencent.setAccessToken(accessToken, expires);
            QQLoginInfo loginInfo = new QQLoginInfo();
            loginInfo.openID = openId;
            loginInfo.accessToken = accessToken;
            loginInfo.expireTime = expireTime;
            UserInfo info = new UserInfo(LoginProxy.getInstance().getContext(), mTencent.getQQToken());
            // Step 2: Get User Info locally from QQOpen SDK
            info.getUserInfo(new QQOpenUserInfoListener(loginInfo));
        }

        @Override
        public void onError(UiError uiError) {
            DMLog.e(TAG, "QQOpenLoginListener.onError: errorCode = [" + uiError.errorCode +
                    "], errorMessage = [" + uiError.errorMessage + "], errorDetail = [" + uiError.errorDetail + "]");
            NewReportManager.getInstance().send(new ExceptionReport(
                    ExceptionCode.AUTH_SDK_ERROR_QQ,
                    "Login onError: " + uiError.errorCode + ", " + uiError.errorMessage + ", " + uiError.errorDetail
            ));
            if (mLoginCallback != null) {
                mLoginCallback.onError(ErrCode.ERR_SDK_FAILED);
            }
        }
        @Override
        public void onCancel() {
            DMLog.e(TAG, "QQOpenLoginListener.onCancel: code = [" + ErrCode.ERR_USER_CANCEL + "]");
            if (mLoginCallback != null) {
                mLoginCallback.onError(ErrCode.ERR_USER_CANCEL);
            }
        }
    }

    private class QQOpenUserInfoListener implements IUiListener {
        private final QQLoginInfo mLoginInfo;

        QQOpenUserInfoListener(QQLoginInfo loginInfo) {
            DMLog.i(TAG, "QQOpenUserInfoListener: openID = [" + loginInfo.openID + "]");
            mLoginInfo = loginInfo;
        }

        @Override
        public void onComplete(Object o) {
            DMLog.i(TAG, "QQOpenUserInfoListener.onComplete: o = [" + o + "]");
            final String nickname;
            final String headImgUrl;
            final int sex;
            JSONObject obj = (JSONObject)o;
            try {
                nickname = obj.getString("nickname");
                headImgUrl = obj.getString("figureurl_qq_2");
                sex = "女".equals(obj.getString("gender")) ? 1 : 0;
            } catch (JSONException e) {
                DMLog.e(TAG, "QQOpenUserInfoListener.onComplete: code = [" + ErrCode.ERR_SDK_FAILED + "], message = [fail to parse json: " + obj + "]");
                NewReportManager.getInstance().send(new ExceptionReport(
                        ExceptionCode.AUTH_SDK_ERROR_QQ,
                        "UserInfo fail to parse result from QQ sdk: " + obj
                ));
                if (mLoginCallback != null) {
                    mLoginCallback.onError(ErrCode.ERR_SDK_FAILED);
                }
                return;
            }
            mLoginInfo.nickname = nickname;
            mLoginInfo.headImgUrl = headImgUrl;
            mLoginInfo.sex = sex;
            if (mLoginCallback != null) {
                mLoginCallback.onSuccess(mLoginInfo);
            }
        }

        @Override
        public void onError(UiError uiError) {
            DMLog.e(TAG, "QQOpenUserInfoListener.onError: errorCode = [" + uiError.errorCode +
                    "], errorMessage = [" + uiError.errorMessage + "], errorDetail = [" + uiError.errorDetail + "]");
            NewReportManager.getInstance().send(new ExceptionReport(
                    ExceptionCode.AUTH_SDK_ERROR_QQ,
                    "UserInfo onError: " + uiError.errorCode + ", " + uiError.errorMessage + ", " + uiError.errorDetail
            ));
            if (mLoginCallback != null) {
                mLoginCallback.onError(ErrCode.ERR_SDK_FAILED);
            }
        }

        @Override
        public void onCancel() {
            DMLog.e(TAG, "QQOpenUserInfoListener.onCancel: code = [" + ErrCode.ERR_USER_CANCEL + "]");
        }
    }

    private static class SingletonHolder {
        private static final QqSdkProxy INSTANCE = new QqSdkProxy();
    }
}
