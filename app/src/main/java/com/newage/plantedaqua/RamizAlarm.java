package com.newage.plantedaqua;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.util.concurrent.TimeUnit;


public class RamizAlarm extends BroadcastReceiver {

    static final String GROUP_KEY="PLANTED_AQUA";
    @Override
    public void onReceive(Context context, Intent intent) {

       // WakeLocker wakeLocker=new WakeLocker();
      //  wakeLocker.acquire(context);
       // Toast.makeText(context,"media player started",Toast.LENGTH_SHORT).show();

        Bundle extras=intent.getExtras();
        int intentID=0;
        String banner="",title="",AType="",AName="",AID="",NotifyType="",AquariumName="";
        Long triggerTime=0L;
        if(extras!=null) {
            AType = extras.getString("AT");
            AName=extras.getString("AlarmName");
            AID=extras.getString("AquariumID");
            NotifyType=extras.getString("NotifyType");
            triggerTime=extras.getLong("KEY_TRIGGER_TIME");
            intentID=extras.getInt("KEY_INTENT_ID");
            AquariumName=extras.getString("AquariumName");
        }


        //System.out.println("Alarm received!!");
        //System.out.println("Aquaid : "+AID);

        triggerTime+= TimeUnit.DAYS.toMillis(7);
        AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);


        // Toast.makeText(this,Integer.toString(j), Toast.LENGTH_SHORT).show();

        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,intentID,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }

        MyDbHelper mydbhelper= MyDbHelper.newInstance(context,AID);
        SQLiteDatabase db=mydbhelper.getWritableDatabase();
        Cursor c= mydbhelper.getDataAN2Condition(db,"Category",AType,"AlarmName",AName);
        if (c.moveToFirst()) {
            banner = c.getString(2);
            c.close();
        }


        if(NotifyType.equals("Notification")||NotifyType.equals("Both")) {

            title=AName+" "+AType+" "+context.getResources().getString(R.string.Alert)+" "+AquariumName;
            NotificationCompat.Builder nb = new NotificationCompat.Builder(context, "channel1ID");

            NotificationHelper notificationHelper = new NotificationHelper(context);
            /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationManager notificationManager = notificationHelper.getManager();
                if (notificationManager.getNotificationChannels().size() < 2) {

                    NotificationCompat.Builder summaryNotification = new NotificationCompat.Builder(context, "channel1ID");
                    summaryNotification.setGroup(AType)
                            .setGroupSummary(true)
                            .setContentTitle(AType)
                            .setAutoCancel(true)
                            .setContentText(context.getResources().getString(R.string.MoreInfo))
                            .setSmallIcon(R.drawable.plantedaqua);

                    notificationHelper.getManager().notify(0, summaryNotification.build());

                }
            }*/


                nb.setContentTitle(title)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(banner))
                        .setContentText(context.getResources().getString(R.string.MoreInfo))
                        .setSmallIcon(R.drawable.plant3)
                        .setGroup(AType)
                        .setPriority(NotificationCompat.PRIORITY_MAX);


            if(NotifyType.equals("Both")) {

                Intent in = new Intent(context, OptionsActivity.class);
                in.putExtra("AquariumID", AID);
                //in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M))
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                in.setAction(AID);
                PendingIntent pi = PendingIntent.getActivity(context, intentID, in, 0);
                nb.setContentIntent(pi);
            }


            if(NotifyType.equals("Notification")) {



                Intent BroadcastIntentC = new Intent(context, NotificationReceiver.class);
                if((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M))
                    BroadcastIntentC.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                BroadcastIntentC.putExtra("AquariumID", AID);
                BroadcastIntentC.putExtra("AT", AType);
                BroadcastIntentC.putExtra("AlarmName", AName);
                BroadcastIntentC.putExtra("Status",  context.getResources().getString(R.string.Completed));
                BroadcastIntentC.putExtra("NotificationID", intentID);
                BroadcastIntentC.setType("Completed");


                PendingIntent actionIntentC = PendingIntent.getBroadcast(context, intentID, BroadcastIntentC, PendingIntent.FLAG_UPDATE_CURRENT);

                Intent BroadcastIntentS = new Intent(context, NotificationReceiver.class);
                if((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M))
                    BroadcastIntentS.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                BroadcastIntentS.putExtra("AquariumID", AID);
                BroadcastIntentS.putExtra("AT", AType);
                BroadcastIntentS.putExtra("AlarmName", AName);
                BroadcastIntentS.putExtra("Status", context.getResources().getString(R.string.Skipped));
                BroadcastIntentS.putExtra("NotificationID", intentID);
                BroadcastIntentS.setType("Skipped");


                PendingIntent actionIntentS = PendingIntent.getBroadcast(context, intentID, BroadcastIntentS, PendingIntent.FLAG_UPDATE_CURRENT);

                nb.addAction(R.mipmap.ic_launcher,context.getResources().getString(R.string.Complete),actionIntentC)
                        .addAction(R.mipmap.ic_launcher,context.getResources().getString(R.string.Skip),actionIntentS)
                        .setDeleteIntent(actionIntentS);
            }

            notificationHelper.getManager().notify(intentID, nb.build());

        }
        if(NotifyType.equals("Alarm")||NotifyType.equals("Both")) {

            Intent in = new Intent(context, MediaStopActivity.class);
            in.putExtra("AlarmName", AName);
            in.putExtra("AquariumID", AID);
            in.putExtra("AT", AType);
            in.putExtra("AquariumName",AquariumName);
            if((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M))
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(in);
        }

    }
}
