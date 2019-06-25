package com.tencent.ai.tvs.dmsdk.demo;

import android.content.Context;
import android.widget.Toast;

public final class ToastUtil {
    private ToastUtil() {}

    public static void productId(Context context) {
        Toast.makeText(context, "请填写您申请的ProductId！", Toast.LENGTH_SHORT).show();
    }

    public static void dsn(Context context) {
        Toast.makeText(context, "请填写设备的DSN！", Toast.LENGTH_SHORT).show();
    }
}
