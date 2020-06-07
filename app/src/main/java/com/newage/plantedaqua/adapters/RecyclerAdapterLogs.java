package com.newage.plantedaqua.adapters;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.newage.plantedaqua.models.LogData;
import com.newage.plantedaqua.R;

import java.util.ArrayList;



public class RecyclerAdapterLogs extends RecyclerView.Adapter<RecyclerAdapterLogs.RecyclerViewHolder>{

    private ArrayList<LogData>arrayList;
    private OnItemClickListener onItemClickListener;


    Context context;




    public RecyclerAdapterLogs(ArrayList<LogData> arrayList, Context context, OnItemClickListener onItemClickListener){
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
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.each_log_layout,parent,false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        String dy = arrayList.get(position).getDt();
        holder.Day.setText(arrayList.get(position).getDy());
        holder.Date.setText(arrayList.get(position).getDt());
        if(arrayList.get(position).getStatus()!=null)
            holder.Status.setText(arrayList.get(position).getStatus());
        else {
            holder.Change.setVisibility(View.GONE);
            holder.DeleteLog.setVisibility(View.GONE);
        }
        holder.Task.setText(arrayList.get(position).getTask());
        holder.Category.setText(arrayList.get(position).getCategory());


    }




    @Override
    public int getItemCount() {
        return arrayList.size();

    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView Day,Date,Task,Status,Category;
        ImageView Change,DeleteLog;



        RecyclerViewHolder(View view) {
            super(view);

            Day=view.findViewById(R.id.Log_day);
            Date=view.findViewById(R.id.Log_time);
            Task=view.findViewById(R.id.Log_TaskName);
            Status=view.findViewById(R.id.Log_taskStatus);
            Change=view.findViewById(R.id.Log_ImageChange);
            Change.setTag(1);
            Category=view.findViewById(R.id.Log_TaskCategory);
            DeleteLog=view.findViewById(R.id.Log_ImageCross);
            DeleteLog.setTag(2);
            Change.setOnClickListener(this);
            DeleteLog.setOnClickListener(this);
            Day.setTag(3);
            Day.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

                onItemClickListener.onClick(view,getLayoutPosition());



        }


    }

}

