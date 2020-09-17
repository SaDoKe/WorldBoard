package org.sadoke.worldboard;

import android.hardware.SensorEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import static java.lang.Math.*;

public class Interpreter {
    int MULTIPLIKATOR = 1000;
    private Interpreter interpreter;

    private Interpreter(){
    }
    public Interpreter getInterpreter(){
        return interpreter;
    }

    /**
     * Returns the direction of the needle.
     * @return degree
     */
    public float degreeNord(SensorEvent event){
       return round(event.values[0]);
    };

    /**
     * Returns the accelerometer fields in a JSON object.
     * like
     * {
     *      "x_Axis":"13,37",
     *      "y_Axis":"13,37",
     *      "z_Axis":"13,37"
     * } in m/(s^2)
     * @param event
     * @return
     */
    public JSONObject accelerometer (SensorEvent event){
        JSONObject accelerometerJSON= new JSONObject();
        try {
            accelerometerJSON.put("x_Axis",event.values[0]);
            accelerometerJSON.put("y_Axis",event.values[1]);
            accelerometerJSON.put("z_Axis",event.values[2]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return accelerometerJSON;
    }


    //TODO: testen
    /**ACHTUNG, KANN GERNE NOCH GETESTET WERDEN
     *
     * @param accList
     * @param multiplier
     * @param locVor [latitude,longitude]
     * @param locNach [latitude,longitude]
     * @return
     */
    public JSONObject movementVector(ArrayList<Float> accList, int multiplier, float[] locVor,float[] locNach) throws JSONException {
        int length = (accList.size()/3);
        JSONArray jsons = new JSONArray();
        double movementVektor;
        float latDist,lngtDist;
        JSONObject request = new JSONObject();
        JSONObject acc = new JSONObject();

        latDist = (locNach[0]-locVor[0])*MULTIPLIKATOR;
        lngtDist = (locNach[0]-locVor[0])*MULTIPLIKATOR;
        movementVektor =  sqrt(pow(latDist,2)+pow(lngtDist,2));

        //ACHTUNG: KP OB DAS FUNZT
        for(int i=3;i<=length*3;i=i+3){
            jsons.put(new JSONObject().put("accelX",accList.get(i-3)).put("accelY",accList.get(i-2)).put("accelZ",accList.get(i-1)));
        }

        request.put("accel",jsons).put("movementVector",movementVektor);
        return request;
    }

    /**
     * Gives back the x and y Axis in JSON back.
     * like
     * {
     *     x_Axis:"",
     *     y_Axis:""
     * }
     * @param event
     * @return
     */
    public JSONObject location(SensorEvent event){
        JSONObject locationJSON= new JSONObject();
        try {
            locationJSON.put("x_Axis",event.values[0]);
            locationJSON.put("y_Axis",event.values[1]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return locationJSON;
    }
}


