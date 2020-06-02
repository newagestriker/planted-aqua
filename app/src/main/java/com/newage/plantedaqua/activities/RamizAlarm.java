package com.newage.plantedaqua.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;
import androidx.core.content.ContextCompat;

import com.newage.plantedaqua.helpers.ExpenseDBHelper;
import com.newage.plantedaqua.helpers.MyDbHelper;
import com.newage.plantedaqua.helpers.NotificationHelper;
import com.newage.plantedaqua.helpers.TankDBHelper;
import com.newage.plantedaqua.helpers.TinyDB;
import com.newage.plantedaqua.helpers.NotificationReceiver;
import com.newage.plantedaqua.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.newage.plantedaqua.helpers.NotificationHelper.channel1ID;


public class RamizAlarm extends BroadcastReceiver {


    public static final String KEY_TEXT_REPLY = "key_text_reply";


    private int previousMonth=-1;
    private String AID="";
    private String AType="";

    private final static AtomicInteger c = new AtomicInteger(0);

    @Override
    public void onReceive(Context context, Intent intent) {

       // WakeLocker wakeLocker=new WakeLocker();
      //  wakeLocker.acquire(context);
       // Toast.makeText(context,"media player started",Toast.LENGTH_SHORT).show();

        Bundle extras=intent.getExtras();
        int intentID=0;
        String banner="",title,AName="",NotifyType="",AquariumName="";
        long triggerTime=0L;
        String storedTimeInMillis="";


        if(extras!=null) {
            AType = extras.getString("AT");
            AID=extras.getString("AquariumID");
            NotifyType=extras.getString("NotifyType");
            triggerTime=extras.getLong("KEY_TRIGGER_TIME");
            intentID=extras.getInt("KEY_INTENT_ID");
            AName=extras.getString("AlarmName");
            storedTimeInMillis = Long.toString(triggerTime);



            TankDBHelper tankDBHelper = TankDBHelper.newInstance(context);
            Cursor c = tankDBHelper.getDataCondition("AquariumID",AID);
            if(c!=null) {
                if (c.moveToFirst()) {
                    AquariumName = c.getString(2);
                }
                c.close();
            }

        }



        setAlarmOn1stDay(context);

        if(AType.equals("MONTHLY_EXPENSE")) {
            monthlyExpenseNotification(context, intent);
        }
        else if(AType.equals("EVERYDAY")) {
            everydayNotification(context, intent);
        }

        else {


            //region SET ACTION FOR WATER CHANGE INPUT

            SharedPreferences tankSettings = context.getApplicationContext().getSharedPreferences(AID, 0);
            float waterChangePercent = tankSettings.getFloat("waterChangePercent",0.5f);

            String replyLabel = "Default is " + String.format(Locale.getDefault(),"%.2f",waterChangePercent*100f);
            RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                    .setLabel(replyLabel)
                    .build();

            Intent replyIntent = new Intent(context, NotificationReceiver.class);
            replyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            replyIntent.putExtra("AquariumID", AID);
            replyIntent.putExtra("AT", AType);
            replyIntent.putExtra("AlarmName", AName);
            replyIntent.putExtra("Status", context.getResources().getString(R.string.Completed));
            replyIntent.putExtra("NotificationID", intentID);
            replyIntent.setType("Completed");

            replyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            replyIntent.setAction(Long.toString(triggerTime));



            PendingIntent replyPendingIntent =
                    PendingIntent.getBroadcast(context.getApplicationContext(),
                            3,
                            replyIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);


            NotificationCompat.Action waterChangeAction =
                    new NotificationCompat.Action.Builder(R.mipmap.ic_launcher,
                            "SET WATER CHANGE %", replyPendingIntent)
                            .addRemoteInput(remoteInput)
                            .build();
            //endregion



            //region SET FUTURE ALARM FOR USER DEFINED TASKS
            triggerTime += TimeUnit.DAYS.toMillis(7);

            intent.putExtra("KEY_TRIGGER_TIME",triggerTime);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, intentID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }

            MyDbHelper mydbhelper = MyDbHelper.newInstance(context, AID);
            SQLiteDatabase db = mydbhelper.getWritableDatabase();
            Cursor c = mydbhelper.getDataAN2Condition(db, "Category", AType, "AlarmName", AName);
            if (c.moveToFirst()) {
                banner = c.getString(2);
                c.close();
            }
            mydbhelper.updateTimeInMillis(db,Long.toString(triggerTime),storedTimeInMillis);
            //endregion


            //region SET TASK NOTIFICATIONS WITH INTENTS
            if (NotifyType.equals("Notification") || NotifyType.equals("Both")) {

                title = AName + " " + AType + " " + context.getResources().getString(R.string.Alert) + " " + AquariumName;
                NotificationCompat.Builder nb = new NotificationCompat.Builder(context, channel1ID);

                NotificationHelper notificationHelper = new NotificationHelper(context);

                nb.setContentTitle(title)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(banner))
                        .setContentText(context.getResources().getString(R.string.MoreInfo))
                        .setSmallIcon(R.drawable.plant3)
                        .setGroup(AType)
                        .setPriority(NotificationCompat.PRIORITY_MAX);


                if (NotifyType.equals("Both")) {

                    Intent in = new Intent(context, LogsActivity.class);
                    in.putExtra("AquariumID", AID);
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    in.setAction(Long.toString(triggerTime));
                    PendingIntent pi = PendingIntent.getActivity(context, intentID, in, 0);
                    nb.setContentIntent(pi);
                }


                if (NotifyType.equals("Notification")) {


                    Intent BroadcastIntentC = new Intent(context, NotificationReceiver.class);
                        BroadcastIntentC.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    BroadcastIntentC.putExtra("AquariumID", AID);
                    BroadcastIntentC.putExtra("AT", AType);
                    BroadcastIntentC.putExtra("AlarmName", AName);
                    BroadcastIntentC.putExtra("Status", context.getResources().getString(R.string.Completed));
                    BroadcastIntentC.putExtra("NotificationID", intentID);
                    BroadcastIntentC.setType("Completed");


                    PendingIntent actionIntentC = PendingIntent.getBroadcast(context, intentID, BroadcastIntentC, PendingIntent.FLAG_UPDATE_CURRENT);

                    Intent BroadcastIntentS = new Intent(context, NotificationReceiver.class);
                        BroadcastIntentS.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    BroadcastIntentS.putExtra("AquariumID", AID);
                    BroadcastIntentS.putExtra("AT", AType);
                    BroadcastIntentS.putExtra("AlarmName", AName);
                    BroadcastIntentS.putExtra("Status", context.getResources().getString(R.string.Skipped));
                    BroadcastIntentS.putExtra("NotificationID", intentID);
                    BroadcastIntentS.setType("Skipped");


                    PendingIntent actionIntentS = PendingIntent.getBroadcast(context, intentID, BroadcastIntentS, PendingIntent.FLAG_UPDATE_CURRENT);

                            nb
                            .addAction(R.mipmap.ic_launcher, context.getResources().getString(R.string.Skip), actionIntentS)
                            .setDeleteIntent(actionIntentS);

                    if(AName.equals("Weekly") && AType.equals("Water Change")){

                        nb.addAction(waterChangeAction);

                    }
                    else{

                        nb.addAction(R.mipmap.ic_launcher, context.getResources().getString(R.string.Complete), actionIntentC);
                    }
                }

                notificationHelper.getManager().notify("com.newage.plantedaqua",intentID, nb.build());

            }


