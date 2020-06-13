package com.newage.plantedaqua.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.newage.plantedaqua.adapters.LightRecyclerAdapter;
import com.newage.plantedaqua.helpers.TankDBHelper;
import com.newage.plantedaqua.models.LightDetails;
import com.newage.plantedaqua.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

public class LightCalcActivity extends AppCompatActivity {

    private Spinner Reflector;
    private EditText Efficiency;
    private ImageView addLights;
    private TextView LightZone;
    private TextView estimatedTankVolumeText;
    private RecyclerView.Adapter lightRecyclerAdapter;
    private TankDBHelper tankDBHelper;
    private String aquariumID;
    private ArrayList<LightDetails> lightDetails = new ArrayList<>();
    private ArrayAdapter<String> lightAdapter;
    private CardView otherReflectorCardView;
    private EditText ReflectorName,TankLength,TankWidth,TankHeight,SubstrateDepth,LightHeight;
    private String reflectorName;
    public float efficiency=1f,length=1f,width=1f,height=1f,lightHeight=1f,substrateDepth=1f;
    private float effectiveLumenPerLight=0f,totalLumens=0f;
    private int reflectorPosition = 0;
    private String strEfficiency;
    private String lightZone;
    private String co2="";
    private String aquariumName="";
    private TextView lsiTextView;
    private int position=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_calc);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        aquariumID = getIntent().getStringExtra("AquariumID");
        position = getIntent().getIntExtra("Position",-1);


        tankDBHelper = TankDBHelper.newInstance(this);

        Cursor c = tankDBHelper.getDataCondition("AquariumID", aquariumID);

        String aquaName = "";

        if(c!=null) {

            if (c.moveToFirst()) {

                aquaName = TextUtils.isEmpty(c.getString(2)) ? "" : c.getString(2);


            }

            c.close();
        }

        getSupportActionBar().setTitle("Lights"+" : "+aquaName);


        Reflector = findViewById(R.id.Reflector);
        Efficiency = findViewById(R.id.Efficiency);
        addLights = findViewById(R.id.AddLights);
        otherReflectorCardView = findViewById(R.id.OtherReflectorCardView);
        ReflectorName = findViewById(R.id.ReflectorName);
        TankLength = findViewById(R.id.TankLength);
        TankWidth = findViewById(R.id.TankWidth);
        TankHeight = findViewById(R.id.TankHeight);
        SubstrateDepth = findViewById(R.id.SubstrateDepth);
        LightHeight = findViewById(R.id.SurfaceDistance);
        ImageView deleteAllLightsData = findViewById(R.id.DeleteAllLightData);
        Button recalculateButton = findViewById(R.id.Recalculate);
        lsiTextView = findViewById(R.id.lsi_output);
        estimatedTankVolumeText = findViewById(R.id.EstimatedTankVolume);

        Button calculateLSIButton = findViewById(R.id.calculateLSI);

        Button setTankVolume = findViewById(R.id.UpdateTankGallons);

        LightZone = findViewById(R.id.LightZone);







        setValues();

        setTankData();

        c = tankDBHelper.getDataLightCondition(aquariumID);
        LightDetails lightDetail;


        //LightDetails(id integer,AquariumID text,LightType text,LumensPerWatt text,Count text,WattPerCount text)")
        if(c!=null ){

            if(c.moveToFirst()) {

                do {

                    lightDetail = new LightDetails();
                    lightDetail.setLightType(c.getString(2));
                    lightDetail.setLumensPerWatt(c.getString(3));
                    lightDetail.setCount(c.getString(4));
                    lightDetail.setWattPerCount(c.getString(5));
                    lightDetail.setId(c.getInt(0));
                    lightDetail.setTotalLumens(c.getString(6));


                    lightDetails.add(lightDetail);
                } while (c.moveToNext());
            }

            c.close();
        }



        setReflectorDetails();

        setLightDetails();

        final RecyclerView lightRecyclerView = findViewById(R.id.LightRecylerView);
        lightRecyclerAdapter = new LightRecyclerAdapter(lightDetails, this, new LightRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {

                Log.d("Lights",Long.toString(lightDetails.get(position).getId()));

                tankDBHelper.deleteItemLight(lightDetails.get(position).getId());
                totalLumens -= lightDetails.get(position).getTotalLumens();
                lightDetails.remove(position);
                lightRecyclerAdapter.notifyItemRemoved(position);
                recalculateLSI();




            }
        });
        lightRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        lightRecyclerView.setAdapter(lightRecyclerAdapter);

        deleteAllLightsData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder deleteAlert = new AlertDialog.Builder(LightCalcActivity.this);
                deleteAlert.setTitle("WARNING")
                        .setMessage("This will delete all your light equipment data")
                        .setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                tankDBHelper.deleteItemLight(aquariumID);
                                lightDetails.clear();
                                lightRecyclerAdapter.notifyDataSetChanged();

                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();

            }
        });

        calculateLSIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recalculateLSI();
            }
        });


        recalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recalculateLSI();
            }
        });

    }

    private Spinner metricSpinner;
    private ArrayAdapter<String> spinnerAdapter;

    private HashMap<String,Float> values;
    private void setValues() {

        // ADD HASHMAP VALUES FOR UNITS
        values = new HashMap<>();
        values.put("inch", 1f);
        values.put("meter", 39.37008f);
        values.put("centimeter", 0.3937008f);
        values.put("feet", 12f);

        metricSpinner = findViewById(R.id.MetricSpinner);
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList("inch","meter","centimeter","feet"));

        spinnerAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,arrayList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        metricSpinner.setAdapter(spinnerAdapter);


    }



        private void setTankData(){

        lsiTextView = findViewById(R.id.lsi_output);


        SharedPreferences sharedPreferences = getSharedPreferences(aquariumID,MODE_PRIVATE);
        String metric = sharedPreferences.getString("Tank_light_metric","inch");
        metricSpinner.setSelection(spinnerAdapter.getPosition(metric));


        Cursor c = tankDBHelper.getDataCondition("AquariumID",aquariumID);
        if(c.moveToFirst()){

                TankLength.setText(c.getString(24));
                TankWidth.setText(c.getString(25));
                TankHeight.setText(c.getString(26));
                SubstrateDepth.setText(c.getString(27));
                LightHeight.setText(c.getString(28));

                if(TextUtils.isEmpty(c.getString(29))){
                    lsiTextView.setText(getResources().getString(R.string.not_calculated));
                }
                else{
                    lsiTextView.setText(c.getString(29));
                }

                effectiveLumenPerLight = Float.parseFloat(c.getString(30).replace(",","."));
                totalLumens+=effectiveLumenPerLight;
                reflectorName = c.getString(31);

                strEfficiency = c.getString(32);

                reflectorPosition = Integer.parseInt(c.getString(34));
                LightZone.setText(c.getString(16));
                co2 = c.getString(12);
                aquariumName = c.getString(2);

        }

    }

    private void setReflectorDetails(){

        //region REFLECTOR TYPES HASHMAP
        final HashMap<String,String> reflectorTypes = new HashMap<>();
        reflectorTypes.put("No Reflector","60");
        reflectorTypes.put("Aluminium Sheet/Foil","85");
        reflectorTypes.put("Mylar","87");
        reflectorTypes.put("Glossy White","90");
        reflectorTypes.put("Polished Aluminium","92");
        reflectorTypes.put("Glass Mirror","95");
        reflectorTypes.put("Others","");
        reflectorTypes.put("One-Directional LED","99");
        //endregion


        //region SET REFLECTOR TYPES SPINNER
        Collection<String> vals = reflectorTypes.keySet();
        final String[] array = vals.toArray(new String[vals.size()]);
        final ArrayAdapter<String> reflectorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,array);
        reflectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Reflector.setAdapter(reflectorAdapter);

        Reflector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                reflectorPosition = position;

                Efficiency.setText(reflectorTypes.get(array[position]));



                if(Reflector.getSelectedItem().toString().equals("Others")){

                    otherReflectorCardView.setVisibility(View.VISIBLE);
                    ReflectorName.setText(reflectorName);
                    Efficiency.setHint("1");
                    Efficiency.setText(strEfficiency);


                }
                else
                {
                    otherReflectorCardView.setVisibility(View.GONE);
                    ReflectorName.setText("");


                }

                recalculateLSI();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Reflector.setSelection(reflectorPosition);

        //endregion


    }

    Spinner lightTypeSpinner;

    private void setLightDetails(){

        //region LIGHT-TYPES SPINNER ADAPTER
        final HashMap<String,String> lightTypes = new HashMap<>();

        lightTypes.put("T12 Flourescent","65");
        lightTypes.put("T8 Flourescent","86");
        lightTypes.put("T5 Flourescent","100");
        lightTypes.put("PLL/PC Flourescent","80");
        lightTypes.put("CFL/Spiral Flourescent","69");
        lightTypes.put("T5 HO Flourescent","92");
        lightTypes.put("LED","100");
        lightTypes.put("Others","");


        Collection<String> vals = lightTypes.keySet();
        final String[] array = vals.toArray(new String[vals.size()]);

        lightAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,array);
        lightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //endregion


        addLights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                //region EACH LIGHT ROW LAYOUT INITIALIZATION
                View eachLightDetailsView = getLayoutInflater().inflate(R.layout.light_type_layout,null);
                lightTypeSpinner = eachLightDetailsView.findViewById(R.id.LightTypeSpinner);
                final EditText lumensPerWatt = eachLightDetailsView.findViewById(R.id.LumensPerWattLight);
                final EditText count = eachLightDetailsView.findViewById(R.id.LightCount);
                final EditText wattPerCount = eachLightDetailsView.findViewById(R.id.WattPerCount);
                final CardView otherLightCardView = eachLightDetailsView.findViewById(R.id.OtherLightCardView);
                final EditText LightName = eachLightDetailsView.findViewById(R.id.LightName);
                lightTypeSpinner.setAdapter(lightAdapter);
                lightTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        lumensPerWatt.setText(lightTypes.get(array[position]));

                        if(lightTypeSpinner.getSelectedItem().toString().equals("Others")){
                            otherLightCardView.setVisibility(View.VISIBLE);

                        }
                        else{
                            otherLightCardView.setVisibility(View.GONE);
                            LightName.setText("");

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                //endregion

                //region ALERT DIALOG CREATION
                AlertDialog.Builder addLightsAlert = new AlertDialog.Builder(LightCalcActivity.this);
                addLightsAlert.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                        .setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                LightDetails lightDetail = new LightDetails();

                                String s = LightName.getText().toString();
                                if(!TextUtils.isEmpty(s)){
                                    lightDetail.setLightType(s);
                                }
                                else {
                                    lightDetail.setLightType(lightTypeSpinner.getSelectedItem().toString());
                                }

                                s = lumensPerWatt.getText().toString();
                                if(!TextUtils.isEmpty(s)){

                                    lightDetail.setLumensPerWatt(s);

                                }

                                s = count.getText().toString();
                                if(!TextUtils.isEmpty(s)){

                                    lightDetail.setCount(s);

                                }

                                s = wattPerCount.getText().toString();
                                if(!TextUtils.isEmpty(s)){

                                    lightDetail.setWattPerCount(s);

                                }




                                calcLSI(lightDetail);


                            }
                        })
                        .setView(eachLightDetailsView).create().show();
                //endregion

            }
        });

    }

    private void updateTankAndLightDetails(){

        SharedPreferences sharedPreferences = getSharedPreferences(aquariumID,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Tank_light_metric",metricSpinner.getSelectedItem().toString());
        editor.apply();


        float metricMultiplier = values.get(metricSpinner.getSelectedItem().toString());

        tankDBHelper.updateSingleItem("AquariumID",aquariumID,"ReflectorPosition",Integer.toString(reflectorPosition));

        if(Reflector.getSelectedItem().toString().equals("Others")) {

            if (!TextUtils.isEmpty(ReflectorName.getText().toString())) {

                tankDBHelper.updateSingleItem("AquariumID",aquariumID,"Reflector",ReflectorName.getText().toString());

            }

            strEfficiency = Efficiency.getText().toString();
        }

        else{

            tankDBHelper.updateSingleItem("AquariumID",aquariumID,"Reflector",Reflector.getSelectedItem().toString());

        }



        length = TextUtils.isEmpty(TankLength.getText().toString())? 1f : Float.parseFloat(TankLength.getText().toString().replace(",","."))* metricMultiplier;
        tankDBHelper.updateSingleItem("AquariumID",aquariumID,"TankLength",Float.toString(length/ metricMultiplier));

        width = TextUtils.isEmpty(TankWidth.getText().toString())? 1f : Float.parseFloat(TankWidth.getText().toString().replace(",","."))* metricMultiplier;
        tankDBHelper.updateSingleItem("AquariumID",aquariumID,"TankWidth",Float.toString(width/ metricMultiplier));

        height = TextUtils.isEmpty(TankHeight.getText().toString())? 1f : Float.parseFloat(TankHeight.getText().toString().replace(",","."))* metricMultiplier;
        tankDBHelper.updateSingleItem("AquariumID",aquariumID,"TankHeight",Float.toString(height/ metricMultiplier));

        substrateDepth = TextUtils.isEmpty(SubstrateDepth.getText().toString())? 1f : Float.parseFloat(SubstrateDepth.getText().toString().replace(",","."))* metricMultiplier;
        tankDBHelper.updateSingleItem("AquariumID",aquariumID,"SubstrateDepth",Float.toString(substrateDepth/ metricMultiplier));

        lightHeight = TextUtils.isEmpty(LightHeight.getText().toString())? 1f : Float.parseFloat(LightHeight.getText().toString().replace(",","."))* metricMultiplier;
        tankDBHelper.updateSingleItem("AquariumID",aquariumID,"HeightFromSurface",Float.toString(lightHeight/ metricMultiplier));

        efficiency = (TextUtils.isEmpty(Efficiency.getText().toString())? 1f : Float.parseFloat(Efficiency.getText().toString().replace(",",".")))/100f;
        tankDBHelper.updateSingleItem("AquariumID",aquariumID,"ReflectorEfficiency",Efficiency.getText().toString());

    }

    private void calcLSI(LightDetails lightDetail){

        updateTankAndLightDetails();

        lightDetail.setTotalLumens(efficiency);

        totalLumens+=lightDetail.getTotalLumens();
        tankDBHelper.updateSingleItem("AquariumID",aquariumID,"TotalLumens",String.format(Locale.getDefault(),"%.2f",effectiveLumenPerLight));






        long rowid=tankDBHelper.addDataLightDetails(aquariumID,lightDetail.getLightType(),lightDetail.getLumensPerWatt(),lightDetail.getCount(),lightDetail.getWattPerCount(),Float.toString(lightDetail.getTotalLumens()));

        //Toast.makeText(this,String.format(Locale.getDefault(),"%.2f",effectiveLumenPerLight),Toast.LENGTH_LONG).show();

        lightDetail.setId(rowid);

        lightDetails.add(lightDetail);
        lightRecyclerAdapter.notifyItemInserted(lightDetails.size()-1);

        Log.i("Lights",Long.toString(rowid));



        finalLSICalc();


    }

    void recalculateLSI(){

        totalLumens = 0;

        updateTankAndLightDetails();

        for(LightDetails lightDetail:lightDetails){


            lightDetail.setTotalLumens(efficiency);

            tankDBHelper.updateDataLightDetails(lightDetail.getId(),String.format(Locale.getDefault(),"%.2f",lightDetail.getTotalLumens()));

            totalLumens+=lightDetail.getTotalLumens();


        }
        lightRecyclerAdapter.notifyDataSetChanged();

        finalLSICalc();


    }

    private String tankVolume = "";

    private void finalLSICalc(){

        tankVolume = Integer.toString(Math.round((length * width * height)*0.004329f));
        estimatedTankVolumeText.setText(tankVolume+" US Gallons");
        float area = length * width;
        float lightDistance = height+lightHeight-substrateDepth;
        float netEffectiveLumens;
        netEffectiveLumens = totalLumens *0.85f;
        float LSIatSurface = netEffectiveLumens/area;
        float LSIatHalfDepth = (1f-(0.01f*lightDistance/2f))*LSIatSurface;
        float LSI = (1f-(0.01f*lightDistance))*LSIatSurface;
        lsiTextView.setText(String.format(Locale.getDefault(),"%.2f",LSI));

        lightZone="Insufficient Data";
        if(LSIatHalfDepth<1){
            lightZone = "Dark";
        }
        if(LSIatHalfDepth>=1 && LSIatHalfDepth<5){
            lightZone = "Low";
        }
        if(LSIatHalfDepth>=5 && LSIatHalfDepth<10){
            lightZone = "Medium";
        }
        if(LSIatHalfDepth>=10 && LSIatHalfDepth<15){
            lightZone = "Medium High";
        }
        if(LSIatHalfDepth>=15 && LSIatHalfDepth<20){
            lightZone = "High";
        }
        if(LSIatHalfDepth>=20){
            lightZone = "Very High";
        }

        LightZone.setText(lightZone);
        tankDBHelper.updateSingleItem("AquariumID",aquariumID,"LightRegion",lightZone);

        tankDBHelper.updateSingleItem("AquariumID",aquariumID,"LSI",String.format(Locale.getDefault(),"%.2f",LSI));
        tankDBHelper.updateSingleItem("AquariumID",aquariumID,"TotalLumens",String.format(Locale.getDefault(),"%.2f",effectiveLumenPerLight));



        Toast.makeText(this,"Data Refreshed",Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        intent.putExtra("Position",position);
        setResult(Activity.RESULT_OK, new Intent());


    }

    @Override
    protected void onStop() {
        checkCO2level();

        super.onStop();
    }

    String recoString="";

    private void checkCO2level(){

        String title="";
        String visibility="1";

        if (!lightZone.equals("Insufficient Data")) {

            if ((lightZone.equals("Dark")||lightZone.equals("Low"))  && !co2.equals("Air")) {

                recoString = "Your tank is receiving LOW light. There is probably no need for additional CO2 supplementation";
                title = "INFO";
            }

            else if (lightZone.equals("Medium") && co2.equals("Air")) {

                recoString = "Your tank is receiving MEDIUM light. As such CO2 supplementation though not entirely necessary can prove beneficial. Opt for Liquid CO2 or at least DIY if not Pressurized ";
                title = "INFO";
            }

            else if (lightZone.equals("Medium High") && (co2.equals("Air") || co2.equals("Liquid"))) {

                recoString = "Your tank is receiving MEDIUM to HIGH light. As such gaseous CO2 injection is an absolute necessity else it will led to algae growth. At least DIY if not pressurized is recommended ";
                title = "ALERT";
            }

            else if ((lightZone.equals("High")||lightZone.equals("Very High")) && !co2.equals("Pressurized")) {

                recoString = "Your tank is receiving HIGH light. As such pressurized CO2 injection is an absolute necessity else it will led to algae growth";
                title = "ALERT";
            }

            else{
                visibility="0";
            }


        }

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new java.util.Date());
        String day = new SimpleDateFormat("EE", Locale.getDefault()).format(new java.util.Date());

        Cursor c = tankDBHelper.getDataRecoCondition(tankDBHelper.getWritableDatabase(),aquariumID);
        if(c.moveToFirst()){
            tankDBHelper.updateDataReco(1,aquariumID,day,timeStamp,title,recoString,visibility,aquariumName);
        }
        else{
            tankDBHelper.addDataReco(1,aquariumID,day,timeStamp,title,recoString,visibility,aquariumName);
        }
        c.close();


    }


    public void updateTankGallons(View view) {

        tankDBHelper.updateSingleItem("AquariumID",aquariumID,"Volume",tankVolume);
        tankDBHelper.updateSingleItem("AquariumID",aquariumID,"VolumeMetric","US Gallon");
        Toast.makeText(this,"Tank Volume Updated",Toast.LENGTH_SHORT).show();
    }
}
