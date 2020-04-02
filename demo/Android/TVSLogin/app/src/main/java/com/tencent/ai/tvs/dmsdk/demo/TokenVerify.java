package com.tencent.ai.tvs.dmsdk.demo;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;

import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.base.log.DMLog;
import com.tencent.ai.tvs.core.common.TVSCallback;

import static android.content.Context.ALARM_SERVICE;

public class TokenVerify {
    // 上一次刷票成功的时间
    private long lastVerifyTime = 0;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private ConnectivityManager connectivityManager;

    private static final String ACTION_VERIFY = "action.tvs.TOKEN_VERIFY";
    private static final String TAG = "TokenVerify";

    private static class SingletonHolder {
        @SuppressLint("StaticFieldLeak")
        private static TokenVerify INSTANCE = new TokenVerify();
    }

    public static TokenVerify getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private boolean isNetworkAvailable() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * 立刻开始执行刷票操作
     */
    private void startVerifyNow() {
        DMLog.i(TAG, "start verify now");
        if (!LoginProxy.getInstance().isTokenExist()) {
            // 未授权
            DMLog.i(TAG, "no need to verify, not login");
            return;
        }

        if (!isNetworkAvailable()) {
            DMLog.i(TAG, "no need to verify, network is unavailable");
            return;
        }

        LoginProxy.getInstance().tvsTokenVerify(new TVSCallback() {
            @Override
            public void onSuccess() {
                registerNextTokenVerify();
            }

            @Override
            public void onError(int code) {
                // TO-DO 重试
                DMLog.i(TAG, "verify error: " + code);
            }
        });
    }

    private TokenVerify() {

    }

    public void init(Context context) {
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(ACTION_VERIFY);

        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(ACTION_VERIFY);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                    if (isNetworkAvailable()) {
                        DMLog.i(TAG, "try refresh when network changed");
                        tryRefreshToken();
                    }
                } else if (ACTION_VERIFY.equals(action)) {
                    startVerifyNow();
                }
            }
        }, intentFilter);
    }

    private long getExpireTime() {
        long expireTime = LoginProxy.getInstance().getAccountInfoManager().getExpireTime() * 1000;
        long time = 2 * 60 * 60 * 1000;
        //long time =  30 * 1000;
        if (expireTime <= 0 || expireTime > time) {
            expireTime = time;
        }

        return expireTime;
    }

    private void setLastVerifyTime(long lastVerifyTime) {
        this.lastVerifyTime = lastVerifyTime;
    }

    /**
     * 设置定时器，下次进行刷票，一般是在登录或者刷票成功之后调用
     */
    public void registerNextTokenVerify() {
        if (alarmManager == null) {
            return;
        }

        setLastVerifyTime(System.currentTimeMillis());
        long expireTime = getExpireTime();
        refreshToken(expireTime);
    }

    /**
     * 尝试刷票，如果未过期，则不进行刷票操作
     */
    public void tryRefreshToken() {
        if (alarmManager == null) {
            return;
        }

        if (!LoginProxy.getInstance().isTokenExist()) {
            // 未授权
            DMLog.i(TAG, "no need to verify, not login");
            return;
        }

        // 账号过期时间
        long expireTime = getExpireTime();

        long time = System.currentTimeMillis() - lastVerifyTime;
        if (lastVerifyTime <= 0 || time > expireTime) {
            refreshToken(100);
        } else {
            DMLog.i(TAG, "no need to verify, wait " + (expireTime - time) + " secs");
        }
    }

    /**
     * 设置定时器，在expireTime内进行刷票
     *
     * @param expireTime 超时时间，单位为毫秒
     */
    private void refreshToken(long expireTime) {
        if (alarmManager == null) {
            return;
        }

        alarmManager.cancel(pendingIntent);
        DMLog.i(TAG, "start verify in next " + expireTime + " mills");
        long triggerAtTime = SystemClock.elapsedRealtime() + expireTime;

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
    }

    /**
     * 停止定时器，一般是在登出账号的时候调用
     */
    public void stopTokenVerify() {
        if (alarmManager == null) {
            return;
        }

        DMLog.i(TAG, "cancel verify");
        alarmManager.cancel(pendingIntent);
    }
}
