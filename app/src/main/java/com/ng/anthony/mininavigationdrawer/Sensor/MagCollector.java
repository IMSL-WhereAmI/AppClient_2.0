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

public class MagCollector implements Runnable {

    String tCode;
    String mapIndex;
    String deviceId;
    int count;
    private static float X;
    private static float Y;

    public MagCollector(String tcode, String mapIdx, String deviceid){
        tCode = tcode;
        mapIndex = mapIdx;
        deviceId = deviceid;
        count = 0;

    }

    @Override
    public void run() {
        String bledata = BleMagData.getBleDataStr();
        String magdata = BleMagData.getAllDataStr();//每秒将magneticSensorData内的数据发送，并清空（传感器持续往magneticSensorData添加）
        String accdata = BleMagData.getAccDataStr();
        String acctemp = BleMagData.getAcctempDataStr();
        String grivaty = BleMagData.getgrivatyDataStr();
        String orientdata = BleMagData.getOrientDataStr();
        if("".equals(magdata)){
            accdata = "";
            orientdata = "";
        }
//        Log.d("BleMagCollector","bledata "+ bledata);
//        Log.d("BleMagCollector","magdata "+ magdata);
//        Log.d("BleMagCollector","accdata "+ accdata);
//        Log.d("BleMagCollector","orientdata "+ orientdata);
        if("".equals(bledata)) return;

        try {
            count++;
            String result = HttpHelper.sendJsonPost(magdata,bledata,accdata,orientdata,acctemp,grivaty,"mag",0,count,mapIndex,deviceId);
            if(result == null || "".equals(result)){
                Log.d("MagCollector","无效结果");
                return;
            }
            //Log.d("BleMagCollector","result "+ result);
            Map maps = (Map) JSON.parse(result);
            String status = maps.get("status").toString();
            String turn_state = maps.get("turn").toString();
            if(("1").equals(turn_state)){
                count = 0;
            }
            String loc_state = maps.get("Fusion").toString();
            Log.d("fusion",loc_state.toString());
            if(("0").equals(loc_state)){
                BleMagData.setLocationstate(0);
            }else if(("1").equals(loc_state)){
                BleMagData.setLocationstate(1);
            }else if(("2").equals(loc_state)){
                BleMagData.setLocationstate(2);
            }else if(("3").equals(loc_state)){
                BleMagData.setLocationstate(3);
            }else if(("4").equals(loc_state)){
                BleMagData.setLocationstate(4);
            }

            if(status!=null && ("1").equals(status)){
                String TermLoc = maps.get("TermLoc").toString();
                TermLoc = TermLoc.substring(1,TermLoc.length()-1);
                String[] str = TermLoc.split(",");
                X =Float.parseFloat(str[0]);
                Y =Float.parseFloat(str[1]);
                BleMagData.setLocationResult(X,Y);
                Log.d("MagCollector","XY: " +  X+" "+Y);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
}
