package com.example.illuminateme;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VolunteerHomeActivity extends AppCompatActivity {
    private Button callVolunteer;
    private TextView username;
    private Switch avaSwitch;

    private DatabaseReference userRef;
    private String userId, getUserName, checked = "" ;
    private String calledBy = "";

    private float rating;
    private RatingBar ratingBar ;


    private BottomNavigationView navView;
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemReselectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.navigation_home:
                            startActivity(new Intent(VolunteerHomeActivity.this, BlindHomeActivity.class));
                            break;

                        case R.id.navigation_settings:
                            //   startActivity(new Intent(BlindHomeActivity.this,SettingsActivity.class));
                            break;

                        case R.id.navigation_logout:
                            startActivity(new Intent(VolunteerHomeActivity.this, LogoutActivity.class));
                            finish();
                            break;


                    }
                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_volunteer);


        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navigationItemReselectedListener);

        callVolunteer = findViewById(R.id.choosevolunteer);
        username = findViewById(R.id.volunteer_username);
        avaSwitch = findViewById(R.id.availabilitySwitch);
        ratingBar = findViewById(R.id.ratingBar);

        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        activiateSwitch();
        //checkSwitchState

        userRef.child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //get the name
                        getUserName = dataSnapshot.child("username").getValue(String.class);
                        System.out.println(getUserName);
                        username.setText(getUserName);

                        //activiate the switch
                        checked = dataSnapshot.child("availability").getValue(String.class);
                        if (checked.equals("true")) {
                            avaSwitch.setChecked(true);
                        } else {
                            avaSwitch.setChecked(false);
                        }

                        //getRating
                        rating =  Float.parseFloat(dataSnapshot.child("rating").getValue(String.class));
                        ratingBar.setRating(rating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void activiateSwitch() {
        avaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    userRef.child(userId)
                            .child("availability")
                            .setValue("true");
                } else {
                    userRef.child(userId)
                            .child("availability")
                            .setValue("false");
                }
            }
        });
    }

    @Override

    protected void onStart() {

        super.onStart();

        userRef.child(userId)

                .child("Ringing")

                .addValueEventListener(new ValueEventListener() {

                    @Override

                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild("ringing")) {

                        //    calledBy = dataSnapshot.child("ringing").getValue().toString();

                            Intent i = new Intent(VolunteerHomeActivity.this, VolunteerAnswerActivity.class);
//
            //                i.putExtra("userId", calledBy);

                            i.putExtra("receiverId", userId);

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