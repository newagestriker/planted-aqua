package com.newage.plantedaqua.activities;

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
import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.newage.plantedaqua.BuildConfig;
import com.newage.plantedaqua.helpers.CloudNotificationHelper;
import com.newage.plantedaqua.models.GalleryInfo;
import com.newage.plantedaqua.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;

import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class GalleryActivity extends AppCompatActivity {

    private final int REQUEST_CAMERA = 34 ;
    private final int SELECT_FILE = 90 ;
    private String User_ID="";
    private ProgressDialog progressDialog;
    private EditText facebookURLInput;
    private EditText twitterURLInput;
    private EditText instagramURLInput;
    private EditText webURLInput;
    private EditText authorsNameInput;
    private EditText techInput;
    private EditText ftsInput;
    private EditText floraInput;
    private EditText faunaInput;
    private EditText descriptionInput;
    private String tempImageID="";
    private boolean newImageCreated=false;
    private GalleryInfo galleryInfo;
    private ImageView tankImage;
    private ImageView galleryDisplayPic;
    private String uUID;
    private String mode;
    private String tempID="";
    private Uri tankpicUri;
    private File image=null;
    private boolean NEW_IMAGE_CREATED = false;


    private DatabaseReference galleryItemRef;
    private FirebaseAuth mAuth;
    private String userPhotoURL;
    private String userDisplayName;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        

        getMenuInflater().inflate(R.menu.edit_tank_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

   
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        
        getAllData();
        return super.onOptionsItemSelected(item);
    }

    private void getAllData(){

        if(mode.equals("create"))
            uUID = UUID.randomUUID().toString();
        else uUID = User_ID;

        userDisplayName = mAuth.getCurrentUser().getDisplayName()==null?"Anonymous User":mAuth.getCurrentUser().getDisplayName();
       userPhotoURL= mAuth.getCurrentUser().getPhotoUrl()==null?"":mAuth.getCurrentUser().getPhotoUrl().toString();
        galleryInfo.setFacebookURL(facebookURLInput.getText().toString());
        galleryInfo.setTwitterURL(twitterURLInput.getText().toString());
        galleryInfo.setInstagramURL(instagramURLInput.getText().toString());
        galleryInfo.setWebsiteURL(webURLInput.getText().toString());
        galleryInfo.setAuthorsName(authorsNameInput.getText().toString());
        galleryInfo.setTech(techInput.getText().toString());
        galleryInfo.setFts(ftsInput.getText().toString());
        galleryInfo.setFlora(floraInput.getText().toString());
        galleryInfo.setFauna(faunaInput.getText().toString());
        galleryInfo.setDescription(descriptionInput.getText().toString());
        galleryInfo.setUserID(uUID);
        galleryInfo.setEmail(mAuth.getCurrentUser().getEmail());
        galleryInfo.setFirebaseUserID(mAuth.getCurrentUser().getUid());

            galleryInfo.setUserphotoURL(userPhotoURL);
        
       
        //UPLOAD TO FIREBASE

        if(NEW_IMAGE_CREATED) {

            if(mode.equals("create")) {

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.getDefault()).format(new Date());
                int rnd = new Random().nextInt(10000);
                tempImageID = timeStamp + "_" + rnd + ".jpg";
            }

            StorageReference storageReference = FirebaseStorage.getInstance().getReference("Gallery");
            final StorageReference itemImages = storageReference.child(tempImageID);
            progressDialog.show();


            tankImage.setDrawingCacheEnabled(true);
            tankImage.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) tankImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = itemImages.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(GalleryActivity.this, "IMAGE UPLOAD ERROR!!", Toast.LENGTH_SHORT).show();
                    image.delete();
                    progressDialog.dismiss();
                }
            })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    itemImages.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String u = uri.toString();
                            //Log.i("Image uri",u);
                            //Log.i("Image uri","Success");
                            galleryInfo.setTankImageURL(u);
                            galleryInfo.setTankImageFileName(tempImageID);
                            if (image != null)
                                image.delete();

                            addDataToRealtimeDatabase();

                            progressDialog.dismiss();
                            notifyAllUsers();
                            Intent intent = new Intent();
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                    });
                }
            });
        }

        else if(mode.equals("edit")){

            addDataToRealtimeDatabase();
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();

        }

        else
            Toast.makeText(this, "No Tank image found",Toast.LENGTH_SHORT).show();



    }

    private void addDataToRealtimeDatabase() {


        galleryItemRef.child(uUID).setValue(galleryInfo);

    }


    private void notifyAllUsers(){
        CloudNotificationHelper cloudNotificationHelper =  new CloudNotificationHelper(this);
        cloudNotificationHelper.sendCloudNotification(userDisplayName + " has added a tank in the users gallery. Check it out!!",userPhotoURL, CloudNotificationHelper.MsgType.allUsers,
                mAuth.getCurrentUser().getUid(),"",
                "New Tank Added!!",userDisplayName,"Gallery");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("My Gallery Item");
        setContentView(R.layout.activity_gallery);

        User_ID = getIntent().getStringExtra("UserID");
        if(User_ID!=null) {

            mode = getIntent().getStringExtra("mode");
            mAuth = FirebaseAuth.getInstance();

            galleryInfo = new GalleryInfo();

            facebookURLInput = findViewById(R.id.FacebookURL);
            twitterURLInput = findViewById(R.id.TwitterURL);
            instagramURLInput = findViewById(R.id.InstagramURL);
            authorsNameInput = findViewById(R.id.AuthorsName);
            webURLInput = findViewById(R.id.WebURL);
            techInput = findViewById(R.id.Tech);
            ftsInput = findViewById(R.id.FullTankSpecs);
            floraInput = findViewById(R.id.Flora);
            faunaInput = findViewById(R.id.Fauna);
            descriptionInput = findViewById(R.id.Description);
            ImageView takeGalleryTankPic = findViewById(R.id.TakeGalleryTankPic);
            tankImage = findViewById(R.id.TankImage);
            galleryDisplayPic = findViewById(R.id.GalleryDisplayPic);
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getResources().getString(R.string.Uploading));

            galleryItemRef = FirebaseDatabase.getInstance().getReference("GI");

            takeGalleryTankPic.setOnClickListener(v -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    verifyPermissions();
                } else {
                    selectImage();
                }

            });

            loadInitialData();
        }


    }


    private void loadInitialData(){

        DatabaseReference tempRef = galleryItemRef.child(User_ID);

        if(mode.equals("edit")){

            tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //TODO : Set all data here for any new addition to realtime database

                    authorsNameInput.setText(dataSnapshot.child("authorsName").getValue(String.class));
                    techInput.setText(dataSnapshot.child("tech").getValue(String.class));
                    ftsInput.setText(dataSnapshot.child("fts").getValue(String.class));
                    floraInput.setText(dataSnapshot.child("flora").getValue(String.class));
                    faunaInput.setText(dataSnapshot.child("fauna").getValue(String.class));
                    descriptionInput.setText(dataSnapshot.child("description").getValue(String.class));
                    facebookURLInput.setText(dataSnapshot.child("facebookURL").getValue(String.class));
                    twitterURLInput.setText(dataSnapshot.child("twitterURL").getValue(String.class));
                    instagramURLInput.setText(dataSnapshot.child("instagramURL").getValue(String.class));
                    webURLInput.setText(dataSnapshot.child("websiteURL").getValue(String.class));
                    tempImageID = dataSnapshot.child("tankImageFileName").getValue(String.class);
                    galleryInfo.setTankImageFileName(tempImageID);
                    galleryInfo.setRating(dataSnapshot.child("rating").getValue(Float.class));
                    galleryInfo.setYourRating(dataSnapshot.child("yourRating").getValue(Float.class));
                    galleryInfo.setRatingCount(dataSnapshot.child("ratingCount").getValue(Integer.class));
                    galleryInfo.setTankImageURL(dataSnapshot.child("tankImageURL").getValue(String.class));


                    Glide.with(GalleryActivity.this)
                            .load(Uri.parse(dataSnapshot.child("userphotoURL").getValue(String.class)))
                            .into(galleryDisplayPic);


                    String picUrl=dataSnapshot.child("tankImageURL").getValue(String.class);
                    picUrl.replace("\\u003d","=");

                    Glide.with(GalleryActivity.this)
                            .load(Uri.parse(picUrl))
                            .into(tankImage);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }

        else {
            if (!TextUtils.isEmpty(mAuth.getCurrentUser().getDisplayName()))
                authorsNameInput.setText(mAuth.getCurrentUser().getDisplayName());

            if (!TextUtils.isEmpty(mAuth.getCurrentUser().getPhotoUrl().toString()))
                Glide.with(this)
                        .load(mAuth.getCurrentUser().getPhotoUrl())
                        .into(galleryDisplayPic);
        }

    }

    private void selectImage(){
        
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
                tankpicUri = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        image);
            }else{
                tankpicUri=Uri.fromFile(image);
            }

            if (image.exists()) filedeleted=image.delete();


        }

        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setTitle("Get Image from");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Camera")) {

                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    i.putExtra(MediaStore.EXTRA_OUTPUT, tankpicUri);

                    startActivityForResult(i, REQUEST_CAMERA);
                }else if (items[which].equals("Gallery")) {

                    Intent i = new Intent(Intent.ACTION_PICK);

                    i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                    startActivityForResult(Intent.createChooser(i, "Select Image Using"), SELECT_FILE);




                }

                else{
                    dialog.dismiss();

                }
            }
        });
        builder.show();

    }
    
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
                        .into(tankImage);
            }

            if (requestCode == SELECT_FILE) {

                Uri tankPicUriFromGallery = data.getData();
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
                        .into(tankImage);



            }


            NEW_IMAGE_CREATED = true;


            Intent scanFileIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,tankpicUri);
            sendBroadcast(scanFileIntent);


        }

    }


    private void verifyPermissions(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[2]) == PackageManager.PERMISSION_GRANTED) {
                selectImage();

            } else {

                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2]==PackageManager.PERMISSION_GRANTED) {
            selectImage();
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
        if(newImageCreated){
            image.delete();
        }
    }
    
    
}
