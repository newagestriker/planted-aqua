package com.newage.plantedaqua;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;


import java.util.ArrayList;

import static android.view.View.GONE;


public class RecyclerAdapterPicsInfo extends RecyclerView.Adapter<RecyclerAdapterPicsInfo.RecyclerViewHolder>{

    private ArrayList<TankProgressDetails>arrayList;
    View v;
    private OnItemClickListener onItemClickListener;
    private RecyclerViewHolder recyclerViewHolder;

    Context context;

    RecyclerAdapterPicsInfo(ArrayList<TankProgressDetails> arrayList, Context context, OnItemClickListener onItemClickListener){
        this.arrayList=arrayList;
        this.context=context;
        this.onItemClickListener=onItemClickListener;

    }

    public interface OnItemClickListener{

       void onClick(View view, int position, String uri);
       void onLongClick(View view, int position);

    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.tankprogressrow,parent,false);
        this.recyclerViewHolder=new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        int i=0;

        if(TextUtils.isEmpty(arrayList.get(position).getText1())){

            holder.Text1.setVisibility(GONE);
        }
        else {
            holder.Text1.setVisibility(View.VISIBLE);
            holder.Text1.setText(arrayList.get(position).getText1());
        }

        if(TextUtils.isEmpty(arrayList.get(position).getText2())){
            holder.Text2.setVisibility(GONE);
        }
        else {
            holder.Text2.setVisibility(View.VISIBLE);
            holder.Text2.setText(arrayList.get(position).getText2());
        }

        if(TextUtils.isEmpty(arrayList.get(position).getImagedate())){
            holder.Date.setVisibility(GONE);
            i++;
            //System.out.println("Gone");
        }
        else {
            holder.Date.setVisibility(View.VISIBLE);
            holder.Date.setText(arrayList.get(position).getImagedate());
        }

        if(TextUtils.isEmpty(arrayList.get(position).getImageday())){
            holder.Day.setVisibility(GONE);
            i++;
        }
        else {
            holder.Day.setVisibility(View.VISIBLE);
            holder.Day.setText(arrayList.get(position).getImageday());
        }
       String s=arrayList.get(position).getImageuri();

        Uri tankpicUri;

        tankpicUri = Uri.parse(s);
        Glide.with(v.getContext())
                .load(tankpicUri)
                .apply(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.aquarium2))
                .into(holder.TankPic);
        holder.TankPic.setFocusable(true);
        holder.TankPic.setClickable(true);

        if(i==2){
            holder.linearLayout.setVisibility(GONE);
        }else{
            holder.linearLayout.setVisibility(View.VISIBLE);
        }


    }




    @Override
    public int getItemCount() {
        return arrayList.size();

    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        TextView Day,Date,Text1,Text2;
        ImageView TankPic;
        ProgressBar progressBar;
        LinearLayout linearLayout;


        RecyclerViewHolder(View view) {
            super(view);
            v=view;
            Text1=view.findViewById(R.id.Text1);
            Text2=view.findViewById(R.id.Text2);
            Day=view.findViewById(R.id.Day);
            Date=view.findViewById(R.id.Date);
            TankPic=view.findViewById(R.id.TankImage);
            TankPic.setOnClickListener(this);
            TankPic.setOnLongClickListener(this);
            progressBar=view.findViewById(R.id.CircularProgressBar);
            linearLayout=view.findViewById(R.id.linearDateDay);
        }

        @Override
        public void onClick(View view) {

            String uri=arrayList.get(getAdapterPosition()).imageuri;
            onItemClickListener.onClick(view,getAdapterPosition(),uri);


        }

        @Override
        public boolean onLongClick(View view) {


           onItemClickListener.onLongClick(view,getAdapterPosition());
           return true;


        }


    }

}
