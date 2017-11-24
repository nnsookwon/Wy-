package bigdee2k.wy.activities;

/**
 * Created by Nate on 11/18/17.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import bigdee2k.wy.R;
import bigdee2k.wy.models.Notification;

/**
 * Created by Pengqi on 11/18/2017.
 */

public class LocationReceivedActivity extends AppCompatActivity {

    private String my_id;
    private String friend_id;
    private double longitude, latitude;
    private String imageUrl;
    private String key;

    private DatabaseReference mDatabase;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.received_location);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent callingIntent = getIntent();

        key = callingIntent.getStringExtra("key");

        mDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Notification notification = dataSnapshot.getValue(Notification.class);

                imageUrl = notification.getImageUrl();
                latitude = notification.getLatitude();
                longitude = notification.getLongitude();

                if (!imageUrl.isEmpty()) {
                    ImageView imageView = (ImageView) findViewById(R.id.image_view);
                    Bitmap imageBitmap = decodeFromFirebaseBase64(imageUrl);
                    imageView.setImageBitmap(imageBitmap);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*
        my_id = callingIntent.getStringExtra("receiver_id");
        friend_id = callingIntent.getStringExtra("sender_id");

        longitude = callingIntent.getDoubleExtra("longitude", 0);
        latitude = callingIntent.getDoubleExtra("latitude", 0);
        imageUrl = callingIntent.getStringExtra("imageUrl");
        */





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

    public static Bitmap decodeFromFirebaseBase64(String image) {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

}
