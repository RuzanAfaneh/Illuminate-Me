package com.example.illuminateme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class BlindOrVolunteerActivity extends AppCompatActivity {

    private Button btnBlind, btnVolunteer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blind_or_volunteer);

        btnBlind = findViewById(R.id.chooseBlind);
        btnVolunteer = findViewById(R.id.choosevolunteer);

        Intent intent = new Intent(getApplicationContext(), MaleOrFemale.class);
        btnBlind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent.putExtra("type", "Blind");
                startActivity(intent);

            }
        });
        btnVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                intent.putExtra("type", "Volunteer");
                //Toast.makeText(VolunteerAnswerActivity.this, type, Toast.LENGTH_SHORT).show();
                startActivity(intent);

            }
        });
    }
}
