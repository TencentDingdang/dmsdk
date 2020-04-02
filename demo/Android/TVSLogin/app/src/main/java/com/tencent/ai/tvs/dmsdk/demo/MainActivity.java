package com.tencent.ai.tvs.dmsdk.demo;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.tencent.ai.dobbydemo.R;
import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.core.common.TVSCallback;
import com.tencent.ai.tvs.core.common.TVSDevice;
import com.tencent.ai.tvs.env.ELoginEnv;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ModuleListAdapter mAdapter;
    private TextInputEditText mProductIdEditText;
    private TextInputEditText mDsnEditText;
    private DemoPreference preference = new DemoPreference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new ModuleListAdapter();

        findViewById(R.id.customConfigButton).setOnClickListener(v -> startActivity(new Intent(this, CustomConfigActivity.class)));
        RadioButton testingRadioButton = findViewById(R.id.testingRadioButton);
        testingRadioButton.setOnClickListener(v -> LoginProxy.getInstance().setEnv(ELoginEnv.TEST));
        RadioButton experienceRadioButton = findViewById(R.id.experienceRadioButton);
        experienceRadioButton.setOnClickListener(v -> LoginProxy.getInstance().setEnv(ELoginEnv.EX));
        RadioButton productionRadioButton = findViewById(R.id.productionRadioButton);
        productionRadioButton.setOnClickListener(v -> LoginProxy.getInstance().setEnv(ELoginEnv.FORMAL));
        ELoginEnv env = LoginProxy.getInstance().getEnv();
        switch (env) {
            case FORMAL:
                productionRadioButton.setChecked(true);
                break;
            case TEST:
                testingRadioButton.setChecked(true);
                break;
            case EX:
                experienceRadioButton.setChecked(true);
                break;
        }

        mProductIdEditText = findViewById(R.id.productIDEditText);
        mProductIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                preference.saveProductID(MainActivity.this, editable.toString());

            }
        });
        mDsnEditText = findViewById(R.id.dsnEditText);
        mDsnEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                preference.saveDSN(MainActivity.this, editable.toString());
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        initModuleList();

        // 每次应用启动时应当调用一次票据刷新，尽量保证票据不过期！
        if (LoginProxy.getInstance().isTokenExist()) {
            LoginProxy.getInstance().tvsTokenVerify(new TVSCallback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(int i) {
                    Toast.makeText(MainActivity.this, "刷新登录凭证失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mProductIdEditText.setText(preference.loadProductID(this));
        mDsnEditText.setText(preference.loadDSN(this));
    }

    private void initModuleList() {
        String productId = preference.loadProductID(this);
        String dsn = preference.loadDSN(this);
        mAdapter.addModuleEntry(getString(R.string.module_account), () -> startActivity(new Intent(this, AccountActivity.class)));
        mAdapter.addModuleEntry(getString(R.string.module_device_binding), () -> startActivity(new Intent(this, DeviceBindingActivity.class)));
        mAdapter.addModuleEntry(getString(R.string.module_web), () -> {
            if (TextUtils.isEmpty(productId) || TextUtils.isEmpty(dsn)) {
                Toast.makeText(this, "请在DemoConstant类中填写ProductID和DSN", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, WebActivity.class);
            TVSDevice device = new TVSDevice();
            device.productID = productId;
            device.dsn = dsn;
            intent.putExtra("devInfo", device);
            startActivity(intent);
        });
        mAdapter.addModuleEntry(getString(R.string.module_member), () -> startActivity(new Intent(this, MemberActivity.class)));
        mAdapter.addModuleEntry(getString(R.string.module_tskm), () -> startActivity(new Intent(this, TSKMActivity.class)));
        mAdapter.addModuleEntry(getString(R.string.log_report), () -> LoginProxy.getInstance().performLogReport(new LoginProxy.LogReportCallback() {
            @Override
            public void onSuccess(@NonNull String reportId) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setPrimaryClip(ClipData.newPlainText("Report ID", reportId));
                Toast.makeText(MainActivity.this, "日志上传成功，Report ID已经复制到剪切板", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(int i) {
                Toast.makeText(MainActivity.this, "日志上传失败", Toast.LENGTH_LONG).show();
            }
        }));
    }

    private static class ModuleListAdapter extends RecyclerView.Adapter<ModuleListAdapter.ViewHolder> {
        private final ArrayList<ModuleEntry> mModuleEntries;

        private ModuleListAdapter() {
            mModuleEntries = new ArrayList<>();
        }

        private void addModuleEntry(String title, Runnable command) {
            mModuleEntries.add(new ModuleEntry(title, command));
            notifyItemInserted(mModuleEntries.size() - 1);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_item_module_entry, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ModuleEntry entry = mModuleEntries.get(position);
            holder.itemTextView.setText(entry.title);
            holder.itemTextView.setOnClickListener(view -> entry.command.run());
        }

        @Override
        public int getItemCount() {
            return mModuleEntries.size();
        }

        private static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView itemTextView;

            private ViewHolder(View itemView) {
                super(itemView);
                itemTextView = itemView.findViewById(R.id.itemTextView);
            }
        }

        private static class ModuleEntry {
            private final String title;
            private final Runnable command;

            private ModuleEntry(String title, Runnable command) {
                this.title = title;
                this.command = command;
            }
        }
    }
}
