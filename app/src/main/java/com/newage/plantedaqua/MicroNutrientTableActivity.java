package com.newage.plantedaqua;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MicroNutrientTableActivity extends AppCompatActivity {

    double liqDoseRatio=1d;
    String aquariumid="";
    boolean liqdosestatus=false;
    String alarmDetails="";
    String nutrient="";
    double weights[]=new double[4];
    MyDbHelper mydbhelper;
    ArrayList<Double> percentAll= new ArrayList <>(Arrays.asList(6.53,1.87, 1.4, 0.37, 0.09, 0.05, 1.18));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.microtable);

        Button reset=findViewById(R.id.resetbuttom);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(MicroNutrientTableActivity.this, MicroNutrientTableActivity.class));
            }
        });

        Intent intent=getIntent();
        aquariumid=intent.getStringExtra("AquariumID");


        TinyDB def=new TinyDB(this);
        def.getBoolean("microNotDef");

        TinyDB tinydb = new TinyDB(this);

        if(!def.getBoolean("microNotDef")) {
            tinydb.putListDouble("microPercent", percentAll);
        }

        setpercentBox(this);




        RadioGroup DoseButton =findViewById(R.id.DoseType);
        DoseButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                RadioButton dosetype=findViewById(checkedId);
                if (dosetype.getText().toString().equals("Liquid Dose")){

                    LinearLayout layout =findViewById(R.id.LiquidDose);
                    for (int i = 0; i < layout.getChildCount(); i++) {
                        View child = layout.getChildAt(i);
                        child.setEnabled(true);
                    }
                    Button notifyMe=findViewById(R.id.AlarmSalt);
                    notifyMe.setVisibility(View.INVISIBLE);
                    notifyMe.setEnabled(false);
                }

                else{

                    LinearLayout layout =findViewById(R.id.LiquidDose);
                    for (int i = 0; i < layout.getChildCount()-3; i++) {
                        View child = layout.getChildAt(i);
                        child.setEnabled(false);
                        liqDoseRatio=1d;

                    }
                    Button notifyMe=findViewById(R.id.AlarmSalt);
                    notifyMe.setVisibility(View.INVISIBLE);
                    notifyMe.setEnabled(false);
                }

            }
        });

        Button defaultpercent=findViewById(R.id.Defaultpercent);
        defaultpercent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                percentAll= new ArrayList <>(Arrays.asList(6.53,1.87, 1.4, 0.37, 0.09, 0.05, 1.18));
                TinyDB tinydb = new TinyDB(MicroNutrientTableActivity.this);
                tinydb.putListDouble("microPercent", percentAll);
                setpercentBox(MicroNutrientTableActivity.this);


            }
        });

        Button calculate = findViewById(R.id.Calculate);
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calculate();
            }
        });
    }

    public void Calculate() {



        EditText tankvolume = findViewById(R.id.TankVolumeBox);
        RadioButton selectedradiobutton;

        RadioGroup dosageType = findViewById(R.id.DoseType);
        selectedradiobutton = findViewById(dosageType.getCheckedRadioButtonId());


        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        NodayDialog dialog = new NodayDialog();

        if (tankvolume.getText().toString().isEmpty()) {
            dialog.show(fragmentManager, "Please enter Tank volume");
        }
        else if (selectedradiobutton.getText().toString().equals("Liquid Dose")) {

            EditText stockvolume = findViewById(R.id.StockBox);
            EditText dosevolume = findViewById(R.id.DoseBox);
            if (stockvolume.getText().toString().isEmpty()&& !dosevolume.getText().toString().isEmpty()) {
                dialog.show(fragmentManager, "Please enter total volume of stock solution");
            }
            else if (dosevolume.getText().toString().isEmpty() && !stockvolume.getText().toString().isEmpty()) {
                dialog.show(fragmentManager, "Please enter dose volume of stock solution");
            }
            else if (dosevolume.getText().toString().isEmpty() && stockvolume.getText().toString().isEmpty()){
                dialog.show(fragmentManager, "Please enter volumes for liquid dosing");
            }
            else mainCalculate();

        }

        else mainCalculate();

    }

    void mainCalculate() {


        double volume;
        double frequency = 1d;

        EditText tankvolume = findViewById(R.id.TankVolumeBox);
        RadioButton selectedradiobutton;

        RadioGroup dosageType = findViewById(R.id.DoseType);
        selectedradiobutton = findViewById(dosageType.getCheckedRadioButtonId());

        volume = Double.parseDouble(tankvolume.getText().toString());

        if (selectedradiobutton.getText().toString().equals("Liquid Dose")) {

            liqdosestatus=true;
            EditText stockvolume = findViewById(R.id.StockBox);
            EditText dosevolume = findViewById(R.id.DoseBox);
            liqDoseRatio = Double.parseDouble(stockvolume.getText().toString()) / Double.parseDouble(dosevolume.getText().toString());
            alarmDetails="Dose "+dosevolume.getText().toString()+" ml of stock solution\n";
        } else {

            liqdosestatus=false;
            liqDoseRatio = 1d;
            alarmDetails="";
        }


        EditText frequencybox = findViewById(R.id.FrequencyBox);
        if (!(frequencybox.getText().toString().isEmpty())) {
            frequency = Double.parseDouble(frequencybox.getText().toString());

        }

        RadioGroup VolumeUnit = findViewById(R.id.VolumeUnit);
        int selectedid = VolumeUnit.getCheckedRadioButtonId();
        selectedradiobutton = findViewById(selectedid);


        // Is Gallon selected or litres
        if (selectedradiobutton.getText().toString().equals("Gallon")) {
            microDetails.volumeinltrs = 3.78541 * volume;
            // Toast.makeText(this, Double.toString(microDetails.volumeinltrs), Toast.LENGTH_SHORT).show();

        } else {
            microDetails.volumeinltrs = volume;
        }


        double rootchemical = 100d;

        //USER INPUT PPM TO ARRAY
        EditText pInput = findViewById(R.id.Fepercent);
        if (!(pInput.getText().toString().isEmpty())) {
            percentAll.set(0, Double.parseDouble(pInput.getText().toString()));
        }
        pInput = findViewById(R.id.Mnpercent);
        if (!(pInput.getText().toString().isEmpty())) {
            percentAll.set(1, Double.parseDouble(pInput.getText().toString()));
        }
        pInput = findViewById(R.id.Mgpercent);
        if (!(pInput.getText().toString().isEmpty())) {
            percentAll.set(2, Double.parseDouble(pInput.getText().toString()));
        }
        pInput = findViewById(R.id.Znpercent);
        if (!(pInput.getText().toString().isEmpty())) {
            percentAll.set(3, Double.parseDouble(pInput.getText().toString()));
        }
        pInput = findViewById(R.id.Cupercent);
        if (!(pInput.getText().toString().isEmpty())) {
            percentAll.set(4, Double.parseDouble(pInput.getText().toString()));
        }
        pInput = findViewById(R.id.Mopercent);
        if (!(pInput.getText().toString().isEmpty())) {
            percentAll.set(5, Double.parseDouble(pInput.getText().toString()));
        }
        pInput = findViewById(R.id.Bpercent);
        if (!(pInput.getText().toString().isEmpty())) {
            percentAll.set(6, Double.parseDouble(pInput.getText().toString()));
        }

        TinyDB tinydb = new TinyDB(this);
        tinydb.putListDouble("microPercent",percentAll);

        setpercentBox(this);



       //Fe
        Double Feppm;
        EditText FeTargetppm=findViewById(R.id.FeTarget);
        if(!FeTargetppm.getText().toString().isEmpty()) {
            Feppm = Double.parseDouble(FeTargetppm.getText().toString());
        }
        else{
            Feppm=1d;
        }
        microDetails Fe = new microDetails(rootchemical, percentAll.get(0), Feppm / frequency);
        double dosageingrames = Fe.calcDosage();
        TextView Feppmtext = findViewById(R.id.Feppm);
        Feppmtext.setText(Double.toString(Fe.calcPPM(dosageingrames))+getResources().getString(R.string.ppm));
        TextView FeTargetgm=findViewById(R.id.FeTargetgm);
        FeTargetgm.setText(Double.toString(Math.round(dosageingrames * liqDoseRatio * 1000d) / 1000d)+getResources().getString(R.string.Gram));
        weights[0]=Math.round(dosageingrames * liqDoseRatio * 1000d) / 1000d;

        //Mn
        microDetails Mn=new microDetails(rootchemical,percentAll.get(1),0);
        TextView Mnppm=findViewById(R.id.Mnppm);
        Mnppm.setText(Double.toString(Mn.calcPPM(dosageingrames))+getResources().getString(R.string.ppm));

        //Mg
        microDetails Mg=new microDetails(rootchemical,percentAll.get(2),0);
        TextView Mgppm=findViewById(R.id.Mgppm);
        Mgppm.setText(Double.toString(Mg.calcPPM(dosageingrames))+getResources().getString(R.string.ppm));

        //Zn
        microDetails Zn=new microDetails(rootchemical,percentAll.get(3),0);
        TextView Znppm=findViewById(R.id.Znppm);
        Znppm.setText(Double.toString(Zn.calcPPM(dosageingrames))+getResources().getString(R.string.ppm));

        //Cu
        microDetails Cu=new microDetails(rootchemical,percentAll.get(4),0);
        TextView Cuppm=findViewById(R.id.Cuppm);
        Cuppm.setText(Double.toString(Cu.calcPPM(dosageingrames))+getResources().getString(R.string.ppm));

        //Mo
        microDetails Mo=new microDetails(rootchemical,percentAll.get(5),0);
        TextView Moppm=findViewById(R.id.Moppm);
        Moppm.setText(Double.toString(Mo.calcPPM(dosageingrames))+getResources().getString(R.string.ppm));

        //B
        microDetails B=new microDetails(rootchemical,percentAll.get(6),0);
        TextView Bppm=findViewById(R.id.Bppm);
        Bppm.setText(Double.toString(B.calcPPM(dosageingrames))+getResources().getString(R.string.ppm));

        setNutAlarmButton();



    }

    void setNutAlarmButton(){

        Button notifyMe=findViewById(R.id.AlarmSalt);
        notifyMe.setEnabled(true);
        notifyMe.setVisibility(View.VISIBLE);

        notifyMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setNutalarmText();

            }
        });




    }
    void setNutalarmText(){


        alarmDetails="";

       mydbhelper= MyDbHelper.newInstance(   this,aquariumid);
        SQLiteDatabase dbNL=mydbhelper.getWritableDatabase();
        Cursor cNL=mydbhelper.getDataANCondition(dbNL,"Category","Dosing");

        ArrayList<String> nutrientList=new ArrayList <>();

        if(cNL.moveToFirst()) {
            do {
                nutrientList.add(cNL.getString(0));
            } while (cNL.moveToNext());

        }
        cNL.close();

        if(nutrientList.isEmpty()) {
            AlertDialog.Builder noFertBuilder = new AlertDialog.Builder(this);
            noFertBuilder.setMessage(getResources().getString(R.string.NoFert))
                    .setNeutralButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
        }else {


            //INCORPORATE LIQUID DOSAGE IN ALARM TEXT
            RadioGroup dosageType = findViewById(R.id.DoseType);
            RadioButton selectedradiobutton = findViewById(dosageType.getCheckedRadioButtonId());

            if (selectedradiobutton.getText().toString().equals("Liquid Dose")) {

                EditText dosevolume = findViewById(R.id.DoseBox);
                liqdosestatus = true;
                alarmDetails = "Dose " + dosevolume.getText().toString() + " ml of stock solution";
            } else {
                TextView FeTargetgm = findViewById(R.id.FeTargetgm);
                alarmDetails = "Dose " + FeTargetgm.getText().toString() + " grams of Micro";
                liqdosestatus = false;
            }


            AlertDialog.Builder builder = new AlertDialog.Builder(MicroNutrientTableActivity.this);
            View view = getLayoutInflater().inflate(R.layout.select, null);
            final Spinner choose = view.findViewById(R.id.choose);
            ArrayAdapter <String> adapter = new ArrayAdapter <>(MicroNutrientTableActivity.this, android.R.layout.simple_spinner_item, nutrientList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            choose.setAdapter(adapter);
            builder.setTitle("Which Fert to Notify...");
            builder.setView(view);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    nutrient = choose.getSelectedItem().toString();
                    addNutrientDosing();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    public void addNutrientDosing(){

        mydbhelper=MyDbHelper.newInstance(this,aquariumid);
        mydbhelper.updateItemAN("AlarmName",nutrient,"Category","Dosing","AlarmText", alarmDetails);
        Toast.makeText(this, "You will be notified about this dosage", Toast.LENGTH_SHORT).show();
    }

    void setpercentBox(Context context){

        TinyDB tinydb=new TinyDB(context);
        TinyDB notDef= new TinyDB(context);
        percentAll=tinydb.getListDouble("microPercent");

        EditText pInput;

        pInput = findViewById(R.id.Fepercent);
        pInput.setText(percentAll.get(0).toString());
        pInput = findViewById(R.id.Mnpercent);
        pInput.setText(percentAll.get(1).toString());
        pInput = findViewById(R.id.Mgpercent);
        pInput.setText(percentAll.get(2).toString());
        pInput = findViewById(R.id.Znpercent);
        pInput.setText(percentAll.get(3).toString());
        pInput = findViewById(R.id.Cupercent);
        pInput.setText(percentAll.get(4).toString());
        pInput = findViewById(R.id.Mopercent);
        pInput.setText(percentAll.get(5).toString());
        pInput = findViewById(R.id.Bpercent);
        pInput.setText(percentAll.get(6).toString());
        notDef.putBoolean("microNotDef",true);

    }
}
