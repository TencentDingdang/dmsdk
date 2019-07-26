package com.tencent.ai.tvs.dmsdk.demo;

import com.tencent.ai.tvs.base.util.TvsDeviceUtil;
import com.tencent.ai.tvs.core.account.AuthDelegate;
import com.tencent.ai.tvs.core.account.TVSAccountInfo;
import com.tencent.ai.tvs.core.account.TVSUserInfo;
import com.tencent.ai.tvs.core.common.TVSCallback;
import com.tencent.ai.tvs.env.ELoginPlatform;

class ThirdPartyAuthDelegate implements AuthDelegate {
    @Override
    public void tvsWXLogin(TVSCallback callback) {
    }

    @Override
    public void tvsWXTokenRefresh(TVSCallback callback) {
    }

    @Override
    public void tvsQQOpenLogin(TVSCallback callback) {
    }

    @Override
    public void tvsLogout() {
    }

    @Override
    public TVSAccountInfo getAccountInfo() {
        TVSAccountInfo accountInfo = new TVSAccountInfo();
        accountInfo.setPlatform(ELoginPlatform.ThirdParty);
        accountInfo.setOpenID(ThirdPartyManager.getThirdPartyAccountId());
        accountInfo.setAppId(TvsDeviceUtil.getAppKey(ThirdPartyManager.getProductId()));
        return accountInfo;
    }

    @Override
    public TVSUserInfo getUserInfo() {
        return null;
    }

    @Override
    public void tvsSetPhoneNumber(String phoneNumber) {
    }
}
