package com.newage.aquapets.helpers;

import android.content.Context;
import android.content.Intent;

import android.widget.Toast;


import com.newage.aquapets.activities.ChatBoxActivity;
import com.newage.aquapets.activities.SellerActivity;
import com.newage.aquapets.activities.UsersGalleryActivity;
import com.newage.aquapets.activities.SingleChatActivity;
import com.onesignal.OSNotificationAction;
//import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;


public class PlantedAquaNotificationOpenedHandler  {
   private Context context;
    public PlantedAquaNotificationOpenedHandler(Context context) {
        this.context=context;
    }

  /*  @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String customKey;
        String msg="",photoUrl="",to_user="",DN="",UID="",msgCat="";

        if (data != null) {
          //  customKey = data.optString("customkey", null);
            try {
                msg = data.getString("msg");
                photoUrl=data.getString("PU");
                to_user =data.getString("to_user");
                DN = data.getString("DN");
                UID = data.getString("UID");
                msgCat = data.getString("msg_cat");

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


        if(msgCat.equals("all_users")) {

            Intent intent = new Intent(context, ChatBoxActivity.class);
            intent.putExtra("Msg", msg);
            intent.putExtra("PU", photoUrl);
            intent.putExtra("to_user", "All");
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);


        }

        else if(msgCat.equals("seller_update")){
            Intent intent = new Intent(context, SellerActivity.class);
            intent.putExtra("User", UID);
            intent.putExtra("DN", DN);
            intent.putExtra("PU", photoUrl);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        }

        else if(msgCat.equals("Gallery")){

            Intent intent = new Intent(context, UsersGalleryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        }
        else if(msgCat.equals("one_to_one")) {

//            if(SINGLE_CHAT_ACTIVITY_RUNNING){
//
//                addToSingleChatDatabase(msg,photoUrl,UID,DN);
//
//
//            }else {


                Intent intent = new Intent(context, SingleChatActivity.class);
                intent.putExtra("carrier","Notification");
                intent.putExtra("Msg", msg);
                intent.putExtra("PU", photoUrl);
                intent.putExtra("from_user", UID);
                intent.putExtra("DN", DN);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                context.startActivity(intent);
   //         }
        }


        // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
        //   if you are calling startActivity above.
     /*
        <application ...>
          <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
        </application>
     */
 //   }

//    private void addToSingleChatDatabase(String msg,String photoUrl,String UID, String DN){
//
//        Calendar calendar = Calendar.getInstance();
//
//        String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
//
//        SingleChatObject singleChatObject =new  SingleChatObject();
//        singleChatObject.setChatMsg(msg);
//        singleChatObject.setChatPhotoURL(photoUrl);
//        DN = (TextUtils.isEmpty(DN))? "Unknown User" : DN;
//        singleChatObject.setChatDisplayName(DN);
//        singleChatObject.setChatID(calendar.getTimeInMillis());
//        singleChatObject.setChatTime(timeStamp);
//        singleChatObject.setChatUserID(UID);
//
//        SingleChatActivity.instance.getSingleChatObjects().add(singleChatObject);
//        SingleChatActivity.instance.getSingleChatAdapter().notifyItemInserted(SingleChatActivity.instance.getSingleChatObjects().size()-1);
//        SingleChatActivity.instance.layoutManager.scrollToPosition(SingleChatActivity.instance.getSingleChatObjects().size() - 1);
//
//
//
//
//    }

}
