package com.newage.plantedaqua.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.snackbar.Snackbar;
import com.newage.plantedaqua.dbhelpers.MyDbHelper;
import com.newage.plantedaqua.dbhelpers.TankDBHelper;
import com.newage.plantedaqua.dbhelpers.NutrientDbHelper;
import com.newage.plantedaqua.R;

import java.util.ArrayList;

import java.util.Locale;

public class DosingGraphsActivity extends AppCompatActivity {

    private String aquariumID;
    private String kDose,nDose,pDose,cDose,mDose,kUptake,nUptake,pUptake,cUptake,mUptake;
    private NutrientDbHelper nutrientDbHelper;
    private MyDbHelper myDbHelper;
    private EditText nDoseEntry,pDoseEntry,kDoseEntry,mDoseEntry,cDoseEntry,nUptakeEntry,pUptakeEntry,kUptakeEntry,cUptakeEntry,mUptakeEntry;
    private Button waterChangePercentButton;
    private SharedPreferences.Editor tankSettingsEditor;
    private LinearLayout dosingGraphsMainLayout;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Cursor c = myDbHelper.getDataMaLDayWise("UPTAKE_PPM");
        if(c.moveToFirst()){

            myDbHelper.updateDataMaL("UPTAKE_PPM",kUptakeEntry.getText().toString(),nUptakeEntry.getText().toString(),pUptakeEntry.getText().toString(),cUptakeEntry.getText().toString(),mUptakeEntry.getText().toString(),"","","","");
        }
        else {

            myDbHelper.addDataMaL("UPTAKE_PPM",kUptakeEntry.getText().toString(),nUptakeEntry.getText().toString(),pUptakeEntry.getText().toString(),cUptakeEntry.getText().toString(),mUptakeEntry.getText().toString(),"","","","");


        }
        c.close();

        c = nutrientDbHelper.getDataMAD();
        if(c.moveToFirst()){
            nutrientDbHelper.updateDataMaD("","","","","","",kDoseEntry.getText().toString(),nDoseEntry.getText().toString(),pDoseEntry.getText().toString(),cDoseEntry.getText().toString(),mDoseEntry.getText().toString(),"","","","","","","");
        }
        else {

            nutrientDbHelper.addDataMaD("","","","","","",kDoseEntry.getText().toString(),nDoseEntry.getText().toString(),pDoseEntry.getText().toString(),cDoseEntry.getText().toString(),mDoseEntry.getText().toString(),"","","","","","","");


        }
        c.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logs_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        MyDbHelper myDbHelper = MyDbHelper.newInstance(this,aquariumID);
        Cursor c = myDbHelper.getDataMaL();

        Intent restartIntent;

        if (id == R.id.ClearLog) {

            if(c!=null){

                if(c.moveToFirst()){
                    myDbHelper.deleteAllMaL();
                    restartIntent = new Intent(this, DosingGraphsActivity.class);
                    restartIntent.putExtra("AquariumID", aquariumID);
                    startActivity(restartIntent);
                    finish();
                }
                else
                    Toast.makeText(this,"No logs to clear",Toast.LENGTH_SHORT).show();

                c.close();

            }



            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dosing_graphs);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        aquariumID = getIntent().getStringExtra("AquariumID");


        TankDBHelper tankDBHelper = TankDBHelper.newInstance(this);

        Cursor c = tankDBHelper.getDataCondition("AquariumID", aquariumID);

        String aquaName = "";

        if(c!=null) {

            if (c.moveToFirst()) {

                aquaName = TextUtils.isEmpty(c.getString(2)) ? "" : c.getString(2);


            }

            c.close();
        }

        getSupportActionBar().setTitle("Nutrients Graphs"+" : "+aquaName);


        dosingGraphsMainLayout = findViewById(R.id.DosingGraphsMainLayout);


        SharedPreferences tankSettings = getApplicationContext().getSharedPreferences(aquariumID, 0);
        float waterChangePercentFromDB = tankSettings.getFloat("waterChangePercent", 0.5f);
        tankSettingsEditor = tankSettings.edit();

        nutrientDbHelper = NutrientDbHelper.newInstance(this,aquariumID);
        myDbHelper = MyDbHelper.newInstance(this,aquariumID);

        nDoseEntry = findViewById(R.id.NDoseEditText);
        pDoseEntry = findViewById(R.id.PDoseEditText);
        kDoseEntry = findViewById(R.id.KDoseEditText);
        cDoseEntry = findViewById(R.id.CDoseEditText);
        mDoseEntry = findViewById(R.id.MDoseEditText);

        nUptakeEntry = findViewById(R.id.NUptakeEditText);
        pUptakeEntry = findViewById(R.id.PUptakeEditText);
        kUptakeEntry = findViewById(R.id.KUptakeEditText);
        cUptakeEntry = findViewById(R.id.CUptakeEditText);
        mUptakeEntry = findViewById(R.id.MUptakeEditText);



