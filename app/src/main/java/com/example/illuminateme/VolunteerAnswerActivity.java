package com.example.illuminateme;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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


public class VolunteerAnswerActivity extends AppCompatActivity {

    private ImageView logoBtn;
    private TextView msg ;
    private Button answerbtn, closebtn;

    private String receiverUserId = "";
    private String senderUserId = "",  checker = "" , done="";
//    private String callingID = "", ringingID = "";

    private DatabaseReference userRef , blind , volunteer , availability;

    private String type = "" , ava  ;
    private ConstraintLayout constraintLayout;

    private Intent intent  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //start service and play music
        startService(new Intent(VolunteerAnswerActivity.this, SoundService.class));


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_answer);

         intent = new Intent(VolunteerAnswerActivity.this, MainActivity.class);

        senderUserId = FirebaseAuth.getInstance().getCurrentUser().getUid(); //blind person who is calling  we must get his id which is the current uesr . duh

        receiverUserId = getIntent().getStringExtra("receiverId");

        type = getIntent().getStringExtra("type");

        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        volunteer = userRef.child(receiverUserId).child("Ringing");
       availability = userRef.child(receiverUserId).child("availability");
        blind = userRef.child(senderUserId).child("Calling");

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {
               cancelUserCall();
                if(type.equals("volunteer"))
                {availability.setValue("false");}
                finish();
            }
        }, 40000);


        checker="";

        logoBtn = findViewById(R.id.button);
        closebtn = findViewById(R.id.cancel_call_V);
        answerbtn = findViewById(R.id.answer_call_V);
msg = findViewById(R.id.textView3);

        constraintLayout = findViewById(R.id.constraint_layout);

        if (type.equals("blind")) {
            answerbtn.setVisibility(View.GONE);
            msg.setText("Wait while we find someone to help");
            setnewConstraint();
        }

        else if(type.equals("volunteer")) {
            answerbtn.setVisibility(View.VISIBLE);
            //sender iser id
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(receiverUserId).hasChild("Ringing")){
                        senderUserId = dataSnapshot.child(receiverUserId).child("Ringing").child("ringing").getValue(String.class);

                    }}

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        blouncingLogo();



        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";
                if(type.equals("volunteer"))
                {availability.setValue("false");};

                //recivers  user id
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(senderUserId).hasChild("Calling")){
                            receiverUserId = dataSnapshot.child(senderUserId).child("Calling").child("calling").getValue(String.class);
                    }
                }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                volunteer = userRef.child(receiverUserId).child("Ringing");
                blind = userRef.child(senderUserId).child("Calling");

                volunteer.removeValue();
                blind.removeValue();



                //cancelUserCall();
            }
        });

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
                                    System.out.println("Type IS "+type + ",receiverUserId  " + receiverUserId+  senderUserId);
                                    intent.putExtra("type", type);
                                    intent.putExtra("receiverUserId",receiverUserId);
                                    intent.putExtra("senderUserId",senderUserId);
                                   startActivity(intent);
                                }
                            }
                        });



            }
        });

    }//on Create

    private void cancelUserCall() {

            blind.removeValue();
            volunteer.removeValue();


    }

    private void blouncingLogo() {

        final Animation myAnim = AnimationUtils.loadAnimation(VolunteerAnswerActivity.this, R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        BounceButton interpolator = new BounceButton(0.2, 20);
        myAnim.setInterpolator(interpolator);

        logoBtn.startAnimation(myAnim);
    }//logo bounce

    private void setnewConstraint() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.setHorizontalBias(R.id.cancel_call_V, 0.0f);
        //constraintSet.connect(R.id.cancel_call_V, END, R.id.constraint_layout, START);
        constraintSet.applyTo(constraintLayout);
    } //set on Constarint

    @Override
    protected void onStart() {
        super.onStart();


 // on close
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child(receiverUserId).hasChild("Ringing") && !dataSnapshot.child(senderUserId).hasChild("Calling") && checker.equals("clicked")
                || !dataSnapshot.child(receiverUserId).hasChild("Ringing") && !dataSnapshot.child(senderUserId).hasChild("Calling")  ){
                    stopService(new Intent(VolunteerAnswerActivity.this, SoundService.class));

                    if(!checker.equals("clicked"))
                    {
                        //stopService(new Intent(VolunteerAnswerActivity.this, SoundService.class));

                        volunteer.removeValue();
                        blind.removeValue();

                    }
                    if(type.equals("volunteer"))
                    {

                        System.out.println("HRER NUMBER 7");
                        startActivity(new Intent(VolunteerAnswerActivity.this , VolunteerHomeActivity.class));
                       //blind.removeValue();
                        finish();

                    }
                    else if(type.equals("blind")  )
                    {

                        System.out.println("HRER NUMBER 5");
                        startActivity(new Intent(VolunteerAnswerActivity.this , BlindHomeActivity.class));
                        finish();

                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //on answer
        userRef.addValueEventListener(new ValueEventListener() {

            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                stopService(new Intent(VolunteerAnswerActivity.this, SoundService.class));


                if (dataSnapshot.child(receiverUserId).child("Ringing").hasChild("picked") && dataSnapshot.child(senderUserId).hasChild("Calling") &&type.equals("blind")) {

                    stopService(new Intent(VolunteerAnswerActivity.this, SoundService.class));

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                    intent.putExtra("receiverUserId", receiverUserId);
                   intent.putExtra("senderUserId",senderUserId);
                    intent.putExtra("type", type);

                    startActivity(intent);

                }



            }



            @Override

            public void onCancelled(@NonNull DatabaseError databaseError) {



            }

        });




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}