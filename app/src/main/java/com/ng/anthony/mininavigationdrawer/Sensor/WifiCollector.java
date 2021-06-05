package com.ng.anthony.mininavigationdrawer.Sensor;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.net.wifi.ScanResult;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSON;
import com.ng.anthony.mininavigationdrawer.Http.HttpHelper;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WifiCollector implements Runnable {

    private WifiManager wifiManager;
    String tCode;
    String mapIndex;
    String deviceId;
    Handler mHandler;
    private float X;
    private float Y;

    List<ScanResult> scanResult;

    List<String> ap_Name = new ArrayList<>();
    List<String> mac_Name = new ArrayList<>();


    public WifiCollector(Context context,WifiManager wm, String tcode, String mapIdx, String deviceid){
        wifiManager = wm;
        tCode = tcode;
        mapIndex = mapIdx;
        deviceId = deviceid;

        Log.d("WifiCollector", "WifiCollector initial. tCode:" + tCode);
        Log.d("WifiCollector", "WifiCollector initial. mapIndex:" + mapIndex);
        Log.d("WifiCollector", "WifiCollector initial. deviceId:" + deviceId);

        try {
            InputStream is = context.getResources().getAssets().open("AP_MAC_FOR_LOC.txt");
            BufferedReader bf=new BufferedReader(new InputStreamReader(is));
            String str;
            // 按行读取字符串
            while ((str = bf.readLine()) != null) {
                mac_Name.add(str);
            }
            bf.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        wifiManager.startScan();
        scanResult = wifiManager.getScanResults();
        //Log.d("TAG", "run: scan wifi, scan number:" +scanResult.size());
        List<String> wdlist = new ArrayList<String>();

        //Log.d("TAG", "scannumber:" +scanResult);
        //Log.d("TAG", "macname" +mac_Name);
        int wifinotnull = 0;
        for (String value:mac_Name) {//按序
            for(ScanResult sc : scanResult){
                if(sc.BSSID.equals(value)){
                    wdlist.add( Integer.toString(sc.level));
                    wifinotnull = 1;
                    break;
                }
            }
            if(wifinotnull == 0){
                wdlist.add( Integer.toString(-100));
            }
            wifinotnull = 0;
        }
        //Log.d("TAG", "wdlist:" +wdlist);
        String data = "";
        for(int i = 0;i < wdlist.size();i++){
            String one_detail = wdlist.get(i);
            data = data + one_detail;
            if(i < wdlist.size()-1)
                data = data + ",";
        }
        Log.d("Wifidata","data: " +  data);

        try {
            String result =  HttpHelper.sendJsonPost("",data,"","","wifi", wdlist.size(),0);
            //Log.d("WifiCollector","result: " +  result);
            Map maps = (Map) JSON.parse(result);
            String status = maps.get("status").toString();

            if(status!=null && ("1").equals(status)){
                String TermLoc = maps.get("TermLoc").toString();
                TermLoc = TermLoc.substring(1,TermLoc.length()-1);
                String[] str = TermLoc.split(",");
                X =Float.parseFloat(str[0]);
                Y =Float.parseFloat(str[1]);
                Log.d("WifiCollector","XY: " +  X+" "+Y);
                WifiData.setLocationResult(X,Y);
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
//        try {
//            String d =  HttpHelper.sendJsonPost(data,"wifi");
//            System.out.println(d);
//        } catch (JSONException | IOException e) {
//            e.printStackTrace();
//        }
//        if(X < 1200){
//            X = X+60;
//            Random rnd = new Random();
//            float flag1 = rnd.nextFloat();
//            float code1 =  flag1 > 0.5 ? (float) (5.0 * rnd.nextFloat()) : (float) (-5.0 * rnd.nextFloat());
//            float flag2 = rnd.nextFloat();
//            float code2 =  flag2 > 0.5 ? (float) (6.0 * rnd.nextFloat()): (float) (-6.0 * rnd.nextFloat());
//            X = X + code1;
//            Y = Y + code2;
//        }else{
//            Y = Y+45;
//            Random rnd = new Random();
//            float flag1 = rnd.nextFloat();
//            float code1 =  flag1 > 0.5 ? (float) (8.0 * rnd.nextFloat()) : (float) (-8.0 * rnd.nextFloat());
//            float flag2 = rnd.nextFloat();
//            float code2 =  flag2 > 0.5 ? (float) (6.0 * rnd.nextFloat()): (float) (-6.0 * rnd.nextFloat());
//            X = X + code1;
//            Y = Y + code2;
//        }
//        WifiData.setLocationResult(X,Y);
    }
}
