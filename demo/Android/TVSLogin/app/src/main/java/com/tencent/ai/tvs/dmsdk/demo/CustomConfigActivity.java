package com.tencent.ai.tvs.dmsdk.demo;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.tencent.ai.dobbydemo.R;

public class CustomConfigActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_config);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("自定义配置");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.containerFrameLayout, PreferenceFragment.newInstance())
                .commit();

        showTipDialog();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTipDialog() {
        new AlertDialog.Builder(this)
                .setTitle("自定义配置说明")
                .setMessage("DM SDK需要在Application初始化时传入参数初始化，您可以在本界面配置初始化参数，以便不重新编译本demo即可体验相关功能。请注意修改本界面的配置后必须强杀重启本demo才会生效！")
                .setPositiveButton("确定", null)
                .show();
    }

    public static class PreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.pref_custom_config, null);
        }

        private static PreferenceFragment newInstance() {
            return new PreferenceFragment();
        }
    }
}
