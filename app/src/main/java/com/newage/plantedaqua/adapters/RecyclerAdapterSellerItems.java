package com.newage.plantedaqua.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.NonNull;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.newage.plantedaqua.BuildConfig;
import com.newage.plantedaqua.R;
import com.newage.plantedaqua.models.SellerItemsDescription;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RecyclerAdapterSellerItems extends RecyclerView.Adapter<RecyclerAdapterSellerItems.RecyclerViewHolder> {

    private Context context;
    private ArrayList<SellerItemsDescription> arrayList;
    private String User_ID;
    String mode;


    public RecyclerAdapterSellerItems(Context context, ArrayList<SellerItemsDescription> arrayList, String UserID,String mode) {

        this.arrayList=arrayList;
        this.context=context;
        this.User_ID=UserID;
        this.mode=mode;

    }


    @NonNull
    @Override
    public RecyclerAdapterSellerItems.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.seller_items_layout,viewGroup,false);
       return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterSellerItems.RecyclerViewHolder recyclerViewHolder, int i) {

        recyclerViewHolder.sellerItemAvail.setText(arrayList.get(i).getItemAvailability());
        recyclerViewHolder.sellerItemDes.setText(arrayList.get(i).getItemDescription());
        recyclerViewHolder.sellerItemName.setText(arrayList.get(i).getItemName());
        recyclerViewHolder.sellerItemPrice.setText(arrayList.get(i).getItemPrice());
        recyclerViewHolder.sellerItemQuan.setText(arrayList.get(i).getItemQuantity());

        setImage(i,recyclerViewHolder.itemImage,recyclerViewHolder.loadingProgress);



    }

    private void setImage(final int position, final ImageView v, final ProgressBar loadingProgress) {

        if (!arrayList.get(position).getItemImage().isEmpty()) {


                String picUrl=arrayList.get(position).getItemImage();
                picUrl.replace("\\u003d","=");
               // Log.i("NewUrl",picUrl);
                ImageDownloader task=new ImageDownloader(v,loadingProgress);
                try{
                    task.execute(picUrl);
                }catch (Exception e){
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }

        }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        String err="";
        RequestManager requestManager;
        private ImageView imageView;
        private ProgressBar progressBar;
        public ImageDownloader(ImageView imageView,ProgressBar progressBar) {
            this.imageView=imageView;
            this.progressBar=progressBar;
            requestManager=Glide.with(context);
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... urls) {

            URL url;
            HttpURLConnection urlConnection;

            try {

                url = new URL(urls[0]);
               // Log.i("ParsedUrl",url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                Bitmap bitmap=BitmapFactory.decodeStream(in);
                return bitmap;


            } catch (Exception e) {
                err=e.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if(bitmap!=null) {
                if (err.isEmpty()) {
                    requestManager
                            .load(bitmap)
                            .apply(new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .placeholder(R.drawable.noimage)
                                    .error(R.drawable.noimage))
                            .into(imageView);
                } else {
                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show();
                    //Log.i("Download error", err);
                }
            }//else
                //Log.i("BitmapError","null");
            progressBar.setVisibility(View.GONE);
            super.onPostExecute(bitmap);
        }
    }





    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{


        ImageView itemImage,takePic;
        EditText sellerItemName,sellerItemDes,sellerItemQuan,sellerItemPrice,sellerItemAvail;
        ProgressBar loadingProgress;
        ImageButton deleteItem;

        public RecyclerViewHolder(View view) {
            super(view);
            itemImage=view.findViewById(R.id.ItemImage);
            sellerItemName=view.findViewById(R.id.SellerItemName);
            sellerItemName.setClickable(false);
            sellerItemName.setFocusable(false);

            sellerItemDes=view.findViewById(R.id.SellerItemDes);
            sellerItemDes.setClickable(false);
            sellerItemDes.setFocusable(false);

            sellerItemQuan=view.findViewById(R.id.SellerItemQuan);
            sellerItemQuan.setClickable(false);
            sellerItemQuan.setFocusable(false);

            sellerItemPrice=view.findViewById(R.id.SellerItemPrice);
            sellerItemPrice.setClickable(false);
            sellerItemPrice.setFocusable(false);

            sellerItemAvail=view.findViewById(R.id.SellerItemAvail);
            sellerItemAvail.setClickable(false);
            sellerItemAvail.setFocusable(false);

            takePic=view.findViewById(R.id.TakePic);
            takePic.setVisibility(View.GONE);

            loadingProgress=view.findViewById(R.id.LoadingProgress);
            loadingProgress.setVisibility(View.GONE);

            deleteItem=view.findViewById(R.id.DeleteItem);
            if(mode.equals("Own")) {
                deleteItem.setVisibility(View.VISIBLE);
                deleteItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.i("Delete", "Delete");
                        deleteTheItem(getLayoutPosition());
                    }
                });
            }else
                deleteItem.setVisibility(View.GONE);

            //Log.i("AdaptorPosition", Integer.toString(getLayoutPosition()));

        }


    }
    private void deleteTheItem(final int pos){

        StorageReference storageReference=FirebaseStorage.getInstance().getReference("Seller");
        if(!arrayList.get(pos).getItemImagePath().isEmpty()){
        StorageReference imageRef=storageReference.child(User_ID+"/"+arrayList.get(pos).getItemImagePath());
        imageRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"Item deleted",Toast.LENGTH_SHORT).show();
                        deleteJSONobject(pos);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Deletion Failed",Toast.LENGTH_SHORT).show();
                        deleteJSONobject(pos);
                    }
                });

    }else{
            deleteJSONobject(pos);
        }

    }
    private void deleteJSONobject(final int pos){

        final ProgressDialog progressDialog=new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(context.getResources().getString(R.string.Deleting));
        arrayList.remove(pos);
        Gson gson=new Gson();
        String SellerItemsJSON;
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("[");
        String comma="";
        for(SellerItemsDescription SID:arrayList)
        {
            stringBuilder.append(comma);
            stringBuilder.append(gson.toJson(SID));
            comma=",";
        }
        stringBuilder.append("]");
        SellerItemsJSON=stringBuilder.toString();


        final File tempJSON;
        try {
            tempJSON = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"temp.JSON");
            tempJSON.createNewFile();
            Writer output=new BufferedWriter(new FileWriter(tempJSON));
            output.write(SellerItemsJSON);
            output.close();
            StorageReference StorageRef=FirebaseStorage.getInstance().getReference("Seller");
            StorageReference JSONStorageRef=StorageRef.child(User_ID+"/JSON1.JSON");
            Uri uri;
            if(Build.VERSION.SDK_INT>=24){
                uri=FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".provider",tempJSON);
            }
            else
                uri=Uri.fromFile(tempJSON);
            //Log.i("URI",uri.toString());
            progressDialog.show();
            JSONStorageRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener <UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            tempJSON.delete();
                            progressDialog.dismiss();
                            notifyItemRemoved(pos);
                            if(arrayList.isEmpty()){
                                FirebaseDatabase.getInstance().getReference("UI").child(User_ID).child("IC").setValue("0");
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(context,"UPLOAD ERROR!!",Toast.LENGTH_SHORT).show();
                            //Log.i("JSON upload error",e.getMessage());
                            tempJSON.delete();
                            progressDialog.dismiss();

                        }
                    });

        }catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            //Log.i("TEMP FILE ERROR",e.getMessage());

        }

    }
}
