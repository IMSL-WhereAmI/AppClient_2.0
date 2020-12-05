package com.ng.anthony.mininavigationdrawer.Sensor;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by justk on 2018/6/13.
 */

public class SensorListener implements SensorEventListener{

    private static int[] SYNC = {0,0,0,0,0,0};
    private String[] accelerometerData;
    private String[] magneticData ;
    private String[] orientationData ;
    private String[] gyroscopeData;
    private String accnorm;
    private String stepCounterData = "0";
    private String stepDetectorData = "0";
    private String stepCount;
    private String temp;


    @Override
    public void onSensorChanged(SensorEvent event){
        switch(event.sensor.getType()){
            case Sensor.TYPE_GYROSCOPE:{
                gyroscopeData = new String[3];
                gyroscopeData[0] = ""+event.values[0];
                gyroscopeData[1] = ""+event.values[1];
                gyroscopeData[2] = ""+event.values[2];
                SYNC[0] = 1;
//                Log.d("capsensordata_g", accelerometerData[1]);
                break;
            }
            case Sensor.TYPE_ACCELEROMETER:{
                accelerometerData = new String[3];
                accelerometerData[0] = ""+event.values[0];
                accelerometerData[1] = ""+event.values[1];
                accelerometerData[2] = ""+event.values[2];
                //Log.d("acc", accelerometerData[0]+" "+accelerometerData[1]+" "+accelerometerData[2]);
                temp = String.valueOf(event.values[0]*event.values[0] + event.values[1]*event.values[1] + event.values[2]*event.values[2]) ;
                accnorm = String.valueOf(Math.sqrt(Double.parseDouble(temp)));
                //Log.d("acc", accnorm);
                SYNC[1] = 1;
//              Log.d("capsensordata_a", accelerometerData[1]);
                break;
            }
            case Sensor.TYPE_ORIENTATION:{
                orientationData = new String[3];
                orientationData[0] = ""+event.values[0];
                orientationData[1] = ""+event.values[1];
                orientationData[2] = ""+event.values[2];
                SYNC[2] = 1;
//                Log.d("capsensordata_o", orientationData[1]);
                break;
            }
            case Sensor.TYPE_MAGNETIC_FIELD:{
                magneticData = new String[3];
                magneticData[0] = ""+event.values[0];
                magneticData[1] = ""+event.values[1];
                magneticData[2] = ""+event.values[2];
                //System.out.println("地磁 "+magneticData);
                SYNC[3] = 1;
//                Log.d("capsensordata_m", magneticData[1]);
                break;
            }
            case Sensor.TYPE_STEP_COUNTER:{
                //stepCounterData = ""+event.values[0];
                stepCount = ""+event.values[0];
                System.out.println("步数 "+stepCount);
//                Log.d("capsensordata_s1", stepCounterData);
                SYNC[4] = 1;
                break;
            }
            case Sensor.TYPE_STEP_DETECTOR:{
                stepDetectorData = ""+event.values[0];
//                Log.d("capsensordata_s2", stepCounterData);
                SYNC[5] = 1;
                break;
            }
            default:
                break;
        }

        if(sync()) {
            String captime = getCurrentTime();
            //Log.d("SensorListener",accelerometerData[0]+ " "+accelerometerData[1]+ " "+accelerometerData[2]+ " " +stepCount);
            //String[] stepData = new String[3];
            //stepData[1] = stepCounterData;
            //stepData[0] = stepDetectorData;
            //stepData[2] = stepCount;
//            Log.d("TAG", "addSensorData: "+magneticData[0]);
//            System.out.println("addSensorData: "+magneticData[0]+" "+magneticData[1]+" "+magneticData[2]);
            SensorData.addSensorData(magneticData, accelerometerData, orientationData,
                    gyroscopeData, stepCount, accnorm, captime);
            reset();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public boolean sync(){
//        for(int i = 0 ; i < 4 ; i++){
//            if(SYNC[i] == 0)
//                return false;
//        }

        if(SYNC[3] == 1&&SYNC[1] == 1){
            //Log.d("SensorListener", "Sensor collect success！");
            return true;
        }
        else
            return false;
    }

    public void reset(){
        for(int i = 0 ; i < SYNC.length ; i++){
            SYNC[i] = 0;
        }
    }

    public String getCurrentTime(){
        return new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
    }

}
