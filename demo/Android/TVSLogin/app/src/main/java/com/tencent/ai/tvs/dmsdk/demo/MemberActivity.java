package com.tencent.ai.tvs.dmsdk.demo;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.tencent.ai.dobbydemo.R;
import com.tencent.ai.tvs.member.DateUnit;
import com.tencent.ai.tvs.member.TVSMember;

import java.util.Date;

public class MemberActivity extends ModuleActivity {
    private EditText mProductIDEditText;
    private EditText mDSNEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        mProductIDEditText = findViewById(R.id.productIDEditText);
        mProductIDEditText.setText(DemoConstant.PRODUCT_ID);
        mDSNEditText = findViewById(R.id.dsnEditText);
        mDSNEditText.setText(DemoConstant.DSN);

        findViewById(R.id.deviceStatusButton).setOnClickListener(v -> {
            if (TextUtils.isEmpty(mProductIDEditText.getText().toString())) {
                ToastUtil.productId(this);
                return;
            }
            if (TextUtils.isEmpty(mDSNEditText.getText().toString())) {
                ToastUtil.dsn(this);
                return;
            }
            TVSMember.getDeviceStatus(mProductIDEditText.getText().toString(), mDSNEditText.getText().toString(), new SimpleTVSCallback3<Boolean, Integer, DateUnit>("查询领取状态") {
                @Override
                protected String loggableResult(Boolean result1, Integer result2, DateUnit result3) {
                    return "能否领取会员 = " + result1 + ", 可领取时间数 = " + result2 + ", 可领取时间单位 = " + (result3 == DateUnit.YEAR ? "年" : "月");
                }
            });
        });
        findViewById(R.id.memberStatusButton).setOnClickListener(v -> {
            if (TextUtils.isEmpty(mProductIDEditText.getText().toString())) {
                ToastUtil.productId(this);
                return;
            }
            if (TextUtils.isEmpty(mDSNEditText.getText().toString())) {
                ToastUtil.dsn(this);
                return;
            }
            TVSMember.getMemberStatus(mProductIDEditText.getText().toString(), mDSNEditText.getText().toString(), new SimpleTVSCallback3<Boolean, Date, Date>("查询会员状态") {
                @Override
                protected String loggableResult(Boolean isVIP, Date startTime, Date expireTime) {
                    return "是否会员 = " + isVIP + ", 会员开始时间 = " + startTime + ", 会员结束时间 = " + expireTime;
                }
            });
        });
    }
}
