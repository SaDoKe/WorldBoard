package de.dmmm.lmapraktikum.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import org.json.JSONException;
import org.json.JSONObject;

import de.dmmm.lmapraktikum.MainActivity;

public class Gyroscope extends de.dmmm.lmapraktikum.sensors.Sensor {

    public Gyroscope(MainActivity mainActivity) {

        super(mainActivity, Sensor.TYPE_GYROSCOPE, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected JSONObject createJSON(SensorEvent event){

        JSONObject data = new JSONObject();

        try {
            data.put("x",  event.values[0]);
            data.put("y",  event.values[1]);
            data.put("z",  event.values[2]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    public String getSensorName() {
        return "Gyroscope";
    }
}
