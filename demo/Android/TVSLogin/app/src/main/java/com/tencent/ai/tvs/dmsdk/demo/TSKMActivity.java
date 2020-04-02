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

public class TSKMActivity extends AppCompatActivity {
    public static final String EXTRA_PRODUCT_ID = "PRODUCT_ID";
    public static final String EXTRA_DSN = "DSN";

    private DemoPreference preference = new DemoPreference();

    private EditText mProductIDEditText;
    private EditText mDSNEditText;
    private TextView mClientIdTextView;
    private String mClientId = "";
    private TextWatcher mUpdateClientIdWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            updateClientId();
        }
    };
    private ClipboardManager mClipboardManager;

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
            case R.id.cpAuthQqMusicButton:
                // Simply open the WebPage
                intent = new Intent(this, WebActivity.class);
                intent.putExtra(WebActivity.EXTRA_TARGET_PRESET_URL_PATH, TVSThirdPartyAuth.getPresetUrlPathByCp(ThirdPartyCp.QQ_MUSIC)+ "?app=1&weixin=1&qq=1");
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

        mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        mClientIdTextView = findViewById(R.id.clientIdTextView);
        mProductIDEditText = findViewById(R.id.productIDEditText);
        mProductIDEditText.setText(preference.loadProductID(this));
        mProductIDEditText.addTextChangedListener(mUpdateClientIdWatcher);
        mDSNEditText = findViewById(R.id.dsnEditText);
        mDSNEditText.setText(preference.loadDSN(this));
        mDSNEditText.addTextChangedListener(mUpdateClientIdWatcher);

        findViewById(R.id.alarmButton).setOnClickListener(mOnClickListener);
        findViewById(R.id.childModeButton).setOnClickListener(mOnClickListener);
        findViewById(R.id.deviceControlButton).setOnClickListener(mOnClickListener);
        findViewById(R.id.thirdpartyBindOpButton).setOnClickListener(mOnClickListener);
        findViewById(R.id.cpAuthQqMusicButton).setOnClickListener(mOnClickListener);

        findViewById(R.id.copyClientIdButton).setOnClickListener(v -> {
            updateClientId();
            ClipData clipData = ClipData.newPlainText("Client ID", mClientId);
            mClipboardManager.setPrimaryClip(clipData);
            Toast.makeText(this, "Client ID copied to clipboard", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh after login and come back to this page
        updateClientId();
    }

    private void updateClientId() {
        String productId = mProductIDEditText.getText().toString();
        String dsn = mDSNEditText.getText().toString();
        mClientId = LoginProxy.getInstance().getAccountInfoManager().getClientId(productId, dsn);
        mClientIdTextView.setText("Client ID:" + mClientId);
    }
}
