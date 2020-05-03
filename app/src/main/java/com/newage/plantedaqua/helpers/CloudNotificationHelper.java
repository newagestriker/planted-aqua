package com.newage.plantedaqua.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CloudNotificationHelper {

    public enum MsgType{
        oneToOne,
        allUsers

    }

   private Context context;
    public CloudNotificationHelper(Context context){

        this.context = context;

    }

    public void sendCloudNotification(String msg, String photoUrl, MsgType msgType, @Nullable String from_user,@Nullable String to_user, String heading,@Nullable String displayName,String msgCat){

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

                        String strJsonBody = "";
                        switch (msgType) {

                            case oneToOne:

                            strJsonBody = "{"
                                    + "\"app_id\": \"75961582-4d0f-4c61-bd6e-7fd712577fbc\","

                                    + "\"headings\": {\"en\": \"" + heading +"\"},"

                                    + "\"large_icon\": \"" + photoUrl + "\","

                                    + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + to_user + "\"}],"

                                    + "\"data\": {\"msg\": \"" + msg + "\",\"PU\": \"" + photoUrl + "\",\"msg_cat\": \"" + msgCat + "\",\"DN\": \"" + displayName + "\",\"UID\": \"" + from_user + "\",\"to_user\":\"" + to_user + "\"},"
                                    + "\"contents\": {\"en\": \"" + msg + "\"}"
                                    + "}";

                            break;

                            case allUsers:

                                strJsonBody = "{"
                                        + "\"app_id\": \"75961582-4d0f-4c61-bd6e-7fd712577fbc\","

                                        + "\"included_segments\": \"Subscribed Users\","  //Subscribed Users  Myself

                                        + "\"headings\": {\"en\": \"" + heading +"\"},"

                                        + "\"large_icon\": \"" + photoUrl + "\","

                                        + "\"data\": {\"msg\": \"" + msg + "\",\"PU\": \"" + photoUrl + "\",\"msg_cat\": \""+msgCat+"\",\"DN\": \"" + displayName + "\",\"UID\": \"" + from_user + "\",\"to_user\":\""+to_user +"\"},"
                                        + "\"contents\": {\"en\": \"" + msg + "\"}"
                                        + "}";
                                break;



                        }

                        Log.i("JSON_RESPONSE",strJsonBody);


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
                        con.disconnect();

                    } catch (Throwable t) {
                        Toast.makeText(context,t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}
