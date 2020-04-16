package com.tencent.ai.tvs.dmsdk.demo;

import android.content.Context;
import android.content.SharedPreferences;

public class DemoPreference {

    private static final String NAME = "demo_preference";

    private static final String DSN = "dsn";

    public void saveDSN(Context context, String dsn) {
        SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(DSN, dsn == null ? "" : dsn).commit();
    }

    public String loadProductID(Context context) {
        return DemoConstant.DEFAULT_APP_KEY + ":" + DemoConstant.DEFAULT_APP_ACCESS_TOKEN;
    }

    public String loadDSN(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return preferences.getString(DSN, DemoConstant.DSN);
    }
}
