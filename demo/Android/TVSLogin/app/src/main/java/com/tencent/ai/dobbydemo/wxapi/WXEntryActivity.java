package com.tencent.ai.dobbydemo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.base.log.DMLog;
import com.tencent.ai.tvs.dmsdk.demo.DemoConstant;
import com.tencent.ai.tvs.tskm.TVSThirdPartyAuth;
import com.tencent.ai.tvs.tskm.thirdpartyauth.CpAuthAgent;
import com.tencent.ai.tvs.tskm.thirdpartyauth.ThirdPartyCp;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.yunxiaowei.dmsdk.cpaa.qqmusic.QQMusicWcmpAuthAgent;

/**
 * 这是一个自定义WXEntryActivity的示例，如果需要自定义又需要集成DMSDK的账号登录相关功能，则可以参考
 * 下面的实现。
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI mWxApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWxApi = WXAPIFactory.createWXAPI(this, DemoConstant.APP_ID_WX, false);
        mWxApi.registerApp(DemoConstant.APP_ID_WX);
        handleAndFinish(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleAndFinish(getIntent());
    }

    private void handleAndFinish(Intent intent) {
        mWxApi.handleIntent(intent, this);
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {
        if (!LoginProxy.getInstance().onReq(baseReq)) {
            // 在此处添加您的自定义实现
            Toast.makeText(this, "DMSDK didn't handle onReq", Toast.LENGTH_SHORT).show();
        }
    }

    private void onWxmpCallback(BaseResp baseResp) {
        WXLaunchMiniProgram.Resp launchMiniProResp = (WXLaunchMiniProgram.Resp) baseResp;
        String extraData = launchMiniProResp.extMsg;
        CpAuthAgent agent = TVSThirdPartyAuth.getCpAuthAgent(ThirdPartyCp.QQ_MUSIC);
        if (agent != null && agent instanceof QQMusicWcmpAuthAgent) {
            ((QQMusicWcmpAuthAgent) agent).onWeChatResp(extraData);
        }
    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (!LoginProxy.getInstance().onResp(baseResp)) {
            // 在此处添加您的自定义实现，如响应微信分享回调
            if (baseResp.getType() == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
                // 如果是微信小程序的回调
                onWxmpCallback(baseResp);
            } else {
                Toast.makeText(this, "DM SDK didn't handle onResp", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
