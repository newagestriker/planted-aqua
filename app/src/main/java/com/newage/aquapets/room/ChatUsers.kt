package com.newage.aquapets.room

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "chat_users_table")
data class ChatUsers (

        var chatUserType : String ="",
        var chatUserTag : String = "",
        var chatUserLastMessageTime : String = "",
        var chatUserPhotoURL : String = "",
        var chatUserName : String = "",

        @PrimaryKey
        var chatUserID: String = ""
)