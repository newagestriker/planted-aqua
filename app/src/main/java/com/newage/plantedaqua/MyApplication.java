package com.newage.plantedaqua;


import android.app.Application;


import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.newage.plantedaqua.helpers.PlantedAquaNotificationOpenedHandler;
import com.newage.plantedaqua.helpers.PlantedAquaNotificationReceivedHandler;
import com.onesignal.OSNotification;
import com.onesignal.OneSignal;


import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

import io.fabric.sdk.android.Fabric;

public class MyApplication extends Application {

    private final static AtomicInteger c = new AtomicInteger(0);
    //private String SN;

    public static boolean SINGLE_CHAT_ACTIVITY_RUNNING = false;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        //CrashlyticsCore core = new CrashlyticsCore.Builder().build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());

        AudienceNetworkAds.initialize(this);

        PlantedAquaNotificationOpenedHandler plantedAquaNotificationOpenedHandler=new PlantedAquaNotificationOpenedHandler(getApplicationContext());

        OneSignal.startInit(getApplicationContext())
             //   .setNotificationReceivedHandler(new PlantedAquaNotificationReceivedHandler(getApplicationContext()))
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(false)
                .setNotificationOpenedHandler(plantedAquaNotificationOpenedHandler)
                .init();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
            OneSignal.sendTag("User_ID", FirebaseAuth.getInstance().getCurrentUser().getUid());

    }



    /*private void checkMsgInShoutBox(final Context context) {

        TinyDB tinyDB = new TinyDB(context);
        final long seqNum = TextUtils.isEmpty(tinyDB.getString("MSN"))?0L:Long.parseLong(tinyDB.getString("MSN"));

        DatabaseReference seqDatabase = FirebaseDatabase.getInstance().getReference("SN");
        seqDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                SN = TextUtils.isEmpty(dataSnapshot.getValue(String.class))?"0" : dataSnapshot.getValue(String.class);

                if (Long.parseLong(SN)<seqNum){


                    NotificationCompat.Builder nb = new NotificationCompat.Builder(context, "channel1ID");

                    NotificationHelper notificationHelper = new NotificationHelper(context);

                    String banner = "You have new messages in Shout box";
                    String title = "New Message";

                    nb.setContentTitle(title)
                            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(banner))
                            .setContentText(context.getResources().getString(R.string.MoreInfo))
                            .setSmallIcon(R.drawable.plant3)
                            .setGroup("everyday")
                            .setPriority(NotificationCompat.PRIORITY_MAX);

                    Intent in = new Intent(context, ChatBox.class);

                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pi = PendingIntent.getActivity(context, getID(), in, 0);
                    nb.setContentIntent(pi);

                    notificationHelper.getManager().notify("com.newage.plantedaqua", getID(), nb.build());


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static int getID() {

        return c.incrementAndGet();
    }*/
}
