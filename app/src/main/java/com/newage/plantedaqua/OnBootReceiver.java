package com.newage.plantedaqua;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import java.util.Calendar;

public class OnBootReceiver extends BroadcastReceiver {
    MyDbHelper alarmdbhelper1;
    @Override
    public void onReceive(Context context, Intent intent) {

        //Toast.makeText(context,"WELCOME", Toast.LENGTH_SHORT).show();
        String AquariumName="";
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){


            AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            TankDBHelper tankDBHelper=TankDBHelper.newInstance(context);
            SQLiteDatabase db1=tankDBHelper.getWritableDatabase();
            Cursor c1=tankDBHelper.getData(db1);
            while(c1.moveToNext()) {

                AquariumName=c1.getString(2);
                alarmdbhelper1 = MyDbHelper.newInstance(context, c1.getString(1));
                SQLiteDatabase db2 = alarmdbhelper1.getWritableDatabase();

                Cursor c = alarmdbhelper1.getData(db2);


                while (c.moveToNext()) {
                    Calendar calendar = Calendar.getInstance();

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


                    Intent inn = new Intent(context, RamizAlarm.class);
                    inn.putExtra("AT", c.getString(1));
                    inn.putExtra("AlarmName",c.getString(0));
                    inn.putExtra("NotifyType",c.getString(7));
                    inn.putExtra("AquariumID",c1.getString(1));
                    inn.putExtra("KEY_TRIGGER_TIME",calendar.getTimeInMillis());
                    inn.putExtra("KEY_INTENT_ID",c.getInt(3));
                    inn.putExtra("AquariumName",AquariumName);

                    // Toast.makeText(context,"I am in bootreceiver", Toast.LENGTH_SHORT).show();
                    PendingIntent pi = PendingIntent.getBroadcast(context, c.getInt(3), inn, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (Build.VERSION.SDK_INT >= 23) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
                    }
                    else if (Build.VERSION.SDK_INT >= 19) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
                    }
                }
                c.close();



            }
            c1.close();


        }
    }
}
