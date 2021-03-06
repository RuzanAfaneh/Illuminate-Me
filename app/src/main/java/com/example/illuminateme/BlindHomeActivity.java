package com.example.illuminateme;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BlindHomeActivity extends AppCompatActivity {
    private Button callVolunteer;

    private String type;

    private BottomNavigationView navView;
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemReselectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.navigation_home:
                            startActivity(new Intent(BlindHomeActivity.this, BlindHomeActivity.class));
                            finish();
                            break;

                        case R.id.navigation_settings:
                            type = "blind";
                            Intent i = new Intent(BlindHomeActivity.this, SettingsActivity.class);
                            i.putExtra("type" , type );
                            startActivity(i);
                            finish();
                            break;

                        case R.id.navigation_logout:
                            startActivity(new Intent(BlindHomeActivity.this, LogoutActivity.class));
                            finish();
                            break;


                    }
                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blind_home);


        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navigationItemReselectedListener);

        Intent intent = new Intent(BlindHomeActivity.this, FindVolunteerActivity.class);
        intent.putExtra("type", "blind");

        callVolunteer = findViewById(R.id.callVolunteer);

        callVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
                finish();


            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
