package com.tencent.ai.tvs.dmsdk.demo;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.base.log.DMLog;
import com.tencent.ai.tvs.core.common.TVSCallback;
import com.tencent.ai.tvs.env.ELoginPlatform;

public class TokenVerify {
    // 上一次刷票成功的时间
    private long lastVerifyTime = 0;
    private ConnectivityManager connectivityManager;
    private Handler mHandler = new Handler();

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

    private boolean isTokenExistAndStandard() {
        return LoginProxy.getInstance().isTokenExist(ELoginPlatform.Vendor);
    }

    private boolean isTokenExistAndLegacy() {
        return LoginProxy.getInstance().isTokenExist(ELoginPlatform.WX) || LoginProxy.getInstance().isTokenExist(ELoginPlatform.QQOpen);
    }

    private void onVerifyError(int code) {
        DMLog.i(TAG, "verify error: " + code);
    }

    private void onVerifySuccessful() {
        registerNextTokenVerify();
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

        if (isTokenExistAndLegacy()) {
            // 旧账号方案
            LoginProxy.getInstance().tvsTokenVerify(new TVSCallback() {
                @Override
                public void onSuccess() {
                    onVerifySuccessful();
                }

                @Override
                public void onError(int code) {
                    onVerifyError(code);
                }
            });
        } else if (isTokenExistAndStandard()) {
            // 厂商账号方案
            LoginProxy.getInstance().refreshToken(new LoginProxy.RefreshTokenCallback() {
                @Override
                public void onSuccess() {
                    onVerifySuccessful();
                }

                @Override
                public void onError(int code) {
                    onVerifyError(code);
                }
            });
        }


    }

    private TokenVerify() {

    }

    private Runnable mRunnable = () -> startVerifyNow();

    public void init(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
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
                }
            }
        }, intentFilter);
    }

    private long getExpireTime() {
        long expireTime = LoginProxy.getInstance().getAccountInfoManager().getExpireTime() * 1000;

        // 提前五分钟刷票，避免过期
        expireTime -= 5 * 60 * 1000;

        long time = 10 * 60 * 1000;

        if (expireTime < time) {
            expireTime = time;
        }

        time = 110 * 60 * 1000;

        if (expireTime > time) {
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
        setLastVerifyTime(System.currentTimeMillis());
        long expireTime = getExpireTime();
        refreshToken(expireTime);
    }

    /**
     * 尝试刷票，如果未过期，则不进行刷票操作
     */
    public void tryRefreshToken() {
        if (!LoginProxy.getInstance().isTokenExist()) {
            // 未授权
            DMLog.i(TAG, "no need to verify, not login");
            return;
        }

        // 账号过期时间
        long expireTime = getExpireTime();

        long time = System.currentTimeMillis() - lastVerifyTime;
        if (lastVerifyTime <= 0 || time > expireTime) {
            refreshToken(0);
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
        mHandler.removeCallbacksAndMessages(null);
        if (expireTime == 0) {
            mHandler.post(mRunnable);
        } else {
            DMLog.i(TAG, "start verify in next " + expireTime + " mills");

            mHandler.postDelayed(mRunnable, expireTime);
        }
    }

    /**
     * 停止定时器，一般是在登出账号的时候调用
     */
    public void stopTokenVerify() {
        DMLog.i(TAG, "cancel verify");
        mHandler.removeCallbacksAndMessages(null);
    }
}
