package com.example.illuminateme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static android.view.Gravity.TOP;
import static androidx.constraintlayout.widget.ConstraintSet.BOTTOM;

public class VolunteerAnswerActivity extends AppCompatActivity {

    private ImageView logoBtn;
    private Button answerbtn, closebtn;

    private String receiverUserId = "", receiverUserName = "";
    private String senderUserId = "", senderUserName = "", checker = "";
    private String callingID = "", ringingID = "";

    private DatabaseReference userRef;

    private String type = "";
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //start service and play music
        startService(new Intent(VolunteerAnswerActivity.this, SoundService.class));


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_answer);

        senderUserId = FirebaseAuth.getInstance().getCurrentUser().getUid(); //blind person who is calling  we must get his id which is the current uesr . duh

        //voulneet id which we get from (findVolunteerActivity) in casee of blind type
        //otherwise we get it from start CheckforCall for the voulnteer
        receiverUserId = getIntent().getStringExtra("volunteerId");

        type = getIntent().getStringExtra("type");

        userRef = FirebaseDatabase.getInstance().getReference().child("users");


        logoBtn = findViewById(R.id.button);
        closebtn = findViewById(R.id.cancel_call_V);
        answerbtn = findViewById(R.id.answer_call_V);

        constraintLayout = findViewById(R.id.constraint_layout);


        if (type.equals("blind")) {
            answerbtn.setVisibility(View.INVISIBLE);
            setnewConstraint();
        }

        blouncingLogo();

        answerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stopService(new Intent(VolunteerAnswerActivity.this, SoundService.class));

                final HashMap<String, Object> callingPickupMap = new HashMap<>();
                callingPickupMap.put("picked", "picked");

                userRef.child(receiverUserId).child("Ringing")
                        .updateChildren(callingPickupMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("type", type);
                                    startActivity(intent);
                                }
                            }
                        });

            }
        });

        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";

                stopService(new Intent(VolunteerAnswerActivity.this, SoundService.class));

                cancelCallingUser();

            }
        });

    }

    private void setnewConstraint() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.setHorizontalBias(R.id.cancel_call_V, .0f);
        constraintSet.connect(R.id.cancel_call_V, TOP, R.id.constraint_layout, BOTTOM);
        constraintSet.applyTo(constraintLayout);
    }

    private void blouncingLogo() {

        final Animation myAnim = AnimationUtils.loadAnimation(VolunteerAnswerActivity.this, R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        BounceButton interpolator = new BounceButton(0.2, 20);
        myAnim.setInterpolator(interpolator);

        logoBtn.startAnimation(myAnim);
    }//logo bounce


    private void cancelCallingUser() {

        //frome sender side
        userRef.child(senderUserId)
                .child("Calling")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists() && dataSnapshot.hasChild("calling")) {

                            callingID = dataSnapshot.child("calling").getValue().toString();

                            userRef.child(callingID) //reciver id
                                    .child("Ringing")
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                userRef.child(senderUserId)
                                                        .child("Calling")
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                {
                                                                    if (type.equals("blind")) {
                                                                        startActivity(new Intent(VolunteerAnswerActivity.this, BlindHomeActivity.class));
                                                                        finish();
                                                                    } else {
                                                                        startActivity(new Intent(VolunteerAnswerActivity.this, VolunteerHomeActivity.class));

                                                                    }
                                                                }
                                                            }
                                                        });
                                            } else {
                                                System.out.println("NOT SUCCCCSUUUFFLFLLL");
                                            }
                                        }
                                    });
                        } else {
                            if (type.equals("blind")) {
                                startActivity(new Intent(VolunteerAnswerActivity.this, BlindHomeActivity.class));
                                finish();
                            } else {
                                startActivity(new Intent(VolunteerAnswerActivity.this, VolunteerHomeActivity.class));

                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        //from reciver side
        {
            userRef.child(senderUserId)
                    .child("Ringing")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.hasChild("ringing")) {
                                ringingID = dataSnapshot.child("ringing").getValue().toString();

                                userRef.child(ringingID)
                                        .child("Calling")
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    userRef.child(senderUserId)
                                                            .child("Ringing")
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    {
                                                                        if (type.equals("blind")) {
                                                                            startActivity(new Intent(VolunteerAnswerActivity.this, BlindHomeActivity.class));
                                                                            finish();
                                                                        } else {
                                                                            startActivity(new Intent(VolunteerAnswerActivity.this, VolunteerHomeActivity.class));

                                                                        }
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            } else {
                                if (type.equals("blind")) {
                                    startActivity(new Intent(VolunteerAnswerActivity.this, BlindHomeActivity.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(VolunteerAnswerActivity.this, VolunteerHomeActivity.class));

                                }
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }


    }


    protected void onDestroy() {
        //stop service and stop music
        stopService(new Intent(VolunteerAnswerActivity.this, SoundService.class));
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

        userRef.child(receiverUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //check if the user is not busy
                        if (!checker.equals("clicked") && !dataSnapshot.hasChild("calling") && !dataSnapshot.hasChild("Ringing")) {
                            final HashMap<String, Object> callingInfo = new HashMap<>();
                            callingInfo.put("calling", receiverUserId); //is calling gthis
                            //TODO maybe add call history hhere

                            //sender is the online user who is going to make the call
                            //sender is calling . so we add the info for the first hashmap

                            userRef.child(senderUserId).child("Calling")
                                    .updateChildren(callingInfo)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //the person whos hone is ringing
                                                final HashMap<String, Object> ringingInfo = new HashMap<>();
                                                ringingInfo.put("ringing", senderUserId); //is calling gthis

                                                //TODO maybe add call history hhere


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

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.child(senderUserId).hasChild("Calling") && dataSnapshot.child(senderUserId).hasChild("Ringing")) {
                    answerbtn.setVisibility(View.VISIBLE);
                }
                if (dataSnapshot.child(receiverUserId).child("Ringing").hasChild("picked")) {
                    stopService(new Intent(VolunteerAnswerActivity.this, SoundService.class));

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("type", type);
                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
