package com.tencent.ai.tvs.dmsdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.tencent.ai.dobbydemo.R;
import com.tencent.ai.tvs.core.common.ErrCode;
import com.tencent.ai.tvs.core.common.TVSCallback;
import com.tencent.ai.tvs.core.common.TVSCallback1;
import com.tencent.ai.tvs.core.common.TVSCallback3;
import com.tencent.ai.tvs.dmsdk.demo.widget.LogView;

public abstract class ModuleActivity extends BaseActivity {
    private LogView mLogView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mLogView = findViewById(R.id.logView);
    }

    protected void logSection(String section) {
        if (mLogView != null) {
            mLogView.appendLogLn("======== " + section + " ========");
        }
    }

    protected void logLine(String line) {
        if (mLogView != null) {
            mLogView.appendLogLn(line);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onError(String action, int code, boolean autoJump) {
        logSection(action);
        logLine("Error: code = " + code);
        if (code == ErrCode.ERR_LOGIN_REQUIRED) {
            Toast.makeText(ModuleActivity.this, R.string.login_required, Toast.LENGTH_SHORT).show();
            if (autoJump) {
                startActivity(new Intent(ModuleActivity.this, AccountActivity.class));
            }
        }
    }

    protected class SimpleTVSCallback implements TVSCallback {
        final String mAction;
        private final boolean mAutoJump;

        public SimpleTVSCallback(String action) {
            this(action, true);
        }

        SimpleTVSCallback(String action, boolean autoJump) {
            mAction = action;
            mAutoJump = autoJump;
        }

        @Override
        public void onSuccess() {
            logSection(mAction);
            logLine("Success");
        }

        @Override
        public void onError(int code) {
            ModuleActivity.this.onError(mAction, code, mAutoJump);
        }
    }

    protected abstract class SimpleTVSCallback1<T> implements TVSCallback1<T> {
        private final String mAction;
        private final boolean mAutoJump;

        protected SimpleTVSCallback1(String action) {
            this(action, true);
        }

        protected SimpleTVSCallback1(String action, boolean autoJump) {
            mAction = action;
            mAutoJump = autoJump;
        }

        @Override
        public void onSuccess(T result) {
            logSection(mAction);
            logLine("Success");
            logLine(loggableResult(result));
        }

        protected abstract String loggableResult(T result);

        @Override
        public void onError(int code) {
            ModuleActivity.this.onError(mAction, code, mAutoJump);
        }
    }

    protected abstract class SimpleTVSCallback3<T1, T2, T3> implements TVSCallback3<T1, T2, T3> {
        private final String mAction;
        private final boolean mAutoJump;

        protected SimpleTVSCallback3(String action) {
            this(action, true);
        }

        protected SimpleTVSCallback3(String action, boolean autoJump) {
            mAction = action;
            mAutoJump = autoJump;
        }

        @Override
        public void onSuccess(T1 result1, T2 result2, T3 result3) {
            logSection(mAction);
            logLine("Success");
            logLine(loggableResult(result1, result2, result3));
        }

        protected abstract String loggableResult(T1 result1, T2 result2, T3 result3);

        @Override
        public void onError(int code) {
            ModuleActivity.this.onError(mAction, code, mAutoJump);
        }
    }
}
