package com.example.illuminateme;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class RateUserActivity extends AppCompatActivity {


    private final int REQ_CODE_SPEECH_INPUT = 100;

    private TextToSpeech textToSpeech;

    private String numberOfCall;
    private String volunteerRating ,receiverUserId ;

    private int tries  = 0;
    float rating= 0 ,  numberOfCalls=0 ,volunteerRatings=0;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityuserratelayout);


        //    Toast.makeText(getApplicationContext(), rating, Toast.LENGTH_LONG).show();

        volunteerRating =getIntent().getStringExtra("rating");
        numberOfCall = getIntent().getStringExtra("numberOfCalls") ;
        receiverUserId = getIntent().getStringExtra("receiverUserId");

        numberOfCalls =Float.parseFloat(numberOfCall);
        volunteerRatings = Float.parseFloat(volunteerRating);

        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(receiverUserId);

        promptSpeechInput();



    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tries == 1 ) {
            startActivity(new Intent(RateUserActivity.this, BlindHomeActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data &&tries!=1) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    try {

                        rating = Integer.parseInt(result.get(0));
                        calculateRating(rating);
                        startActivity  ( new Intent(RateUserActivity.this , BlindHomeActivity.class));
                        finish();
                    }
                    catch (Exception e)
                    {
                        //rating = 5;
                      //  calculateRating(rating);
                       // startActivity  ( new Intent(RateUserActivity.this , BlindHomeActivity.class));
                        finish();
                    }


                }
                break;
            }

        }
    }



    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {

            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void calculateRating(float rating) {

        tries++;
//        if(numberOfCalls==0)
//        {
//            volunteerRating = Float.toString((volunteerRatings+rating)/(numberOfCalls+2) );
//
//        }
     //   else {
        //    volunteerRating = Float.toString((volunteerRatings + rating) / (numberOfCalls + 1));
//        }

        volunteerRating = Float.toString((volunteerRatings + rating) / (2));
        numberOfCall = String.valueOf(numberOfCalls+1);
        userRef.child("rating").setValue(volunteerRating.toString());
        userRef.child("numberOfCalls").setValue(numberOfCall);
        startActivity  ( new Intent(RateUserActivity.this , BlindHomeActivity.class));
        finish();







    }
}
