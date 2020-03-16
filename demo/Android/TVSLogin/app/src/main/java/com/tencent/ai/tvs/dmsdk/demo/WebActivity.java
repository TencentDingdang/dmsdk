package com.tencent.ai.tvs.dmsdk.demo;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.tencent.ai.dobbydemo.BuildConfig;
import com.tencent.ai.dobbydemo.R;
import com.tencent.ai.tvs.core.common.TVSDevice;
import com.tencent.ai.tvs.env.ELoginPlatform;
import com.tencent.ai.tvs.env.EUserAttrType;
import com.tencent.ai.tvs.tskm.TVSThirdPartyAuth;
import com.tencent.ai.tvs.tskm.thirdpartyauth.CpAuthAgent;
import com.tencent.ai.tvs.tskm.thirdpartyauth.ThirdPartyCp;
import com.tencent.ai.tvs.web.TVSWebController;
import com.tencent.ai.tvs.web.TVSWebView;

import org.json.JSONObject;

public class WebActivity extends ModuleActivity {
    private String LOG_TAG = "DMSDK_WebActivity";
    public static final String EXTRA_TARGET_URL = "targetUrl";
    public static final String EXTRA_TARGET_PRESET_URL_PATH = "targetPresetUrlPath";
    public static final String EXTRA_DEVICE_INFO = "devInfo";

    public static final int ACTIVITY_RESULT_CODE_FILECHOOSER = 1000;

    private ProgressBar mProgressBar;
    private EditText mURLEditText;

