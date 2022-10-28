package com.newage.aquapets

import android.app.Application

import com.facebook.ads.AudienceNetworkAds
import com.google.firebase.auth.FirebaseAuth
import com.newage.aquapets.di.dbModules
import com.newage.aquapets.di.servicesModules
import com.newage.aquapets.di.vModules
import com.newage.aquapets.helpers.TinyDB
import com.newage.aquapets.models.Admin
import com.newage.aquapets.models.Hobbyist
import com.newage.aquapets.models.Seller
import com.newage.aquapets.models.UserTypes
import com.onesignal.OneSignal

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree


class AquaPetsApplication : Application() {


    override fun onCreate() {
        super.onCreate()

        AudienceNetworkAds.initialize(this)

        Timber.plant(DebugTree())

        startKoin {
            androidLogger()
            androidContext(this@AquaPetsApplication)
            modules(listOf(vModules, dbModules, servicesModules))
        }

        if (FirebaseAuth.getInstance().currentUser != null) OneSignal.sendTag(
            "User_ID",
            FirebaseAuth.getInstance().currentUser!!.uid
        )
        val userOptedForChatNotification: TinyDB? = TinyDB(this)
        val isChecked =
            userOptedForChatNotification?.getBoolean(Constants.userOptedForChatNotification)
        OneSignal.sendTag("Opted", (if (isChecked!!) "Y" else "N"))

        //ADD USER TYPES

        UserTypes.getInstance().addUserTypes(arrayOf(Admin.type, Hobbyist.type, Seller.type))

    }


}