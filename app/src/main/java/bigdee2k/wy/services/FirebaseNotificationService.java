package bigdee2k.wy.services;

/**
 * Created by Pengqi on 11/18/2017.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;

import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import bigdee2k.wy.R;
import bigdee2k.wy.activities.LocationReceivedActivity;
import bigdee2k.wy.activities.MainActivity;
import bigdee2k.wy.activities.SendLocationActivity;
import bigdee2k.wy.models.Notification;


public class FirebaseNotificationService extends Service {

    SharedPreferences sharedPreferences;
    public FirebaseDatabase mDatabase;
    FirebaseAuth firebaseAuth;
    Context context;
    static String TAG = "FirebaseService";

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        setupNotificationListener();
    }


    private boolean alReadyNotified(String key){
        if(sharedPreferences.getBoolean(key,false)){
            return true;
        }else{
            return false;
        }
    }


    private void saveNotificationKey(String key){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,true);
        editor.commit();
    }

    private void setupNotificationListener() {
        System.out.print(Profile.getCurrentProfile().getId());
        mDatabase.getReference().child("notifications")
                .child(Profile.getCurrentProfile().getId())
                .orderByChild("status").equalTo(0)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot != null){
                            Notification notification = dataSnapshot.getValue(Notification.class);
                            if (notification.isRequest())
                                showRequestNotification(context,notification,dataSnapshot.getKey());
                            else if (notification.isReject()){
                                showRejectNotification(context,notification,dataSnapshot.getKey());
                            }
                            else
                                showLocationNotification(context,notification,dataSnapshot.getKey());
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(new Intent(getApplicationContext(), FirebaseNotificationService.class));
    }

    private void showRequestNotification(Context context, Notification notification, String notification_key){
        flagNotificationAsSent(notification_key);

        Intent backIntent = new Intent(context, SendLocationActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        backIntent.putExtra("sender_id", notification.getSender_user_id());
        backIntent.putExtra("receiver_id", notification.getReceiver_user_id());

        Intent acceptIntent = new Intent(context, SendLocationActivity.class);
        acceptIntent.putExtras(backIntent);
        acceptIntent.putExtra("sendLocation", true);

        Intent declineIntent = new Intent(context, SendLocationActivity.class);
        declineIntent.putExtras(backIntent);
        declineIntent.putExtra("sendLocation", false);



        Intent intent = new Intent(context, MainActivity.class);

        /*  Use the notification type to switch activity to stack on the main activity*/
        if(notification.getType().equals("chat_view")){
            intent = new Intent(context, MainActivity.class);
        }

        final PendingIntent pendingIntent = PendingIntent.getActivities(context, 900,
                new Intent[] {backIntent}, PendingIntent.FLAG_ONE_SHOT);


        final PendingIntent pendingAcceptIntent = PendingIntent.getActivities(context, 901,
                new Intent[] {acceptIntent}, PendingIntent.FLAG_ONE_SHOT);


        final PendingIntent pendingDeclineIntent = PendingIntent.getActivities(context, 902,
                new Intent[] {declineIntent}, PendingIntent.FLAG_ONE_SHOT);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notification.getDescription())
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentText(Html.fromHtml(notification.getMessage()
                ))
                .addAction(R.drawable.question_mark, "ACCEPT", pendingAcceptIntent)
                .addAction(R.drawable.question_mark, "DECLINE", pendingDeclineIntent)
                .setAutoCancel(true)
                .setOngoing(true);

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager =  (NotificationManager)context. getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    private void showRejectNotification(Context context, Notification notification, String notification_key){
        flagNotificationAsSent(notification_key);

        Intent backIntent = new Intent(context, SendLocationActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        backIntent.putExtra("sender_id", notification.getSender_user_id());
        backIntent.putExtra("receiver_id", notification.getReceiver_user_id());

        Intent intent = new Intent(context, MainActivity.class);

        /*  Use the notification type to switch activity to stack on the main activity*/
        if(notification.getType().equals("chat_view")){
            intent = new Intent(context, MainActivity.class);
        }

        final PendingIntent pendingIntent = PendingIntent.getActivities(context, 900,
                new Intent[] {backIntent}, PendingIntent.FLAG_ONE_SHOT);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notification.getDescription())
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentText(Html.fromHtml(notification.getMessage()
                ))
                .setAutoCancel(true);

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager =  (NotificationManager)context. getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    private void showLocationNotification(Context context, Notification notification, String notification_key){
        flagNotificationAsSent(notification_key);

        Intent backIntent = new Intent(context, LocationReceivedActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        backIntent.putExtra("sender_id", notification.getSender_user_id());
        backIntent.putExtra("receiver_id", notification.getReceiver_user_id());
        backIntent.putExtra("longitude", notification.getLongitude());
        backIntent.putExtra("latitude", notification.getLatitude());
        backIntent.putExtra("imageUrl", notification.getImageUrl());


        System.out.println("*@@@@@@@@*lolol" + notification.getLongitude());


        Intent intent = new Intent(context, MainActivity.class);

        /*  Use the notification type to switch activity to stack on the main activity*/
        if(notification.getType().equals("chat_view")){
            intent = new Intent(context, MainActivity.class);
        }

        final PendingIntent pendingIntent = PendingIntent.getActivities(context, 905,
                new Intent[] {backIntent}, PendingIntent.FLAG_ONE_SHOT);



        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notification.getDescription())
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentText(Html.fromHtml(notification.getMessage()
                ))
                .setAutoCancel(true)
                .setOngoing(true);

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager =  (NotificationManager)context. getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    private void flagNotificationAsSent(String notification_key) {
        mDatabase.getReference().child("notifications")
                .child(Profile.getCurrentProfile().getId())
                .child(notification_key)
                .child("status")
                .setValue(1);
    }

}
