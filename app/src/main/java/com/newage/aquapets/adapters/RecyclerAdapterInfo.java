package com.newage.aquapets.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.newage.aquapets.R;
import com.newage.aquapets.models.TankAdviceInfo;

import java.util.ArrayList;

public class RecyclerAdapterInfo extends RecyclerView.Adapter<RecyclerAdapterInfo.RecyclerViewHolder>{

    private ArrayList<TankAdviceInfo> arrayList;
    private OnItemClickListener onItemClickListener;


    Context context;




    public RecyclerAdapterInfo(ArrayList<TankAdviceInfo> arrayList, Context context, OnItemClickListener onItemClickListener){
        this.arrayList=arrayList;
        this.context=context;
        this.onItemClickListener=onItemClickListener;

    }


    public interface OnItemClickListener{

        void onClick(View view, int position);
    }




    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.each_info_layout,parent,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {


        holder.infoData.setText(arrayList.get(position).getInfoMessage());
        holder.infoDate.setText(arrayList.get(position).getInfoDate());
        holder.infoType.setText(arrayList.get(position).getInfoType());



    }




    @Override
    public int getItemCount() {
        return arrayList.size();

    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView infoData,infoType,infoDate;



        RecyclerViewHolder(View view) {
            super(view);

            infoData=view.findViewById(R.id.infoData);
            infoType=view.findViewById(R.id.infoType);
            infoDate=view.findViewById(R.id.infoDate);


        }

        @Override
        public void onClick(View view) {

            onItemClickListener.onClick(view,getLayoutPosition());



        }


    }

}