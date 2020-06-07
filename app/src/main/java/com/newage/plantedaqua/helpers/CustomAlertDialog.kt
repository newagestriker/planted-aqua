package com.newage.plantedaqua.helpers

import android.app.AlertDialog
import android.content.Context
import android.view.View
import com.newage.plantedaqua.R

class CustomAlertDialog {

    fun showDialog(context: Context,view: View?,title:String,message:String,onClickedOK:()->Unit){

        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle(title)
                .setMessage(message)
                .setNegativeButton(context.resources.getString(R.string.Cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(context.resources.getString(R.string.ok)){ _,_ ->

                    onClickedOK()
                }
        view?.let{
            alertDialogBuilder.setView(it)
        }

        alertDialogBuilder.create().show()

    }
}