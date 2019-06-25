package com.tencent.ai.tvs.dmsdk.demo;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.ai.dobbydemo.R;
import com.tencent.ai.tvs.aispeech.TVSAISpeech;
import com.tencent.ai.tvs.core.common.TVSAISpeechItem;
import com.tencent.ai.tvs.core.common.TVSTTSConfig;

import java.util.ArrayList;

public class AISpeechActivity extends ModuleActivity {
    private EditText mProductIDEditText;
    private EditText mDSNEditText;
    private EditText mSpeechIDEditText;
    private EditText mSpeedEditText;
    private EditText mVolumeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_speech);

        mProductIDEditText = findViewById(R.id.productIDEditText);
        mProductIDEditText.setText(DemoConstant.PRODUCT_ID);
        mDSNEditText = findViewById(R.id.dsnEditText);
        mDSNEditText.setText(DemoConstant.DSN);
        mSpeechIDEditText = findViewById(R.id.speechIDEditText);
        mSpeedEditText = findViewById(R.id.speedEditText);
        mVolumeEditText = findViewById(R.id.volumeEditText);

        findViewById(R.id.getBotButton).setOnClickListener(v -> {
            if (TextUtils.isEmpty(mProductIDEditText.getText().toString())) {
                ToastUtil.productId(this);
                return;
            }
            TVSAISpeech.getBotAISpeechOption(mProductIDEditText.getText().toString(), new SimpleTVSCallback1<ArrayList<TVSAISpeechItem>>("查询Bot AI Speech", false) {
                @Override
                protected String loggableResult(ArrayList<TVSAISpeechItem> result) {
                    StringBuilder ret = new StringBuilder();
                    for (TVSAISpeechItem item : result) {
                        ret
                                .append("SpeechID = ").append(item.speechID)
                                .append(", SpeechName = ").append(item.speechName)
                                .append(", SpeechEnum = ").append(item.speechEnum)
                                .append(", IsDefaultOption = ").append(item.isDefaultOption);
                        if (item.ttsConfig != null) {
                            ret
                                .append(", Speed = ").append(item.ttsConfig.speed)
                                .append(", Volume = ").append(item.ttsConfig.volume);
                        }
                        ret.append("\n");
                    }
                    return ret.toString();
                }
            });
        });
        findViewById(R.id.getDeviceButton).setOnClickListener(v -> {
            if (TextUtils.isEmpty(mProductIDEditText.getText().toString())) {
                ToastUtil.productId(this);
                return;
            }
            if (TextUtils.isEmpty(mDSNEditText.getText().toString())) {
                ToastUtil.dsn(this);
                return;
            }
            TVSAISpeech.getDeviceAISpeech(mProductIDEditText.getText().toString(), mDSNEditText.getText().toString(), new SimpleTVSCallback1<TVSAISpeechItem>("查询设备AI Speech", true) {
                @Override
                protected String loggableResult(TVSAISpeechItem result) {
                    return "SpeechID = " + result.speechID +
                            ", SpeechName = " + result.speechName +
                            ", SpeechEnum = " + result.speechEnum +
                            ", IsDefaultOption = " + result.isDefaultOption +
                            (result.ttsConfig != null ? (", Speed = " + result.ttsConfig.speed +
                            ", Volume = " + result.ttsConfig.volume) : "") +
                            "\n";
                }
            });
        });
        findViewById(R.id.setDeviceButton).setOnClickListener(v -> {
            if (TextUtils.isEmpty(mProductIDEditText.getText().toString())) {
                ToastUtil.productId(this);
                return;
            }
            if (TextUtils.isEmpty(mDSNEditText.getText().toString())) {
                ToastUtil.dsn(this);
                return;
            }
            TVSTTSConfig tc = new TVSTTSConfig();
            try {
                tc.speed = Integer.parseInt(mSpeedEditText.getText().toString());
                tc.volume = Integer.parseInt(mVolumeEditText.getText().toString());
                // Ensure SpeechID is number
                Integer.parseInt(mSpeechIDEditText.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "请保证语速和音量输入的是数字", Toast.LENGTH_SHORT).show();
                return;
            }
            TVSAISpeech.setDeviceAISpeech(mProductIDEditText.getText().toString(), mDSNEditText.getText().toString(), mSpeechIDEditText.getText().toString(), tc, new SimpleTVSCallback("设置设备AI Speech", true));
        });
    }
}
