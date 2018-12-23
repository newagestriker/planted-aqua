package com.newage.plantedaqua;

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
    private TextView tankVolumeConvertedOutput;
    private TextView volumeConvertedOutput;
    private TextView weightConvertedOutput;
    private TextView ppmOutput;
    private EditText volumeInput;
    private EditText tankLengthInput;
    private EditText tankWidthInput;
    private EditText tankHeightInput;
    private EditText weightInput;
    private EditText weightPPMInput;
    private EditText volumePPMInput;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        volumeFromSpinner = findViewById(R.id.VolumeFromSpinner);
        volumeToSpinner = findViewById(R.id.VolumeToSpinner);
        tankVolumeFromSpinner = findViewById(R.id.TankVolumeFromSpinner);
        tankVolumeToSpinner = findViewById(R.id.TankVolumeToSpinner);
        volumeInput = findViewById(R.id.VolumeInput);
        volumeConvertedOutput = findViewById(R.id.VolumeConvertedOutput);
        tankLengthInput = findViewById(R.id.TankLengthInput);
        tankWidthInput = findViewById(R.id.TankWidthInput);
        tankHeightInput = findViewById(R.id.TankHeightInput);
        tankVolumeConvertedOutput = findViewById(R.id.TankVolumeConvertedOutput);
        weightFromSpinner = findViewById(R.id.WeightFromSpinner);
        weightToSpinner = findViewById(R.id.WeightToSpinner);
        weightInput = findViewById(R.id.WeightInput);
        weightConvertedOutput = findViewById(R.id.WeightConvertedOutput);
        weightSpinner = findViewById(R.id.WeightSpinner);
        volumeSpinner = findViewById(R.id.VolumeSpinner);
        weightPPMInput = findViewById(R.id.WeightPPMInput);
        volumePPMInput = findViewById(R.id.VolumePPMInput);
        ppmOutput = findViewById(R.id.PPMOutput);
        setValues();
    }
    private HashMap<String,Double> values;
    void setValues() {

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

        //ASSIGN SPINNERS

        ArrayList<String> volumeUnits = new ArrayList<>(Arrays.asList("Litre","US Gallon","UK gallon","Cubic metre","Cubic inch","Cubic foot","Cubic cm"));
        ArrayList<String> cubicVolumeUnits = new ArrayList<>(Arrays.asList("Cubic metre","Cubic inch","Cubic foot","Cubic cm"));
        ArrayList<String> weightUnits = new ArrayList<>(Arrays.asList("Gram","milligram","Ounce"));
        ArrayAdapter<String> volumeSpinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,volumeUnits);
        volumeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> cubicVolumeSpinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,cubicVolumeUnits);
        cubicVolumeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> weightSpinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,weightUnits);
        weightSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        volumeFromSpinner.setAdapter(volumeSpinnerAdapter);
        volumeToSpinner.setAdapter(volumeSpinnerAdapter);
        tankVolumeToSpinner.setAdapter(volumeSpinnerAdapter);
        tankVolumeFromSpinner.setAdapter(cubicVolumeSpinnerAdapter);
        weightFromSpinner.setAdapter(weightSpinnerAdapter);
        weightToSpinner.setAdapter(weightSpinnerAdapter);
        volumeSpinner.setAdapter(volumeSpinnerAdapter);
        weightSpinner.setAdapter(weightSpinnerAdapter);






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
    }
}
