package com.tencent.ai.dobbydemo.proxy;

import android.content.Context;
import android.content.Intent;

import com.tencent.ai.tvs.base.log.DMLog;
import com.tencent.ai.tvs.base.report.ExceptionCode;
import com.tencent.ai.tvs.base.report.ExceptionReport;
import com.tencent.ai.tvs.base.report.NewReportManager;
import com.tencent.ai.tvs.core.account.IWxSdkProxy;
import com.tencent.ai.tvs.core.common.ErrCode;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import androidx.annotation.NonNull;

public final class WxSdkProxy implements IWxSdkProxy {
    private static final String TAG = "WxSdkProxy";

    private OnSendAuthCallback mOnSendAuthCallback;

    private IWXAPI mWxApi;
    private IWXAPIEventHandler mWxApiEventHandler;

    public static WxSdkProxy getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public boolean registerApp(Context context, String appId,
                               @NonNull OnSendAuthCallback onSendAuthCallback) {
        DMLog.i(TAG, "registerApp: context = [" + context + "], appId = [" + appId + "], onSendAuthCallback = [" + onSendAuthCallback + "]");
        try {
            mWxApi = WXAPIFactory.createWXAPI(context, appId, false);
            mWxApi.registerApp(appId);
            mWxApiEventHandler = new IWXAPIEventHandler() {
                @Override
                public void onReq(BaseReq baseReq) {
                    WxSdkProxy.this.onReq(baseReq);
                }

                @Override
                public void onResp(BaseResp baseResp) {
                    WxSdkProxy.this.onResp(baseResp);
                }
            };
            mOnSendAuthCallback = onSendAuthCallback;
            return true;
        } catch (NoClassDefFoundError e) {
            DMLog.w(TAG, "registerApp: Failed to register WeChat SDK due to SDK dependency not found."
                    + " If you need WeChat Login feature of DMSDK, you should add dependency "
                    + "`com.tencent.mm.opensdk:wechat-sdk-android-with-mta:+` to your app.");
            mWxApi = null;
            return false;
        }
    }

    public IWXAPI getWXAPI() {
        return mWxApi;
    }

    public void handleIntent(Intent intent) {
        DMLog.i(TAG, "handleIntent");
        if (mWxApi == null) {
            return;
        }
        mWxApi.handleIntent(intent, mWxApiEventHandler);
    }

    @Override
    public void sendLoginReq() {
        DMLog.i(TAG, "sendLoginReq");
        if (mWxApi == null) {
            return;
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "none";
        mWxApi.sendReq(req);
    }

    @Override
    public void clearToken() {
        DMLog.i(TAG, "clearToken");
        if (mWxApi == null) {
            return;
        }
        // TODO: Why unregister???
        mWxApi.unregisterApp();
    }

    @Override
    public boolean isWxAppInstalled() {
        DMLog.i(TAG, "isWxAppInstalled");
        if (mWxApi == null) {
            DMLog.w(TAG, "isWxAppInstalled: no WeChat SDK dependency so return false");
            return false;
        }
        return mWxApi.isWXAppInstalled();
    }

    @Override
    public boolean onReq(Object baseReq) {
        DMLog.i(TAG, "onReq");
        return false;
    }

    public boolean onResp(Object baseRespObj) {
        BaseResp baseResp = (BaseResp)baseRespObj;
        DMLog.i(TAG, "onResp");
        if (mWxApi == null) {
            return false;
        }
        if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            if (baseResp.errCode == BaseResp.ErrCode.ERR_OK) {
                DMLog.i(TAG, "onResp: onSuccess");
                SendAuth.Resp resp = (SendAuth.Resp) baseResp;
                String code = resp.code;
                mOnSendAuthCallback.onSuccess(code);
            } else if (baseResp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
                DMLog.e(TAG, "onResp: errCode = [" + ErrCode.ERR_USER_CANCEL +
                        "], baseResp.errCode = [" + baseResp.errCode + "], baseResp.errStr = [" + baseResp.errStr + "]");
                mOnSendAuthCallback.onError(ErrCode.ERR_USER_CANCEL);
            } else {
                DMLog.e(TAG, "onResp: errCode = [" + ErrCode.ERR_SDK_FAILED +
                        "], baseResp.errCode = [" + baseResp.errCode + "], baseResp.errStr = [" + baseResp.errStr + "]");
                mOnSendAuthCallback.onError(ErrCode.ERR_SDK_FAILED);
                NewReportManager.getInstance().send(new ExceptionReport(
                        ExceptionCode.AUTH_SDK_ERROR_WX,
                        "onResp errCode = [" + baseResp.errCode + "], errStr = [" + baseResp.errStr + "]"
                ));
            }
            return true;
        }
        NewReportManager.getInstance().send(new ExceptionReport(
                ExceptionCode.AUTH_SDK_ERROR_WX,
                "onResp type = [" + baseResp.getType() + "]"
        ));
        return false;
    }

    private static class SingletonHolder {
        private static final WxSdkProxy INSTANCE = new WxSdkProxy();
    }
}
