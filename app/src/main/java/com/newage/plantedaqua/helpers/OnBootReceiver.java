package com.newage.plantedaqua.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;


import com.newage.plantedaqua.activities.RamizAlarm;

import java.util.Calendar;

public class OnBootReceiver extends BroadcastReceiver {
    MyDbHelper alarmdbhelper1;
    @Override
    public void onReceive(Context context, Intent intent) {

        //Toast.makeText(context,"WELCOME", Toast.LENGTH_SHORT).show();
        String AquariumName;
        PendingIntent pi;
        String storedTimeInMillis;
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {


            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            TankDBHelper tankDBHelper = TankDBHelper.newInstance(context);
            SQLiteDatabase db1 = tankDBHelper.getWritableDatabase();
            Cursor c1 = tankDBHelper.getData(db1);
            while (c1.moveToNext()) {

                AquariumName = c1.getString(2);
                alarmdbhelper1 = MyDbHelper.newInstance(context, c1.getString(1));
                SQLiteDatabase db2 = alarmdbhelper1.getWritableDatabase();

                Cursor c = alarmdbhelper1.getData(db2);


                while (c.moveToNext()) {
                    Calendar calendar = Calendar.getInstance();
                    storedTimeInMillis = c.getString(2);


                    calendar.set(

                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH),
                            c.getInt(5),
                            c.getInt(6),
                            0
                    );


                    calendar.set(Calendar.DAY_OF_WEEK, c.getInt(4));
                    if (calendar.getTimeInMillis() < System.currentTimeMillis()) {

                        //System.out.println(calendar.getTimeInMillis()+" Added 7 to it");
                        calendar.add(Calendar.DAY_OF_YEAR, 7);


                    }

                    alarmdbhelper1.updateTimeInMillis(db2, Long.toString(calendar.getTimeInMillis()), storedTimeInMillis);
                    // Log.i("Time in millis 1", Long.toString(calendar.getTimeInMillis()));


                    Intent inn = new Intent(context, RamizAlarm.class);
                    inn.putExtra("AT", c.getString(1));
                    inn.putExtra("AlarmName", c.getString(0));
                    inn.putExtra("NotifyType", c.getString(7));
                    inn.putExtra("AquariumID", c1.getString(1));
                    inn.putExtra("KEY_TRIGGER_TIME", calendar.getTimeInMillis());
                    //Log.i("Time_in_millis_1", Long.toString(calendar.getTimeInMillis()));
                    inn.putExtra("KEY_INTENT_ID", c.getInt(3));
                    inn.putExtra("AquariumName", AquariumName);

                    // Toast.makeText(context,"I am in bootreceiver", Toast.LENGTH_SHORT).show();
                    pi = PendingIntent.getBroadcast(context, c.getInt(3), inn, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (Build.VERSION.SDK_INT >= 23) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
                    } else if (Build.VERSION.SDK_INT >= 19) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
                    }
                }
                c.close();


            }
            c1.close();

        }

        setAlarmOn1stDay(context);



    }

    private void setAlarmOn1stDay(Context context) {



        Calendar calendar = Calendar.getInstance();
        calendar.set(

                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                1,
                9,
                0,
                0
        );

        Calendar calendar_everday = Calendar.getInstance();
        calendar_everday.set(
                calendar_everday.get(Calendar.YEAR),
                calendar_everday.get(Calendar.MONTH),
                calendar_everday.get(Calendar.DAY_OF_MONTH),
                10,
                0,
                0

        );

        if(calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.MONTH, 1);
        }

        if(calendar_everday.getTimeInMillis() < System.currentTimeMillis()) {
            calendar_everday.add(Calendar.DAY_OF_MONTH,1);
        }

        defaultAlarms(calendar,1,"MONTHLY_EXPENSE",context);
        defaultAlarms(calendar_everday,2,"EVERYDAY",context);




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
}
