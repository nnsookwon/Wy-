package bigdee2k.wy.activities;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import bigdee2k.wy.R;
import bigdee2k.wy.reusables.Utilities;

/**
 * Created by Pengqi on 11/18/2017.
 */

public class SendLocationActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    private FusedLocationProviderClient mFusedLocationClient;

    private String my_id;
    private String friend_id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.send_location);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Intent callingIntent = getIntent();

        my_id = callingIntent.getStringExtra("receiver_id");
        friend_id = callingIntent.getStringExtra("sender_id");
        

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void rejectRequest(View v) {
        finish();
    }

    public void sendLocation(View v) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);

            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();

                        Utilities.sendLocationNotification(getApplicationContext(),
                                my_id,
                                friend_id,
                                "Click to find your friend",
                                "Location received",
                                "new_notification",
                                location.getLongitude(),
                                location.getLatitude(),
                                "");

                        finish();


                    }
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, try again
                    sendLocation(null);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
