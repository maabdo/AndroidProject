package com.example.imane.sherrymap;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.security.PublicKey;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    String TAG = "PHONE";
    TextView redirectRegistration;
    EditText email;
    EditText password;
    Button login;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private InterstitialAd interstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setInterface();
        MobileAds.initialize(this,"ca-app-pub-1666125859780363~2422212420");
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("ca-app-pub-1666125859780363/1597065972").build();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-1666125859780363/1597065972");
        interstitialAd.loadAd(new AdRequest.Builder().build());

       interstitialAd.setAdListener(new AdListener()
                                    {
                                       /* @Override
                                         public void onAdLoaded() {
                                            if (interstitialAd.isLoaded()) {
                                                interstitialAd.show();
                                            }
                                        } */

                                        @Override
                                        public void onAdClosed() {
                                            startActivity(new Intent(MainActivity.this, LocationActivity.class));
                                            interstitialAd.loadAd(new AdRequest.Builder().build());
                                        }
                                    }
       );
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });
        redirectRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            }
        });

    }
    private void setInterface(){
        email = (EditText) findViewById(R.id.etmail);
        password = (EditText) findViewById(R.id.etPassword);
        login= (Button) findViewById(R.id.btnLogin);
        redirectRegistration = findViewById(R.id.tvRedirectRegistration);
    }

    private void connect(){
            firebaseAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Login succesful", Toast.LENGTH_SHORT).show();
                        if (interstitialAd.isLoaded())
                            interstitialAd.show();
                        else
                        startActivity(new Intent(MainActivity.this, LocationActivity.class));
                    } else
                        Toast.makeText(getApplicationContext(), "Login failed, please try again", Toast.LENGTH_SHORT).show();

                }
            });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
