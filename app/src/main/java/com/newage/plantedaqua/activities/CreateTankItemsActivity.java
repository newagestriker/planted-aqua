package com.newage.plantedaqua.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.newage.plantedaqua.BuildConfig;
import com.newage.plantedaqua.helpers.ExpenseDBHelper;
import com.newage.plantedaqua.helpers.MyDbHelper;
import com.newage.plantedaqua.R;
import com.newage.plantedaqua.helpers.TankDBHelper;
import com.newage.plantedaqua.helpers.TinyDB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class CreateTankItemsActivity extends AppCompatActivity {

    EditText NameInput,PriceInput,AcDate,ExpiryDate,Food,Care,Info,QuantityInput,catInput,SciInput;
    ImageView eqImage,loadImage,acCalendar,expCalendar;
    TextView CurInput;
    LinearLayout linearLayout;
    RadioGroup Gender;
    String AquariumID;
    MyDbHelper myDbHelper;
    private String category;
    int REQUEST_CAMERA=23;
    int SELECT_FILE=31;
    Uri tankpicUri;
    Uri tankPicUriFromGallery;
    File image;
    private String s="",sciName="";
    private String dt="";
    private String nameInput="",quantityInput="1";
    private String gender="";
    private String curInput="";
    private String priceInput="0.0";
    private String acDate="0000-00-00";
    private String expiryDate="";
    private String food="";
    private String care="";
    private String info="";
    private String ID="";
    private String mode="";
    boolean newImageCreated=false;
    TinyDB settingsDB;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_tank_details,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean Name;
        String txt1,txt2,txt3,txt4;

        if(item.getItemId()==R.id.Save){
            Name=getAllData();
            if(tankpicUri!=null) {
                s = tankpicUri.toString();
            }

            switch (category){

                case "E":
                    txt1=nameInput;
                    txt2=quantityInput;
                    txt3=acDate;
                    txt4=expiryDate;
                    break;

                default:
                    txt1=nameInput;
                    txt2=quantityInput;
                    txt3=sciName;
                    txt4=acDate;
                    break;

            }

            if(Name) {

                String aquaname="";
                myDbHelper = MyDbHelper.newInstance(this, AquariumID);
                TankDBHelper tankDBHelper = TankDBHelper.newInstance(this);
                Cursor c = tankDBHelper.getDataCondition("AquariumID",AquariumID);
                if(c!=null){
                    if(c.moveToFirst()){

                        aquaname = c.getString(2);

                    }
                }
                SQLiteDatabase db = myDbHelper.getWritableDatabase();
                ExpenseDBHelper expenseDBHelper = ExpenseDBHelper.getInstance(this);
                float numericPrice = Float.parseFloat(priceInput.replace(",","."));
                int numericQuantity = Integer.parseInt(quantityInput);


                if (mode.equals("creation")) {


                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    ID = category + "_" + timeStamp;
                    myDbHelper.addDataTI(db, ID, nameInput, category, s, curInput, numericPrice, Integer.parseInt(quantityInput), acDate, expiryDate, gender, food, care, info, sciName);
                    expenseDBHelper.addDataExpense(expenseDBHelper.getWritableDatabase(),ID,aquaname,nameInput,dy,mnth,yr,acDate,0L,numericQuantity,numericPrice,numericPrice*numericQuantity,"",AquariumID);
                }
                if (mode.equals("modification")) {
                    dy = Integer.parseInt(acDate.split("-")[2]);
                    mnth = Integer.parseInt(acDate.split("-")[1]);
                    yr = Integer.parseInt(acDate.split("-")[0]);


                    myDbHelper.updateItemTI(db, ID, nameInput, category, s, curInput, numericPrice, Integer.parseInt(quantityInput), acDate, expiryDate, gender, food, care, info, sciName);
                    if(expenseDBHelper.checkExists(ID)) {
                        expenseDBHelper.updateDataExpense(expenseDBHelper.getWritableDatabase(), ID, aquaname, nameInput, dy, mnth, yr, acDate, 0L, numericQuantity, numericPrice, numericPrice * numericQuantity, "", AquariumID);
                    }else{
                        expenseDBHelper.addDataExpense(expenseDBHelper.getWritableDatabase(),ID,aquaname,nameInput,dy,mnth,yr,acDate,0L,numericQuantity,numericPrice,numericPrice*numericQuantity,"",AquariumID);

                    }
                }

                int position=getIntent().getIntExtra("position",-1);

                Intent intent = new Intent();
                intent.putExtra("ImageUri", s);
                intent.putExtra("Txt1", txt1);
                intent.putExtra("Txt2", txt2);
                intent.putExtra("Txt3", txt3);
                intent.putExtra("Txt4", txt4);
                intent.putExtra("Tag", ID);
                intent.putExtra("QuickNote", info);
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
        setContentView(R.layout.activity_tank_items);

        AquariumID=getIntent().getStringExtra("AquariumID");
        category=getIntent().getStringExtra("ItemCategory");
        mode=getIntent().getStringExtra("mode");

        //intent.putExtra("Position",position);

        settingsDB = new TinyDB(this.getApplicationContext());

        linearLayout=findViewById(R.id.LinearTankItemDetails);

        NameInput=findViewById(R.id.NameInput);
        SciInput=findViewById(R.id.SciEdit);
        QuantityInput=findViewById(R.id.QuantityInput);
        CurInput=findViewById(R.id.EqCurrencyInput);
        PriceInput=findViewById(R.id.EqPriceInput);
        Food=findViewById(R.id.FoodInput);
        Care=findViewById(R.id.CareInput);
        Info=findViewById(R.id.Info);
        Gender=findViewById(R.id.Gender);
        eqImage=findViewById(R.id.EqImage);
        AcDate=findViewById(R.id.AcDateInput);
        ExpiryDate=findViewById(R.id.ExDateInput);

        loadImage=findViewById(R.id.loadImage);

        CurInput.setText(settingsDB.getString("DefaultCurrencySymbol"));

        if(mode.equals("modification")) {
            ID = getIntent().getStringExtra("ItemID");
            setAllData();
        }
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

        acCalendar=findViewById(R.id.AcCalendar);
        acCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(R.id.AcDateInput);
            }
        });

        expCalendar=findViewById(R.id.ExpCalendar);
        expCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(R.id.ExDateInput);
            }
        });

        switch(category) {

            case "Fl":

                findViewById(R.id.GenderText).setVisibility(View.GONE);
                findViewById(R.id.FoodText).setVisibility(View.GONE);

                Gender.setVisibility(View.GONE);
                Food.setVisibility(View.GONE);

                break;

            case "E":

                findViewById(R.id.GenderText).setVisibility(View.GONE);
                findViewById(R.id.FoodText).setVisibility(View.GONE);
                findViewById(R.id.CareText).setVisibility(View.GONE);
                findViewById(R.id.SciNameText).setVisibility(View.GONE);

                SciInput.setVisibility(View.GONE);
                Gender.setVisibility(View.GONE);
                Food.setVisibility(View.GONE);
                Care.setVisibility(View.GONE);

                break;

            default:

                break;
        }

    }
    private void setAllData(){

        myDbHelper=MyDbHelper.newInstance(this,AquariumID);
        SQLiteDatabase db=myDbHelper.getWritableDatabase();
        Cursor c=myDbHelper.getDataTI2Condition(db,"I_ID",ID,"I_Category",category);
        c.moveToNext();
        s=c.getString(3);

       // Log.i("ID",ID);
      //  Log.i("Category",category);

        NameInput.setText(c.getString(1));


            SciInput.setText(c.getString(14));



            QuantityInput.setText(c.getString(6));


            CurInput.setText(settingsDB.getString("DefaultCurrencySymbol"));


            PriceInput.setText(c.getString(5));


            Food.setText(c.getString(10));


            Care.setText(c.getString(11));


            Info.setText(c.getString(13));



            for (int i = 0; i < Gender.getChildCount(); i++) {

                if (c.getString(9).equals(((RadioButton) Gender.getChildAt(i)).getText().toString())) {
                    ((RadioButton) Gender.getChildAt(i)).setChecked(true);
                    break;
                }

            }





            tankpicUri = Uri.parse(s);
            Glide.with(this)
                    .load(tankpicUri)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .error(R.drawable.aquarium2))
                    .into(eqImage);


            AcDate.setText(c.getString(7));


            ExpiryDate.setText(c.getString(8));

        c.close();




    }
    private boolean getAllData(){



        if(!NameInput.getText().toString().isEmpty()) {
            nameInput = NameInput.getText().toString();
            if (!QuantityInput.getText().toString().isEmpty()) {
                quantityInput= QuantityInput.getText().toString();
            }

            if (!CurInput.getText().toString().isEmpty()) {
                curInput = CurInput.getText().toString();
            }
            if (!SciInput.getText().toString().isEmpty()) {
                sciName = SciInput.getText().toString();
            }
            if (!PriceInput.getText().toString().isEmpty()) {
                priceInput = PriceInput.getText().toString();
            }
            if (!AcDate.getText().toString().isEmpty()) {
                acDate = AcDate.getText().toString();
            }
            if (!ExpiryDate.getText().toString().isEmpty()) {
                expiryDate = ExpiryDate.getText().toString();
            }

            RadioButton selectedradiobutton = findViewById(Gender.getCheckedRadioButtonId());
            if (!selectedradiobutton.getText().toString().isEmpty()) {
                gender = selectedradiobutton.getText().toString();
            }
            if (!Food.getText().toString().isEmpty()) {
                food = Food.getText().toString();
            }
            if (!Care.getText().toString().isEmpty()) {
                care = Care.getText().toString();
            }
            if (Info.getText() != null) {
                info = Info.getText().toString();
            }



            return true;


        }else
        {
            Snackbar.make(linearLayout,getResources().getString(R.string.NameMust),Snackbar.LENGTH_LONG).show();
            return false;
        }
    }

    int dy=0,mnth=0,yr=0;
    public void selectDate(final int idName) {


        final Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dt = year+"-"+convertToString(month+1)+"-"+convertToString(dayOfMonth);
                EditText dateText1=findViewById(idName);
                dateText1.setText(dt);

                if(idName==R.id.AcDateInput) {

                    dy = dayOfMonth;
                    mnth = month + 1;
                    yr = year;
                }

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    private String convertToString(int num){


        return num<10?("0"+num): Integer.toString(num);


    }

    public void selectImage(){

        boolean filedeleted;
        final CharSequence items[]={"Camera", "Gallery", "Cancel"};
        File imagesFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "JPT_images");
        if(!(imagesFolder.exists())){
            boolean dirCreated=imagesFolder.mkdirs();
        }

        if(imagesFolder.isDirectory()){



            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

            image = new File(imagesFolder, category+"_"+timeStamp+"_Pic.jpg");
            if (Build.VERSION.SDK_INT >= 24) {
                tankpicUri = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        image);
            }else{
                tankpicUri=Uri.fromFile(image);
            }

            if (image.exists()) filedeleted=image.delete();

           // System.out.println("Uri : "+tankpicUri.toString());


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

       /* if(data==null){
            System.out.println("Null data");
        }*/
        boolean fileCreated;




        if (resultCode == Activity.RESULT_OK) {
            //System.out.println("Result OK");
            newImageCreated=true;

            if (requestCode == REQUEST_CAMERA) {
                Glide.with(this)
                        .load(tankpicUri)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .error(R.drawable.aquarium2))
                        .into(eqImage);
            }

            if (requestCode == SELECT_FILE) {

                tankPicUriFromGallery=data.getData();
                try {
                    fileCreated = image.createNewFile();
                    if (fileCreated) {


                        try {
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
                Glide.with(this)
                        .load(tankPicUriFromGallery)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .error(R.drawable.aquarium2))
                        .into(eqImage);

            }





            Intent scanFileIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,tankpicUri);
            sendBroadcast(scanFileIntent);


        }





               /* Uri imageuri=data.getData();
                eqImage.setImageURI(imageuri);



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

                ActivityCompat.requestPermissions(CreateTankItemsActivity.this, permissions, 1);
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


