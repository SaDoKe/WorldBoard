package de.dmmm.lmapraktikum.sensors;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.json.JSONObject;

import de.dmmm.lmapraktikum.MainActivity;
import de.dmmm.lmapraktikum.sensorManager.JSONSensorData;

public abstract class Sensor implements JSONSensorData {

    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private JSONObject data;
    private int sensorType;
    private int sensorDelay;
    private boolean sensorActive;
    private MainActivity mainActivity;

    public Sensor(MainActivity mainActivity, int sensorType, int sensorDelay){

        this.sensorType = sensorType;
        this.sensorDelay = sensorDelay;
        this.sensorActive = false;
        this.mainActivity = mainActivity;
        this.init();
    }

    protected abstract JSONObject createJSON(SensorEvent event);

    private void init(){

        sensorManager = (SensorManager) mainActivity.getSystemService(Context.SENSOR_SERVICE);

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                if(event.sensor.getType() == sensorType){

                    data = createJSON(event);
                }
            }

            @Override
            public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {

            }
        };
    }

    /**
     * Sensor starts to generate output data
     */
    public void start(){

        if(sensorActive) return;
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(sensorType), sensorDelay);
        sensorActive = true;
    }

    /**
     * Sensor stops to generate output data
     */
    public void stop(){

        if(!sensorActive) return;
        sensorManager.unregisterListener(sensorEventListener);
        sensorActive = false;
    }

    /**
     * returns sensor data as JSONObject
     * @return JSONObject
     */
    @Override
    public JSONObject getData(){

        try {
            if(!sensorActive) throw new Exception();
        }catch (Exception e){
            Log.e("Sensor class", "SENSOR NOT ACTIVATED");
        }

        try {
            if(this.data == null) throw new Exception();
        }catch (Exception e){
            Log.e("Sensor class", "DATA == NULL");
        }

        return this.data;
    }
}
