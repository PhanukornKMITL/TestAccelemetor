package com.example.testaccelemetor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StepCount extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView stepCountText;
    boolean activityRunning;


   public void getStepCount(){
       setContentView(R.layout.activity_main);
       stepCountText = (TextView) findViewById(R.id.StepCountText);
       sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
       Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
       if(countSensor != null){
           sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
       }else {
           Toast.makeText(this, "Count sensor not availble!",Toast.LENGTH_LONG).show();
       }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        stepCountText.setText(String.valueOf(event.values[0]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
