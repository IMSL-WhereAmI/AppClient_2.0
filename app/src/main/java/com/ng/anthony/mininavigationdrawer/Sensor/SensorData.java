package com.ng.anthony.mininavigationdrawer.Sensor;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by justk on 2018/6/13.
 */

public class SensorData {
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
        /*timestamps.clear();
        magneticSensorData.clear();
        accelerometerSensorData.clear();
        orientationSensorData.clear();
        stepSensorData.clear();
        gyroscopeSensorData.clear();
*/
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

    public static String getFileHead(){
        return  "frame,mag_x,mag_y,mag_z,acc_x,acc_y,acc_z,gyro_x,gyro_y,gyro_z," +
                "orien_x,orien_y,orien_z,step_detect,step_count,timestamp\n";
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

/*
        if(count == 0){
            prestepCount = stepCount;
            return true;//开始默认静止
        }else{
            count++;
            if((Float.parseFloat(stepCount) - Float.parseFloat(prestepCount)) == 0){
                prestepCount = stepCount;
                return true;
            }else {
                prestepCount = stepCount;
                return false;
            }
        }

*/

    }

    public static String getAllDataStr(){//运动静止状态只影响mag数据，wifi数据照常发送
        String data = "";
        Log.d("SensorData","magneticSensorData.size(): "+ magneticSensorData.size());
        //Log.d("SensorData","accnormlist.size(): "+ accnormlist.size());
        Log.d("SensorData","accnormvar: "+ Variance(accnormlist));
        //if(Variance(accnormlist)<=0.1) Log.d("SensorData","静止");
        //else Log.d("SensorData","运动");
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
        //float[] res=DataConstrain.BoxConstrain(X,Y);
        if(true){
            locationResult[0] = X;
            locationResult[1] = Y;
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

