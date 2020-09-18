package org.sadoke.worldboard.sensormanager;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Handler;

import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sadoke.worldboard.Interpreter;
import org.sadoke.worldboard.MainActivity;
import org.sadoke.worldboard.locationtracker.FusedLocationTracker;
import org.sadoke.worldboard.ui.main.MainViewModel;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;

public class SensorDataManager implements SensorEventListener {
    private SensorManager sensorManager;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sensorManager.registerListener(SensorDataManager.this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 0);
            sensorManager.registerListener(SensorDataManager.this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 0);
            sensorManager.registerListener(SensorDataManager.this, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), 0);

            handler.postDelayed(this, 400);
        }
    };
    private MainActivity mainActivity;
    private MainViewModel mainViewModel;
    private Interpreter interpreter = Interpreter.getInterpreter();

    public SensorDataManager(MainActivity mainActivity, MainViewModel mainViewModel) {
        this.sensorManager = (SensorManager) mainActivity.getSystemService(SENSOR_SERVICE);
        this.mainActivity = mainActivity;
        this.mainViewModel = mainViewModel;
    }

    public void startLogging() {
        handler.post(runnable);
    }

    public void stopLogging() {
        handler.removeCallbacks(runnable);
    }

    SensorEvent mGravity;
    SensorEvent mGeomagnetic;
    SensorEvent mRotationVector;

    int windowCounter = 0;
    Location mLocation;
    ArrayList<Float> windows = new ArrayList<>();

    @Override
    public synchronized void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                mGravity = sensorEvent;
                break;
            case Sensor.TYPE_ACCELEROMETER:
                mGeomagnetic = sensorEvent;

                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                mRotationVector = sensorEvent;
                break;
        }
        if (mGravity != null && mGeomagnetic != null)
            mainViewModel.setDegree(interpreter.degreeNord(mRotationVector, mGravity, mGeomagnetic));

        if (windowCounter == 0)
            mLocation = mainActivity.fusedLocationTracker.getLastLocation();
        if (mLocation != null) {
            windows.add(sensorEvent.values[0]);
            windows.add(sensorEvent.values[1]);
            windows.add(sensorEvent.values[2]);
            if (windowCounter == 5) {
                Location loc = mainActivity.fusedLocationTracker.getLastLocation();
                try {
                    mainActivity.sendLogs(interpreter.movementVector(windows, 1000, new double[]{mLocation.getLatitude(), mLocation.getLongitude()}, new double[]{loc.getLatitude(), loc.getLongitude()}));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                windows = new ArrayList<>();
            }
            windowCounter++;
            if (windowCounter > 5) windowCounter = 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
