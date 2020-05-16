package com.example.illuminateme;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FindVolunteerActivity extends AppCompatActivity

{

    private static final String TAG = FindVolunteerActivity.class.getSimpleName();


    private DatabaseReference userRef, genderRef;
    private FirebaseAuth currUser;

    private String blindGender = " ", blindName = "", type = "", volunteerRef = "";
    private String volunteerId, calledBy = "" , userId;

    private Float rating ;
    private Array ava ;
private Boolean found = false ;

    private HashMap rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //get the gender of blind caller to match it with volunteer
        String i = getIntent().getStringExtra("gender");

        userRef = FirebaseDatabase.getInstance().getReference().child("users");

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); //blind person
        rate = new HashMap<String,Object>();
        rate.put("rate",2.5);
        genderRef = userRef.child(userId);
        //get the gender
        genderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                blindGender = dataSnapshot.child("gender").getValue(String.class);
                blindName = dataSnapshot.child("username").getValue(String.class);

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();

                            map.put("gender", snapshot.child("gender").getValue(String.class));
                            map.put("username", snapshot.child("username").getValue(String.class));
                            map.put("type", snapshot.child("type").getValue(String.class));
                            map.put("availability", snapshot.child("availability").getValue(String.class));
                            map.put("rating", snapshot.child("rating").getValue(String.class));



                            if (    map.get("gender").equals(blindGender)
                                    && !snapshot.getKey().equals(userId)
                                    && map.get("type").equals("Volunteer")
                                    && map.get("availability").equals("true")
                                   && map.get("rating") != null &&( (rating = Float.parseFloat(String.valueOf(map.get("rating")))) >2.5)
                            )
                            {

                                found = true ;
                                    Intent i = new Intent(FindVolunteerActivity.this, VolunteerAnswerActivity.class);
                                    i.putExtra("type", "blind");
                                    i.putExtra("senderId" , userId);
                                    i.putExtra("receiverId", snapshot.getKey());
                                    System.out.println(snapshot.getKey()     + "HERE NUMBER 3");
                                    checkForRecivingCall(snapshot.getKey());
                                    startActivity(i);
                                    finish();
                                    break;

                                }







                        }


                    if(found==false)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(FindVolunteerActivity.this);
                        builder.setMessage("No One Is Available");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(FindVolunteerActivity.this,BlindHomeActivity.class);
                                startActivity(intent);
                                dialog.dismiss();
                        }
                    });
                        AlertDialog alert = builder.create();
                        alert.show();

//                        Toast.makeText(FindVolunteerActivity.this,"NO ONE WAS FOUND",Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(FindVolunteerActivity.this , BlindHomeActivity.class));
//                        finish();

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


       // checkForRecivingCall();
    }

    private void checkForRecivingCall(String receiverUserId) {

        userRef.child(receiverUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check if the user is not busy
                if ( !dataSnapshot.hasChild("Calling") && !dataSnapshot.hasChild("Ringing")) {
                    final HashMap<String, Object> callingInfo = new HashMap<>();
                    callingInfo.put("calling",receiverUserId); //is calling gthis
                    //sender is the online user who is going to make the call
                    //sender is calling . so we add the info for the first hashmap
                    System.out.println("HERE NUMBER 4");
                    userRef.child(userId).child("Calling")
                            .updateChildren(callingInfo)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //the person whos hone is ringing
                                        final HashMap<String, Object> ringingInfo = new HashMap<>();
                                        ringingInfo.put("ringing", userId); //is calling gthis
                                        userRef.child(receiverUserId).child("Ringing")
                                                .updateChildren(ringingInfo);


                                    }
                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

//    @Override
//    public int compareTo(Object o) {
//        rate = new HashMap<String,Object>();
//        rate.put("rate",2.5);
//
//        if(o > map.get);
//    }
//    boolean isGreaterThan(Object that) {
//        return this.compareTo(that) > 2.5;
//    }
}

