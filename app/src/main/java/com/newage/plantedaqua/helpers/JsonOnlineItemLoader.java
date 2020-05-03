package com.newage.plantedaqua.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.newage.plantedaqua.BuildConfig;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

public class JsonOnlineItemLoader<ListClass> {

    private  ArrayList<ListClass> arrayList;
    private String itemsJSON;
    private Context context;
    private boolean outcome;
    private String url;
    private String JSONtext;
    private Class<ListClass> listClassClass;


    JsonOnlineItemLoader(ArrayList<ListClass> arrayList, Context context){

        this.arrayList = arrayList;
        this.context = context;
        JSONtext = "";


    }

    public void createJSONFile(){

        Gson gson=new Gson();
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("[");
        String comma="";
        for(ListClass SID:arrayList)
        {
            stringBuilder.append(comma);
            stringBuilder.append(gson.toJson(SID));
            comma=",";
        }
        stringBuilder.append("]");
        itemsJSON=stringBuilder.toString();

    }



    public void uploadJSONFile(StorageReference JSONStorageRef, final ProgressBar progressBar){



        final File tempJSON;
        try {
            tempJSON = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"temp.JSON");
            tempJSON.createNewFile();
            Writer output=new BufferedWriter(new FileWriter(tempJSON));
            output.write(itemsJSON);
            output.close();
            Uri uri;
            if(Build.VERSION.SDK_INT>=24){
                uri=FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".provider",tempJSON);
            }
            else
                uri=Uri.fromFile(tempJSON);
            progressBar.setVisibility(View.VISIBLE);
            JSONStorageRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            tempJSON.delete();
                            progressBar.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(context,"UPLOAD ERROR!!",Toast.LENGTH_SHORT).show();
                            //Log.i("JSON upload error",e.getMessage());
                            tempJSON.delete();
                            progressBar.setVisibility(View.GONE);

                        }
                    });

        }catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();


        }

    }



    public boolean deleteUploadedImageFromFirebase(String fileID,StorageReference imageRef){


            imageRef.delete()
                    .addOnSuccessListener(new OnSuccessListener <Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context,"Item deleted",Toast.LENGTH_SHORT).show();
                            outcome = true;

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,"Deletion Failed",Toast.LENGTH_SHORT).show();
                            outcome = false;

                        }
                    });

        return outcome;
    }

    public String uploadImageToFirebase(final ProgressBar progressBar, String tempImageID, ImageView itemImage, final StorageReference itemImageRef){

        progressBar.setVisibility(View.VISIBLE);
        itemImage.setDrawingCacheEnabled(true);
        itemImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) itemImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = itemImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context, "IMAGE UPLOAD ERROR!!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                url = "FAILED";

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                itemImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener <Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        url= uri.toString();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
        return url;

    }
    public void loadFromDatabase(StorageReference JSONStorageRef){


        arrayList=new ArrayList <>();

        final File tempJSON = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"temp.JSON");

        try {
            if(tempJSON.exists())
                tempJSON.delete();
            tempJSON.createNewFile();

            JSONStorageRef.getFile(tempJSON)
                    .addOnSuccessListener(new OnSuccessListener <FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            try {
                                if(tempJSON.length()!=0) {
                                    Scanner scanner = new Scanner(tempJSON);
                                    JSONtext = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                    scanner.close();
                                    JSONArray jsonArray = new JSONArray(JSONtext);
                                    Gson gson = new Gson();
                                    JSONObject jsonObject;
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        jsonObject = jsonArray.getJSONObject(i);
                                        arrayList.add(gson.fromJson(jsonObject.toString(),listClassClass));

                                    }
                                }
                            }
                            catch (Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                //Log.i("DOWNLOAD ERROR JSON",e.getMessage());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,"NO ITEMS LISTED",Toast.LENGTH_LONG).show();
                            //Log.i("JSON DOWNLOAD FAIL",e.getMessage());
                        }
                    });

        }catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }



}
