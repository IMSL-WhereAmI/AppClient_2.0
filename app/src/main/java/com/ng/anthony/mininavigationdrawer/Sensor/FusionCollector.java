package com.ng.anthony.mininavigationdrawer.Sensor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.ng.anthony.mininavigationdrawer.Http.HttpHelper;
import com.ng.anthony.mininavigationdrawer.MainActivity;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FusionCollector implements Runnable {

    String tCode;
    String mapIndex;
    String deviceId;
    //Boolean flag;
    Integer count;

    List<String> mac_Name = new ArrayList<>();
    List<ScanResult> scanResult;

    Handler mHandler;
    private static float X;
    private static float Y;
    private WifiManager wifiManager;

    public FusionCollector(Context context, WifiManager wm, String tcode, String mapIdx, String deviceid){
        wifiManager = wm;
        tCode = tcode;
        mapIndex = mapIdx;
        deviceId = deviceid;
        count = 0;

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

        Log.d("FusionCollctor", "SensorCollector initial. tCode:" + tCode);
        Log.d("FusionCollctor", "SensorCollector initial. mapIndex:" + mapIndex);
        Log.d("FusionCollctor", "SensorCollector initial. deviceId:" + deviceId);
    }

    @Override
    public void run() {
        String magdata = SensorData.getAllDataStr();//每秒将magneticSensorData内的数据发送，并清空（传感器持续往magneticSensorData添加）
        Log.d("FusionCollctor","magdata: "+ magdata);
        if("".equals(magdata)) return;

        wifiManager.startScan();
        scanResult = wifiManager.getScanResults();
        List<String> wdlist = new ArrayList<String>();

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

        String wifidata = "";
        for(int i = 0;i < wdlist.size();i++){
            String one_detail = wdlist.get(i);
            wifidata = wifidata + one_detail + ",";
        }
        wifidata = wifidata.substring(0, wifidata.length()-1);
        Log.d("FusionCollctor","wifidata: "+ wifidata);


        try {
            count++;
            String result =  HttpHelper.sendJsonPost(magdata, wifidata,"","","","","fusion",wdlist.size(),count,mapIndex,deviceId);
            Log.d("FusionCollector","count: " +  count);
            Log.d("FusionCollector","result: " +  result);
            if(result.equals("{}")) return;
            Map maps = (Map)JSON.parse(result);
            String status = maps.get("status").toString();
            if(status!=null && ("1").equals(status)){
                String TermLoc = maps.get("TermLoc").toString();
                TermLoc = TermLoc.substring(1,TermLoc.length()-1);
                String[] str = TermLoc.split(",");
                X =Float.parseFloat(str[0]);
                Y =Float.parseFloat(str[1]);
                SensorData.setLocationResult(X,Y);
                Log.d("FusionCollector","XY: " +  X+" "+Y);
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();

        }


    }

}
