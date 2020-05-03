package com.newage.plantedaqua.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.newage.plantedaqua.R
import com.newage.plantedaqua.adapters.ChatUserRecyclerAdapter
import com.newage.plantedaqua.room.ChatUsers
import com.newage.plantedaqua.room.SingleChatDB
import com.newage.plantedaqua.room.SingleChatObject
import kotlinx.coroutines.*

class ChatUsersActivity : AppCompatActivity() {

    private lateinit var chatUserRecyclerAdapter : ChatUserRecyclerAdapter
    private lateinit var chatRecyclerView : RecyclerView
    private lateinit var singleChatDB : SingleChatDB
    private var chatUserss = ArrayList<ChatUsers>()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_users_acitivity)
        supportActionBar!!.title = getString(R.string.connections)

        if (firebaseAuth.currentUser == null) {
            Toast.makeText(this, "Sorry!! You must be logged in to see all chats..", Toast.LENGTH_LONG).show()
            finish()
        }
        else {

            singleChatDB = SingleChatDB.getInstance(this)





            val job = Job()
            CoroutineScope(Dispatchers.IO + job).launch {
                chatUserss = singleChatDB.singleChatDao.getChatUsers() as ArrayList<ChatUsers>
                withContext(Dispatchers.Main) {
                    chatRecyclerView = findViewById(R.id.chat_user_recycler_view)
                    chatUserRecyclerAdapter = ChatUserRecyclerAdapter(chatUserss, this@ChatUsersActivity)
                    chatRecyclerView.adapter = chatUserRecyclerAdapter
                    chatRecyclerView.layoutManager = LinearLayoutManager(this@ChatUsersActivity)

                    loadChatsFromFirebase()
                }

            }



        }

    }

    private fun loadChatsFromFirebase(){


        val firebaseDatabase = FirebaseDatabase.getInstance()


        val  userID = firebaseAuth.currentUser!!.uid
        val chatRef = firebaseDatabase.getReference("SC")
        chatRef.child(userID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var singleChatObject : SingleChatObject
                val chatUsers = ChatUsers()
                for (snapshot in dataSnapshot.children) {

                    singleChatObject = SingleChatObject()
                    singleChatObject = snapshot.getValue(SingleChatObject::class.java)!!
                    singleChatObject.ChatTag = singleChatObject.chatUserID // CHANGING CHAT TAG TO SENDER USER ID


                    chatUsers.chatUserPhotoURL = singleChatObject.chatPhotoURL
                    chatUsers.chatUserName = singleChatObject.chatDisplayName
                    chatUsers.chatUserLastMessageTime = singleChatObject.chatTime
                    chatUsers.chatUserID = singleChatObject.chatUserID



                    GlobalScope.launch(Dispatchers.IO ) {
                        // singleChatDB!!.singleChatDao.insertChatUser(chatUsers)

                        singleChatDB.singleChatDao.insertSingleChat(singleChatObject)
                        singleChatDB.singleChatDao.insertChatUser(chatUsers)


                    }

                }

                chatRef.child(userID).removeValue()
            }

            override fun onCancelled(databaseError: DatabaseError) {}


        })

    }
}
