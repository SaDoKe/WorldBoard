package org.sadoke.worldboard.locationtracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;

import org.sadoke.worldboard.MainActivity;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class FusedLocationTracker implements LocationListener {
    LocationManager locationManager;
    LocationRequest locationRequest;

    public FusedLocationTracker(MainActivity mainActivity) {
        super();
        locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(400);
        locationRequest.setFastestInterval(400);
    }

    @SuppressLint("MissingPermission")
    public void startTracking() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 0, this);
    }

    public void stopTracking() {
        locationManager.removeUpdates(this);
    }

    @SuppressLint("MissingPermission")
    public Location getLastLocation() {
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("xxx","never ever"+location.getLatitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
