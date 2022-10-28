package com.newage.aquapets.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.newage.aquapets.MyApplication.SINGLE_CHAT_ACTIVITY_RUNNING
import com.newage.aquapets.R
import com.newage.aquapets.adapters.SingleChatRecyclerAdapter
import com.newage.aquapets.helpers.CloudNotificationHelper
import com.newage.aquapets.room.ChatUsers
import com.newage.aquapets.room.SingleChatDB
import com.newage.aquapets.room.SingleChatDB.Companion.getInstance
import com.newage.aquapets.room.SingleChatObject
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class SingleChatActivity : AppCompatActivity() {


    companion object{

        lateinit var instance : SingleChatActivity



    }

    init {
        instance = this
    }

    var firebaseDatabase = FirebaseDatabase.getInstance()
    var chatRef = firebaseDatabase.getReference("SC")
    var singleChatObjects: ArrayList<SingleChatObject>? = null
    var singleChatAdapter: RecyclerView.Adapter<*>? = null
    lateinit var layoutManager: RecyclerView.LayoutManager

    private lateinit var sendImageView: ImageView
    private lateinit var messageTextView: EditText
    private var firebaseAuth: FirebaseAuth? = null
    private var displayName: String? = ""
    private var userID: String? = null
    private lateinit var chatRecyclerView: RecyclerView


    private var other_party: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        SINGLE_CHAT_ACTIVITY_RUNNING = true
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_chat)
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth!!.currentUser == null) {
            Toast.makeText(this, "Sorry!! You must be logged in to see all chats..", Toast.LENGTH_LONG).show()
            finish()
        } else {

            supportActionBar!!.title = getString(R.string.messages)
            sendImageView = findViewById(R.id.SendSingleChatMessage)
            messageTextView = findViewById(R.id.SingleMessageText)
            chatRecyclerView = findViewById(R.id.SingleChatRecyclerView)

            singleChatObjects = ArrayList()

            loadChatDB() // LOAD ALL CHATS FROM DATABASE



            userID = firebaseAuth!!.uid
            displayName = firebaseAuth!!.currentUser!!.displayName
            val PU = if (TextUtils.isEmpty(firebaseAuth!!.currentUser!!.photoUrl.toString())) "" else firebaseAuth!!.currentUser!!.photoUrl.toString()
            sendImageView.setOnClickListener(View.OnClickListener {
                val send_msg: String
                if (!TextUtils.isEmpty(messageTextView.text.toString())) {
                    send_msg = messageTextView.text.toString()
                    sendMessage(send_msg, PU)
                }
            })


        }
    }

    private fun downloadChatsFromFirebase() {

        chatRef.child(userID!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var singleChatObject : SingleChatObject
                val chatUsers : ChatUsers = ChatUsers()
                for (snapshot in dataSnapshot.children) {

                    singleChatObject = SingleChatObject()
                    singleChatObject = snapshot.getValue(SingleChatObject::class.java)!!
                    singleChatObject.ChatTag = singleChatObject.chatUserID // CHANGING CHAT TAG TO SENDER USER ID
                    singleChatObjects!!.add(singleChatObject)
                    singleChatAdapter!!.notifyItemInserted(singleChatObjects!!.size-1)
                    layoutManager.scrollToPosition(singleChatObjects!!.size-1)

                    chatUsers.chatUserPhotoURL = singleChatObject.chatPhotoURL
                    chatUsers.chatUserName = singleChatObject.chatDisplayName
                    chatUsers.chatUserLastMessageTime = singleChatObject.chatTime
                    chatUsers.chatUserID = singleChatObject.chatUserID


                    GlobalScope.launch(Dispatchers.IO ) {
                       // singleChatDB!!.singleChatDao.insertChatUser(chatUsers)

                        singleChatDB!!.singleChatDao.insertSingleChat(singleChatObject)
                        singleChatDB!!.singleChatDao.insertChatUser(chatUsers)
                    }

                }

                chatRef.child(userID!!).removeValue()
            }

            override fun onCancelled(databaseError: DatabaseError) {}


        })

    }



    var singleChatDB: SingleChatDB? = null
    private fun loadChatDB() {

        other_party = intent.getStringExtra("from_user")
        singleChatDB = getInstance(this)


        val job = SupervisorJob()

        CoroutineScope(job + Dispatchers.IO).launch {


            singleChatObjects = singleChatDB!!.singleChatDao.getFilteredChat(other_party!!) as ArrayList<SingleChatObject>

            downloadChatsFromFirebase()

            initialSetup()

        }




    }


    private fun initialSetup() {

        singleChatAdapter = SingleChatRecyclerAdapter(singleChatObjects, this@SingleChatActivity,userID)
        layoutManager = LinearLayoutManager(this@SingleChatActivity)
        chatRecyclerView.layoutManager = (layoutManager)
        chatRecyclerView.adapter = (singleChatAdapter)
        layoutManager.scrollToPosition(singleChatObjects!!.size - 1)
        messageTextView.setOnClickListener(View.OnClickListener { layoutManager.scrollToPosition(singleChatObjects!!.size - 1) })
    }


    var singleChatObject: SingleChatObject? = null

    private fun sendMessage(send_msg: String, PU: String) {

        val calendar = Calendar.getInstance()
        messageTextView.setText("")
        val timeStamp = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        singleChatObject = SingleChatObject()
        singleChatObject!!.chatDisplayName = displayName!!
        singleChatObject!!.chatMsg = send_msg
        singleChatObject!!.chatPhotoURL = PU
        singleChatObject!!.chatUserID = userID!!
        singleChatObject!!.chatUserType = "myself"
        singleChatObject!!.chatID = calendar.timeInMillis
        singleChatObject!!.chatTime = timeStamp
        singleChatObject!!.ChatTag = other_party!!

        singleChatObjects!!.add(singleChatObject!!)
        singleChatAdapter!!.notifyItemInserted(singleChatObjects!!.size - 1)


        layoutManager.scrollToPosition(singleChatObjects!!.size - 1)


        val job = SupervisorJob()
        CoroutineScope(job + Dispatchers.IO).launch {

            singleChatDB!!.singleChatDao.insertSingleChat(singleChatObject!!)
        }

        chatRef.child(other_party!!).child(singleChatObject!!.chatID.toString()).setValue(singleChatObject)
        val myDN = if (TextUtils.isEmpty(firebaseAuth!!.currentUser!!.displayName)) "Unknown user" else firebaseAuth!!.currentUser!!.displayName!!
        val cloudNotificationHelper = CloudNotificationHelper(this)
        cloudNotificationHelper.sendCloudNotification(send_msg, PU, CloudNotificationHelper.MsgType.oneToOne, userID, other_party, "You have a message from $myDN", myDN, "one_to_one")
        // notifyAllUsers(PU,send_msg);
    }


    override fun onStop() {
        
        SINGLE_CHAT_ACTIVITY_RUNNING = false
        super.onStop()
    }
}


