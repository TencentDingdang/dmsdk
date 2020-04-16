package com.tencent.ai.tvs.dmsdk.demo;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.ai.dobbydemo.R;
import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.core.common.TVSDevice;
import com.tencent.ai.tvs.dmsdk.demo.tskm.AlarmActivity;
import com.tencent.ai.tvs.dmsdk.demo.tskm.ChildModeActivity;
import com.tencent.ai.tvs.dmsdk.demo.tskm.DeviceControlActivity;
import com.tencent.ai.tvs.dmsdk.demo.tskm.ThirdPartBindOpActivity;
import com.tencent.ai.tvs.tskm.TVSThirdPartyAuth;
import com.tencent.ai.tvs.tskm.thirdpartyauth.ThirdPartyCp;

public class TSKMActivity extends BaseActivity {
    public static final String EXTRA_PRODUCT_ID = "PRODUCT_ID";
    public static final String EXTRA_DSN = "DSN";

    private DemoPreference preference = new DemoPreference();

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
            case R.id.cpAuthQqMusicButton:
                // Simply open the WebPage
                intent = new Intent(this, WebActivity.class);
                intent.putExtra(WebActivity.EXTRA_TARGET_PRESET_URL_PATH, TVSThirdPartyAuth.getPresetUrlPathByCp(ThirdPartyCp.QQ_MUSIC) + "?app=1&weixin=1&qq=1");
                TVSDevice device = new TVSDevice();
                device.productID = preference.loadProductID(this);
                device.dsn = preference.loadDSN(this);
                intent.putExtra(WebActivity.EXTRA_DEVICE_INFO, device);
                break;
        }
        if (intent != null) {
            // Check login!
            if (!LoginProxy.getInstance().isTokenExist()) {
                Toast.makeText(this, R.string.login_required, Toast.LENGTH_SHORT).show();
                intent = new Intent(this, AccountActivity.class);
            } else {
                intent.putExtra(EXTRA_PRODUCT_ID, preference.loadProductID(this));
                intent.putExtra(EXTRA_DSN, preference.loadDSN(this));
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

        findViewById(R.id.alarmButton).setOnClickListener(mOnClickListener);
        findViewById(R.id.childModeButton).setOnClickListener(mOnClickListener);
        findViewById(R.id.deviceControlButton).setOnClickListener(mOnClickListener);
        findViewById(R.id.cpAuthQqMusicButton).setOnClickListener(mOnClickListener);
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
