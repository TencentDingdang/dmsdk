package com.tencent.ai.tvs.dmsdk.demo;

public class DemoConstant {
    // 请填写您申请的微信AppID
    public static final String APP_ID_WX = "";

    // 在云小微开放平台生成的appkey
    public static final String DEFAULT_APP_KEY = "";

    // APP Secret用于厂商账号登录，在云小微开放平台勾选厂商账号登录后，会显示出来
    public static final String DEFAULT_APP_SECRET = "";

    // 在云小微开放平台生成的access Token
    public static final String DEFAULT_APP_ACCESS_TOKEN = "";

    // 设备的DSN
    public static final String DSN = "";

    /**
     * QQ音乐授权App ID，向QQ音乐申请后获得。
     */
    public static final String QQ_MUSIC_APP_ID = "";

    /**
     * 拉起QQ音乐应用授权，授权完成后跳回本应用时拉起本应用所用的scheme，需要和AndroidManifest.xml中QQMusicAuthResultActivity的配置对应。
     * 注意回调URL的配置要保证各应用之间唯一，因此建议在您接入时在URL中包含您的QQ音乐AppID来保证唯一性
     * （如qqmusic1://，并同步修改AndroidManifest.xml中的配置）。
     */
    public static final String QQ_MUSIC_CALLBACK_URL = "qqmusictvsdemo" + QQ_MUSIC_APP_ID + "://";

    /**
     * 与QQ音乐应用通信时需要用到的RSA私钥，参考QQ音乐提供的文档由您自行生成。
     */
    public static final String AppPrivateKey = "";
}