    private TVSWebController mWebViewController;
    private ImageButton mPresetButton;
    private ImageButton mGoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_web);
        mProgressBar = findViewById(R.id.progressBar);
        mURLEditText = findViewById(R.id.urlEditText);
        mPresetButton = findViewById(R.id.presetButton);
        registerForContextMenu(mPresetButton);
        mPresetButton.setOnClickListener(this::openContextMenu);
        mPresetButton.setLongClickable(false);
        mGoButton = findViewById(R.id.goButton);
        mGoButton.setOnClickListener(v -> mWebViewController.loadURL(mURLEditText.getText().toString()));

        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        mWebViewController = ((TVSWebView) findViewById(R.id.tvsWebView)).getController();
        Intent intent = getIntent();
        TVSDevice tvsDevice = (TVSDevice) intent.getSerializableExtra(EXTRA_DEVICE_INFO);
        String targetPresetUrlPath = intent.getStringExtra(EXTRA_TARGET_PRESET_URL_PATH);
        String targetUrl = intent.getStringExtra(EXTRA_TARGET_URL);
        String ddAuthRedirectUrl = intent.getStringExtra("ddAuthRedirectUrl");
        mWebViewController.setDeviceInfo(tvsDevice);
        mWebViewController.setDDAuthRedirectUrl(ddAuthRedirectUrl);
        mWebViewController.setUIEventListener(new DemoUIEventListener());
        mWebViewController.setBusinessEventListener(new DemoBusinessEventListener());
        if (targetPresetUrlPath != null) {
            mWebViewController.loadPresetURLByPath(targetPresetUrlPath);
        } else {
            mWebViewController.loadURL(targetUrl != null ? targetUrl : "https://dingdang.qq.com");
        }

        CheckBox cacheCheckBox = findViewById(R.id.cacheCheckBox);
        cacheCheckBox.setChecked(mWebViewController.isLoadCacheOnDisconnected());
        cacheCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                mWebViewController.setLoadCacheOnDisconnected(isChecked)
        );
        CheckBox debugCheckBox = findViewById(R.id.debugCheckBox);
        debugCheckBox.setChecked(mWebViewController.isEnableDebugFlag());
        debugCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                mWebViewController.setEnableDebugFlag(isChecked)
        );
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.v(LOG_TAG, "onNewIntent authResult = " + intent.getStringExtra("authResult"));
        mWebViewController.loadURL(TVSThirdPartyAuth.getTargetUrl());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ACTIVITY_RESULT_CODE_FILECHOOSER) {
            mWebViewController.onPickFileResult(resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_web, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_web_preset, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!mWebViewController.goBack()) {
                    hideInputMethod();
                    finish();
                }
                return true;
            case R.id.backwardMenuItem:
                mWebViewController.goBack();
                return true;
            case R.id.stopMenuItem:
                mWebViewController.stop();
                return true;
            case R.id.reloadMenuItem:
                mWebViewController.reload();
                return true;
            case R.id.forwardMenuItem:
                mWebViewController.goForward();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO: Change URL EditText
        switch (item.getItemId()) {
            case R.id.authMenuItem:
                mWebViewController.toPresetURL(EUserAttrType.AUTH);
                return true;
            case R.id.profileMenuItem:
                mWebViewController.toPresetURL(EUserAttrType.HOMEPAGE);
                return true;
            case R.id.memberRequestMenuItem:
                mWebViewController.toPresetURL(EUserAttrType.CPOPERATION);
                return true;
            case R.id.memberPurchaseMenuItem:
                mWebViewController.toPresetURL(EUserAttrType.RECHARGE);
                return true;
            case R.id.phoneMenuItem:
                mWebViewController.toPresetURL(EUserAttrType.INFOSETTING);
                return true;
            case R.id.qqMusicMenuItem:
                mWebViewController.toPresetURL(EUserAttrType.QQ_MUSIC);
                return true;
            case R.id.iotMenuItem:
                mWebViewController.toPresetURL(EUserAttrType.IOT);
                return true;
            case R.id.musicCtrlItem:
                mWebViewController.toPresetURL(EUserAttrType.MUSIC_CTRL);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CpAuthAgent agent = TVSThirdPartyAuth.getCpAuthAgent(ThirdPartyCp.QQ_MUSIC);
        if (agent != null) {
            agent.onResume();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!mWebViewController.goBack()) {
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        Log.v(LOG_TAG, "onDestroy");
        CpAuthAgent agent = TVSThirdPartyAuth.getCpAuthAgent(ThirdPartyCp.QQ_MUSIC);
        if (agent != null) {
            agent.onDestory();
        }
        mWebViewController.onDestroy();
        super.onDestroy();
    }

    private void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(mURLEditText.getWindowToken(), 0);
        }
    }

    private class DemoUIEventListener implements TVSWebController.UIEventListener {
        @Override
        public void requireUISettings(String settings) {
        }

        @Override
        public boolean shouldOverrideUrlLoading(String url) {
            return false;
        }

        @Override
        public void onReceiveTitle(String title) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(title);
            }
        }

        @Override
        public void onLoadProgress(int progress) {
            mProgressBar.setProgress(progress);
        }

        @Override
        public void onLoadStarted(String url) {
            Log.i(LOG_TAG, "onLoadStarted: url = " + url);
            mURLEditText.setText(url);
            mProgressBar.setAlpha(1);
        }

        @Override
        public void onLoadFinished(String url) {
            Log.i(LOG_TAG, "onLoadFinished: url = " + url);
            ObjectAnimator animator = ObjectAnimator.ofFloat(mProgressBar, "alpha", 0);
            animator.setDuration(500);
            animator.start();
        }

        @Override
        public void onLoadError() {
            Log.w(LOG_TAG, "onLoadError");
        }

        @Override
        public boolean showDialog(String title, String message,
                                  String positiveButtonText, @Nullable DialogInterface.OnClickListener positiveButtonListener,
                                  @Nullable String negativeButtonText, @Nullable DialogInterface.OnClickListener negativeButtonListener) {
            return false;
        }
    }

    private class DemoBusinessEventListener implements TVSWebController.BusinessEventListener {
        @Override
        public void requireCloseWebView() {
            finish();
        }

        @Override
        public void onPickFile(Intent fileChooser) {
            startActivityForResult(fileChooser, ACTIVITY_RESULT_CODE_FILECHOOSER);
        }

        @Override
        public void onReceiveProxyData(JSONObject data) {
        }

        @Override
        public void onLoginResult(ELoginPlatform platform, int errorCode) {
        }

        @Override
        public void onTokenRefreshResult(ELoginPlatform platform, int errorCode) {
            Log.i(LOG_TAG, "onTokenRefreshResult: platform = " + platform + ", errorCode = " + errorCode);
        }
    }
}