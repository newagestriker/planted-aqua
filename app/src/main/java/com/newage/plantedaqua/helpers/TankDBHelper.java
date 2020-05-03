package com.newage.plantedaqua.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class TankDBHelper extends SQLiteOpenHelper {
    
    private Context context;

    private TankDBHelper(Context context) {
        super(context, "TankDetails", null  , 2);
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
            db.execSQL("create table TankDetails(id integer,AquariumID text,AquariumName text,ImageUri text,AquariumType text,Price text,Currency text,Volume text,VolumeMetric text,CurrentStatus text,StartupDate text," +//10
                    "DismantleDate text,CO2 text,LightType text,Wattage text,LumensPerWatt text,LightRegion text,AdditionalDetails text,Quality text,Seller text, MicroDosage text, MacroDosage text, DefaultAlarm text default 000, LumensPerGallon text default 0,TankLength text, TankWidth text, TankHeight text, SubstrateDepth text, HeightFromSurface text, LSI text,TotalLumens text default 0, Reflector text,ReflectorEfficiency text,LUX text,ReflectorPosition text default 0)");
            db.execSQL("create table RecoDetails(id integer,AquariumID text,Day text,Date text,Title text,Message text,Visibility text,AquariumName text)");
            db.execSQL("create table LightDetails(id integer primary key,AquariumID text,LightType text,LumensPerWatt text,Count text,WattPerCount text,EffectiveLumens text)");
        }
        catch (Exception e) {
            
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(oldVersion<2) {
            try {
                db.execSQL("alter table TankDetails add column MicroDosage text ");
                db.execSQL("alter table TankDetails add column MacroDosage text");
                db.execSQL("alter table TankDetails add column DefaultAlarm text default 000");
                db.execSQL("alter table TankDetails add column LumensPerGallon text default 0");
                db.execSQL("alter table TankDetails add column TankLength text");
                db.execSQL("alter table TankDetails add column TankWidth text");
                db.execSQL("alter table TankDetails add column TankHeight text");
                db.execSQL("alter table TankDetails add column SubstrateDepth text");
                db.execSQL("alter table TankDetails add column HeightFromSurface text");
                db.execSQL("alter table TankDetails add column LSI text");
                db.execSQL("alter table TankDetails add column TotalLumens text default 0");
                db.execSQL("alter table TankDetails add column Reflector text");
                db.execSQL("alter table TankDetails add column ReflectorEfficiency text");
                db.execSQL("alter table TankDetails add column LUX text");
                db.execSQL("alter table TankDetails add column ReflectorPosition text default 0");
                db.execSQL("create table RecoDetails(id integer,AquariumID text,Day text,Date text,Title text,Message text,Visibility text,AquariumName text)");
                db.execSQL("create table LightDetails(id integer primary key,AquariumID text,LightType text,LumensPerWatt text,Count text,WattPerCount text,EffectiveLumens text)");

            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
    public long addData(SQLiteDatabase db,Long id,String aquariumid,String aquariumname,String imageuri,String aquariumtype,String price,String currency, String volume,String volumemetric,String currentstatus,String startupdate, String dismantledate,
                        String co2,String lighttype,String wattage,String lpw, String additionaldetails){

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
        contentValues.put("AdditionalDetails",additionaldetails);



        try {
            long row = db.insert("TankDetails", null, contentValues);
            return row;
        }
        catch (Exception e)
        {
            
            return 0;

        }

    }
    public Cursor getData(SQLiteDatabase db) {

        try {
            Cursor data = db.rawQuery("select * from TankDetails", null);
            return data;
        } catch (Exception e) {
            
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
                
                return null;
        }


    }
    public Integer deleteData(){

        SQLiteDatabase db = this.getWritableDatabase();
        try {

            return db.delete("TankDetails",null,null);
        } catch (Exception e) {
            

            return 0;
        }

    }
    public void deleteItem(String ColumnName, String MatchText) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.delete("TankDetails", ColumnName+"='"+MatchText+"'", null);

        } catch (Exception e) {


        }

    }
    public void updateItem(SQLiteDatabase db,String aquariumid,String aquariumname,String imageuri,String aquariumtype,String price,String currency, String volume,String volumemetric,String currentstatus,String startupdate, String dismantledate,
                           String co2,String lighttype,String wattage,String lpw,String additionaldetails) {

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
        contentValues.put("AdditionalDetails",additionaldetails);


        try {

            db.update("TankDetails",contentValues, "AquariumID=?",new String[]{aquariumid});

        } catch (Exception e) {
            

        }

    }

    public void updateSingleItem(String ColumnName1, String MatchText,String UpdateCol,String UpdateText) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UpdateCol,UpdateText);

        try {

            db.update("TankDetails",contentValues, ColumnName1+"=?",new String []{MatchText});

        } catch (Exception e) {
            

        }

    }

    //RecoDetails(id integer,AquariumID text,Day text,Date text,Title text,Message text,Visibility text)"

    public void addDataReco(int id, String aquariumID, String dy, String dt, String title, String message, String visibility, String AquariumName){

        SQLiteDatabase db = tankDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",id);
        contentValues.put("AquariumID",aquariumID);
        contentValues.put("Day",dy);
        contentValues.put("Date",dt);
        contentValues.put("Title",title);
        contentValues.put("Message",message);
        contentValues.put("Visibility",visibility);
        contentValues.put("AquariumName",AquariumName);

        try {
            db.insert("RecoDetails", null, contentValues);

        }
        catch (Exception e)
        {
            

        }


    }

    public void updateDataReco(int id, String aquariumid, String dy, String dt, String title, String message, String visibility,String AquariumName){

        SQLiteDatabase db = tankDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",id);
        contentValues.put("AquariumID",aquariumid);
        contentValues.put("Day",dy);
        contentValues.put("Date",dt);
        contentValues.put("Title",title);
        contentValues.put("Message",message);
        contentValues.put("Visibility",visibility);
        contentValues.put("AquariumName",AquariumName);

        try {

            db.update("RecoDetails",contentValues, "AquariumID=?",new String[]{aquariumid});

        } catch (Exception e) {
            

        }



    }

    public void updateDataVisibility(String visibility){

        SQLiteDatabase db = tankDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("Visibility",visibility);

        try {

            db.update("RecoDetails",contentValues, null,null);

        } catch (Exception e) {
            

        }



    }

    public Cursor getDataReco(SQLiteDatabase db) {

        try {
            return db.rawQuery("select * from RecoDetails", null);
        } catch (Exception e) {
            
            return null;
        }
    }

    public Cursor getDataRecoCondition(SQLiteDatabase db,String aquariumID) {

        try {
            return db.rawQuery("select * from RecoDetails where AquariumID=?", new String[]{aquariumID});
        } catch (Exception e) {
            
            return null;
        }
    }


    public void updateDataRecoCondition(String aquariumID,String UpdateCol, String UpdateText) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UpdateCol,UpdateText);

        try {

            db.update("RecoDetails",contentValues, "AquariumID=?",new String []{aquariumID});

        } catch (Exception e) {
            

        }

    }

    public void deleteItemReco(String aquariumID) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.delete("RecoDetails", "AquariumID=?", new String[]{aquariumID});

        } catch (Exception e) {
            

        }

    }

    //LightDetails(id integer,AquariumID text,LightType text,LumensPerWatt text,Count text,WattPerCount text)")

    public long addDataLightDetails( String aquariumID, String LightType, String LumensPerWatt, String Count, String WattPerCount, String EffectiveLumens){

        SQLiteDatabase db = tankDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("AquariumID",aquariumID);
        contentValues.put("LightType",LightType);
        contentValues.put("LumensPerWatt",LumensPerWatt);
        contentValues.put("Count",Count);
        contentValues.put("WattPerCount",WattPerCount);
        contentValues.put("EffectiveLumens",EffectiveLumens);

        try {
            return db.insert("LightDetails", null, contentValues);

        }
        catch (Exception e)
        {
            return -1;

        }



    }

    public Cursor getDataLightCondition(String aquariumID) {

        try {

            SQLiteDatabase db = tankDBHelper.getReadableDatabase();

            return db.rawQuery("select * from LightDetails where AquariumID=?", new String[]{aquariumID});
        } catch (Exception e) {
            
            return null;
        }
    }

    public void deleteItemLight(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.delete("LightDetails", "id="+id, null);

        } catch (Exception e) {
            

        }

    }
    public void deleteItemLight(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.delete("LightDetails", "AquariumID=?", new String[]{id});

        } catch (Exception e) {
            

        }

    }


    public void updateDataLightDetails( long id, String EffectiveLumens){

        SQLiteDatabase db = tankDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("EffectiveLumens",EffectiveLumens);

        try {
            db.update("LightDetails",contentValues,"id="+id,null);

        }
        catch (Exception e)
        {
            

        }


    }


}