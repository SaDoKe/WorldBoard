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
    private Author author;
    // Messages Position
    private Position position;
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
        this.author = new Author();
        this.position = new Position();
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
    public int getId() throws JSONException {
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
     * Messages Author
     *
     * @return Author as Author
     */
    public Author getAuthor() {
        return author;
    }

    /**
     * Messages Position
     *
     * @return Position as Position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * ImageView for UI
     * @return ImageView
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * Author Class
     */
    private class Author {
        /**
         * Authors API
         *
         * @return API as int
         * @throws JSONException
         */
        public int getApi() throws JSONException {
            return jsonObject.getInt("api");
        }

        /**
         * Authors Status
         *
         * @return Status as Int
         * @throws JSONException
         */
        public int getStatus() throws JSONException {
            return jsonObject.getInt("status");
        }

        /**
         * Authors Last Update
         *
         * @return Last Update as LocalDateTime
         * @throws JSONException
         */
        @RequiresApi(api = Build.VERSION_CODES.O)
        public LocalDateTime getLastUpdate() throws JSONException {
            return LocalDateTime.parse(jsonObject.getString("lastUpdate"));
        }

        /**
         * Authors Counter
         *
         * @return Counter as int
         * @throws JSONException
         */
        public int getCounter() throws JSONException {
            return jsonObject.getInt("counter");
        }
    }

    /**
     * Position Class
     */
    private class Position {
        /**
         * Positions Longitude
         *
         * @return Longitude as Double
         * @throws JSONException
         */
        public Double getLongitude() throws JSONException {
            return jsonObject.getDouble("longitude");
        }

        /**
         * Postitions Latitude
         *
         * @return Latitude as Double
         * @throws JSONException
         */
        public Double getLattitude() throws JSONException {
            return jsonObject.getDouble("lattitude");
        }

        /**
         * Positions ID
         *
         * @return ID as Int
         * @throws JSONException
         */
        public int getId() throws JSONException {
            return jsonObject.getInt("id");
        }
    }
}
