package com.newage.aquapets.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.newage.aquapets.R;
import com.newage.aquapets.dbhelpers.TankDBHelper;

public class OptionsActivity extends AppCompatActivity {

String aquaID;
String tankName;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle bundle=getIntent().getExtras();
        aquaID=bundle.getString("AquariumID");

        TankDBHelper tankDBHelper=TankDBHelper.newInstance(this);
        SQLiteDatabase db=tankDBHelper.getWritableDatabase();
        Cursor c=tankDBHelper.getDataCondition("AquariumID",aquaID);
        c.moveToNext();
        tankName=c.getString(2);
        c.close();
        
        setContentView(R.layout.activity_main_selection);
        getSupportActionBar().setTitle(getResources().getString(R.string.Options) + " : " +tankName);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);




       // System.out.println("Aqua ID : "+aquaID);


        Button light_calc_button =  findViewById(R.id.LightCalculatorButton);

        light_calc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i3 = new Intent(view.getContext(), LightCalcActivity.class);
                i3.putExtra("AquariumID",aquaID);
                startActivity(i3);

            }

        });

        Button macrodosagebutton =  findViewById(R.id.macrodosagebutton);

        macrodosagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i4 = new Intent(view.getContext(), MacroNutrientTableActivity.class);
                i4.putExtra("AquariumID",aquaID);
                startActivity(i4);

            }

        });
        Button microdosagebutton =  findViewById(R.id.microdosagebutton);

        microdosagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i5 = new Intent(view.getContext(), MicroNutrientTableActivity.class);
                i5.putExtra("AquariumID",aquaID);
                startActivity(i5);

            }

        });
        Button albumbutton =  findViewById(R.id.tankProgressButton);

        albumbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i6 = new Intent(view.getContext(), TankProgressActivity.class);
                i6.putExtra("AquariumID",aquaID);
                i6.putExtra("AquariumName",tankName);
                startActivity(i6);

            }

        });
        Button taskButton =  findViewById(R.id.TaskButton);

        taskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i7 = new Intent(view.getContext(), TasksActivity.class);
                i7.putExtra("AquariumID",aquaID);
                i7.putExtra("AquariumName",tankName);
                startActivity(i7);

            }

        });
        Button LogsButton =  findViewById(R.id.LogsButton);

        LogsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i8 = new Intent(view.getContext(), LogsActivity.class);
                i8.putExtra("AquariumID",aquaID);
                i8.putExtra("AquariumName",tankName);
                startActivity(i8);

            }

        });
        Button EqButton =  findViewById(R.id.EqButton);

        EqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i9 = new Intent(view.getContext(), TankItemListActivity.class);
                i9.putExtra("AquariumID",aquaID);
                i9.putExtra("ItemCategory","E");
                i9.putExtra("AquariumName",tankName);
                startActivity(i9);

            }

        });

        Button floraButton =  findViewById(R.id.FloraButton);

        floraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i10 = new Intent(view.getContext(), TankItemListActivity.class);
                i10.putExtra("AquariumID",aquaID);
                i10.putExtra("ItemCategory","Fl");
                i10.putExtra("AquariumName",tankName);
                startActivity(i10);

            }

        });

        Button faunaButton =  findViewById(R.id.FaunaButton);

        faunaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i11 = new Intent(view.getContext(), TankItemListActivity.class);
                i11.putExtra("AquariumID",aquaID);
                i11.putExtra("ItemCategory","Fr");
                i11.putExtra("AquariumName",tankName);
                startActivity(i11);

            }

        });

       Button convertButton=findViewById(R.id.ConvButton);
       convertButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent i12 = new Intent(view.getContext(), ConverterActivity.class);
               i12.putExtra("AquariumID",aquaID);

               startActivity(i12);
           }
       });
        Button InsightButton=findViewById(R.id.InsightButton);
        InsightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i13 = new Intent(view.getContext(), TankInsights.class);
                i13.putExtra("AquariumID",aquaID);

                startActivity(i13);
            }
        });

        Button dosing_button=findViewById(R.id.DosingButton);
        dosing_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i14 = new Intent(view.getContext(), DosingGraphsActivity.class);
                i14.putExtra("AquariumID",aquaID);

                startActivity(i14);
            }
        });

    }

}





