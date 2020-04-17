package com.tencent.ai.tvs.dmsdk.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

public class DemoConstant {
    private static final String TAG = "DemoConstant";

    // 注意：此处使用的微信和QQ的App ID均为demo专用，无法用于您的应用，您需要为自己的应用申请专门的App ID！详见接入指南文档。
    public static final String APP_ID_WX = "wxdbd76c1af795f58e";
    public static final String APP_ID_QQ_OPEN = "101470979";
    // 请填入默认Product ID和DSN
    public static final String PRODUCT_ID = "";
    public static final String DSN = "";
    // 请填入您申请的QQ音乐相关参数！
    /**
     * QQ音乐授权App ID，向QQ音乐申请后获得。
     */
    public static String QQ_MUSIC_APP_ID = "";

    /**
     * 拉起QQ音乐应用授权，授权完成后跳回本应用时拉起本应用所用的scheme，需要和AndroidManifest.xml中QQMusicAuthResultActivity的配置对应。
     * 注意回调URL的配置要保证各应用之间唯一，因此建议在您接入时在URL中包含您的QQ音乐AppID来保证唯一性
     * （如qqmusic1://，并同步修改AndroidManifest.xml中的配置）。
     */
    public static final String QQ_MUSIC_CALLBACK_URL = "qqmusictvsdemo" + QQ_MUSIC_APP_ID + "://";

    /**
     * 与QQ音乐应用通信时需要用到的RSA私钥，参考QQ音乐提供的文档由您自行生成。
     */
    public static String AppPrivateKey = "";

    private static final String KEY_USE_CUSTOM_CONFIG = "useCustomConfig";
    private static final String KEY_QQ_MUSIC_APP_ID = "qqMusicAppId";
    private static final String KEY_QQ_MUSIC_PRIVATE_KEY = "qqMusicPrivateKey";

    public static void loadCustomConfigIfEnabled(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean useCustomConfig = sp.getBoolean(KEY_USE_CUSTOM_CONFIG, false);
        if (useCustomConfig) {
            // 使用在UI中配置的初始化参数
            Toast.makeText(context, "使用自定义初始化参数", Toast.LENGTH_SHORT).show();
            QQ_MUSIC_APP_ID = sp.getString(KEY_QQ_MUSIC_APP_ID, "");
            Log.i(TAG, "loadCustomConfigIfEnabled: QQ Music App ID: " + QQ_MUSIC_APP_ID);
            AppPrivateKey = sp.getString(KEY_QQ_MUSIC_PRIVATE_KEY, "");
            Log.i(TAG, "loadCustomConfigIfEnabled: QQ Music PK: " + AppPrivateKey);
        }
    }
}
