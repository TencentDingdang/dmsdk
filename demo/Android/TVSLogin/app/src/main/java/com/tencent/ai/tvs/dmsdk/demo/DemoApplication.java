package com.tencent.ai.tvs.dmsdk.demo;

import android.app.Application;

public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ThirdPartyManager.init(this);

        // TVS登录和第三方账号登录方案的初始化方式分别在对应flavor中的DmsdkInitializer类中
        DmsdkInitializer.init(this);
        // 是否在登录成功后检查绑定。一般而言不需要启用。
//        TVSWeb.getConfiguration().setEnableBinding(true);
    }
}
