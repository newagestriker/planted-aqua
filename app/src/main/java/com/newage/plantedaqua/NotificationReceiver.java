package com.newage.plantedaqua;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import java.text.SimpleDateFormat;
import java.util.Locale;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new java.util.Date());
        String day = new SimpleDateFormat("EE", Locale.getDefault()).format(new java.util.Date());
        /*BroadcastIntentS.putExtra("AquariumID",AID);
        BroadcastIntentS.putExtra("AT",AType);
        BroadcastIntentS.putExtra("AlarmName",AName);
        BroadcastIntentS.putExtra("Status","Skipped");*/

        String AquariumID=intent.getStringExtra("AquariumID");
        String AName=intent.getStringExtra("AlarmName");
        String AType=intent.getStringExtra("AT");
        String STATUS=intent.getStringExtra("Status");
        int NotificationID=intent.getIntExtra("NotificationID",0);
        NotificationManager notificationManager=(NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationID);

        /*System.out.println(AquariumID);
        System.out.println(AName);
        System.out.println(AType);
        System.out.println(STATUS);
        System.out.println(NotificationID);*/


        MyDbHelper mydbhelper= MyDbHelper.newInstance(context,AquariumID);
        //Toast.makeText(context,AquariumID,Toast.LENGTH_SHORT).show();
        mydbhelper.addDataLogs(day,timeStamp,AName,AType,STATUS);
        /*Intent i=new Intent(context,Logs.class);
        if((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M))
           i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("AquariumID", AquariumID);
        context.startActivity(i);*/

    }
}
