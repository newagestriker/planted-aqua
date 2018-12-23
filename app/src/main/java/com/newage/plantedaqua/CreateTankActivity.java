package com.newage.plantedaqua;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;


public class CreateTankActivity extends AppCompatActivity {

    int REQUEST_CAMERA=4;
    int SELECT_FILE=5;
    ImageView tankImage;
    Uri tankpicUri;
    Uri tankPicUriFromGallery;
    File image;
    String aquaname="";
    String aquatype="";
    String startupdate="";
    String ID="";
    long rowid;
    String s="";
    String dt="";
    int position;
    String price;
    String currency;
    String volume;
    String volumemetric;
    String currentstatus;
    String dismantledate;
    String co2;
    String lighttype;
    String wattage;
    String lumensperwatt;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    String tempID="";
    boolean newImageCreated=false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_tank_details,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean TANK_NAME_PRESENT;
        if(item.getItemId()==R.id.Save){

            if(tankpicUri!=null) {
                s = tankpicUri.toString();
            }

            Intent i=getIntent();
            String mode=i.getStringExtra("mode");
            TANK_NAME_PRESENT=getAllData();
            if(TANK_NAME_PRESENT) {
                if (mode.equals("creation")) {

                    TankDBHelper tankDBHelper = TankDBHelper.newInstance(this);
                    SQLiteDatabase DB = tankDBHelper.getWritableDatabase();
                    rowid = DatabaseUtils.queryNumEntries(DB, "TankDetails");
                    tankDBHelper.addData(DB, rowid, ID, aquaname, s, aquatype, price, currency, volume, volumemetric, currentstatus, startupdate, dismantledate, co2, lighttype, wattage, lumensperwatt, "", "");
                }


                if (mode.equals("modification")) {

                    TankDBHelper tankDBHelper = TankDBHelper.newInstance(this);
                    SQLiteDatabase DB = tankDBHelper.getWritableDatabase();
                    tankDBHelper.updateItem(DB, ID, aquaname, s, aquatype, price, currency, volume, volumemetric, currentstatus, startupdate, dismantledate, co2, lighttype, wattage, lumensperwatt, "", "");
                }

                Intent intent = new Intent();
                intent.putExtra("Aquaname", aquaname);
                intent.putExtra("Aquatype", aquatype);
                intent.putExtra("ImageUri", s);
                intent.putExtra("StartupDate", startupdate);
                intent.putExtra("Tag", ID);
                intent.putExtra("Position", position);

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tank_details);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.TankData));
        tankImage = findViewById(R.id.TankImage);
        progressBar = findViewById(R.id.CTankProgressBar);
        linearLayout=findViewById(R.id.LinearTankDetails);



        Intent intent=getIntent();

        String mode=intent.getStringExtra("mode");
        if(mode.equals("creation")) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.getDefault()).format(new Date());
            int rnd = new Random().nextInt(10000);
            ID = timeStamp + "_" + rnd;

        }

        position=intent.getIntExtra("Position",-1);
        if(position>=0) {

             TankDBHelper tankDBHelper = TankDBHelper.newInstance(this);
             SQLiteDatabase DB=tankDBHelper.getWritableDatabase();
            Cursor c = tankDBHelper.getData(DB);
            if(c.getCount() > 0) {
                c.moveToPosition(position);
                setAlldata(c);
            }
            c.close();
        }





        ImageView calendarIcon=findViewById(R.id.startupCalendar);
        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectDate(R.id.StartupDateInput);

            }
        });

        calendarIcon=findViewById(R.id.dismantleCalendar);
        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectDate(R.id.DismantleDateInput);

            }
        });




        ImageView loadImage = findViewById(R.id.loadImage);
        loadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    verifyPermissions();
                }else{
                    selectImage();
                }
            }
        });
    }

    public boolean getAllData()
    {

        EditText AquaName=findViewById(R.id.AquariumNameInput);
        if(!AquaName.getText().toString().isEmpty()) {
            aquaname = AquaName.getText().toString();


            EditText Price = findViewById(R.id.AquariumPriceInput);

                price = Price.getText().toString();

            EditText LightType = findViewById(R.id.AquariumLightInput);

                lighttype = LightType.getText().toString();

            EditText Wattage = findViewById(R.id.AquariumWattageInput);

                wattage = Wattage.getText().toString();


            EditText StartupDate = findViewById(R.id.StartupDateInput);

                startupdate = StartupDate.getText().toString();


            EditText DismantleDate = findViewById(R.id.DismantleDateInput);

                dismantledate = DismantleDate.getText().toString();


            EditText LumensPerWatt = findViewById(R.id.LumensPerWatt);

                lumensperwatt = LumensPerWatt.getText().toString();


            EditText Currency = findViewById(R.id.AquariumCurrencyInput);

                currency = Currency.getText().toString();


            EditText Volume = findViewById(R.id.AquariumVolumeInput);

                volume = Volume.getText().toString();


            RadioGroup AquariumType = findViewById(R.id.AquariumTypeGroup);
            RadioButton selectedradiobutton = findViewById(AquariumType.getCheckedRadioButtonId());

                aquatype = selectedradiobutton.getText().toString();


            RadioGroup CurrentStatus = findViewById(R.id.AquariumUseGroup);
            selectedradiobutton = findViewById(CurrentStatus.getCheckedRadioButtonId());

                currentstatus = selectedradiobutton.getText().toString();


            RadioGroup CO2 = findViewById(R.id.AquariumCO2Group);
            selectedradiobutton = findViewById(CO2.getCheckedRadioButtonId());

                co2 = selectedradiobutton.getText().toString();


            Spinner VolumeMetric = findViewById(R.id.AquariumVolMetricSpinner);

                volumemetric = VolumeMetric.getSelectedItem().toString();


            return true;
        }

        else{
            Snackbar.make(linearLayout,getResources().getString(R.string.TankNameMust),Snackbar.LENGTH_LONG).show();
            return false;
        }


    }

    public void setAlldata(Cursor c){

        EditText AquaName=findViewById(R.id.AquariumNameInput);
        AquaName.setText(c.getString(2));


        EditText Price=findViewById(R.id.AquariumPriceInput);
        Price.setText(c.getString(5));

        EditText LightType=findViewById(R.id.AquariumLightInput);
        LightType.setText(c.getString(13));

        EditText Wattage=findViewById(R.id.AquariumWattageInput);
        Wattage.setText(c.getString(14));


        EditText StartupDate=findViewById(R.id.StartupDateInput);
        StartupDate.setText(c.getString(10));


        EditText DismantleDate=findViewById(R.id.DismantleDateInput);
        DismantleDate.setText(c.getString(11));


        EditText LumensPerWatt=findViewById(R.id.LumensPerWatt);
        LumensPerWatt.setText(c.getString(15));

        EditText Currency=findViewById(R.id.AquariumCurrencyInput);
        Currency.setText(c.getString(6));

        EditText Volume=findViewById(R.id.AquariumVolumeInput);
        Volume.setText(c.getString(7));

        RadioGroup AquariumType = findViewById(R.id.AquariumTypeGroup);
        for (int i = 0; i < AquariumType.getChildCount(); i++) {

            if(c.getString(4).equals(((RadioButton)AquariumType.getChildAt(i)).getText().toString())){
                ((RadioButton) AquariumType.getChildAt(i)).setChecked(true);
                break;
            }

        }


        RadioGroup CurrentStatus = findViewById(R.id.AquariumUseGroup);

        for (int i = 0; i < CurrentStatus.getChildCount(); i++) {

            if(c.getString(9).equals(((RadioButton)CurrentStatus.getChildAt(i)).getText().toString())){
                ((RadioButton)CurrentStatus.getChildAt(i)).setChecked(true);
                break;
            }

        }


        RadioGroup CO2 = findViewById(R.id.AquariumCO2Group);
        for (int i = 0; i < CO2.getChildCount(); i++) {

            if(c.getString(12).equals(((RadioButton)CO2.getChildAt(i)).getText().toString())){
                ((RadioButton)CO2.getChildAt(i)).setChecked(true);
                break;
            }

        }

        String volumemetric[]={"Litre","US Gallon","UK Gallon"};
        int pos=0;
        for (int i = 0; i < 3; i++) {

          if(c.getString(8).equals(volumemetric[i])){
              pos=i;
              break;
          }

        }


        Spinner VolumeMetric=findViewById(R.id.AquariumVolMetricSpinner);
        VolumeMetric.setSelection(pos);
        ID=c.getString(1);
        s=c.getString(3);

        tankImage = findViewById(R.id.TankImage);

        s=c.getString(3);
        tankpicUri = Uri.parse(s);
        Glide.with(this)
                .load(tankpicUri)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .error(R.drawable.aquarium2))
                .into(tankImage);
            tankImage.setFocusable(true);
            tankImage.setClickable(true);



        tankImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(tankpicUri, "image/*");
                i.setFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivity(i);

            }
        });


    }

    public void selectDate(final int idName) {


        final Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTankActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dt = dayOfMonth + "/" + month + "/" + year;
                EditText dateText1=findViewById(idName);
                dateText1.setText(dt);

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }





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

            image = new File(imagesFolder, "TankNo_"+tempID+"_Pic.jpg");
            if (Build.VERSION.SDK_INT >= 24) {
                tankpicUri = FileProvider.getUriForFile(CreateTankActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        image);
            }else{
                tankpicUri=Uri.fromFile(image);
            }

            if (image.exists()) filedeleted=image.delete();


        }

        AlertDialog.Builder builder =new AlertDialog.Builder(CreateTankActivity.this);
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

                tankPicUriFromGallery=data.getData();
                try {
                    fileCreated = image.createNewFile();
                    if (fileCreated) {


                        try {
                            image.createNewFile();
                            this.copyFile(new File(getRealPathFromURI(data.getData())), image);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
                Glide.with(this)
                        .load(tankPicUriFromGallery)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .error(R.drawable.aquarium2))
                        .into(tankImage);

            }





                Intent scanFileIntent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,tankpicUri);
                sendBroadcast(scanFileIntent);


        }





               /* Uri imageuri=data.getData();
                tankImage.setImageURI(imageuri);



                Intent i=new Intent(Intent.ACTION_VIEW);

                i.setDataAndType(selectImageUri, "image/*");
                startActivity(i);*/


        }


    public void verifyPermissions(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[2]) == PackageManager.PERMISSION_GRANTED) {
                selectImage();

            } else {

                ActivityCompat.requestPermissions(CreateTankActivity.this, permissions, 1);
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
