package com.newage.plantedaqua.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;

import android.content.Intent;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.newage.plantedaqua.dbhelpers.MyDbHelper;
import com.newage.plantedaqua.R;
import com.newage.plantedaqua.dbhelpers.TankDBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class SetAlarmActivity extends AppCompatActivity {



   int j;
    String strHr,strMin;
   int hr;
   int mn;
   String format;
   int hour;
   int min;
   Bundle bundle;
   TextView timeText;
    MyDbHelper alarmdbhelper;
    String allDays="";
    String alarmTime="";

    String aquariumID;
    String  alarmName;
    LinearLayout linearLayout;
    String selectedItem="Notification";
    Spinner spinnerSetNotify;
    int Slot=0;
    int position;
    int frequency;
    String noInfoText;


    public static String AT="null";
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setalarm, menu);
        Cursor c= alarmdbhelper.getData2Condition("Alarm_Name",alarmName,"Alarm_Type",AT);
        if(c!=null && c.moveToNext()) {
            selectedItem = c.getString(7);
            c.close();
        }
        HashMap<String,Integer> notified=new HashMap<>();
        notified.put("Notification",0);
        notified.put("Alarm",1);
        notified.put("Both",2);
        spinnerSetNotify=(Spinner)menu.findItem(R.id.actionSpinner).getActionView();
        String[] notify=getResources().getStringArray(R.array.NotifyType);

        ArrayAdapter<String> adapter= new ArrayAdapter <>(this,android.R.layout.simple_spinner_item,notify);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSetNotify.setAdapter(adapter);
        spinnerSetNotify.setSelection(notified.get(selectedItem),true);
        selectedItem=spinnerSetNotify.getSelectedItem().toString();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_alarm);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        noInfoText = getResources().getString(R.string.NoInfo);
        Intent inn=getIntent();
        bundle=inn.getExtras();
        if (bundle!=null) {
            AT = bundle.getString("AlarmType");
            aquariumID=bundle.getString("AquariumID");
            alarmName = bundle.getString("AlarmName");
            position=bundle.getInt("Position",-20);
        }



        final EditText alarmtext=findViewById(R.id.alarmText);
        alarmtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmtext.setCursorVisible(true);

            }
        });


        // GET ALARM TEXT TO BE DISPLAYED AT THE TIME OF ALARM

        linearLayout=findViewById(R.id.mainLayout);

        alarmdbhelper = MyDbHelper.newInstance(this,aquariumID);
        SQLiteDatabase dbAN=alarmdbhelper.getWritableDatabase();
        Cursor cAN=alarmdbhelper.getDataAN2Condition(dbAN,"AlarmName",alarmName,"Category",AT);
        if(cAN!=null&&cAN.moveToFirst()) {
            alarmtext.setText(cAN.getString(2));
            Slot=cAN.getInt(3);
            cAN.close();
        }

        if (Slot==0){


            MyDbHelper mydbhelper;
            SQLiteDatabase db;
            Cursor c;
            boolean equal=true;

            TankDBHelper tankDBHelper=TankDBHelper.newInstance(this);
            SQLiteDatabase TDb=tankDBHelper.getWritableDatabase();
            Cursor cT=tankDBHelper.getData(TDb);
            Slot=3;
            ArrayList<Integer> tableSlots=new ArrayList <>();
            while (cT.moveToNext()) {
                mydbhelper = MyDbHelper.newInstance(this, cT.getString(1));
                db = mydbhelper.getWritableDatabase();
                c = mydbhelper.getDataAN(db);
                while (c.moveToNext()) {
                    tableSlots.add(c.getInt(3));

                }
                c.close();
            }

            cT.close();
            while(equal && tableSlots.size()>0){
                for(int i=0;i<tableSlots.size();i++){
                    //System.out.println("Table Slots : "+tableSlots.get(i)+" Check Slot : "+ Slot);
                    if(tableSlots.get(i)==Slot){
                        // System.out.println("Equality found so break!!");
                        Slot++;
                        equal=true;
                        break;
                    }
                    equal=false;
                }
            }


        }

        //System.out.println("Slot : "+Slot);




        //SET TEXT BANNER OF DOSING ALARM WINDOW
        final String DosingAlarmText=AT + " : " + alarmName;
        TextView nutrientdosingregime=findViewById(R.id.BannerText);
        nutrientdosingregime.setText(DosingAlarmText);



        //CREATE AN ARRAY OF CHECKBOXES
        ArrayList<CheckBox>DaysofWeek=new ArrayList<>();
        DaysofWeek.add((CheckBox)findViewById(R.id.Sun));
        DaysofWeek.add((CheckBox)findViewById(R.id.Mon));
        DaysofWeek.add((CheckBox)findViewById(R.id.Tue));
        DaysofWeek.add((CheckBox)findViewById(R.id.Wed));
        DaysofWeek.add((CheckBox)findViewById(R.id.Thu));
        DaysofWeek.add((CheckBox)findViewById(R.id.Fri));
        DaysofWeek.add((CheckBox)findViewById(R.id.Sat));

        //SET THE CHECKBOXES IF TABLE EXISTS


       Cursor c= alarmdbhelper.getData2Condition("Alarm_Name",alarmName,"Alarm_Type",AT);
        int loc;
        if(c != null && c.moveToFirst()) {
            do{
                loc = c.getInt(4) - 1;
                //System.out.println("Ramiz: initialize check box : "+ loc);
                DaysofWeek.get(loc).setChecked(true);

            }while (c.moveToNext());
        }



        timeText = findViewById(R.id.timeText);
         if(c != null && c.moveToFirst()) {

            /*System.out.println("Ramiz: Hour : " + c.getInt(5));
            System.out.println("Ramiz: Minute : " + c.getInt(6));*/

            timeformat(c.getInt(5), c.getInt(6));
            hr=c.getInt(5);
            mn=c.getInt(6);

            timeText.setText(strHr +":"+strMin+" "+format);

        }else
        {

            Calendar currenttime= Calendar.getInstance();
            hour=currenttime.get(Calendar.HOUR_OF_DAY);
            hr=hour;
            min=currenttime.get(Calendar.MINUTE);
            mn=min;
            timeformat(hour,min);
            timeText.setText(strHr+":"+strMin+" "+format);


        }

        if (c!=null)
            c.close();





        /*NOTIFICATION AT STARTUP*/
        /*NotificationHelper notificationHelper=new NotificationHelper(this);
        NotificationCompat.Builder nb= notificationHelper.getChannel1Notification("Dosing","Time to dose");
        notificationHelper.getManager().notify(1,nb.build());*/

        /* ACTION ON CLICK */
        Button button =  findViewById(R.id.SetAlarm);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){

                setAlarmText();
                assignDays();//JUMP TO ASSIGNDAYS()
                //System.out.println(allDays);
                //System.out.println(strHr+":"+strMin+" "+format);
                if(allDays.isEmpty()){
                    alarmTime="";
                }else{
                    alarmTime=(strHr+":"+strMin+" "+format);
                }

                if(position!=-786) {
                    Intent in = getIntent();
                    in.putExtra("Position", position);
                    in.putExtra("AlarmDays", allDays);
                    in.putExtra("AlarmDate", alarmTime);
                    setResult(RESULT_OK, in);
                    alarmdbhelper.updateItemAN("AlarmName",alarmName,"Category",AT,"AlarmDays",allDays);
                    alarmdbhelper.updateItemAN("AlarmName",alarmName,"Category",AT,"AlarmDate",(alarmTime));
                }
                else{

                    if(alarmName.equals("Macro"))
                    {



                        Intent intent = new Intent(SetAlarmActivity.this, SetAlarmActivity.class);
                        intent.putExtra("AlarmType","Dosing");
                        intent.putExtra("AquariumID",aquariumID);
                        intent.putExtra("AlarmName","Micro");
                        intent.putExtra("Position",-786);
                        startActivity(intent);


                    }
                    else if(alarmName.equals("Micro"))
                    {
                        Intent intent = new Intent(SetAlarmActivity.this, SetAlarmActivity.class);
                        intent.putExtra("AlarmType","Water Change");
                        intent.putExtra("AquariumID",aquariumID);
                        intent.putExtra("AlarmName","Weekly");
                        intent.putExtra("Position",-786);
                        startActivity(intent);

                    }
                    else if(alarmName.equals("Weekly"))
                    {
                        Intent intent = new Intent(SetAlarmActivity.this, DosingGraphsActivity.class);
                        intent.putExtra("AquariumID",aquariumID);
                        startActivity(intent);

                    }
                    SQLiteDatabase db=alarmdbhelper.getWritableDatabase();
                    Cursor c = alarmdbhelper.getDataAN2Condition(db,"AlarmName",alarmName,"Category",AT);
                    if(c.moveToFirst()){
                        alarmdbhelper.updateItemAN("AlarmName",alarmName,"Category",AT,"AlarmDays",allDays);
                        alarmdbhelper.updateItemAN("AlarmName",alarmName,"Category",AT,"AlarmDate",(alarmTime));
                    }
                    else {
                        alarmdbhelper.addDataAN(db, alarmName, AT, noInfoText, Slot, allDays, alarmTime);
                    }
                    c.close();


                }

                alarmdbhelper.updateItemAN("AlarmName",alarmName,"Category",AT,"AlarmFreq",Integer.toString(frequency));


                finish();

            }


        });


        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog=new TimePickerDialog(SetAlarmActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        hr=hourOfDay;
                        mn=minute;
                        timeformat(hourOfDay,minute);
                        timeText.setText(strHr+":"+strMin+" "+format);

                    }
                },hour,min,false);
                timePickerDialog.show();
            }
        });



    }

    private void setAlarmText(){

        EditText alarmText=findViewById(R.id.alarmText);

        if(!alarmText.getText().toString().isEmpty()) {

           String  alarmtext=alarmText.getText().toString();
           alarmdbhelper.updateItemAN("AlarmName",alarmName,"Category",AT,"AlarmText",alarmtext);
        }





    }

    private void assignDays(){


        SQLiteDatabase db=alarmdbhelper.getWritableDatabase();

        Cursor c= alarmdbhelper.getData2Condition("Alarm_Name",alarmName,"Alarm_Type",AT);

        while(c.moveToNext()) {


            AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent inn= new Intent(this, RamizAlarm.class);
            PendingIntent pi = PendingIntent.getBroadcast(this, c.getInt(3), inn, PendingIntent.FLAG_NO_CREATE);
            if(pi==null){
                //System.out.println("null");
                //System.out.println("Ramiz: No such pending intent exists "+c.getInt(3));
            }
            else{
                //System.out.println("not null");
                //System.out.println("Ramiz: Pending intent exists "+c.getInt(3));
                pi = PendingIntent.getBroadcast(this, c.getInt(3), inn, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pi);

            }

        }
        if (c!=null)
            c.close();



        //DELETE NUTRIENT TYPE ROWS TO POPULATE NEW LIST

        alarmdbhelper.deleteItem("Alarm_Name",alarmName,"Alarm_Type",AT);

        int i;
        ArrayList<CheckBox>DaysofWeek=new ArrayList<>();

            DaysofWeek.add((CheckBox) findViewById(R.id.Sun));
            DaysofWeek.add((CheckBox) findViewById(R.id.Mon));
            DaysofWeek.add((CheckBox) findViewById(R.id.Tue));
            DaysofWeek.add((CheckBox) findViewById(R.id.Wed));
            DaysofWeek.add((CheckBox) findViewById(R.id.Thu));
            DaysofWeek.add((CheckBox) findViewById(R.id.Fri));
            DaysofWeek.add((CheckBox) findViewById(R.id.Sat));
            frequency = 0;
            i = 0;
            for (CheckBox DoW : DaysofWeek) {
                if (!DoW.isChecked()) {
                    i++;
                }
            }
            if(i==7){
                Toast.makeText(this,"Alarms Cleared", Toast.LENGTH_SHORT).show();

            }
            else{
            i=1;
                j=Slot*7+1;

            for(CheckBox DoW:DaysofWeek) {
                if(DoW.isChecked()) {
                    SetTime(db, i);
                    ++frequency;
                }
                i++;
            }
                Toast.makeText(this,"Alarm(s) Set", Toast.LENGTH_SHORT).show();
            }

    }

    private void timeformat(int hour,int min){
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

    }

   private void SetTime(SQLiteDatabase db,int loc){

        Calendar calendar = Calendar.getInstance();




                calendar.set(

                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        hr,
                        mn,
                        0
                );





            /*String s1;
            s1=Integer.toString((loc));
            Toast.makeText(this, s1, Toast.LENGTH_SHORT).show();*/

            calendar.set(Calendar.DAY_OF_WEEK, loc);
            if(calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 7);
            }
            SimpleDateFormat dayFormat=new SimpleDateFormat("EEE", Locale.getDefault());
            allDays+=dayFormat.format(calendar.getTime())+" ";

            setAlarm(db,calendar.getTimeInMillis(),loc);
    }


    private void setAlarm(SQLiteDatabase db,long timeInMillis,int loc) {

        TankDBHelper tankDBHelper=TankDBHelper.newInstance(this);
        Cursor c=tankDBHelper.getDataCondition("AquariumID",aquariumID);
        c.moveToFirst();
        String AquariumName=c.getString(2);
        String defaultAlarm = c.getString(22);
        c.close();


        selectedItem=spinnerSetNotify.getSelectedItem().toString();

        AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this,RamizAlarm.class);
        intent.putExtra("AT",AT);
        intent.putExtra("AlarmName",alarmName);
        intent.putExtra("AquariumID",aquariumID);
        intent.putExtra("NotifyType",selectedItem);
        intent.putExtra("KEY_TRIGGER_TIME",timeInMillis);
        intent.putExtra("KEY_INTENT_ID",j);
        intent.putExtra("AquariumName",AquariumName);
        //System.out.println("value of j " +j);

       // Toast.makeText(this,Integer.toString(j), Toast.LENGTH_SHORT).show();

        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,j,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        }else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        }


        // Populate data <<DO NOT DELETE>>
        String s1 = Long.toString(timeInMillis);
       /* System.out.println("Ramiz: Nutrient Type to be populated "+AT);
        System.out.println("Ramiz: alarm value to be populated " +timeInMillis );
        System.out.println("Ramiz: alarm no to be populated " +j );
        System.out.println("Ramiz: day to be populated " +loc );*/
        long rows=alarmdbhelper.addData(db,alarmName,AT,s1,j++,loc,hr,mn,selectedItem,Slot);
        int defaultAlarmInt;
        try {

             defaultAlarmInt = Integer.parseInt(defaultAlarm);
        }catch (Exception e){

             defaultAlarmInt = 0;

        }
        int alarmBitsWeekly = defaultAlarmInt % 10;
        defaultAlarmInt /=10;
        int alarmBitsMicro = defaultAlarmInt % 10;
        defaultAlarmInt /=10;
        int alarmBitsMacro = defaultAlarmInt % 10;

        if(!defaultAlarm.equals("111")) {

            if (AT.equals("Dosing")) {
                if (alarmName.equals("Macro")) {

                    alarmBitsMacro = 1;

                } else if (alarmName.equals("Micro")) {

                    alarmBitsMicro = 1;
                }
            }
            if (AT.equals("Water Change") && alarmName.equals("Weekly")) {

                alarmBitsWeekly = 1;
            }

            defaultAlarm = Integer.toString(alarmBitsMacro) + Integer.toString(alarmBitsMicro) + Integer.toString(alarmBitsWeekly);
            tankDBHelper.updateSingleItem("AquariumID",aquariumID,"DefaultAlarm",defaultAlarm);
            Toast.makeText(this,defaultAlarm,Toast.LENGTH_LONG).show();
        }

        /*long row =DatabaseUtils.queryNumEntries(db,"NTTable");
        System.out.println("Final rows " +row );*/



    }

}
