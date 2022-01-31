package com.example.babble;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetPage extends AppCompatActivity {
    EditText email;
    Button submit;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset_page);

        email = findViewById(R.id.editTextPasswordEmail);
        submit = findViewById(R.id.buttonPasswordSubmit);

        submit.setOnClickListener(view -> {
            String userEmail = email.getText().toString();
            resetPassword(userEmail);
        });
    }

    private void resetPassword(String userEmail) {
        auth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),"We sent you an email.",
                            Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Enter correct email address.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}