package com.example.illuminateme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MaleOrFemale extends AppCompatActivity {

    private Button btnmale, btnFemale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_male_or_female);

        btnmale = findViewById(R.id.choosemale);
        btnFemale = findViewById(R.id.choosefemale);
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);

        String type = getIntent().getStringExtra("type");

        btnmale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("gender", "male");
                intent.putExtra("type", type);
                startActivity(intent);

            }
        });

        btnFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("gender", "female");
                intent.putExtra("type", type);

                startActivity(intent);

            }
        });


    }
}
