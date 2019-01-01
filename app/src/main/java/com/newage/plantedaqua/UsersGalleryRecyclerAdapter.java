package com.newage.plantedaqua;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;



public class UsersGalleryRecyclerAdapter extends RecyclerView.Adapter<UsersGalleryRecyclerAdapter.RecyclerViewHolder> {

    private ArrayList<GalleryInfo> arrayList;
    private OnItemClickListener onItemClickListener;
    private Context context;
    private String User_ID;

    public UsersGalleryRecyclerAdapter(Context context, ArrayList<GalleryInfo> arrayList, String User_ID, OnItemClickListener onItemClickListener) {

        this.arrayList = arrayList;
        this.User_ID = User_ID;
        this.onItemClickListener = onItemClickListener;
        this.context = context;
    }

    public interface OnItemClickListener {

        void onClick(View view, int position);
    }



    @NonNull
    @Override
    public UsersGalleryRecyclerAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_layout,viewGroup,false);
        return new RecyclerViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull UsersGalleryRecyclerAdapter.RecyclerViewHolder recyclerViewHolder, int i) {

        setImage(i,recyclerViewHolder.galleyItemImageView,recyclerViewHolder.progressBar);

        if(TextUtils.isEmpty(arrayList.get(i).getFacebookURL())) {
            recyclerViewHolder.facebookImageView.setEnabled(false);
            recyclerViewHolder.facebookImageView.setColorFilter(Color.argb(150,200,200,200));

        }
        else
        {
            recyclerViewHolder.facebookImageView.setEnabled(true);
            recyclerViewHolder.facebookImageView.setColorFilter(null);

        }

        if(TextUtils.isEmpty(arrayList.get(i).getTwitterURL())) {
            recyclerViewHolder.twitterImageView.setEnabled(false);
            recyclerViewHolder.twitterImageView.setColorFilter(Color.argb(150,200,200,200));

        }
        else
        {
            recyclerViewHolder.twitterImageView.setEnabled(true);
            recyclerViewHolder.twitterImageView.setColorFilter(null);

        }

        if(TextUtils.isEmpty(arrayList.get(i).getInstagramURL())) {
            recyclerViewHolder.instagramImageView.setEnabled(false);
            recyclerViewHolder.instagramImageView.setColorFilter(Color.argb(150,200,200,200));

        }
        else
        {
            recyclerViewHolder.instagramImageView.setEnabled(true);
            recyclerViewHolder.instagramImageView.setColorFilter(null);

        }

        if(TextUtils.isEmpty(arrayList.get(i).getWebsiteURL())) {
            recyclerViewHolder.webImageView.setEnabled(false);
            recyclerViewHolder.webImageView.setColorFilter(Color.argb(150,200,200,200));

        }
        else
        {
            recyclerViewHolder.webImageView.setEnabled(true);
            recyclerViewHolder.webImageView.setColorFilter(null);

        }

        if(TextUtils.isEmpty(arrayList.get(i).getAuthorsName())) {
            recyclerViewHolder.authorsNameTextView.setVisibility(View.GONE);
            recyclerViewHolder.authorsNameTitle.setVisibility(View.GONE);
            recyclerViewHolder.circularDPImageView.setVisibility(View.GONE);
        }
        else
        {

            recyclerViewHolder.authorsNameTextView.setVisibility(View.VISIBLE);
            recyclerViewHolder.authorsNameTitle.setVisibility(View.VISIBLE);
            recyclerViewHolder.circularDPImageView.setVisibility(View.VISIBLE);
            recyclerViewHolder.authorsNameTextView.setText(arrayList.get(i).getAuthorsName());
        }

        if(TextUtils.isEmpty(arrayList.get(i).getTech())) {
            recyclerViewHolder.techTextView.setVisibility(View.GONE);
            recyclerViewHolder.techTitle.setVisibility(View.GONE);

        }
        else
        {

            recyclerViewHolder.techTitle.setVisibility(View.VISIBLE);
            recyclerViewHolder.techTextView.setVisibility(View.VISIBLE);
            recyclerViewHolder.techTextView.setText(arrayList.get(i).getTech());

        }

        if(TextUtils.isEmpty(arrayList.get(i).getFts())) {
            recyclerViewHolder.ftsTextView.setVisibility(View.GONE);
            recyclerViewHolder.ftsTitle.setVisibility(View.GONE);

        }
        else
        {

            recyclerViewHolder.ftsTextView.setVisibility(View.VISIBLE);
            recyclerViewHolder.ftsTitle.setVisibility(View.VISIBLE);
            recyclerViewHolder.ftsTextView.setText(arrayList.get(i).getFts());

        }
        if(TextUtils.isEmpty(arrayList.get(i).getFlora())) {
            recyclerViewHolder.floraTitle.setVisibility(View.GONE);
            recyclerViewHolder.floraTextView.setVisibility(View.GONE);

        }
        else
        {

            recyclerViewHolder.floraTextView.setVisibility(View.VISIBLE);
            recyclerViewHolder.floraTitle.setVisibility(View.VISIBLE);
            recyclerViewHolder.floraTextView.setText(arrayList.get(i).getFlora());

        }

        if(TextUtils.isEmpty(arrayList.get(i).getFauna())) {
            recyclerViewHolder.faunaTitle.setVisibility(View.GONE);
            recyclerViewHolder.faunaTextView.setVisibility(View.GONE);

        }
        else
        {

            recyclerViewHolder.faunaTitle.setVisibility(View.VISIBLE);
            recyclerViewHolder.faunaTextView.setVisibility(View.VISIBLE);
            recyclerViewHolder.faunaTextView.setText(arrayList.get(i).getFauna());


        }

        if(TextUtils.isEmpty(arrayList.get(i).getDescription())) {
            recyclerViewHolder.descriptionTitle.setVisibility(View.GONE);
            recyclerViewHolder.descriptionTextView.setVisibility(View.GONE);

        }
        else
        {

            recyclerViewHolder.descriptionTitle.setVisibility(View.VISIBLE);
            recyclerViewHolder.descriptionTextView.setVisibility(View.VISIBLE);
            recyclerViewHolder.descriptionTextView.setText(arrayList.get(i).getDescription());


        }



        recyclerViewHolder.ratingCountTextView.setText(Integer.toString(arrayList.get(i).getRatingCount()) + " ratings");
        recyclerViewHolder.currentRatingBar.setRating(arrayList.get(i).getRating()/arrayList.get(i).getRatingCount());
        float ratingValueFloat = arrayList.get(i).getRating()/arrayList.get(i).getRatingCount();
        if(arrayList.get(i).getRatingCount()==0) {
            ratingValueFloat=0f;

        }
        recyclerViewHolder.ratingValue.setText(String.format("%.1f", ratingValueFloat));

        if(arrayList.get(i).getYourRating()!=0) {

            recyclerViewHolder.ratingComment.setVisibility(View.VISIBLE);
            recyclerViewHolder.ratingComment.setText("You already rated " + arrayList.get(i).getYourRating() + " stars previously.");
        }
        else
            recyclerViewHolder.ratingComment.setVisibility(View.GONE);

        if(arrayList.get(i).getFirebaseUserID().equals(User_ID)){

            recyclerViewHolder.deleteGalleryItem.setVisibility(View.VISIBLE);
            recyclerViewHolder.editGalleryItem.setVisibility(View.VISIBLE);
        }
        else
        {
            recyclerViewHolder.deleteGalleryItem.setVisibility(View.GONE);
            recyclerViewHolder.editGalleryItem.setVisibility(View.GONE);
        }


        Glide.with(context)
                .load(Uri.parse(arrayList.get(i).getUserphotoURL()))
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .error(R.drawable.aquarium2))
                .into(recyclerViewHolder.circularDPImageView);
    }


    private void setImage(final int position, final ImageView v, final ProgressBar loadingProgress) {

        if (!TextUtils.isEmpty(arrayList.get(position).getTankImageURL())) {


            String picUrl=arrayList.get(position).getTankImageURL();
            picUrl.replace("\\u003d","=");
            //Log.i("NewUrl",picUrl);
            AllImageDownloader task=new AllImageDownloader(v,loadingProgress,context);
            try{
                task.execute(picUrl);
            }catch (Exception e){
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            }

        }

    }

    private void deleteGalleryItems(int position){

        StorageReference storageReference=FirebaseStorage.getInstance().getReference("Gallery");
        StorageReference imageRef=storageReference.child(arrayList.get(position).getTankImageFileName());
        imageRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"Item deleted",Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Deletion Failed",Toast.LENGTH_SHORT).show();
                    }
                });

        DatabaseReference galleryItemRef =FirebaseDatabase.getInstance().getReference("GI");
        galleryItemRef.child(arrayList.get(position).getUserID()).removeValue();

    }




    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    protected class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        DatabaseReference galleryItemRef = FirebaseDatabase.getInstance().getReference("GI");

        ImageView facebookImageView;
        ImageView twitterImageView;
        ImageView instagramImageView;
        ImageView webImageView;
        ImageView circularDPImageView;
        ImageView galleyItemImageView;
        RatingBar galleryRatingBar;
        RatingBar currentRatingBar;
        TextView ratingCountTextView;
        TextView authorsNameTextView;
        TextView techTextView;
        TextView ftsTextView;
        TextView floraTextView;
        TextView faunaTextView;
        TextView descriptionTextView;
        TextView authorsNameTitle;
        TextView techTitle;
        TextView ftsTitle;
        TextView floraTitle;
        TextView faunaTitle;
        TextView descriptionTitle;
        TextView ratingValue;
        TextView ratingComment;
        ProgressBar progressBar;
        ImageButton deleteGalleryItem;
        ImageButton editGalleryItem;



        public RecyclerViewHolder(@NonNull View itemView) {

            super(itemView);

            facebookImageView = itemView.findViewById(R.id.FacebookLink);
            twitterImageView = itemView.findViewById(R.id.TwitterLink);
            instagramImageView = itemView.findViewById(R.id.InstagramLink);
            webImageView = itemView.findViewById(R.id.WebLink);
            circularDPImageView = itemView.findViewById(R.id.CircularDPImageView);
            galleyItemImageView = itemView.findViewById(R.id.ItemImage);
            ratingCountTextView = itemView.findViewById(R.id.RatingCount);
            authorsNameTextView = itemView.findViewById(R.id.AuthorsNameText);
            techTextView = itemView.findViewById(R.id.TechTextView);
            ftsTextView = itemView.findViewById(R.id.FullTankSpecsText);
            floraTextView = itemView.findViewById(R.id.FloraText);
            faunaTextView = itemView.findViewById(R.id.FaunaText);
            descriptionTextView = itemView.findViewById(R.id.DescriptionText);
            ratingValue = itemView.findViewById(R.id.RatingValue);
            authorsNameTitle = itemView.findViewById(R.id.AuthorsNameTitle);
            techTitle = itemView.findViewById(R.id.TechTitle);
            ftsTitle = itemView.findViewById(R.id.FtsTitle);
            floraTitle = itemView.findViewById(R.id.FloraTitle);
            faunaTitle = itemView.findViewById(R.id.FaunaTitle);
            descriptionTitle = itemView.findViewById(R.id.DescriptionTitle);
            progressBar = itemView.findViewById(R.id.LoadingProgress);
            deleteGalleryItem = itemView.findViewById(R.id.DeleteGalleryItem);
            galleryRatingBar = itemView.findViewById(R.id.UserRatingBar);
            currentRatingBar = itemView.findViewById(R.id.CurrentRating);
            ratingComment = itemView.findViewById(R.id.RatingComment);
            editGalleryItem = itemView.findViewById(R.id.EditGalleryItem);


            deleteGalleryItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(arrayList.get(getAdapterPosition()).getFirebaseUserID().equals(User_ID)) {
                            deleteGalleryItems(getAdapterPosition());
                            arrayList.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                        }
                    }
            });

            editGalleryItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                   /* if(arrayList.get(getAdapterPosition()).getFirebaseUserID().equals(User_ID)) {
                        Intent intent = new Intent(context,GalleryActivity.class);
                        intent.putExtra("mode","edit");
                        intent.putExtra("UserID",arrayList.get(getAdapterPosition()).getUserID());
                        context.startActivity(intent);


                    }*/

                    onItemClickListener.onClick(v,getAdapterPosition());

                }
            });


            final Intent intent = new Intent(context, ConditionsActivity.class);
            facebookImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra("URL",arrayList.get(getAdapterPosition()).getFacebookURL());
                    context.startActivity(intent);
                }
            });

            twitterImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra("URL",arrayList.get(getAdapterPosition()).getTwitterURL());
                    context.startActivity(intent);
                }
            });

            instagramImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra("URL",arrayList.get(getAdapterPosition()).getInstagramURL());
                    context.startActivity(intent);
                }
            });

            webImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra("URL",arrayList.get(getAdapterPosition()).getWebsiteURL());
                    context.startActivity(intent);
                }
            });

            galleryRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                    float userRatingAbsolute = arrayList.get(getAdapterPosition()).getRating();
                    userRatingAbsolute += galleryRatingBar.getRating();
                    arrayList.get(getAdapterPosition()).setRating(userRatingAbsolute);
                    galleryItemRef.child(arrayList.get(getAdapterPosition()).getUserID()).child("rating").setValue(userRatingAbsolute);

                    int userRatingCount = arrayList.get(getAdapterPosition()).getRatingCount();
                    userRatingCount++;
                    arrayList.get(getAdapterPosition()).setRatingCount(userRatingCount);
                    galleryItemRef.child(arrayList.get(getAdapterPosition()).getUserID()).child("ratingCount").setValue(userRatingCount);


                    ratingCountTextView.setText(Integer.toString(userRatingCount) + " ratings");

                    currentRatingBar.setRating(userRatingAbsolute/userRatingCount);

                    ratingValue.setText(String.format("%.1f",userRatingAbsolute/userRatingCount));

                    arrayList.get(getAdapterPosition()).setYourRating(galleryRatingBar.getRating());
                    galleryItemRef.child(arrayList.get(getAdapterPosition()).getUserID()).child("yourRating").setValue(galleryRatingBar.getRating());


                }
            });

        }

        @Override
        public void onClick(View v) {


        }
    }
}
