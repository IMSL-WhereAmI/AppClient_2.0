package com.ng.anthony.mininavigationdrawer.Sensor;

import android.app.Activity;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.ng.anthony.mininavigationdrawer.Http.HttpHelper;
import com.ng.anthony.mininavigationdrawer.MainActivity;

import org.json.JSONException;

import java.io.IOException;

import java.math.BigDecimal;
import java.util.Map;

public class SensorCollector implements Runnable {

    String tCode;
    String mapIndex;
    String deviceId;
    Boolean flag;
    Integer count;
    private static float X;
    private static float Y;

    public SensorCollector(String tcode, String mapIdx, String deviceid){
        tCode = tcode;
        mapIndex = mapIdx;
        deviceId = deviceid;
        flag = false;
        count = 0;
        Log.d("SensorCollector", "SensorCollector initial. tCode:" + tCode);
        Log.d("SensorCollector", "SensorCollector initial. mapIndex:" + mapIndex);
        Log.d("SensorCollector", "SensorCollector initial. deviceId:" + deviceId);
    }

    @Override
    public void run() {
        Log.d("SensorCollector","run ");
        String magdata = SensorData.getAllDataStr();//每秒将magneticSensorData内的数据发送，并清空（传感器持续往magneticSensorData添加）
        String accdata = SensorData.getAccDataStr();
        String orientdata = SensorData.getOrientDataStr();
        //Log.d("SensorCollector","magdata "+ magdata);
//        Log.d("SensorCollector","accdata "+ accdata);
//        Log.d("SensorCollector","orientdata "+ orientdata);
        if("".equals(accdata)||"".equals(orientdata)) return;
        try {
            count++;
            String result =  HttpHelper.sendJsonPost(magdata,"",accdata,orientdata,"","","mag",0,count,mapIndex,deviceId);
            Log.d("SensorCollector","result "+ result);
            Map maps = (Map) JSON.parse(result);
            String status = maps.get("status").toString();
            if(status!=null && ("1").equals(status)){
                String TermLoc = maps.get("TermLoc").toString();
                TermLoc = TermLoc.substring(1,TermLoc.length()-1);
                String[] str = TermLoc.split(",");
                X =Float.parseFloat(str[0]);
                Y =Float.parseFloat(str[1]);
                SensorData.setLocationResult(X,Y);
                Log.d("Collector","XY: " +  X+" "+Y);
                flag = false;
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
            flag = false;
        }
    }
}
