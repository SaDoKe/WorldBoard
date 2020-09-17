package org.sadoke.worldboard.sensormanager;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.sadoke.worldboard.MainActivity;

import java.util.LinkedList;
import java.util.List;

public class JSONSensorDataManager {
    private List<JSONSensorData> sensorList;
    private MainActivity mainActivity;
    private boolean keepRunning;
    private Thread thread;
    private int interval = 1000;
    private boolean debug = false;
    private OnSensorDataChangeListener onSensorDataChangeListener;

    public JSONSensorDataManager(MainActivity mainActivity, OnSensorDataChangeListener onSensorDataChangeListener){
        this.mainActivity = mainActivity;
        this.onSensorDataChangeListener = onSensorDataChangeListener;
        sensorList = new LinkedList<>();
    }

    private Runnable loggingRunnable = new Runnable() {
        @Override
        public void run() {
            onSensorDataChangeListener.onSensorDataChanged();
        }
    };

    public void startLogging(){
        if(thread == null || !thread.isAlive()){

            thread = new Thread(){
                @Override
                public void run(){
                    keepRunning = true;

                    while(keepRunning) {
                        mainActivity.runOnUiThread(loggingRunnable);
                        try {
                            sleep(interval);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(debug) Log.e("Thread running?", String.valueOf(thread.isAlive()));
                    }
                    if(debug) Log.e("Thread running?", String.valueOf(thread.isAlive()));
                }
            };
            thread.start();
            onSensorDataChangeListener.setLogRecord("Sensor Data Manager Started");
        }
    }

    public void stopLogging(){
        if (thread != null && thread.isAlive()) {
            keepRunning = false;

            if(debug) Log.e("Thread running?", String.valueOf(thread.isAlive()));
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            thread.interrupt();

            if(debug) Log.e("Thread running?", String.valueOf(thread.isAlive()));
        }
    }

    /**
     * adds a sensor with interface: JSONSensorData
     * @param sensor
     */
    public void addSensor(JSONSensorData sensor){
        if(!sensorList.contains(sensor)){
            this.sensorList.add(sensor);
            if(debug) Log.e("addSensor", "new sensor");
        }else{
            if(debug) Log.e("addSensor", "sensor already in list");
        }

        if(debug) Log.i("sensor List size = ", ""+this.sensorList.size());
    }

    /**
     * removes a sensor
     * @param sensor
     */
    public void removeSensor(JSONSensorData sensor){
        this.sensorList.remove(sensor);
    }

    /**
     * returns a single JSON object with all data of all added de.dmmm.lmapraktikum.sensors
     * @return JSONObject
     */
    public JSONObject getData(){
        JSONObject data = new JSONObject();

        try {
            data.put("timestamp", TimestampCreator.createTimestampString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(JSONSensorData jsonData : this.sensorList){
            try {
                data.put(jsonData.getSensorName(), jsonData.getData());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public void setInterval(int interval){
        this.interval = interval;
    }

    public int getInterval(){
        return this.interval;
    }
}
