package com.tencent.ai.tvs.dmsdk.demo;

import android.app.Application;

import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.tskm.TVSThirdPartyAuth;
import com.tencent.ai.tvs.tskm.thirdpartyauth.CPAuthType;
import com.tencent.ai.tvs.tskm.thirdpartyauth.CpAuthAgent;
import com.tencent.ai.tvs.tskm.thirdpartyauth.ThirdPartyCp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.yunxiaowei.dmsdk.cpaa.qqmusic.QQMusicAuthAgent;
import com.tencent.yunxiaowei.dmsdk.cpaa.qqmusic.QQMusicQqmpAuthAgent;
import com.tencent.yunxiaowei.dmsdk.cpaa.qqmusic.QQMusicWcmpAuthAgent;

import java.util.HashMap;
import java.util.Map;

public class DemoApplication extends Application {

    IWXAPI mWxApi;
    public IWXAPI getWXAPI() {
        return mWxApi;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化微信SDK,用于QQ音乐微信小程序授权
        String wxAppId = DemoConstant.APP_ID_WX;
        mWxApi = WXAPIFactory.createWXAPI(this, wxAppId, false);
        mWxApi.registerApp(wxAppId);

        LoginProxy.getInstance().registerApp(this, DemoConstant.DEFAULT_APP_KEY);
        // 如果集成了TSKM模块，则需要注入逻辑到WebView SDK
        TVSThirdPartyAuth.setupWithWeb();

        Map<CPAuthType, CpAuthAgent> agentMap = new HashMap<>();
        agentMap.put(CPAuthType.QQ, new QQMusicQqmpAuthAgent(this, DemoConstant.QQ_MUSIC_APP_ID,
                DemoConstant.AppPrivateKey, DemoConstant.QQ_MUSIC_CALLBACK_URL));

        agentMap.put(CPAuthType.Weixin, new QQMusicWcmpAuthAgent(this, DemoConstant.QQ_MUSIC_APP_ID,
                DemoConstant.AppPrivateKey, getPackageName(), getWXAPI()));

        agentMap.put(CPAuthType.APP, new QQMusicAuthAgent(this,
                DemoConstant.QQ_MUSIC_APP_ID, DemoConstant.AppPrivateKey, DemoConstant.QQ_MUSIC_CALLBACK_URL));

        TVSThirdPartyAuth.setCpAuthAgent(ThirdPartyCp.QQ_MUSIC, agentMap);
        // 是否在登录成功后检查绑定。一般而言不需要启用。
//        TVSWeb.getConfiguration().setEnableBinding(true);

        // 初始化自动刷票功能
        TokenVerify.getInstance().init(this);
        TokenVerify.getInstance().tryRefreshToken();

    }
}
