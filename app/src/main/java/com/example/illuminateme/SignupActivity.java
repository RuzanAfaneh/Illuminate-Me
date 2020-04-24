package com.example.illuminateme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    EditText emailId, password, username;
    Button btnSignUp, s;
    TextView tvSignIn;

    String type = "", gender;

    FirebaseAuth mFirebaseAuth;
    DatabaseReference mDatebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatebase = FirebaseDatabase.getInstance().getReference();

        gender = getIntent().getStringExtra("gender");
        type = getIntent().getStringExtra("type");

        username = findViewById(R.id.name_signup);
        emailId = findViewById(R.id.email_signup);
        password = findViewById(R.id.password_signup);
        btnSignUp = findViewById(R.id.signup);
        tvSignIn = findViewById(R.id.login_signup);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String name = username.getText().toString();
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                if (name.isEmpty()) {
                    username.setError("Please enter your name");
                    username.requestFocus();
                }
                if (email.isEmpty()) {
                    emailId.setError("Please enter email id");
                    emailId.requestFocus();
                }

                if (pwd.isEmpty()) {
                    password.setError("Please enter your password");
                    password.requestFocus();
                } else if (email.isEmpty() && pwd.isEmpty() && name.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Fields Are Empty!", Toast.LENGTH_SHORT).show();
                } else if (!(email.isEmpty() && pwd.isEmpty())) {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(SignupActivity.this, "SignUp Unsuccessful, Please Try Again", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String userId = mFirebaseAuth.getUid();
                                        writeNewUser(userId, type, name, gender);
                                        if (type.equals("blind"))
                                            startActivity(new Intent(SignupActivity.this, BlindHomeActivity.class));
                                        else
                                            startActivity(new Intent(SignupActivity.this, VolunteerHomeActivity.class));

                                    }
                                }
                            });
                } else {
                    Toast.makeText(SignupActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();

                }
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    private void writeNewUser(String userId, String typee, String namee, String genderr) {
        User user = new User(namee, genderr, "true", typee);

        // if (typee.equals("Blind"))
        mDatebase.child("users").child(userId).setValue(user);

        if (typee.equals("Volunteer")){
            final HashMap<String, Object> map = new HashMap<>();
            map.put("rating", "5");
            map.put("numberOfCalls","0");
            mDatebase.child("users").child(userId)
                    .updateChildren(map);

        }

//
    }
}
