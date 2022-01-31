package com.example.babble;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatPage extends AppCompatActivity {
    FirebaseAuth auth;
    private EditText messageEditText;

    private String userName,otherName;

    private DatabaseReference reference;

    MessageAdapter messageAdapter;
    List<ModelClass> list;

    RecyclerView recyclerViewMessages;
    private String otherPersonUID;
    private String otherPersonImageURL;
    // private boolean isFirstMessage=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);

        auth=FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Messages");

        ImageView backImage = findViewById(R.id.chatpage_back_imageview);
        ImageView otherPFP = findViewById(R.id.chatpage_profilepicture);
        TextView otherPersonName = findViewById(R.id.chatpage_username_textview);
        messageEditText = findViewById(R.id.chatpage_multiline);
        FloatingActionButton sendFAB = findViewById(R.id.chatpage_fab);
        recyclerViewMessages = findViewById(R.id.chatpage_recyclerview);

        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();

        //info coming from user adapter
        userName = getIntent().getStringExtra("userName");
        otherName = getIntent().getStringExtra("otherName");
        otherPersonUID = getIntent().getStringExtra("otherPersonUID");
        otherPersonImageURL = getIntent().getStringExtra("imageURL");

        //bc when chatting we see other persons name
        otherPersonName.setText(otherName);
        if(otherPersonImageURL.equals("null") || otherPersonImageURL.equals(""))
            Picasso.get().load(R.drawable.babble_logo).into(otherPFP);
        else
        Picasso.get().load(otherPersonImageURL).into(otherPFP);

        backImage.setOnClickListener(view->{
            Intent i = new Intent(ChatPage.this,HomePage.class);
            startActivity(i);
        });

        otherPFP.setOnClickListener(view->{
            Intent i = new Intent(ChatPage.this,OtherProfilePage.class);
            i.putExtra("otherName",otherName);
            i.putExtra("otherPersonalImageURL",otherPersonImageURL);
            startActivity(i);
        });
        sendFAB.setOnClickListener(view -> {
            String message = messageEditText.getText().toString();
            if(!message.equals(""))
            sendMessage(message);

            messageEditText.setText("");
        });
        //method to fetch and show the messages
        //we don't want to fetch messages for first time bc no message exists as it is first time
        //there fore checking whether it is first message or not
        //if(!isFirstMessage)
            showMessages();
    }

    private void showMessages() {
        //made reference here bc when it throws out for loading other classes & gets back to
        //flow, 'reference' was reinitialised to 'root' of db thus created a new reference here
        DatabaseReference savedMessageReference = reference.child(Objects.requireNonNull(auth.getUid())).child(otherPersonUID);
        savedMessageReference.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ModelClass modelClass = snapshot.getValue(ModelClass.class);
                list.add(modelClass);
                messageAdapter.notifyDataSetChanged();
                //so that recent message is always on top
                recyclerViewMessages.scrollToPosition(list.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        messageAdapter = new MessageAdapter(list,userName);
        recyclerViewMessages.setAdapter(messageAdapter);
    }

    private void sendMessage(String message) {
       final String key = reference.child(Objects.requireNonNull(auth.getUid())).child(otherPersonUID).push().getKey(); //push methods generated unique id
       final Map<String,Object> messageMap = new HashMap<>();
       messageMap.put("message",message);
       messageMap.put("from",auth.getUid());
        if (key != null) {
            reference.child(auth.getUid()).child(otherPersonUID).child(key).setValue(messageMap).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    //isFirstMessage = false; //from now onwards the msg is not the first
                    //bc both sides will have the same msg
                    reference.child(otherPersonUID).child(auth.getUid()).child(key).setValue(messageMap);
                }
            });
        }
    }
}