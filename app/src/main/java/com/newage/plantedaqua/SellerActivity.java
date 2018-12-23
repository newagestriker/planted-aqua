package com.newage.plantedaqua;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;


import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class SellerActivity extends AppCompatActivity {

    private final int REQUEST_CAMERA = 56 ;
    private final int SELECT_FILE = 89 ;
    private ArrayList<SellerItemsDescription> sellerItemsDescriptionArrayList;
    private String mode;
    FirebaseAuth mAuth;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        User_ID=getIntent().getStringExtra("User");
        if(User_ID.equals("Own")) {
            mode="Own";
            SellerSpeciality.setHint(getResources().getString(R.string.Specialization));
            mAuth = FirebaseAuth.getInstance();
            User_ID = mAuth.getCurrentUser().getUid();
            SellerName.setText(getResources().getString(R.string.Owner) + " : " + (mAuth.getCurrentUser().getDisplayName()));
            Glide.with(this)
                    .load(mAuth.getCurrentUser().getPhotoUrl())
                    .into(sellerPhoto);
            getMenuInflater().inflate(R.menu.main, menu);

        }
        else {
            mode = "Other";
            SellerName.setText(getResources().getString(R.string.Owner) + " : " + (getIntent().getStringExtra("DN")));
            SellerSpeciality.setFocusable(false);
            SellerSpeciality.setClickable(false);
            Glide.with(this)
                    .load(Uri.parse(getIntent().getStringExtra("PU")))
                    .into(sellerPhoto);
        }
        SellerRef= FirebaseDatabase.getInstance().getReference("UI").child(User_ID).child("SP");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyPermissions();
        }else{
            getFromDatabase();
        }

        return super.onCreateOptionsMenu(menu);
    }

    ImageView itemImage;
    EditText sellerItemName,sellerItemDes,sellerItemQuan,sellerItemPrice,sellerItemAvail;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.seller_items_layout,null);
        itemImage=view.findViewById(R.id.ItemImage);
        sellerItemName=view.findViewById(R.id.SellerItemName);
        sellerItemDes=view.findViewById(R.id.SellerItemDes);
        sellerItemQuan=view.findViewById(R.id.SellerItemQuan);
        sellerItemPrice=view.findViewById(R.id.SellerItemPrice);
        sellerItemAvail=view.findViewById(R.id.SellerItemAvail);

        itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    selectImage();
            }

        });

        builder.setView(view)
                .setTitle(getResources().getString(R.string.AddItems))
                .setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                            getAllData();

                    }
                })
                .setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setCancelable(false).create();
        builder.show();

        return super.onOptionsItemSelected(item);
    }
    SellerItemsDescription sellerItemsDescription=new SellerItemsDescription();
    DatabaseReference SellerRef;
    private void getAllData(){

        sellerItemsDescription=new SellerItemsDescription();

        sellerItemsDescription.setItemName(sellerItemName.getText().toString());
        sellerItemsDescription.setItemQuantity(sellerItemQuan.getText().toString());
        sellerItemsDescription.setItemAvailability(sellerItemAvail.getText().toString());
        sellerItemsDescription.setItemDescription(sellerItemDes.getText().toString());
        sellerItemsDescription.setItemPrice(sellerItemPrice.getText().toString());


        SellerRef.setValue(SellerSpeciality.getText().toString());


        //UPLOAD TO FIREBASE


        if(image!=null) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.getDefault()).format(new Date());
            int rnd = new Random().nextInt(10000);
            tempImageID = timeStamp + "_" + rnd+".jpg";
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("Seller");
            final StorageReference itemImages = storageReference.child(User_ID + "/" + tempImageID);
            progressDialog.show();
            /*itemImages.putFile(tankpicUri)
                    .addOnSuccessListener(new OnSuccessListener <UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            itemImages.getDownloadUrl().addOnSuccessListener(new OnSuccessListener <Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String u= uri.toString();
                                    //Log.i("Image uri",u);
                                    //Log.i("Image uri","Success");
                                    sellerItemsDescription.setItemImage(u);
                                    sellerItemsDescription.setItemImagePath(tempImageID);
                                    image.delete();
                                    uploadSellerJSONFile();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    })


                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(SellerActivity.this, "IMAGE UPLOAD ERROR!!", Toast.LENGTH_SHORT).show();
                            image.delete();
                            progressDialog.dismiss();
                        }
                    });*/

            itemImage.setDrawingCacheEnabled(true);
            itemImage.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) itemImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = itemImages.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(SellerActivity.this, "IMAGE UPLOAD ERROR!!", Toast.LENGTH_SHORT).show();
                    image.delete();
                    progressDialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    itemImages.getDownloadUrl().addOnSuccessListener(new OnSuccessListener <Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String u= uri.toString();
                            //Log.i("Image uri",u);
                            //Log.i("Image uri","Success");
                            sellerItemsDescription.setItemImage(u);
                            sellerItemsDescription.setItemImagePath(tempImageID);
                            image.delete();
                            uploadSellerJSONFile();
                            progressDialog.dismiss();
                        }
                    });
                }
            });



        }
        else {
            sellerItemsDescription.setItemImage("");
            sellerItemsDescription.setItemImagePath(tempImageID);
            uploadSellerJSONFile();
        }


    }

    private String User_ID="";
    private ProgressDialog progressDialog;
    private TextView SellerName;
    private ImageView sellerPhoto;
    private EditText SellerSpeciality;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setTitle(getResources().getString(R.string.Shop));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);
        progressDialog=new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getResources().getString(R.string.Uploading));
        SellerName=findViewById(R.id.SellerName);
        sellerPhoto=findViewById(R.id.SellerImage);
        SellerSpeciality=findViewById(R.id.SellerSpeciality);
        SellerSpeciality.setText(getResources().getString(R.string.NoInfo));
        SellerRef= FirebaseDatabase.getInstance().getReference("UI").child(User_ID).child("SP");

    }

    String JSONtext;

    private void getFromDatabase(){

        if(SellerRef!=null) {

            //Log.i("REALTIME","not null");

            SellerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ////Log.i("Snapshot",dataSnapshot.getValue(String.class));
                        SellerSpeciality.setText(dataSnapshot.getValue(String.class));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SellerActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }//else
            //Log.i("REALTIME","null");


        sellerItemsDescriptionArrayList=new ArrayList <>();
        final RecyclerView recyclerView=findViewById(R.id.SellerItemRecyclerView);
        adapter=new RecyclerAdapterSellerItems(this,sellerItemsDescriptionArrayList,User_ID,mode);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        final File tempJSON = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"temp.JSON");

        try {
            if(tempJSON.exists())
                tempJSON.delete();
            tempJSON.createNewFile();

            StorageReference StorageRef = FirebaseStorage.getInstance().getReference("Seller");
            StorageReference JSONStorageRef = StorageRef.child(User_ID + "/JSON1.JSON");

            JSONStorageRef.getFile(tempJSON)
                    .addOnSuccessListener(new OnSuccessListener <FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            try {
                                if(tempJSON.length()!=0) {
                                    Scanner scanner = new Scanner(tempJSON);
                                    JSONtext = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                    scanner.close();
                                    //Log.i("JSON TEXT", JSONtext);
                                    JSONArray jsonArray = new JSONArray(JSONtext);
                                    Gson gson = new Gson();
                                    JSONObject jsonObject;
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        jsonObject = jsonArray.getJSONObject(i);
                                        sellerItemsDescriptionArrayList.add(gson.fromJson(jsonObject.toString(), SellerItemsDescription.class));
                                        //Log.i("InsertPosition",Integer.toString(i));
                                    }
                                    adapter.notifyDataSetChanged();

                                    //populateList();

                                }
                            }
                            catch (Exception e) {
                                Toast.makeText(SellerActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                //Log.i("DOWNLOAD ERROR JSON",e.getMessage());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SellerActivity.this,"NO ITEMS LISTED",Toast.LENGTH_LONG).show();
                            //Log.i("JSON DOWNLOAD FAIL",e.getMessage());
                        }
                    });

        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    RecyclerView.Adapter adapter;

    String tempID="";
    Uri tankpicUri;
    File image=null;

    public void selectImage(){
        boolean filedeleted;
        final CharSequence items[]={"Camera", "Gallery", "Cancel"};
        File imagesFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "JPT_images");
        if(!(imagesFolder.exists())){
            boolean dirCreated=imagesFolder.mkdirs();
        }

        if(imagesFolder.isDirectory()){



            String timeStamp = new SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.getDefault()).format(new Date());
            int rnd = new Random().nextInt(10000);
            tempID = timeStamp + "_" + rnd;
            image = new File(imagesFolder, tempID+"_Pic.jpg");
            if (Build.VERSION.SDK_INT >= 24) {
                tankpicUri = FileProvider.getUriForFile(SellerActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        image);
            }else{
                tankpicUri=Uri.fromFile(image);
            }

            if (image.exists()) filedeleted=image.delete();


        }

        AlertDialog.Builder builder =new AlertDialog.Builder(SellerActivity.this);
        builder.setTitle("Get Image from");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Camera")) {

                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    i.putExtra(MediaStore.EXTRA_OUTPUT, tankpicUri);

                    startActivityForResult(i, REQUEST_CAMERA);
                }else if (items[which].equals("Gallery")) {

                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    i.setType("image/*");
                    startActivityForResult(i.createChooser(i, "Select Image Using"), SELECT_FILE);
                }

                else{
                    dialog.dismiss();

                }
            }
        });
        builder.show();

    }
    String tempImageID="";
    boolean newImageCreated=false;
    Uri tankPicUriFromGallery;
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean fileCreated;

        if (resultCode == Activity.RESULT_OK) {
            newImageCreated=true;
            if (requestCode == REQUEST_CAMERA) {
                Glide.with(this)
                        .load(tankpicUri)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .error(R.drawable.aquarium2))
                        .into(itemImage);
            }

            if (requestCode == SELECT_FILE) {

                tankPicUriFromGallery=data.getData();
                try {
                    fileCreated = image.createNewFile();
                    if (fileCreated) {


                        try {
                            image.createNewFile();
                            this.copyFile(new File(getRealPathFromURI(data.getData())), image);
                        } catch (IOException e) {
                            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {

                    Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
                Glide.with(this)
                        .load(tankPicUriFromGallery)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .error(R.drawable.aquarium2))
                        .into(itemImage);

            }





            Intent scanFileIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,tankpicUri);
            sendBroadcast(scanFileIntent);


        }





               /* Uri imageuri=data.getData();
                itemImage.setImageURI(imageuri);



                Intent i=new Intent(Intent.ACTION_VIEW);

                i.setDataAndType(selectImageUri, "image/*");
                startActivity(i);*/


    }
    private void uploadSellerJSONFile(){

        sellerItemsDescriptionArrayList.add(sellerItemsDescription);
        adapter.notifyItemInserted((sellerItemsDescriptionArrayList.size()-1));
        Gson gson=new Gson();
        String SellerItemsJSON;
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("[");
        String comma="";
        for(SellerItemsDescription SID:sellerItemsDescriptionArrayList)
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
                    uri=FileProvider.getUriForFile(this,BuildConfig.APPLICATION_ID+".provider",tempJSON);
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
                               // adapter.notifyItemInserted((sellerItemsDescriptionArrayList.size()-1));
                                //adapter.notifyDataSetChanged();
                                //Log.i("InsertPosition",Integer.toString(sellerItemsDescriptionArrayList.size()-1));
                            }
                                                    })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(SellerActivity.this,"UPLOAD ERROR!!",Toast.LENGTH_SHORT).show();
                                //Log.i("JSON upload error",e.getMessage());
                                tempJSON.delete();
                                progressDialog.dismiss();
                                deleteUploadedItems(tempImageID);
                                sellerItemsDescriptionArrayList.remove(sellerItemsDescriptionArrayList.size()-1);
                                adapter.notifyItemRemoved(sellerItemsDescriptionArrayList.size()-1);

                            }
                        });

        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
            //Log.i("TEMP FILE ERROR",e.getMessage());

        }

    }

    private void deleteUploadedItems(String fileID){
        if(image!=null){
            StorageReference storageReference=FirebaseStorage.getInstance().getReference("Seller");
            StorageReference imageRef=storageReference.child(User_ID+"/"+fileID);
            imageRef.delete()
                    .addOnSuccessListener(new OnSuccessListener <Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(SellerActivity.this,"Item deleted",Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SellerActivity.this,"Deletion Failed",Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }



    public void verifyPermissions(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[2]) == PackageManager.PERMISSION_GRANTED) {
                getFromDatabase();

            } else {

                ActivityCompat.requestPermissions(SellerActivity.this, permissions, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2]==PackageManager.PERMISSION_GRANTED) {
            getFromDatabase();
        }else {
            Toast.makeText(this,getResources().getString(R.string.PermRationale),Toast.LENGTH_LONG).show();
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source;
        FileChannel destination;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }


    }

    private String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SellerRef.setValue(SellerSpeciality.getText().toString());
        if(newImageCreated){
            image.delete();
        }
    }
}
