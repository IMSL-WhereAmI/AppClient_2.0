

package com.ng.anthony.mininavigationdrawer.Sensor;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.ng.anthony.mininavigationdrawer.MainActivity.GetSystemTime;


/**
 * Created by justk on 2018/6/13.
 */

public class SensorListener implements SensorEventListener{

    private static com.clj.blesample.FileUtil fileUtil;
    private static int[] SYNC = {0,0,0,0,0,0,0};
    private String[] accelerometerData;
    private String[] magneticData ;
    private String[] orientationData ;
    private String[] gyroscopeData;
    private String accnorm;
    private String stepCounterData = "0";
    private String stepDetectorData = "0";
    private String stepCount;
    private String temp;

    private float[] I = new float[9];
    private float[] r = new float[9];
    private float[] acceler = null;
    private float[] gravity = null;
    private float[] geomagnetic = null;
    private float[] orien = new float[3];
    private final float alpha = (float) 0.8;
    private float[] acc2gra = new float[3];

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
                acceler = event.values;

                //Isolate the force of gravity with the low-pass filter.
                acc2gra[0] = alpha * acc2gra[0] + (1 - alpha) * event.values[0];
                acc2gra[1] = alpha * acc2gra[1] + (1 - alpha) * event.values[1];
                acc2gra[2] = alpha * acc2gra[2] + (1 - alpha) * event.values[2];

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
                break;
            }
            case Sensor.TYPE_MAGNETIC_FIELD:{
                geomagnetic = event.values;
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
            case Sensor.TYPE_GRAVITY:{
                gravity = event.values;
                SYNC[6] = 1;
                break;
            }
            default:
                break;
        }

        if(sync()) {
            String captime = getCurrentTime();
            fileUtil = new com.clj.blesample.FileUtil();

            if(SensorManager.getRotationMatrix(r, I, acc2gra, geomagnetic)){

                float [] A_W = new float[3];
                A_W[0] = r[0] * geomagnetic[0] + r[1] * geomagnetic[1] + r[2] * geomagnetic[2];
                A_W[1] = r[3] * geomagnetic[0] + r[4] * geomagnetic[1] + r[5] * geomagnetic[2];
                A_W[2] = r[6] * geomagnetic[0] + r[7] * geomagnetic[1] + r[8] * geomagnetic[2];
                geomagnetic[0] = A_W[0];
                geomagnetic[1] = A_W[1];
                geomagnetic[2] = A_W[2];
//                Log.d("test", "校准磁强："+geomagnetic[0]+" "+geomagnetic[1]+" "+geomagnetic[2]);


                SensorManager.getOrientation(r, orien);

                //Log.d("test", "旋转角: "+ (float)Math.toDegrees(orien[0])+" "+(float)Math.toDegrees(orien[1])+" "+(float)Math.toDegrees(orien[2]));

            }



            SensorData.addSensorData(magneticData, accelerometerData, orientationData,
                    gyroscopeData, stepCount, accnorm, captime);
            BleMagData.addSensorData(magneticData, accelerometerData, orientationData,
                    gyroscopeData, stepCount, accnorm, captime);
            reset();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public boolean sync(){

        if(SYNC[3] == 1&&SYNC[1] == 1&&SYNC[6] == 1){
//            Log.d("SensorListener", "Sensor collect success！");
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
