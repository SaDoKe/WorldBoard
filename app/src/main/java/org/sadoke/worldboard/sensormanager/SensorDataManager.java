package org.sadoke.worldboard.sensormanager;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

import org.sadoke.worldboard.MainActivity;

import static android.content.Context.SENSOR_SERVICE;

public class SensorDataManager implements SensorEventListener {
    private SensorManager sensorManager;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run(){
            sensorManager.registerListener(SensorDataManager.this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 0);
            handler.postDelayed(this, 400);
        }
    };

    public SensorDataManager(MainActivity mainActivity){
        this.sensorManager = (SensorManager) mainActivity.getSystemService(SENSOR_SERVICE);
    }

    public void startLogging(){
        handler.post(runnable);
    }

    public void stopLogging(){
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
