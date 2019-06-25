package com.tencent.ai.tvs.dmsdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.ai.dobbydemo.R;
import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.core.common.TVSDevice;
import com.tencent.ai.tvs.env.ELoginEnv;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ModuleListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new ModuleListAdapter();

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

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        initModuleList();

        // 每次应用启动时应当调用一次票据刷新，尽量保证票据不过期！
        if (LoginProxy.getInstance().isTokenExist()) {
            LoginProxy.getInstance().tvsTokenVerify(null);
        }
    }

    private void initModuleList() {
        mAdapter.addModuleEntry(getString(R.string.module_account), () -> startActivity(new Intent(this, AccountActivity.class)));
        mAdapter.addModuleEntry(getString(R.string.module_device_binding), () -> startActivity(new Intent(this, DeviceBindingActivity.class)));
        mAdapter.addModuleEntry(getString(R.string.module_web), () -> {
            if (TextUtils.isEmpty(DemoConstant.PRODUCT_ID) || TextUtils.isEmpty(DemoConstant.DSN)) {
                Toast.makeText(this, "请在DemoConstant类中填写ProductID和DSN", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, WebActivity.class);
            TVSDevice device = new TVSDevice();
            device.productID = DemoConstant.PRODUCT_ID;
            device.dsn = DemoConstant.DSN;
            intent.putExtra("devInfo", device);
            startActivity(intent);
        });
        mAdapter.addModuleEntry(getString(R.string.module_member), () -> startActivity(new Intent(this, MemberActivity.class)));
        mAdapter.addModuleEntry(getString(R.string.module_tskm), () -> startActivity(new Intent(this, TSKMActivity.class)));
        mAdapter.addModuleEntry(getString(R.string.module_ai_speech), () -> startActivity(new Intent(this, AISpeechActivity.class)));
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
