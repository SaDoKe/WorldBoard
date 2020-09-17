package org.sadoke.worldboard.locationtracker;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;
import org.sadoke.worldboard.MainActivity;

import static android.content.Context.LOCATION_SERVICE;

public class GPSFusedLocationTracker implements LocationListener {
    private LocationManager locMan;
    private LocationRequest locationRequest;
    private long interval = 1000;
    private long fastestInterval = 1000;
    private int accuracy = LocationRequest.PRIORITY_HIGH_ACCURACY;
    private MainActivity mainActivity;
    private LocationCallback callback;

    public GPSFusedLocationTracker(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        locMan = (LocationManager) mainActivity.getSystemService(LOCATION_SERVICE);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(this.accuracy);
        locationRequest.setInterval(this.interval);
        locationRequest.setFastestInterval(this.fastestInterval);

        callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
    }

    @Override // LocationListener
    public void onLocationChanged(Location location){
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("lattitude", location.getLatitude());
            jsonData.put("longitude", location.getLongitude());
        }catch (JSONException e){
            Log.e("JSON EXCEPTION: ", String.valueOf(e));
        }
    }

    @Override // LocationListener
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(mainActivity.getApplicationContext(), provider.toUpperCase() + " HAS CHANGED\n" + "STATUS: " + status, Toast.LENGTH_LONG).show();
    }

    @Override // LocationListener
    public void onProviderEnabled(String provider) {
        Toast.makeText(mainActivity.getApplicationContext(), "Please Activate " + provider.toUpperCase(), Toast.LENGTH_LONG).show();
    }

    @Override // LocationListener
    public void onProviderDisabled(String provider) {
        Toast.makeText(mainActivity.getApplicationContext(), provider.toUpperCase() + " ENABLED", Toast.LENGTH_LONG).show();
    }

    @SuppressLint("MissingPermission")
    public void startTracking(){
        Toast.makeText(mainActivity.getApplicationContext(), "starting fusedGPS", Toast.LENGTH_LONG).show();
        LocationServices.getFusedLocationProviderClient(this.mainActivity).requestLocationUpdates(locationRequest, callback, Looper.myLooper());
    }

    public void stopTracking(){
        LocationServices.getFusedLocationProviderClient(this.mainActivity).removeLocationUpdates(callback);
    }
}
