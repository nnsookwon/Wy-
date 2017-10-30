package bigdee2k.wy.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import bigdee2k.wy.R;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;

    /* Facebook login */
    private CallbackManager mCallbackManager;
    private LoginButton loginButton;

    private ProgressBar progressSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        //FirebaseApp.initializeApp(getApplicationContext());

        setContentView(R.layout.activity_sign_in);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        progressSpinner = (ProgressBar)findViewById(R.id.progress_spinner);

        initFacebookLogin();

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

    }

    private void initFacebookLogin() {
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile", "user_friends");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {

        progressSpinner.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.INVISIBLE);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        Task<AuthResult> authResultTask = mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressSpinner.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            loginButton.setVisibility(View.VISIBLE);
                        } else {
                            Log.d("DONE", "Opening new screeN");
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                            finish();
                        }

                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

}
