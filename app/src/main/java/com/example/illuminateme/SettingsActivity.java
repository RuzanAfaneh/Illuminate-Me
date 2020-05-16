package com.example.illuminateme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();


    private BottomNavigationView navView;

    private DatabaseReference userRef;
    private FirebaseUser user;
    private String userId , type  , name , pass , nameedit="",passedit="";

    private EditText username , password ;
    private Button editPassword , editUsername , update ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        navView = findViewById(R.id.nav_view);

        type = getIntent().getStringExtra("type");
        System.out.println(type + "TYPEEEEEEEEEEEEEEEe");

         BottomNavigationView.OnNavigationItemSelectedListener navigationItemReselectedListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.navigation_home :
                                if(type.equals("blind")) {
                                    startActivity(new Intent(SettingsActivity.this, BlindHomeActivity.class));
                                }
                                if(type.equals("volunteer"))
                                {
                                    startActivity(new Intent(SettingsActivity.this, VolunteerHomeActivity.class));

                                }
                                else System.out.println("OOMMAAAGOD");
                                break;

                            case R.id.navigation_settings:
                                startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
                                break;

                            case R.id.navigation_logout:
                                startActivity(new Intent(SettingsActivity.this, LogoutActivity.class));
                                finish();
                                break;


                        }
                        return true;
                    }
                };

        navView.setOnNavigationItemSelectedListener(navigationItemReselectedListener);




        username = findViewById(R.id.username_setting);
        password = findViewById(R.id.password_setting);
        editPassword = findViewById(R.id.edit_password);
        editUsername = findViewById(R.id.edit_username);
         update = findViewById(R.id.update);


        userRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            username.setEnabled(true);
                username.requestFocus();
            nameedit = "clicked";
            update.setVisibility(View.VISIBLE);
            }
        });

        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password.setEnabled(true);
                password.requestFocus();
                //pass = password.getText().toString();
                passedit = "clicked";
                update.setVisibility(View.VISIBLE);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameedit.equals("clicked"))
                    name = username.getText().toString();
                userRef.child("username").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                        update.setVisibility(View.INVISIBLE);
                        Toast.makeText(SettingsActivity.this , "NAME CHANGE COMPLETE" , Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(getIntent());
                        }
                    }
                });

                if(passedit.equals("clicked"))
                {
                    pass = password.getText().toString();

                    System.out.println(pass+"PASSWORD ");
                    user.updatePassword(pass)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User password updated.");
                                        finish();
                                        startActivity(getIntent());
                                    }
                                    else
                                    {
                                        Log.d(TAG, "AAAAAAAAAAAAAH.");

                                    }
                                }
                            });
                }
            }
        });


    }
}
