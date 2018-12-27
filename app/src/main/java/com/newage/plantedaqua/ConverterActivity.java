package com.newage.plantedaqua;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ConverterActivity extends AppCompatActivity {

    private Spinner volumeFromSpinner;
    private Spinner volumeToSpinner;
    private Spinner tankVolumeFromSpinner;
    private Spinner tankVolumeToSpinner;
    private Spinner weightFromSpinner;
    private Spinner weightToSpinner;
    private Spinner weightSpinner;
    private Spinner volumeSpinner;
    private Spinner tempFromSpinner;
    private Spinner tempToSpinner;
    private TextView tankVolumeConvertedOutput;
    private TextView volumeConvertedOutput;
    private TextView weightConvertedOutput;
    private TextView ppmOutput;
    private TextView tempConvertedOutput;
    private EditText volumeInput;
    private EditText tankLengthInput;
    private EditText tankWidthInput;
    private EditText tankHeightInput;
    private EditText weightInput;
    private EditText weightPPMInput;
    private EditText volumePPMInput;
    private EditText tempFromInput;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        volumeFromSpinner = findViewById(R.id.VolumeFromSpinner);
        volumeToSpinner = findViewById(R.id.VolumeToSpinner);
        tankVolumeFromSpinner = findViewById(R.id.TankVolumeFromSpinner);
        tankVolumeToSpinner = findViewById(R.id.TankVolumeToSpinner);
        weightSpinner = findViewById(R.id.WeightSpinner);
        volumeSpinner = findViewById(R.id.VolumeSpinner);
        tempFromSpinner = findViewById(R.id.TempFromSpinner);
        tempToSpinner =findViewById(R.id.TempToSpinner);
        weightFromSpinner = findViewById(R.id.WeightFromSpinner);
        weightToSpinner = findViewById(R.id.WeightToSpinner);


        volumeInput = findViewById(R.id.VolumeInput);
        tankLengthInput = findViewById(R.id.TankLengthInput);
        tankWidthInput = findViewById(R.id.TankWidthInput);
        tankHeightInput = findViewById(R.id.TankHeightInput);
        weightPPMInput = findViewById(R.id.WeightPPMInput);
        volumePPMInput = findViewById(R.id.VolumePPMInput);
        weightInput = findViewById(R.id.WeightInput);
        tempFromInput = findViewById(R.id.TempFromInput);

        volumeConvertedOutput = findViewById(R.id.VolumeConvertedOutput);
        tankVolumeConvertedOutput = findViewById(R.id.TankVolumeConvertedOutput);
        weightConvertedOutput = findViewById(R.id.WeightConvertedOutput);
        ppmOutput = findViewById(R.id.PPMOutput);
        tempConvertedOutput = findViewById(R.id.ConvertedTempOutput);
        setValues();
    }
    private HashMap<String,Double> values;
    private void setValues() {

        // ADD HASHMAP VALUES FOR UNITS
        values = new HashMap<>();
        values.put("Litre",1d);
        values.put("US Gallon",0.264172d);
        values.put("UK gallon",0.219969d);
        values.put("Cubic metre",0.001d);
        values.put("Cubic inch",61.0237d);
        values.put("Cubic foot",0.0353147d);
        values.put("Cubic cm",1000d);
        values.put("Gram",1d);
        values.put("milligram",1000d);
        values.put("Ounce",0.035274d);
        values.put("Celsius",0d);
        values.put("Fahrenheit",32d);
        values.put("Kelvin",273.15d);


        //ASSIGN SPINNERS

        ArrayList<String> volumeUnits = new ArrayList<>(Arrays.asList("Litre","US Gallon","UK gallon","Cubic metre","Cubic inch","Cubic foot","Cubic cm"));
        ArrayList<String> cubicVolumeUnits = new ArrayList<>(Arrays.asList("Cubic metre","Cubic inch","Cubic foot","Cubic cm"));
        ArrayList<String> weightUnits = new ArrayList<>(Arrays.asList("Gram","milligram","Ounce"));
        ArrayList<String> tempUnits = new ArrayList<>(Arrays.asList("Celsius","Fahrenheit","Kelvin"));
        ArrayAdapter<String> volumeSpinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,volumeUnits);
        volumeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> cubicVolumeSpinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,cubicVolumeUnits);
        cubicVolumeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> weightSpinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,weightUnits);
        weightSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> tempSpinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,tempUnits);
        tempSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        volumeFromSpinner.setAdapter(volumeSpinnerAdapter);
        volumeToSpinner.setAdapter(volumeSpinnerAdapter);
        tankVolumeToSpinner.setAdapter(volumeSpinnerAdapter);
        tankVolumeFromSpinner.setAdapter(cubicVolumeSpinnerAdapter);
        weightFromSpinner.setAdapter(weightSpinnerAdapter);
        weightToSpinner.setAdapter(weightSpinnerAdapter);
        volumeSpinner.setAdapter(volumeSpinnerAdapter);
        weightSpinner.setAdapter(weightSpinnerAdapter);
        tempFromSpinner.setAdapter(tempSpinnerAdapter);
        tempToSpinner.setAdapter(tempSpinnerAdapter);






    }

    void convert(View view) {

        Double firstValue, secondValue, inputValue, resultValue, resultValue2, finalResultValue;

        if(view.getTag().toString().equals("TankVolume")){

            if (!TextUtils.isEmpty(tankLengthInput.getText()) && !TextUtils.isEmpty(tankWidthInput.getText()) && !TextUtils.isEmpty(tankHeightInput.getText())) {


                inputValue = Double.parseDouble(tankLengthInput.getText().toString()) * Double.parseDouble(tankWidthInput.getText().toString()) * Double.parseDouble(tankHeightInput.getText().toString());
                firstValue = values.get(tankVolumeFromSpinner.getSelectedItem().toString());
                //Log.i("1st Unit",firstValue.toString());
                secondValue = values.get(tankVolumeToSpinner.getSelectedItem().toString());
                resultValue = Double.parseDouble(String.format("%.2f", secondValue / firstValue * inputValue));
                tankVolumeConvertedOutput.setText(resultValue.toString());
            }
            else
                Toast.makeText(this, "Please provide all input values", Toast.LENGTH_SHORT).show();


        }

       else if (view.getTag().toString().equals("Volume")) {

            if (!TextUtils.isEmpty(volumeInput.getText())) {
                inputValue = Double.parseDouble(volumeInput.getText().toString());
                firstValue = values.get(volumeFromSpinner.getSelectedItem().toString());
                //Log.i("1st Unit",firstValue.toString());
                secondValue = values.get(volumeToSpinner.getSelectedItem().toString());
                resultValue = Double.parseDouble(String.format("%.2f", secondValue / firstValue * inputValue));
                volumeConvertedOutput.setText(resultValue.toString());


            } else
                Toast.makeText(this, "Please enter a value to convert", Toast.LENGTH_SHORT).show();


        }

       else if (view.getTag().toString().equals("Weight")) {

            if (!TextUtils.isEmpty(weightInput.getText())) {
                inputValue = Double.parseDouble(weightInput.getText().toString());
                firstValue = values.get(weightFromSpinner.getSelectedItem().toString());
                //Log.i("1st Unit",firstValue.toString());
                secondValue = values.get(weightToSpinner.getSelectedItem().toString());
                resultValue = Double.parseDouble(String.format("%.2f", secondValue / firstValue * inputValue));
                weightConvertedOutput.setText(resultValue.toString());


            } else
                Toast.makeText(this, "Please enter a value to convert", Toast.LENGTH_SHORT).show();


        }

       else if (view.getTag().toString().equals("PPM")) {

            if (!TextUtils.isEmpty(weightPPMInput.getText()) && !TextUtils.isEmpty(volumePPMInput.getText())) {
                inputValue = Double.parseDouble(weightPPMInput.getText().toString());
                //Log.i("1st Unit",firstValue.toString());
                secondValue = values.get(weightSpinner.getSelectedItem().toString());
                resultValue = Double.parseDouble(String.format("%.2f", 1000d * inputValue / secondValue));
                Log.i("1st Unit",resultValue.toString());
                inputValue = Double.parseDouble(volumePPMInput.getText().toString());
                //Log.i("1st Unit",firstValue.toString());
                secondValue = values.get(volumeSpinner.getSelectedItem().toString());
                resultValue2 = Double.parseDouble(String.format("%.2f",inputValue / secondValue));
                Log.i("2nd Unit",resultValue2.toString());

                finalResultValue = Double.parseDouble(String.format("%.2f",resultValue/resultValue2));
                ppmOutput.setText(finalResultValue.toString());


            } else
                Toast.makeText(this, "Please provide all input values", Toast.LENGTH_SHORT).show();


        }

        else if (view.getTag().toString().equals("Temp")) {

            if (!TextUtils.isEmpty(tempFromInput.getText())) {

                double k1=1d,k2=1d;
                inputValue = Double.parseDouble(tempFromInput.getText().toString());
                if(tempFromSpinner.getSelectedItem().toString().equals("Fahrenheit"))
                     k1 = 0.55555556d;


                firstValue = k1 * (inputValue - values.get(tempFromSpinner.getSelectedItem().toString()));

                if(tempToSpinner.getSelectedItem().toString().equals("Fahrenheit"))
                    k2 = 1.8d;

                resultValue = (k2 * firstValue) + values.get(tempToSpinner.getSelectedItem().toString());

                finalResultValue = Double.parseDouble(String.format("%.2f",resultValue));
                tempConvertedOutput.setText(finalResultValue.toString());


            } else
                Toast.makeText(this, "Please provide all input values", Toast.LENGTH_SHORT).show();


        }
    }
    public void mixRO (View view) {

        startActivity(new Intent(this,GHActivity.class));

    }
}
