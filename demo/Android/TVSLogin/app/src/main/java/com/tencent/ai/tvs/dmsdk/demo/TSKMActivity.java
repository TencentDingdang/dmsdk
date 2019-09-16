package com.tencent.ai.tvs.dmsdk.demo;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.ai.dobbydemo.R;
import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.core.account.AccountInfoManager;
import com.tencent.ai.tvs.dmsdk.demo.tskm.AlarmActivity;
import com.tencent.ai.tvs.dmsdk.demo.tskm.ChildModeActivity;
import com.tencent.ai.tvs.dmsdk.demo.tskm.DeviceControlActivity;
import com.tencent.ai.tvs.dmsdk.demo.tskm.ThirdPartBindOpActivity;

public class TSKMActivity extends AppCompatActivity {
    public static final String EXTRA_PRODUCT_ID = "PRODUCT_ID";
    public static final String EXTRA_DSN = "DSN";

    private EditText mProductIDEditText;
    private EditText mDSNEditText;
    private EditText mAccountIdEditText;
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

        mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        mClientIdTextView = findViewById(R.id.clientIdTextView);
        mProductIDEditText = findViewById(R.id.productIDEditText);
        mProductIDEditText.setText(DemoConstant.PRODUCT_ID);
        mProductIDEditText.addTextChangedListener(mUpdateClientIdWatcher);
        mDSNEditText = findViewById(R.id.dsnEditText);
        mDSNEditText.setText(DemoConstant.DSN);
        mDSNEditText.addTextChangedListener(mUpdateClientIdWatcher);
        mAccountIdEditText = findViewById(R.id.accountIdEditText);
        mAccountIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (ThirdPartyManager.isThirdParty()) {
                    ThirdPartyManager.setThirdPartyAccountId(s.toString());
                }
                mUpdateClientIdWatcher.afterTextChanged(s);
            }
        });

        RadioButton dmsdkRadioButton = findViewById(R.id.dmsdkRadioButton);
        RadioButton thirdPartyRadioButton = findViewById(R.id.thirdPartyRadioButton);
        dmsdkRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                changeMode(false);
            }
        });
        thirdPartyRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                changeMode(true);
            }
        });

        findViewById(R.id.alarmButton).setOnClickListener(mOnClickListener);
        findViewById(R.id.childModeButton).setOnClickListener(mOnClickListener);
        findViewById(R.id.deviceControlButton).setOnClickListener(mOnClickListener);
        findViewById(R.id.thirdpartyBindOpButton).setOnClickListener(mOnClickListener);

        findViewById(R.id.copyClientIdButton).setOnClickListener(v -> {
            updateClientId();
            ClipData clipData = ClipData.newPlainText("Client ID", mClientId);
            mClipboardManager.setPrimaryClip(clipData);
            Toast.makeText(this, "Client ID copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        dmsdkRadioButton.setChecked(!ThirdPartyManager.isThirdParty());
        thirdPartyRadioButton.setChecked(ThirdPartyManager.isThirdParty());
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

    private void changeMode(boolean isThirdParty) {
        ThirdPartyManager.setThirdParty(isThirdParty);
        mAccountIdEditText.setEnabled(isThirdParty);
        String accountId = isThirdParty ? ThirdPartyManager.getThirdPartyAccountId() : AccountInfoManager.getInstance().getOpenID();
        mAccountIdEditText.setText(accountId == null ? "" : accountId);
        updateClientId();
    }

    private void updateClientId() {
        String productId = mProductIDEditText.getText().toString();
        String dsn = mDSNEditText.getText().toString();
        mClientId = ThirdPartyManager.isThirdParty()
                ? AccountInfoManager.getClientIdForThirdParty(mAccountIdEditText.getText().toString(), "", "", productId, dsn)
                : AccountInfoManager.getInstance().getClientId(productId, dsn);
        mClientIdTextView.setText("Client ID:" + mClientId);
    }
}
