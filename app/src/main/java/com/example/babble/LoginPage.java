package com.example.babble;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginPage extends AppCompatActivity {
    TextInputEditText email, password;
    Button signIn;
    TextView signUp,forgotPassword;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.editTextLoginEmail);
        password = findViewById(R.id.editTextLoginPassword);
        signIn = findViewById(R.id.buttonSignIn);
        signUp = findViewById(R.id.textViewSignUp);
        forgotPassword = findViewById(R.id.textViewForgotPassword);

        signIn.setOnClickListener(view -> {
            String userEmail = Objects.requireNonNull(email.getText()).toString();
            String userPassword = Objects.requireNonNull(password.getText()).toString();
            SignIn(userEmail, userPassword);
        });

        signUp.setOnClickListener(view -> {
            Intent i = new Intent(LoginPage.this, SignUpPage.class);
            startActivity(i);
        });

        forgotPassword.setOnClickListener(view -> {
            Intent i = new Intent(LoginPage.this, PasswordResetPage.class);
            startActivity(i);
        });

    }

    private void SignIn(String userEmail, String userPassword) {
        auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(LoginPage.this, task -> {
                    if(task.isSuccessful()) {
                        Intent i = new Intent(LoginPage.this, HomePage.class);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(),"Welcome to BABBLE!",Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),
                                "Please! Enter correct email and password", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            Intent i = new Intent(LoginPage.this,HomePage.class);
            startActivity(i);
            Toast.makeText(getApplicationContext(),"Welcome to BABBLE!",Toast.LENGTH_LONG).show();
            finish();
        }
        super.onStart();
    }
}