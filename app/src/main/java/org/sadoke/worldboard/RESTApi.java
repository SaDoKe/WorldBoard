package org.sadoke.worldboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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

    private RESTApi(Context context) {
        this.context = context;
        queue = Volley.newRequestQueue(context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String systemUserToken = prefs.getString("user_token", null);
        if (systemUserToken == null) {
            createUser(result -> {
                try {
                    userToken = new JSONObject(result).getString("key");
                    prefs.edit().putString("user_token", userToken).apply();
                } catch (JSONException e) {
                    Log.e("Error in get user token", e.getMessage());
                }
            });
        } else
            this.userToken = systemUserToken;
    }

    public static RESTApi init(Context context) {
        if (restApi == null)
            restApi = new RESTApi(context);
        return restApi;
    }

    private void createUser(VolleyCallback<String> callback) {
        String url = String.format(serverAddress, "user/create/" + context.getResources().getString(R.string.world_board_api_key));

        StringRequest postalRequest = new StringRequest(Request.Method.POST, url,
                callback::onSuccess,
                error -> Log.e("createUserError", error.toString())
        );

        // Access the RequestQueue
        queue.add(postalRequest);
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
