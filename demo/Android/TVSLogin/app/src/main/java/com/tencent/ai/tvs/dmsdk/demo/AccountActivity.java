package com.tencent.ai.tvs.dmsdk.demo;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.ai.dobbydemo.R;
import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.core.account.AccountInfoManager;
import com.tencent.ai.tvs.core.account.UserInfoManager;
import com.tencent.ai.tvs.env.ELoginPlatform;

public class AccountActivity extends ModuleActivity {
    private ClipboardManager mClipboardManager;
    private EditText mAccountIdEditText;
    private TextView mWXTextView;
    private Button mWXLoginButton;
    private Button mWXRefreshButton;
    private TextView mQQTextView;
    private Button mQQLoginButton;
    private Button mQQVerifyButton;
    private Button mAccountInfoButton;
    private Button mUserInfoButton;
    private Button mLogoutButton;
    private TextView mClientIdTextView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        LoginProxy api = LoginProxy.getInstance();

        mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

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
        mWXTextView = findViewById(R.id.wxTitleTextView);
        mWXLoginButton = findViewById(R.id.wxLoginButton);
        mWXLoginButton.setOnClickListener(v -> api.tvsLogin(ELoginPlatform.WX, null, new SimpleTVSCallback("微信登录")));
        mWXRefreshButton = findViewById(R.id.wxRefreshButton);
        mWXRefreshButton.setOnClickListener(v -> api.tvsTokenVerify(new SimpleTVSCallback("微信刷票")));
        mQQTextView = findViewById(R.id.qqTitleTextView);
        mQQLoginButton = findViewById(R.id.qqLoginButton);
        mQQLoginButton.setOnClickListener(v -> api.tvsLogin(ELoginPlatform.QQOpen, this, new SimpleTVSCallback("QQ登录")));
        mQQVerifyButton = findViewById(R.id.qqRefreshButton);
        mQQVerifyButton.setOnClickListener(v -> api.tvsTokenVerify(new SimpleTVSCallback("QQ验票")));
        mAccountInfoButton = findViewById(R.id.accountInfoButton);
        mAccountInfoButton.setOnClickListener(v -> {
            AccountInfoManager m = AccountInfoManager.getInstance();
            logSection("获取账户信息(第三方账号方案不适用)");
            logLine("appID = " + m.getAppID());
            logLine("openID = " + m.getOpenID());
            logLine("tvsID = " + m.getTvsID());
            logLine("accessToken = " + m.getAccessToken());
            logLine("refreshToken = " + m.getRefreshToken());
            logLine("userID = " + m.getUserId());
            logLine("clientID = " + m.getClientId(DemoConstant.PRODUCT_ID, DemoConstant.DSN));
        });
        mUserInfoButton = findViewById(R.id.userInfoButton);
        mUserInfoButton.setOnClickListener(v -> {
            UserInfoManager m = UserInfoManager.getInstance();
            logSection("获取用户信息");
            logLine("nickname = " + m.getNickname());
            logLine("gender = " + (m.getSex() == UserInfoManager.MALE ? "男" : "女"));
            logLine("avatar = " + m.getHeadImgUrl());
            logLine("phoneNumber = " + m.getPhoneNumber());
        });
        mLogoutButton = findViewById(R.id.logoutButton);
        mLogoutButton.setOnClickListener(v -> {
            api.logout();
            reloadState();
            logSection("退出登录");
            logLine("Success");
        });
        mClientIdTextView = findViewById(R.id.clientIdTextView);
        mClientIdTextView.setOnClickListener(v -> {
            updateClientId();
            ClipData clipData = ClipData.newPlainText("Client ID", mClientIdTextView.getText());
            mClipboardManager.setPrimaryClip(clipData);
            Toast.makeText(this, "Client ID copied to clipboard", Toast.LENGTH_SHORT).show();
        });
        CheckBox logoutBeforeReloginCheckBox = findViewById(R.id.logoutBeforeReloginCheckBox);
        logoutBeforeReloginCheckBox.setChecked(LoginProxy.getInstance().isLogoutBeforeRelogin());
        logoutBeforeReloginCheckBox.setOnClickListener(view -> LoginProxy.getInstance().setLogoutBeforeRelogin(logoutBeforeReloginCheckBox.isChecked()));

        // Init login state
        reloadState();

        dmsdkRadioButton.setChecked(!ThirdPartyManager.isThirdParty());
        thirdPartyRadioButton.setChecked(ThirdPartyManager.isThirdParty());
        dmsdkRadioButton.setEnabled(false);
        thirdPartyRadioButton.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh after login and come back to this page
        updateClientId();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (LoginProxy.getInstance().handleQQOpenIntent(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void reloadState() {
        ELoginPlatform platform = AccountInfoManager.getInstance().getPlatformType();
        mWXTextView.setTextColor(platform == ELoginPlatform.WX ? Color.GREEN : Color.BLACK);
        mWXLoginButton.setEnabled(platform == null);
        mWXRefreshButton.setEnabled(platform == ELoginPlatform.WX);
        mQQTextView.setTextColor(platform == ELoginPlatform.QQOpen ? Color.GREEN : Color.BLACK);
        mQQLoginButton.setEnabled(platform == null);
        mQQVerifyButton.setEnabled(platform == ELoginPlatform.QQOpen);
        mAccountInfoButton.setEnabled(platform != null);
        mUserInfoButton.setEnabled(platform != null);
        mLogoutButton.setEnabled(platform != null);
    }

    private void changeMode(boolean isThirdParty) {
        mAccountIdEditText.setEnabled(isThirdParty);
        String accountId = isThirdParty ? ThirdPartyManager.getThirdPartyAccountId() : AccountInfoManager.getInstance().getOpenID();
        mAccountIdEditText.setText(accountId == null ? "" : accountId);
        updateClientId();
    }

    private void updateClientId() {
        String clientId = ThirdPartyManager.isThirdParty()
                ? AccountInfoManager.getClientIdForThirdParty(mAccountIdEditText.getText().toString(), "", "", DemoConstant.PRODUCT_ID, DemoConstant.DSN)
                : AccountInfoManager.getInstance().getClientId(DemoConstant.PRODUCT_ID, DemoConstant.DSN);
        mClientIdTextView.setText(clientId);
    }

    private class SimpleTVSCallback extends ModuleActivity.SimpleTVSCallback {
        private SimpleTVSCallback(String action) {
            super(action, false);
        }

        @Override
        public void onSuccess() {
            logSection(mAction);
            logLine("Success");
            reloadState();

        }

        @Override
        public void onError(int code) {
            logSection(mAction);
            logLine("Error: code = " + code);
            reloadState();
        }
    }
}
