package com.newage.plantedaqua;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GHActivity extends AppCompatActivity {

    EditText targetWaterVolumeInput;
    EditText targetGHInput;
    EditText tapGHInput;
    EditText roGHInput;
    Button ghOKButton;
    Spinner volumeUnitsSpinner;
    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gh);

        targetWaterVolumeInput = findViewById(R.id.TargetWaterVolumeInput);
        targetGHInput = findViewById(R.id.TargetGHInput);
        tapGHInput = findViewById(R.id.TapGHInput);
        roGHInput = findViewById(R.id.ROGHInput);
        ghOKButton = findViewById(R.id.GHOKButton);
        volumeUnitsSpinner = findViewById(R.id.VolumeUnitSpinner);
        resultTextView = findViewById(R.id.ResultTextView);
        setValues();
        ArrayList<String> volumeUnits = new ArrayList<>(Arrays.asList("Litre","US Gallon","UK gallon","Cubic metre","Cubic inch","Cubic foot","Cubic cm"));
        ArrayAdapter<String> volumeUnitsAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,volumeUnits);
        volumeUnitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        volumeUnitsSpinner.setAdapter(volumeUnitsAdapter);
    }

    private HashMap<String,Double> values;
    private void setValues() {

        // ADD HASHMAP VALUES FOR UNITS
        values = new HashMap<>();
        values.put("Litre", 1d);
        values.put("US Gallon", 0.264172d);
        values.put("UK gallon", 0.219969d);
        values.put("Cubic metre", 0.001d);
        values.put("Cubic inch", 61.0237d);
        values.put("Cubic foot", 0.0353147d);
        values.put("Cubic cm", 1000d);

    }

    public void calculateMix(View view) {

        if (!TextUtils.isEmpty(targetWaterVolumeInput.getText()) && !TextUtils.isEmpty(targetGHInput.getText()) && !TextUtils.isEmpty(tapGHInput.getText()) && !TextUtils.isEmpty(roGHInput.getText())) {


            String unit = volumeUnitsSpinner.getSelectedItem().toString();
            Double targetWaterVolume = Double.parseDouble(targetWaterVolumeInput.getText().toString()) / values.get(unit);
            Double targetGH = Double.parseDouble(targetGHInput.getText().toString());
            Double tapWaterGH = Double.parseDouble(tapGHInput.getText().toString());
            Double roWaterGH = Double.parseDouble(roGHInput.getText().toString());

            Double tapWaterVolume = Double.parseDouble(String.format("%.2f", (targetWaterVolume * (targetGH - roWaterGH) / (tapWaterGH - roWaterGH) * values.get(unit))));
            Double roWaterVolume = Double.parseDouble(String.format("%.2f", (((targetWaterVolume * values.get(unit)) - tapWaterVolume))));

            String resultText = "To reach a target of " + targetGH + " GH, Mix\n" + tapWaterVolume + " " + unit + " of Tap Water and\n" + roWaterVolume + " " + unit + " of RO water";
            resultTextView.setText(resultText);
        }

        else
            Toast.makeText(this, "Please provide all input values", Toast.LENGTH_SHORT).show();
    }


}


