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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.ng.anthony.mininavigationdrawer.Http.HttpHelper;
import com.ng.anthony.mininavigationdrawer.Sensor.AzimuthData;
import com.ng.anthony.mininavigationdrawer.Sensor.BleData;
import com.ng.anthony.mininavigationdrawer.Sensor.BleMagCollector;
import com.ng.anthony.mininavigationdrawer.Sensor.DataConstrain;
import com.ng.anthony.mininavigationdrawer.Sensor.BleMagData;
import com.ng.anthony.mininavigationdrawer.Sensor.MagCollector;
import com.ng.anthony.mininavigationdrawer.Sensor.SensorCollector;
import com.ng.anthony.mininavigationdrawer.Sensor.SensorData;
import com.ng.anthony.mininavigationdrawer.Sensor.SensorListener;
import com.ng.anthony.mininavigationdrawer.Sensor.WifiCollector;
import com.ng.anthony.mininavigationdrawer.Sensor.WifiCollector2;
import com.ng.anthony.mininavigationdrawer.Sensor.WifiData;
import com.ng.anthony.mininavigationdrawer.Sensor.FusionCollector;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleMtuChangedCallback;
import com.clj.fastble.callback.BleRssiCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.clj.blesample.FileUtil;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_PERMISSION_EXTERNAL_STORAGE = 0x1111;
    private static final int REQ_CODE_PERMISSION_SENSOR = 0x2222;


    private boolean flag;   // true: begin location ; false: stop location
    private boolean changeMapflag;
    private boolean constrain_flag; // true: add_azimuth

    private String tCode;
    // T10101 -- wifi mode
    // T10102 -- magnetic mode
    // T10201 -- fusion mode
    // T10103 -- bluetooth

    private String deviceId;
    private String mapIndex;
    private static double X;
    private static double Y;
    private static float azimuth;
    private static Integer state;

    private FloatingActionButton fab;
    private Button control_constrain;
    private WifiManager wifiManager;
    private SensorManager sensorManager;
    private SensorListener sensorListener;
    private Sensor accelerometerSensor;
    private Sensor accelertempSensor;
    private Sensor gyroscopeSensor;
    private Sensor magneticSensor;
    private Sensor orientationSensor;
    private Sensor stepCounterSensor;
    private Sensor stepDetecterSensor;
    private Sensor gravitySensor;
    private ScheduledFuture future1;
    private ScheduledFuture future2;
    private Timer timer;
    private Timer viewtimer;
    private static FileUtil fileUtil;
    private static Integer Blecount;

    private TextView getstate;
    private TextView getstate1;

    private TextView mapView;
    private TextView methodView;

    private BluetoothLeScannerCompat scanner;
    private ScanSettings settings;
    private ScanCallback scanCallback;

    //蓝牙
    private static String[] BLE_NAMES = new String[]{"A207-01","A207-02","A207-03","A207-04","A207-05","A207-06","A207-07","A207-08", "A207-09","A207-10",
            "A207-11","A207-12","A207-13","A207-14","A207-15","A207-16","A207-17","A207-18", "A207-19","A207-20",
            "A207-21","A207-22","A207-23","A207-24","A207-25","A207-26","A207-27","A207-28", "A207-29","A207-30",
            "A207-31","A207-32","A207-33","A207-34","A207-35","A207-36","A207-37","A207-38"};

    private static String[] BLE_MACS1 = new String[]{"0C:EC:80:FE:8C:CC","0C:EC:80:FF:10:FA","0C:EC:80:FE:7B:E9","0C:EC:80:FF:0F:B2","0C:EC:80:FE:82:AC"};

    //private static String[] BLE_MACS2 = new String[]{"0C:EC:80:FE:8C:CC","0C:EC:80:FF:10:FA","0C:EC:80:FE:7B:E9","0C:EC:80:FF:0F:B2","0C:EC:80:FE:82:AC", "C5:7A:11:D5:98:6F","FA:0C:4B:97:A0:0E","CA:5D:8F:59:87:1C","E0:A8:DE:AA:B4:C4","D1:42:7A:BB:E0:25"};
    //停车场
    private static String[] BLE_MACS2_PARKING = new String[]{"80:EC:CA:CD:05:CB", "80:EC:CA:CD:05:C0",
                                                        "80:EC:CA:CD:05:C5", "80:EC:CA:CD:06:1D",
                                                        "80:EC:CA:CD:05:C7", "80:EC:CA:CD:05:C4",
                                                        "80:EC:CA:CD:05:E6", "80:EC:CA:CD:05:ED",
                                                        "80:EC:CA:CD:05:D5", "80:EC:CA:CD:05:C9",
                                                        "80:EC:CA:CD:06:28", "80:EC:CA:CD:06:20",
                                                        "80:EC:CA:CD:05:C2", "80:EC:CA:CD:05:D2",
                                                        "80:EC52:CA:CD:05:C1", "80:EC:CA:CD:05:E8",
                                                        "80:EC:CA:CD:05:EC", "80:EC:CA:CD:05:DD",
                                                        "80:EC:CA:CD:06:23", "80:EC:CA:CD:05:D4",
                                                        "80:EC:CA:CD:05:C3", "80:EC:CA:CD:06:1E",
                                                        "80:EC:CA:CD:05:E4", "80:EC:CA:CD:05:E5",
                                                        "80:EC:CA:CD:06:24", "80:EC:CA:CD:06:21",
                                                        "80:EC:CA:CD:05:E0", "80:EC:CA:CD:05:D6",
                                                        "80:EC:CA:CD:05:CA", "80:EC:CA:CD:06:26",
                                                        "80:EC:CA:CD:05:EF", "80:EC:CA:CD:05:EE",
                                                        "80:EC:CA:CD:05:CC", "80:EC:CA:CD:05:D3",
                                                        "80:EC:CA:CD:06:1F", "80:EC:CA:CD:06:27",
                                                        "80:EC:CA:CD:05:E9", "80:EC:CA:CD:05:EB",
                                                        "80:EC:CA:CD:06:25", "80:EC:CA:CD:05:E7"};
    //学院2楼
    private static String[] BLE_MACS2_FLOOR2 = new String[]{"80:EC:CA:CD:12:9B", "80:EC:CA:CD:12:AF",
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
            "80:EC:CA:CD:11:B2", "80:EC:CA:CD:11:E6"};
