package com.newage.aquapets.dbhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class NutrientDbHelper extends SQLiteOpenHelper {


    private Context context;
    private static String AQUARIUM_ID;

    private NutrientDbHelper(Context context) {
        super(context, "NutrientDetails", null  , 1);
        this.context=context;
    }

    private static NutrientDbHelper nutrientDbHelper=null;

    public static  NutrientDbHelper newInstance(Context context, String AD){

        AQUARIUM_ID = AD;

        if(nutrientDbHelper==null) {

            nutrientDbHelper = new NutrientDbHelper(context.getApplicationContext());
        }
        return  nutrientDbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("create table AquaDetails(AquariumID text, TankVolume text, VolumeMetric text, Frequency text, DosageType text, StockVolume text, DoseVolume text, NutrientType text,PercentChange text,PercentUptake text)");
            db.execSQL("create table MacroDetails(AquariumID text, kName text, nName text, pName text, caName text, mgName text, sName text, kPpm text default 0, nPpm text default 0, pPpm text default 0, caPpm text default 0, mgPpm text default 0, sPpm text default 0, kWt text, nWt text, pWt text, caWt text, mgWt text, sWt text)");
            db.execSQL("create table MicroDetails(AquariumID text, feName text, mnName text, cuName text, znName text, bName text, moName text, clName text, niName text, coName text, siName text, vName text, seName text, fePpm text, mnPpm text, cuPpm text, znPpm text, bPpm text, moPpm text, clPpm text, niPpm text, coPpm text, siPpm text, vPpm text, sePpm text, feWt text, mnWt text, cuWt text, znWt text, bWt text, moWt text, clWt text, niWt text, coWt text, siWt text, vWt text, seWt text)");

        }



        catch (Exception e) {
            
        }

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long addDataAD(String TankVolume, String VolumeMetric, String Frequency, String DosageType, String StockVolume, String DoseVolume, String NutrientType, String PercentChange, String PercentUptake){

        SQLiteDatabase db = nutrientDbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("AquariumID",AQUARIUM_ID);
        contentValues.put("TankVolume",TankVolume);
        contentValues.put("VolumeMetric",VolumeMetric);
        contentValues.put("Frequency",Frequency);
        contentValues.put("DosageType",DosageType);
        contentValues.put("StockVolume",StockVolume);
        contentValues.put("DoseVolume",DoseVolume);
        contentValues.put("NutrientType",NutrientType);
        contentValues.put("PercentChange",PercentChange);
        contentValues.put("PercentUptake",PercentUptake);


        try {
            long row = db.insert("AquaDetails", null, contentValues);
            return row;
        }
        catch (Exception e)
        {
            
            return 0;

        }

    }




    public long updateDataAD(String TankVolume, String VolumeMetric, String Frequency, String DosageType, String StockVolume, String DoseVolume, String NutrientType, String PercentChange, String PercentUptake){

        SQLiteDatabase db = nutrientDbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("AquariumID",AQUARIUM_ID);
        contentValues.put("TankVolume",TankVolume);
        contentValues.put("VolumeMetric",VolumeMetric);
        contentValues.put("Frequency",Frequency);
        contentValues.put("DosageType",DosageType);
        contentValues.put("StockVolume",StockVolume);
        contentValues.put("DoseVolume",DoseVolume);
        contentValues.put("NutrientType",NutrientType);
        contentValues.put("PercentChange",PercentChange);
        contentValues.put("PercentUptake",PercentUptake);


        try {
            long row = db.update("AquaDetails", contentValues, "AquariumID =?",new String []{AQUARIUM_ID});
            return row;
        }
        catch (Exception e)
        {
            
            return 0;

        }

    }

    public void updateItemAD(String ColumnName1, String MatchText1,String UpdateCol,String UpdateText) {



        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UpdateCol,UpdateText);

        try {

            db.update("AquaDetails",contentValues, ColumnName1 + "=? AND AquariumID =?",new String []{MatchText1,AQUARIUM_ID});

        } catch (Exception e) {
            

        }

    }

    public Cursor getDataAD(String ColumnName1,String MatchText1 ) {

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            Cursor data = db.rawQuery("select * from AquaDetails where "+ColumnName1 + "=? AND AquariumID =?",new String[]{MatchText1,AQUARIUM_ID});
            return data;
        }
        catch (Exception e){
            
            return null;
        }

    }



    public void deleteItemAD(String ColumnName1, String MatchText1) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.delete("AquaDetails", ColumnName1 + "=? AND AquariumID =?",new String []{MatchText1,AQUARIUM_ID});

        } catch (Exception e) {
            

        }

    }

    public long addDataMaD(String kName, String nName, String pName, String caName, String mgName, String sName, String kPpm, String nPpm, String pPpm, String caPpm, String mgPpm, String sPpm, String kWt, String nWt, String pWt, String caWt, String mgWt, String sWt) {

        SQLiteDatabase db = nutrientDbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("AquariumID",AQUARIUM_ID);
        contentValues.put("kName",kName);
        contentValues.put("nName",nName);
        contentValues.put("pName",pName);
        contentValues.put("caName",caName);
        contentValues.put("mgName",mgName);
        contentValues.put("sName",sName);
        contentValues.put("kPpm",kPpm);
        contentValues.put("nPpm",nPpm);
        contentValues.put("pPpm",pPpm);
        contentValues.put("caPpm",caPpm);
        contentValues.put("mgPpm",mgPpm);
        contentValues.put("sPpm",sPpm);
        contentValues.put("kWt",kWt);
        contentValues.put("nWt",nWt);
        contentValues.put("pWt",pWt);
        contentValues.put("caWt",caWt);
        contentValues.put("mgWt",mgWt);
        contentValues.put("sWt",sWt);



        try {
            long row = db.insert("MacroDetails", null, contentValues);
            return row;
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            return 0;

        }
    }

    public Cursor getDataMAD() {

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            return db.rawQuery("select * from MacroDetails where AquariumID =? ",new String[]{AQUARIUM_ID});

        }
        catch (Exception e){
            
            return null;
        }

    }

    public long updateDataMaD(String kName, String nName, String pName, String caName, String mgName, String sName, String kPpm, String nPpm, String pPpm, String caPpm, String mgPpm, String sPpm, String kWt, String nWt, String pWt, String caWt, String mgWt, String sWt) {

        SQLiteDatabase db = nutrientDbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("AquariumID",AQUARIUM_ID);
        contentValues.put("kName",kName);
        contentValues.put("nName",nName);
        contentValues.put("pName",pName);
        contentValues.put("caName",caName);
        contentValues.put("mgName",mgName);
        contentValues.put("sName",sName);
        contentValues.put("kPpm",kPpm);
        contentValues.put("nPpm",nPpm);
        contentValues.put("pPpm",pPpm);
        contentValues.put("caPpm",caPpm);
        contentValues.put("mgPpm",mgPpm);
        contentValues.put("sPpm",sPpm);
        contentValues.put("kWt",kWt);
        contentValues.put("nWt",nWt);
        contentValues.put("pWt",pWt);
        contentValues.put("caWt",caWt);
        contentValues.put("mgWt",mgWt);
        contentValues.put("sWt",sWt);



        try {
            long row = db.update("MacroDetails",contentValues, "AquariumID =?",new String []{AQUARIUM_ID});
            return row;
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            return 0;

        }
    }

    public long addDataMiD(String feName, String mnName, String cuName, String znName, String bName, String moName, String clName, String niName, String coName, String siName, String vName, String seName, String fePpm, String mnPpm, String cuPpm, String znPpm, String bPpm, String moPpm, String clPpm, String niPpm, String coPpm, String siPpm, String vPpm, String sePpm, String feWt, String mnWt, String cuWt, String znWt, String bWt, String moWt, String clWt, String niWt, String coWt, String siWt, String vWt, String seWt){

        SQLiteDatabase db = nutrientDbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put("AquariumID",AQUARIUM_ID);
        contentValues.put("feName",feName);
        contentValues.put("mnName",mnName);
        contentValues.put("cuName",cuName);
        contentValues.put("znName",znName);
        contentValues.put("bName",bName);
        contentValues.put("moName",moName);
        contentValues.put("clName",clName);
        contentValues.put("niName",niName);
        contentValues.put("coName",coName);
        contentValues.put("siName",siName);
        contentValues.put("vName",vName);
        contentValues.put("seName",seName);
        contentValues.put("fePpm",fePpm);
        contentValues.put("mnPpm",mnPpm);
        contentValues.put("cuPpm",cuPpm);
        contentValues.put("znPpm",znPpm);
        contentValues.put("bPpm",bPpm);
        contentValues.put("moPpm",moPpm);
        contentValues.put("clPpm",clPpm);
        contentValues.put("niPpm",niPpm);
        contentValues.put("coPpm",coPpm);
        contentValues.put("siPpm",siPpm);
        contentValues.put("vPpm",vPpm);
        contentValues.put("sePpm",sePpm);
        contentValues.put("feWt",feWt);
        contentValues.put("mnWt",mnWt);
        contentValues.put("cuWt",cuWt);
        contentValues.put("znWt",znWt);
        contentValues.put("bWt",bWt);
        contentValues.put("moWt",moWt);
        contentValues.put("clWt",clWt);
        contentValues.put("niWt",niWt);
        contentValues.put("coWt",coWt);
        contentValues.put("siWt",siWt);
        contentValues.put("vWt",vWt);
        contentValues.put("seWt",seWt);


        try {
            long row = db.insert("MicroDetails", null, contentValues);
            return row;
        }
        catch (Exception e)
        {
            
            return 0;

        }


    }

    public void deleteNutrientTables() {

        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.delete("AquaDetails", "AquariumID =?",new String []{AQUARIUM_ID});
            db.delete("MacroDetails", "AquariumID =?",new String []{AQUARIUM_ID});
            db.delete("MicroDetails", "AquariumID =?",new String []{AQUARIUM_ID});

        } catch (Exception e) {
            

        }


    }





}
