package com.newage.plantedaqua.activities;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.HashMap;
import java.util.Locale;

import androidx.fragment.app.FragmentManager;

import com.newage.plantedaqua.models.Chemical;
import com.newage.plantedaqua.dbhelpers.MyDbHelper;
import com.newage.plantedaqua.fragments.NodayDialog;
import com.newage.plantedaqua.dbhelpers.NutrientDbHelper;
import com.newage.plantedaqua.R;
import com.newage.plantedaqua.dbhelpers.TankDBHelper;
import com.newage.plantedaqua.helpers.TinyDB;

public class MacroNutrientTableActivity extends AppCompatActivity {

    private double liqDoseRatio=1d;
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
    private TextView setRecommended;
    private EditText tankvolume;
    private RadioGroup VolumeUnit;
    private double frequency;
    private EditText frequencybox;

    private void addInitialPPMValuesToDB() {


        TinyDB ppmDB = new TinyDB(this);
        ppmDB.putListDouble("Custom values",new ArrayList <>(Arrays.asList(0d, 0d, 0d, 0d, 0d)));
        ppmDB.putListDouble("Default values",new ArrayList <>(Arrays.asList(20d, 3d, 30d, 10d, 30d)));
        ppmDB.putListDouble("EI with Green Spot Algae Issue",new ArrayList <>(Arrays.asList(25d, 6d, 35d, 15d, 35d)));
        ppmDB.putListDouble("EI Low Tech",new ArrayList <>(Arrays.asList(7d, 2d, 10d, 3d, 10d)));

    }

