package com.tencent.ai.tvs.dmsdk.demo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tencent.ai.dobbydemo.R;
import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.core.account.AccountInfoManager;
import com.tencent.ai.tvs.core.account.TVSAccountProfile;
import com.tencent.ai.tvs.env.ELoginPlatform;

public class AccountActivity extends ModuleActivity {
    private Button mAccountInfoButton;
    private Button mUserInfoButton;
    private Button mLogoutButton;
    private Button mSigButton;
    private Button mLoginButton;
    private Button mAccountModifyButton;
    private Button mRefreshTokenButton;
    private String mCurrentSig = "";

    private void registerNextTokenVerify() {
        TokenVerify.getInstance().registerNextTokenVerify();
    }

    private void stopTokenVerify() {
        TokenVerify.getInstance().stopTokenVerify();
    }

    private void onClickGetSig() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_vendor_login, null);
        final EditText editText = view.findViewById(R.id.valueEditText);
        builder.setTitle("获取Sig");
        builder.setView(view);
        builder.setPositiveButton("获取", (dialogInterface, i) -> {
            String userName = editText.getText().toString();
            if (TextUtils.isEmpty(userName)) {
                Toast.makeText(AccountActivity.this, "请填写用户名", Toast.LENGTH_LONG).show();
                return;
            }
            logSection("获取Sig");
            VendorSigUtils.getSig(LoginProxy.getInstance().getEnv(), userName, DemoConstant.DEFAULT_APP_KEY, DemoConstant.DEFAULT_APP_SECRET, (error, sig) -> {

                if (error == 0) {
                    mCurrentSig = sig;
                    runOnUiThread(() -> {
                        logLine("成功");
                        logLine(sig);
                    });
                } else {
                    runOnUiThread(() -> {
                        logLine("失败， 错误码 " + error);
                    });
                }
            });
        });
        builder.show();
    }

    private void onLoginButtonClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_vendor_login, null);
        builder.setTitle("登录");
        final EditText editText = view.findViewById(R.id.valueEditText);
        editText.setText(mCurrentSig);
        builder.setView(view);
        builder.setPositiveButton("确定", (dialogInterface, i) -> {
            String sig = editText.getText().toString();
            if (TextUtils.isEmpty(sig)) {
                Toast.makeText(AccountActivity.this, "请填写Sig", Toast.LENGTH_LONG).show();
                return;
            }
            logSection("标准化账号方案登录");
            LoginProxy.getInstance().loginWithVendorAccount(sig, new LoginProxy.VendorLoginCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        logLine("登录成功");
                        reloadState();
                        // 设置下次刷票时间
                        registerNextTokenVerify();
                    });

                }

                @Override
                public void onError(int i) {
                    runOnUiThread(() -> {
                        logLine("登录失败，error " + i);
                        reloadState();
                    });
                }
            });
        });
        builder.show();
    }

    private void onModifyAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_standard_login_account_profile, null);
        builder.setView(view);
        final EditText nickNameEditText = view.findViewById(R.id.nicknameEditText);
        final EditText avatarUrlEditText = view.findViewById(R.id.avatarUrlEditText);
        final EditText extraEditText = view.findViewById(R.id.extraEditText);
        builder.setTitle("修改用户信息");
        builder.setPositiveButton("修改", (dialogInterface, i) -> {
            logSection("设置账号资料");
            TVSAccountProfile accountProfile = new TVSAccountProfile();
            accountProfile.setNickname(nickNameEditText.getText().toString());
            accountProfile.setAvatarUrl(avatarUrlEditText.getText().toString());
            accountProfile.setExtra(extraEditText.getText().toString());
            logLine("设置为：" + accountProfile);
            LoginProxy.getInstance().setAccountProfile(accountProfile, new LoginProxy.SetAccountProfileCallback() {
                @Override
                public void onSuccess() {
                    logLine("设置成功");
                }

                @Override
                public void onError(int code) {
                    logLine("设置失败：" + code);
                }
            });
        });
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        LoginProxy api = LoginProxy.getInstance();

        mSigButton = findViewById(R.id.vendorSigButton);
        mSigButton.setOnClickListener(v -> onClickGetSig());

        mLoginButton = findViewById(R.id.vendorLoginButton);
        mLoginButton.setOnClickListener(v -> onLoginButtonClick());

        mAccountModifyButton = findViewById(R.id.modifyAccountButton);
        mAccountModifyButton.setOnClickListener(v -> onModifyAccount());

        mRefreshTokenButton = findViewById(R.id.vendorRefreshButton);
        mRefreshTokenButton.setOnClickListener(v -> {
            logSection("刷票");
            LoginProxy.getInstance().refreshToken(new LoginProxy.RefreshTokenCallback() {
                @Override
                public void onSuccess() {
                    logLine("刷票成功");
                }

                @Override
                public void onError(int code) {
                    logLine("刷票失败：" + code);
                }
            });
        });
        mAccountInfoButton = findViewById(R.id.accountInfoButton);
        mAccountInfoButton.setOnClickListener(v -> {
            AccountInfoManager m = LoginProxy.getInstance().getAccountInfoManager();
            logSection("获取账户信息");
            logLine("openID = " + m.getOpenID());
            logLine("accessToken = " + m.getAccessToken());
            logLine("refreshToken = " + m.getRefreshToken());
            logLine("expireTime = " + m.getExpireTime());
        });
        mUserInfoButton = findViewById(R.id.userInfoButton);
        mUserInfoButton.setOnClickListener(v -> {
            LoginProxy.getInstance().getAccountProfile(new LoginProxy.GetAccountProfileCallback() {
                @Override
                public void onSuccess(@NonNull TVSAccountProfile tvsAccountProfile) {
                    logSection("获取用户信息");
                    logLine("nickname = " + tvsAccountProfile.getNickname());
                    logLine("avatar = " + tvsAccountProfile.getAvatarUrl());
                    logLine("extra = " + tvsAccountProfile.getExtra());
                }

                @Override
                public void onError(int i) {
                    logSection("获取用户信息失败，错误码 " + i);
                }
            });

        });
        mLogoutButton = findViewById(R.id.logoutButton);
        mLogoutButton.setOnClickListener(v -> {
            api.logout();
            reloadState();
            logSection("退出登录");
            logLine("Success");
            stopTokenVerify();
        });

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
        //ELoginPlatform platform = LoginProxy.getInstance().getAccountInfoManager().getPlatformType();
        boolean isLogin = LoginProxy.getInstance().isTokenExist(ELoginPlatform.Vendor);
        mSigButton.setEnabled(!isLogin);
        mLoginButton.setEnabled(!isLogin);
        mAccountModifyButton.setEnabled(isLogin);
        mRefreshTokenButton.setEnabled(isLogin);
        mAccountInfoButton.setEnabled(isLogin);
        mUserInfoButton.setEnabled(isLogin);
        mLogoutButton.setEnabled(isLogin);
    }
}
