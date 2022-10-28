package com.newage.aquapets.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import com.newage.aquapets.BuildConfig;
import com.newage.aquapets.dbhelpers.MyDbHelper;
import com.newage.aquapets.R;
import com.newage.aquapets.adapters.RecyclerAdapterPicsInfo;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;


public class TankProgressActivity extends AppCompatActivity {

    int REQUEST_CAMERA = 0;
    int SELECT_FILE = 1;
    Uri tankpicUri;
    Uri tankPicUriFromGallery;
    File image;
    Long tankNo = 1L;
    RecyclerView recyclerView;



    RecyclerAdapterPicsInfo adapter;
    RecyclerView.LayoutManager layoutManager;
    String learning = "";
    String notes = "";
    ArrayList <TankProgressDetails> TPD = new ArrayList <>();
    TankProgressDetails tpd = new TankProgressDetails();
    MyDbHelper albumDbHelper;
    ArrayList<String> ImageURI=new ArrayList <>();
    LinearLayout linearLayout;
    TankProgressDetails temp=new TankProgressDetails();
    String aquaID = "";
    boolean clicked = false;
    Snackbar snackbar;
    ProgressBar progressBar;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.progress, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addDetails) {
            addAlbumData();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_tank_progress);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        progressBar = findViewById(R.id.progressTank);

        Intent i = getIntent();
        aquaID = i.getStringExtra("AquariumID");
        String aquaName=i.getStringExtra("AquariumName");

        getSupportActionBar().setTitle("TPA"+" : "+aquaName);


        linearLayout = findViewById(R.id.mainLayout);

        albumDbHelper = MyDbHelper.newInstance(this, aquaID);
        SQLiteDatabase db = albumDbHelper.getWritableDatabase();
        Cursor c = albumDbHelper.getDataAD(db);
        if (c != null) {
            while (c.moveToNext()) {
                tpd = new TankProgressDetails();
                tpd.setImageuri(c.getString(1));
                tpd.setImagedate(c.getString(2));
                tpd.setImageday(c.getString(3));
                tpd.setText2(c.getString(4));
                tpd.setText1(c.getString(5));
                TPD.add(tpd);

            }

            c.close();

        }
        db.close();
        recyclerView = findViewById(R.id.albumRecyclerView);
        layoutManager = new LinearLayoutManager(this);


       /* final SharedPreferences TankPicUri=getApplicationContext().getSharedPreferences("TankPicUri",0);
        String s=TankPicUri.getString("TankPicUri","");
        tankImage=findViewById(R.id.TankImage);


        if(!s.isEmpty()) {
            File file = new File(URI.create(s).getPath());
            if (file.exists()) {


            tankpicUri = Uri.parse(s);
            tankImage.setImageURI(tankpicUri);
            tankImage.setFocusable(true);
            tankImage.setClickable(true);
        }else{
                Toast.makeText(this,"File missing",Toast.LENGTH_SHORT).show();
            }

        }else
        {
            Glide.with(this)
                    .load(R.drawable.aquarium2)
                    .into(tankImage);
            tankImage.setFocusable(false);
            tankImage.setClickable(false);
        }
        tankImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(tankpicUri,"image/*");
                i.setFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivity(i);

            }
        });*/

       /* FloatingActionButton fbutton=findViewById(R.id.floatingActionButton);
        fbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPermissions();
                selectImage();
            }
        });*/

        recyclerView.setLayoutManager(layoutManager);

                adapter = new RecyclerAdapterPicsInfo(TPD, this,R.layout.tankprogressrow, new RecyclerAdapterPicsInfo.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, String uri) {

                Uri tankpicUri = Uri.parse(uri);

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(tankpicUri, "image/*");
                i.setFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivity(i);

            }

            @Override
            public void onLongClick(View view, int position) {

                //Toast.makeText(view.getContext(), "Long Clicked", Toast.LENGTH_SHORT).show();

            }


        });
        recyclerView.setAdapter(adapter);


        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //swiped position
                clicked = false;
                /*if(snackbar!=null&&snackbar.isShownOrQueued()){
                    snackbar.dismiss();
                }*/
                if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT ) { //swipe left
                    /*for(j=0;j<TPD.size();j++){
                        System.out.println("Position "+j+" Uri "+TPD.get(j).getImageuri());
                    }*/

                    temp=new TankProgressDetails();
                    temp = TPD.get(position);
                    ImageURI.add(temp.getImageuri());
                    //ln("Swiped ImageURI "+ImageURI);
                    TPD.remove(position);
                    adapter.notifyItemRemoved(position);
                    snackbar = Snackbar
                            .make(linearLayout, "Record deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    TPD.add(position, temp);
                                    clicked = true;
                                    ImageURI.remove(ImageURI.size()-1);
                                    adapter.notifyItemInserted(position);
                                    snackbar.dismiss();

                                }
                            }).addCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int dismissType) {
                                    super.onDismissed(snackbar, dismissType);

                                    if ((dismissType == DISMISS_EVENT_TIMEOUT || dismissType == DISMISS_EVENT_ACTION || dismissType == DISMISS_EVENT_SWIPE
                                            || dismissType == DISMISS_EVENT_CONSECUTIVE || dismissType == DISMISS_EVENT_MANUAL) && !clicked) {
                                        String tmp;
                                        for(int i=0;i<ImageURI.size();i++) {
                                            tmp=ImageURI.get(i);
                                            albumDbHelper.deleteItemAD("ImageUri", tmp);
                                            //System.out.println("Removed URI : "+tmp);
                                            ImageURI.remove(i);
                                        }

                                    }

                                }
                            });

                    snackbar.show();

                }

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


    }

    public void addAlbumData() {
        tpd = new TankProgressDetails();

        final EditText LearningText;
        View view = LayoutInflater.from(this).inflate(R.layout.albumdialog, null);
        LearningText = view.findViewById(R.id.enterText);
        LearningText.setHint(getResources().getString(R.string.LearningHint));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Data Required!!!");
        builder.setMessage("Any new learning today?");
        builder.setView(view);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                learning = LearningText.getText().toString();
                tpd.setText1("Learning: " + learning);
                getTxt2();


            }
        });
        builder.setNegativeButton("No Learning Today", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                learning = null;
                tpd.setText1(learning);
                getTxt2();

            }
        });
        builder.show();
    }

    public void getTxt2() {

        final EditText NotesText;
        View view2 = LayoutInflater.from(this).inflate(R.layout.albumdialog, null);



        NotesText = view2.findViewById(R.id.enterText);
        NotesText.setHint(getResources().getString(R.string.NotesHint));
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Data Required!!!");
        builder.setMessage("Any Additional Notes");
        builder.setView(view2);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                notes = NotesText.getText().toString();
                tpd.setText2("Notes: " + notes);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    verifyPermissions();
                }else{
                    selectImage();
                }

            }
        });
        builder.setNegativeButton("No Notes for now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                notes =null;
                tpd.setText2(notes);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    verifyPermissions();
                }else{
                    selectImage();
                }

            }
        });
        builder.show();
    }

    public void selectImage() {
        boolean filedeleted;
        final CharSequence items[] = {"Camera", "Gallery", "Cancel"};

        File imagesFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "JPT_images");
        if (!(imagesFolder.exists())) {
            boolean dirCreated = imagesFolder.mkdirs();
        }

        if (imagesFolder.isDirectory()) {


            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new java.util.Date());


            image = new File(imagesFolder, "TankNo_" + tankNo + "_" + timeStamp + "_Pic.jpg");
            if (Build.VERSION.SDK_INT >= 24)
            tankpicUri = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    image);
            else
                tankpicUri=Uri.fromFile(image);

            if (image.exists()) filedeleted = image.delete();


        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Get Image from");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Camera")) {

                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    i.putExtra(MediaStore.EXTRA_OUTPUT, tankpicUri);

                    startActivityForResult(i, REQUEST_CAMERA);
                } else if (items[which].equals("Gallery")) {

                    Intent i = new Intent(Intent.ACTION_PICK );

                    i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                    startActivityForResult(Intent.createChooser(i, "Select Image Using"), SELECT_FILE);
                } else {
                    dialog.dismiss();

                }
            }
        });
        builder.show();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean fileCreated;

        //System.out.println("Result code : " + resultCode);

        if (resultCode == Activity.RESULT_OK) {
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new java.util.Date());
            tpd.setImagedate(timeStamp);
            String day = new SimpleDateFormat("EE", Locale.getDefault()).format(new java.util.Date());
            tpd.setImageday(day);

            if(requestCode==REQUEST_CAMERA) {

                String s = tankpicUri.toString();
                tpd.setImageuri(s);
                TPD.add(tpd);
                adapter.notifyItemInserted(TPD.size() - 1);
            }


            if (requestCode == SELECT_FILE) {

                tankPicUriFromGallery = data.getData();

                try {
                    fileCreated = image.createNewFile();
                    if (fileCreated) {


                        try {
                            image.createNewFile();
                            this.copyFile(new File(getRealPathFromURI(data.getData())), image);
                        } catch (Exception e) {
                            String msg = TextUtils.isEmpty(e.getMessage())?getString(R.string.cloud_storage_error):e.getMessage();
                            Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {

                    String msg = TextUtils.isEmpty(e.getMessage())?getString(R.string.unknown_error):e.getMessage();
                    Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
                }




                String s=tankpicUri.toString();
                tpd.setImageuri(s);
                TPD.add(tpd);
                adapter.notifyItemInserted(TPD.size() - 1);



           //  MyTask myTask=new MyTask();
          //   myTask.execute();


            }




           /* System.out.println("tankpicuri: "+tankpicUri.toString());
            System.out.println("s: "+s);*/
            SQLiteDatabase db = albumDbHelper.getWritableDatabase();
            albumDbHelper.addDataAD(db, tankpicUri.toString(), tpd.getImagedate(), tpd.getImageday(), tpd.getText2(), tpd.getText1());

            db.close();

            /*SharedPreferences tankPicUri=getApplicationContext().getSharedPreferences("TankPicUri",0);
            SharedPreferences.Editor editor= tankPicUri.edit();
            editor.putString("TankPicUri",s);
            editor.apply();*/


            Intent scanFileIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, tankpicUri);
            sendBroadcast(scanFileIntent);


        }





               /* Uri imageuri=data.getData();
                tankImage.setImageURI(imageuri);



                Intent i=new Intent(Intent.ACTION_VIEW);

                i.setDataAndType(selectImageUri, "image/*");
                startActivity(i);*/


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

    public void verifyPermissions(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[2]) == PackageManager.PERMISSION_GRANTED) {
                selectImage();

            } else {

                ActivityCompat.requestPermissions(TankProgressActivity.this, permissions, 1);
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


}
