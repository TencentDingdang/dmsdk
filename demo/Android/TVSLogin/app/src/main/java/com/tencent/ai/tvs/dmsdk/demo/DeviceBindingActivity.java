package com.tencent.ai.tvs.dmsdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.ai.dobbydemo.R;
import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.core.account.TVSAccountInfo;
import com.tencent.ai.tvs.core.common.TVSDevice;
import com.tencent.ai.tvs.core.common.TVSDeviceBindType;
import com.tencent.ai.tvs.env.EUserAttrType;
import com.tencent.ai.tvs.tskm.TVSThirdPartyAuth;

import java.util.ArrayList;

public class DeviceBindingActivity extends ModuleActivity {
    private EditText mProductIDEditText;
    private EditText mDSNEditText;
    private TVSDevice mQueriedDevice;
    private DemoPreference preference = new DemoPreference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_binding);
        String productId = preference.loadProductID(this);
        String dsn = preference.loadDSN(this);

        mProductIDEditText = findViewById(R.id.productIDEditText);
        mProductIDEditText.setText(productId);
        mDSNEditText = findViewById(R.id.dsnEditText);
        mDSNEditText.setText(dsn);
        findViewById(R.id.bindButton).setOnClickListener(v -> {
            if (TextUtils.isEmpty(mProductIDEditText.getText().toString()) && TextUtils.isEmpty(productId)) {
                ToastUtil.productId(this);
                return;
            }
            if (TextUtils.isEmpty(mDSNEditText.getText().toString()) && TextUtils.isEmpty(dsn)) {
                ToastUtil.dsn(this);
                return;
            }
            LoginProxy.getInstance().bindPushDevice(getDevice(productId, dsn), new SimpleTVSCallback("绑定"));
        });
        findViewById(R.id.unbindButton).setOnClickListener(v -> {
            if (TextUtils.isEmpty(mProductIDEditText.getText().toString()) && TextUtils.isEmpty(productId)) {
                ToastUtil.productId(this);
                return;
            }
            if (TextUtils.isEmpty(mDSNEditText.getText().toString()) && TextUtils.isEmpty(dsn)) {
                ToastUtil.dsn(this);
                return;
            }
            LoginProxy.getInstance().unbindPushDevice(getDevice(productId, dsn), new SimpleTVSCallback("解绑"));
        });
        findViewById(R.id.queryDeviceButton).setOnClickListener(v -> LoginProxy.getInstance().getDeviceInfoListByAccount(TVSDeviceBindType.TVS_SPEAKER, new SimpleTVSCallback1<ArrayList<TVSDevice>>("帐号查设备") {
            @Override
            protected String loggableResult(ArrayList<TVSDevice> result) {
                StringBuilder lines = new StringBuilder();
                for (TVSDevice device : result) {
                    lines.append("Device: ProductID = ").append(device.productID).append(", DSN = ").append(device.dsn).append("\n");
                }
                return lines.toString();
            }
        }));

        findViewById(R.id.queryDeviceGuidButton).setOnClickListener(v -> {
            if (TextUtils.isEmpty(mProductIDEditText.getText().toString()) && TextUtils.isEmpty(productId)) {
                ToastUtil.productId(this);
                return;
            }
            if (TextUtils.isEmpty(mDSNEditText.getText().toString()) && TextUtils.isEmpty(dsn)) {
                ToastUtil.dsn(this);
                return;
            }
            LoginProxy.getInstance().getDeviceInfoListByDSN(TVSDeviceBindType.TVS_SPEAKER,
                    TextUtils.isEmpty(mProductIDEditText.getText().toString()) ? productId : mProductIDEditText.getText().toString(),
                        TextUtils.isEmpty(mDSNEditText.getText().toString()) ? dsn : mDSNEditText.getText().toString(),
                            new SimpleTVSCallback1<ArrayList<TVSDevice>>("帐号查设备") {
                @Override
                protected String loggableResult(ArrayList<TVSDevice> result) {
                    StringBuilder lines = new StringBuilder();
                    for (TVSDevice device : result) {
                        mQueriedDevice = new TVSDevice();
                        mQueriedDevice.productID = device.productID;
                        mQueriedDevice.dsn = device.dsn;
                        mQueriedDevice.guid = device.guid;
                        lines.append("Device: GUID = ").append(device.guid).append("\n");
                    }
                    return lines.toString();
                }
            });
        });

        findViewById(R.id.queryAccountButton).setOnClickListener(v -> {
            if (TextUtils.isEmpty(mProductIDEditText.getText().toString()) && TextUtils.isEmpty(productId)) {
                ToastUtil.productId(this);
                return;
            }
            if (TextUtils.isEmpty(mDSNEditText.getText().toString()) && TextUtils.isEmpty(dsn)) {
                ToastUtil.dsn(this);
                return;
            }
            LoginProxy.getInstance().getBoundAccount(getDevice(productId, dsn), new SimpleTVSCallback1<TVSAccountInfo>("设备查帐号", false) {
                @Override
                protected String loggableResult(TVSAccountInfo result) {
                    return "OpenID = " + result.getOpenID();
                }
            });
        });
        findViewById(R.id.toCloudDDWebButton).setOnClickListener(v -> {
            if (mQueriedDevice == null) {
                Toast.makeText(this, "请先点击“DSN查GUID”初始化设备信息", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, WebActivity.class);
            intent.putExtra("devInfo", mQueriedDevice);

            intent.putExtra("ddAuthRedirectUrl", TVSThirdPartyAuth.getRedirectUrl(EUserAttrType.QQ_MUSIC));
            intent.putExtra("targetUrl", TVSThirdPartyAuth.getTargetUrl());

            startActivity(intent);
        });
        findViewById(R.id.toCloudDDNativeButton).setOnClickListener(v -> {
            if (mQueriedDevice == null) {
                Toast.makeText(this, "请先点击“DSN查GUID”初始化设备信息", Toast.LENGTH_SHORT).show();
                return;
            }
            TVSThirdPartyAuth.requestCloudDDAuth(DeviceBindingActivity.this, mQueriedDevice, DeviceBindingActivity.class.getCanonicalName(), "");
        });
    }

    private TVSDevice getDevice(String productId, String dsn) {
        TVSDevice device = new TVSDevice();
        if (mProductIDEditText.getText().toString().equals("")) {
            device.productID = productId;
        }
        else {
            device.productID = mProductIDEditText.getText().toString();
        }
        if (mDSNEditText.getText().toString().equals("")) {
            device.dsn = dsn;
        }
        else {
            device.dsn = mDSNEditText.getText().toString();
        }
        // 这里使用TVS方案，字段填入规则请阅读LoginProxy#bindPushDevice的文档！
        device.bindType = TVSDeviceBindType.TVS_SPEAKER;
        device.pushIDExtra = "TVSSpeaker";
        return device;
    }
}
