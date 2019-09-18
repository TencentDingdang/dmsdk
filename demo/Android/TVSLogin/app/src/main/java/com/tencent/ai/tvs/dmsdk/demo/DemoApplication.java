package com.tencent.ai.tvs.dmsdk.demo;

import android.app.Application;

import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.base.util.TvsDeviceUtil;
import com.tencent.ai.tvs.core.account.TVSAccountInfo;
import com.tencent.ai.tvs.core.account.TVSAuthDelegate;
import com.tencent.ai.tvs.env.ELoginPlatform;
import com.tencent.ai.tvs.tskm.TVSThirdPartyAuth;

public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ThirdPartyManager.init(this);

        // 注意：下面的初始化提供了自定义的AuthDelegate实现！一般情况下不需要传第四个参数，使用三参数的registerApp即可。
        //       自定义AuthDelegate的目的见registerApp的文档。
        // 注意：此处使用的微信和QQ的App ID均为demo专用，无法用于您的应用，您需要为自己的应用申请专门的App ID！详见接入指南文档。
        LoginProxy.getInstance().registerApp(this, DemoConstant.APP_ID_WX, DemoConstant.APP_ID_QQ_OPEN, new TVSAuthDelegate() {
            @Override
            public TVSAccountInfo getAccountInfo() {
                if (ThirdPartyManager.isThirdParty()) {
                    TVSAccountInfo accountInfo = new TVSAccountInfo();
                    accountInfo.setPlatform(ELoginPlatform.ThirdParty);
                    accountInfo.setAppId(TvsDeviceUtil.getAppKey(DemoConstant.PRODUCT_ID));
                    accountInfo.setOpenID(ThirdPartyManager.getThirdPartyAccountId());
                    return accountInfo;
                } else {
                    return super.getAccountInfo();
                }
            }
        });
        // 如果集成了TSKM模块，则需要注入逻辑到WebView SDK
        TVSThirdPartyAuth.setupWithWeb();
        // 是否在登录成功后检查绑定。一般而言不需要启用。
//        TVSWeb.getConfiguration().setEnableBinding(true);
    }
}
