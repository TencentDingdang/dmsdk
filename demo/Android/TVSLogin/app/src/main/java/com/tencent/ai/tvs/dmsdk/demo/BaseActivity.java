package com.tencent.ai.tvs.dmsdk.demo;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        TokenVerify.getInstance().tryRefreshToken();
    }
}
