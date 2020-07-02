package com.example.testaccelemetor;

import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import java.text.DecimalFormat;

public class Location {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private double  lat1,  lon1,  lat2,  lon2, result, speed;
    private boolean first = true;


}
