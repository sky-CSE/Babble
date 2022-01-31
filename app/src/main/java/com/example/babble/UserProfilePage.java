package com.example.babble;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfilePage extends AppCompatActivity {
    CircleImageView circularImageViewProfile;
    EditText editTextUserName;
    Button update;

    boolean isImage = false; //whether image is there or not

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference dbRef;
    FirebaseStorage storage;
   private StorageReference storageRef;
    private Uri imageUri; //uri of profile image
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
        }

        database = FirebaseDatabase.getInstance();//accessed database
        dbRef = database.getReference();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference(); //Create Cloud Storage reference from the app

        circularImageViewProfile = findViewById(R.id.userprofile_profilepicture);
        editTextUserName = findViewById(R.id.userprofile_username);
        update = findViewById(R.id.userprofile_update);
        //show info
        getUserInfo();

        circularImageViewProfile.setOnClickListener(view -> imageChooser());

        update.setOnClickListener(view -> updateProfile());
    }

    private void getUserInfo() {
        dbRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = (String) snapshot.child("Username").getValue();
                String image = (String) snapshot.child("Image").getValue();
                editTextUserName.setText(userName);

                //Glide.with(getApplicationContext()).load(imageReference).into(circularImageViewProfile);
                    if (image.equals("null") || image.equals("") || image == null)
                        circularImageViewProfile.setImageResource(R.drawable.babble_logo);
                    else
                        Picasso.get().load(image).into(circularImageViewProfile);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateProfile() {
        String userName = editTextUserName.getText().toString();
        dbRef.child("Users").child(currentUserId).child("Username").setValue(userName);

        if (isImage) {
            String imageName = "images/" + UUID.randomUUID() + ".jpg";

                final StorageReference ref = storageRef.child(imageName);
            UploadTask uploadTask = ref.putFile(imageUri);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }

                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            dbRef.child("Users").child(Objects.requireNonNull(user.getUid())).child("Image").setValue(String.valueOf(downloadUri));

                        } else {
                            // Handle failures
                            // ...
                            Toast.makeText(getApplicationContext(),"Image not updated",Toast.LENGTH_LONG).show();
                        }
                    }
                });
        }
        //sending userName to HomePage and also going there
        Intent i = new Intent(UserProfilePage.this, HomePage.class);
        startActivity(i);
        Toast.makeText(getApplicationContext(),"Profile Updated",Toast.LENGTH_LONG).show();
    }

    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            isImage = true;
            Picasso.get().load(imageUri).into(circularImageViewProfile);
        }
        else
            isImage = false;
    }
}