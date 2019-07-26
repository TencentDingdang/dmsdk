package com.tencent.ai.tvs.dmsdk.demo;

import android.content.Context;
import android.content.SharedPreferences;

import com.tencent.ai.dobbydemo.BuildConfig;

/**
 * 为了在demo中展示第三方账号方案和DMSDK账号方案而实现的简易类。
 */
public class ThirdPartyManager {
    private static final String KEY_ACCOUNT_ID = "accountId";
    private static SharedPreferences mPref;
    private static String sProductId = DemoConstant.PRODUCT_ID;

    public static void init(Context context) {
        mPref = context.getSharedPreferences("default", Context.MODE_PRIVATE);
    }
    public static String getThirdPartyAccountId() {
        return mPref.getString(KEY_ACCOUNT_ID, "");
    }

    public static void setThirdPartyAccountId(String accountId) {
        mPref.edit().putString(KEY_ACCOUNT_ID, accountId).apply();
    }

    public static boolean isThirdParty() {
        return BuildConfig.IS_THIRD_PARTY;
    }

    public static String getProductId() {
        return sProductId;
    }

    public static void setProductId(String productId) {
        sProductId = productId;
    }
}
