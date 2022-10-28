package com.newage.aquapets.helpers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;
import androidx.core.content.ContextCompat;

import com.newage.aquapets.R;
import com.newage.aquapets.dbhelpers.MyDbHelper;
import com.newage.aquapets.dbhelpers.NutrientDbHelper;

import static com.newage.aquapets.helpers.NotificationHelper.channel1ID;
import static com.newage.aquapets.activities.RamizAlarm.KEY_TEXT_REPLY;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        float waterChangePercent = 0.5f;


        String AquariumID = intent.getStringExtra("AquariumID");
        String AName = intent.getStringExtra("AlarmName");
        String AType = intent.getStringExtra("AT");
        String STATUS = intent.getStringExtra("Status");
        int NotificationID = intent.getIntExtra("NotificationID", 0);
        String alarmTextDetails = "No Info!!!";
        NotificationManager notificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel("com.newage.aquapets",NotificationID);


        if (!AName.equals("MONTHLY_EXPENSE_ALARM")) {



            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new java.util.Date());
            String day = new SimpleDateFormat("EE", Locale.getDefault()).format(new java.util.Date());

            MyDbHelper mydbhelper = MyDbHelper.newInstance(context, AquariumID);
            Cursor c = mydbhelper.getDataAN2Condition(mydbhelper.getReadableDatabase(),"Category",AType,"AlarmName",AName);
            if (c.moveToFirst()) {

                alarmTextDetails = c.getString(2);

            }
            c.close();

            mydbhelper.addDataLogs(day, timeStamp, AName, AType, STATUS,alarmTextDetails ,Long.toString(System.currentTimeMillis()) );



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

            if(AType.equals("Dosing") && AName.equals("Macro") && STATUS.equals(context.getResources().getString(R.string.Completed))) {

                NutrientDbHelper nutrientDbHelper = NutrientDbHelper.newInstance(context, AquariumID);
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

                        Cursor cr = mydbhelper.getDataMaLDayWise(previousDate);

                        if (cr.moveToFirst()) {

                            kPPM += TextUtils.isEmpty(cr.getString(1))?0f :(Float.parseFloat(cr.getString(1).replace(",", "."))-kUptake);
                            nPPM += TextUtils.isEmpty(cr.getString(2))?0f :(Float.parseFloat(cr.getString(2).replace(",", "."))-nUptake);
                            pPPM += TextUtils.isEmpty(cr.getString(3))?0f :(Float.parseFloat(cr.getString(3).replace(",", "."))-pUptake);
                            cPPM += TextUtils.isEmpty(cr.getString(4))?0f :(Float.parseFloat(cr.getString(4).replace(",", "."))-cUptake);
                            mPPM += TextUtils.isEmpty(cr.getString(5))?0f :(Float.parseFloat(cr.getString(5).replace(",", "."))-mUptake);
                            sPPM += TextUtils.isEmpty(cr.getString(6))?0f :(Float.parseFloat(cr.getString(6).replace(",", "."))-sUptake);




                           // Log.i("Nutrient","Add all macro ppm values");



                        }
                        else{

                            kPPM -= kUptake;
                            nPPM -= nUptake;
                            pPPM -= pUptake;
                            cPPM -= cUptake;
                            mPPM -= mUptake;
                            sPPM -= sUptake;


                        }
                        cr.close();

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
                    cursor.close();
                }



                } else if (AType.equals("Water Change") && AName.equals("Weekly") && STATUS.equals(context.getResources().getString(R.string.Completed))) {

                CharSequence waterChange;
                Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
                if (remoteInput != null) {
                    waterChange = remoteInput.getCharSequence(KEY_TEXT_REPLY);
                    Toast.makeText(context,waterChange,Toast.LENGTH_LONG).show();

                    //region BUILD ANOTHER NOTIFICATION TO UPDATE THE USER
                    if(!TextUtils.isEmpty(waterChange)) {
                        NotificationCompat.Builder repliedNotification = new NotificationCompat.Builder(context, channel1ID);

                        repliedNotification.setContentTitle("Weekly Water Change Task Completed")
                                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                                .setAutoCancel(true)
                                .setContentText("Water Change Percentage for this Week is " + waterChange)
                                .setSmallIcon(R.drawable.plant3)
                                .setGroup(AType)
                                .setPriority(NotificationCompat.PRIORITY_MAX);

                        NotificationHelper notificationHelper = new NotificationHelper(context);
                        notificationHelper.getManager().notify("com.newage.aquapets", NotificationID, repliedNotification.build());
                    }
                    //endregion

                    SharedPreferences tankSettings = context.getApplicationContext().getSharedPreferences(AquariumID, 0);
                    waterChangePercent = tankSettings.getFloat("waterChangePercent",0.5f);


                    if(!TextUtils.isEmpty(waterChange)) {
                        try {
                            waterChangePercent = Float.parseFloat(((String) waterChange).replace(",", ".")) / 100f;
                        }catch (Exception e){
                            Toast.makeText(context,"Error occurred. Please enter only numeric values from 0-100. Set to Default Values",Toast.LENGTH_LONG).show();
                        }
                    }

                }
                else
                {
                    Toast.makeText(context,"No input received. Please enter only numeric values from 0-100. Set to Default Values",Toast.LENGTH_LONG).show();
                }




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


                SharedPreferences preferences = context.getSharedPreferences(AquariumID, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("DAYS_WITHOUT_WATER_CHANGE", 0);
                editor.apply();


            }

            //endregion




        }
    }
}
