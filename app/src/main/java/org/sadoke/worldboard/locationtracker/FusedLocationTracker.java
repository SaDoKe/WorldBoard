package org.sadoke.worldboard.locationtracker;

import android.annotation.SuppressLint;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import org.sadoke.worldboard.MainActivity;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class FusedLocationTracker extends LocationCallback {
    LocationRequest locationRequest = new LocationRequest();
    FusedLocationProviderClient fusedLocationProviderClient;

    public FusedLocationTracker(MainActivity mainActivity) {
        super();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(400);
        locationRequest.setFastestInterval(400);
        fusedLocationProviderClient = getFusedLocationProviderClient(mainActivity);
    }

    @SuppressLint("MissingPermission")
    public void startTracking() {
        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                this,
                Looper.myLooper()
        );
    }

    public void stopTracking() {
        fusedLocationProviderClient.removeLocationUpdates(this);
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
    }
}
