package com.tencent.ai.tvs.dmsdk.demo;

import android.util.Log;

import com.tencent.ai.tvs.env.ELoginEnv;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class VendorSigUtils {
    private static final String TAG = "VendorSigUtils";

    public interface IVendorSigCallback {
        void onCallback(int error, String sig);
    }

    public static void getSig(ELoginEnv env, String userName, String appKey, String appSecret, IVendorSigCallback callback) {
        JSONObject request = new JSONObject();
        try {
            request.put("appkey", appKey);
            StringBuilder builder = new StringBuilder();
            builder.append(appKey);
            builder.append(":");
            builder.append(appSecret);
            builder.append(":");
            builder.append(userName);

            byte[] md5Bytes = MessageDigest.getInstance("MD5").digest(builder.toString().getBytes());

            String sign = new BigInteger(1, md5Bytes).toString(16);

            request.put("encryptsecret", sign);
            request.put("firmacctid", userName);
        } catch (JSONException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            callback.onCallback(-1, "");
            return;
        }
        String url = "";
        if (env == ELoginEnv.FORMAL) {
            url = "https://firmacct.html5.qq.com/oauth2/get_sig";
        } else if (env == ELoginEnv.EX) {
            url = "https://firmacctgray.html5.qq.com/oauth2/get_sig";
        } else {
            url = "http://firmacct.sparta.html5.qq.com/oauth2/get_sig";
        }

        String requestJson = request.toString();

        Log.i(TAG, "get sig, url " + url + ", req " + requestJson);

        OkHttpClient client = new OkHttpClient();

        client.newCall(new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), requestJson))
                .build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "get sig, failed, " + e.getMessage());
                callback.onCallback(-2, "");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.i(TAG, "get sig, failed, http failed");
                    callback.onCallback(-3, "");
                    return;
                }

                ResponseBody respBody = response.body();
                if (respBody == null) {
                    callback.onCallback(-4, "");
                    return;
                }

                String respJson = respBody.string();

                try {
                    JSONObject respJsObj = new JSONObject(respJson);
                    int errorCode = respJsObj.getInt("ErrCode");
                    if (errorCode != 0) {
                        callback.onCallback(-5, "");
                        return;
                    }

                    String sig = respJsObj.getString("Sig");

                    callback.onCallback(0, sig);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onCallback(-6, "");
                }
            }
        });
    }
}
