package com.newage.plantedaqua.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "single_chat_table")
data class SingleChatObject (


    var chatUserID: String = "" ,
    var  chatDisplayName: String = "" ,
    var chatTime: String = "" ,
    var chatPhotoURL: String = "" ,
    var chatMsg: String = "" ,
    var chatUserType: String = "other_party" ,
    var ChatTag: String = "" ,
    var chatSeen : Boolean = false ,
    var chatTyping : Boolean= false ,

    @PrimaryKey
    var chatID: Long = 0)
