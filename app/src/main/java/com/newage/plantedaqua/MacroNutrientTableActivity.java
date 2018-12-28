package com.newage.plantedaqua;

import android.app.AlertDialog;

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
import android.widget.CheckBox;
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
import java.util.HashMap;

public class MacroNutrientTableActivity extends AppCompatActivity {

    private double liqDoseRatio=1d;
    private boolean liqdosestatus=false;
    private String alarmDetails="";
    private String aquariumid;
    private double weights[]=new double[5];
    private String nutrient;
    private ArrayList<ArrayList<Double>> ppmAll = new ArrayList<>();
    private MyDbHelper mydbhelper;
    private Spinner presetSpinner;
    private ImageView deleteSetButton;
    private ArrayList<String> defaultSets=new ArrayList <>(Arrays.asList("Custom values","Default values", "EI with Green Spot Algae Issue","EI Low Tech"));
    private ArrayList<String> allSets;
    private ImageView saveSetButton;

    private void addInitialPPMValuesToDB() {


        TinyDB ppmDB = new TinyDB(this);
        ppmDB.putListDouble("Custom values",new ArrayList <>(Arrays.asList(0d, 0d, 0d, 0d, 0d)));
        ppmDB.putListDouble("Default values",new ArrayList <>(Arrays.asList(20d, 3d, 30d, 10d, 30d)));
        ppmDB.putListDouble("EI with Green Spot Algae Issue",new ArrayList <>(Arrays.asList(25d, 6d, 35d, 15d, 35d)));
        ppmDB.putListDouble("EI Low Tech",new ArrayList <>(Arrays.asList(7d, 2d, 10d, 3d, 10d)));

    }

