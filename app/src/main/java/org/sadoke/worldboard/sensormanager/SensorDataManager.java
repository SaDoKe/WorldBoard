package org.sadoke.worldboard.sensormanager;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

import androidx.lifecycle.ViewModelProvider;

import org.sadoke.worldboard.Interpreter;
import org.sadoke.worldboard.MainActivity;
import org.sadoke.worldboard.ui.main.MainViewModel;

import static android.content.Context.SENSOR_SERVICE;

public class SensorDataManager implements SensorEventListener {
    private SensorManager sensorManager;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sensorManager.registerListener(SensorDataManager.this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 0);
            sensorManager.registerListener(SensorDataManager.this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 0);
            handler.postDelayed(this, 400);
        }
    };
    private MainViewModel mainViewModel;
    private int windows = 0;
    private Interpreter interpreter = Interpreter.getInterpreter();

    public SensorDataManager(MainActivity mainActivity, MainViewModel mainViewModel) {
        this.sensorManager = (SensorManager) mainActivity.getSystemService(SENSOR_SERVICE);
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

    @Override
    public synchronized void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                mGravity = sensorEvent;
                if (mGravity != null && mGeomagnetic != null)
                    mainViewModel.setDegree(interpreter.degreeNord(mGravity, mGeomagnetic));
                break;
            case Sensor.TYPE_ACCELEROMETER:
                mGeomagnetic = sensorEvent;
                interpreter.accelerometer(sensorEvent);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
