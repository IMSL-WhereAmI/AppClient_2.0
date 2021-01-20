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
        Log.d("BleMagCollector", "BleMagCollector initial. tCode:" + tCode);
        Log.d("BleMagCollector", "BleMagCollector initial. mapIndex:" + mapIndex);
        Log.d("BleMagCollector", "BleMagCollector initial. deviceId:" + deviceId);
    }

    @Override
    public void run() {
        String Bledata = BleMagData.getBleDataStr();
        String Magdata = BleMagData.getAllDataStr();
        Log.d("BleMagCollector","Bledata "+ Bledata);
        Log.d("BleMagCollector","Magdata "+ Magdata);
        try {
            count++;
            String result =  HttpHelper.sendJsonPost(Magdata,Bledata,"BleMag",0,count);
            Log.d("BleMagCollector","result "+ result);
            Map maps = (Map)JSON.parse(result);
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