    private ArrayAdapter<String> presetAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrient_table);
        addInitialPPMValuesToDB();
        allSets = readSets();

        TinyDB ppmSets = new TinyDB(this);

        for(String ppmSetName:allSets){
            ppmAll.add(ppmSets.getListDouble(ppmSetName));
        }

        deleteSetButton = findViewById(R.id.DeleteSetButton);
        saveSetButton = findViewById(R.id.SaveSetButton);
        presetSpinner = findViewById(R.id.PresetSpinner);
        presetAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,allSets);
        presetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        presetSpinner.setAdapter(presetAdapter);
        presetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if(position<4)
                    deleteSetButton.setVisibility(View.GONE);
                else
                    deleteSetButton.setVisibility(View.VISIBLE);


                if(position==0)
                    saveSetButton.setVisibility(View.VISIBLE);
                else
                    saveSetButton.setVisibility(View.GONE);

                setppmBox(ppmAll.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button reset=findViewById(R.id.resetbuttom);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(MacroNutrientTableActivity.this, MacroNutrientTableActivity.class));
            }
        });

        Intent intent=getIntent();
        aquariumid=intent.getStringExtra("AquariumID");

        setppmBox(ppmAll.get(0));



        ArrayList<String> NO3list=new ArrayList <>();
        NO3list.add("KNO3");

        Spinner chemicalSpinner=findViewById(R.id.NO3Spinner);
        ArrayAdapter<String> adapter= new ArrayAdapter <>(this,R.layout.spinner_macrotable,NO3list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chemicalSpinner.setAdapter(adapter);

        ArrayList<String> PO4list=new ArrayList <>();
        PO4list.add("KH2PO4");

        chemicalSpinner=findViewById(R.id.PO4Spinner);
        adapter= new ArrayAdapter <>(this,R.layout.spinner_macrotable,PO4list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chemicalSpinner.setAdapter(adapter);

        ArrayList<String> Klist=new ArrayList <>();
        Klist.add("K2SO4");
        Klist.add("KHCO3");

        chemicalSpinner=findViewById(R.id.K2Spinner);
        adapter= new ArrayAdapter <>(this,R.layout.spinner_macrotable,Klist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chemicalSpinner.setAdapter(adapter);

        ArrayList<String> Mglist=new ArrayList <>();
        Mglist.add("MgSO4.7H2O");
        Mglist.add("MgCl2");
        Mglist.add("MgCl2.6H2O");

        chemicalSpinner=findViewById(R.id.MgSpinner);
        adapter= new ArrayAdapter <>(this,R.layout.spinner_macrotable,Mglist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chemicalSpinner.setAdapter(adapter);

        ArrayList<String> Calist=new ArrayList <>();
        Calist.add("CaCl2");
        Calist.add("CaCl2.2H2O");
        Calist.add("CaCO3");


        chemicalSpinner=findViewById(R.id.CaSpinner);
        adapter= new ArrayAdapter <>(this,R.layout.spinner_macrotable,Calist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chemicalSpinner.setAdapter(adapter);


        RadioGroup DoseButton =findViewById(R.id.DoseType);
        DoseButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                RadioButton dosetype=findViewById(checkedId);
                if (dosetype.getText().toString().equals("Liquid Dose")){

                    LinearLayout layout =findViewById(R.id.LiquidDose);
                    for (int i = 0; i < layout.getChildCount()-2; i++) {
                        View child = layout.getChildAt(i);
                        child.setEnabled(true);
                        TextView quantity=findViewById(R.id.target);
                        quantity.setText("Add to stock");

                    }
                    Button notifyMe=findViewById(R.id.AlarmSalt);
                    notifyMe.setVisibility(View.INVISIBLE);
                    notifyMe.setEnabled(false);
                }

                else{

                    LinearLayout layout =findViewById(R.id.LiquidDose);
                    for (int i = 0; i < layout.getChildCount()-2; i++) {
                        View child = layout.getChildAt(i);
                        child.setEnabled(false);
                        liqDoseRatio=1d;
                        TextView quantity=findViewById(R.id.target);
                        quantity.setText("Weekly Target");

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

    private void addSets() {

        TinyDB setDB = new TinyDB(this);
        setDB.putListString("AllSetMacro",allSets);

    }
    private void removeSets(String setToRemove) {

        TinyDB setDB = new TinyDB(this);
        setDB.remove(setToRemove);

    }
    private ArrayList<String> readSets() {

        TinyDB setDB = new TinyDB(this);

        if ((setDB.getListString("AllSetMacro")).isEmpty())
            return defaultSets;

        return(setDB.getListString("AllSetMacro"));

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

        HashMap<String,Double> rootchemical=new HashMap<>();

        rootchemical.put("KNO3",101.1032);
        rootchemical.put("KH2PO4",136.0855);
        rootchemical.put("K2SO4",174.2592);
        rootchemical.put("MgSO4.7H2O",246.474);
        rootchemical.put("CaCl2",110.98);
        rootchemical.put("CaCO3",100.0869);
        rootchemical.put("CaCl2.2H2O",147.0146);
        rootchemical.put("MgCl2.6H2O",203.3027);
        rootchemical.put("MgCl2",95.211);
        rootchemical.put("KHCO3",100.115);


        double volume;
        double frequency = 1d;
        double KinKNO3ppm=0d;
        double KinKH2PO4ppm=0d;




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
            Chemical.volumeinltrs = 3.78541 * volume;
           // Toast.makeText(this, Double.toString(Chemical.volumeinltrs), Toast.LENGTH_SHORT).show();

        } else {
            Chemical.volumeinltrs = volume;
        }

        //USER INPUT PPM TO ARRAY

        ArrayList<Double> tempPPM = new ArrayList<>(Arrays.asList(20d, 3d, 30d, 10d, 30d));

        addUserInputPPMToArray("Custom Values",tempPPM);

        //NO3
        Spinner spinner=findViewById(R.id.NO3Spinner);
        Chemical NO3 = new Chemical(rootchemical.get(spinner.getSelectedItem().toString()), 62.0049, tempPPM.get(0)/ frequency);
        double dosageingrames = NO3.calcDosage();
        TextView dosage = findViewById(R.id.GKNO3);
        dosage.setText(Double.toString(Math.round(dosageingrames * liqDoseRatio * 100d) / 100d));
        weights[0]=Math.round(dosageingrames * liqDoseRatio * 100d) / 100d;

        double ppm = NO3.calcPPM(dosageingrames);
        TextView dosageppm = findViewById(R.id.NO3KNO3);
        dosageppm.setText(Double.toString(ppm));

        if(spinner.getSelectedItem().toString().equals("KNO3")){

            Chemical KinKNO3 = new Chemical(rootchemical.get(spinner.getSelectedItem().toString()), 39.0983, 30);
            KinKNO3ppm = KinKNO3.calcPPM(dosageingrames);

        }

        dosageppm = findViewById(R.id.KKNO3);
        dosageppm.setText(Double.toString(KinKNO3ppm));

        //PO4
        spinner=findViewById(R.id.PO4Spinner);
        Chemical PO4 = new Chemical(rootchemical.get(spinner.getSelectedItem().toString()), 94.9714, tempPPM.get(1) / frequency);
        dosageingrames = PO4.calcDosage();
        dosage = findViewById(R.id.GKH2PO4);
        dosage.setText(Double.toString(Math.round(dosageingrames * liqDoseRatio * 100d) / 100d));
        weights[1]=Math.round(dosageingrames * liqDoseRatio * 100d) / 100d;

        ppm = PO4.calcPPM(dosageingrames);
        dosageppm = findViewById(R.id.PO4KH2PO4);
        dosageppm.setText(Double.toString(ppm));

        if(spinner.getSelectedItem().toString().equals("KH2PO4")) {

            Chemical KinKH2PO4 = new Chemical(rootchemical.get(spinner.getSelectedItem().toString()), 39.0983, 30);
            KinKH2PO4ppm = KinKH2PO4.calcPPM(dosageingrames);
        }
        dosageppm = findViewById(R.id.KKH2PO4);
        dosageppm.setText(Double.toString(KinKH2PO4ppm));


        //K
        spinner=findViewById(R.id.K2Spinner);

        double Kppm = (tempPPM.get(2) / frequency) - KinKH2PO4ppm - KinKNO3ppm;
        Chemical K = new Chemical(rootchemical.get(spinner.getSelectedItem().toString()), 78.1966, Kppm);
        dosageingrames = K.calcDosage();
        dosage = findViewById(R.id.GK2SO4);
        dosage.setText(Double.toString(Math.round(dosageingrames * liqDoseRatio * 100d) / 100d));
        weights[2]=Math.round(dosageingrames * liqDoseRatio * 100d) / 100d;

        ppm = K.calcPPM(dosageingrames);
        dosageppm = findViewById(R.id.KK2SO4);
        dosageppm.setText(Double.toString(ppm));

        //Mg
        spinner=findViewById(R.id.MgSpinner);
        Chemical Mg = new Chemical(rootchemical.get(spinner.getSelectedItem().toString()), 24.305, tempPPM.get(3) / frequency);
        dosageingrames = Mg.calcDosage();
        dosage = findViewById(R.id.GMgSO4);
        dosage.setText(Double.toString(Math.round(dosageingrames * liqDoseRatio * 100d) / 100d));
        weights[3]=Math.round(dosageingrames * liqDoseRatio * 100d) / 100d;

        ppm = Mg.calcPPM(dosageingrames);
        dosageppm = findViewById(R.id.MgMgSO4);
        dosageppm.setText(Double.toString(ppm));

        //Ca

        spinner=findViewById(R.id.CaSpinner);
        Chemical Ca = new Chemical(rootchemical.get(spinner.getSelectedItem().toString()), 40.08, tempPPM.get(4) / frequency);
        dosageingrames = Ca.calcDosage();
        dosage = findViewById(R.id.GCaCl2);
        dosage.setText(Double.toString(Math.round(dosageingrames * liqDoseRatio * 100d) / 100d));
        weights[4]=Math.round(dosageingrames * liqDoseRatio * 100d) / 100d;

        ppm = Ca.calcPPM(dosageingrames);
        dosageppm = findViewById(R.id.CaCa);
        dosageppm.setText(Double.toString(ppm));



        setNutAlarmButton();

    }

    private void addUserInputPPMToArray(String ppmSetName,ArrayList<Double> tempPPM) {




        EditText ppmInput = findViewById(R.id.NO3ppm);
        if (!(ppmInput.getText().toString().isEmpty())) {
            tempPPM.set(0, Double.parseDouble(ppmInput.getText().toString()));
        }
        ppmInput = findViewById(R.id.PO4ppm);
        if (!(ppmInput.getText().toString().isEmpty())) {
            tempPPM.set(1, Double.parseDouble(ppmInput.getText().toString()));
        }
        ppmInput = findViewById(R.id.Kppm);
        if (!(ppmInput.getText().toString().isEmpty())) {
            tempPPM.set(2, Double.parseDouble(ppmInput.getText().toString()));
        }
        ppmInput = findViewById(R.id.Mgppm);
        if (!(ppmInput.getText().toString().isEmpty())) {
            tempPPM.set(3, Double.parseDouble(ppmInput.getText().toString()));
        }
        ppmInput = findViewById(R.id.Cappm);
        if (!(ppmInput.getText().toString().isEmpty())) {
            tempPPM.set(4, Double.parseDouble(ppmInput.getText().toString()));
        }
        TinyDB ppmlist= new TinyDB(this);
        ppmlist.putListDouble(ppmSetName,tempPPM);
    }

    void setNutAlarmButton(){

        Button notifyMe=findViewById(R.id.AlarmSalt);
        notifyMe.setVisibility(View.VISIBLE);
        notifyMe.setEnabled(true);

        notifyMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setNutalarmText();

            }
        });




    }
    void setNutalarmText(){

        CheckBox KNO3= findViewById(R.id.gNO3);
        CheckBox KH2PO4= findViewById(R.id.gPO4);
        CheckBox K2SO4= findViewById(R.id.gK);
        CheckBox MgSO4= findViewById(R.id.gMg);
        CheckBox CaCl2= findViewById(R.id.gCa);
        int checked=0;
        alarmDetails="";

        mydbhelper= MyDbHelper.newInstance(this,aquariumid);
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
        }else{



        //INCORPORATE LIQUID DOSAGE IN ALARM TEXT
        RadioGroup dosageType = findViewById(R.id.DoseType);
        RadioButton selectedradiobutton = findViewById(dosageType.getCheckedRadioButtonId());

        if (selectedradiobutton.getText().toString().equals("Liquid Dose")) {

            EditText dosevolume = findViewById(R.id.DoseBox);
            liqdosestatus=true;
            alarmDetails="Dose "+dosevolume.getText().toString()+" ml of stock solution\n";
        }
        else {
                alarmDetails = "";
                liqdosestatus=false;


            if (KNO3.isChecked()) {

                alarmDetails = alarmDetails + "Dose " + weights[0] + " grams of KNO3\n";
                checked++;
            }
            if (KH2PO4.isChecked()) {

                alarmDetails = alarmDetails + "Dose " + weights[1] + " grams of KH2PO4\n";
                checked++;
            }

            if (K2SO4.isChecked()) {

                alarmDetails = alarmDetails + "Dose " + weights[2] + " grams of K2SO4\n";
                checked++;
            }

            if (MgSO4.isChecked()) {

                alarmDetails = alarmDetails + "Dose " + weights[3] + " grams of MgSO4.7H2O\n";
                checked++;
            }

            if (CaCl2.isChecked()) {

                alarmDetails = alarmDetails + "Dose " + weights[4] + " grams of CaCl2\n";
                checked++;
            }
        }

        if (checked==0 && !liqdosestatus){
            Toast.makeText(this,"Please select a salt",Toast.LENGTH_LONG).show();

        }
        else {

                AlertDialog.Builder builder = new AlertDialog.Builder(MacroNutrientTableActivity.this);
                View view = getLayoutInflater().inflate(R.layout.select, null);
                final Spinner choose = view.findViewById(R.id.choose);
                ArrayAdapter <String> adapter = new ArrayAdapter <>(MacroNutrientTableActivity.this, android.R.layout.simple_spinner_item, nutrientList);
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


        }




       /*
        Cursor c= alarmtext.getData_Dose(db);
        c.moveToFirst();


            System.out.print(c.getString(1));

        }
        c.close();*/

    public void addNutrientDosing(){


        mydbhelper.updateItemAN("AlarmName",nutrient,"Category","Dosing","AlarmText", alarmDetails);
        Toast.makeText(this, "You will be notified about this dosage", Toast.LENGTH_SHORT).show();



    }
    public void setppmBox(ArrayList<Double> tempPPM){


        TinyDB notDef= new TinyDB(this);


        EditText ppmInput = findViewById(R.id.NO3ppm);
        ppmInput.setText(tempPPM.get(0).toString());

        ppmInput = findViewById(R.id.PO4ppm);
        ppmInput.setText(tempPPM.get(1).toString());

        ppmInput = findViewById(R.id.Kppm);
        ppmInput.setText(tempPPM.get(2).toString());

        ppmInput = findViewById(R.id.Mgppm);
        ppmInput.setText(tempPPM.get(3).toString());

        ppmInput = findViewById(R.id.Cappm);
        ppmInput.setText(tempPPM.get(4).toString());

        notDef.putBoolean("macroNotDef",true);

    }

    private String ppmSetName = "";

    public void saveSet(View view) {

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

                        ArrayList<Double> tempPPM = new ArrayList<>(Arrays.asList(20d, 3d, 30d, 10d, 30d));
                        ppmSetName = enterSetName.getText().toString();
                        addUserInputPPMToArray(ppmSetName,tempPPM);
                        ppmAll.add(tempPPM);
                        allSets.add(ppmSetName);
                        presetAdapter.notifyDataSetChanged();
                        presetSpinner.setSelection(ppmAll.size()-1);
                        addSets();
                    }
                })
                .create().show();
    }

    public void deleteSet(View view) {

        int position = presetSpinner.getSelectedItemPosition();
        String removePPMsetName = presetSpinner.getSelectedItem().toString();
        ppmAll.remove(position);
        allSets.remove(removePPMsetName);
        presetAdapter.notifyDataSetChanged();
        removeSets(removePPMsetName);
        addSets();


    }



}
