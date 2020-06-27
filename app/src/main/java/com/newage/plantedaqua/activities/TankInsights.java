package com.newage.plantedaqua.activities;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;

import android.text.format.DateUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newage.plantedaqua.adapters.RecyclerAdapterLogs;
import com.newage.plantedaqua.dbhelpers.MyDbHelper;
import com.newage.plantedaqua.dbhelpers.TankDBHelper;
import com.newage.plantedaqua.models.LogData;
import com.newage.plantedaqua.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class TankInsights extends AppCompatActivity {

    private TankDBHelper tankDBHelper;
    private TextView lightZone,macroDosageTextView,microDosageTextView;
    LinearLayout linearUpcoming, linearPending, linearReco;

    private String aquariumID;

    private RecyclerAdapterLogs adapter2;
    private ArrayList<LogData> logData2=new ArrayList<>();

    private RecyclerView recyclerView;

    private ArrayList<LogData> logData1 = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tank_insights);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        Intent intent = getIntent();
        aquariumID = intent.getStringExtra("AquariumID");


        tankDBHelper = TankDBHelper.newInstance(this);

        Cursor c = tankDBHelper.getDataCondition("AquariumID", aquariumID);

        String aquaName = "";

        if(c!=null) {

            if (c.moveToFirst()) {

                aquaName = TextUtils.isEmpty(c.getString(2)) ? "" : c.getString(2);


            }

            c.close();
        }



        getSupportActionBar().setTitle("Tank Insights"+" : "+aquaName);

        lightZone = findViewById(R.id.LightZone);
        macroDosageTextView = findViewById(R.id.DosageMacro);
        microDosageTextView = findViewById(R.id.DosageMicro);
        linearPending = findViewById(R.id.LinearPending);
        linearUpcoming = findViewById(R.id.LinearUpcoming);
        linearReco = findViewById(R.id.LinearReco);





        calcLightRegion();
        addDosageText();
        upcomingTasks();
        pendingTasks();
        setReco();

    }

    private void calcLightRegion() {

        Cursor c = tankDBHelper.getDataCondition("AquariumID", aquariumID);

        if(c!=null) {

            if (c.moveToFirst()) {

                String lightZoneFromDB = TextUtils.isEmpty(c.getString(16)) ? "" : c.getString(16);

                lightZone.setText(lightZoneFromDB);
                if (lightZoneFromDB.equals("Insufficient Data"))
                    lightZone.setTextColor(getResources().getColor(R.color.grey_500));

            }

            c.close();
        }




    }

    private void addDosageText(){

        Cursor c = tankDBHelper.getDataCondition("AquariumID",aquariumID);
        if(c.moveToFirst()) {

            if(!TextUtils.isEmpty(c.getString(20))) {
                microDosageTextView.setText(c.getString(20));
                microDosageTextView.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            else
                microDosageTextView.setText("No Data. Please set recommended dosage data from Micro Dosage Calculator.");
            if(!TextUtils.isEmpty(c.getString(21))) {
                macroDosageTextView.setText(c.getString(21));
                macroDosageTextView.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            else
                macroDosageTextView.setText("No Data. Please set recommended dosage data from Macro Dosage Calculator.");
        }
        c.close();




    }

    private void upcomingTasks(){



        final MyDbHelper myDbHelper = MyDbHelper.newInstance(this,aquariumID);
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor c = myDbHelper.getData(db);
        if(c.moveToFirst()){
            do{

                collectAlarmData(c.getString(0),c.getString(1),Long.parseLong(c.getString(2)),Integer.parseInt(c.getString(4)),Integer.parseInt(c.getString(5)),Integer.parseInt(c.getString(6)));
            }while (c.moveToNext());
        }
        c.close();



        Collections.sort(logData1, new Comparator<LogData>() {
            @Override
            public int compare(LogData lhs, LogData rhs) {

                    return Long.toString(lhs.getTimeInMillis()).compareTo(Long.toString(rhs.getTimeInMillis()));

            }
        });

        TextView upcomingTaskTextView = findViewById(R.id.UpcomingTasks);
        RecyclerView recyclerView=findViewById(R.id.Upcoming_RecyclerView);

        if(logData1.isEmpty()){
            upcomingTaskTextView.setText("There are no upcoming tasks");
            linearUpcoming.setVisibility(View.GONE);
        }
        else {
            linearUpcoming.setVisibility(View.VISIBLE);
            upcomingTaskTextView.setText("Upcoming Tasks");
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            RecyclerAdapterLogs adapter1 = new RecyclerAdapterLogs(logData1, this, new RecyclerAdapterLogs.OnItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                }
            });
            recyclerView.setAdapter(adapter1);
        }

    }

    private void collectAlarmData(String alarmName,String alarmCategory,long alarmTimeInMillis,int dayNumber, int hr, int min){


        LogData logData = new LogData();

        String[] strDays = new String[]{
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday"
        };


        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
        String dateString = formatter.format(new Date(alarmTimeInMillis));


        Calendar calendar = Calendar.getInstance();
        long currentTimeInMillis = calendar.getTimeInMillis();
        if (currentTimeInMillis<alarmTimeInMillis){

            logData.setDy(setRelativeDays(alarmTimeInMillis,strDays[dayNumber-1]));

            logData.setCategory(alarmCategory);
            logData.setTask(alarmName);
            logData.setDt(dateString+" "+timeFormat(hr,min));
            logData.setTimeInMillis(alarmTimeInMillis);
            logData1.add(logData);
        }

    }

    private String setRelativeDays(long timeInMillis, String defaultValue){


        if(DateUtils.isToday(timeInMillis)){
            return ("TODAY");
        }
        else if(isTomorrow(timeInMillis)){
            return ("TOMORROW");
        }
        else if(isYesterday(timeInMillis)) {
            return ("YESTERDAY");
        }

        return defaultValue;

    }

    private String timeFormat(int hour,int min){
        String format,strHr,strMin;
        if(hour==0){
            hour+=12;
            format="AM";
        }else if(hour==12){
            format="PM";
        }else if(hour>12){
            hour-=12;
            format="PM";
        }
        else{
            format="AM";
        }
        if(hour<10){
            strHr="0"+Integer.toString(hour);
        }
        else {
            strHr = Integer.toString(hour);
        }
        if (min<10){
            strMin="0"+Integer.toString(min);
        }
        else {
            strMin = Integer.toString(min);
        }

        return (strHr+":"+strMin+" "+format);

    }

    private void pendingTasks() {

        final MyDbHelper myDbHelper = MyDbHelper.newInstance(this, aquariumID);
        final TextView pendingTasksTextView = findViewById(R.id.PendingTasks);

        Cursor c = myDbHelper.getDataLogsCondition("Log_Status", "Skipped");
        if (c.moveToFirst()) {
            do {
                LogData logData = new LogData();
                logData.setCategory(c.getString(3));
                logData.setTask(c.getString(2));
                logData.setDt(c.getString(1));
                if(TextUtils.isEmpty(c.getString(6))){

                    logData.setDy(c.getString(0));

                }else {

                    logData.setDy(setRelativeDays(Long.parseLong(c.getString(6)), c.getString(0)));
                }
                logData.setStatus(c.getString(4));
                logData2.add(logData);

            } while (c.moveToNext());
        }
        c.close();

           recyclerView = findViewById(R.id.Pending_RecyclerView);

        if (logData2.isEmpty()) {
            pendingTasksTextView.setText("There are no pending tasks");
            linearPending.setVisibility(View.GONE);
        } else {
            linearPending.setVisibility(View.VISIBLE);
            pendingTasksTextView.setText("Pending Tasks");

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            adapter2 = new RecyclerAdapterLogs(logData2, this, new RecyclerAdapterLogs.OnItemClickListener() {
                @Override
                public void onClick(View view, int position) {

                    String task = logData2.get(position).getTask();
                    String category = logData2.get(position).getCategory();
                    String dt = logData2.get(position).getDt();

                    if (view.getTag().equals(1)) {


                        myDbHelper.updateItemLogsUsingDate(dt,"Log_Status", getResources().getString(R.string.Completed));;
                        myDbHelper.updateStatusMaL(getResources().getString(R.string.Completed),dt);
                        logData2.remove(position);
                        adapter2.notifyItemRemoved(position);
                        if (logData2.isEmpty()) {
                            pendingTasksTextView.setText("There are no pending tasks");
                            recyclerView.setVisibility(View.GONE);
                            linearPending.setVisibility(View.GONE);
                        } else {
                            pendingTasksTextView.setText("Pending tasks");
                            recyclerView.setVisibility(View.VISIBLE);
                            linearPending.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(TankInsights.this, "Task " + task + " is completed..", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        myDbHelper.deleteItemLogsUsingDate(dt);
                        myDbHelper.deleteItemMaL(dt);
                        logData2.remove(position);
                        adapter2.notifyItemRemoved(position);
                        if (logData2.isEmpty()) {
                            pendingTasksTextView.setText("There are no pending tasks");
                            recyclerView.setVisibility(View.GONE);
                            linearPending.setVisibility(View.GONE);

                        } else {
                            pendingTasksTextView.setText("Pending tasks");
                            recyclerView.setVisibility(View.VISIBLE);
                            linearPending.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(TankInsights.this, "Task " + task + " is deleted from Logs", Toast.LENGTH_SHORT).show();
                }


                }
            });
            recyclerView.setAdapter(adapter2);

        }
    }


    private void setReco(){

        ArrayList<LogData> logDatas = new ArrayList<>();
        LogData logData;

        //RecoDetails(id integer,AquariumID text,Day text,Date text,Title text,Message text,Visibility text)"

        TankDBHelper tankDBHelper = TankDBHelper.newInstance(this);
        Cursor c = tankDBHelper.getDataRecoCondition(tankDBHelper.getReadableDatabase(),aquariumID);
        if(c!=null) {
            if (c.moveToFirst()) {
                do {
                    if (!TextUtils.isEmpty(c.getString(5))) {
                        logData = new LogData();
                        logData.setDy(c.getString(4));
                        logData.setDt(c.getString(3));
                        logData.setTask(c.getString(5));
                        logData.setStatus(null);
                        logDatas.add(logData);
                    }
                } while (c.moveToNext());
            }
            c.close();
        }


        TextView recoTaskTextView = findViewById(R.id.Recommendations);
        LinearLayout linearReco = findViewById(R.id.LinearReco);
        RecyclerView recyclerView=findViewById(R.id.Reco_RecyclerView);

        if(logDatas.isEmpty()){
            recoTaskTextView.setText(getResources().getString(R.string.no_reco));
            linearReco.setVisibility(View.GONE);
        }
        else {
            linearReco.setVisibility(View.VISIBLE);
            recoTaskTextView.setText(getResources().getString(R.string.reco));
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            RecyclerAdapterLogs adapter = new RecyclerAdapterLogs(logDatas, this, new RecyclerAdapterLogs.OnItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                /*String status=logData1.get(position).getStatus();
                if(status.equals(getResources().getString(R.string.Completed))){
                    logData1.get(position).setStatus(getResources().getString(R.string.Skipped));
                    adapter1.notifyDataSetChanged();
                    String task=logData1.get(position).getTask();
                    String category=logData1.get(position).getCategory();
                    myDbHelper.updateItemLogs("Log_Task",task,"Log_Category",category,"Log_Status",getResources().getString(R.string.Skipped));

                }
                else{
                    logData1.get(position).setStatus(getResources().getString(R.string.Completed));
                    adapter1.notifyDataSetChanged();
                    String task=logData1.get(position).getTask();
                    String category=logData1.get(position).getCategory();
                    myDbHelper.updateItemLogs("Log_Task",task,"Log_Category",category,"Log_Status",getResources().getString(R.string.Completed));
                }*/
                }
            });
            recyclerView.setAdapter(adapter);
        }


    }

    private boolean isYesterday(long d) {
        return DateUtils.isToday(d + DateUtils.DAY_IN_MILLIS);
    }

    private boolean isTomorrow(long d) {
        return DateUtils.isToday(d - DateUtils.DAY_IN_MILLIS);
    }



}
