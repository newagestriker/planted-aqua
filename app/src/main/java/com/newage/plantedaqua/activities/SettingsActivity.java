package com.newage.plantedaqua.activities;

import android.content.DialogInterface;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.newage.plantedaqua.R;
import com.newage.plantedaqua.helpers.TinyDB;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle(getResources().getString(R.string.Settings));
        final TinyDB settingsDB=new TinyDB(this.getApplicationContext());
        final TextView userTypeTextView = findViewById(R.id.UserTypeTextView);
        final TextView defaultCurrencySymbol = findViewById(R.id.CurrencySymbolTextView);
        userTypeTextView.setText(settingsDB.getString("UserType"));
        defaultCurrencySymbol.setText(settingsDB.getString("DefaultCurrencySymbol"));


        LinearLayout userTypeLinear=findViewById(R.id.LinearUserType);
        userTypeLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SettingsActivity.this);
                final CharSequence items[] = {getResources().getString(R.string.Hobbyist), getResources().getString(R.string.Seller), getResources().getString(R.string.Cancel)};
                builder.setTitle(getResources().getString(R.string.HorS))
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (items[i].equals(getResources().getString(R.string.Cancel))) {

                                    dialogInterface.dismiss();
                                } else {
                                    settingsDB.putString("UserType", items[i].toString());
                                    userTypeTextView.setText(items[i].toString());
                                }
                            }
                        }).create();
                builder.show();

            }
        });

        LinearLayout CurrencyLinear=findViewById(R.id.LinearCurrencyType);
        final EditText currencySymbolInput = new EditText(this);
        CurrencyLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SettingsActivity.this);
                if(currencySymbolInput.getParent()!=null)
                    ((ViewGroup)currencySymbolInput.getParent()).removeView(currencySymbolInput);
                builder.setView(currencySymbolInput);
                builder.setTitle("Default Currency Symbol")
                        .setNeutralButton(getResources().getText(R.string.OK), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                settingsDB.putString("DefaultCurrencySymbol",currencySymbolInput.getText().toString());
                                defaultCurrencySymbol.setText(currencySymbolInput.getText().toString());

                            }
                        })
                       .create();
                builder.show();

            }
        });
    }
}
