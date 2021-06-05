package com.ng.anthony.mininavigationdrawer.Sensor;


import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BleMagData {
    public static List<String> timestamps = new ArrayList<String>();
    public static List<String[]> magneticSensorData = new ArrayList<>();
    public static List<String[]> accelerometerSensorData = new ArrayList<String[]>();
    public static List<String[]> orientationSensorData = new ArrayList<String[]>();
    public static List<String[]> stepSensorData = new ArrayList<String[]>();
    public static List<String[]> gyroscopeSensorData = new ArrayList<String[]>();
    public static List<String> accnormlist = new ArrayList<String>();
    public static String stepCount;
    public static String prestepCount;
    public static Integer count = 0;
    private static double[] locationResult = {0,0};

//    private static String[] BLE_NAMES = new String[]{"A207-01","A207-02","A207-03","A207-04","A207-05","A207-06","A207-07","A207-08", "A207-09","A207-10",
//            "A207-11","A207-12","A207-13","A207-14","A207-15","A207-16","A207-17","A207-18", "A207-19","A207-20",
//            "A207-21","A207-22","A207-23","A207-24","A207-25","A207-26","A207-27","A207-28", "A207-29","A207-30",
//            "A207-31","A207-32","A207-33","A207-34","A207-35","A207-36","A207-37","A207-38"};
//    private static int[] ble_rssi = new int[BLE_NAMES.length];

    //停车场
//    private static String[] BLE_MACS2 = new String[]{"80:EC:CA:CD:05:CB", "80:EC:CA:CD:05:C0",
//                                                        "80:EC:CA:CD:05:C5", "80:EC:CA:CD:06:1D",
//                                                        "80:EC:CA:CD:05:C7", "80:EC:CA:CD:05:C4",
//                                                        "80:EC:CA:CD:05:E6", "80:EC:CA:CD:05:ED",
//                                                        "80:EC:CA:CD:05:D5", "80:EC:CA:CD:05:C9",
//                                                        "80:EC:CA:CD:06:28", "80:EC:CA:CD:06:20",
//                                                        "80:EC:CA:CD:05:C2", "80:EC:CA:CD:05:D2",
//                                                        "80:EC:CA:CD:05:C1", "80:EC:CA:CD:05:E8",
//                                                        "80:EC:CA:CD:05:EC", "80:EC:CA:CD:05:DD",
//                                                        "80:EC:CA:CD:06:23", "80:EC:CA:CD:05:D4",
//                                                        "80:EC:CA:CD:05:C3", "80:EC:CA:CD:06:1E",
//                                                        "80:EC:CA:CD:05:E4", "80:EC:CA:CD:05:E5",
//                                                        "80:EC:CA:CD:06:24", "80:EC:CA:CD:06:21",
//                                                        "80:EC:CA:CD:05:E0", "80:EC:CA:CD:05:D6",
//                                                        "80:EC:CA:CD:05:CA", "80:EC:CA:CD:06:26",
//                                                        "80:EC:CA:CD:05:EF", "80:EC:CA:CD:05:EE",
//                                                        "80:EC:CA:CD:05:CC", "80:EC:CA:CD:05:D3",
//                                                        "80:EC:CA:CD:06:1F", "80:EC:CA:CD:06:27",
//                                                        "80:EC:CA:CD:05:E9", "80:EC:CA:CD:05:EB",
//                                                        "80:EC:CA:CD:06:25", "80:EC:CA:CD:05:E7"};

    private static String[] BLE_MACS2 = new String[]{"80:EC:CA:CD:12:9B", "80:EC:CA:CD:12:AF",
            "80:EC:CA:CD:11:F0", "80:EC:CA:CD:12:53",
            "80:EC:CA:CD:12:AB", "80:EC:CA:CD:12:5F",
            "80:EC:CA:CD:12:6B", "80:EC:CA:CD:12:8E",
            "80:EC:CA:CD:12:46", "80:EC:CA:CD:12:41",
            "80:EC:CA:CD:12:5D", "80:EC:CA:CD:12:AC",
            "80:EC:CA:CD:12:82", "80:EC:CA:CD:12:48",
            "80:EC:CA:CD:12:91", "80:EC:CA:CD:12:72",
            "80:EC:CA:CD:11:E5", "80:EC:CA:CD:12:9D",
            "80:EC:CA:CD:12:3A", "80:EC:CA:CD:12:74",
            "80:EC:CA:CD:12:8D", "80:EC:CA:CD:12:3F",
            "80:EC:CA:CD:12:80", "80:EC:CA:CD:12:0B",
            "80:EC:CA:CD:11:B2", "80:EC:CA:CD:11:E6",};

    private static int[] ble_rssi = new int[BLE_MACS2.length];

    private static void reset(int[] rssi){
        for(int i = 0; i < rssi.length; i++)
            rssi[i] = -100;
    }


    public static void acc_clear(){
        if(accelerometerSensorData.size()!=0){
            accelerometerSensorData.clear();
        }
    }
    public static void orient_clear(){
        if(orientationSensorData.size()!=0){
            orientationSensorData.clear();
        }
    }

    public static void mag_clear(){
        if(magneticSensorData.size()!=0){
            magneticSensorData.clear();
        }
        if(accnormlist.size()!=0){
            accnormlist.clear();
        }

    }

    public static void ble_clear(){
        reset(ble_rssi);
    }

    public static void addBleData(int[] BleData){
        System.arraycopy(BleData,0,ble_rssi,0,BLE_MACS2.length);
    }


    public static void addSensorData(String[] mData, String[] aData, String[] oData,
                                     String gData[], String stepcount,String accnorm, String captime){
        magneticSensorData.add(mData);
        accelerometerSensorData.add(aData);
        orientationSensorData.add(oData);
        stepCount = stepcount;
        accnormlist.add(accnorm);
        //System.out.println("addSensorData: "+aData[0]+" "+aData[1]+" "+aData[2]);
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
        ble_clear();
        return data;
    }

    public static String getAllDataStr(){//那地磁数据，运动静止状态只影响mag数据
        String data = "";
        Log.d("SensorData","magneticSensorData.size(): "+ magneticSensorData.size());
        if(magneticSensorData.size() == 0) return "";
        if(!isStationary()){//只有运动的时候才发mag数据，否则发送数据为空
            Log.d("SensorData","运动发送数据");
            for(int i = 0 ; i < magneticSensorData.size() ; i++){
                String[] mag = magneticSensorData.get(i);
                String one_detail = mag[0] + "," + mag[1] + "," + mag[2]+",";

                data = data + one_detail;
            }
            data = data.substring(0,data.length()-1);
        }else{
            Log.d("SensorData","静止该时间段数据清空");
        }
        mag_clear();
        return data;
    }

    public static String getOrientDataStr(){
        String data = "";
        //System.out.println("orient:"+orientationSensorData);
        if(orientationSensorData.size() == 0) return "";
        for(int i = 0 ; i < orientationSensorData.size() ; i++){
            String[] orien = orientationSensorData.get(i);
            String one_detail = orien[0]+",";
            data = data + one_detail;
        }
        data = data.substring(0,data.length()-1);
        orient_clear();
        return data;
    }

    public static String getAccDataStr(){
        String data = "";
        if(accelerometerSensorData.size() == 0) return "";
        for(int i = 0 ; i < accelerometerSensorData.size() ; i++){
            String[] acc = accelerometerSensorData.get(i);
            String one_detail = acc[0] + "," + acc[1] + "," + acc[2]+",";
            data = data + one_detail;
        }
        data = data.substring(0,data.length()-1);
        acc_clear();
        return data;
    }

    public static String null2zero(String item){
        if(item == null || item.equals(""))
            return "0";
        return item;
    }

    public static void setLocationResult(float X, float Y){
//        locationResult[0] = X;
//        locationResult[1] = Y;
        float[] res=DataConstrain.BoxConstrain(X,Y);
        locationResult[0] = res[0];
        locationResult[1] = res[1];
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

