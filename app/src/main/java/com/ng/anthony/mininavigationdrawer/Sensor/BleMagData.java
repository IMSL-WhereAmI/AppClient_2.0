package com.ng.anthony.mininavigationdrawer.Sensor;


import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BleMagData {
    public static List<String[]> magneticSensorData = new ArrayList<>();
    public static List<String[]> accelerometerSensorData = new ArrayList<String[]>();
    public static List<String> accnormlist = new ArrayList<String>();
    public static String stepCount;
    private static double[] locationResult = {0,0};

    private static String[] BLE_NAMES = new String[]{"A207-01","A207-02","A207-03","A207-04","A207-05","A207-06","A207-07","A207-08"};
    private static int[] ble_rssi = new int[BLE_NAMES.length];
    private static void reset(int[] rssi){
        for(int i = 0; i < rssi.length; i++)
            rssi[i] = -100;
    }


    public static void clear(){
        if(magneticSensorData.size()!=0){
            magneticSensorData.clear();
        }
        if(accnormlist.size()!=0){
            accnormlist.clear();
        }
    }

    public static void bleclear(){
        reset(ble_rssi);
    }

    public static void addBleData(int[] BleData){
        System.arraycopy(BleData,0,ble_rssi,0,BLE_NAMES.length);
    }

    public static void addSensorData(String[] mData, String[] aData, String[] oData,
                                     String gData[], String stepcount,String accnorm, String captime){
        magneticSensorData.add(mData);
        accelerometerSensorData.add(aData);
        stepCount = stepcount;
        accnormlist.add(accnorm);
    }


    public static double Variance(List<String> x){
        int m = x.size();
        int i;
        double sum = 0, dAve, dVar;
        for(i = 0; i < m;i++){//求和
            sum += Double.parseDouble(x.get(i));
        }
        dAve = sum/m;//求平均值
        dVar = 0;
        for(i = 0; i < m;i++){//求方差
            dVar += (( Double.parseDouble(x.get(i)) - dAve)*(Double.parseDouble(x.get(i)) - dAve));
        }
        dVar = dVar/m;
        return dVar;
    }

    public static boolean isStationary(){
        if(Variance(accnormlist) <= 0.1){
            //静止
            return true;
        }else{
            //运动
            return false;
        }
    }

    public static String getBleDataStr(){//拿蓝牙数据
        String data = Arrays.toString(ble_rssi);
        bleclear();
        return data;
    }

    public static String getAllDataStr(){//拿地磁数据
        String data = "";
        Log.d("BleMagData","magneticSensorData.size(): "+ magneticSensorData.size());
        Log.d("BleMagData","accnormvar: "+ Variance(accnormlist));
        if(magneticSensorData.size() == 0) return "";
        if(!isStationary()){//只有运动的时候才发mag数据，否则发送数据为空
            Log.d("BleMagData","运动发送数据");
            for(int i = 0 ; i < magneticSensorData.size() ; i++){
                String[] mag = magneticSensorData.get(i);
                String one_detail = mag[0] + "," + mag[1] + "," + mag[2]+",";
                data = data + one_detail;
            }
            data = data.substring(0,data.length()-1);
        }else{
            Log.d("BleMagData","静止该时间段数据清空");
        }
        clear();
        return data;
    }

    public static String null2zero(String item){
        if(item == null || item.equals(""))
            return "0";
        return item;
    }

    public static void setLocationResult(float X, float Y){
        float[] res=DataConstrain.BoxConstrain(X,Y);
        if(true){
            locationResult[0] = res[0];
            locationResult[1] = res[1];
        }
    }

    public static double[] getLocationResult(){
        return locationResult;
    }

    public void reset(){
        for(int i = 0 ; i < locationResult.length ; i++){
            locationResult[i] = 0;
        }
    }


}

