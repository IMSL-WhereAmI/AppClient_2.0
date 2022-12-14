package com.ng.anthony.mininavigationdrawer.Http;

import android.text.TextUtils;
import android.util.Log;

import okhttp3.*;
//import com.squareup.okhttp.*;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import com.ng.anthony.mininavigationdrawer.MainActivity;


import com.ng.anthony.mininavigationdrawer.Sensor.SensorData;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class HttpHelper {

    //private static String apiUrl = "http://33998831by.wicp.vip";// Cmax,EMUI
    public static String apiUrl = "http://33998831by.wicp.vip:49880";// Bmax,HarmonyOs

    private MainActivity mainActivity;

    public static String sendJsonPost(String magdata,String wifidata, String accdata, String oriendata, String acctempdata, String grivatydata, String method, Integer ApNum,Integer count,String Mapidx,String deviceId) throws JSONException, IOException {
        String json = null;

        if(method.equals("wifi")) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("DevID", "0001");
            jsonObject.put("TCode", "T10101");
            jsonObject.put("MapIdx", "01");
            jsonObject.put("ApNum", ApNum);
            jsonObject.put("MagData", magdata);
            jsonObject.put("WifiData", wifidata);
            jsonObject.put("CountNum", count);
            json = jsonObject.toString();
        }else if(method.equals("mag")){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("CountNum", count);
            jsonObject.put("DevID", "0001");
            jsonObject.put("TCode", "T10102");
            jsonObject.put("MapIdx", Mapidx);
            jsonObject.put("MagData", magdata);
            jsonObject.put("BleData", wifidata);
            jsonObject.put("AccData", accdata);
            jsonObject.put("OrienData", oriendata);
            jsonObject.put("AcctempData",acctempdata);
            jsonObject.put("Grivaty",grivatydata);
            json = jsonObject.toString();
        }else if(method.equals("fusion")){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("DevID", "0003");
            jsonObject.put("TCode", "T10201");
            jsonObject.put("MapIdx", "01");
            jsonObject.put("MagData",magdata);
            jsonObject.put("WifiData",wifidata);
            jsonObject.put("ApNum", ApNum);
            jsonObject.put("CountNum",count);
            json = jsonObject.toString();
        }else if(method.equals("bluetooth")){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("DevID", "0004");
            jsonObject.put("TCode", "T10103");
            jsonObject.put("MapIdx", "01");
            jsonObject.put("MagData",magdata);
            jsonObject.put("BleData",wifidata);
            jsonObject.put("ApNum", ApNum);
            jsonObject.put("CountNum",count);
            json = jsonObject.toString();
        }else if(method.equals("BleMag")){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("CountNum", count);
            jsonObject.put("DevID", "0001");
            jsonObject.put("TCode", "T10202");
            jsonObject.put("MapIdx", Mapidx);
            jsonObject.put("MagData", magdata);
            jsonObject.put("BleData", wifidata);
            jsonObject.put("AccData", accdata);
            jsonObject.put("OrienData", oriendata);
            jsonObject.put("AcctempData",acctempdata);
            jsonObject.put("Grivaty",grivatydata);
            json = jsonObject.toString();
        }
        // HttpClient 6.0????????????
        String result = "";
        //Log.d("json", json);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        //client.setConnectTimeout(10, TimeUnit.SECONDS);
        //client.setReadTimeout(10, TimeUnit.SECONDS);
        //client.setWriteTimeout(10, TimeUnit.SECONDS);
        //client.setRetryOnConnectionFailure(true);
        try{
            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder()
                    .header("Accept-Encoding", "identity")
                    .url(apiUrl)
                    .post(body)
                    .addHeader("Connection", "close")
                    .addHeader("Charset", "UTF-8")
                    .addHeader("accept", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
                //Log.d("httphelper", "result" + result);
            } else {
                throw new IOException("httphelper " + response);
            }

        } catch (SocketTimeoutException e){
            Log.i(TAG, "ontask rerun: "+e.getCause());
            client.dispatcher().cancelAll();
            client.connectionPool().evictAll();
            //TODO: ????????????
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//
//        BufferedReader reader = null;
//        try {
//            String urlPath = apiUrl;
//            URL url = new URL(urlPath);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//            conn.setUseCaches(false);
//            conn.setRequestProperty("Connection", "Keep-Alive");
//            conn.setRequestProperty("Charset", "UTF-8");
//            conn.setConnectTimeout(30000);
//            conn.setReadTimeout(30000);
//             //conn.setRequestProperty("Connection", "close");
//
//            // ??????????????????:
//            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//            // ??????????????????????????????415??????
//            //conn.setRequestProperty("accept","*/*")?????????????????????????????????????????????????????????????????????415;
//            conn.setRequestProperty("accept", "application/json");
//            // ??????????????????????????????
//            if (json != null && !TextUtils.isEmpty(json)) {
//                byte[] writebytes = json.getBytes();
//                // ??????????????????
//                conn.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
//                OutputStream outwritestream = conn.getOutputStream();
//                outwritestream.write(json.getBytes());
//                outwritestream.flush();
//                outwritestream.close();
//                Log.d("hlhupload", "doJsonPost: conn" + conn.getResponseCode());
//            }
//            if (conn.getResponseCode() == 200) {
//                reader = new BufferedReader(
//                        new InputStreamReader(conn.getInputStream()));
//                result = reader.readLine();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        return result;
    }

}
