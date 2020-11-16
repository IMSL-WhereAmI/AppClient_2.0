package com.ng.anthony.mininavigationdrawer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.ng.anthony.mininavigationdrawer.Sensor.SensorCollector;
import com.ng.anthony.mininavigationdrawer.Sensor.SensorData;
import com.ng.anthony.mininavigationdrawer.Sensor.SensorListener;
import com.ng.anthony.mininavigationdrawer.Sensor.WifiCollector;
import com.ng.anthony.mininavigationdrawer.Sensor.WifiData;
import com.ng.anthony.mininavigationdrawer.Sensor.FusionCollector;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_PERMISSION_EXTERNAL_STORAGE = 0x1111;
    private static final int REQ_CODE_PERMISSION_SENSOR = 0x2222;


    private boolean flag;   // true: begin location ; false: stop location
    private boolean changeMapflag;

    private String tCode;
    // T10101 -- wifi mode
    // T10102 -- magnetic mode
    // T10201 -- fusion mode

    private String deviceId;
    private String mapIndex;
    private static double X;
    private static double Y;

    private FloatingActionButton fab;
    private WifiManager wifiManager;
    private SensorManager sensorManager;
    private SensorListener sensorListener;
    private Sensor accelerometerSensor;
    private Sensor gyroscopeSensor;
    private Sensor magneticSensor;
    private Sensor orientationSensor;
    private Sensor stepCounterSensor;
    private Sensor stepDetecterSensor;
    private ScheduledFuture future1;
    private ScheduledFuture future2;
    private Timer timer;

    ImageView imageView;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        flag = false;
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(btn_listener);

        IntentFilter intentFilter = new IntentFilter();//创建IntentFilter对象
        intentFilter.addAction("update_XY");//IntentFilter对象中添加动作
        MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
        getApplicationContext().registerReceiver(myBroadcastReceiver, intentFilter);//动态注册广播

        initial();
    }

    public void initial() {
        sensorListener = new SensorListener();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        stepDetecterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        permissionCheck();

        tCode = "T10102";
        mapIndex = "01";
        changeMapflag=false;

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        deviceId = tm.getDeviceId();
        Toast.makeText(MainActivity.this, "permissionCheck over, deviceId:" + deviceId, Toast.LENGTH_SHORT).show();
    }



    public void permissionCheck(){
        // for magnetic location
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED){
            //申请BODY_SENSOR权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BODY_SENSORS},
                    REQ_CODE_PERMISSION_SENSOR);
        }

        // for wifi location
        String[] permissions = new String[]{
                Manifest.permission.CALL_PHONE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CHANGE_WIFI_STATE};
        List<String> mPermissionList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
            Log.d("permissionCheck", "wifi location over");
        } else {//请求权限方法
            String[] permissionsss = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(MainActivity.this, permissionsss, 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setIconsVisible(menu,true);
        return true;
    }

    private void setIconsVisible(Menu menu, boolean flag) {
        //判断menu是否为空
        if(menu != null) {
            try {
                //如果不为空,就反射拿到menu的setOptionalIconsVisible方法
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                //暴力访问该方法
                method.setAccessible(true);
                //调用该方法显示icon
                method.invoke(menu, flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        String text = "";

        if(flag){
            text = "Stop Location!  ";
            stopCollector(tCode);
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_wifi) {
            tCode = "T10101";
            text = text + "Change to Wifi Mode";
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            return true;
        }else if(id == R.id.action_magnetic){
            tCode = "T10102";
            text = text + "Change to Magnetic Mode";
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            return true;
        }else if(id == R.id.action_fusion){
            tCode = "T10201";
            text = text + "Change to Fusion Mode";
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    final Handler handler = new Handler();
    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            // TODO Auto-generated method stub
            stopCollector(tCode);
            beginCollector(tCode);
            handler.postDelayed(this, 15*1000);// 15s是延时时长
        }
    };

    private View.OnClickListener btn_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!flag){
                Snackbar.make(v, "Begin to Location", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                beginCollector(tCode);
                handler.postDelayed(runnable, 15*1000);
            }else {
                Snackbar.make(v, "Stop to Location", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                stopCollector(tCode);
            }
        }
    };

    public void beginCollector(String tCode){
        if(!flag){
            flag = !flag;
            Log.d("beginCollector", "flag: "+flag+" tCode:" +tCode);
            if (tCode == "T10101") {
                Toast.makeText(MainActivity.this, "Begin to Wifi location", Toast.LENGTH_SHORT).show();
                ScheduledExecutorService service = Executors.newScheduledThreadPool(5);
                WifiCollector instance = new WifiCollector(getApplicationContext(), wifiManager, tCode, mapIndex, deviceId);//发送wifi信号，返回坐标
                LocationResult lr = new LocationResult(tCode);//赋值xy，广播更新
                future1 = service.scheduleAtFixedRate(instance, 0, 1, TimeUnit.SECONDS);
                future2 = service.scheduleAtFixedRate(lr, 0, 1, TimeUnit.SECONDS);
            }
            if (tCode == "T10102"){
                Toast.makeText(MainActivity.this, "Begin to Magnetic location", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST ))
                    Toast.makeText(MainActivity.this, "加速度传感器不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, magneticSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    Toast.makeText(MainActivity.this, "磁场传感器不可用", Toast.LENGTH_SHORT).show();//监听收集地磁
                if(!sensorManager.registerListener(sensorListener, orientationSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    Toast.makeText(MainActivity.this, "方向传感器不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    //Toast.makeText(MainActivity.this, "记步传感器不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, stepDetecterSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    //Toast.makeText(MainActivity.this, "记步传感器不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    Toast.makeText(MainActivity.this, "陀螺仪不可用", Toast.LENGTH_SHORT).show();
                ScheduledExecutorService service = Executors.newScheduledThreadPool(5);
                SensorCollector instance = new SensorCollector(tCode,mapIndex,deviceId);//发送地磁，返回坐标
                LocationResult lr = new LocationResult(tCode);//赋值xy，广播更新
                future1 = service.scheduleAtFixedRate(instance, 0, 1, TimeUnit.SECONDS);//间隔时间1s
                future2 = service.scheduleAtFixedRate(lr, 0, 1, TimeUnit.SECONDS);//间隔时间1s
            }
            if (tCode == "T10201"){
                Toast.makeText(MainActivity.this, "Begin to Fusion location", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST ))
                    Toast.makeText(MainActivity.this, "加速度传感器不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, magneticSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    Toast.makeText(MainActivity.this, "磁场传感器不可用", Toast.LENGTH_SHORT).show();//监听收集地磁
                if(!sensorManager.registerListener(sensorListener, orientationSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    Toast.makeText(MainActivity.this, "方向传感器不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    //Toast.makeText(MainActivity.this, "记步传感器不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, stepDetecterSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    //Toast.makeText(MainActivity.this, "记步传感器不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    Toast.makeText(MainActivity.this, "陀螺仪不可用", Toast.LENGTH_SHORT).show();
                ScheduledExecutorService service = Executors.newScheduledThreadPool(5);
                FusionCollector instance = new FusionCollector(wifiManager, tCode, mapIndex, deviceId);//发送融合信号，返回坐标
                LocationResult lr = new LocationResult(tCode);//赋值xy，广播更新
                future1 = service.scheduleAtFixedRate(instance, 0, 1, TimeUnit.SECONDS);
                future2 = service.scheduleAtFixedRate(lr, 0, 1, TimeUnit.SECONDS);

            }
        }
        else
            return;
    }

    public void stopCollector(String tCode){
        if(flag){
            flag = !flag;
            Log.d("stopCollector", "flag: "+ flag +" tCode:" + tCode);
            if (tCode == "T10101"){
                future1.cancel(true);
                future2.cancel(true);
            }
            if (tCode == "T10102"){
                future1.cancel(true);
                future2.cancel(true);
                sensorManager.unregisterListener(sensorListener);//停止监听收集地磁
            }
            if (tCode == "T10201"){
                future1.cancel(true);
                future2.cancel(true);
                sensorManager.unregisterListener(sensorListener);//停止监听收集地磁
            }
        }
        else
            return;
    }

    public void changemap(){
        changeMapflag=true;
        return;
    }

    public String gettCode(){
        return tCode;
    }

    public String getDeviceId(){
        return deviceId;
    }

    public String getMapIndex(){
        return mapIndex;
    }

    public void setMapIndex(String mapIdx){
        this.mapIndex = mapIdx;
        Log.d("TAG", "selectMap: "+mapIdx);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        private double width;
        private double height;

        private static final int MAXN = 100000;
        float path_x[];
        float path_y[];
        float X_pre;
        float Y_pre;
        int number;

        public MyBroadcastReceiver(){
            X_pre = (float) 1000;
            Y_pre = (float) 1050;
            path_x = new float[MAXN];
            path_y = new float[MAXN];
            number = 0;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d("paint", "mapIndex: "+mapIndex);
            if(mapIndex == "01"){
                imageView = (ImageView) findViewById(R.id.map1_image);
                width = 1600;
                height = 475;
            }
            else if (mapIndex == "02"){
                imageView = (ImageView) findViewById(R.id.map2_image);
                width = 1271;
                height = 842;
            }

            if(changeMapflag==true){
                number = 0;
                X=0;
                Y=0;
                X_pre = (float) 1000;
                Y_pre = (float) 1050;
                path_x = null;
                path_y = null;
                path_x = new float[MAXN];
                path_y = new float[MAXN];
                changeMapflag=false;
            }

            double xRatio = X / width;
            double yRatio = Y / height;
            double xCor = xRatio * imageView.getWidth();
            double yCor = yRatio * imageView.getHeight();
            bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(20);
            // canvas.drawPoint((float) xCor, (float) yCor, paint);
            canvas.drawCircle((float) xCor, (float) yCor, 30,paint);

            //Log.d("paint", "number: "+number);
            if(number > 0){
                int i = 0;
                if(number > 1){//
                    for (i = 1; i<number; i++){
                        //Log.d("paint", "xy: "+path_x[i]+" "+path_y[i]);
                        X_pre = path_x[i];
                        Y_pre = path_y[i];
                        float X_ppre = path_x[i-1];
                        float Y_ppre = path_y[i-1];
                        double xpreRatio = X_pre / width;
                        double ypreRatio = Y_pre / height;
                        double xppreRatio = X_ppre / width;
                        double yppreRatio = Y_ppre / height;
                        double xpreCor = xpreRatio * imageView.getWidth();
                        double ypreCor = ypreRatio * imageView.getHeight();
                        double xppreCor = xppreRatio * imageView.getWidth();
                        double yppreCor = yppreRatio * imageView.getHeight();
                        paint.setStrokeWidth((float) 15.0);
                        if(xpreCor > 0 && ypreCor>0 && xppreCor>0 && yppreCor>0){
                            paint.setPathEffect(new DashPathEffect(new float[]{20, 10}, 0));
                            canvas.drawLine((float) xppreCor, (float) yppreCor, (float) xpreCor, (float) ypreCor,  paint );
                        }
                    }
                }else{//number=1
                    i = 1;
                }
                X_pre = path_x[i-1];
                Y_pre = path_y[i-1];
                if(X_pre > 0 && Y_pre>0){
                    double xpreRatio = X_pre / width;
                    double ypreRatio = Y_pre / height;
                    double xpreCor = xpreRatio * imageView.getWidth();
                    double ypreCor = ypreRatio * imageView.getHeight();
                    paint.setStrokeWidth((float) 5.0);
                    canvas.drawLine((float) xCor, (float) yCor, (float) xpreCor, (float) ypreCor,  paint );
                }
            }
            path_x[number] = (float) X;
            path_y[number] = (float) Y;
            number++;
            imageView.setImageBitmap(bitmap);
            changeMapflag=false;
        }
    }

    public class LocationResult implements Runnable {

        private double[] locationResult;
        private String tCode;
        Intent intent;

        LocationResult(String tcode){
            tCode = tcode;
            intent = new Intent("update_XY");
        }

        @Override
        public void run() {
            //Log.d("LocationResult", "run: ");
            if(tCode == "T10101"){
                locationResult = WifiData.getLocationResult();
                //Log.d("LocationResult", "run: "+locationResult[0]+" "+locationResult[1]);
                X = locationResult[0];
                Y = locationResult[1];
                sendBroadcast(intent);
            }
            else if(tCode == "T10102"){
                locationResult = SensorData.getLocationResult();
                //Log.d("LocationResult", "run: "+locationResult[0]+" "+locationResult[1]);
                X = locationResult[0];
                Y = locationResult[1];
                sendBroadcast(intent);//发送广播
            }
            else if(tCode == "T10201"){
                locationResult = SensorData.getLocationResult();
                //Log.d("LocationResult", "run: "+locationResult[0]+" "+locationResult[1]);
                X = locationResult[0];
                Y = locationResult[1];
                sendBroadcast(intent);//发送广播
            }
        }
    }
}
