package com.tencent.ai.tvs.dmsdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_binding);

        mProductIDEditText = findViewById(R.id.productIDEditText);
        mProductIDEditText.setText(ThirdPartyManager.getProductId());
        mProductIDEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                ThirdPartyManager.setProductId(s.toString());
            }
        });
        mDSNEditText = findViewById(R.id.dsnEditText);
        mDSNEditText.setText(DemoConstant.DSN);
        findViewById(R.id.bindButton).setOnClickListener(v -> {
            if (TextUtils.isEmpty(mProductIDEditText.getText().toString()) && TextUtils.isEmpty(DemoConstant.PRODUCT_ID)) {
                ToastUtil.productId(this);
                return;
            }
            if (TextUtils.isEmpty(mDSNEditText.getText().toString()) && TextUtils.isEmpty(DemoConstant.DSN)) {
                ToastUtil.dsn(this);
                return;
            }
            LoginProxy.getInstance().bindPushDevice(getDevice(), new SimpleTVSCallback("绑定"));
        });
        findViewById(R.id.unbindButton).setOnClickListener(v -> {
            if (TextUtils.isEmpty(mProductIDEditText.getText().toString()) && TextUtils.isEmpty(DemoConstant.PRODUCT_ID)) {
                ToastUtil.productId(this);
                return;
            }
            if (TextUtils.isEmpty(mDSNEditText.getText().toString()) && TextUtils.isEmpty(DemoConstant.DSN)) {
                ToastUtil.dsn(this);
                return;
            }
            LoginProxy.getInstance().unbindPushDevice(getDevice(), new SimpleTVSCallback("解绑"));
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
            if (TextUtils.isEmpty(mProductIDEditText.getText().toString()) && TextUtils.isEmpty(DemoConstant.PRODUCT_ID)) {
                ToastUtil.productId(this);
                return;
            }
            if (TextUtils.isEmpty(mDSNEditText.getText().toString()) && TextUtils.isEmpty(DemoConstant.DSN)) {
                ToastUtil.dsn(this);
                return;
            }
            LoginProxy.getInstance().getDeviceInfoListByDSN(TVSDeviceBindType.TVS_SPEAKER,
                    TextUtils.isEmpty(mProductIDEditText.getText().toString()) ? DemoConstant.PRODUCT_ID : mProductIDEditText.getText().toString(),
                        TextUtils.isEmpty(mDSNEditText.getText().toString()) ? DemoConstant.DSN : mDSNEditText.getText().toString(),
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
            if (TextUtils.isEmpty(mProductIDEditText.getText().toString()) && TextUtils.isEmpty(DemoConstant.PRODUCT_ID)) {
                ToastUtil.productId(this);
                return;
            }
            if (TextUtils.isEmpty(mDSNEditText.getText().toString()) && TextUtils.isEmpty(DemoConstant.DSN)) {
                ToastUtil.dsn(this);
                return;
            }
            LoginProxy.getInstance().getBoundAccount(getDevice(), new SimpleTVSCallback1<TVSAccountInfo>("设备查帐号", false) {
                @Override
                protected String loggableResult(TVSAccountInfo result) {
                    return "OpenID = " + result.getOpenID();
                }
            });
        });
        findViewById(R.id.toCloudDDWebButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, WebActivity.class);
            intent.putExtra("devInfo", mQueriedDevice);

            intent.putExtra("ddAuthRedirectUrl", TVSThirdPartyAuth.getRedirectUrl(EUserAttrType.QQ_MUSIC));
            intent.putExtra("targetUrl", TVSThirdPartyAuth.getTargetUrl());

            startActivity(intent);
        });
        findViewById(R.id.toCloudDDNativeButton).setOnClickListener(v -> {
            if (mQueriedDevice != null) {
                TVSThirdPartyAuth.requestCloudDDAuth(DeviceBindingActivity.this, mQueriedDevice, "com.tencent.ai.tvs.dmsdk.demo.DeviceBindingActivity", "");
            } else {
                Toast.makeText(this, "请先点击“DSN查GUID”初始化设备信息", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private TVSDevice getDevice() {
        TVSDevice device = new TVSDevice();
        if (mProductIDEditText.getText().toString().equals("")) {
            device.productID = DemoConstant.PRODUCT_ID;
        }
        else {
            device.productID = mProductIDEditText.getText().toString();
        }
        if (mDSNEditText.getText().toString().equals("")) {
            device.dsn = DemoConstant.DSN;
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
