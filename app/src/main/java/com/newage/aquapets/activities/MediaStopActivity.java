package com.newage.aquapets.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;


import androidx.appcompat.app.AppCompatActivity;



import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.newage.aquapets.dbhelpers.MyDbHelper;
import com.newage.aquapets.dbhelpers.NutrientDbHelper;
import com.newage.aquapets.R;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MediaStopActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private String  AName="",AID="",AType="",AquariumName="",timeStamp="",day="";
    private String STATUS;
    private MyDbHelper mydbhelper;
    private String alarmTextDetails="No Info!!!";
    private float waterChangePercent;
    private LinearLayout waterPercentLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);



        setContentView(R.layout.activity_media_stop);

        STATUS=getResources().getString(R.string.Skipped);
        Intent inn=getIntent();
        Bundle bundle=inn.getExtras();
        mediaPlayer=MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
        if (!mediaPlayer.isPlaying())
        {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }

        if (bundle!=null){
            AName=bundle.getString("AlarmName");
            AID=bundle.getString("AquariumID");
            AType=bundle.getString("AT");
            AquariumName=bundle.getString("AquariumName");
        }



        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        Button complete = findViewById(R.id.Complete);
        Button skip = findViewById(R.id.Skip);
        waterPercentLayout = findViewById(R.id.WaterPercentLayout);


        TextView alarminfo=findViewById(R.id.alarmtext);
        TextView alarmtextdetails=findViewById(R.id.alarmdetails);
        final Button waterPercentButton = findViewById(R.id.WaterPercentButton);

        final LinearLayout alarmWindowLayout = findViewById(R.id.AlarmWindowLayout);
        final LinearLayout newWaterChangePercentLayout = findViewById(R.id.NewWaterChangePercentLayout);
        final Button waterPercentEnteredButton = findViewById(R.id.WaterPercentEnteredButton);
        final EditText alarmWaterChangePercentInput = findViewById(R.id.AlarmWaterChangePercentInput);



        SharedPreferences tankSettings = getApplicationContext().getSharedPreferences(AID, 0);
        waterChangePercent = tankSettings.getFloat("waterChangePercent",0.5f);

        waterPercentButton.setText(String.format(Locale.getDefault(),"%.2f",waterChangePercent*100f));


        waterPercentEnteredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(alarmWaterChangePercentInput.getText().toString())){
                    Snackbar.make(alarmWindowLayout,"No input received. Default value will be used",Snackbar.LENGTH_LONG).show();
                }
                else{
                    waterChangePercent = Float.parseFloat(alarmWaterChangePercentInput.getText().toString().replace(",","."))/100f;
                    waterPercentButton.setText(alarmWaterChangePercentInput.getText().toString());
                }
                newWaterChangePercentLayout.setVisibility(View.GONE);
            }
        });






        waterPercentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newWaterChangePercentLayout.setVisibility(View.VISIBLE);


            }
        });



        mydbhelper= MyDbHelper.newInstance(this,AID);
        SQLiteDatabase db=mydbhelper.getWritableDatabase();
        Cursor c = mydbhelper.getDataAN2Condition(db,"Category",AType,"AlarmName",AName);
        if (c.moveToFirst()) {

            alarmTextDetails = c.getString(2);
            alarmtextdetails.setText(alarmTextDetails);

        }
        c.close();



        String banner=AType+" "+AName+" "+getResources().getString(R.string.Alert)+" "+AquariumName;


        alarminfo.setText(banner);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    STATUS=getResources().getString(R.string.Completed);



                }
                finish();


            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    STATUS=getResources().getString(R.string.Skipped);


                }
                finish();


            }
        });

        if(AType.equals("Water Change") && AName.equals("Weekly")){
            waterPercentLayout.setVisibility(View.VISIBLE);
        }
        else{
            waterPercentLayout.setVisibility(View.GONE);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        day = new SimpleDateFormat("EE", Locale.getDefault()).format(new java.util.Date());
        timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new java.util.Date());

        mydbhelper.addDataLogs(day,timeStamp,AName,AType,STATUS,alarmTextDetails,Long.toString(System.currentTimeMillis()));


        float kPPM ;
        float nPPM ;
        float pPPM ;
        float sPPM ;
        float cPPM ;
        float mPPM ;

        Calendar previousDay = Calendar.getInstance();
        previousDay.add(Calendar.DAY_OF_YEAR, -1);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        String previousDate = simpleDateFormat.format(previousDay.getTimeInMillis());

        //region UPDATE 'DAILY NUTRIENT DOSING DATABASE

        if(AType.equals("Dosing") && AName.equals("Macro") && STATUS.equals(getResources().getString(R.string.Completed))) {

            NutrientDbHelper nutrientDbHelper = NutrientDbHelper.newInstance(this, AID);
            Cursor cursor = nutrientDbHelper.getDataMAD();

            if(cursor!=null && cursor.getCount()>0) {


                if (cursor.moveToFirst()) {


                    kPPM = TextUtils.isEmpty(cursor.getString(7))?0f :Float.parseFloat(cursor.getString(7).replace(",", "."));
                    nPPM = TextUtils.isEmpty(cursor.getString(8))?0f :Float.parseFloat(cursor.getString(8).replace(",", "."));
                    pPPM = TextUtils.isEmpty(cursor.getString(9))?0f :Float.parseFloat(cursor.getString(9).replace(",", "."));
                    cPPM = TextUtils.isEmpty(cursor.getString(10))?0f :Float.parseFloat(cursor.getString(10).replace(",", "."));
                    mPPM = TextUtils.isEmpty(cursor.getString(11))?0f :Float.parseFloat(cursor.getString(11).replace(",", "."));
                    sPPM = TextUtils.isEmpty(cursor.getString(12))?0f :Float.parseFloat(cursor.getString(12).replace(",", "."));

                    // Log.i("Nutrient","Get all macro ppm values");
                    String dateOnly = timeStamp.split(" ")[0];

                    //db.execSQL("create table MacroLogs(Date text,kPpm text, nPpm text, pPpm text, caPpm text, mgPpm text, sPpm text)");

                    Cursor cr = mydbhelper.getDataMaLDayWise(previousDate);

                    Cursor crUptake = mydbhelper.getDataMaLDayWise("UPTAKE_PPM");

                    float kUptake = 0f;
                    float nUptake = 0f;
                    float pUptake = 0f;
                    float cUptake = 0f;
                    float mUptake = 0f;
                    float sUptake = 0f;

                    if(crUptake.moveToFirst()){
                        kUptake = TextUtils.isEmpty(crUptake.getString(1))?0f :Float.parseFloat(crUptake.getString(1).replace(",", "."));
                        nUptake = TextUtils.isEmpty(crUptake.getString(2))?0f :Float.parseFloat(crUptake.getString(2).replace(",", "."));
                        pUptake = TextUtils.isEmpty(crUptake.getString(3))?0f :Float.parseFloat(crUptake.getString(3).replace(",", "."));
                        cUptake = TextUtils.isEmpty(crUptake.getString(4))?0f :Float.parseFloat(crUptake.getString(4).replace(",", "."));
                        mUptake = TextUtils.isEmpty(crUptake.getString(5))?0f :Float.parseFloat(crUptake.getString(5).replace(",", "."));
                        sUptake = TextUtils.isEmpty(crUptake.getString(6))?0f :Float.parseFloat(crUptake.getString(6).replace(",", "."));
                    }
                    crUptake.close();

                    if (cr.moveToFirst()) {

                        kPPM += TextUtils.isEmpty(cr.getString(1))?0f :(Float.parseFloat(cr.getString(1).replace(",", "."))-kUptake);
                        nPPM += TextUtils.isEmpty(cr.getString(2))?0f :(Float.parseFloat(cr.getString(2).replace(",", "."))-nUptake);
                        pPPM += TextUtils.isEmpty(cr.getString(3))?0f :(Float.parseFloat(cr.getString(3).replace(",", "."))-pUptake);
                        cPPM += TextUtils.isEmpty(cr.getString(4))?0f :(Float.parseFloat(cr.getString(4).replace(",", "."))-cUptake);
                        mPPM += TextUtils.isEmpty(cr.getString(5))?0f :(Float.parseFloat(cr.getString(5).replace(",", "."))-mUptake);
                        sPPM += TextUtils.isEmpty(cr.getString(6))?0f :(Float.parseFloat(cr.getString(6).replace(",", "."))-sUptake);




                        // Log.i("Nutrient","Add all macro ppm values");

                        Cursor crCurrentDate = mydbhelper.getDataMaLDayWise(dateOnly);
                        if (crCurrentDate.moveToFirst()) {
                            // Log.i("Nutrient","entry exists");

                            mydbhelper.updateDataMaL(dateOnly, String.format(Locale.getDefault(), "%.2f", kPPM), String.format(Locale.getDefault(), "%.2f", nPPM), String.format(Locale.getDefault(), "%.2f", pPPM), String.format(Locale.getDefault(), "%.2f", cPPM), String.format(Locale.getDefault(), "%.2f", mPPM), String.format(Locale.getDefault(), "%.2f", sPPM), STATUS,"","");
                        } else {
                            //  Log.i("Nutrient","entry does not exist");

                            mydbhelper.addDataMaL(dateOnly, String.format(Locale.getDefault(), "%.2f", kPPM), String.format(Locale.getDefault(), "%.2f", nPPM), String.format(Locale.getDefault(), "%.2f", pPPM), String.format(Locale.getDefault(), "%.2f", cPPM), String.format(Locale.getDefault(), "%.2f", mPPM), String.format(Locale.getDefault(), "%.2f", sPPM), STATUS,"","");
                        }
                        crCurrentDate.close();

                    }
                    else{

                        // Log.i("Nutrient","entry does not exist");
                        mydbhelper.addDataMaL(dateOnly, String.format(Locale.getDefault(), "%.2f", kPPM), String.format(Locale.getDefault(), "%.2f", nPPM), String.format(Locale.getDefault(), "%.2f", pPPM), String.format(Locale.getDefault(), "%.2f", cPPM), String.format(Locale.getDefault(), "%.2f", mPPM), String.format(Locale.getDefault(), "%.2f", sPPM), STATUS,"","");

                    }
                    cr.close();
                }
                cursor.close();
            }



        } else if (AType.equals("Water Change") && AName.equals("Weekly") && STATUS.equals(getResources().getString(R.string.Completed))) {


            Cursor crPrev = mydbhelper.getDataMaLDayWise(previousDate);
            if(crPrev.moveToFirst()) {

                kPPM = TextUtils.isEmpty(crPrev.getString(1))?0f : Float.parseFloat(crPrev.getString(1).replace(",", ".")) * (1f-waterChangePercent);
                nPPM = TextUtils.isEmpty(crPrev.getString(2))?0f : Float.parseFloat(crPrev.getString(2).replace(",", ".")) * (1f-waterChangePercent);
                pPPM = TextUtils.isEmpty(crPrev.getString(3))?0f : Float.parseFloat(crPrev.getString(3).replace(",", ".")) * (1f-waterChangePercent);
                cPPM = TextUtils.isEmpty(crPrev.getString(4))?0f : Float.parseFloat(crPrev.getString(4).replace(",", ".")) * (1f-waterChangePercent);
                mPPM = TextUtils.isEmpty(crPrev.getString(5))?0f : Float.parseFloat(crPrev.getString(5).replace(",", ".")) * (1f-waterChangePercent);
                sPPM = TextUtils.isEmpty(crPrev.getString(6))?0f : Float.parseFloat(crPrev.getString(6).replace(",", ".")) * (1f-waterChangePercent);
                crPrev.close();

                String dateOnly = timeStamp.split(" ")[0];

                Cursor crCurrent = mydbhelper.getDataMaLDayWise(dateOnly);
                if (crCurrent.moveToFirst()) {

                    mydbhelper.updateDataMaL(dateOnly, String.format(Locale.getDefault(), "%.2f", kPPM), String.format(Locale.getDefault(), "%.2f", nPPM), String.format(Locale.getDefault(), "%.2f", pPPM), String.format(Locale.getDefault(), "%.2f", cPPM), String.format(Locale.getDefault(), "%.2f", mPPM), String.format(Locale.getDefault(), "%.2f", sPPM), STATUS, String.format(Locale.getDefault(),"%.2f",waterChangePercent),"");
                } else {


                    mydbhelper.addDataMaL(dateOnly, String.format(Locale.getDefault(), "%.2f", kPPM), String.format(Locale.getDefault(), "%.2f", nPPM), String.format(Locale.getDefault(), "%.2f", pPPM), String.format(Locale.getDefault(), "%.2f", cPPM), String.format(Locale.getDefault(), "%.2f", mPPM), String.format(Locale.getDefault(), "%.2f", sPPM), STATUS, String.format(Locale.getDefault(),"%.2f",waterChangePercent),"");
                }
                crCurrent.close();

            }crPrev.close();


        }
        //endregion


    }
}
