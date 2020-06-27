package com.newage.plantedaqua.activities;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.newage.plantedaqua.adapters.RecyclerAdapterLogs;
import com.newage.plantedaqua.helpers.CustomAlertDialog;
import com.newage.plantedaqua.dbhelpers.MyDbHelper;
import com.newage.plantedaqua.models.LogData;
import com.newage.plantedaqua.R;

import java.util.ArrayList;


public class LogsActivity extends AppCompatActivity {

    private MyDbHelper myDbHelper;
    private String aquariumID="";
    private ArrayList<LogData> logData=new ArrayList <>();
    private RecyclerView.Adapter adapter;
    private  String aquaName;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logs_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        CustomAlertDialog customAlertDialog = new CustomAlertDialog();

        //noinspection SimplifiableIfStatement
        if (id == R.id.ClearLog) {

            if(!logData.isEmpty()) {
                customAlertDialog.showDialog(this,null,"Delete All Logs from "+aquaName,"Deleted Logs cannot be recovered",()->{
                    logData.clear();
                    myDbHelper.deleteDataLogs();
                    adapter.notifyDataSetChanged();
                    return null;
                });


            }

            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        CoordinatorLayout mainLayout = findViewById(R.id.LogsMainLayout);
        Intent intent=getIntent();
        aquariumID=intent.getStringExtra("AquariumID");
        aquaName=intent.getStringExtra("AquariumName");
        getSupportActionBar().setTitle(getResources().getString(R.string.Logs)+" : "+aquaName);
        myDbHelper=MyDbHelper.newInstance(this,aquariumID);
        LogData LD;

        Cursor c=myDbHelper.getDataLogs();
        while (c.moveToNext()){
            LD=new LogData();
            LD.setDy(c.getString(0));
            LD.setDt(c.getString(1));
            LD.setTask(c.getString(2));
            LD.setCategory(c.getString(3));
            LD.setStatus(c.getString(4));
            LD.setAlarmInfo(c.getString(5));
            logData.add(LD);

        }
        c.close();


        CustomAlertDialog customAlertDialog = new CustomAlertDialog();

        RecyclerView recyclerView = findViewById(R.id.Log_RecyclerView);
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new RecyclerAdapterLogs(logData, this, new RecyclerAdapterLogs.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                String task = logData.get(position).getTask();
                View infoView = getLayoutInflater().inflate(R.layout.alarm_info_layout,null);
                TextView infoText = infoView.findViewById(R.id.AlarmDetailsText);
                String dt = logData.get(position).getDt();
                if (view.getTag().equals(1)) {
                    String status = logData.get(position).getStatus();
                    if (status.equals(getResources().getString(R.string.Completed))) {
                        logData.get(position).setStatus(getResources().getString(R.string.Skipped));
                        adapter.notifyDataSetChanged();
                        myDbHelper.updateItemLogsUsingDate(dt,"Log_Status", getResources().getString(R.string.Skipped));
                        myDbHelper.updateStatusMaL(getResources().getString(R.string.Skipped),dt);
                        Snackbar.make(mainLayout,"Task " + task + " is "+getResources().getString(R.string.Skipped),Snackbar.LENGTH_SHORT).show();

                    } else {
                        logData.get(position).setStatus(getResources().getString(R.string.Completed));
                        adapter.notifyDataSetChanged();
                        myDbHelper.updateItemLogsUsingDate(dt, "Log_Status", getResources().getString(R.string.Completed));
                        myDbHelper.updateStatusMaL(getResources().getString(R.string.Completed),dt);
                        Snackbar.make(mainLayout,"Task " + task + " is "+getResources().getString(R.string.Completed),Snackbar.LENGTH_SHORT).show();
                    }
                }

            else if(view.getTag().equals(2))

            {

                customAlertDialog.showDialog(LogsActivity.this,null,"Delete Log", "Deleted Logs cannot be recovered.", ()->{

                    myDbHelper.deleteItemLogsUsingDate(dt);
                    myDbHelper.deleteItemMaL(dt);
                    logData.remove(position);
                    adapter.notifyItemRemoved(position);

                   // Toast.makeText(LogsActivity.this, "Task " + task + " is deleted from Logs", Toast.LENGTH_SHORT).show();
                    Snackbar.make(mainLayout,"Task " + task + " is deleted from Logs",Snackbar.LENGTH_SHORT).show();
                    return null;
                });

            }
            else{

                    infoText.setText(logData.get(position).getAlarmInfo());

                    Dialog dialog = new Dialog(LogsActivity.this);
                    dialog.setContentView(infoView);
                    dialog.show();
                    /*AlertDialog.Builder builder = new AlertDialog.Builder(Logs.this);
                    builder
                            .setView(infoView)


                            .create().show();*/



                }
        }
        });
        recyclerView.setAdapter(adapter);


    }
}
