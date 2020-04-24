package com.example.illuminateme;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText emailId, password;
    Button btnSignIn, signUp;
    TextView tvSignUp;
    FirebaseAuth mFirebaseAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private String type, userId;
    private DatabaseReference ref;

    private String typeValue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //splash
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnSignIn = findViewById(R.id.signin);
        tvSignUp = findViewById(R.id.signUp);

        ref = FirebaseDatabase.getInstance().getReference();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser!=null) {
                    Toast.makeText(LoginActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
                    userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    ref.child("users")
                            .child(userId)
                            .child("type")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    typeValue = dataSnapshot.getValue(String.class);

                                if (typeValue.equals("Blind")) {
                                        Intent i = new Intent(LoginActivity.this, BlindHomeActivity.class);
                                        // i.putExtra("type","blind");
                                        startActivity(i);
                                        finish();

                                    } else if (typeValue.equals("Volunteer")) {
                                        Intent i = new Intent(LoginActivity.this, VolunteerHomeActivity.class);
                                        i.putExtra("type", "volunteer");
                                        startActivity(i);
                                        finish();

                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                } else {
                    Toast.makeText(LoginActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                }
            }
        };

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                if (email.isEmpty()) {
                    emailId.setError("Please enter email id");
                    emailId.requestFocus();
                } else if (pwd.isEmpty()) {
                    password.setError("Please enter your password");
                    password.requestFocus();
                } else {
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login Error, Please Login Again", Toast.LENGTH_SHORT).show();
                            } else {
                                userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                ref.child("users")
                                        .child(userId)
                                        .child("type")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                typeValue = dataSnapshot.getValue(String.class);

                                                if (typeValue.equals("Blind")) {
                                                    Intent i = new Intent(LoginActivity.this, BlindHomeActivity.class);
                                                    // i.putExtra("type","blind");
                                                    startActivity(i);
                                                    finish();

                                                } else if (typeValue.equals("Volunteer")) {
                                                    Intent i = new Intent(LoginActivity.this, VolunteerHomeActivity.class);
                                                    i.putExtra("type", "volunteer");
                                                    startActivity(i);
                                                    finish();

                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });




                            }
                        }
                    });
                }

            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, BlindOrVolunteerActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void checkUserType(String userId) {


        System.out.println("kljkjkjhjghhjghgfrdr");
        //return  typeValue ;
    }

}
