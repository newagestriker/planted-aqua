package com.newage.plantedaqua;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;


public class RecyclerAdapterLogs extends RecyclerView.Adapter<RecyclerAdapterLogs.RecyclerViewHolder>{

    private ArrayList<LogData>arrayList;
    private OnItemClickListener onItemClickListener;
    private RecyclerViewHolder recyclerViewHolder;




    Context context;




    RecyclerAdapterLogs(ArrayList<LogData> arrayList, Context context, OnItemClickListener onItemClickListener){
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
        this.recyclerViewHolder=new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.Day.setText(arrayList.get(position).getDy());
        holder.Date.setText(arrayList.get(position).getDt());
        holder.Status.setText(context.getResources().getString(R.string.Status)+" "+arrayList.get(position).getStatus());
        holder.Task.setText(arrayList.get(position).getCategory()+" : "+arrayList.get(position).getTask());



    }




    @Override
    public int getItemCount() {
        return arrayList.size();

    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView Day,Date,Task,Status;
        ImageView Change;


        RecyclerViewHolder(View view) {
            super(view);

            Day=view.findViewById(R.id.Log_day);
            Date=view.findViewById(R.id.Log_time);
            Task=view.findViewById(R.id.Log_TaskName);
            Status=view.findViewById(R.id.Log_taskStatus);
            Change=view.findViewById(R.id.Log_ImageChange);
            Change.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            onItemClickListener.onClick(view,getAdapterPosition());


        }


    }

}