            if (NotifyType.equals("Alarm") || NotifyType.equals("Both")) {

                Intent in = new Intent(context, MediaStopActivity.class);
                in.putExtra("AlarmName", AName);
                in.putExtra("AquariumID", AID);
                in.putExtra("AT", AType);
                in.putExtra("AquariumName", AquariumName);

                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(in);
            }

            //endregion

        }



    }

    private void setAlarmOn1stDay(Context context) {

        if(AType.equals("MONTHLY_EXPENSE") ) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(

                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    1,
                    9,
                    0,
                    0
            );

            calendar.add(Calendar.MONTH,1);





            defaultAlarms(calendar, 1, "MONTHLY_EXPENSE", context);
        }

        else if(AType.equals("EVERYDAY")) {

            //region SET ALARM FOR NEXT DAY
            Calendar calendar_everday = Calendar.getInstance();
            calendar_everday.set(
                    calendar_everday.get(Calendar.YEAR),
                    calendar_everday.get(Calendar.MONTH),
                    calendar_everday.get(Calendar.DAY_OF_MONTH),
                    10,
                    0,
                    0

            );
            calendar_everday.add(Calendar.DATE,1);






            defaultAlarms(calendar_everday, 2, "EVERYDAY", context);
            //endregion

            //region SET PREVIOUS DAY'S PPM FOR CURRENT DAY

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());

            Calendar previousDay = Calendar.getInstance();
            previousDay.add(Calendar.DAY_OF_YEAR, -1);


            String dateOnly = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new java.util.Date());
            String previousDate = simpleDateFormat.format(previousDay.getTimeInMillis());


            TankDBHelper tankDBHelper = TankDBHelper.newInstance(context);
            Cursor cursor = tankDBHelper.getData(tankDBHelper.getReadableDatabase());
            if(cursor!=null) {
                if (cursor.moveToFirst()) {

                   // Log.i("ALARM_INFO","inside tank db");

                    do {


                        MyDbHelper myDbHelper = MyDbHelper.newInstance(context, cursor.getString(1));


                      //  Log.i("ALARM_INFO","inside tank db " + cursor.getString(1));

                        Cursor cr = myDbHelper.getDataMaLDayWise(previousDate);
                        if(cr!=null) {
                            if (cr.moveToFirst() && cr.getCount()>0) {

                               // Log.i("ALARM_INFO","Previous day "+previousDate);


                                Cursor cur = myDbHelper.getDataMaLDayWise(dateOnly);
                                if (!cur.moveToFirst()) {
                                 //   Log.i("ALARM_INFO","Current Day "+dateOnly);
                                    myDbHelper.addDataMaL(dateOnly, cr.getString(1), cr.getString(2), cr.getString(3), cr.getString(4), cr.getString(5), cr.getString(6), "","","");
                                }
                                cur.close();

                            }

                            cr.close();
                        }

                        cr = myDbHelper.getDataMaL();
                        if(cr!=null) {

                            cr.moveToNext();

                            long rows = cr.getCount();
                            if (rows > 30) {

                                rows -= 30;

                                myDbHelper.deleteRowsMacroTable(rows);
                            }

                            cr.close();
                        }


                    } while (cursor.moveToNext());

                }

                cursor.close();
            }


            //endregion


        }

    }

    private void defaultAlarms(Calendar calendar,int reqCode, String alarmType,Context context){

        long alarmInMillis;

        alarmInMillis = calendar.getTimeInMillis();
        AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context.getApplicationContext(),RamizAlarm.class);
        intent.putExtra("AT",alarmType);


        PendingIntent pi = PendingIntent.getBroadcast(context, reqCode, intent, PendingIntent.FLAG_NO_CREATE);
        if(pi==null){
            //System.out.println("null");
            //System.out.println("Ramiz: No such pending intent exists "+c.getInt(3));
        }
        else{
            //System.out.println("not null");
            //System.out.println("Ramiz: Pending intent exists ");
            pi = PendingIntent.getBroadcast(context, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pi);

        }

        pi = PendingIntent.getBroadcast(context.getApplicationContext(), reqCode, intent, 0);
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmInMillis, pi);
        }
        else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmInMillis, pi);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmInMillis, pi);
        }

    }

    float expense = 0f;

    private String generateExpenseReport(final Context context) {

        ExpenseDBHelper expenseDBHelper = ExpenseDBHelper.getInstance(context.getApplicationContext());

        Calendar calendar = Calendar.getInstance();
        previousMonth = calendar.get(Calendar.MONTH);
        if (Calendar.MONTH == 0){
            previousMonth = 12;
        }

        expense = expenseDBHelper.getExpenseperMonth(previousMonth);


        return String.format(Locale.getDefault(),"%.2f",expense);

    }

    private void monthlyExpenseNotification(Context context, Intent intent) {


        String expense = generateExpenseReport(context);

        TinyDB settingsDB = new TinyDB(context.getApplicationContext());
        String defaultCurrency = settingsDB.getString("DefaultCurrencySymbol");


        if(!expense.isEmpty()) {

            NotificationCompat.Builder nb = new NotificationCompat.Builder(context, "channel1ID");

            NotificationHelper notificationHelper = new NotificationHelper(context);

            String banner = "You had "+defaultCurrency+ " " + expense + " spends last month";
            String title = context.getString(R.string.expense_report_generated);

            nb.setContentTitle(title)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(banner))
                    .setContentText(context.getResources().getString(R.string.MoreInfo))
                    .setSmallIcon(R.drawable.plant3)
                    .setGroup(expense)
                    .setPriority(NotificationCompat.PRIORITY_MAX);


            if(this.expense!=0d) {


                Intent in = new Intent(context, ExpenseActivity.class);
                in.putExtra("PREVIOUS_MONTH", previousMonth);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pi = PendingIntent.getActivity(context, 3, in, 0);
                nb.setContentIntent(pi);
            }

            notificationHelper.getManager().notify("com.newage.plantedaqua",getID(), nb.build());
        }

    }



        public static int getID() {

            return c.incrementAndGet();
        }


    private void  everydayNotification(Context context, Intent intent){


        //CO2 REQUIREMENT UPDATE
        updateCO2LevelInfo(context);

        //DAYS WITHOUT WATERCHANGE CALCULATION PER TANK
        TankDBHelper tankDBHelper = TankDBHelper.newInstance(context);
        Cursor c = tankDBHelper.getData(tankDBHelper.getReadableDatabase());
        if(c!=null) {
            if (c.moveToFirst()) {

                do {

                        checkDaysWithoutWaterChange(context,c.getString(1),c.getString(2));

                } while (c.moveToNext());
            }
            c.close();
        }






    }

    private void checkDaysWithoutWaterChange(Context context, String aquaID, String AquariumName) {



        SharedPreferences preferences = context.getSharedPreferences(aquaID,Context.MODE_PRIVATE);
        int waterChangeCounter = preferences.getInt("DAYS_WITHOUT_WATER_CHANGE",0);
        waterChangeCounter +=1;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("DAYS_WITHOUT_WATER_CHANGE",waterChangeCounter);
        editor.apply();

        if(waterChangeCounter>14){

            createEverydayNotification(context,String.format(Locale.getDefault(),"You have not performed a water change in your Tank %s since two weeks. This will lead to high DOCs in your tank causing adverse effects to your flora and fauna",AquariumName),"NO WATER CHANGE ALERT",AID,null,null);
        }


    }



    private void updateCO2LevelInfo(Context context){

        //tankDBHelper.addDataReco(1,ID,"","","CO2 level",recoString,"1");
        TankDBHelper tankDBHelper = TankDBHelper.newInstance(context);
        Cursor c = tankDBHelper.getDataReco(tankDBHelper.getReadableDatabase());
        String aquaID;
        if(c!=null) {
            if (c.moveToFirst()) {

                do {


                    if (c.getString(6).equals("1")) {

                        aquaID = c.getString(1);


                        tankDBHelper.updateDataVisibility("0");


                        NotificationCompat.Builder nb = new NotificationCompat.Builder(context, "channel1ID");

                        NotificationHelper notificationHelper = new NotificationHelper(context);

                        String banner = c.getString(5);
                        String title = c.getString(7) + " " + c.getString(4);

                        nb.setContentTitle(title)
                                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                                .setAutoCancel(true)
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(banner))
                                .setContentText(context.getResources().getString(R.string.MoreInfo))
                                .setSmallIcon(R.drawable.plant3)
                                .setGroup("everyday")
                                .setPriority(NotificationCompat.PRIORITY_MAX);

                        Intent in = new Intent(context, LightCalcActivity.class);
                        in.putExtra("AquariumID", aquaID);

                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent pi = PendingIntent.getActivity(context, getID(), in, 0);
                        nb.setContentIntent(pi);

                        notificationHelper.getManager().notify("com.newage.plantedaqua", getID(), nb.build());

                    }

                } while (c.moveToNext());
            }
            c.close();
        }


    }


    private void createEverydayNotification(Context context, String banner, String title, String aquaID, @Nullable Intent notificationIntent, String additionalData){


        NotificationCompat.Builder nb = new NotificationCompat.Builder(context, "channel1ID");

        NotificationHelper notificationHelper = new NotificationHelper(context);

        nb.setContentTitle(title)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(banner))
                .setContentText(context.getResources().getString(R.string.MoreInfo))
                .setSmallIcon(R.drawable.plant3)
                .setGroup("everyday")
                .setPriority(NotificationCompat.PRIORITY_MAX);


        if(notificationIntent!=null) {

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pi = PendingIntent.getActivity(context, getID(), notificationIntent, 0);
            nb.setContentIntent(pi);
        }

        notificationHelper.getManager().notify("com.newage.plantedaqua", getID(), nb.build());



    }

}
