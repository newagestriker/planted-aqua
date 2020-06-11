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
import timber.log.Timber;

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

        Timber.plant(new Timber.DebugTree());



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



}
