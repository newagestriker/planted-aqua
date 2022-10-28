package com.newage.aquapets.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.newage.aquapets.models.ChatBoxObject;
import com.newage.aquapets.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.RecyclerViewHolder>{

    private ArrayList<ChatBoxObject> arrayList;
    Context context;




    public ChatRecyclerAdapter(ArrayList<ChatBoxObject> arrayList,Context context){
        this.arrayList=arrayList;
        this.context = context;

    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.each_chat,parent,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        holder.chatEnteredText.setText(arrayList.get(position).getMSG());
        holder.userNameText.setText(arrayList.get(position).getDN());
        Glide
                .with(context)
                .load(arrayList.get(position).getPU())
                .into(holder.chatImageView);



    }


    @Override
    public int getItemCount() {
        return arrayList.size();

    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView chatImageView;
        TextView chatEnteredText;
        TextView userNameText;

        RecyclerViewHolder(View view){
            super(view);

            userNameText = view.findViewById(R.id.DNText);

            chatImageView = view.findViewById(R.id.ChatUserImage);
            chatEnteredText = view.findViewById(R.id.ChatEnteredText);


        }

    }
}