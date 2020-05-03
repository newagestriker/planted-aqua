package com.newage.plantedaqua.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.newage.plantedaqua.models.PresetCalc;
import com.newage.plantedaqua.R;

public class PresetMixActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset_mix);

        PresetCalc presetCalc = new PresetCalc();
        float test = presetCalc.calcDosage(35f,50f);
        Toast.makeText(this,Float.toString(test),Toast.LENGTH_LONG).show();

    }
}
