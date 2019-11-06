package com.tencent.yunxiaowei.dmsdk.cpaa.qqmusic;

import android.util.Base64;

import com.tencent.qqmusic.third.api.contract.Keys;

import org.json.JSONObject;

public class OpenIDHelper {
    private static final String QQ_MUSIC_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCrp4sMcJjY9hb2J3sHWlwIEBrJlw2Cimv+rZAQmR8V3EI+0PUK14pL8OcG7CY79li30IHwYGWwUapADKA01nKgNeq7+rSciMYZv6ByVq+ocxKY8az78HwIppwxKWpQ+ziqYavvfE5+iHIzAc8RvGj9lL6xx1zhoPkdaA0agAyuMQIDAQAB";

    public static String getEncryptString(String nonce, String privateKey, String callbackUrl) {
        if (nonce == null || nonce.isEmpty()) {
            return null;
        }

        try {
            //1.使用App私钥签名
            String signString = RSAUtils.sign(nonce.getBytes(), privateKey);

            JSONObject signJson = new JSONObject();
            signJson.put(Keys.API_RETURN_KEY_NONCE, nonce);
            signJson.put(Keys.API_RETURN_KEY_SIGN, signString);
            signJson.put(Keys.API_RETURN_KEY_CALLBACK_URL, callbackUrl);
            String sourceString = signJson.toString();

            //2. 使用Q音公钥加密(随机数+签名)
            byte[] encryptData = RSAUtils.encryptByPublicKey(sourceString.getBytes(), QQ_MUSIC_PUBLIC_KEY);
            if (encryptData == null) {
                return null;
            }
            return Base64.encodeToString(encryptData, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decryptQQMEncryptString(String qmEncryptString, String privateKey) {
        try {
            byte[] qmEncryptData = Base64.decode(qmEncryptString, Base64.DEFAULT);
            //7.使用App私钥解密
            byte[] decryptData = RSAUtils.decryptByPrivateKey(qmEncryptData, privateKey);
            if (decryptData != null) {
                return new String(decryptData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检查QQ音乐签名
     *
     * @param sign  签名
     * @param nonce 种子
     */
    public static boolean checkQMSign(String sign, String nonce) {
        if (sign == null || nonce == null)
            return false;
        try {
            return RSAUtils.verify(nonce.getBytes(), QQ_MUSIC_PUBLIC_KEY, sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
