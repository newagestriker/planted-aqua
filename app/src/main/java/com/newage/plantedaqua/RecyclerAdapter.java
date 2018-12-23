package com.newage.plantedaqua;

import android.content.Context;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>{

    private ArrayList<TaskItems>arrayList;

    private String aquariumID;
    private String AlarmType;
    Context context;
    private OnItemClickListener onItemClickListener;



    RecyclerAdapter(ArrayList<TaskItems> arrayList, String aquariumID, String AlarmType,OnItemClickListener onItemClickListener){
        this.arrayList=arrayList;
        this.aquariumID=aquariumID;
        this.AlarmType=AlarmType;
        this.onItemClickListener=onItemClickListener;

    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout,parent,false);
        RecyclerViewHolder recyclerViewHolder=new RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        String tsk;
        tsk=arrayList.get(position).getAlarmName();
        holder.TaskName.setText(tsk);
        holder.DeleteTask.setTag("Delete");
        holder.rowDate.setText(arrayList.get(position).getAlarmDays());
        holder.rowTime.setText(arrayList.get(position).getAlarmTime());


    }

    public interface OnItemClickListener{

        void onClick(View view, int position);

    }




    @Override
    public int getItemCount() {
        return arrayList.size();

    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Button TaskName;
        ImageView DeleteTask;
        TextView rowTime,rowDate;

        RecyclerViewHolder(View view){
            super(view);
            TaskName= view.findViewById(R.id.TaskButton);
            TaskName.setOnClickListener(this);
            DeleteTask=view.findViewById(R.id.deleteImage);
            DeleteTask.setOnClickListener(this);
            rowTime=view.findViewById(R.id.rowTime);
            rowDate=view.findViewById(R.id.rowDate);


        }

        @Override
        public void onClick(View view) {

            onItemClickListener.onClick(view,getAdapterPosition());


        }
    }
}
