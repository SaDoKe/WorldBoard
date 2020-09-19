package org.sadoke.worldboard;

import android.os.Build;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

/**
 * Message Object containing all information from Server
 */
public class Message {
    // JSONObject from Server
    private JSONObject jsonObject;
    // Messages Author
    private JSONObject author;
    // Messages Position
    private JSONObject position;
    // Message Image
    private ImageView imageView;

    /**
     * Constructs Message from JSONObject
     *
     * @param jsonObject JSONObject from Server
     * @throws JSONException
     */
    public Message(JSONObject jsonObject, MainActivity mainActivity) throws JSONException {
        this.jsonObject = jsonObject;
        this.author = jsonObject.getJSONObject("author");
        this.position = jsonObject.getJSONObject("position");
        this.imageView = new ImageView(mainActivity);
        //setting image resource
        imageView.setImageResource(R.drawable.message);
        //setting image position
        imageView.setLayoutParams(
                new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT
                )
        );
        imageView.setTranslationX(0);
        imageView.setTranslationY(0);
    }

    /**
     * Messages ID
     *
     * @return ID as Int
     * @throws JSONException
     */
    public int getMessageId() throws JSONException {
        return jsonObject.getInt("id");
    }

    /**
     * Messages Message
     *
     * @return Message as String
     * @throws JSONException
     */
    public String getMessage() throws JSONException {
        return jsonObject.getString("message");
    }

    /**
     * Authors API
     *
     * @return API as int
     * @throws JSONException
     */
    public int getApi() throws JSONException {
        return author.getInt("api");
    }

    /**
     * Authors Status
     *
     * @return Status as Int
     * @throws JSONException
     */
    public int getStatus() throws JSONException {
        return author.getInt("status");
    }

    /**
     * Authors Last Update
     *
     * @return Last Update as LocalDateTime
     * @throws JSONException
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDateTime getLastUpdate() throws JSONException {
        return LocalDateTime.parse(author.getString("lastUpdate"));
    }

    /**
     * Authors Counter
     *
     * @return Counter as int
     * @throws JSONException
     */
    public int getCounter() throws JSONException {
        return author.getInt("counter");
    }

    /**
     * Positions Longitude
     *
     * @return Longitude as Double
     * @throws JSONException
     */
    public Double getLongitude() throws JSONException {
        return position.getDouble("longitude");
    }

    /**
     * Postitions Latitude
     *
     * @return Latitude as Double
     * @throws JSONException
     */
    public Double getLattitude() throws JSONException {
        return position.getDouble("lattitude");
    }

    /**
     * Positions ID
     *
     * @return ID as Int
     * @throws JSONException
     */
    public int getPositionId() throws JSONException {
        return position.getInt("id");
    }

    /**
     * ImageView for UI
     * @return ImageView
     */
    public ImageView getImageView() {
        return imageView;
    }
}
