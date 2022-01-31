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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpPage extends AppCompatActivity {
    CircleImageView profileImage;
    EditText username;
    EditText email;
    EditText password;
    Button register;
    boolean isImage; //whether image is there or not

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference dbRef;
    FirebaseStorage storage;
    StorageReference storageRef;
     Uri imageUri; //uri of profile image


    //here as in place of userUid auth.getUid will be used as it is signUp page so user doesn't exist
    // We're in signUp , we're here bc user doesn't exist
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();//accessed database
        dbRef = database.getReference();
        storage = FirebaseStorage.getInstance();
        storageRef=storage.getReference();//Create Cloud Storage reference from the app

        profileImage = findViewById(R.id.userprofile_profilepicture);
        username = findViewById(R.id.editTextSignUpUserName);
        email = findViewById(R.id.editTextSignUpEmail);
        password = findViewById(R.id.editTextSignUpPassword);
        register = findViewById(R.id.buttonSignUpRegister);

        profileImage.setOnClickListener(view -> imageChooser());

        register.setOnClickListener(view -> {
            String userName = username.getText().toString();
            String userEmail = email.getText().toString();
            String userPassword = password.getText().toString();
            if(!userName.equals("") && !userEmail.equals("") && !userPassword.equals(""))
            SignUp(userName, userEmail, userPassword);
        });
    }

    private void SignUp(String userName, String userEmail, String userPassword) {
        auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(SignUpPage.this, task -> {
                    if (task.isSuccessful()) {
                        //auth.getCurrentUser is used as User with email and password is created successfully
                        FirebaseUser user = auth.getCurrentUser();
                        dbRef.child("Users").child(Objects.requireNonNull(auth.getUid())).child("Username").setValue(userName);

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
                                        dbRef.child("Users").child(Objects.requireNonNull(auth.getUid())).child("Image").setValue(String.valueOf(downloadUri));
                                        Toast.makeText(getApplicationContext(),"Write to database is successful",Toast.LENGTH_LONG).show();
                                    } else {
                                        // Handle failures
                                        // ...
                                        Toast.makeText(getApplicationContext(),"Write to database is failed",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else
                            dbRef.child("Users").child(auth.getUid()).child("Image").setValue("null");

                        Intent i = new Intent(SignUpPage.this, HomePage.class);
                        startActivity(i);
                        finish();
                        Toast.makeText(getApplicationContext(),"Welcome to BABBLE!",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SignUpPage.this, "There's a problem.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            isImage = true;
            Picasso.get().load(imageUri).into(profileImage);
        }
        else{
            isImage = false;
        }
    }

}
