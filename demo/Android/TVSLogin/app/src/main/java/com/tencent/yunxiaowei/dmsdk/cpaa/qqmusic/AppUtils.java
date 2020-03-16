package com.tencent.yunxiaowei.dmsdk.cpaa.qqmusic;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.tencent.ai.tvs.base.log.DMLog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtils {
    private static final Pattern PATTERN_VERSION_NAME = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");

    public static boolean checkAppInstallation(Context context, String packageName, int requiredMaj, int requiredMin, int requiredPatch, String tag) {
        // 判定QQ音乐APP是否已经安装
        try {
            PackageManager mPackageManager = context.getPackageManager();
            PackageInfo packageInfo = mPackageManager.getPackageInfo(packageName, 0);
            String versionName = packageInfo.versionName;
            DMLog.i(tag, "checkCpAppInstallation: versionName = [" + versionName + "]");
            Matcher matcher = PATTERN_VERSION_NAME.matcher(versionName);
            if (!matcher.matches()) {
                return false;
            }
            int major = Integer.parseInt(matcher.group(1));
            int minor = Integer.parseInt(matcher.group(2));
            int patch = Integer.parseInt(matcher.group(3));
            return major > requiredMaj ||
                    (major == requiredMaj && minor > requiredMin) ||
                    (major == requiredMaj && minor == requiredMin && patch >= requiredPatch);
        } catch (PackageManager.NameNotFoundException | NumberFormatException e) {
            DMLog.e(tag, "checkCpAppInstallation: Fail to check installed version: ", e);
            return false;
        }
    }

    public static void jumpToDownload(Context context, String packageName) {
        String url = "https://sj.qq.com/myapp/detail.htm?apkName=" + packageName;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
