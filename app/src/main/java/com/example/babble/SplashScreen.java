package com.example.babble;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        TextView name=findViewById(R.id.textViewIntroName);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.intro);
        name.startAnimation(animation);

        //after 5000 milliseconds open login page
        new Handler().postDelayed(() -> {
            Intent homeIntent = new Intent(SplashScreen.this,LoginPage.class);
            startActivity(homeIntent);
            finish();
        },5000);


    }
}