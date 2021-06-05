package com.ng.anthony.mininavigationdrawer.Sensor;

import android.app.Activity;
import android.content.Intent;
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
import java.util.Map;

public class BleMagCollector implements Runnable {

    String tCode;
    String mapIndex;
    String deviceId;
    int count;
    private static float X;
    private static float Y;

    public BleMagCollector(String tcode, String mapIdx, String deviceid){
        tCode = tcode;
        mapIndex = mapIdx;
        deviceId = deviceid;
        count = 0;
        Log.d("BleMagCollector", "BleMagCollector initial. tCode:" + tCode);
        Log.d("BleMagCollector", "BleMagCollector initial. mapIndex:" + mapIndex);
        Log.d("BleMagCollector", "BleMagCollector initial. deviceId:" + deviceId);
    }

    @Override
    public void run() {
        String bledata = BleMagData.getBleDataStr();
        String magdata = BleMagData.getAllDataStr();//每秒将magneticSensorData内的数据发送，并清空（传感器持续往magneticSensorData添加）
        String accdata = BleMagData.getAccDataStr();
        String orientdata = BleMagData.getOrientDataStr();
        if("".equals(magdata)){
            accdata = "";
            orientdata = "";
        }
        Log.d("BleMagCollector","bledata "+ bledata);
        Log.d("BleMagCollector","magdata "+ magdata);
        Log.d("BleMagCollector","accdata "+ accdata);
        Log.d("BleMagCollector","orientdata "+ orientdata);
        if("".equals(bledata)) return;

        try {
            count++;
            String result =  HttpHelper.sendJsonPost(magdata,bledata,accdata,orientdata,"BleMag",0,count);
            if(result == null || "".equals(result)){
                Log.d("BleMagCollector","无效结果");
                return;
            }
            Log.d("BleMagCollector","result "+ result);
            Map maps = (Map) JSON.parse(result);
            String status = maps.get("status").toString();
            if(status!=null && ("1").equals(status)){
                String TermLoc = maps.get("TermLoc").toString();
                TermLoc = TermLoc.substring(1,TermLoc.length()-1);
                String[] str = TermLoc.split(",");
                X =Float.parseFloat(str[0]);
                Y =Float.parseFloat(str[1]);
                BleMagData.setLocationResult(X,Y);
                Log.d("BleMagCollector","XY: " +  X+" "+Y);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
}
