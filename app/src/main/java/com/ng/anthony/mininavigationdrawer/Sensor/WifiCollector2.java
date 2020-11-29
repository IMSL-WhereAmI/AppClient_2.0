package com.ng.anthony.mininavigationdrawer.Sensor;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

//offline_wifi_match
public class WifiCollector2 implements Runnable {

    private WifiManager wifiManager;
    String tCode;
    String mapIndex;
    String deviceId;
    Handler mHandler;
    private float X;
    private float Y;
    List<String> wifiLable;
    List<List> wifiData;
    List<String> testData = new ArrayList<>();

    List<ScanResult> scanResult;

    //List<String> ap_Name = new ArrayList<>();
    List<String> mac_Name = new ArrayList<>();


    public WifiCollector2(Context context, WifiManager wm, String tcode, String mapIdx, String deviceid, List<String>wifilable, List<List>wifidata){
        wifiManager = wm;
        tCode = tcode;
        mapIndex = mapIdx;
        deviceId = deviceid;
        wifiData = wifidata;
        wifiLable = wifilable;

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

        testData = new ArrayList<>();
        for(int i = 0;i < wdlist.size();i++){
            String one_detail = wdlist.get(i);
            testData.add(one_detail);
        }
        Log.d("testwifi", ""+testData);

        double mindist = 9999999;
        double v1, v2;
        double sum2norm = 0;
        int i, j, idx = 0;
        for (j = 0;j < wifiData.size();j++){
            sum2norm = 0;
            for (i = 0;i < testData.size();i++){
                v1 = Double.parseDouble(wifiData.get(j).get(i).toString());
                v2 = Double.parseDouble(testData.get(i));
                sum2norm += Math.pow(v1 - v2, 2);
            }
            sum2norm = Math.sqrt(sum2norm);
            if(sum2norm < mindist){
                mindist = sum2norm;
                idx = j;
            }
        }

        String TermLoc = wifiLable.get(idx);
        String[] str = TermLoc.split(",");
        X = Float.parseFloat(str[0]);
        Y = Float.parseFloat(str[1]);
        Log.d("WifiCollector","XY: " +  X+" "+Y);
        WifiData.setLocationResult(X,Y);


    }
}
