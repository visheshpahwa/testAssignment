package com.example.test;

import static android.hardware.Sensor.TYPE_LIGHT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    TextView modelNameTxt;
    TextView modelNumberTxt;
    TextView manufacturerTxt;
    TextView ramTxt;
    TextView storageTxt;
    TextView batteryTxt;
    TextView androidTxt;
    TextView processorTxt;
    TextView gpuTxt;
    TextView imeiTxt;

    double latitude, longitude, pressure, accelerometer, gyroscope, lightsensor;
    TextView tvlongitude, tvlatitude, tvbarometer, tvaccelerometer, tvgyroscope, tvlightsensor;
    LocationManager locationManager;
    SensorManager sensorManager;
    Sensor pressureSensor, accelerometerSensor, ambientLightSensor, gyroscopeSensor;

    private Context context;

    private String formatSize(long size) {
        String suffix = "B";
        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
                if (size >= 1024) {
                    suffix = "GB";
                    size /= 1024;
                }
            }
        }
        return String.format("%d %s", size, suffix);
    }


    private BroadcastReceiver batterylevelReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            batteryTxt.setText(String.valueOf(level));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String manufacturer = Build.MANUFACTURER;
        String modelName = Build.MODEL;
        String modelNumber = Build.DEVICE;

        manufacturerTxt = findViewById(R.id.manufacturerTxt);
        manufacturerTxt.setText(manufacturer);

        modelNameTxt = findViewById(R.id.modelNameTxt);
        modelNameTxt.setText(modelName);

        modelNumberTxt = findViewById(R.id.modelNumberTxt);
        modelNumberTxt.setText(modelNumber);


        batteryTxt = findViewById(R.id.batteryTxt);
        this.registerReceiver(this.batterylevelReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));


        // Get the device storage information
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        long blockSize = statFs.getBlockSizeLong();
        long totalBlocks = statFs.getBlockCountLong();
        long freeBlocks = statFs.getFreeBlocksLong();

        // Calculate the storage information
        long totalSize = blockSize * totalBlocks;
        long freeSize = blockSize * freeBlocks;

        // Format the storage information as a string
        String storageInfo = "Total: " + formatSize(totalSize);

        // Display the storage information in a TextView

        storageTxt = findViewById(R.id.storageTxt);
        storageTxt.setText(String.valueOf(storageInfo));


        // Get the device RAM information
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);
        long totalMemory = memoryInfo.totalMem;
        long availableMemory = memoryInfo.availMem;

        // Format the RAM information as a string
        String ramInfo = "Total: " + formatSize(totalMemory);

        // Display the RAM information in a TextView

        ramTxt = findViewById(R.id.ramTxt);
        ramTxt.setText(String.valueOf(ramInfo));

        String androidVersion = Build.VERSION.RELEASE;

        androidTxt = findViewById(R.id.androidTxt);
        androidTxt.setText(androidVersion);

        String processor = Build.HARDWARE;
        String gpu = ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getDeviceConfigurationInfo().getGlEsVersion();

        processorTxt = findViewById(R.id.processorTxt);
        processorTxt.setText(processor);

        gpuTxt = findViewById(R.id.gpuTxt);
        gpuTxt.setText(gpu);

        tvlongitude = findViewById(R.id.tvlongitude);
        tvlatitude = findViewById(R.id.tvlatitude);
        tvbarometer = findViewById(R.id.tvbarometer);
        tvaccelerometer = findViewById(R.id.tvaccelerometer);

        tvgyroscope = findViewById(R.id.tvgyroscope);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor= sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        ambientLightSensor=sensorManager.getDefaultSensor(TYPE_LIGHT);

        SensorEventListener pressurelistener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                pressure = event.values[0];
                tvbarometer.setText(String.valueOf(pressure));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        SensorEventListener accelerometerListener= new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                accelerometer= event.values[0];
                tvaccelerometer.setText(String.valueOf(accelerometer));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        SensorEventListener gyroscopeListener= new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                gyroscope= event.values[0]+ event.values[1] +event.values[2];
                tvgyroscope.setText(String.valueOf(gyroscope));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        SensorEventListener ambientlightListener= new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                gyroscope= event.values[0];
                tvgyroscope.setText(String.valueOf(gyroscope));

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(pressurelistener,pressureSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(accelerometerListener,accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(gyroscopeListener,gyroscopeSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener((SensorEventListener) this,ambientLightSensor,SensorManager.SENSOR_DELAY_NORMAL);


    }

    }




