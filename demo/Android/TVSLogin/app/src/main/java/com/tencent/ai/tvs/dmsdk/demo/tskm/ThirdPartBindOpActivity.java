package com.tencent.ai.tvs.dmsdk.demo.tskm;

import android.os.Bundle;

import com.tencent.ai.dobbydemo.R;
import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.core.account.TVSAccountInfo;
import com.tencent.ai.tvs.core.common.TVSDevice;
import com.tencent.ai.tvs.core.common.TVSDeviceBindType;
import com.tencent.ai.tvs.dmsdk.demo.ModuleActivity;
import com.tencent.ai.tvs.dmsdk.demo.TSKMActivity;
import com.tencent.ai.tvs.tskm.TVSThirdPartyAuth;

import java.util.ArrayList;

public class ThirdPartBindOpActivity extends ModuleActivity {

    public static final String LOG_TAG = "ThirdPartBindOpActivity";

    private TVSThirdPartyAuth mTskm;
    private TVSDevice mThirdDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thirdparty_bindingop);

        String productId = getIntent().getStringExtra(TSKMActivity.EXTRA_PRODUCT_ID);
        String dsn = getIntent().getStringExtra(TSKMActivity.EXTRA_DSN);
        mTskm = new TVSThirdPartyAuth(productId, dsn);

        mThirdDevice = new TVSDevice();
        mThirdDevice.productID = productId;
        mThirdDevice.dsn = dsn;
        mThirdDevice.guid = "";

        findViewById(R.id.thirdPartyUnbindButton).setOnClickListener(v -> mTskm.reqThirdPartyAcctBindOp(false, null, null, new SimpleTVSCallback("云叮当授权解绑")));

        findViewById(R.id.thirdPartyQueryGuidButton).setOnClickListener(v-> LoginProxy.getInstance().getDeviceInfoListByDSN(TVSDeviceBindType.TVS_SPEAKER, productId, dsn, new SimpleTVSCallback1<ArrayList<TVSDevice>>("帐号查设备GUID") {
            @Override
            protected String loggableResult(ArrayList<TVSDevice> result) {
                StringBuilder lines = new StringBuilder();
                for (TVSDevice device : result) {
                    mThirdDevice = new TVSDevice();
                    mThirdDevice.productID = productId;
                    mThirdDevice.dsn = dsn;
                    mThirdDevice.guid = device.guid;
                    lines.append("Device: GUID = ").append(device.guid).append("\n");
                }
                return lines.toString();
            }
        }));

        // 返回onError(int code) code值为TVSThirdPartyAuth.THIRDPARTY_ERR_NOBINDING为未绑定
        findViewById(R.id.thirdPartyQueryButton).setOnClickListener(v -> mTskm.reqQueryThirdPartyAcctBindOp(mThirdDevice, new SimpleTVSCallback1<TVSAccountInfo>("云叮当授权查询") {
            @Override
            protected String loggableResult(TVSAccountInfo result) {
                StringBuilder lines = new StringBuilder();
                lines.append("CloudDD OpenId = ").append(result.getOpenID()).append("\n");
                return lines.toString();
            }
        }));
    }
}
