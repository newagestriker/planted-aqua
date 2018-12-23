package com.newage.plantedaqua;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class PlantedAquaNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    private Context context;
    public PlantedAquaNotificationOpenedHandler(Context context) {
        this.context=context;
    }

    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String customKey;
        String msg="",photoUrl="",send_email="";

        if (data != null) {
          //  customKey = data.optString("customkey", null);
            try {
                msg = data.getString("msg");
                photoUrl=data.getString("PU");
                send_email=data.getString("send_email");

               // Log.i("ReceivedMessage", msg);
            }catch(Exception e){
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
           // if (customKey != null)
               // Log.i("OneSignalExample", "customkey set with value: " + customKey);
        }

       // if (actionType == OSNotificationAction.ActionType.ActionTaken)
           // Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

        // The following can be used to open an Activity of your choice.
        // Replace - getApplicationContext() - with any Android Context.

            Intent intent = new Intent(context, HandleConnectRequestActivity.class);
            intent.putExtra("Msg", msg);
            intent.putExtra("PU", photoUrl);
            intent.putExtra("send_email", send_email);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);


        // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
        //   if you are calling startActivity above.
     /*
        <application ...>
          <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
        </application>
     */
    }
}
