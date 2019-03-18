package com.example.imane.sherrymap;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    TextView redirectLogin;
    EditText surname;
    EditText name;
    EditText email;
    EditText password;
    EditText repassword;
    Button register;
    private FirebaseAuth firebaseAuth;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        setInterface();
        registerUser();
        redirectLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            }
        });


    }
    private void setInterface() {
        surname = (EditText) findViewById(R.id.etPrenom);
        name = (EditText) findViewById(R.id.etNom);
        email = (EditText) findViewById(R.id.etmail);
        password = (EditText) findViewById(R.id.etNewPassword);
        repassword = (EditText)findViewById(R.id.etPassword);
        redirectLogin = (TextView) findViewById(R.id.tvRedirectLogin);
        register = (Button) findViewById(R.id.bSave);
    }
    private void registerUser(){
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = new User(surname.getText().toString().trim(), name.getText().toString().trim(), email.getText().toString().trim(), 0.0, 0.0,0.0);
                user.setPassword( password.getText().toString().trim());
                if (!isBlank(user)) {
                    if (validatePassword(user)) {
                        if (confirmPassword(user, repassword.getText().toString().trim())) {
                            compareUsers(user);
                            AuthUser(user);
                        }
                    }
                }
            }
    private void AuthUser(User user){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(),user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        sendData();
                        startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
                        Toast.makeText(getApplicationContext(), "Enregistrement réussi" ,Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Enregistrement echoué, essayez encore" ,Toast.LENGTH_SHORT).show();
                }
               });
             }
         });
    }
    private boolean validatePassword (User user){
        if (user.getPassword().length() <= 5){
            Toast.makeText(getApplicationContext(), "Le mot de passe doit contenir au moins six caractère" ,Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }
    private boolean isBlank(User user){
        if(user.getSurname().length() == 0 || user.getName().length() == 0 || user.getEmail().length() == 0 || user.getPassword().length() == 0 ){
            Toast.makeText(getApplicationContext(), "Veuillez remplir tous les champs" ,Toast.LENGTH_SHORT).show();
            return true;
        }
            return false;
    }
    private void sendData(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myDB = firebaseDatabase.getReference(firebaseAuth.getUid());
        User user = new User(surname.getText().toString().trim(), name.getText().toString().trim(), email.getText().toString().trim(), 0.0, 0.0,0.0);
        myDB.setValue(user);
        Map<String, Object> adresse = new HashMap<>();
        adresse.put("adresse", 0 );
        myDB.updateChildren(adresse);
    }

    private boolean confirmPassword(User user, String confirmationPassword){
        if (user.getPassword().equals(confirmationPassword))
        {
            return true;
        } else {

             Toast.makeText(getApplicationContext(), "La confirmation du mot de passe doit correspondre au mot de passe saisi  " ,Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void compareUsers(final User user){
                final boolean userExist = false;
                FirebaseDatabase database = FirebaseDatabase.getInstance();

                DatabaseReference myRef = database.getReference();
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot chidSnap : dataSnapshot.getChildren()) {

                            if (user.getEmail().equals(chidSnap.child("email").getValue().toString())) {
                                Toast.makeText(getApplicationContext(), "Cet email est déjà utilisé",Toast.LENGTH_SHORT ).show();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
}

