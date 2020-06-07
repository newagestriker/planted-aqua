package com.newage.plantedaqua.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.newage.plantedaqua.activities.ConditionsActivity;
import com.newage.plantedaqua.activities.SingleChatActivity;
import com.newage.plantedaqua.helpers.CloudNotificationHelper;
import com.newage.plantedaqua.models.GalleryInfo;
import com.newage.plantedaqua.R;
import com.newage.plantedaqua.room.ChatUsers;
import com.newage.plantedaqua.room.SingleChatDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class UsersGalleryRecyclerAdapter extends RecyclerView.Adapter<UsersGalleryRecyclerAdapter.RecyclerViewHolder> {

    private ArrayList<GalleryInfo> arrayList;
    private OnItemClickListener onItemClickListener;
    private Context context;
    private String User_ID;
    private FirebaseAuth mAuth;
    private String PU;

    public UsersGalleryRecyclerAdapter(Context context, ArrayList<GalleryInfo> arrayList, String User_ID, OnItemClickListener onItemClickListener) {

        this.arrayList = arrayList;
        this.User_ID = User_ID;
        this.onItemClickListener = onItemClickListener;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser().getPhotoUrl()!=null) {
            PU = TextUtils.isEmpty(mAuth.getCurrentUser().getPhotoUrl().toString()) ? "" : mAuth.getCurrentUser().getPhotoUrl().toString();
        }
        else {
            PU = "";
        }
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

        setImage(i,recyclerViewHolder.galleyItemImageView);

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
        recyclerViewHolder.ratingValue.setText(String.format(Locale.getDefault(),"%.1f", ratingValueFloat));



        if(arrayList.get(i).getFirebaseUserID().equals(User_ID)){

            recyclerViewHolder.deleteGalleryItem.setVisibility(View.VISIBLE);
            recyclerViewHolder.editGalleryItem.setVisibility(View.VISIBLE);
            recyclerViewHolder.connectGalleryUser.setVisibility(View.GONE);
        }
        else
        {
            recyclerViewHolder.deleteGalleryItem.setVisibility(View.GONE);
            recyclerViewHolder.editGalleryItem.setVisibility(View.GONE);
            recyclerViewHolder.connectGalleryUser.setVisibility(View.VISIBLE);
        }


        Glide.with(context)
                .load(Uri.parse(arrayList.get(i).getUserphotoURL()))
                .into(recyclerViewHolder.circularDPImageView);
    }


    private void setImage(final int position, final ImageView v) {

        if (!TextUtils.isEmpty(arrayList.get(position).getTankImageURL())) {

            v.setImageResource(R.drawable.loading);
            String picUrl=arrayList.get(position).getTankImageURL();
            picUrl.replace("\\u003d","=");
            //Log.i("NewUrl",picUrl);
            Glide.with(context)
                    .load(picUrl)
                    .into(v);


            /*AllImageDownloader task=new AllImageDownloader(v,loadingProgress,context);
            try{
                task.execute(picUrl);
            }catch (Exception e){
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            }
*/
        }
        else{
            v.setImageResource(R.drawable.noimage);
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
        Button connectGalleryUser;



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
            connectGalleryUser = itemView.findViewById(R.id.connect_gallery_user);

            connectGalleryUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connectWithAuthor(getLayoutPosition());
                }
            });


            deleteGalleryItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(arrayList.get(getLayoutPosition()).getFirebaseUserID().equals(User_ID)) {
                            deleteGalleryItems(getLayoutPosition());
                            arrayList.remove(getLayoutPosition());
                            notifyItemRemoved(getLayoutPosition());
                        }
                    }
            });

            editGalleryItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                   /* if(arrayList.get(getLayoutPosition()).getFirebaseUserID().equals(User_ID)) {
                        Intent intent = new Intent(context,GalleryActivity.class);
                        intent.putExtra("mode","edit");
                        intent.putExtra("UserID",arrayList.get(getLayoutPosition()).getUserID());
                        context.startActivity(intent);


                    }*/

                    onItemClickListener.onClick(v,getLayoutPosition());

                }
            });


            final Intent intent = new Intent(context, ConditionsActivity.class);
            facebookImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra("URL",arrayList.get(getLayoutPosition()).getFacebookURL());
                    context.startActivity(intent);
                }
            });

            twitterImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra("URL",arrayList.get(getLayoutPosition()).getTwitterURL());
                    context.startActivity(intent);
                }
            });

            instagramImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra("URL",arrayList.get(getLayoutPosition()).getInstagramURL());
                    context.startActivity(intent);
                }
            });

            webImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra("URL",arrayList.get(getLayoutPosition()).getWebsiteURL());
                    context.startActivity(intent);
                }
            });

            galleryRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                    float userRatingAbsolute = arrayList.get(getLayoutPosition()).getRating();
                    userRatingAbsolute += galleryRatingBar.getRating();
                    arrayList.get(getLayoutPosition()).setRating(userRatingAbsolute);
                    galleryItemRef.child(arrayList.get(getLayoutPosition()).getUserID()).child("rating").setValue(userRatingAbsolute);

                    int userRatingCount = arrayList.get(getLayoutPosition()).getRatingCount();
                    userRatingCount++;
                    arrayList.get(getLayoutPosition()).setRatingCount(userRatingCount);
                    galleryItemRef.child(arrayList.get(getLayoutPosition()).getUserID()).child("ratingCount").setValue(userRatingCount);


                    ratingCountTextView.setText(Integer.toString(userRatingCount) + " ratings");

                    currentRatingBar.setRating(userRatingAbsolute/userRatingCount);

                    ratingValue.setText(String.format(Locale.getDefault(),"%.1f",userRatingAbsolute/userRatingCount));

                    arrayList.get(getLayoutPosition()).setYourRating(galleryRatingBar.getRating());
                    galleryItemRef.child(arrayList.get(getLayoutPosition()).getUserID()).child("yourRating").setValue(galleryRatingBar.getRating());

                    msgTheAuthor(galleryRatingBar.getRating(),arrayList.get(getLayoutPosition()).getFirebaseUserID());
                }
            });

        }

        @Override
        public void onClick(View v) {


        }
    }

    private void msgTheAuthor(float rating, String userID){

        CloudNotificationHelper cloudNotificationHelper = new CloudNotificationHelper(context);
        cloudNotificationHelper.sendCloudNotification("You received "+rating+" stars for showcasing your tank",PU, CloudNotificationHelper.MsgType.oneToOne,User_ID,userID,"Your Tank was rated","","Gallery");
    }

    private void connectWithAuthor(int pos){

        storeReceiverUserDetails(pos);

        Intent intent = new Intent(context, SingleChatActivity.class);
        intent.putExtra("from_user", arrayList.get(pos).getFirebaseUserID());
        context.startActivity(intent);

    }

    private void storeReceiverUserDetails(int pos){

        String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        ChatUsers chatUsers = new ChatUsers();
        chatUsers.setChatUserID(arrayList.get(pos).getFirebaseUserID());
        chatUsers.setChatUserName(arrayList.get(pos).getAuthorsName());
        chatUsers.setChatUserPhotoURL(arrayList.get(pos).getUserphotoURL());
        chatUsers.setChatUserLastMessageTime(timeStamp);

        SingleChatDB singleChatDB = SingleChatDB.Companion.getInstance(context);



        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                singleChatDB.getSingleChatDao().insertChatUser(chatUsers);
            }
        };

        AsyncTask.execute(runnable);

    }
}
