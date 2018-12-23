package com.newage.plantedaqua;

import android.support.annotation.Nullable;
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
    private EditText volumeInput;
    private TextView volumeConvertedOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        volumeFromSpinner = findViewById(R.id.VolumeFromSpinner);
        volumeToSpinner = findViewById(R.id.VolumeToSpinner);
        volumeInput = findViewById(R.id.VolumeInput);
        volumeConvertedOutput = findViewById(R.id.VolumeConvertedOutput);
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
        values.put("Gram",1d);
        values.put("mg",1000d);
        values.put("Ounce",0.035274d);

        //ASSIGN SPINNERS

        ArrayList<String> volumeUnits = new ArrayList<>(Arrays.asList("Litre","US Gallon","UK gallon","Cubic metre","Cubic inch","Cubic foot"));
        ArrayList<String> weightUnits = new ArrayList<>(Arrays.asList("Gram","mg","Ounce"));
        ArrayAdapter<String> volumeFromSpinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,volumeUnits);
        volumeFromSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        volumeFromSpinner.setAdapter(volumeFromSpinnerAdapter);

        ArrayAdapter<String> volumeToSpinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,volumeUnits);
        volumeToSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        volumeToSpinner.setAdapter(volumeFromSpinnerAdapter);

    }

    void convert(View view) {

        Double firstValue,secondValue,inputValue,resultValue;

        if(!TextUtils.isEmpty(volumeInput.getText())) {
            inputValue = Double.parseDouble(volumeInput.getText().toString());
            firstValue = values.get(volumeFromSpinner.getSelectedItem().toString());
            //Log.i("1st Unit",firstValue.toString());
            secondValue = values.get(volumeToSpinner.getSelectedItem().toString());
            resultValue = Double.parseDouble(String.format("%.2f",secondValue/firstValue*inputValue));
            volumeConvertedOutput.setText(resultValue.toString());


        }

        else
            Toast.makeText(this,"Please enter a value to convert",Toast.LENGTH_SHORT).show();



    }
}
