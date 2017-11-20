package bigdee2k.wy.activities;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;

import bigdee2k.wy.R;
import bigdee2k.wy.reusables.Utilities;

/**
 * Created by Pengqi on 11/18/2017.
 */

public class SendLocationActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    private FusedLocationProviderClient mFusedLocationClient;
    private String imageUrl;
    private String my_id;
    private String my_name;
    private String friend_id;

    private SharedPreferences prefs;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_location);

        prefs = getSharedPreferences("wya_pref", MODE_PRIVATE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Intent callingIntent = getIntent();

        imageUrl = "";
        my_id = callingIntent.getStringExtra("receiver_id");
        friend_id = callingIntent.getStringExtra("sender_id");
        my_name = prefs.getString("my_name", "Your friend");

        if (callingIntent.hasExtra("sendLocation")) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(01);
            if (callingIntent.getBooleanExtra("sendLocation", false)) {
                // user clicked accept
               sendLocation();

            }
            else {
                // user clicked decline
                rejectRequest(null);

            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void rejectRequest(View v) {
        Utilities.sendRejectNotification(getApplicationContext(),
                my_id,
                friend_id,
                my_name,
                "new_notification"
        );
    }

    public void acceptRequest(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Would you like to also send a picture of your surroundings?")
                .setCancelable(false)
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendLocation();
                        dialog.cancel();
                    }
                })
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        launchCamera();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Pics or nah?");
        alert.show();
    }

    public void sendLocation() {
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
                                imageUrl);

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
                    sendLocation();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    // Check permissions before calling this function
    public void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // mImageLabel.setImageBitmap(imageBitmap);
            imageUrl= encodeBitmapAndSaveToFirebase(imageBitmap);
            sendLocation();
        }
    }

    public String encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        return imageEncoded;
    }
}
