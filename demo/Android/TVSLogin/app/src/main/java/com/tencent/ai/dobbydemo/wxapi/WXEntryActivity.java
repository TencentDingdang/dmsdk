package com.tencent.ai.dobbydemo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.dmsdk.demo.DemoConstant;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

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

    @Override
    public void onResp(BaseResp baseResp) {
        if (!LoginProxy.getInstance().onResp(baseResp)) {
            // 在此处添加您的自定义实现，如响应微信分享回调
            Toast.makeText(this, "DMSDK didn't handle onResp", Toast.LENGTH_SHORT).show();
        }
    }
}
