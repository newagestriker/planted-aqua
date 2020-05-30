package com.newage.plantedaqua.adapters;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.newage.plantedaqua.R;
import com.newage.plantedaqua.activities.TankProgressDetails;


import java.util.ArrayList;

import static android.view.View.GONE;


public class RecyclerAdapterPicsInfo extends RecyclerView.Adapter<RecyclerAdapterPicsInfo.RecyclerViewHolder>{

    private ArrayList<TankProgressDetails>arrayList;
    View v;
    private OnItemClickListener onItemClickListener;
    private RecyclerViewHolder recyclerViewHolder;
    private int layoutID;

    Context context;

    public RecyclerAdapterPicsInfo(ArrayList<TankProgressDetails> arrayList, Context context,int layoutID, OnItemClickListener onItemClickListener){
        this.arrayList=arrayList;
        this.context=context;
        this.onItemClickListener=onItemClickListener;
        this.layoutID = layoutID;

    }

    public interface OnItemClickListener{

       void onClick(View view, int position, String uri);
       void onLongClick(View view, int position);

    }
//R.layout.tankprogressrow
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(layoutID,parent,false);
        this.recyclerViewHolder=new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {


        int i=0;

            if(layoutID== R.layout.tankprogressrow) {

                if (TextUtils.isEmpty(arrayList.get(position).getText1())) {

                    holder.Text1.setVisibility(GONE);
                } else {
                    holder.Text1.setVisibility(View.VISIBLE);
                    holder.Text1.setText(arrayList.get(position).getText1());
                }
            }
            else {

                holder.Text1.setVisibility(GONE);


                if (TextUtils.isEmpty(arrayList.get(position).getText1())) {

                    holder.banner.setVisibility(GONE);
                } else {
                    holder.banner.setVisibility(View.VISIBLE);
                    holder.banner.setText(arrayList.get(position).getText1());
                }
            }

            if (TextUtils.isEmpty(arrayList.get(position).getText2())) {
                holder.Text2.setVisibility(GONE);
            } else {
                holder.Text2.setVisibility(View.VISIBLE);
                holder.Text2.setText(arrayList.get(position).getText2());
            }

            if (TextUtils.isEmpty(arrayList.get(position).getImagedate())) {
                holder.Date.setVisibility(GONE);
                i++;
                //System.out.println("Gone");
            } else {
                holder.Date.setVisibility(View.VISIBLE);
                holder.Date.setText(arrayList.get(position).getImagedate());
            }

            if (TextUtils.isEmpty(arrayList.get(position).getImageday())) {
                holder.Day.setVisibility(GONE);
                i++;
            } else {
                holder.Day.setVisibility(View.VISIBLE);
                holder.Day.setText(arrayList.get(position).getImageday());
            }

            if(i==2){
                holder.linearLayout.setVisibility(GONE);
            }else{
                holder.linearLayout.setVisibility(View.VISIBLE);
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



    }




    @Override
    public int getItemCount() {
        return arrayList.size();

    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        TextView banner;
        TextView Day,Date,Text1,Text2;
        ImageView TankPic;
        ProgressBar progressBar;
        LinearLayout linearLayout;
        ImageButton editTank, infoTank, settingsTank;


        RecyclerViewHolder(View view) {
            super(view);
            v=view;


                Text1 = view.findViewById(R.id.Text1);
                Text2 = view.findViewById(R.id.Text2);
                Day = view.findViewById(R.id.Day);
                Date = view.findViewById(R.id.Date);
                linearLayout = view.findViewById(R.id.linearDateDay);

            progressBar = view.findViewById(R.id.CircularProgressBar);
            TankPic = view.findViewById(R.id.TankImage);
            TankPic.setOnLongClickListener(this);
            if(layoutID!=R.layout.tankprogressrow) {
                editTank = view.findViewById(R.id.TankEdit);
                editTank.setTag(1);
                editTank.setOnClickListener(this);
                settingsTank = view.findViewById(R.id.TankSettings);
                settingsTank.setTag(2);
                settingsTank.setOnClickListener(this);
                infoTank = view.findViewById(R.id.TankInfo);
                infoTank.setOnClickListener(this);
                infoTank.setTag(3);
            }
            if(layoutID==R.layout.each_tank_layout)
                banner = view.findViewById(R.id.TankNameTextView);
        }

        @Override
        public void onClick(View view) {

            String uri=arrayList.get(getLayoutPosition()).imageuri;
            onItemClickListener.onClick(view,getLayoutPosition(),uri);
        }

        @Override
        public boolean onLongClick(View view) {

            onItemClickListener.onLongClick(view,getLayoutPosition());
            return true;


        }


    }

}
