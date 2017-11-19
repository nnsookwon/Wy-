package bigdee2k.wy.models;

/**
 * Created by Pengqi on 11/18/2017.
 */

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class Notification {

    private String sender_user_id;
    private String receiver_user_id;
    private String message;
    private String description;
    private String type;
    private long timestamp, status;
    private boolean request;
    private boolean reject;
    private double longitude, latitude;
    private String imageUrl;

    public Notification() {
    }

    public String getSender_user_id() {
        return sender_user_id;
    }

    public void setSender_user_id(String sender_user_id) {
        this.sender_user_id = sender_user_id;
    }

    public String getReceiver_user_id() {
        return receiver_user_id;
    }

    public void setReceiver_user_id(String receiver_user_id) {
        this.receiver_user_id = receiver_user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public boolean isRequest() {
        return request;
    }

    public boolean isReject() {
        return reject;
    }

    public void setRequest(boolean request) {
        this.request = request;
    }

    public void setReject (boolean reject) {
        this.reject = reject;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", message);
        result.put("description", description);
        result.put("timestamp", ServerValue.TIMESTAMP);
        result.put("type",type);
        result.put("status",status);
        result.put("sender_user_id", sender_user_id);
        result.put("receiver_user_id", receiver_user_id);
        result.put("request", request);
        result.put("longitude", longitude);
        result.put("latitude", latitude);
        result.put("imageUrl", imageUrl);
        return result;
    }


}
