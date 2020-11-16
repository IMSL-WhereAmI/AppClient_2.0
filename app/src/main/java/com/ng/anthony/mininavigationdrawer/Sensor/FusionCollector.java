package com.ng.anthony.mininavigationdrawer.Sensor;

import android.app.Activity;
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

import java.io.IOException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class FusionCollector implements Runnable {

    String tCode;
    String mapIndex;
    String deviceId;
    Boolean flag;
    private WifiManager wifiManager;
    List<ScanResult> scanResult;
    Handler mHandler;
    private static float X;
    private static float Y;

    public FusionCollector(WifiManager wm,String tcode, String mapIdx, String deviceid){
        wifiManager = wm;
        tCode = tcode;
        mapIndex = mapIdx;
        deviceId = deviceid;
        flag = false;
        Log.d("FusionCollctor", "SensorCollector initial. tCode:" + tCode);
        Log.d("FusionCollctor", "SensorCollector initial. mapIndex:" + mapIndex);
        Log.d("FusionCollctor", "SensorCollector initial. deviceId:" + deviceId);
    }

    @Override
    public void run() {
        String magdata = SensorData.getAllDataStr();//每秒将magneticSensorData内的数据发送，并清空（传感器持续往magneticSensorData添加）
        Log.d("SensorCollctor","data"+magdata);
        if(magdata == null ||"".equals(magdata))
            return;

        wifiManager.startScan();
        scanResult = wifiManager.getScanResults();
        Log.d("TAG", "run: scan wifi, scan number:" +scanResult.size());
        float[] wdlist={0,0,0,0,0};
        for (ScanResult sc : scanResult) {
            if(sc.BSSID.toUpperCase().equals("4C:E9:E4:7A:4C:50")){
                wdlist[0]=sc.level;
            }
            if(sc.BSSID.toUpperCase().equals("4C:E9:E4:7A:4C:50")){
                wdlist[1]=sc.level;
            }
            if(sc.BSSID.toUpperCase().equals("4C:E9:E4:7A:4C:50")){
                wdlist[2]=sc.level;
            }
            if(sc.BSSID.toUpperCase().equals("4C:E9:E4:7A:4C:50")){
                wdlist[3]=sc.level;
            }
            if(sc.BSSID.toUpperCase().equals("4C:E9:E4:7A:4C:50")){
                wdlist[4]=sc.level;
            }
            //System.out.println(sc.level);
            //System.out.println(sc.SSID.toUpperCase());
        }
        String wifidata = wdlist[0]+","+wdlist[1]+","+wdlist[2]+","+wdlist[3]+","+wdlist[4];
        String data = wifidata+","+magdata;
        flag = true;

        try {
            String result =  HttpHelper.sendJsonPost(data,"fusion",0);
            Log.d("FusionCollector","result: " +  result);
            System.out.println(result);
            Map maps = (Map)JSON.parse(result);
            String status = maps.get("status").toString();
            if(status!=null && ("1").equals(status)){
                String TermLoc = maps.get("TermLoc").toString();
                TermLoc = TermLoc.substring(1,TermLoc.length()-1);
                String[] str = TermLoc.split(",");
                X =Float.parseFloat(str[0]);
                Y =Float.parseFloat(str[1]);
                //X=55;Y=55;
                SensorData.setLocationResult(X,Y);
                flag = false;
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
            flag = false;
        }
        //}
    }
}
