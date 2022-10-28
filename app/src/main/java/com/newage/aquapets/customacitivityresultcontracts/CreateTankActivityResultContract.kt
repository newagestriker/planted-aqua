package com.newage.aquapets.customacitivityresultcontracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import androidx.activity.result.contract.ActivityResultContract
import com.newage.aquapets.activities.CreateTankActivity
import com.newage.aquapets.models.TankDetails

class CreateTankActivityResultContract : ActivityResultContract<Int, TankDetails?>(){
    override fun createIntent(context: Context, input: Int): Intent =
        Intent(context, CreateTankActivity::class.java)
        .putExtra("mode", "creation")


    override fun parseResult(resultCode: Int, intent: Intent?): TankDetails? {
        if (resultCode == Activity.RESULT_OK) {
            return intent!!.getParcelableExtra("TankItemsObject")
        }
        return null
    }
}