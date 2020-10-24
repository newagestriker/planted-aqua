package com.newage.plantedaqua

import android.app.Application

import com.facebook.ads.AudienceNetworkAds
import com.google.firebase.auth.FirebaseAuth
import com.newage.plantedaqua.di.dbModules
import com.newage.plantedaqua.di.vModules
import com.newage.plantedaqua.helpers.PlantedAquaNotificationOpenedHandler
import com.newage.plantedaqua.helpers.TinyDB
import com.onesignal.OneSignal

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree


class PlantedAquaApplication : Application() {



    override fun onCreate() {
        super.onCreate()

        AudienceNetworkAds.initialize(this)

        Timber.plant(DebugTree())

        startKoin {
            androidLogger()
            androidContext(this@PlantedAquaApplication)
            modules(listOf(vModules, dbModules))
        }


        val plantedAquaNotificationOpenedHandler = PlantedAquaNotificationOpenedHandler(applicationContext)
        OneSignal.startInit(applicationContext) //   .setNotificationReceivedHandler(new PlantedAquaNotificationReceivedHandler(getApplicationContext()))
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(false)
                .setNotificationOpenedHandler(plantedAquaNotificationOpenedHandler)
                .init()
        if (FirebaseAuth.getInstance().currentUser != null) OneSignal.sendTag("User_ID", FirebaseAuth.getInstance().currentUser!!.uid)
        val userOptedForChatNotification: TinyDB? = TinyDB(this)
        val isChecked = userOptedForChatNotification?.getBoolean(Constants.userOptedForChatNotification)
        OneSignal.sendTag("Opted",(if (isChecked!!) "Y" else "N"))

    }


}