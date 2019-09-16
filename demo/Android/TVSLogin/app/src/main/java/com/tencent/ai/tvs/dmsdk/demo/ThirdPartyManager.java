package com.tencent.ai.tvs.dmsdk.demo;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 为了在demo中展示第三方账号方案和DMSDK账号方案而实现的简易类。
 */
public class ThirdPartyManager {
    private static final String KEY_ACCOUNT_ID = "accountId";
    private static SharedPreferences mPref;
    private static boolean sIsThirdParty = false;

    public static void init(Context context) {
        mPref = context.getSharedPreferences("default", Context.MODE_PRIVATE);
    }
    public static String getThirdPartyAccountId() {
        return mPref.getString(KEY_ACCOUNT_ID, "");
    }

    public static void setThirdPartyAccountId(String accountId) {
        mPref.edit().putString(KEY_ACCOUNT_ID, accountId).apply();
    }

    public static void setThirdParty(boolean isThirdParty) {
        sIsThirdParty = isThirdParty;
    }

    public static boolean isThirdParty() {
        return sIsThirdParty;
    }
}