    private ArrayAdapter<String> presetAdapter;
    private double volume = 0d;
    private String volumeMetric = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrient_table);

        setRecommended = findViewById(R.id.SetRecommendedMacro);

        tankvolume = findViewById(R.id.TankVolumeBox);

        VolumeUnit = findViewById(R.id.VolumeUnit);

        frequencybox = findViewById(R.id.FrequencyBox);

        Intent intent = getIntent();
        aquariumid=intent.getStringExtra("AquariumID");


        if(aquariumid!=null) {
            mydbhelper = MyDbHelper.newInstance(this, aquariumid);
            SQLiteDatabase db = mydbhelper.getReadableDatabase();
            Cursor c = mydbhelper.getDataAN2Condition(db,"Category","Dosing","AlarmName","Macro");
            if(c.moveToFirst())
                if(!c.getString(6).equals("0"))
                    frequencybox.setText(c.getString(6));
            c.close();

            TankDBHelper tankDBHelper = TankDBHelper.newInstance(this);
            c = tankDBHelper.getDataCondition("AquariumID", aquariumid);
            if (c.moveToFirst()) {

                volume = c.getString(7).equals("")?0 : Double.parseDouble(c.getString(7).replace(",","."));
                volumeMetric = c.getString(8);
                c.close();


                if (volumeMetric.equals("Litre")) {
                    VolumeUnit.check(R.id.LitreBox);
                } else {
                    VolumeUnit.check(R.id.GallonBox);
                    if (volumeMetric.equals("UK Gallon")) {
                        volume = Math.round(volume * 120d)/100d;
                    }


                }

                tankvolume.setText(String.format(Locale.getDefault(),"%.2f",volume));
            }
            c.close();
        }




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

                Intent intent = new Intent(MacroNutrientTableActivity.this, MacroNutrientTableActivity.class);
                intent.putExtra("AquariumID",aquariumid);
                finish();
                startActivity(intent);
            }
        });


        if(aquariumid!=null) {
            setRecommended.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   generateAlarmText();

                        TankDBHelper tankDBHelper = TankDBHelper.newInstance(MacroNutrientTableActivity.this);
                        tankDBHelper.updateSingleItem("AquariumID",aquariumid,"MacroDosage",String.format(Locale.getDefault(),"%.2f",frequency)+" time(s) per week.\n"+alarmDetails);
                        Toast.makeText(MacroNutrientTableActivity.this, "Current dosing regimen is set as recommended", Toast.LENGTH_LONG).show();


                    NutrientDbHelper nutrientDbHelper = NutrientDbHelper.newInstance(MacroNutrientTableActivity.this,aquariumid);
                    Cursor cursor = nutrientDbHelper.getDataAD("NutrientType","Macro");
                    if(!cursor.moveToFirst()){
                        nutrientDbHelper.addDataAD(Double.toString(volume), volumeMetric, String.format(Locale.getDefault(),"%.2f",frequency), dosage_type, stockVolume, doseVolume, "Macro", "", "");
                    }
                    else {
                        nutrientDbHelper.updateDataAD(Double.toString(volume), volumeMetric, String.format(Locale.getDefault(),"%.2f",frequency), dosage_type, stockVolume, doseVolume, "Macro", "", "");
                    }
                    cursor.close();

                    cursor = nutrientDbHelper.getDataMAD();
                    if(cursor.moveToFirst()){
                        nutrientDbHelper.updateDataMaD(preferredChemical.get("K"),preferredChemical.get("NO3"),preferredChemical.get("PO4"),preferredChemical.get("Ca"),preferredChemical.get("Mg"),"",kPPM,nPPM,pPPM,caPPM,mgPM,sPPM,String.format(Locale.getDefault(),"%.2f",weights[2]),String.format(Locale.getDefault(),"%.2f",weights[0]),String.format(Locale.getDefault(),"%.2f",weights[1]),String.format(Locale.getDefault(),"%.2f",weights[4]),String.format(Locale.getDefault(),"%.2f",weights[3]),"");
                    }
                    else{
                        nutrientDbHelper.addDataMaD(preferredChemical.get("K"),preferredChemical.get("NO3"),preferredChemical.get("PO4"),preferredChemical.get("Ca"),preferredChemical.get("Mg"),"",kPPM,nPPM,pPPM,caPPM,mgPM,sPPM,String.format(Locale.getDefault(),"%.2f",weights[2]),String.format(Locale.getDefault(),"%.2f",weights[0]),String.format(Locale.getDefault(),"%.2f",weights[1]),String.format(Locale.getDefault(),"%.2f",weights[4]),String.format(Locale.getDefault(),"%.2f",weights[3]),"");

                    }
                    cursor.close();
                }
            });
        }
        else{
            setRecommended.setVisibility(View.GONE);
        }

        setppmBox(ppmAll.get(0));



        ArrayList<String> NO3list=new ArrayList <>();
        NO3list.add("KNO3");
        NO3list.add("none");

        Spinner chemicalSpinner=findViewById(R.id.NO3Spinner);
        ArrayAdapter<String> adapter= new ArrayAdapter <>(this,R.layout.spinner_macrotable,NO3list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chemicalSpinner.setAdapter(adapter);

        ArrayList<String> PO4list=new ArrayList <>();
        PO4list.add("KH2PO4");
        PO4list.add("none");

        chemicalSpinner=findViewById(R.id.PO4Spinner);
        adapter= new ArrayAdapter <>(this,R.layout.spinner_macrotable,PO4list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chemicalSpinner.setAdapter(adapter);

        ArrayList<String> Klist=new ArrayList <>();
        Klist.add("K2SO4");
        Klist.add("KHCO3");
        Klist.add("none");

        chemicalSpinner=findViewById(R.id.K2Spinner);
        adapter= new ArrayAdapter <>(this,R.layout.spinner_macrotable,Klist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chemicalSpinner.setAdapter(adapter);

        ArrayList<String> Mglist=new ArrayList <>();
        Mglist.add("MgSO4.7H2O");
        Mglist.add("MgCl2");
        Mglist.add("MgCl2.6H2O");
        Mglist.add("none");

        chemicalSpinner=findViewById(R.id.MgSpinner);
        adapter= new ArrayAdapter <>(this,R.layout.spinner_macrotable,Mglist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chemicalSpinner.setAdapter(adapter);

        ArrayList<String> Calist=new ArrayList <>();
        Calist.add("CaCl2");
        Calist.add("CaCl2.2H2O");
        Calist.add("CaCO3");
        Calist.add("none");


        chemicalSpinner=findViewById(R.id.CaSpinner);
        adapter= new ArrayAdapter <>(this,R.layout.spinner_macrotable,Calist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chemicalSpinner.setAdapter(adapter);


        RadioGroup DoseButton =findViewById(R.id.DoseType);
        DoseButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                LinearLayout layout =findViewById(R.id.LiquidDose);
                RadioButton dosetype=findViewById(checkedId);

                if (dosetype.getText().toString().equals("Liquid Dose")){


                    for (int i = 0; i < layout.getChildCount()-2; i++) {
                        View child = layout.getChildAt(i);
                        child.setEnabled(true);
                        TextView quantity=findViewById(R.id.target);
                        quantity.setText("Add to stock");

                    }

                }

                else{

                    for (int i = 0; i < layout.getChildCount()-2; i++) {
                        View child = layout.getChildAt(i);
                        child.setEnabled(false);
                        liqDoseRatio=1d;
                        TextView quantity=findViewById(R.id.target);
                        quantity.setText("Weekly Target");

                    }

                }

                Button notifyMe=findViewById(R.id.AlarmSalt);
                notifyMe.setVisibility(View.INVISIBLE);
                notifyMe.setEnabled(false);
                setRecommended.setVisibility(View.GONE);

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

    private String stockVolume = "", doseVolume = "";

    private void Calculate() {


        RadioButton selectedradiobutton;

        RadioGroup dosageType = findViewById(R.id.DoseType);
        selectedradiobutton = findViewById(dosageType.getCheckedRadioButtonId());


        FragmentManager fragmentManager = getSupportFragmentManager();
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

    private HashMap<String,String> preferredChemical = new HashMap<>();
    private String dosage_type = "";

    private void mainCalculate() {

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
        rootchemical.put("none",0.0);


        double volume;
        frequency = 1d;
        double KinKNO3ppm=0d;
        double KinKH2PO4ppm=0d;





        RadioButton selectedradiobutton;

        RadioGroup dosageType = findViewById(R.id.DoseType);
        selectedradiobutton = findViewById(dosageType.getCheckedRadioButtonId());

        volume = Double.parseDouble(tankvolume.getText().toString().replace(",","."));

        dosage_type = selectedradiobutton.getText().toString();

        if (dosage_type.equals("Liquid Dose")) {

            EditText stockvolume = findViewById(R.id.StockBox);
            EditText dosevolume = findViewById(R.id.DoseBox);
            stockVolume =stockvolume.getText().toString().replace(",",".");
            doseVolume =dosevolume.getText().toString().replace(",",".");
            liqDoseRatio = Double.parseDouble(stockVolume) / Double.parseDouble(doseVolume);
            alarmDetails="Dose "+doseVolume+" ml of stock solution\n";
        } else {


            liqDoseRatio = 1d;
            alarmDetails="";
        }



        if (!(frequencybox.getText().toString().isEmpty())) {
            frequency = Double.parseDouble(frequencybox.getText().toString().replace(",","."));

        }


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
        preferredChemical.put("NO3",spinner.getSelectedItem().toString());
        double dosageingrames = NO3.calcDosage();
        TextView dosage = findViewById(R.id.GKNO3);
        dosage.setText(String.format(Locale.getDefault(),"%.2f",Math.round(dosageingrames * liqDoseRatio * 100d) / 100d));
        weights[0]=Math.round(dosageingrames * liqDoseRatio * 100d) / 100d;

        double ppm = NO3.calcPPM(dosageingrames);
        nPPM = String.format(Locale.getDefault(),"%.2f",ppm);
        TextView dosageppm = findViewById(R.id.NO3KNO3);
        dosageppm.setText(nPPM);

        //K in KNO3

        if(spinner.getSelectedItem().toString().equals("KNO3")){

            Chemical KinKNO3 = new Chemical(rootchemical.get(spinner.getSelectedItem().toString()), 39.0983, 30);
            KinKNO3ppm = KinKNO3.calcPPM(dosageingrames);

        }

        dosageppm = findViewById(R.id.KKNO3);
        dosageppm.setText(String.format(Locale.getDefault(),"%.2f",KinKNO3ppm));

        //PO4
        spinner=findViewById(R.id.PO4Spinner);
        Chemical PO4 = new Chemical(rootchemical.get(spinner.getSelectedItem().toString()), 94.9714, tempPPM.get(1) / frequency);
        preferredChemical.put("PO4",spinner.getSelectedItem().toString());
        dosageingrames = PO4.calcDosage();
        dosage = findViewById(R.id.GKH2PO4);
        dosage.setText(String.format(Locale.getDefault(),"%.2f",Math.round(dosageingrames * liqDoseRatio * 100d) / 100d));
        weights[1]=Math.round(dosageingrames * liqDoseRatio * 100d) / 100d;

        ppm = PO4.calcPPM(dosageingrames);
        pPPM = String.format(Locale.getDefault(),"%.2f",ppm);
        dosageppm = findViewById(R.id.PO4KH2PO4);
        dosageppm.setText(pPPM);


        //K in KH2PO4

        if(spinner.getSelectedItem().toString().equals("KH2PO4")) {

            Chemical KinKH2PO4 = new Chemical(rootchemical.get(spinner.getSelectedItem().toString()), 39.0983, 30);
            KinKH2PO4ppm = KinKH2PO4.calcPPM(dosageingrames);
        }
        dosageppm = findViewById(R.id.KKH2PO4);
        dosageppm.setText(String.format(Locale.getDefault(),"%.2f",KinKH2PO4ppm));

        Double KinOtherSaltsppm = KinKH2PO4ppm + KinKNO3ppm;

        //K
        spinner=findViewById(R.id.K2Spinner);

        double Kppm = (tempPPM.get(2) / frequency) - KinOtherSaltsppm;
        Chemical K = new Chemical(rootchemical.get(spinner.getSelectedItem().toString()), 78.1966, Kppm);
        preferredChemical.put("K",spinner.getSelectedItem().toString());
        dosageingrames = K.calcDosage();
        dosage = findViewById(R.id.GK2SO4);
        dosage.setText(String.format(Locale.getDefault(),"%.2f",Math.round(dosageingrames * liqDoseRatio * 100d) / 100d));
        weights[2]=Math.round(dosageingrames * liqDoseRatio * 100d) / 100d;

        ppm = K.calcPPM(dosageingrames);
        dosageppm = findViewById(R.id.KK2SO4);
        dosageppm.setText(String.format(Locale.getDefault(),"%.2f",ppm));
        kPPM = String.format(Locale.getDefault(),"%.2f",ppm+KinOtherSaltsppm);

        //Mg
        spinner=findViewById(R.id.MgSpinner);
        Chemical Mg = new Chemical(rootchemical.get(spinner.getSelectedItem().toString()), 24.305, tempPPM.get(3) / frequency);
        preferredChemical.put("Mg",spinner.getSelectedItem().toString());
        dosageingrames = Mg.calcDosage();
        dosage = findViewById(R.id.GMgSO4);
        dosage.setText(String.format(Locale.getDefault(),"%.2f",Math.round(dosageingrames * liqDoseRatio * 100d) / 100d));
        weights[3]=Math.round(dosageingrames * liqDoseRatio * 100d) / 100d;

        ppm = Mg.calcPPM(dosageingrames);
        mgPM = String.format(Locale.getDefault(),"%.2f",ppm);
        dosageppm = findViewById(R.id.MgMgSO4);
        dosageppm.setText(mgPM);

        //Ca

        spinner=findViewById(R.id.CaSpinner);
        Chemical Ca = new Chemical(rootchemical.get(spinner.getSelectedItem().toString()), 40.08, tempPPM.get(4) / frequency);
        preferredChemical.put("Ca",spinner.getSelectedItem().toString());
        dosageingrames = Ca.calcDosage();
        dosage = findViewById(R.id.GCaCl2);
        dosage.setText(String.format(Locale.getDefault(),"%.2f",Math.round(dosageingrames * liqDoseRatio * 100d) / 100d));
        weights[4]=Math.round(dosageingrames * liqDoseRatio * 100d) / 100d;

        ppm = Ca.calcPPM(dosageingrames);
        caPPM = String.format(Locale.getDefault(),"%.2f",ppm);
        dosageppm = findViewById(R.id.CaCa);
        dosageppm.setText(caPPM);



        setNutAlarmButton();

    }

    private String kPPM="";
    private String nPPM="";
    private String pPPM="";
    private String caPPM="";
    private String mgPM="";
    private String sPPM="";


    private void addUserInputPPMToArray(String ppmSetName,ArrayList<Double> tempPPM) {




        EditText ppmInput = findViewById(R.id.NO3ppm);
        if (!(ppmInput.getText().toString().isEmpty())) {
            tempPPM.set(0, Double.parseDouble(ppmInput.getText().toString().replace(",",".")));
        }
        ppmInput = findViewById(R.id.PO4ppm);
        if (!(ppmInput.getText().toString().isEmpty())) {
            tempPPM.set(1, Double.parseDouble(ppmInput.getText().toString().replace(",",".")));
        }
        ppmInput = findViewById(R.id.Kppm);
        if (!(ppmInput.getText().toString().isEmpty())) {
            tempPPM.set(2, Double.parseDouble(ppmInput.getText().toString().replace(",",".")));
        }
        ppmInput = findViewById(R.id.Mgppm);
        if (!(ppmInput.getText().toString().isEmpty())) {
            tempPPM.set(3, Double.parseDouble(ppmInput.getText().toString().replace(",",".")));
        }
        ppmInput = findViewById(R.id.Cappm);
        if (!(ppmInput.getText().toString().isEmpty())) {
            tempPPM.set(4, Double.parseDouble(ppmInput.getText().toString().replace(",",".")));
        }
        TinyDB ppmlist= new TinyDB(this);
        ppmlist.putListDouble(ppmSetName,tempPPM);
    }

    private void setNutAlarmButton(){

        if(aquariumid!=null) {

            Button notifyMe = findViewById(R.id.AlarmSalt);
            notifyMe.setVisibility(View.VISIBLE);
            notifyMe.setEnabled(true);


            notifyMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setNutalarmText();

                }
            });

            setRecommended.setVisibility(View.VISIBLE);


        }

    }
    private void setNutalarmText(){


        int checked;
        alarmDetails = "";

        AlertDialog.Builder noFertBuilder = new AlertDialog.Builder(this);
        noFertBuilder.setMessage(getResources().getString(R.string.NoFert))
                .setNeutralButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();

        if(aquariumid!=null) {



            SQLiteDatabase dbNL = mydbhelper.getWritableDatabase();
            Cursor cNL = mydbhelper.getDataANCondition(dbNL, "Category", "Dosing");

            ArrayList<String> nutrientList = new ArrayList<>();

            if (cNL.moveToFirst()) {
                do {
                    nutrientList.add(cNL.getString(0));
                } while (cNL.moveToNext());

            }
            cNL.close();

            if (nutrientList.isEmpty()) {

                noFertBuilder.show();

            }
            else {

                generateAlarmText();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MacroNutrientTableActivity.this);
                    View view = getLayoutInflater().inflate(R.layout.select, null);
                    final Spinner choose = view.findViewById(R.id.choose);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MacroNutrientTableActivity.this, android.R.layout.simple_spinner_item, nutrientList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    choose.setAdapter(adapter);
                    builder.setTitle("Select Alarm...");
                    builder.setView(view);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            nutrient = choose.getSelectedItem().toString();
                            addNutrientDosing("You will be notified about this dosage");
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
        else {

            noFertBuilder.show();
        }



    }

    private void generateAlarmText(){

       /* CheckBox KNO3= findViewById(R.id.gNO3);
        CheckBox KH2PO4= findViewById(R.id.gPO4);
        CheckBox K2SO4= findViewById(R.id.gK);
        CheckBox MgSO4= findViewById(R.id.gMg);
        CheckBox CaCl2= findViewById(R.id.gCa);*/

        //int checked = 0;


        RadioGroup dosageType = findViewById(R.id.DoseType);
        RadioButton selectedradiobutton = findViewById(dosageType.getCheckedRadioButtonId());

        if (selectedradiobutton.getText().toString().equals("Liquid Dose")) {

            EditText dosevolume = findViewById(R.id.DoseBox);
            alarmDetails = "Dose " + dosevolume.getText().toString() + " ml of stock solution\n";
        } else {
            alarmDetails = "";


            if (!preferredChemical.get("NO3").equals("none")) {

                alarmDetails = alarmDetails + "Dose " + String.format(Locale.getDefault(),"%.2f",weights[0]) + " grams of "+preferredChemical.get("NO3")+"\n";
                //checked++;
            }else{

                weights[0]=0d;

            }
            if (!preferredChemical.get("PO4").equals("none")) {

                alarmDetails = alarmDetails + "Dose " + String.format(Locale.getDefault(),"%.2f",weights[1]) + " grams of "+preferredChemical.get("PO4")+"\n";
                //checked++;
            }
            else{

                weights[1]=0d;

            }

            if (!preferredChemical.get("K").equals("none")) {

                alarmDetails = alarmDetails + "Dose " + String.format(Locale.getDefault(),"%.2f",weights[2]) + " grams of "+preferredChemical.get("K")+"\n";
                //checked++;
            }
            else{

                weights[2]=0d;

            }

            if (!preferredChemical.get("Mg").equals("none")) {

                alarmDetails = alarmDetails + "Dose " + String.format(Locale.getDefault(),"%.2f",weights[3]) + " grams of "+preferredChemical.get("Mg")+"\n";
                //checked++;
            }
            else{

                weights[3]=0d;

            }

            if (!preferredChemical.get("Ca").equals("none")) {

                alarmDetails = alarmDetails + "Dose " + String.format(Locale.getDefault(),"%.2f",weights[4]) + " grams of "+preferredChemical.get("Ca")+"\n";
               // checked++;
            }
            else{

                weights[4]=0d;

            }
        }

       // return checked;


    }





       /*
        Cursor c= alarmtext.getData_Dose(db);
        c.moveToFirst();


            System.out.print(c.getString(1));

        }
        c.close();*/

    private void addNutrientDosing(String msg){



        if(aquariumid!=null) {
            mydbhelper.updateItemAN("AlarmName", nutrient, "Category", "Dosing", "AlarmText",alarmDetails);

            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }



    }
    private void setppmBox(ArrayList<Double> tempPPM){


        TinyDB notDef= new TinyDB(this);


        EditText ppmInput = findViewById(R.id.NO3ppm);
        ppmInput.setText(String.format(Locale.getDefault(),"%.2f",tempPPM.get(0)));

        ppmInput = findViewById(R.id.PO4ppm);
        ppmInput.setText(String.format(Locale.getDefault(),"%.2f",tempPPM.get(1)));

        ppmInput = findViewById(R.id.Kppm);
        ppmInput.setText(String.format(Locale.getDefault(),"%.2f",tempPPM.get(2)));

        ppmInput = findViewById(R.id.Mgppm);
        ppmInput.setText(String.format(Locale.getDefault(),"%.2f",tempPPM.get(3)));

        ppmInput = findViewById(R.id.Cappm);
        ppmInput.setText(String.format(Locale.getDefault(),"%.2f",tempPPM.get(4)));

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
