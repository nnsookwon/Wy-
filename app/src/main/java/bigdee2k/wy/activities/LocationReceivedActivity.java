package bigdee2k.wy.activities;

/**
 * Created by Nate on 11/18/17.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import bigdee2k.wy.R;

/**
 * Created by Pengqi on 11/18/2017.
 */

public class LocationReceivedActivity extends AppCompatActivity {

    private String my_id;
    private String friend_id;
    private double longitude, latitude;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.received_location);


        Intent callingIntent = getIntent();

        my_id = callingIntent.getStringExtra("receiver_id");
        friend_id = callingIntent.getStringExtra("sender_id");

        longitude = callingIntent.getDoubleExtra("longitude", 0);
        latitude = callingIntent.getDoubleExtra("latitude", 0);

        System.out.println("*********lolol" + longitude);


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void rejectRequest(View v) {

    }

    public void openMap(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setData(Uri.parse("geo:0,0?q=" + latitude + "," + longitude));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
