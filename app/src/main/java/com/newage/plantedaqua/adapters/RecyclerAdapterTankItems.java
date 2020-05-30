package com.newage.plantedaqua.adapters;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.newage.plantedaqua.R;
import com.newage.plantedaqua.models.TankItems;

import java.util.ArrayList;

import static android.view.View.GONE;

public class RecyclerAdapterTankItems extends RecyclerView.Adapter<RecyclerAdapterTankItems.RecyclerViewHolder>{


    private ArrayList<TankItems> arrayList;
    View v;
    private RecyclerAdapterTankItems.OnItemClickListener onItemClickListener;
    String category;

    Context context;




    public RecyclerAdapterTankItems(ArrayList<TankItems> arrayList,String category, Context context, RecyclerAdapterTankItems.OnItemClickListener onItemClickListener){
        this.arrayList=arrayList;
        this.context=context;
        this.onItemClickListener=onItemClickListener;
        this.category=category;

    }






    public interface OnItemClickListener{

        void onClick(View view, int position, String uri);
        void onLongClick(View view, int position);

    }




    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.tank_item_list_layout,parent,false);
        RecyclerViewHolder recyclerViewHolder=new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterTankItems.RecyclerViewHolder holder, int position) {

        String Title2="",Title3="",Title4="";

        switch (category){
            case "E":
                Title2=context.getResources().getString(R.string.Quan);
                Title3=context.getResources().getString(R.string.AcDate);
                Title4=context.getResources().getString(R.string.ExpiryDate);
                break;
                default:
                    Title2=context.getResources().getString(R.string.Quan);
                    Title3=context.getResources().getString(R.string.Sciname);
                    Title4=context.getResources().getString(R.string.AcDate);

        }


        holder.Text1.setText(arrayList.get(position).getTxt1());

        if(TextUtils.isEmpty(arrayList.get(position).getTxt2())){
            holder.Text2.setVisibility(GONE);
        }
        else {
            holder.Text2.setVisibility(View.VISIBLE);
            holder.Text2.setText(Title2+" : "+arrayList.get(position).getTxt2());
        }

        if(TextUtils.isEmpty(arrayList.get(position).getTxt3())){
            holder.Text3.setVisibility(GONE);
        }
        else {
            holder.Text3.setVisibility(View.VISIBLE);
            holder.Text3.setText(Title3+" : "+arrayList.get(position).getTxt3());
        }

        if(TextUtils.isEmpty(arrayList.get(position).getTxt4())) {

            holder.Text4.setVisibility(GONE);
        }
        else {
            holder.Text4.setVisibility(View.VISIBLE);
            holder.Text4.setText(Title4+" : "+arrayList.get(position).getTxt4());
        }
        String s=arrayList.get(position).getItemUri();

        if(TextUtils.isEmpty(arrayList.get(position).getQuickNote())){
            holder.NoteImage.setVisibility(GONE);
        }
        else{
            holder.NoteImage.setVisibility(View.VISIBLE);
        }



            Uri tankpicUri;

            tankpicUri = Uri.parse(s);
            Glide.with(v.getContext())
                    .load(tankpicUri)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .placeholder(R.drawable.aquarium2)
                            .error(R.drawable.aquarium2))

                    .into(holder.Pic);




    }




    @Override
    public int getItemCount() {
        return arrayList.size();

    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        TextView Text1,Text2,Text3,Text4;
        ImageView Pic,NoteImage;
        CardView cardView;



        RecyclerViewHolder(View view) {
            super(view);
            v=view;
            NoteImage=view.findViewById(R.id.noteImage);
            cardView=view.findViewById(R.id.card1);
            Text1=view.findViewById(R.id.txt1);
            Text2=view.findViewById(R.id.txt2);
            Text3=view.findViewById(R.id.txt3);
            Text4=view.findViewById(R.id.txt4);
            Pic=view.findViewById(R.id.image1);
            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {

            String tag=arrayList.get(getLayoutPosition()).getTag();
            onItemClickListener.onClick(view,getLayoutPosition(),tag);


        }

        @Override
        public boolean onLongClick(View view) {


            onItemClickListener.onLongClick(view,getLayoutPosition());
            return true;


        }


    }

}
