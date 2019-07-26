package com.tencent.ai.tvs.dmsdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.ai.dobbydemo.R;
import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.dmsdk.demo.tskm.AlarmActivity;
import com.tencent.ai.tvs.dmsdk.demo.tskm.ChildModeActivity;
import com.tencent.ai.tvs.dmsdk.demo.tskm.DeviceControlActivity;
import com.tencent.ai.tvs.dmsdk.demo.tskm.ThirdPartBindOpActivity;

public class TSKMActivity extends AppCompatActivity {
    public static final String EXTRA_PRODUCT_ID = "PRODUCT_ID";
    public static final String EXTRA_DSN = "DSN";

    private EditText mProductIDEditText;
    private EditText mDSNEditText;

    private final View.OnClickListener mOnClickListener = v -> {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.alarmButton:
                intent = new Intent(this, AlarmActivity.class);
                break;
            case R.id.childModeButton:
                intent = new Intent(this, ChildModeActivity.class);
                break;
            case R.id.deviceControlButton:
                intent = new Intent(this, DeviceControlActivity.class);
                break;
            case R.id.thirdpartyBindOpButton:
                intent = new Intent(this, ThirdPartBindOpActivity.class);
                break;
        }
        if (intent != null) {
            // Check login!
            if (!ThirdPartyManager.isThirdParty() && !LoginProxy.getInstance().isTokenExist()) {
                Toast.makeText(this, R.string.login_required, Toast.LENGTH_SHORT).show();
                intent = new Intent(this, AccountActivity.class);
            } else {
                if (TextUtils.isEmpty(mProductIDEditText.getText().toString())) {
                    ToastUtil.productId(this);
                    return;
                }
                if (TextUtils.isEmpty(mDSNEditText.getText().toString())) {
                    ToastUtil.dsn(this);
                    return;
                }
                intent.putExtra(EXTRA_PRODUCT_ID, mProductIDEditText.getText().toString());
                intent.putExtra(EXTRA_DSN, mDSNEditText.getText().toString());
            }
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tskm);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mProductIDEditText = findViewById(R.id.productIDEditText);
        mProductIDEditText.setText(DemoConstant.PRODUCT_ID);
        mDSNEditText = findViewById(R.id.dsnEditText);
        mDSNEditText.setText(DemoConstant.DSN);

        findViewById(R.id.alarmButton).setOnClickListener(mOnClickListener);
        findViewById(R.id.childModeButton).setOnClickListener(mOnClickListener);
        findViewById(R.id.deviceControlButton).setOnClickListener(mOnClickListener);
        findViewById(R.id.thirdpartyBindOpButton).setOnClickListener(mOnClickListener);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
