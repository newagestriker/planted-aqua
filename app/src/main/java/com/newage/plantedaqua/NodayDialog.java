package com.newage.plantedaqua;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;

public class NodayDialog extends AppCompatDialogFragment {
    String tag = null;

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        this.tag=tag;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View view= inflater.inflate(R.layout.layout_dialog,null);


        builder.setView(view)


                .setMessage(getTag())
                .setTitle(getResources().getString(R.string.Error))
                .setNeutralButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });

        return builder.create();

    }
}
