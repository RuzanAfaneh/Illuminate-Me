package com.example.illuminateme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class VolunteerAnswerActivity extends AppCompatActivity {

    private ImageView logoBtn;
    private Button answerbtn, closebtn;
    private String type = "v";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //start service and play music
        startService(new Intent(VolunteerAnswerActivity.this, SoundService.class));


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_answer);

        logoBtn = findViewById(R.id.button);
        closebtn = findViewById(R.id.cancel_call_V);
        answerbtn = findViewById(R.id.answer_call_V);


        final Animation myAnim = AnimationUtils.loadAnimation(VolunteerAnswerActivity.this, R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        BounceButton interpolator = new BounceButton(0.2, 20);
        myAnim.setInterpolator(interpolator);

        logoBtn.startAnimation(myAnim);


        //
        answerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("type", type);
                //Toast.makeText(VolunteerAnswerActivity.this, type, Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeVolunteerActivity.class);
                startActivity(intent);
            }
        });

    }

    protected void onDestroy() {
        //stop service and stop music
        stopService(new Intent(VolunteerAnswerActivity.this, SoundService.class));
        super.onDestroy();
    }
}
