package com.example.babble;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    List<ModelClass> list;
    String username;
    FirebaseAuth auth =FirebaseAuth.getInstance();
    boolean status;
    int send;
    int receive;

    public MessageAdapter(List<ModelClass> list, String username) {
        this.list = list;
        this.username = username;

        status = false;
        send = 1;
        receive = 2;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if(viewType == send)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_send,parent,false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_receive,parent,false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String message = list.get(position).getMessage();
        if(message==null)
            //setting message(s) on textView of cardView.
            holder.textView.setText("");

        else
            holder.textView.setText(message);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView textView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            if(status){ //if the status is true i.e. the message has been sent
                textView = itemView.findViewById(R.id.textViewSend);
            }
            else
                textView = itemView.findViewById(R.id.textViewReceive);
        }
    }

    @Override
    public int getItemViewType(int position) {
            //checking whether the message is from sender or from receiver
            if (list.get(position).getFrom().equals(auth.getUid())) {
                status = true;
                return send;
            } else {
                status = false;
                return receive;
            }
        }
    }

