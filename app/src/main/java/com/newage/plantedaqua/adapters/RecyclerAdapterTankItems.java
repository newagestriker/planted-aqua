package com.newage.plantedaqua.adapters;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.newage.plantedaqua.R;
import com.newage.plantedaqua.models.TankItems;

import java.util.ArrayList;

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
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterTankItems.RecyclerViewHolder holder, int position) {

//        String Title2="",Title3="",Title4="";
//
//        if ("E".equals(category)) {
//            Title2 = context.getResources().getString(R.string.Quan);
//            Title3 = context.getResources().getString(R.string.AcDate);
//            Title4 = context.getResources().getString(R.string.ExpiryDate);
//        } else {
//            Title2 = context.getResources().getString(R.string.Quan);
//            Title3 = context.getResources().getString(R.string.Sciname);
//            Title4 = context.getResources().getString(R.string.AcDate);
//        }
//
//        holder.tankItemCheckBox.setTag(arrayList.get(position).tag);
//        holder.tankItemCheckBox.setChecked(arrayList.get(position).getChecked());
//
//        holder.Text1.setText(arrayList.get(position).txt1);
//
//        holder.tankItemCheckBox.setVisibility(arrayList.get(position).getShown()?View.VISIBLE:GONE);
//
//        if(TextUtils.isEmpty(arrayList.get(position).txt2)){
//            holder.Text2.setVisibility(GONE);
//        }
//        else {
//            holder.Text2.setVisibility(View.VISIBLE);
//            holder.Text2.setText(Title2+" : "+ arrayList.get(position).txt2);
//        }
//
//        if(TextUtils.isEmpty(arrayList.get(position).txt3)){
//            holder.Text3.setVisibility(GONE);
//        }
//        else {
//            holder.Text3.setVisibility(View.VISIBLE);
//            holder.Text3.setText(Title3+" : "+ arrayList.get(position).txt3);
//        }
//
//        if(TextUtils.isEmpty(arrayList.get(position).txt4)) {
//
//            holder.Text4.setVisibility(GONE);
//        }
//        else {
//            holder.Text4.setVisibility(View.VISIBLE);
//            holder.Text4.setText(Title4+" : "+ arrayList.get(position).txt4);
//        }
//        String s= arrayList.get(position).itemUri;
//
//        if(TextUtils.isEmpty(arrayList.get(position).quickNote)){
//            holder.NoteImage.setVisibility(GONE);
//        }
//        else{
//            holder.NoteImage.setVisibility(View.VISIBLE);
//        }
//


            Uri tankpicUri;

//            tankpicUri = Uri.parse(s);
//            Glide.with(v.getContext())
//                    .load(tankpicUri)
//                    .apply(new RequestOptions()
//                            .diskCacheStrategy(DiskCacheStrategy.NONE)
//                            .skipMemoryCache(true)
//                            .placeholder(R.drawable.aquarium2)
//                            .error(R.drawable.aquarium2))
//
//                    .into(holder.Pic);




    }




    @Override
    public int getItemCount() {
        return arrayList.size();

    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        TextView Text1,Text2,Text3,Text4;
        ImageView Pic,NoteImage;
        CardView cardView;
        CheckBox tankItemCheckBox;



        RecyclerViewHolder(View view) {
            super(view);
            v=view;
            NoteImage=view.findViewById(R.id.noteImage);
            cardView=view.findViewById(R.id.card1);
            Text1=view.findViewById(R.id.txt1);
            Text2=view.findViewById(R.id.txt2);
            Text3=view.findViewById(R.id.txt3);
            Text4=view.findViewById(R.id.txt4);
            Pic=view.findViewById(R.id.tankItemImage);
            tankItemCheckBox = view.findViewById(R.id.tankItemCheckBox);
            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
            tankItemCheckBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

           // String tag= arrayList.get(getLayoutPosition()).tag;
          //  onItemClickListener.onClick(view,getLayoutPosition(),tag);


        }

        @Override
        public boolean onLongClick(View view) {


            onItemClickListener.onLongClick(view,getLayoutPosition());
            return true;


        }


    }

}
