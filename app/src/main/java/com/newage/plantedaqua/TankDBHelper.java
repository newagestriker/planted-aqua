package com.newage.plantedaqua;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class TankDBHelper extends SQLiteOpenHelper {
    
    private Context context;

    private TankDBHelper(Context context) {
        super(context, "TankDetails", null  , 1);
        this.context=context;
    }

    private static TankDBHelper tankDBHelper=null;

    public static  TankDBHelper newInstance(Context context){

        if(tankDBHelper==null) {

            tankDBHelper = new TankDBHelper(context.getApplicationContext());
        }
        return  tankDBHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("create table TankDetails(id integer,AquariumID text,AquariumName text,ImageUri text,AquariumType text,Price text,Currency text,Volume text,VolumeMetric text,CurrentStatus text,StartupDate text," +
                    "DismantleDate text,CO2 text,LightType text,Wattage text,LumensPerWatt text,LightRegion text,AdditionalDetails text,Quality text,Seller text)");
        }
        catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("drop table if exists TankDetails");
            onCreate(db);
        }
        catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }
    public long addData(SQLiteDatabase db,Long id,String aquariumid,String aquariumname,String imageuri,String aquariumtype,String price,String currency, String volume,String volumemetric,String currentstatus,String startupdate, String dismantledate,
                        String co2,String lighttype,String wattage,String lpw, String lightregion,String additionaldetails){

        ContentValues contentValues = new ContentValues();
        contentValues.put("id",id);
        contentValues.put("AquariumID",aquariumid);
        contentValues.put("AquariumName",aquariumname);
        contentValues.put("ImageUri",imageuri);
        contentValues.put("AquariumType", aquariumtype);
        contentValues.put("Price",price);
        contentValues.put("Currency",currency);
        contentValues.put("Volume",volume);
        contentValues.put("VolumeMetric",volumemetric);
        contentValues.put("CurrentStatus",currentstatus);
        contentValues.put("StartupDate",startupdate);
        contentValues.put("DismantleDate",dismantledate);
        contentValues.put("CO2",co2);
        contentValues.put("LightType",lighttype);
        contentValues.put("Wattage",wattage);
        contentValues.put("LumensPerWatt",lpw);
        contentValues.put("LightRegion",lightregion);
        contentValues.put("AdditionalDetails",additionaldetails);



        try {
            long row = db.insert("TankDetails", null, contentValues);
            return row;
        }
        catch (Exception e)
        {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            return 0;

        }

    }
    public Cursor getData(SQLiteDatabase db) {

        try {
            Cursor data = db.rawQuery("select * from TankDetails", null);
            return data;
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            return null;
        }
    }

        public Cursor getDataCondition(String ColumnName, String MatchText) {
                SQLiteDatabase db = this.getWritableDatabase();
                try {
                    Cursor data = db.rawQuery("select * from TankDetails where "+ColumnName+"=?",new String[]{MatchText});
                    return data;
                }
                catch (Exception e){
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                    return null;
                }


    }
    public Integer deleteData(){

        SQLiteDatabase db = this.getWritableDatabase();
        try {

            return db.delete("TankDetails",null,null);
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();

            return 0;
        }

    }
    public void deleteItem(String ColumnName, String MatchText) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.delete("TankDetails", ColumnName+"='"+MatchText+"'", null);

        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();

        }

    }
    public void updateItem(SQLiteDatabase db,String aquariumid,String aquariumname,String imageuri,String aquariumtype,String price,String currency, String volume,String volumemetric,String currentstatus,String startupdate, String dismantledate,
                           String co2,String lighttype,String wattage,String lpw, String lightregion,String additionaldetails) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("AquariumName",aquariumname);
        contentValues.put("ImageUri",imageuri);
        contentValues.put("AquariumType", aquariumtype);
        contentValues.put("Price",price);
        contentValues.put("Currency",currency);
        contentValues.put("Volume",volume);
        contentValues.put("VolumeMetric",volumemetric);
        contentValues.put("CurrentStatus",currentstatus);
        contentValues.put("StartupDate",startupdate);
        contentValues.put("DismantleDate",dismantledate);
        contentValues.put("CO2",co2);
        contentValues.put("LightType",lighttype);
        contentValues.put("Wattage",wattage);
        contentValues.put("LumensPerWatt",lpw);
        contentValues.put("LightRegion",lightregion);
        contentValues.put("AdditionalDetails",additionaldetails);


        try {

            db.update("TankDetails",contentValues, "AquariumID=?",new String[]{aquariumid});

        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();

        }

    }




}