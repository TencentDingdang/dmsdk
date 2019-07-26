package com.tencent.ai.tvs.dmsdk.demo;

import android.content.Context;

import com.tencent.ai.tvs.LoginProxy;

class DmsdkInitializer {
    static void init(Context context) {
        LoginProxy.getInstance().registerApp(context, DemoConstant.APP_ID_WX, DemoConstant.APP_ID_QQ_OPEN);
    }
}
