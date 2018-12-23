package com.newage.plantedaqua;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class MyDbHelper extends SQLiteOpenHelper {

    private static MyDbHelper mydbhelper = null;
    private String DB;
    private Context context;

    private MyDbHelper(Context context, String DB) {
        super(context, DB, null, 1);
        this.context=context;
        this.DB=DB;
    }

    public static MyDbHelper newInstance(Context context, String DB){

       if(mydbhelper!=null&&mydbhelper.DB.equals(DB)){

           return mydbhelper;

       }
       else{
           mydbhelper=new MyDbHelper(context.getApplicationContext(),DB);
           return mydbhelper;
       }


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {

            db.execSQL("create table ATTable(Alarm_Name text, Alarm_Type text, alarmInMillis text, alarmID integer, dayNo integer, hour integer, min integer, Notify_Type text, Alarm_Slot integer, Alarm_day text, Alarm_time text)");

        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }

        try {
            db.execSQL("create table AlarmNames(AlarmName text,Category text,AlarmText text,Slot integer,AlarmDays text,AlarmDate text)");
        }
        catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        try {
            db.execSQL("create table AlbumDetails(id integer primary key autoincrement,ImageURI text,ImageDate text,ImageDay text,Notes text,Learning text)");
        }
        catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        try {
            db.execSQL("create table Logs(Log_Day text,Log_Date text,Log_Task text,Log_Category text,Log_Status text)");
        }
        catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        try {
            db.execSQL("create table TankItems(I_ID text,I_Name text,I_Category text,I_URI text,I_Currency text,I_Price real,I_Quantity integer,I_BuyDate text,I_ExpDate text,I_Gender text,I_Food text,I_Care text,I_Quality text,I_Remarks text,I_Sci_Name text,I_Seller text)");
        }
        catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        try {
            db.execSQL("create table URI(I_ID text,U_URI text,U_Date text,U_Notes text)");
        }
        catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        try {
            db.execSQL("create table Expense(E_ID text,E_TankName text,E_Item text,E_CurrencySymbol text,E_Price real,E_Quantity integer,E_BuyDate text,E_Seller text,E_Quality text,E_Notes text)");
        }
        catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }


    }



    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*if(oldVersion<2) {
            try {
                db.execSQL("create table Expense(E_ID text,E_Name text,E_Category text,E_Price real,E_BuyDate text,E_ExpDate text,Remarks text)");

            } catch (Exception e) {
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
        if(oldVersion<3){
            try {
                db.execSQL("create table URI(U_ID text,URI text)");
            }
            catch (Exception e) {
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }*/
        try {
            db.execSQL("drop table if exists ATTable");
            onCreate(db);
        }
        catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }




    }

    public long addData(SQLiteDatabase db,String item6, String item0, String item1, int item2, int item3, int item4, int item5,String item7,int item8) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("Alarm_Name", item6);
        contentValues.put("Alarm_Type", item0);
        contentValues.put("alarmInMillis", item1);
        contentValues.put("alarmID", item2);
        contentValues.put("dayNo", item3);
        contentValues.put("hour", item4);
        contentValues.put("min", item5);
        contentValues.put("Notify_Type", item7);
        contentValues.put("Alarm_Slot", item8);
        try {
            long row = db.insert("ATTable", null, contentValues);
            //System.out.println("row number is : "+row);
            return row;
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            return 0;
        }


    }

    public Cursor getData(SQLiteDatabase db) {

        try {
            Cursor data = db.rawQuery("select * from ATTable", null);
            return data;
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            return null;
        }

    }

    public Integer deleteTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            return db.delete("ATTable", null, null);
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            return 0;
        }


    }

    public void deleteItem(String ColumnName1, String MatchText1,String ColumnName2,String MatchText2) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.delete("ATTable", ColumnName1+"=? AND "+ColumnName2+"=?",new String[]{MatchText1,MatchText2});

        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();

        }

    }

    public Cursor getData2Condition(String ColumnName1, String MatchText1,String ColumnName2, String MatchText2) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor data = db.rawQuery("select * from ATTable where "+ColumnName1+"=? AND "+ColumnName2+"=?",new String[]{MatchText1,MatchText2});
            return data;
        }
        catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            return null;
        }


    }

    public long addDataAN(SQLiteDatabase db,String alarmName,String category,String alarmText,Integer slot,String AlarmDays,String AlarmDate){

        ContentValues contentValues = new ContentValues();
        contentValues.put("AlarmName",alarmName);
        contentValues.put("Category",category);
        contentValues.put("AlarmText",alarmText);
        contentValues.put("Slot",slot);
        contentValues.put("AlarmDays",AlarmDays);
        contentValues.put("AlarmDate",AlarmDate);
        try {
            long row = db.insert("AlarmNames", null, contentValues);
            return row;
        }
        catch (Exception e)
        {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            return 0;

        }

    }
    public Cursor getDataANCondition(SQLiteDatabase db,String ColumnName, String MatchText) {

        try {
            Cursor data = db.rawQuery("select * from AlarmNames where "+ColumnName+"=?", new String[]{MatchText});
            return data;
        }
        catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            return null;
        }

    }
    public Cursor getDataAN2Condition(SQLiteDatabase db,String ColumnName1, String MatchText1,String ColumnName2, String MatchText2) {

        try {
            Cursor data = db.rawQuery("select * from AlarmNames where "+ColumnName1+"=? AND "+ColumnName2+"=?",new String[]{MatchText1,MatchText2});
            return data;
        }
        catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            return null;
        }

    }
    public Cursor getDataAN(SQLiteDatabase db) {

        try {
            Cursor data = db.rawQuery("select * from AlarmNames",null);
            return data;
        }
        catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            return null;
        }

    }
    public Integer deleteDataAN(){

        SQLiteDatabase db = this.getWritableDatabase();
        try {

            return db.delete("AlarmNames",null,null);
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();

            return 0;
        }

    }
    public void deleteItemAN(String ColumnName1, String MatchText1,String ColumnName2, String MatchText2) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.delete("AlarmNames", ColumnName1+"=? AND "+ColumnName2+" =?",new String []{MatchText1,MatchText2});

        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();

        }

    }
    public void updateItemAN(String ColumnName1, String MatchText1,String ColumnName2, String MatchText2,String UpdateCol,String UpdateText) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UpdateCol,UpdateText);

        try {

            db.update("AlarmNames",contentValues, ColumnName1+"=? AND "+ColumnName2+" =?",new String []{MatchText1,MatchText2});

        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();

        }

    }

    public void addDataAD(SQLiteDatabase db,String imageuri,String imagedate,String imageday,String notes,String learning){

        ContentValues contentValues = new ContentValues();
        contentValues.put("ImageURI",imageuri);
        contentValues.put("ImageDate", imagedate);
        contentValues.put("ImageDay",imageday);
        contentValues.put("Notes",notes);
        contentValues.put("Learning",learning);


        try {
            db.insert("AlbumDetails", null, contentValues);

        }
        catch (Exception e)
        {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();


        }

    }
    public Cursor getDataAD(SQLiteDatabase db) {

        try {
            Cursor data = db.rawQuery("select * from AlbumDetails", null);
            return data;
        }
        catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            return null;
        }



    }
    public Integer deleteDataAD(){

        SQLiteDatabase db = this.getWritableDatabase();
        try {

            return db.delete("AlbumDetails",null,null);
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();

            return 0;
        }

    }
    public void deleteItemAD(String ColumnName, String MatchText) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.delete("AlbumDetails", ColumnName+"='"+MatchText+"'", null);

        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();

        }

    }

    public Cursor getDataLogs() {

        SQLiteDatabase db=getWritableDatabase();

        try {
            Cursor data = db.rawQuery("select * from Logs", null);
            return data;
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            return null;
        }

    }

    public Integer deleteDataLogs(){

        SQLiteDatabase db = this.getWritableDatabase();
        try {

            return db.delete("Logs",null,null);
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();

            return 0;
        }

    }

    public void updateItemLogs(String ColumnName1, String MatchText1,String ColumnName2, String MatchText2,String UpdateCol,String UpdateText) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UpdateCol,UpdateText);

        try {

            db.update("Logs",contentValues, ColumnName1+"=? AND "+ColumnName2+" =?",new String []{MatchText1,MatchText2});

        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();

        }

    }
    public Cursor getDataLogsCondition(String ColumnName, String MatchText) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor data = db.rawQuery("select * from Logs where "+ColumnName+"=?", new String[]{MatchText});
            return data;
        }
        catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            return null;
        }

    }

    public long addDataLogs(String dy, String dt,String task,String category,String status)  {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Log_Day", dy);
        contentValues.put("Log_Date", dt);
        contentValues.put("Log_Task", task);
        contentValues.put("Log_Category", category);
        contentValues.put("Log_Status", status);


        try {
            long row = db.insert("Logs", null, contentValues);
            //System.out.println("row number is : "+row);
            return row;
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            return 0;
        }


    }

    public void addDataTI(SQLiteDatabase db,String id,String name,String category,String uriName,String cur,Double price,Integer quantity,String buyDate,String expDate,String gender,String food,String care,String remarks,String sciName){

        ContentValues contentValues = new ContentValues();
        contentValues.put("I_ID",id);
        contentValues.put("I_Name", name);
        contentValues.put("I_Category",category);
        contentValues.put("I_URI",uriName);
        contentValues.put("I_Currency",cur);
        contentValues.put("I_Price",price);
        contentValues.put("I_Quantity",quantity);
        contentValues.put("I_BuyDate",buyDate);
        contentValues.put("I_ExpDate",expDate);
        contentValues.put("I_Gender",gender);
        contentValues.put("I_Food",food);
        contentValues.put("I_Care",care);
        contentValues.put("I_Remarks",remarks);
        contentValues.put("I_Sci_Name",sciName);



        try {
            db.insert("TankItems", null, contentValues);

        }
        catch (Exception e)
        {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();


        }

    }
    public Cursor getDataTI(SQLiteDatabase db) {

        try {
            Cursor data = db.rawQuery("select * from TankItems", null);
            return data;
        }
        catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            return null;
        }



    }
    public Cursor getDataTICondition(String ColumnName, String MatchText) {
        SQLiteDatabase db=getWritableDatabase();
        try {
            Cursor data = db.rawQuery("select * from TankItems where "+ColumnName+"=?", new String[]{MatchText});
            return data;
        }
        catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            return null;
        }

    }
    public void updateItemTI(SQLiteDatabase db,String id,String name,String category,String uriName,String cur,Double price,Integer quantity,String buyDate,String expDate,String gender,String food,String care,String remarks,String sciName) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("I_Name", name);
        contentValues.put("I_Category",category);
        contentValues.put("I_URI",uriName);
        contentValues.put("I_Currency",cur);
        contentValues.put("I_Price",price);
        contentValues.put("I_Quantity",quantity);
        contentValues.put("I_BuyDate",buyDate);
        contentValues.put("I_ExpDate",expDate);
        contentValues.put("I_Gender",gender);
        contentValues.put("I_Food",food);
        contentValues.put("I_Care",care);
        contentValues.put("I_Remarks",remarks);
        contentValues.put("I_Sci_Name",sciName);

        try {

            db.update("TankItems",contentValues, "I_ID=?",new String[]{id});

        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();

        }

    }

    public Cursor getDataTI2Condition(SQLiteDatabase db,String ColumnName1, String MatchText1,String ColumnName2, String MatchText2) {

        try {
            Cursor data = db.rawQuery("select * from TankItems where "+ColumnName1+"=? AND "+ColumnName2+"=?",new String[]{MatchText1,MatchText2});
            return data;
        }
        catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            return null;
        }

    }
    public void deleteItemTI(String ColumnName, String MatchText) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.delete("TankItems", ColumnName+"='"+MatchText+"'", null);

        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();

        }

    }
    public void updateItemTISingleItem(String id,String ColumnName,String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ColumnName, item);
        try {

            db.update("TankItems",contentValues, "I_ID=?",new String[]{id});

        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();

        }

    }

}





