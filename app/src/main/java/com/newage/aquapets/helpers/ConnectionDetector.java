package com.newage.aquapets.helpers;

import android.content.Context;
import android.net.ConnectivityManager;



public class ConnectionDetector {

    private Context context;
    private String internetAddress;

    public ConnectionDetector(Context context, String internetAddress){

        this.internetAddress = internetAddress;
        this.context = context;
    }


    public boolean isInternetAvailable() {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }

        return false;
    }
}
