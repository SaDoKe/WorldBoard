package org.sadoke.worldboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RESTApi {
    private static RESTApi restApi;
    private Context context;
    private RequestQueue queue;
    private String userToken;
    private String serverAddress = "http://h2877718.stratoserver.net:8080/worldboard/%s";

    /**
     * Private Constructor, initilize Object
     * @param context
     * Context of calling instance
     */
    private RESTApi(Context context) {
        this.context = context;
        queue = Volley.newRequestQueue(context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.userToken = prefs.getString("user_token", null);
        if (userToken == null)
            createUser( result -> {
                try {
                    this.userToken = new JSONObject(result).getString("key");
                    prefs.edit().putString("user_token", this.userToken).apply();
                } catch (JSONException e) {
                    Log.e("Error in get user token", e.getMessage());
                }
            });
    }

    /**
     * Creates Static RESTApi Object
     * @param context
     * @return Static instance of RESTApi
     */
    public static RESTApi init(Context context) {
        if (restApi == null)
            restApi = new RESTApi(context);
        restApi.context = context;
        return restApi;
    }

    /**
     * Requests a new User from Server
     * @param callback
     * Callback funktion, used for async Server Answer
     */
    private void createUser(VolleyCallback<String> callback) {
        String url = String.format(serverAddress, "user/create/" + context.getResources().getString(R.string.world_board_api_key));

        StringRequest postalRequest = new StringRequest(Request.Method.POST, url,
                callback::onSuccess,
                error -> Log.e("createUserError", error.toString())
        );

        // Access the RequestQueue
        queue.add(postalRequest);
    }

    /**
     * Sends Actuel Position and gets List of near by Messages
     * @param lat
     * Latitude of user
     * @param lng
     * Longitude of user
     * @param callback
     * Callback funktion, used for async Server Answer
     */
    /**
     * Sends a new message whit the userKey, the message and the location to the Server.
     * @param message
     * @param callback
     * @param longitude
     * @param lattitude
     */
    private void createMessage(String message, VolleyCallback<JSONObject> callback, float longitude,float lattitude){
        String url = String.format(serverAddress, "message/create/" + this.userToken);

        JSONObject jsonBody = new JSONObject();
        JSONObject jsonPosition = new JSONObject();
        try {
            jsonBody.put("message",message);
            jsonPosition.put("longitude",longitude);
            jsonPosition.put("lattitude",lattitude);
            jsonBody.put("position",jsonPosition);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("MessageCreate","Fehler beim erstellen des Bodys in createMessage");
        }
        JsonObjectRequest postalRequest = new JsonObjectRequest( url, jsonBody,
                callback::onSuccess,
                error -> Log.e("sendError", "ein Oopsi:"+error.toString())
        );
    }

    public void getNextMessage(Float lat, Float lng, VolleyCallback<JSONObject> callback) {
       String url = String.format(serverAddress, "message/next/" + userToken + "?lattitude=" + lat + "&longitude=" + lng);

         StringRequest postalRequest = new StringRequest(Request.Method.GET, url,
                 response -> {
                     try {
                         callback.onSuccess(new JSONObject(response));
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }
                 },
                 error -> Log.e("sendError", error.toString())
         );

        // Access the RequestQueue
        queue.add(postalRequest);
    }
}