//            "80:EC:CA:CD:12:3B", "80:EC:CA:CD:11:C8",
//            "80:EC:CA:CD:12:5E", "80:EC:CA:CD:12:7C",
//            "80:EC:CA:CD:12:09", "80:EC:CA:CD:12:99",
//            "80:EC:CA:CD:12:9A", "80:EC:CA:CD:12:75",
//            "80:EC:CA:CD:12:69", "80:EC:CA:CD:12:51",
//            "80:EC:CA:CD:12:4D", "80:EC:CA:CD:12:97",
//            "80:EC:CA:CD:12:94", "80:EC:CA:CD:12:8F"};

    private static String[] BLE_MACS2 = new String[]{"80:EC:CA:CD:05:CB", "80:EC:CA:CD:05:C0",
            "80:EC:CA:CD:05:C5", "80:EC:CA:CD:06:1D",
            "80:EC:CA:CD:05:C7", "80:EC:CA:CD:05:C4",
            "80:EC:CA:CD:05:E6", "80:EC:CA:CD:05:ED",
            "80:EC:CA:CD:05:D5", "80:EC:CA:CD:05:C9",
            "80:EC:CA:CD:06:28", "80:EC:CA:CD:06:20",
            "80:EC:CA:CD:05:C2", "80:EC:CA:CD:05:D2",
            "80:EC52:CA:CD:05:C1", "80:EC:CA:CD:05:E8",
            "80:EC:CA:CD:05:EC", "80:EC:CA:CD:05:DD",
            "80:EC:CA:CD:06:23", "80:EC:CA:CD:05:D4",
            "80:EC:CA:CD:05:C3", "80:EC:CA:CD:06:1E",
            "80:EC:CA:CD:05:E4", "80:EC:CA:CD:05:E5",
            "80:EC:CA:CD:06:24", "80:EC:CA:CD:06:21",
            "80:EC:CA:CD:05:E0", "80:EC:CA:CD:05:D6",
            "80:EC:CA:CD:05:CA", "80:EC:CA:CD:06:26",
            "80:EC:CA:CD:05:EF", "80:EC:CA:CD:05:EE",
            "80:EC:CA:CD:05:CC", "80:EC:CA:CD:05:D3",
            "80:EC:CA:CD:06:1F", "80:EC:CA:CD:06:27",
            "80:EC:CA:CD:05:E9", "80:EC:CA:CD:05:EB",
            "80:EC:CA:CD:06:25", "80:EC:CA:CD:05:E7"};

    private static int[] ble_rssi = new int[BLE_MACS2.length];
    private static List<List<Integer>> ble_rssi_list = new ArrayList<List<Integer>>();
    private static void reset(int[] rssi){
        for(int i = 0; i < rssi.length; i++)
            rssi[i] = -100;
    }
    private static void ble_clear(){
        ble_rssi_list.clear();
        for(int i = 0; i < BLE_MACS2.length; i++){
            List<Integer> list = new ArrayList<Integer>();
            list.add(-100);
            ble_rssi_list.add(list);
        }
    }
    //wifi
    List<String> wifilable = new ArrayList<>();
    List<List> wifidata = new ArrayList<>();

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
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        scanner = BluetoothLeScannerCompat.getScanner();
        settings = new ScanSettings.Builder().
                setUseHardwareFilteringIfSupported(true).
                setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).
                setReportDelay(0).
                build();
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                if(Arrays.asList(BLE_MACS2).contains(result.getDevice().toString())){
                    ble_rssi_list.get(Arrays.asList(BLE_MACS2).indexOf(result.getDevice().toString())).add(0,result.getRssi());

//                    Log.d("scanResult", result.toString());

                }
