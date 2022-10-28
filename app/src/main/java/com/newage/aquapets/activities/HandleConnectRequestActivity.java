package com.newage.aquapets.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.newage.aquapets.R;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class HandleConnectRequestActivity extends AppCompatActivity   {
    TextView DN;
    ImageView DP;
    String send_email="";
    EditText userResponseText;
    String msg="";
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setTitle(getResources().getString(R.string.Notifications));
        getSupportActionBar().setIcon(R.drawable.plantedaqua);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_connect_request);
        mAuth= FirebaseAuth.getInstance();
        DN=findViewById(R.id.DisplayName);
        DP=findViewById(R.id.DisplayPic);
        TextView connectBanner=findViewById(R.id.ConnectBanner);
        LinearLayout linearButtons=findViewById(R.id.LinearButtons);
        userResponseText=findViewById(R.id.UserResponse);
        Intent in=getIntent();
        send_email=in.getStringExtra("send_email");
        if(!send_email.equals("na")) {

            connectBanner.setVisibility(View.VISIBLE);
            linearButtons.setVisibility(View.VISIBLE);
            userResponseText.setVisibility(View.VISIBLE);

            Button okButton = findViewById(R.id.OkButton);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    msg = userResponseText.getText().toString();
                    Toast.makeText(HandleConnectRequestActivity.this, getResources().getString(R.string.MsgSent), Toast.LENGTH_SHORT).show();
                    sendRequest();

                }
            });
            Button declineButton = findViewById(R.id.DeclineButton);
            declineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    msg = getResources().getString(R.string.UserDeclined);
                    Toast.makeText(HandleConnectRequestActivity.this, getResources().getString(R.string.MsgSent), Toast.LENGTH_SHORT).show();
                    sendRequest();
                }
            });

            Button doNothing = findViewById(R.id.DoNothingButton);
            doNothing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        else{

            connectBanner.setVisibility(View.GONE);
            linearButtons.setVisibility(View.GONE);
            userResponseText.setVisibility(View.GONE);

        }
        if (!in.getStringExtra("PU").equals("na")) {
            DP.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(Uri.parse(in.getStringExtra("PU")))
                    .into(DP);
        }else{
            DP.setVisibility(View.GONE);
        }
        DN.setText(in.getStringExtra("Msg"));


    }
    String PU="";
   String new_send_email="";
    private void sendRequest(){
        String dn="";

        if(mAuth.getCurrentUser()!=null) {

            if(mAuth.getCurrentUser().getPhotoUrl()!=null)
            {
                PU=mAuth.getCurrentUser().getPhotoUrl().toString();
            }
            if(mAuth.getCurrentUser().getDisplayName()!=null)
            {
                dn=mAuth.getCurrentUser().getDisplayName();
            }
            if(mAuth.getCurrentUser().getEmail()!=null)
            {
                new_send_email=mAuth.getCurrentUser().getEmail();
            }
            final String msg="Message from "+dn+" : "+this.msg;


            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    int SDK_INT = android.os.Build.VERSION.SDK_INT;
                    if (SDK_INT > 8) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);


                        //This is a Simple Logic to Send Notification different Device Programmatically....

                        try {
                            String jsonResponse;
                            URL url = new URL("https://onesignal.com/api/v1/notifications");
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                            con.setUseCaches(false);
                            con.setDoOutput(true);
                            con.setDoInput(true);

                            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                            con.setRequestProperty("Authorization", "Basic ZjY1NWJjMmYtYzEyZS00ZTZjLWIwOTktYjA1OGM1YzZlYjNh");
                            con.setRequestMethod("POST");

                            String strJsonBody = "{"
                                    + "\"app_id\": \"75961582-4d0f-4c61-bd6e-7fd712577fbc\","

                                    + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                    + "\"data\": {\"msg\": \"" + msg + "\",\"PU\": \"" + PU + "\",\"send_email\":\""+new_send_email+"\"},"
                                    + "\"contents\": {\"en\": \"" + msg + "\"}"
                                    + "}";


                            // System.out.println("strJsonBody:\n" + strJsonBody);

                            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                            con.setFixedLengthStreamingMode(sendBytes.length);

                            OutputStream outputStream = con.getOutputStream();
                            outputStream.write(sendBytes);

                            int httpResponse = con.getResponseCode();
                            //System.out.println("httpResponse: " + httpResponse);

                            if (httpResponse >= HttpURLConnection.HTTP_OK
                                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                scanner.close();
                            } else {
                                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                scanner.close();
                            }
                            //System.out.println("jsonResponse:\n" + jsonResponse);

                        } catch (Throwable t) {
                            Toast.makeText(HandleConnectRequestActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            finish();
        }
        else
            Toast.makeText(this,getResources().getString(R.string.NotLoggedInMsg),Toast.LENGTH_SHORT).show();
    }


}