        //region WATER CHANGE % BUTTON IMPLEMENTATION
        waterChangePercentButton = findViewById(R.id.WaterChangePercentButton);

        waterChangePercentButton.setText(String.format(Locale.getDefault(),"%.2f", waterChangePercentFromDB *100f) + " %");


        waterChangePercentButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               AlertDialog.Builder inputPercentAlert = new AlertDialog.Builder(DosingGraphsActivity.this);
               final EditText waterChangePercentInput = new EditText(DosingGraphsActivity.this);
               waterChangePercentInput.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);
               inputPercentAlert.setView(waterChangePercentInput)
                       .setTitle("Water Change %")
                       .setMessage("Please enter the water default water change percent")
                       .setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {

                               String waterChangeInputFromUser = waterChangePercentInput.getText().toString();
                               float waterChangePercentValue = 0.5f;
                               if (TextUtils.isEmpty(waterChangeInputFromUser)){

                                   Snackbar.make(dosingGraphsMainLayout,"No Input from user. Default value set.",Snackbar.LENGTH_SHORT).show();

                               }
                               else{

                                   waterChangePercentValue =  Float.parseFloat(waterChangeInputFromUser.replace(",","."))/100f;
                               }
                               waterChangePercentButton.setText(String.format(Locale.getDefault(),"%.2f",waterChangePercentValue*100f) + " %");
                               tankSettingsEditor.putFloat("waterChangePercent",waterChangePercentValue);
                               tankSettingsEditor.apply();



                           }
                       })
                       .setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               dialog.dismiss();
                           }
                       })
                       .create()
                       .show();
           }
       });
        //endregion


        c = tankDBHelper.getDataCondition("AquariumID",aquariumID);
        c.moveToFirst();
        String defaultAlarm = c.getString(22);
        c.close();

        if(!defaultAlarm.equals("111")) {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Insufficient Data")
                    .setMessage("You must add Macro, Micro Dosage and Weekly Water Change Schedule to use this feature. Lets start with Macro Dosing Schedule")
                    .setPositiveButton("Lets do it", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(DosingGraphsActivity.this, SetAlarmActivity.class);
                            intent.putExtra("AlarmType", "Dosing");
                            intent.putExtra("AquariumID", aquariumID);
                            intent.putExtra("AlarmName", "Macro");
                            intent.putExtra("Position", -786);
                            startActivity(intent);
                            finish();


                        }
                    })
                    .setNegativeButton("May be later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            finish();

                        }
                    })
                    .setCancelable(false)
                    .create().show();
        }

        else {


            ImageView saveDoseImage = findViewById(R.id.SaveDoseImage);
            saveDoseImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Cursor c = nutrientDbHelper.getDataMAD();
                    if(c.moveToFirst()){
                        nutrientDbHelper.updateDataMaD("","","","","","",kDoseEntry.getText().toString(),nDoseEntry.getText().toString(),pDoseEntry.getText().toString(),cDoseEntry.getText().toString(),mDoseEntry.getText().toString(),"","","","","","","");
                    }
                    else {

                        nutrientDbHelper.addDataMaD("","","","","","",kDoseEntry.getText().toString(),nDoseEntry.getText().toString(),pDoseEntry.getText().toString(),cDoseEntry.getText().toString(),mDoseEntry.getText().toString(),"","","","","","","");


                    }
                    c.close();
                    Toast.makeText(DosingGraphsActivity.this,"Dosing values are set",Toast.LENGTH_SHORT).show();


                }
            });
            ImageView saveUptakeImage = findViewById(R.id.SaveUptakeImage);
            saveUptakeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Cursor c = myDbHelper.getDataMaLDayWise("UPTAKE_PPM");
                    if(c.moveToFirst()){

                        myDbHelper.updateDataMaL("UPTAKE_PPM",kUptakeEntry.getText().toString(),nUptakeEntry.getText().toString(),pUptakeEntry.getText().toString(),cUptakeEntry.getText().toString(),mUptakeEntry.getText().toString(),"","","","");
                    }
                    else {

                        myDbHelper.addDataMaL("UPTAKE_PPM",kUptakeEntry.getText().toString(),nUptakeEntry.getText().toString(),pUptakeEntry.getText().toString(),cUptakeEntry.getText().toString(),mUptakeEntry.getText().toString(),"","","","");


                    }
                    c.close();

                    Toast.makeText(DosingGraphsActivity.this,"Uptake values are set",Toast.LENGTH_SHORT).show();



                }
            });



            presetData();

            setInitialValues();

            LineChart lineChart1, lineChart2;

            lineChart1 = findViewById(R.id.DosingChart1);

            XAxis xAxis = lineChart1.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setLabelRotationAngle(90);


            lineChart1.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int index = (int) value;
                    return xAxes.get(index);
                }
            });


            ArrayList<ILineDataSet> dataSet1 = new ArrayList<>();


            generateGraph(dataSet1,lineChart1, generateData("K"), "K in ppm", Color.MAGENTA);
            generateGraph(dataSet1,lineChart1, generateData("NO3"), "NO3 in ppm", Color.CYAN);
            generateGraph(dataSet1,lineChart1, generateData("PO4"), "PO4 in ppm", Color.GREEN);

            lineChart2 = findViewById(R.id.DosingChart2);

            XAxis xAxis2 = lineChart2.getXAxis();
            xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis2.setLabelRotationAngle(90);


            lineChart2.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int index = (int) value;
                    return xAxes.get(index);
                }
            });


            ArrayList<ILineDataSet> dataSet2 = new ArrayList<>();
            generateGraph(dataSet2, lineChart2, generateData("Ca"), "Ca in ppm", Color.YELLOW);
            generateGraph(dataSet2, lineChart2, generateData("Mg"), "Mg in ppm", Color.BLUE);


        }

    }

    private void presetData(){

        nDose = "0";
        pDose = "0";
        kDose = "0";
        cDose = "0";
        mDose = "0";

        Cursor c = nutrientDbHelper.getDataMAD();

            if (c.moveToFirst()) {
                kDose = c.getString(7);
                nDose = c.getString(8);
                pDose = c.getString(9);
                cDose = c.getString(10);
                mDose = c.getString(11);


            }
            c.close();
        c = myDbHelper.getDataMaLDayWise("UPTAKE_PPM");
        if(c.moveToFirst()){

            nUptake = c.getString(2);
            pUptake = c.getString(3);
            kUptake = c.getString(1);
            cUptake = c.getString(4);
            mUptake = c.getString(5);



        }else {

            nUptake = String.format(Locale.getDefault(), "%.2f", (Float.parseFloat(nDose.replace(",", ".")) * 0.8f));
            pUptake = String.format(Locale.getDefault(), "%.2f", (Float.parseFloat(pDose.replace(",", ".")) * 0.8f));
            kUptake = String.format(Locale.getDefault(), "%.2f", (Float.parseFloat(kDose.replace(",", ".")) * 0.8f));
            cUptake = String.format(Locale.getDefault(), "%.2f", (Float.parseFloat(cDose.replace(",", ".")) * 0.8f));
            mUptake = String.format(Locale.getDefault(), "%.2f", (Float.parseFloat(mDose.replace(",", ".")) * 0.8f));
        }

        c.close();



    }

    private void setInitialValues(){

        nDoseEntry.setText(nDose);
        pDoseEntry.setText(pDose);
        kDoseEntry.setText(kDose);
        cDoseEntry.setText(cDose);
        mDoseEntry.setText(mDose);

        nUptakeEntry.setText(nUptake);
        pUptakeEntry.setText(pUptake);
        kUptakeEntry.setText(kUptake);
        cUptakeEntry.setText(cUptake);
        mUptakeEntry.setText(mUptake);




    }





    private ArrayList<String> xAxes;

    private ArrayList<Entry> generateData(String fertName){


        MyDbHelper myDbHelper = MyDbHelper.newInstance(this,aquariumID);
        ArrayList<Entry> yValues = new ArrayList<>();
        //Log_Day text,Log_Date text,Log_Task text,Log_Category text,Log_Status text)
        Cursor c = myDbHelper.last20MacroTable();
        float i = 0f;
        float nutrient = 0f;
        xAxes = new ArrayList<>();
        if(c.moveToFirst()){
            do{
                if(!c.getString(0).equals("UPTAKE_PPM")) {
                    xAxes.add(c.getString(0));

                    switch (fertName) {
                        //MacroLogs(Date text,kPpm text, nPpm text, pPpm text, caPpm text, mgPpm text, sPpm text)");
                        case "K":
                            nutrient = Float.parseFloat(c.getString(1).replace(",", "."));
                            break;
                        case "NO3":
                            nutrient = Float.parseFloat(c.getString(2).replace(",", "."));
                            break;
                        case "PO4":
                            nutrient = Float.parseFloat(c.getString(3).replace(",", "."));
                            break;
                        case "Ca":
                            nutrient = Float.parseFloat(c.getString(4).replace(",", "."));
                            break;
                        case "Mg":
                            nutrient = Float.parseFloat(c.getString(5).replace(",", "."));
                            break;

                    }
                    //Log.i("nutrient",c.getString(0));
                    yValues.add(new Entry(i++, nutrient));
                }

            }while (c.moveToNext());
        }
        c.close();



        return yValues;
    }





    private void generateGraph(ArrayList<ILineDataSet> dataSets, LineChart lineChart, ArrayList<Entry> yValues, String dataSetName,int color) {

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);



        LineDataSet lineDataSet = new LineDataSet(yValues,dataSetName);
        lineDataSet.setColor(color);
        dataSets.add(lineDataSet);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);



    }
}