//                // BLE_MACS1表示使用第一组mac地址
//                if(Arrays.asList(BLE_MACS2).contains(result.getDevice().toString())){
//                    ble_rssi[Arrays.asList(BLE_MACS2).indexOf(result.getDevice().toString())] = result.getRssi();
//                    Log.d("scanResult", result.toString());
//                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };

        flag = false;
        fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setBackground(getResources().getDrawable(R.drawable.wifi));
        fab.setBackgroundColor(Color.parseColor("#2196F3"));
        fab.setOnClickListener(btn_listener);

        constrain_flag = false;//初始不添加方位角约束
        DataConstrain.setConstrainMode(constrain_flag);

        control_constrain = (Button) findViewById(R.id.constrain);
        control_constrain.setOnClickListener(constrain_listener);


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
        accelertempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        stepDetecterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        fileUtil = new FileUtil();
        permissionCheck();

        tCode = "T10102";
        mapIndex = "01";
        changeMapflag=false;

        getstate = (TextView) findViewById(R.id.loc_state);
        getstate1 = (TextView) findViewById(R.id.loc_state1);

        mapView = (TextView) findViewById(R.id.loc_map);
        methodView = (TextView) findViewById(R.id.loc_method);

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
        //Toast.makeText(MainActivity.this, "permissionCheck over, deviceId:" + deviceId, Toast.LENGTH_SHORT).show();
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

    private void setBleScanRule() { //蓝牙扫描规则
        String[] names;
        String BLE_names = "A207-01,A207-02,A207-03,A207-04,A207-05,A207-06,A207-07,A207-08,A207-09,A207-10," +
                "A207-11,A207-12,A207-13,A207-14,A207-15,A207-16,A207-17,A207-18,A207-19,A207-20," +
                "A207-21,A207-22,A207-23,A207-24,A207-25,A207-26,A207-27,A207-28,A207-29,A207-30," +
                "A207-31,A207-32,A207-33,A207-34,A207-35,A207-36,A207-37,A207-38";
        names = BLE_names.split(",");
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setDeviceName(true, names)   // 只扫描指定广播名的设备，可选
                .setAutoConnect(false)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(0)              // 扫描超时时间，可选，默认    10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);

    }

    public static String GetSystemTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    public static int majorityElement(Integer[] nums) {//求众数
        int len = nums.length;
        int count = 0;
        Integer max = nums[0];
        for(int i = 1; i < len-1; i++){
            if(max == nums[i]){
                count++;
            }else{
                count--;
                if(count == 0){
                    max = nums[i];
                    count++;
                }
            }
        }
        return max.intValue();
    }

    public static int getpartElement(Integer[] nums) {
        int range[][]={{-100,-95},{-95,-90},{-90,-85},{-85,-80},{-80,-75},{-75,-70}, {-70,-65},
                {-65,-60},{-60,-55},{-55,-50},{-50,-45},{-45,-40},{-40,-35},{-35,-30}};
        Arrays.sort(nums);
        int max_count = 0, count = 0;
        int max_sum = 0, sum = 0;
        for(int j = 0;j<range.length;j++){//对每个区间
            count = 0;
            sum = 0;
            for(int i = 1;i<nums.length;i++){//对每个rssi
                if(range[j][0] <= nums[i] && nums[i] <= range[j][1]){
                    count++;
                    sum+=nums[i];
                }
            }
            if(count > max_count){
                max_count = count;
                max_sum = sum;
            }
        }

        if (max_count == 0)
            return -100;

        max_sum /= max_count;
        return max_sum;
    }

    public static int frequentElement(Integer[] nums){
        Arrays.sort(nums);
        int index = nums.length/2, len = index/2;
        Integer count = 1+len*2;
        Integer sum = nums[index];
        for(int i = 1;i<=len;i++){
            sum += (nums[index-1]+nums[index+1]);
            //-100 -89 -82 -82 -80 -70 -70 -70
        }
        sum /= count;
        return sum.intValue();
    }

    public static int[] GetFrequentRssi(List<List<Integer>> list){
        int[] rssi = new int[list.size()];
        for(int i = 0; i < list.size(); i++){
            Integer[] nums = new Integer[list.get(i).size()];
            int fre_rssi = getpartElement(list.get(i).toArray(nums));
            //int fre_rssi = frequentElement(list.get(i).toArray(nums));
            rssi[i] = fre_rssi;
        }
        return rssi;
    }

    public static class SaveScanRes extends TimerTask {//计时器保存蓝牙扫描数据
        @Override
        public void run() {
            Blecount++;
            ble_rssi = GetFrequentRssi(ble_rssi_list);
            String data = Arrays.toString(ble_rssi);
            Log.i("BleData", data);
            try {
                String result =  HttpHelper.sendJsonPost("",data,"","","","","bluetooth",Blecount,0,"","");
                Log.d("BleResult", result);
                Map maps = (Map)JSON.parse(result);
                String status = maps.get("status").toString();
                if(status!=null && ("1").equals(status)) {
                    String TermLoc = maps.get("TermLoc").toString();
                    Log.d("TermLoc", TermLoc);
                    TermLoc = TermLoc.substring(1, TermLoc.length() - 1);
                    String[] str = TermLoc.split(",");
                    float X = Float.parseFloat(str[0]);
                    float Y = Float.parseFloat(str[1]);
                    Log.d("BleResData", "XY: " + X + " " + Y);
                    BleData.setLocationResult(X, Y);
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            ble_clear();//时间段内统计数据清空
        }
    }

    public static class SaveScanRes1 extends TimerTask {//计时器保存蓝牙扫描数据
        @Override
        public void run() {
            Blecount++;
            String data = Arrays.toString(ble_rssi);
//            boolean success = fileUtil.saveSensorData("LocationBleData.csv", data.substring(1, data.length()-1)
//                    + "," + GetSystemTime() + "\n");

            Log.i("BleData", data);
            try {
                String result =  HttpHelper.sendJsonPost("",data,"","","","","bluetooth",Blecount,0,"","");
                Log.d("BleResult", result);
                Map maps = (Map)JSON.parse(result);
                String status = maps.get("status").toString();
                if(status!=null && ("1").equals(status)) {
                    String TermLoc = maps.get("TermLoc").toString();
                    Log.d("TermLoc", TermLoc);
                    TermLoc = TermLoc.substring(1, TermLoc.length() - 1);
                    String[] str = TermLoc.split(",");
                    float X = Float.parseFloat(str[0]);
                    float Y = Float.parseFloat(str[1]);
                    Log.d("BleResData", "XY: " + X + " " + Y);
                    BleData.setLocationResult(X, Y);
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            reset(ble_rssi);
        }
    }



    private void StartBleScan(){
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                if(success){
                    Log.i("onScanStarted", "onScanStarted: success");
                }
                else {
                    Log.i("onScanStarted", "onScanStarted: failed");
                }
                timer = new Timer();
                timer.schedule(new SaveScanRes(), 2000, 2000);
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
                //Log.i("onLeScan", "onLeScan: " + bleDevice.getName() + "," + bleDevice.getRssi());
                String name = bleDevice.getName();
                if(Arrays.asList(BLE_NAMES).contains(name)){
                    ble_rssi[Integer.parseInt(name.substring(5)) - 1] = bleDevice.getRssi();
                }
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                timer.cancel();

            }
        });
    }

    public static class SaveScanRes_addmag extends TimerTask {//计时器保存蓝牙扫描数据
        @Override
        public void run() {
            Blecount++;
            ble_rssi = GetFrequentRssi(ble_rssi_list);
            BleMagData.addBleData(ble_rssi);
            ble_clear();//时间段内统计数据清空
        }
    }

    public class viewstate extends TimerTask {//显示当前状态
        @Override
        public void run() {
                state = BleMagData.getLocationstate();
                if(state == 0){
                    getstate.setText("当前正在收集地磁序列");
                    getstate1.setText(" ");
                }
                else if(state == 1){
                    getstate.setText("定位方式:");
                    getstate1.setText("地磁");
                }
                else if(state == 2){
                    getstate.setText("定位方式:");
                    getstate1.setText("PDR");
                }
                else if(state == 3){
                    getstate.setText("当前处于静止状态");
                    getstate1.setText(" ");
                }
                else if(state == 4){
                    getstate.setText("当前检测到掉头");
                    getstate1.setText(" ");
                }

        }
    }

    private void StartBleScan_addmag(){
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                if(success){
                    Log.i("onScanStarted", "onScanStarted: success");
                }
                else {
                    Log.i("onScanStarted", "onScanStarted: failed");
                }
//                timer = new Timer();
//                timer.schedule(new SaveScanRes_addmag(), 500, 1000);
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
                String name = bleDevice.getName();
                if(Arrays.asList(BLE_NAMES).contains(name)){
                    ble_rssi[Integer.parseInt(name.substring(5)) - 1] = bleDevice.getRssi();
                }
            }

            @Override

            public void onScanning(BleDevice bleDevice) {
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                timer.cancel();
            }
        });
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

    public void ReadWiFifingerprint(){
        //读取WiFi指纹数据库
        List<String> onewifidata = new ArrayList<>();
        try {
            Scanner scanner;
            InputStream is = getResources().getAssets().open("ScanData_Mate7.csv");
            scanner=new Scanner(is,"UTF-8");
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                onewifidata = new ArrayList<>();
                String sourceString = scanner.nextLine();
                Pattern pattern = Pattern.compile("[^,]*,");
                Matcher matcher = pattern.matcher(sourceString);
                String[] lines=new String[260];
                int i=0;
                while(matcher.find()) {
                    String find = matcher.group().replace(",", "");
                    lines[i]=find.trim();
                    if(i==0||i==1){
                        if(lines[i].equals("")){
                            break;
                        }
                        i++;
                        continue;
                    }else if(i==2){
                        if(lines[i].equals("avg")) break;
                        if(lines[i].equals("")){
                            wifilable.add(lines[0]+","+lines[1]);
                        }
                    }else{
                        if(lines[i].equals("0")){
                            onewifidata.add("-100");
                        }else{
                            onewifidata.add(lines[i]);
                        }
                    }
                    i++;
                }
                if(onewifidata.size()!=0){
                    //Log.d("readcsv", "onedata:" + onewifidata);
                    wifidata.add(onewifidata);
                }
            }
            //Log.d("readcsv", "lable:" + wifilable);
            //Log.d("readcsv", "data:" + wifidata.size());
        } catch (IOException e) {
            e.printStackTrace();
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
            wifilable.clear();
            wifidata.clear();
            ReadWiFifingerprint();
            text = text + "Change to Wifi Mode";
            //Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            return true;
        }else if(id == R.id.action_magnetic){
            tCode = "T10102";
            methodView.setText("    地磁定位");
            text = text + "Change to Magnetic Mode";
            //Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            return true;
        }else if(id == R.id.action_fusion){
            tCode = "T10201";
            text = text + "Change to Fusion Mode";
            //Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            return true;
        }else if(id == R.id.action_bluetooth){
            tCode = "T10103";
            text = text + "Change to Bluetooth Mode";
            //Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            return true;
        }else if(id == R.id.action_blemag){
            tCode = "T10202";
            methodView.setText("    地磁+PDR定位");
            text = text + "Change to Magnetic and Pdr Mode";
            //Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
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
     */

    private View.OnClickListener constrain_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            flag = !flag;
            DataConstrain.changeConstrainMode();
            if (!flag){ //false
                control_constrain.setText("约束");
                Log.d("cons_msg:",flag+"");
            }else{
                control_constrain.setText("方向约束");
                Log.d("cons_msg:",flag+"");
            }

        }
    };

    private View.OnClickListener btn_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!flag){
//                Snackbar.make(v, "Begin to Location", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                //v.setBackground(getResources().getDrawable(R.drawable.map_1));
                v.setBackgroundColor(Color.rgb(0, 255, 0));
                beginCollector(tCode);
                //handler.postDelayed(runnable, 15*1000);
            }else {
//                Snackbar.make(v, "Stop to Location", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                //v.setBackground(getResources().getDrawable(R.drawable.wifi));
                v.setBackgroundColor(Color.rgb(255, 0, 0));
                stopCollector(tCode);
            }
        }
    };

    public void beginCollector(String tCode){
        if(!flag){
            flag = !flag;
            Log.d("beginCollector", "flag: "+ flag +" tCode:" +tCode);
            if (tCode == "T10101") {
//                getstate.setText("当前正在进行WiFi定位");
                //Toast.makeText(MainActivity.this, "Begin to Wifi location", Toast.LENGTH_SHORT).show();
                ScheduledExecutorService service = Executors.newScheduledThreadPool(5);
                WifiCollector2 instance = new WifiCollector2(getApplicationContext(), wifiManager, tCode, mapIndex, deviceId, wifilable, wifidata);//发送wifi信号，返回坐标
                LocationResult lr = new LocationResult(tCode);//赋值xy，广播更新
                future1 = service.scheduleAtFixedRate(instance, 0, 1, TimeUnit.SECONDS);
                future2 = service.scheduleAtFixedRate(lr, 0, 1, TimeUnit.SECONDS);
            }
            if (tCode == "T10102"){
               // getstate.setText("当前正在进行地磁定位");
                //Toast.makeText(MainActivity.this, "Begin to Magnetic location", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST ))
                    Toast.makeText(MainActivity.this, "加速度传感器不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, magneticSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    Toast.makeText(MainActivity.this, "磁场传感器不可用", Toast.LENGTH_SHORT).show();//监听收集地磁
                if(!sensorManager.registerListener(sensorListener, orientationSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    Toast.makeText(MainActivity.this, "方向传感器不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, accelertempSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    Toast.makeText(MainActivity.this, "加速度线性传感器不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, stepDetecterSensor, SensorManager.SENSOR_DELAY_FASTEST))
                   // Toast.makeText(MainActivity.this, "记步传感器不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    Toast.makeText(MainActivity.this, "陀螺仪不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener,gravitySensor,SensorManager.SENSOR_DELAY_FASTEST))
                    Toast.makeText(MainActivity.this, "重力传感器不可用", Toast.LENGTH_SHORT).show();

                List<ScanFilter> filters = new ArrayList<>();
                Blecount = 0;
                reset(ble_rssi);
                ble_clear();
                scanner.startScan(filters, settings, scanCallback);

                timer = new Timer();
                timer.schedule(new SaveScanRes_addmag(), 500, 1000);
                viewtimer = new Timer();
                timer.schedule(new viewstate(),500,1000);

                ScheduledExecutorService service = Executors.newScheduledThreadPool(5);
                MagCollector instance = new MagCollector(tCode,mapIndex,deviceId);
                LocationResult lr = new LocationResult(tCode);//赋值xy，广播更新
                future1 = service.scheduleAtFixedRate(instance, 0, 1, TimeUnit.SECONDS);
                future2 = service.scheduleAtFixedRate(lr, 0, 1, TimeUnit.SECONDS);
//                ScheduledExecutorService service = Executors.newScheduledThreadPool(5);
//                SensorCollector instance = new SensorCollector(tCode,mapIndex,deviceId);//发送地磁，返回坐标
//                LocationResult lr = new LocationResult(tCode);//赋值xy，广播更新
//                future1 = service.scheduleAtFixedRate(instance, 0, 1, TimeUnit.SECONDS);//间隔时间1s
//                future2 = service.scheduleAtFixedRate(lr, 0, 1, TimeUnit.SECONDS);//间隔时间1s
            }
            if (tCode == "T10201"){
//                getstate.setText("当前正在进行融合定位");
                //Toast.makeText(MainActivity.this, "Begin to Fusion location", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST ))
                    Toast.makeText(MainActivity.this, "加速度传感器不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, magneticSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    Toast.makeText(MainActivity.this, "磁场传感器不可用", Toast.LENGTH_SHORT).show();//监听收集地磁
                if(!sensorManager.registerListener(sensorListener, orientationSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    Toast.makeText(MainActivity.this, "方向传感器不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, accelertempSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    Toast.makeText(MainActivity.this, "加速度线性传感器不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, stepDetecterSensor, SensorManager.SENSOR_DELAY_FASTEST))
                   // Toast.makeText(MainActivity.this, "记步传感器不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    Toast.makeText(MainActivity.this, "陀螺仪不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener,gravitySensor,SensorManager.SENSOR_DELAY_FASTEST))
                    Toast.makeText(MainActivity.this, "重力传感器不可用", Toast.LENGTH_SHORT).show();
                ScheduledExecutorService service = Executors.newScheduledThreadPool(5);
                FusionCollector instance = new FusionCollector(getApplicationContext(),wifiManager, tCode, mapIndex, deviceId);//发送融合信号，返回坐标
                LocationResult lr = new LocationResult(tCode);//赋值xy，广播更新
                future1 = service.scheduleAtFixedRate(instance, 1, 1, TimeUnit.SECONDS);
                future2 = service.scheduleAtFixedRate(lr, 1, 1, TimeUnit.SECONDS);

            }

            if (tCode == "T10103"){
//                getstate.setText("当前正在进行蓝牙定位");
                //Toast.makeText(MainActivity.this, "Begin to Ble location", Toast.LENGTH_SHORT).show();
                List<ScanFilter> filters = new ArrayList<>();

                Blecount = 0;
                reset(ble_rssi);
                ble_clear();
                //filters.add(new ScanFilter.Builder().setServiceUuid(mUuid).build());
                scanner.startScan(filters, settings, scanCallback);
//                //初始化及配置
//                BleManager.getInstance().init(getApplication());
//                BleManager.getInstance()
//                        .enableLog(true)
//                        .setReConnectCount(1, 5000)
//                        .setConnectOverTime(20000)
//                        .setOperateTimeout(5000);
//
//                Toast.makeText(MainActivity.this, "Begin to Fusion location", Toast.LENGTH_SHORT).show();
//                if(!BleManager.getInstance().isSupportBle())
//                    Toast.makeText(MainActivity.this, "当前设备不支持蓝牙", Toast.LENGTH_SHORT).show();
//                if(!BleManager.getInstance().isBlueEnable()){
//                    Toast.makeText(MainActivity.this, "蓝牙未开启", Toast.LENGTH_SHORT).show();
//                    return;
//                    //BleManager.getInstance().enableBluetooth();
//                }
//                setBleScanRule();
//                StartBleScan();
//
                ScheduledExecutorService service = Executors.newScheduledThreadPool(5);
                LocationResult lr = new LocationResult(tCode);//赋值xy，广播更新
                future2 = service.scheduleAtFixedRate(lr, 1, 1, TimeUnit.SECONDS);
                timer = new Timer();

//                timer.schedule(new SaveScanRes(), 2000, 2000);
                timer.schedule(new SaveScanRes(), 500, 1000);

            }

            if (tCode == "T10202"){
//                getstate.setText("当前正在进行地磁PDR融合定位");
                //Toast.makeText(MainActivity.this, "Begin to Pdr&Mag location", Toast.LENGTH_SHORT).show();
                //磁强运动传感器初始化
                if(!sensorManager.registerListener(sensorListener, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST ))
                    Toast.makeText(MainActivity.this, "加速度传感器不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, magneticSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    Toast.makeText(MainActivity.this, "磁场传感器不可用", Toast.LENGTH_SHORT).show();//监听收集地磁
                if(!sensorManager.registerListener(sensorListener, orientationSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    Toast.makeText(MainActivity.this, "方向传感器不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, accelertempSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    Toast.makeText(MainActivity.this, "加速度线性传感器不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, stepDetecterSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    //Toast.makeText(MainActivity.this, "记步传感器不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST))
                    Toast.makeText(MainActivity.this, "陀螺仪不可用", Toast.LENGTH_SHORT).show();
                if(!sensorManager.registerListener(sensorListener,gravitySensor,SensorManager.SENSOR_DELAY_FASTEST))
                    Toast.makeText(MainActivity.this, "重力传感器不可用", Toast.LENGTH_SHORT).show();

                List<ScanFilter> filters = new ArrayList<>();
                Blecount = 0;
                reset(ble_rssi);
                ble_clear();
                scanner.startScan(filters, settings, scanCallback);

                timer = new Timer();
                timer.schedule(new SaveScanRes_addmag(), 500, 1000);
                viewtimer = new Timer();
                timer.schedule(new viewstate(),500,1000);

                ScheduledExecutorService service = Executors.newScheduledThreadPool(5);
                BleMagCollector instance = new BleMagCollector(tCode,mapIndex,deviceId);
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
//                future1.cancel(true);
//                future2.cancel(true);
//                sensorManager.unregisterListener(sensorListener);
                scanner.stopScan(scanCallback);
//                BleManager.getInstance().cancelScan();
//                BleManager.getInstance().disconnectAllDevice();
//                BleManager.getInstance().destroy();
                future1.cancel(true);
                future2.cancel(true);
                sensorManager.unregisterListener(sensorListener);
                timer.cancel();
                getstate.setText("用户停止定位");
                getstate1.setText(" ");
            }
            if (tCode == "T10201"){
                future1.cancel(true);
                future2.cancel(true);
                sensorManager.unregisterListener(sensorListener);
            }
            if (tCode == "T10103"){
                scanner.stopScan(scanCallback);
//                BleManager.getInstance().cancelScan();
//                BleManager.getInstance().disconnectAllDevice();
//                BleManager.getInstance().destroy();
                future2.cancel(true);
                timer.cancel();
            }
            if (tCode == "T10202"){
                scanner.stopScan(scanCallback);
//                BleManager.getInstance().cancelScan();
//                BleManager.getInstance().disconnectAllDevice();
//                BleManager.getInstance().destroy();
                future1.cancel(true);
                future2.cancel(true);
                sensorManager.unregisterListener(sensorListener);
                timer.cancel();
                getstate.setText("用户停止定位");
                getstate1.setText(" ");
            }
        }
        else
            return;
    }

    public void changemap(String map_id){
        if(map_id.equals("01")){
            mapView.setText("学院2层");
        }else if(map_id.equals("02")){
            mapView.setText("学院3层");
        }else if(map_id.equals("03")){
            mapView.setText("学院4层");
        }

        getstate.setText(" ");
        getstate1.setText(" ");
        //constrain_flag = false;
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
                //height = 475;
                height = 600;
            }
            else if (mapIndex == "02" || mapIndex == "03"){
                imageView = (ImageView) findViewById(R.id.map2_image);
                width = 1300;
                //height = 475;
                height = 610;
                // 停车场场地比例
//                width = 1403;
//                height = 570;
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
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(20);
            // canvas.drawPoint((float) xCor, (float) yCor, paint);
            canvas.drawCircle((float) xCor+2, (float) yCor+2, 30,paint);

            Bitmap bitmap_loc;
            state = BleMagData.getLocationstate();
            if(state == 2){
                bitmap_loc  = ((BitmapDrawable)getResources().getDrawable(R.drawable.icons_navigation_24_black)).getBitmap();
            }else{
                bitmap_loc  = ((BitmapDrawable)getResources().getDrawable(R.drawable.icons_navigation_24)).getBitmap();
            }
            //Bitmap bitmap_loc  = ((BitmapDrawable)getResources().getDrawable(R.drawable.icons_navigation_24)).getBitmap();
            Matrix matrix=new Matrix();
            matrix.postRotate(azimuth-135);
            Bitmap dstbmp=Bitmap.createBitmap(bitmap_loc,0,0,bitmap_loc.getWidth(), bitmap_loc.getHeight(),matrix,true);
            canvas.drawBitmap(dstbmp, (float) xCor-40, (float) yCor-40, null);

//            canvas.drawBitmap(bitmap_loc, (float) xCor-30, (float) yCor-30, null);






//
//            //Log.d("paint", "number: "+number);
//            if(number > 0){
//                int i = 0;
//                if(number > 1){//
//                    for (i = 1; i<number; i++){
//                        //Log.d("paint", "xy: "+path_x[i]+" "+path_y[i]);
//                        X_pre = path_x[i];
//                        Y_pre = path_y[i];
//                        float X_ppre = path_x[i-1];
//                        float Y_ppre = path_y[i-1];
//                        double xpreRatio = X_pre / width;
//                        double ypreRatio = Y_pre / height;
//                        double xppreRatio = X_ppre / width;
//                        double yppreRatio = Y_ppre / height;
//                        double xpreCor = xpreRatio * imageView.getWidth();
//                        double ypreCor = ypreRatio * imageView.getHeight();
//                        double xppreCor = xppreRatio * imageView.getWidth();
//                        double yppreCor = yppreRatio * imageView.getHeight();
//                        paint.setStrokeWidth((float) 15.0);
////                        edit by duke
////                        if(xpreCor > 0 && ypreCor>0 && xppreCor>0 && yppreCor>0){
////                            paint.setPathEffect(new DashPathEffect(new float[]{20, 10}, 0));
////                            canvas.drawLine((float) xppreCor, (float) yppreCor, (float) xpreCor, (float) ypreCor,  paint );
////                        }
//                    }
//                }else{//number=1
//                    i = 1;
//                }
//                X_pre = path_x[i-1];
//                Y_pre = path_y[i-1];
////                edit by duke
////                if(X_pre > 0 && Y_pre>0){
////                    double xpreRatio = X_pre / width;
////                    double ypreRatio = Y_pre / height;
////                    double xpreCor = xpreRatio * imageView.getWidth();
////                    double ypreCor = ypreRatio * imageView.getHeight();
////                    paint.setStrokeWidth((float) 5.0);
////                    canvas.drawLine((float) xCor, (float) yCor, (float) xpreCor, (float) ypreCor,  paint );
////                }
//            }
//            path_x[number] = (float) X;
//            path_y[number] = (float) Y;
//            number++;
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
                X = locationResult[0];
                Y = locationResult[1];
                sendBroadcast(intent);
            }
            else if(tCode == "T10102"){
//                locationResult = SensorData.getLocationResult();
//                azimuth = AzimuthData.getAzimuth();
//                X = locationResult[0];
//                Y = locationResult[1];
////                X=1550;
////                Y=400;
//                sendBroadcast(intent);//发送广播
                locationResult = BleMagData.getLocationResult();
                azimuth = AzimuthData.getAzimuth();
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
            else if(tCode == "T10103"){
                locationResult = BleData.getLocationResult();
                azimuth = AzimuthData.getAzimuth();
                //Log.d("LocationResult", "run: "+locationResult[0]+" "+locationResult[1]);
                X = locationResult[0];
                Y = locationResult[1];
                sendBroadcast(intent);//发送广播
            }
            else if(tCode == "T10202"){
                locationResult = BleMagData.getLocationResult();
                azimuth = AzimuthData.getAzimuth();
                X = locationResult[0];
                Y = locationResult[1];
                sendBroadcast(intent);//发送广播
            }
        }
    }
}
