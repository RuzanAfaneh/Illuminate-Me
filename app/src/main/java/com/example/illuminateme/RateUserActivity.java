package com.example.illuminateme;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RateUserActivity extends AppCompatActivity {

    RatingBar ratingBar;
    float ratingF;
    String rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_user);


        RatingBar ratingBar = findViewById(R.id.ratingBar);
        Toast.makeText(getApplicationContext(), rating, Toast.LENGTH_LONG).show();

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rating = String.valueOf(v);

            }
        });


    }
}
