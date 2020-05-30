package com.newage.plantedaqua.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Locale;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.newage.plantedaqua.models.LightDetails;
import com.newage.plantedaqua.R;

public class LightRecyclerAdapter extends RecyclerView.Adapter<LightRecyclerAdapter.RecyclerViewHolder> {


private ArrayList<LightDetails> arrayList;
private OnItemClickListener onItemClickListener;
private RecyclerViewHolder recyclerViewHolder;




        private Context context;




        public LightRecyclerAdapter(ArrayList<LightDetails> arrayList, Context context, OnItemClickListener onItemClickListener){
        this.arrayList=arrayList;
        this.context=context;
        this.onItemClickListener=onItemClickListener;




        }



public interface OnItemClickListener{

    void onClick(View view, int position);
}




    @NonNull
    @Override
    public LightRecyclerAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.each_light_row_layout,parent,false);
        this.recyclerViewHolder=new LightRecyclerAdapter.RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LightRecyclerAdapter.RecyclerViewHolder holder, int position) {

            holder.LightType.setText(arrayList.get(position).getLightType());
            holder.LightCount.setText(arrayList.get(position).getCount());
            holder.LumensPerWattLight.setText(arrayList.get(position).getLumensPerWatt());
            holder.WattPerCount.setText(arrayList.get(position).getWattPerCount());
            holder.EffectiveLumens.setText(String.format(Locale.getDefault(),"%.2f",arrayList.get(position).getTotalLumens()));


    }




    @Override
    public int getItemCount() {
        return arrayList.size();

    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView LumensPerWattLight,LightCount,WattPerCount,LightType,EffectiveLumens;
    ImageView deleteLight;

    RecyclerViewHolder(View view) {
        super(view);

        LightType = view.findViewById(R.id.LightText);
        LumensPerWattLight = view.findViewById(R.id.LumensWattText);
        LightCount = view.findViewById(R.id.LightCountText);
        WattPerCount = view.findViewById(R.id.WattPerCountText);
        deleteLight = view.findViewById(R.id.DeleteLight);
        EffectiveLumens = view.findViewById(R.id.EffectiveLumensText);
        deleteLight.setOnClickListener(this);




    }

    @Override
    public void onClick(View view) {

        onItemClickListener.onClick(view,getLayoutPosition());



    }


}

}
