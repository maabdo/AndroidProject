package com.example.imane.sherrymap;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.sax.StartElementListener;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.AndroidException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.common.images.internal.LoadingImageView;
import com.google.android.gms.common.internal.Objects;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class LocationActivity extends AppCompatActivity {
    TextView genAdress;
    TextView showAdress;
    Button afficherAdresse;
    Button trouverAdresse;
    ProgressBar progressBar;
    LocationManager locationManager;
    LocationListener locationListener;
    Button logout;
    Button myadress;
    MapsActivity mapsActivity;
    static double longitude;
    static double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        myadress = (Button) findViewById(R.id.btnMyadress);

        showAdress = (TextView) findViewById(R.id.tvAdress);
        afficherAdresse = (Button) findViewById(R.id.btnShowAdress);
        trouverAdresse = (Button) findViewById(R.id.btnFindAdress);
        logout = (Button) findViewById(R.id.btnlogout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        trouverAdresse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LocationActivity.this, FindUserActivity.class));
            }
        });
        disconnet();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
             /*   if (location.getAccuracy() > 1500){
                    progressBar.setVisibility(View.VISIBLE);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0, locationListener);
                }
                else { */
                progressBar.setVisibility(View.INVISIBLE);
                locationManager.removeUpdates(locationListener);
                saveLocalisation();
                startActivity(new Intent(LocationActivity.this, MapsActivity.class));
                // }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                }, 10);
                return;
            } else {
                configureButton();
            }
        } else {
            configureButton();
        }
        showUserAdress();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
                return;
        }
    }

    public void configureButton() {
        afficherAdresse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
                // Toast.makeText(getApplicationContext(),"accurracy is " + locationManager. , )
                // showAdress.setText("hello");
            }
        });
    }



    public void disconnet() {
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LocationActivity.this, MainActivity.class));

            }
        });
    }

    public void saveLocalisation() {
        Map<String, Object> lon = new HashMap<>();
        Map<String, Object> lat = new HashMap<>();
        Map<String, Object> adresse = new HashMap<>();
        int ad = generateAdress();
        lon.put("longitude", longitude);
        lat.put("latitude", latitude);
        validAdress(ad);
        adresse.put("adresse", ad);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myDB = firebaseDatabase.getReference(user.getUid());
        myDB.updateChildren(lon);
        myDB.updateChildren(lat);
        myDB.updateChildren(adresse);

    }

    public void findLocation(final String email) {
        trouverAdresse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LocationActivity.this, FindUserActivity.class));
            }
        });
    }

    public int generateAdress() {
        int min = 1;
        int max = 100000;
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public void validAdress(final int randomNum) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot chidSnap : dataSnapshot.getChildren()) {

                    if (chidSnap.child("adresse").equals(randomNum)) {
                        Toast.makeText(getApplicationContext(), "Echec, adresse non enregistrée, essayez encore", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showUserAdress(){
        myadress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        for (DataSnapshot chidSnap : dataSnapshot.getChildren()) {

                            if (userEmail.equals(chidSnap.child("email").getValue().toString())) {
                                Toast.makeText(getApplicationContext(), "Votre adresse est " + chidSnap.child("adresse").getValue().toString(),Toast.LENGTH_SHORT).show();
                            }

                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        }

}

