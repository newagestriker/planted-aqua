package com.newage.aquapets;


import android.app.Application;


import com.facebook.ads.AudienceNetworkAds;

import com.google.firebase.auth.FirebaseAuth;
import com.newage.aquapets.helpers.PlantedAquaNotificationOpenedHandler;

import com.onesignal.OneSignal;




import timber.log.Timber;

public class MyApplication extends Application {



    public static boolean SINGLE_CHAT_ACTIVITY_RUNNING = false;

    @Override
    public void onCreate() {
        super.onCreate();

        AudienceNetworkAds.initialize(this);

        Timber.plant(new Timber.DebugTree());



    /*    PlantedAquaNotificationOpenedHandler plantedAquaNotificationOpenedHandler=new PlantedAquaNotificationOpenedHandler(getApplicationContext());

        OneSignal.startInit(getApplicationContext())
             //   .setNotificationReceivedHandler(new PlantedAquaNotificationReceivedHandler(getApplicationContext()))
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(false)
                .setNotificationOpenedHandler(plantedAquaNotificationOpenedHandler)
                .init();*/

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
            OneSignal.sendTag("User_ID", FirebaseAuth.getInstance().getCurrentUser().getUid());

    }



}
