package org.sadoke.worldboard;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import static java.lang.Math.*;

public class Interpreter {
    int MULTIPLIKATOR = 1000;
    private static Interpreter interpreter;

    private Interpreter(){
    }

    public static Interpreter getInterpreter(){
        if (interpreter == null)
            interpreter = new Interpreter();
        return interpreter;
    }

    /**
     * Returns the direction of the needle.
     * @return degree
     */
    @Deprecated
    public float degreeNord(SensorEvent event){
       return round(event.values[0]);
    };

    public float degreeNord(SensorEvent mGravityEvent, SensorEvent mGeomagneticEvent){
        final float ALPHA = 0.97f;
        float[] mGravity = new float[3];
        float[] mGeomagnetic = new float[3];
        float[] R = new float[9];
        float[] I = new float[9];
        float azimuth;
        float azimuthFix=0;

        mGravity[0] = ALPHA * mGravity[0] + (1 - ALPHA)
                * mGravityEvent.values[0];
        mGravity[1] = ALPHA * mGravity[1] + (1 - ALPHA)
                * mGravityEvent.values[1];
        mGravity[2] = ALPHA * mGravity[2] + (1 - ALPHA)
                * mGravityEvent.values[2];

        mGeomagnetic[0] = ALPHA * mGeomagnetic[0] + (1 - ALPHA)
                * mGeomagneticEvent.values[0];
        mGeomagnetic[1] = ALPHA * mGeomagnetic[1] + (1 - ALPHA)
                * mGeomagneticEvent.values[1];
        mGeomagnetic[2] = ALPHA * mGeomagnetic[2] + (1 - ALPHA)
                * mGeomagneticEvent.values[2];

        boolean success = SensorManager.getRotationMatrix(R, I, mGravity,
                mGeomagnetic);

        float orientation[] = new float[3];
        SensorManager.getOrientation(R, orientation);
        azimuth = (float) Math.toDegrees(orientation[0]); // orientation
        azimuth = (azimuth + azimuthFix + 360) % 360;
        return azimuth;
    }

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
        JSONObject accelerometerJSON = new JSONObject();
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


