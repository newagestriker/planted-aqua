package com.newage.plantedaqua;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import java.util.ArrayList;


public class Logs extends AppCompatActivity {

    MyDbHelper myDbHelper;
    String aquariumID="";
    ArrayList<LogData> logData=new ArrayList <>();
    RecyclerView.Adapter adapter;

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.ClearLog) {

            if(!logData.isEmpty()) {
                logData.clear();
                myDbHelper.deleteDataLogs();
                adapter.notifyDataSetChanged();
            }

            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        Intent intent=getIntent();
        aquariumID=intent.getStringExtra("AquariumID");
        String aquaName=intent.getStringExtra("AquariumName");
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
            logData.add(LD);

        }
        c.close();
        RecyclerView recyclerView=findViewById(R.id.Log_RecyclerView);
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new RecyclerAdapterLogs(logData, this, new RecyclerAdapterLogs.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                String status=logData.get(position).getStatus();
                if(status.equals(getResources().getString(R.string.Completed))){
                    logData.get(position).setStatus(getResources().getString(R.string.Skipped));
                    adapter.notifyDataSetChanged();
                    String task=logData.get(position).getTask();
                    String category=logData.get(position).getCategory();
                    myDbHelper.updateItemLogs("Log_Task",task,"Log_Category",category,"Log_Status",getResources().getString(R.string.Skipped));

                }
                else{
                    logData.get(position).setStatus(getResources().getString(R.string.Completed));
                    adapter.notifyDataSetChanged();
                    String task=logData.get(position).getTask();
                    String category=logData.get(position).getCategory();
                    myDbHelper.updateItemLogs("Log_Task",task,"Log_Category",category,"Log_Status",getResources().getString(R.string.Completed));
                }
            }
        });
        recyclerView.setAdapter(adapter);


    }
}
