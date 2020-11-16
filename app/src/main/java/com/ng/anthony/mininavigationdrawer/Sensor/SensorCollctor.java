package com.ng.anthony.mininavigationdrawer.Sensor;

import android.app.Activity;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.ng.anthony.mininavigationdrawer.MainActivity;

public class SensorCollctor implements Runnable {

    @Override
    public void run() {
        String data = SensorData.getAllDataStr();
        Log.d("SensorCollctor","DATA: " +  data);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
