package com.example.testaccelemetor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class WorkOutActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView stepCountText, avgVelocity;
    boolean activityRunning;

    Chronometer chronometer;
    private TextView timeText;
    private Button getLocation;
    private TextView locationText;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private double  lat1,  lon1,  lat2, lon2, totalDistance = 0,speed;
    private boolean first = true;
    int elaspeTime = 0;
    float[] result = new float[1];


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        avgVelocity = findViewById(R.id.Velocity);
        stepCountText = (TextView) findViewById(R.id.StepCountText);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        timeText = (TextView) findViewById(R.id.TimeText);

        Thread t = new Thread(){
            @Override
            public void run(){
                while(!isInterrupted()){
                    try {
                        Thread.sleep(1000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                elaspeTime++;

                                timeText.setText("Total Time "+String.valueOf(elaspeTime));

                            }
                        });

                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }

            }

        };

        t.start();

        // ตัวแปรสำหรับกดปุ่มขอพิกัดแต่กรณีนี้ไม่ใช้เลยคอมเม้นไว้
        // getLocation = (Button) findViewById(R.id.getLocation);
        locationText = (TextView) findViewById(R.id.locationDistance);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //locationText.append("\n "+location.getLatitude()+ " "+location.getLongitude());



                lat2 = lat1;
                lat1 = location.getLatitude();
                lon2 = lon1;
                lon1 = location.getLongitude();
                DecimalFormat  d = new DecimalFormat("#.###");
                speed = location.getSpeed();
                if(!first){
                    Location.distanceBetween(lat1, lon1, lat2, lon2, result);
                    totalDistance += result[0]/1000;
                   // result += getDistance(lat2, lon2, lat1, lon1);
                //result = speed;
                }else{
                    totalDistance = 0;
                    first = false;
                }

                locationText.setText("Total Distance "+ d.format(totalDistance)+" Km");
                avgVelocity.setText("Average Velocity "+d.format(((totalDistance*1000) / elaspeTime))+" m/s");
                //avgVelocity.setText(" "+location.getSpeed());

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        //ขอ Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions((new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            }), 10);

            return;
        }else{
            //configureButton();
            recieveLocation();
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    //configureButton();
                break;
        }
    }

    private void recieveLocation(){
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
    }

    //อันนี้ำสำหรับจะเอาค่าพิกัด
    /*private void configureButton(){
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
            }
        });
    }*/

    // method สำหรับคำนวนค่าระยะทางจากพิกัดออกมาเป็นหน่วยเกิโลเมตร
    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double latA = Math.toRadians(lat1);
        double lonA = Math.toRadians(lon1);
        double latB = Math.toRadians(lat2);
        double lonB = Math.toRadians(lon2);
        double cosAng = (Math.cos(latA) * Math.cos(latB) * Math.cos(lonB-lonA)) +
                (Math.sin(latA) * Math.sin(latB));
        double ang = Math.acos(cosAng);
        double dist = ang *6371;
        return dist;
    }


    /////////////
    ////////////////
    /////////////////////////
    //Walk Sensor path
    protected void onResume() {
        super.onResume();
        activityRunning = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null){
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        }else {
            Toast.makeText(this, "Count sensor not availble!",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        stepCountText.setText("Total Step "+String.valueOf(event.values[0]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
