package com.newage.plantedaqua;

import android.os.Bundle;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ConversionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);

        TextView ok=findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertValue();
            }
        });


    }
    void convertValue()
    {
        CoordinatorLayout convert=findViewById(R.id.convert);
        EditText conversionInput=findViewById(R.id.ConversionInput);
        if(conversionInput.getText().toString().isEmpty()){
            Snackbar.make(convert,getResources().getString(R.string.Novalue),Snackbar.LENGTH_LONG).show();
        }
    }
}
