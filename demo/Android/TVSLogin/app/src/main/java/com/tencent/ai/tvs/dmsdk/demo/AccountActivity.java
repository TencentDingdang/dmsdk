package com.tencent.ai.tvs.dmsdk.demo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tencent.ai.dobbydemo.R;
import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.core.account.AccountInfoManager;
import com.tencent.ai.tvs.core.account.UserInfoManager;
import com.tencent.ai.tvs.env.ELoginPlatform;

public class AccountActivity extends ModuleActivity {
    private TextView mWXTextView;
    private Button mWXLoginButton;
    private Button mWXRefreshButton;
    private TextView mQQTextView;
    private Button mQQLoginButton;
    private Button mQQVerifyButton;
    private Button mAccountInfoButton;
    private Button mUserInfoButton;
    private Button mLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        LoginProxy api = LoginProxy.getInstance();

        String productID = "mProductId";
        String dsn ="mDSN";

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
            logSection("获取账户信息");
            logLine("appID = " + m.getAppID());
            logLine("openID = " + m.getOpenID());
            logLine("tvsID = " + m.getTvsID());
            logLine("accessToken = " + m.getAccessToken());
            logLine("refreshToken = " + m.getRefreshToken());
            logLine("userID = " + m.getUserId());
            logLine("clientID = " + m.getClientId(productID, dsn));
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
        CheckBox logoutBeforeReloginCheckBox = findViewById(R.id.logoutBeforeReloginCheckBox);
        logoutBeforeReloginCheckBox.setChecked(LoginProxy.getInstance().isLogoutBeforeRelogin());
        logoutBeforeReloginCheckBox.setOnClickListener(view -> LoginProxy.getInstance().setLogoutBeforeRelogin(logoutBeforeReloginCheckBox.isChecked()));

        // Init login state
        reloadState();
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
