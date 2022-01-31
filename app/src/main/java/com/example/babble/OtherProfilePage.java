package com.example.babble;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class OtherProfilePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile_page);

        TextView usernameField = findViewById(R.id.otherprofile_username);
        TextView statusField = findViewById(R.id.otherprofile_status);
        ImageView profilePicture = findViewById(R.id.otherprofile_profilepicture);

        String username = getIntent().getStringExtra("otherName");
        String imageURL = getIntent().getStringExtra("otherPersonImageURL");

        usernameField.setText(""+username);

        if(imageURL.equals("null"))
        Picasso.get().load(R.drawable.babble_logo).into(profilePicture);
        else
            Picasso.get().load(imageURL).into(profilePicture);

    }
}