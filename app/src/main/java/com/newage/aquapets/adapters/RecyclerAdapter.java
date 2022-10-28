package com.newage.aquapets.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.newage.aquapets.R;
import com.newage.aquapets.models.TaskItems;

import java.util.ArrayList;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>{

    private ArrayList<TaskItems>arrayList;

    Context context;
    private OnItemClickListener onItemClickListener;



    public RecyclerAdapter(ArrayList<TaskItems> arrayList, String aquariumID, String AlarmType,OnItemClickListener onItemClickListener){
        this.arrayList=arrayList;
        this.onItemClickListener=onItemClickListener;

    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout,parent,false);

        return new RecyclerViewHolder(view);
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

            onItemClickListener.onClick(view,getLayoutPosition());


        }
    }
}
