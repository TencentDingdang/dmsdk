package com.tencent.ai.tvs.dmsdk.demo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.tencent.ai.dobbydemo.proxy.QqSdkProxy;
import com.tencent.ai.dobbydemo.proxy.WxSdkProxy;
import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.tskm.TVSThirdPartyAuth;
import com.tencent.ai.tvs.tskm.thirdpartyauth.CPAuthType;
import com.tencent.ai.tvs.tskm.thirdpartyauth.CpAuthAgent;
import com.tencent.ai.tvs.tskm.thirdpartyauth.ThirdPartyCp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.yunxiaowei.dmsdk.cpaa.qqmusic.QQMusicAuthAgent;
import com.tencent.yunxiaowei.dmsdk.cpaa.qqmusic.QQMusicQqmpAuthAgent;
import com.tencent.yunxiaowei.dmsdk.cpaa.qqmusic.QQMusicWcmpAuthAgent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoApplication extends Application {

    public IWXAPI getWXAPI() {
        return WxSdkProxy.getInstance().getWXAPI();
    }

    private boolean isMainProcess() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DemoConstant.loadCustomConfigIfEnabled(this);
        // 将QQ、微信登录的相关逻辑注入到SDK，必须在调用LoginProxy.registerApp之前执行
        LoginProxy.getInstance().registerSdkProxy(WxSdkProxy.getInstance(), QqSdkProxy.getInstance());
        // 注意：下面的初始化提供了自定义的AuthDelegate实现！一般情况下不需要传第四个参数，使用三参数的registerApp即可。
        //       自定义AuthDelegate的目的见registerApp的文档。
        // 注意：此处使用的微信和QQ的App ID均为demo专用，无法用于您的应用，您需要为自己的应用申请专门的App ID！详见接入指南文档。
        LoginProxy.getInstance().registerApp(this, DemoConstant.APP_ID_WX, DemoConstant.APP_ID_QQ_OPEN);
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

        if (isMainProcess()) {
            // 为了防止在子进程中重复刷票，要判断一下是否主进程
            TokenVerify.getInstance().init(this);
            TokenVerify.getInstance().tryRefreshToken();
        }
    }
}
