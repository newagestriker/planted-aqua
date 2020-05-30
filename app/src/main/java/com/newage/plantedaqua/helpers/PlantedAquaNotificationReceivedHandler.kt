package com.newage.plantedaqua.helpers

import android.content.Context
import android.text.TextUtils

import android.widget.Toast
import com.newage.plantedaqua.activities.SingleChatActivity
import com.newage.plantedaqua.room.ChatUsers
import com.newage.plantedaqua.room.SingleChatDB
import com.newage.plantedaqua.room.SingleChatObject
import com.onesignal.OSNotification
import com.onesignal.OneSignal

import java.text.SimpleDateFormat
import java.util.*


class PlantedAquaNotificationReceivedHandler(var context: Context) : OneSignal.NotificationReceivedHandler {


    override fun notificationReceived(notification: OSNotification) {
        val data = notification.payload.additionalData

        var msg: String? = ""
        var photoUrl: String? = ""
        var to_user: String? = ""
        var DN: String? = ""
        var UID: String? = ""
        var msgCat: String? = ""

      //  Log.i("TEST_ACTIVITY", "RECEIVED")
        if (data != null) {

            //  customKey = data.optString("customkey", null);
            try {
                msg = data.getString("msg")
                photoUrl = data.getString("PU")
                to_user = data.getString("to_user")
                DN = data.getString("DN")
                UID = data.getString("UID")
                msgCat = data.getString("msg_cat")
                // Log.i("ReceivedMessage", msg);
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }

        }

        if (msgCat == "one_to_one") {


                addToSingleChatDatabase(msg!!, photoUrl!!, UID!!, DN!!)

        }
    }

        private fun addToSingleChatDatabase(msg: String, photoUrl: String, UID: String, displayName: String) {

            //region CREATING CHAT OBJECTS AND UPDATING VIEWS
            var DN: String? = displayName
        val calendar = Calendar.getInstance()
        val timeStamp = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        val singleChatObject = SingleChatObject()
        singleChatObject.chatMsg = msg
        singleChatObject.chatPhotoURL = photoUrl
        DN = if (TextUtils.isEmpty(DN)) "Unknown User" else DN
        singleChatObject.chatDisplayName = DN!!
        singleChatObject.chatID = calendar.timeInMillis
        singleChatObject.chatTime = timeStamp
        singleChatObject.chatUserID = UID
            singleChatObject.ChatTag = UID

        SingleChatActivity.instance.singleChatObjects!!.add(singleChatObject)

        SingleChatActivity.instance.singleChatAdapter?.notifyItemInserted(SingleChatActivity.instance.singleChatObjects!!.size - 1)
            SingleChatActivity.instance.layoutManager.scrollToPosition(SingleChatActivity.instance.singleChatObjects!!.size - 1)
            //endregion

            //region CREATING CHAT USER OBJECTS
            val chatUsers = ChatUsers()
            chatUsers.chatUserID = UID
            chatUsers.chatUserLastMessageTime = timeStamp
            chatUsers.chatUserPhotoURL = photoUrl
            chatUsers.chatUserName = displayName
            //endregion

           //region INSERTING CHATS AND USERS IN DATABASE
            val singleChatDB = SingleChatDB.getInstance(context)



            //endregion
    }


}