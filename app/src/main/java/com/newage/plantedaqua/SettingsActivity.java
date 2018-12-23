package com.newage.plantedaqua;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle(getResources().getString(R.string.Settings));
        final TinyDB settingsDB=new TinyDB(this.getApplicationContext());
        final TextView userTypeTextView = findViewById(R.id.UserTypeTextView);
        userTypeTextView.setText(settingsDB.getString("UserType"));


        LinearLayout userTypeLinear=findViewById(R.id.LinearUserType);
        userTypeLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SettingsActivity.this);
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
    }
}
