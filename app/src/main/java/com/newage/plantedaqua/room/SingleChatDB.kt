package com.newage.plantedaqua.room

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SingleChatDao {

    @Query("SELECT * FROM single_chat_table")
    fun getSingleChat() : List<SingleChatObject>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSingleChat(singleChatObject: SingleChatObject)

    @Query("SELECT * FROM single_chat_table WHERE chatID=:arg0")
    fun getSingleChat(arg0:Long): LiveData<List<SingleChatObject>>

    @Query("SELECT * FROM single_chat_table WHERE ChatTag=:arg0")
    fun getFilteredChat(arg0:String): List<SingleChatObject>

    @Query("SELECT * FROM chat_users_table")
    fun getChatUsers() : List<ChatUsers>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChatUser(chatUsers: ChatUsers)
}


@Database(entities = [SingleChatObject::class,ChatUsers::class],version = 1,exportSchema = false)
abstract class SingleChatDB : RoomDatabase() {

    abstract val singleChatDao: SingleChatDao



    companion object {


        @Volatile
        private var INSTANCE: SingleChatDB? = null


        fun getInstance(context: Context): SingleChatDB {

            var instance = INSTANCE
            synchronized(this) {


                if (instance == null) {


                    instance = Room.databaseBuilder(context.applicationContext,
                            SingleChatDB::class.java, "SingleChat_DB")
                            .fallbackToDestructiveMigration()
                            .build()

                    INSTANCE = instance
                }

            }

            return INSTANCE!!
        }


    }
}