package com.newage.aquapets.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.newage.aquapets.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView logo = findViewById(R.id.SplashTitle);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.splash_animation);
        animation.setDuration(1000);
        logo.startAnimation(animation);

        Handler handler = new Handler();
        handler.postDelayed(

                new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(SplashActivity.this, A1Activity.class);
                        startActivity(intent);
                        finish();

                    }
                }

                , 1000);


    }
}
