package bigdee2k.wy.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import bigdee2k.wy.R;
import bigdee2k.wy.models.FacebookFriend;
import bigdee2k.wy.models.MyRecyclerAdapter;
import bigdee2k.wy.reusables.Utilities;

public class MainActivity extends AppCompatActivity implements MyRecyclerAdapter.RecyclerViewClickListener {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase mDatabase;

    boolean facebookSDKInitilized;

    ArrayList<FacebookFriend> friends;

    RecyclerView recyclerView;
    MyRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        friends = new ArrayList<>();

        adapter = new MyRecyclerAdapter(friends);
        adapter.setItemListener(this);
        recyclerView = (RecyclerView)findViewById(R.id.friend_list_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();

        checkAuth();
    }

    private void checkAuth() {
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }
        else {
            FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
                @Override
                public void onInitialized() {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        facebookSDKInitilized = true;
                        initFriendsList();
                    }
                }
            });
        }
    }

    private void initFriendsList() {
        //Grab User's friends also using this app
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                    /* handle the result */
                        Log.d("facebook friends:" ,response.toString());
                        try {
                            friends.clear();
                            JSONObject json = response.getJSONObject();
                            JSONArray jArray = json.getJSONArray("data");
                            Log.d("Facebook Json:", jArray.toString());
                            for (int i = jArray.length()-1; i >= 0; i--){
                                JSONObject jsonFriend = jArray.getJSONObject(i);
                                FacebookFriend friend =
                                        new FacebookFriend(jsonFriend.getString("id"),
                                                jsonFriend.getString("name"),
                                                null);
                                friend.setPhotoUrl("https://graph.facebook.com/" +
                                        friend.getId() + "/picture?type=normal");
                                friends.add(friend);
                            }
                            Log.d("facebook friends", friends.toString());
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }
        ).executeAsync();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.button_sign_out:
                logOut(null);
                break;
        }
        return true;
    }

    public void logOut(View view) {
        mFirebaseAuth.signOut(); //sign out of firebase
        LoginManager.getInstance().logOut(); //sign out of facebook
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }

    // JACK TODO: send wya request upon selecting friend
    private void sendNotificationToUser(FacebookFriend friend) {
        Utilities.sendNotification(this,
                friend.getId(),
                "A new notification from " + Profile.getCurrentProfile().getFirstName(),
                "New Notification",
                "new_notification"
        );
    }
    @Override
    public void recyclerViewListClicked(View v, int position) {
        final FacebookFriend friend = friends.get(position);
        System.out.println("User name: " + friend.getUserName() + "\nID: " + friend.getId() + "\n");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to ask " + friend.getUserName() + " WY@?")
                .setCancelable(false)
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendNotificationToUser(friend);
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Are You Sure?");
        alert.show();
    }
}
