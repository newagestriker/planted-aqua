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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MicroNutrientTableActivity extends AppCompatActivity {

    private double liqDoseRatio=1d;
    private String aquariumid="";
    private boolean liqdosestatus=false;
    private String alarmDetails="";
    private String nutrient="";
    private Spinner presetSpinner;
    private double weights[]=new double[4];
    private ArrayList<ArrayList<Double>> percentAll = new ArrayList<>();
    private MyDbHelper mydbhelper;
    private ArrayList<String> defaultSets=new ArrayList <>(Arrays.asList("CSM + 1.18% B", "Fe EDTA 10%","Fe EDTA 12%"));
    private ArrayList<String> allSets;
    private ArrayAdapter<String> presetAdapter;
    private ImageView deleteSetButton;

    private void addInitialPPMValuesToDB() {


        TinyDB ppmDB = new TinyDB(this);
        ppmDB.putListDouble("CSM + 1.18% B",new ArrayList <>(Arrays.asList(6.53d, 1.87d, 1.4d, 0.37d, 0.09d, 0.05d, 1.18d)));
        ppmDB.putListDouble("Fe EDTA 10%",new ArrayList <>(Arrays.asList(10d, 0d, 0d, 0d, 0d, 0d, 0d)));
        ppmDB.putListDouble("Fe EDTA 12%",new ArrayList <>(Arrays.asList(12d, 0d, 0d, 0d, 0d, 0d, 0d)));

        // TODO : Increase minimum position to make delete button visible as new values are added here

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.microtable);
        addInitialPPMValuesToDB();
        allSets = readSets();

        TinyDB percentSets = new TinyDB(this);

        for(String percentSetName:allSets){
            percentAll.add(percentSets.getListDouble(percentSetName));
        }

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

        deleteSetButton = findViewById(R.id.DeleteMicroSetButton);

        presetSpinner = findViewById(R.id.PresetMicroSpinner);
        presetAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,allSets);
        presetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        presetSpinner.setAdapter(presetAdapter);

        presetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if(position<3)
                    deleteSetButton.setVisibility(View.GONE);
                else
                    deleteSetButton.setVisibility(View.VISIBLE);

                setpercentBox(percentAll.get(position));


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setpercentBox(percentAll.get(0));

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

    private void addSets() {

        TinyDB setDB = new TinyDB(this);
        setDB.putListString("AllSet",allSets);

    }
    private void removeSets(String setToRemove) {

        TinyDB setDB = new TinyDB(this);
        setDB.remove(setToRemove);

    }
    private ArrayList<String> readSets() {

        TinyDB setDB = new TinyDB(this);

        if ((setDB.getListString("AllSet")).isEmpty())
            return defaultSets;

        return(setDB.getListString("AllSet"));

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

        ArrayList<Double> tempPPM = new ArrayList<>(Arrays.asList(6.53d, 1.87d, 1.4d, 0.37d, 0.09d, 0.05d, 1.18d));

        addUserInputPPMToArray("Custom Values",tempPPM);

       //Fe
        Double Feppm;
        EditText FeTargetppm=findViewById(R.id.FeTarget);
        if(!FeTargetppm.getText().toString().isEmpty()) {
            Feppm = Double.parseDouble(FeTargetppm.getText().toString());
        }
        else{
            Feppm=1d;
        }
        microDetails Fe = new microDetails(rootchemical, tempPPM.get(0), Feppm / frequency);
        double dosageingrames = Fe.calcDosage();
        TextView Feppmtext = findViewById(R.id.Feppm);
        Feppmtext.setText(Double.toString(Fe.calcPPM(dosageingrames))+getResources().getString(R.string.ppm));
        TextView FeTargetgm=findViewById(R.id.FeTargetgm);
        FeTargetgm.setText(Double.toString(Math.round(dosageingrames * liqDoseRatio * 1000d) / 1000d)+getResources().getString(R.string.Gram));
        weights[0]=Math.round(dosageingrames * liqDoseRatio * 1000d) / 1000d;

        //Mn
        microDetails Mn=new microDetails(rootchemical,tempPPM.get(1),0);
        TextView Mnppm=findViewById(R.id.Mnppm);
        Mnppm.setText(Double.toString(Mn.calcPPM(dosageingrames))+getResources().getString(R.string.ppm));

        //Mg
        microDetails Mg=new microDetails(rootchemical,tempPPM.get(2),0);
        TextView Mgppm=findViewById(R.id.Mgppm);
        Mgppm.setText(Double.toString(Mg.calcPPM(dosageingrames))+getResources().getString(R.string.ppm));

        //Zn
        microDetails Zn=new microDetails(rootchemical,tempPPM.get(3),0);
        TextView Znppm=findViewById(R.id.Znppm);
        Znppm.setText(Double.toString(Zn.calcPPM(dosageingrames))+getResources().getString(R.string.ppm));

        //Cu
        microDetails Cu=new microDetails(rootchemical,tempPPM.get(4),0);
        TextView Cuppm=findViewById(R.id.Cuppm);
        Cuppm.setText(Double.toString(Cu.calcPPM(dosageingrames))+getResources().getString(R.string.ppm));

        //Mo
        microDetails Mo=new microDetails(rootchemical,tempPPM.get(5),0);
        TextView Moppm=findViewById(R.id.Moppm);
        Moppm.setText(Double.toString(Mo.calcPPM(dosageingrames))+getResources().getString(R.string.ppm));

        //B
        microDetails B=new microDetails(rootchemical,tempPPM.get(6),0);
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

    public void setpercentBox(ArrayList<Double> tempPPM){


        TinyDB notDef= new TinyDB(this);


        EditText percentInput = findViewById(R.id.Fepercent);
        percentInput.setText(tempPPM.get(0).toString());

        percentInput = findViewById(R.id.Mnpercent);
        percentInput.setText(tempPPM.get(1).toString());

        percentInput = findViewById(R.id.Mgpercent);
        percentInput.setText(tempPPM.get(2).toString());

        percentInput = findViewById(R.id.Znpercent);
        percentInput.setText(tempPPM.get(3).toString());

        percentInput = findViewById(R.id.Cupercent);
        percentInput.setText(tempPPM.get(4).toString());

        percentInput = findViewById(R.id.Mopercent);
        percentInput.setText(tempPPM.get(5).toString());

        percentInput = findViewById(R.id.Bpercent);
        percentInput.setText(tempPPM.get(6).toString());
        

        notDef.putBoolean("macroNotDef",true);

    }

    private void addUserInputPPMToArray(String ppmSetName,ArrayList<Double> tempPPM) {




        EditText percentInput = findViewById(R.id.Fepercent);
        if (!(percentInput.getText().toString().isEmpty())) {
            tempPPM.set(0, Double.parseDouble(percentInput.getText().toString()));
        }
        percentInput = findViewById(R.id.Mnpercent);
        if (!(percentInput.getText().toString().isEmpty())) {
            tempPPM.set(1, Double.parseDouble(percentInput.getText().toString()));
        }
        percentInput = findViewById(R.id.Mgpercent);
        if (!(percentInput.getText().toString().isEmpty())) {
            tempPPM.set(2, Double.parseDouble(percentInput.getText().toString()));
        }
        percentInput = findViewById(R.id.Znpercent);
        if (!(percentInput.getText().toString().isEmpty())) {
            tempPPM.set(3, Double.parseDouble(percentInput.getText().toString()));
        }
        percentInput = findViewById(R.id.Cupercent);
        if (!(percentInput.getText().toString().isEmpty())) {
            tempPPM.set(4, Double.parseDouble(percentInput.getText().toString()));
        }
        percentInput = findViewById(R.id.Mopercent);
        if (!(percentInput.getText().toString().isEmpty())) {
            tempPPM.set(5, Double.parseDouble(percentInput.getText().toString()));
        }
        percentInput = findViewById(R.id.Bpercent);
        if (!(percentInput.getText().toString().isEmpty())) {
            tempPPM.set(6, Double.parseDouble(percentInput.getText().toString()));
        }
        TinyDB ppmlist= new TinyDB(this);
        ppmlist.putListDouble(ppmSetName,tempPPM);
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



    private String percentSetName = "";

    public void saveMicroSet(View view) {

        final EditText enterSetName = new EditText(this);
        AlertDialog.Builder setNameInputDialog = new AlertDialog.Builder(this);
        setNameInputDialog.setView(enterSetName)
                .setMessage("Enter a name for the Custom PPM profile")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ArrayList<Double> tempPPM = new ArrayList<>(Arrays.asList(6.53d, 1.87d, 1.4d, 0.37d, 0.09d, 0.05d, 1.18d));
                        percentSetName = enterSetName.getText().toString();
                        addUserInputPPMToArray(percentSetName,tempPPM);
                        percentAll.add(tempPPM);
                        allSets.add(percentSetName);
                        presetAdapter.notifyDataSetChanged();
                        presetSpinner.setSelection(percentAll.size()-1);
                        addSets();
                    }
                })
                .create().show();
    }

    public void deleteMicroSet(View view) {

        int position = presetSpinner.getSelectedItemPosition();
        String removePPMsetName = presetSpinner.getSelectedItem().toString();
        percentAll.remove(position);
        allSets.remove(removePPMsetName);
        presetAdapter.notifyDataSetChanged();
        removeSets(removePPMsetName);
        addSets();


    }
}
