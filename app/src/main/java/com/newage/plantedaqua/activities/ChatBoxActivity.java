package com.newage.plantedaqua.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;

import android.text.TextUtils;

import android.view.View;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.newage.plantedaqua.adapters.ChatRecyclerAdapter;
import com.newage.plantedaqua.helpers.CloudNotificationHelper;
import com.newage.plantedaqua.models.ChatBoxObject;
import com.newage.plantedaqua.R;
import com.newage.plantedaqua.helpers.TinyDB;

import java.util.ArrayList;
import java.util.Calendar;



public class ChatBoxActivity extends AppCompatActivity {

    private ImageView sendImageView;
    private EditText messageTextView;
    private FirebaseAuth firebaseAuth;
    private String displayName="";
    private DatabaseReference chatDatabase;
    private String userID;
    private String sequenceNumber;
    private RecyclerView chatRecyclerView;
    private ArrayList<ChatBoxObject> chatBoxObjects;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TinyDB tinyDB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);

        getSupportActionBar().setTitle("Shout Box");


        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()==null){
            Toast.makeText(this,"Sorry!! You must be logged in to see all chats..",Toast.LENGTH_LONG).show();
            finish();
        }
        else {

            sendImageView = findViewById(R.id.SendChatMessage);
            messageTextView = findViewById(R.id.MessageText);
            chatRecyclerView = findViewById(R.id.ChatRecyclerView);
            final LinearLayout chatBannerLayout = findViewById(R.id.ChatBannerlayout);




            tinyDB = new TinyDB(getApplicationContext());

            final ImageView closeBanner = findViewById(R.id.CloseChatBanner);
            closeBanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tinyDB.putString("CLOSE_CHAT_BANNER", "Y");
                    chatBannerLayout.setVisibility(View.GONE);
                }
            });

            if (tinyDB.getString("CLOSE_CHAT_BANNER").equals("Y")) {
                chatBannerLayout.setVisibility(View.GONE);
            }

            chatBoxObjects = new ArrayList<>();


            userID = firebaseAuth.getUid();

            displayName = firebaseAuth.getCurrentUser().getDisplayName();

            chatDatabase = FirebaseDatabase.getInstance().getReference("CH");

            final String PU = firebaseAuth.getCurrentUser().getPhotoUrl().toString();

            sendImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String send_msg;
                    if (!TextUtils.isEmpty(messageTextView.getText().toString())) {
                        send_msg = messageTextView.getText().toString();
                        sendMessage(send_msg, PU);
                    }

                }
            });

            adapter = new ChatRecyclerAdapter(chatBoxObjects, ChatBoxActivity.this);
            layoutManager = new LinearLayoutManager(ChatBoxActivity.this);
            chatRecyclerView.setLayoutManager(layoutManager);
            chatRecyclerView.setAdapter(adapter);
            layoutManager.scrollToPosition(chatBoxObjects.size() - 1);

            messageTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    layoutManager.scrollToPosition(chatBoxObjects.size() - 1);

                }
            });


            getAllChats();
        }
    }


    private void getAllChats(){

        /*Cursor cursor = chatDBHelper.getData(chatDBHelper.getReadableDatabase());
        //ChatDB(id integer primary key,SN integer,DN text,MSG text,PU text,TS text)");

        long lastSNinDB=1L;
        if(cursor.moveToFirst()){
            do{

                chatBoxObject = new ChatBoxObject();
                chatBoxObject.setSN(Long.toString(cursor.getLong(1)));
                chatBoxObject.setDN(cursor.getString(2));
                chatBoxObject.setMSG(cursor.getString(3));
                chatBoxObject.setPU(cursor.getString(4));
                chatBoxObject.setTS(cursor.getString(5));
                chatBoxObject.setUID(cursor.getString(6));


                chatBoxObjects.add(chatBoxObject);
                adapter.notifyItemInserted(chatBoxObjects.size()-1);
                lastSNinDB = cursor.getLong(1);


            }while (cursor.moveToNext());


        }
        cursor.close();

        FirebaseDatabase.getInstance().getReference("MSN").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              finalSN = TextUtils.isEmpty(dataSnapshot.getValue(String.class))?1L:Long.parseLong(dataSnapshot.getValue(String.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        while (lastSNinDB<finalSN){

            getEachChatFromDatabase(lastSNinDB++);
        }*/


        chatDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    chatBoxObjects.add(snapshot.getValue(ChatBoxObject.class));
                    adapter.notifyItemInserted(chatBoxObjects.size()-1);
                    layoutManager.scrollToPosition(chatBoxObjects.size() - 1);
//                    Log.i("Adapter_size",Integer.toString(chatBoxObjects.size()));

                }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    /*private void getEachChatFromDatabase(long seqNum){

        chatDatabase.child(Long.toString(seqNum)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chatBoxObjects.add(dataSnapshot.getValue(ChatBoxObject.class));
                adapter.notifyItemInserted(chatBoxObjects.size()-1);
                Log.i("Adapter_size",Integer.toString(chatBoxObjects.size()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }*/

    ChatBoxObject chatBoxObject;

    private void sendMessage(final String send_msg, final String PU){



        final Calendar calendar = Calendar.getInstance();


        messageTextView .setText("");

        final String timeStamp = Long.toString(calendar.getTimeInMillis());


        chatBoxObject = new ChatBoxObject();
        chatBoxObject.setDN(displayName);
        chatBoxObject.setMSG(send_msg);
        chatBoxObject.setPU(PU);
        chatBoxObject.setTS(timeStamp);
        chatBoxObject.setUID(userID);

        chatBoxObjects.add(chatBoxObject);
        adapter.notifyItemInserted(chatBoxObjects.size()-1);
        layoutManager.scrollToPosition(chatBoxObjects.size()-1);

       // notifyAllUsers(PU,send_msg);



        FirebaseDatabase.getInstance().getReference("MSN").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                long sn;

                sequenceNumber = dataSnapshot.getValue(String.class);

                if(TextUtils.isEmpty(sequenceNumber)){
                    sn=1L;
                }
                else if (Long.parseLong(sequenceNumber)>1000L){

                    long deleteSn = Long.parseLong(sequenceNumber)-1000L;



                    FirebaseDatabase.getInstance().getReference("CH").child(Long.toString(deleteSn)).removeValue();

                    sn = Long.parseLong(sequenceNumber)+1;


                }
                else{

                    sn = Long.parseLong(sequenceNumber)+1;

                }




                FirebaseDatabase.getInstance().getReference("MSN").setValue(Long.toString(sn));

                notifyAllUsers(PU,send_msg);


/*
                chatDatabase.child(userID+"_"+timeStamp).child("SN").setValue(Long.toString(sn));
                chatDatabase.child(userID+"_"+timeStamp).child("DN").setValue(displayName);
                chatDatabase.child(userID+"_"+timeStamp).child("MSG").setValue(send_msg);
                chatDatabase.child(userID+"_"+timeStamp).child("PU").setValue(PU);

                chatDatabase.child(userID+"_"+timeStamp).child("TS").setValue(timeStamp);*/

                chatBoxObject.setSN(Long.toString(sn));


                chatDatabase.child(Long.toString(sn)).setValue(chatBoxObject);








            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });







    }

    private void notifyAllUsers(final String PU, final String send_msg){

        CloudNotificationHelper cloudNotificationHelper = new CloudNotificationHelper(this);
        cloudNotificationHelper.sendCloudNotification(send_msg,PU, CloudNotificationHelper.MsgType.allUsers,firebaseAuth.getCurrentUser().getUid(),"All",displayName+" says",displayName,"all_users");





    }
}
