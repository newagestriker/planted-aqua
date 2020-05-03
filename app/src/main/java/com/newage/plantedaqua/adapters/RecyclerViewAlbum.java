package com.newage.plantedaqua.adapters;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.newage.plantedaqua.R;

import java.util.ArrayList;

public class RecyclerViewAlbum extends RecyclerView.Adapter<RecyclerViewAlbum.RecyclerViewHolder>{

    ArrayList<String> arrayList;
    View v;
    OnItemClickListener onItemClickListener;
    RecyclerViewHolder recyclerViewHolder;




    Context context;




    public RecyclerViewAlbum(ArrayList<String> arrayList, Context context, OnItemClickListener onItemClickListener){
        this.arrayList=arrayList;
        this.context=context;
        this.onItemClickListener=onItemClickListener;

    }






    public interface OnItemClickListener{

        void onClick(View view, int position);
        void onLongClick(View view,int position);
    }




    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.image_layout,parent,false);
        this.recyclerViewHolder=new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        Uri PicUri=Uri.parse(arrayList.get(position));
        Glide.with(v.getContext())
                .load(PicUri)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .error(R.drawable.aquarium2))
                .into(holder.Pic);
    }




    @Override
    public int getItemCount() {
        return arrayList.size();

    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

        ImageView Pic;


        RecyclerViewHolder(View view) {
            super(view);
            v=view;
            Pic=view.findViewById(R.id.Pic);
            Pic.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            onItemClickListener.onClick(view,getAdapterPosition());


        }

        @Override
        public boolean onLongClick(View view) {
            onItemClickListener.onLongClick(view,getAdapterPosition());
            return true;
        }
    }

}

