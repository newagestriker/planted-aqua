package com.newage.aquapets.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.newage.aquapets.room.SingleChatObject;
import com.newage.aquapets.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class SingleChatRecyclerAdapter extends RecyclerView.Adapter<SingleChatRecyclerAdapter.RecyclerViewHolder>{

    private ArrayList<SingleChatObject> arrayList;
    Context context;
    String myUserID;




    public SingleChatRecyclerAdapter(ArrayList<SingleChatObject> arrayList,Context context,String myUserID){
        this.arrayList=arrayList;
        this.context = context;
        this.myUserID = myUserID;

    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.each_chat,parent,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        holder.chatEnteredText.setText(arrayList.get(position).getChatMsg());


            Glide
                    .with(context)
                    .load(arrayList.get(position).getChatPhotoURL())
                    .apply(new RequestOptions()
                            .error(R.drawable.profle))
                    .into(holder.chatImageView);

            if(arrayList.get(position).getChatUserID().equals(myUserID)){
                holder.userNameText.setText("Me");
                holder.userNameText.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }
            else {
                holder.userNameText.setText(arrayList.get(position).getChatDisplayName());
                holder.userNameText.setTextColor(context.getResources().getColor(R.color.colorAccent));
            }





    }


    @Override
    public int getItemCount() {
        return arrayList.size();

    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView chatImageView;
        TextView chatEnteredText;
        TextView userNameText;
        CardView chatCard;

        RecyclerViewHolder(View view){
            super(view);

            userNameText = view.findViewById(R.id.DNText);

            chatImageView = view.findViewById(R.id.ChatUserImage);
            chatEnteredText = view.findViewById(R.id.ChatEnteredText);
            chatCard = view.findViewById(R.id.ChatCard);


        }

    }
}
