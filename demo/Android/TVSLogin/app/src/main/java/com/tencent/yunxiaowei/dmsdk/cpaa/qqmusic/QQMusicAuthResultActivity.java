package com.tencent.yunxiaowei.dmsdk.cpaa.qqmusic;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.ai.tvs.base.log.DMLog;
import com.tencent.ai.tvs.tskm.TVSThirdPartyAuth;
import com.tencent.ai.tvs.tskm.thirdpartyauth.CpAuthAgent;
import com.tencent.ai.tvs.tskm.thirdpartyauth.ThirdPartyCp;

import androidx.appcompat.app.AppCompatActivity;

public class QQMusicAuthResultActivity extends AppCompatActivity {
    private static final String TAG = "QQMusicAuthResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = getIntent().getData();
        DMLog.i(TAG, "uri:" + uri);
        CpAuthAgent agent = TVSThirdPartyAuth.getCpAuthAgent(ThirdPartyCp.QQ_MUSIC);
        if (uri != null && agent != null && agent instanceof QQMusicAuthAgent) {
            QQMusicAuthAgent qqMusicAuthAgent = (QQMusicAuthAgent) agent;
            String cmd = uri.getQueryParameter("cmd");
            String qmlogin = uri.getQueryParameter("qmlogin");
            if ("verify".equals(cmd)) {
                int retCode;
                try {
                    retCode = Integer.parseInt(uri.getQueryParameter("ret"));
                } catch (NumberFormatException e) {
                    retCode = -1;
                }
                // On verify: 0 -> ok, -1 -> error, -2 -> cancel
                if (retCode == 0) {
                    qqMusicAuthAgent.onAuthResultFromQqMusic(QQMusicAuthAgent.AUTH_RESULT_OK);
                } else if (retCode == -2) {
                    qqMusicAuthAgent.onAuthResultFromQqMusic(QQMusicAuthAgent.AUTH_RESULT_CANCEL);
                } else {
                    qqMusicAuthAgent.onAuthResultFromQqMusic(QQMusicAuthAgent.AUTH_RESULT_ERROR);
                }
            } else if (!TextUtils.isEmpty(qmlogin)) {
                int retCode;
                try {
                    retCode = Integer.parseInt(qmlogin);
                } catch (NumberFormatException e) {
                    retCode = -1;
                }
                // On qmlogin: 1 -> ok, 0 -> cancel
                if (retCode == 1) {
                    qqMusicAuthAgent.onAuthResultFromQqMusic(QQMusicAuthAgent.AUTH_RESULT_OK);
                } else if (retCode == 0) {
                    qqMusicAuthAgent.onAuthResultFromQqMusic(QQMusicAuthAgent.AUTH_RESULT_CANCEL);
                } else {
                    qqMusicAuthAgent.onAuthResultFromQqMusic(QQMusicAuthAgent.AUTH_RESULT_ERROR);
                }
            } else {
                // On other response, simply regard as error
                qqMusicAuthAgent.onAuthResultFromQqMusic(QQMusicAuthAgent.AUTH_RESULT_ERROR);
            }
        }

        finish();
    }
}
