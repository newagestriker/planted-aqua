package com.newage.plantedaqua;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MediaStopActivity extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    String  AName="",AID="",AType="",AquariumName="",timeStamp="",day="";
    String STATUS;
    MyDbHelper mydbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_media_stop);
        STATUS=getResources().getString(R.string.Skipped);
        Intent inn=getIntent();
        Bundle bundle=inn.getExtras();
        mediaPlayer=MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
        if (!mediaPlayer.isPlaying())
        {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }

        if (bundle!=null){
            AName=bundle.getString("AlarmName");
            AID=bundle.getString("AquariumID");
            AType=bundle.getString("AT");
            AquariumName=bundle.getString("AquariumName");
        }



        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        Button complete = findViewById(R.id.Complete);
        Button skip = findViewById(R.id.Skip);


        TextView alarminfo=findViewById(R.id.alarmtext);
        TextView alarmtextdetails=findViewById(R.id.alarmdetails);
        mydbhelper= MyDbHelper.newInstance(this,AID);
        SQLiteDatabase db=mydbhelper.getWritableDatabase();
        Cursor c= mydbhelper.getDataAN2Condition(db,"Category",AType,"AlarmName",AName);
        if (c.moveToFirst()) {
            alarmtextdetails.setText(c.getString(2));
            c.close();
        }



        String banner=AName+" "+AType+" "+getResources().getString(R.string.Alert)+" "+AquariumName;


        alarminfo.setText(banner);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    STATUS=getResources().getString(R.string.Completed);


                }
                finish();


            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    STATUS=getResources().getString(R.string.Skipped);


                }
                finish();


            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        day = new SimpleDateFormat("EE", Locale.getDefault()).format(new java.util.Date());
        timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new java.util.Date());
        mydbhelper.addDataLogs(day,timeStamp,AName,AType,STATUS);

    }
}
