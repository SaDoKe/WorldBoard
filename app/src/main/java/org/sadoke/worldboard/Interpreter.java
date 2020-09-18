package org.sadoke.worldboard;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.*;

public class Interpreter {
    int MULTIPLIKATOR = 1000;
    float azimuth = 0;

    private static Interpreter interpreter;

    private Interpreter() {
    }

    public static Interpreter getInterpreter() {
        if (interpreter == null)
            interpreter = new Interpreter();
        return interpreter;
    }

    /**
     * Returns the direction of the needle.
     *
     * @return degree
     */
    @Deprecated
    public float degreeNord(SensorEvent event) {
        return round(event.values[0]);
    }

    ;

    /**
     * Returns the direction of the needle.
     *
     * @param mGravityEvent
     * @param mGeomagneticEvent
     * @return
     */
    public float degreeNord(SensorEvent mGravityEvent, SensorEvent mGeomagneticEvent) {
        final float ALPHA = 0.97f;
        float[] mGravity = new float[3];
        float[] mGeomagnetic = new float[3];
        float[] R = new float[9];
        float[] I = new float[9];

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
        Log.e("d", Arrays.toString(R));
        if (success) {
            float orientation[] = new float[3];
            SensorManager.getOrientation(R, orientation);
            azimuth = (float) Math.toDegrees(orientation[0]); // orientation
            azimuth = (azimuth  + 360) % 360;
            return azimuth;
        }
        return azimuth;
    }

    public float degreeNord(SensorEvent mRotationEvent, SensorEvent mGravityEvent, SensorEvent mGeomagneticEvent) {
        int mAzimuth;
        float[] rMat = new float[9];
        float[] orientation = new float[9];
        float[] mLastMagnetometer = new float[3];
        float[] mLastAccelerometer = new float[3];

        SensorManager.getRotationMatrixFromVector(rMat, mRotationEvent.values);
        System.arraycopy(mGravityEvent.values, 0, mLastAccelerometer, 0, mGravityEvent.values.length);
        System.arraycopy(mGravityEvent.values, 0, mLastMagnetometer, 0, mGravityEvent.values.length);
        SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
        SensorManager.getOrientation(rMat, orientation);
        mAzimuth = (int) ((Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360);

        mAzimuth = Math.round(mAzimuth);
        return -mAzimuth-90;
    }

    /**
     * Returns the accelerometer fields in a JSON object.
     * like
     * {
     * "x_Axis":"13,37",
     * "y_Axis":"13,37",
     * "z_Axis":"13,37"
     * } in m/(s^2)
     *
     * @param event
     * @return
     */
    public JSONObject accelerometer(SensorEvent event) {
        JSONObject accelerometerJSON = new JSONObject();
        try {
            accelerometerJSON.put("x_Axis", event.values[0]);
            accelerometerJSON.put("y_Axis", event.values[1]);
            accelerometerJSON.put("z_Axis", event.values[2]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return accelerometerJSON;
    }


    //TODO: testen

    /**
     * ACHTUNG, KANN GERNE NOCH GETESTET WERDEN
     *
     * @param accList
     * @param locVor  [latitude,longitude]
     * @param locNach [latitude,longitude]
     * @return
     */
    public JSONObject movementVector(ArrayList<Float> accList, int multiplier, double[] locVor, double[] locNach) throws JSONException {
        int length = (accList.size() / 3);
        JSONArray jsons = new JSONArray();
        double movementVektor;
        double latDist, lngtDist;

        JSONObject request = new JSONObject();

        latDist = (locNach[0] - locVor[0]) * MULTIPLIKATOR;
        lngtDist = (locNach[0] - locVor[0]) * MULTIPLIKATOR;
        movementVektor = sqrt(pow(latDist, 2) + pow(lngtDist, 2));

        //ACHTUNG: KP OB DAS FUNZT
        for (int i = 3; i <= length * 3; i = i + 3) {
            jsons.put(new JSONObject().put("accelX", accList.get(i - 3)).put("accelY", accList.get(i - 2)).put("accelZ", accList.get(i - 1)));
        }

        request.put("accel", jsons).put("movementVector", movementVektor);
        return request;
    }

    /**
     * Gives back the x and y Axis in JSON back.
     * like
     * {
     * x_Axis:"",
     * y_Axis:""
     * }
     *
     * @param event
     * @return
     */
    public JSONObject location(SensorEvent event) {
        JSONObject locationJSON = new JSONObject();
        try {
            locationJSON.put("x_Axis", event.values[0]);
            locationJSON.put("y_Axis", event.values[1]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return locationJSON;
    }

    /**
     * [Latitude,longitude]
     * @param lokU
     * @param lokM
     * @return
     */
    public float degreeToMessage(double lokU[], double lokM[]) {
            double lokNordpol[] = new double[2];
            float degree;
            double rad;

            lokNordpol[0] = 90;
            lokNordpol[1] = 0;
            double vektorNord[] = new double[2];
            vektorNord[0] = lokNordpol[0]-lokU[0];
            vektorNord[1] = lokNordpol[1]-lokU[1];
            double vektorMessage[] = new double[2];
            vektorMessage[0] = lokM[0]-lokU[0];
            vektorMessage[1] = lokM[1]-lokU[1];

            rad = Math.acos((vektorNord[0]*vektorMessage[0]+vektorNord[1]*vektorMessage[1])/(Math.sqrt(vektorNord[0]*vektorNord[0]+vektorNord[1]*vektorNord[1]))*Math.sqrt(vektorMessage[0]*vektorMessage[0]+vektorMessage[1]*vektorMessage[1]));
            degree = (float) Math.toDegrees(rad);

            return degree;
    }
}


