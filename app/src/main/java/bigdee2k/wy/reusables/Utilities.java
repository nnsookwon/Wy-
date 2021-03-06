package bigdee2k.wy.reusables;

import android.content.Context;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

import bigdee2k.wy.models.Notification;

public class Utilities {

    public static void sendRejectNotification(final Context context, String sender_user_id, String receiver_user_id, String sender_name, String type){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notifications").child(receiver_user_id);
        String pushKey = databaseReference.push().getKey();
        Notification notification = new Notification();

        notification.setDescription("Request Declined");
        notification.setMessage(sender_name + " has rejected your WY@ request");
        notification.setSender_user_id(sender_user_id);
        notification.setReceiver_user_id(receiver_user_id);
        notification.setType(type);
        notification.setRequest(false);
        notification.setReject(true);

        Map<String, Object> forumValues = notification.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(pushKey, forumValues);
        databaseReference.setPriority(ServerValue.TIMESTAMP);
        databaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError == null){
                    Toast.makeText(context,"Request Rejected",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static void sendRequestNotification(final Context context, String sender_user_id, String receiver_user_id, String message, String description, String type){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notifications").child(receiver_user_id);
        String pushKey = databaseReference.push().getKey();

        Notification notification = new Notification();
        notification.setDescription(description);
        notification.setMessage(message);
        notification.setSender_user_id(sender_user_id);
        notification.setReceiver_user_id(receiver_user_id);
        notification.setType(type);
        notification.setRequest(true);
        notification.setReject(false);


        Map<String, Object> forumValues = notification.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(pushKey, forumValues);
        databaseReference.setPriority(ServerValue.TIMESTAMP);
        databaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError == null){
                    Toast.makeText(context,"Notification sent",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static void sendLocationNotification(final Context context, String sender_user_id, String receiver_user_id,
                                                String message, String description, String type,
                                                double longitude, double latitude, String imageUrl){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notifications").child(receiver_user_id);
        String pushKey = databaseReference.push().getKey();

        Notification notification = new Notification();
        notification.setDescription(description);
        notification.setMessage(message);
        notification.setSender_user_id(sender_user_id);
        notification.setReceiver_user_id(receiver_user_id);
        notification.setType(type);
        notification.setRequest(false);
        notification.setReject(false);
        notification.setLongitude(longitude);
        notification.setLatitude(latitude);
        notification.setImageUrl(imageUrl);


        Map<String, Object> forumValues = notification.toMap();


        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(pushKey, forumValues);
        databaseReference.setPriority(ServerValue.TIMESTAMP);
        databaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError == null){
                    Toast.makeText(context,"Location sent",Toast.LENGTH_LONG).show();
                }
                else {

                    Toast.makeText(context,databaseError.getMessage() + "," + databaseError.getDetails(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}