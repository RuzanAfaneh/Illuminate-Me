package com.example.illuminateme;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class FindVolunteerActivity extends AppCompatActivity {

    private static final String TAG = FindVolunteerActivity.class.getSimpleName();


    private DatabaseReference userRef, genderRef;
    private FirebaseAuth currUser;

    private String blindGender = " ", blindId = "", blindName = "", type = "", volunteerRef = "";
    private String volunteerId, calledBy = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //get the gender of blind caller to match it with volunteer
        String i = getIntent().getStringExtra("gender");

        userRef = FirebaseDatabase.getInstance().getReference().child("users");

        blindId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        genderRef = userRef.child(blindId);
        //get the gender
        genderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                blindGender = dataSnapshot.child("gender").getValue(String.class);
                blindName = dataSnapshot.child("username").getValue(String.class);
                System.out.println(blindGender);
                System.out.println(blindName);

                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();

                            map.put("gender", snapshot.child("gender").getValue(String.class));
                            map.put("username", snapshot.child("username").getValue(String.class));
                            map.put("type", snapshot.child("type").getValue(String.class));
                            map.put("availability", snapshot.child("availability").getValue(String.class));


                            if (map.get("gender").equals(blindGender) && !snapshot.getKey().equals(blindId) && map.get("type").equals("Volunteer") && map.get("availability").equals("true")) {

                                Intent i = new Intent(FindVolunteerActivity.this, VolunteerAnswerActivity.class);
                                i.putExtra("type", "blind");
                                i.putExtra("volunteerId", snapshot.getKey());
                                startActivity(i);
                                finish();
                                break;

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //TODO alert the blind person no volunteer was found


    }

    @Override
    protected void onStart() {

        super.onStart();

        checkForRecivingCall();
    }

    private void checkForRecivingCall() {
        userRef.child(blindId)
                .child("Ringing")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("ringing")) {
                            calledBy = dataSnapshot.child("ringing").getValue().toString();
                            Intent i = new Intent(FindVolunteerActivity.this, VolunteerAnswerActivity.class);
                            i.putExtra("userId", calledBy);
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

