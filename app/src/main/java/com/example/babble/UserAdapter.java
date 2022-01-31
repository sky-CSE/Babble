package com.example.babble;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    FirebaseDatabase database;
    DatabaseReference reference;

    List<String> userList;
    String userName;
    Context mContext;

    public UserAdapter(List<String> userList, String userName, Context mContext) {
        this.userList = userList;
        this.userName = userName;
        this.mContext = mContext;

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        reference.child("Users").child(userList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String otherPersonUID = snapshot.getKey();
                String otherName = (String) snapshot.child("Username").getValue();
                String imageURL = (String) snapshot.child("Image").getValue();

                holder.textViewUserName.setText(otherName);

                if(imageURL==null || imageURL.equals("null") || imageURL.equals(""))
                    holder.imageViewUserImage.setImageResource(R.drawable.babble_logo);

                else
                    Picasso.get().load(imageURL).into(holder.imageViewUserImage);


                //sending userName and otherPerson's name to chatPage
                holder.cardView.setOnClickListener(view -> {
                    Intent i = new Intent(mContext,ChatPage.class);
                    i.putExtra("userName",userName);
                    i.putExtra("otherName",otherName);
                    i.putExtra("otherPersonUID",otherPersonUID);
                    i.putExtra("imageURL",imageURL);
                    mContext.startActivity(i);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName;
        CircleImageView imageViewUserImage;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.cardViewUserName);
            imageViewUserImage = itemView.findViewById(R.id.userprofile_profilepicture);
            cardView = itemView.findViewById(R.id.cardView);
        }

    }
}
