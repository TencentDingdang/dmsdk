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
import com.tencent.ai.tvs.core.device.TVSDeviceIdentity;
import com.tencent.ai.tvs.env.EUserAttrType;
import com.tencent.ai.tvs.tskm.TVSThirdPartyAuth;

import java.util.ArrayList;

public class DeviceBindingActivity extends ModuleActivity {
    private EditText mProductIDEditText;
    private EditText mDSNEditText;
    private EditText mAuthReqInfoText;
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

        mAuthReqInfoText = findViewById(R.id.authReqInfoText);

        findViewById(R.id.bindButton).setOnClickListener(v -> {
            if (TextUtils.isEmpty(mProductIDEditText.getText().toString()) && TextUtils.isEmpty(productId)) {
                ToastUtil.productId(this);
                return;
            }
            if (TextUtils.isEmpty(mDSNEditText.getText().toString()) && TextUtils.isEmpty(dsn)) {
                ToastUtil.dsn(this);
                return;
            }

            if (TextUtils.isEmpty(mAuthReqInfoText.getText().toString())) {
                Toast.makeText(DeviceBindingActivity.this, "请输入Auth Req Info", Toast.LENGTH_LONG).show();
                return;
            }
            TVSDeviceIdentity identity = getDevice(productId, dsn);
            LoginProxy.getInstance().authorizeDevice(identity, mAuthReqInfoText.getText().toString(), new LoginProxy.DeviceAuthorizationCallback() {

                @Override
                public void onSuccess(String clientId, String authRespInfo) {
                    logLine("成功：\nClient ID = " + clientId + "\nAuthRespInfo = " + authRespInfo);
                }

                @Override
                public void onError(int code) {
                    logLine("失败：" + code);
                }
            });
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
            TVSDeviceIdentity identity = getDevice(productId, dsn);
            LoginProxy.getInstance().revokeDeviceAuthorization(identity, new LoginProxy.RevokeDeviceAuthorizationCallback() {

                @Override
                public void onSuccess() {
                    logLine("解除授权成功");
                }

                @Override
                public void onError(int code) {
                    logLine("解除授权失败：" + code);
                }
            });
        });
    }

    private TVSDeviceIdentity getDevice(String productId, String dsn) {
        TVSDeviceIdentity device = new TVSDeviceIdentity();
        if (mProductIDEditText.getText().toString().equals("")) {
            device.productID = productId;
        } else {
            device.productID = mProductIDEditText.getText().toString();
        }
        if (mDSNEditText.getText().toString().equals("")) {
            device.dsn = dsn;
        } else {
            device.dsn = mDSNEditText.getText().toString();
        }

        return device;
    }
}
