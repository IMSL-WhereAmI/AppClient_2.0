package com.ng.anthony.mininavigationdrawer.Sensor;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by justk on 2018/6/13.
 */

public class SensorData {
    public static List<String> timestamps = new ArrayList<String>();
    public static List<String[]> magneticSensorData = new ArrayList<String[]>();
    public static List<String[]> accelerometerSensorData = new ArrayList<String[]>();
    public static List<String[]> orientationSensorData = new ArrayList<String[]>();
    public static List<String[]> stepSensorData = new ArrayList<String[]>();
    public static List<String[]> gyroscopeSensorData = new ArrayList<String[]>();
    private static double[] locationResult = {0,0};


    public static void clear(){
        timestamps.clear();
        magneticSensorData.clear();
        accelerometerSensorData.clear();
        orientationSensorData.clear();
        stepSensorData.clear();
        gyroscopeSensorData.clear();

    }


    public static void addSensorData(String[] mData, String[] aData, String[] oData,
                                     String gData[], String[] sData, String captime){
        magneticSensorData.add(mData);
        accelerometerSensorData.add(aData);
//        orientationSensorData.add(oData);
//        stepSensorData.add(sData);
//        gyroscopeSensorData.add(gData);
//        timestamps.add(captime);
//        System.out.println("addSensorData: "+mData[0]+" "+mData[1]+" "+mData[2]);
    }

    public static String getFileHead(){
        return  "frame,mag_x,mag_y,mag_z,acc_x,acc_y,acc_z,gyro_x,gyro_y,gyro_z," +
                "orien_x,orien_y,orien_z,step_detect,step_count,timestamp\n";
    }
    public static boolean isStationary(){

        String[] acc0 = accelerometerSensorData.get(accelerometerSensorData.size() - 2);
        String[] acc1 = accelerometerSensorData.get(accelerometerSensorData.size() - 1);
        float x0 = Float.parseFloat(acc0[0]);
        float y0 = Float.parseFloat(acc0[1]);
        float z0 = Float.parseFloat(acc0[2]);
        float x1 = Float.parseFloat(acc1[0]);
        float y1 = Float.parseFloat(acc1[1]);
        float z1 = Float.parseFloat(acc1[2]);

        //Log.d("isStationary",  x1+" "+y1+" "+z1);
        //Log.d("chazhi",  Math.abs(x1-x0)+" "+Math.abs(y1-y0)+" ");
        if(Math.abs(x1-x0)> 0.05 || Math.abs(y1-y0) > 0.05 ) {
            return false;
        }else{
            return true;
        }
    }
    public static String getAllDataStr(){
        String data = "";
        Log.d("SensorData","magneticSensorData.size(): "+magneticSensorData.size());
        if(!isStationary()){//只有运动的时候才发mag数据，否则发送数据为空，
            for(int i = 0 ; i < magneticSensorData.size() ; i++){
                String[] mag = magneticSensorData.get(i);
                String one_detail = mag[0] + "," + mag[1] + "," + mag[2];
                if(i < magneticSensorData.size()-1)
                    one_detail = one_detail + ",";
                data = data + one_detail;
//            String[] mag = magneticSensorData.get(i);
//            String[] gyro = gyroscopeSensorData.get(i);
//            String[] orien = orientationSensorData.get(i);
//            String[] step = stepSensorData.get(i);
//            String[] acc = accelerometerSensorData.get(i);
//            String one_detail = "" + (i+1) + ","
//                    + mag[0] + "," + mag[1] + "," + mag[2] + ","
//                    + acc[0] + "," + acc[1] + "," + acc[2] + ","
//                    + gyro[0] + "," + gyro[1] + "," + gyro[2] + ","
//                    + orien[0] + "," + orien[1] + "," + orien[2] + ","
//                    + null2zero(step[0]) + "," + step[1] + "," + timestamps.get(i) + "\n" ;
//            data = data + one_detail;
            }
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
        if(!isStationary()){
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

