package com.newage.plantedaqua.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ExpenseDBHelper extends SQLiteOpenHelper {

    private static ExpenseDBHelper expenseDBHelper=null;

    Context context;


    private  ExpenseDBHelper(Context context){

        super(context, "ExpenseDB", null  , 1);
        this.context = context;


    }

    public static ExpenseDBHelper getInstance(Context context){



        if (expenseDBHelper == null){

            expenseDBHelper = new ExpenseDBHelper(context.getApplicationContext());
        }

        return expenseDBHelper;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        try{
            db.execSQL("create table expense_table(id integer primary key autoincrement,ItemID text,TankName text,ItemName text,Day integer,Month integer,Year integer,ShownDate text,TimeInMIllis integer,Quantity integer,Price real,TotalPrice real,AdditionalInfo text, AquariumID text)");
        }

        catch (Exception e){
            
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean checkExists(String id){

        SQLiteDatabase db = expenseDBHelper.getReadableDatabase();
        boolean exists = false;

        Cursor c = db.rawQuery("select * from expense_table where ItemID=?",new String[]{id});
        if(c!=null){

            exists = c.getCount()>0;
            c.close();
        }


        return exists;
    }

    public Cursor getDataExpense(SQLiteDatabase db) {

        try {
            Cursor data = db.rawQuery("select * from expense_table order by date(ShownDate) desc", null);
            return data;
        } catch (Exception e) {
            
            return null;
        }
    }

    //id integer primary key autoincrement,AquariumID text,TankName text,ItemName text,Day text,Month text,Year text,ShownDate text,TimeInMIllis integer,Quantity integer,Price real,TotalPrice real,AdditionalInfo text)


    public void addDataExpense(SQLiteDatabase db,String itemID,String TankName,String ItemName,int Day, int Month, int Year, String ShownDate, long TimeInMIllis, int Quantity, float Price, float TotalPrice, String AdditionalInfo, String aquariumID ) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("ItemID",itemID);
        contentValues.put("TankName",TankName);
        contentValues.put("ItemName", ItemName);
        contentValues.put("Day",Day);
        contentValues.put("Month",Month);
        contentValues.put("Year",Year);
        contentValues.put("ShownDate",ShownDate);
        contentValues.put("TimeInMIllis",TimeInMIllis);
        contentValues.put("Quantity",Quantity);
        contentValues.put("Price",Price);
        contentValues.put("TotalPrice",TotalPrice);
        contentValues.put("AdditionalInfo",AdditionalInfo);
        contentValues.put("AquariumID",aquariumID);

        try {

            db.insert("expense_table",null,contentValues);

        } catch (Exception e) {
            

        }

    }

    public void updateDataExpense(SQLiteDatabase db,String AquariumID,String TankName,String ItemName,int Day, int Month, int Year, String ShownDate, long TimeInMIllis, int Quantity, float Price, float TotalPrice, String AdditionalInfo, String aquariumID ) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("ItemID",AquariumID);
        contentValues.put("TankName",TankName);
        contentValues.put("ItemName", ItemName);
        contentValues.put("Day",Day);
        contentValues.put("Month",Month);
        contentValues.put("Year",Year);
        contentValues.put("ShownDate",ShownDate);
        contentValues.put("TimeInMIllis",TimeInMIllis);
        contentValues.put("Quantity",Quantity);
        contentValues.put("Price",Price);
        contentValues.put("TotalPrice",TotalPrice);
        contentValues.put("AdditionalInfo",AdditionalInfo);
        contentValues.put("AquariumID",aquariumID);

        try {

            db.update("expense_table",contentValues,"ItemID=?",new String[]{AquariumID});

        } catch (Exception e) {
            

        }

    }

    public void updateExpenseItem(String matchCol,String matchText,String updateCol, String updateText){

        SQLiteDatabase db = expenseDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(updateCol,updateText);

        try{

            db.update("expense_table",contentValues,matchCol+"=?",new String[]{matchText});

        }
        catch (Exception e){

        }
    }

    public void deleteExpense(String column,String value){

        SQLiteDatabase db = expenseDBHelper.getWritableDatabase();

        try{

            db.delete("expense_table",column+"=?",new String[]{value});

        }
        catch (Exception e){
            
        }
    }

    public void deleteAllExpense(){

        SQLiteDatabase db = expenseDBHelper.getWritableDatabase();

        try{

            db.delete("expense_table",null,null);

        }
        catch (Exception e){

        }
    }
    //id integer primary key autoincrement,AquariumID text,TankName text,ItemName text,Day text,Month text,Year text,ShownDate text,TimeInMIllis integer,Quantity integer,Price real,TotalPrice real,AdditionalInfo text)

    public Cursor getFilteredDataExpense(int var1, int var2, int var3) {

        SQLiteDatabase db = expenseDBHelper.getReadableDatabase();

        try {
            Cursor data = db.rawQuery("select * from expense_table where Day="+var1+"and Month="+var2+"and Year="+var3+" order by date(ShownDate) desc", null);
            return data;
        } catch (Exception e) {

            return null;
        }
    }

    public Cursor getFilteredByDataExpense(String startDate, String endDate) {

        SQLiteDatabase db = expenseDBHelper.getReadableDatabase();
        String query = "select * from expense_table where date(ShownDate) between date('"+startDate+"') and date('"+endDate+"') order by date(ShownDate) desc";



        try {
            Cursor data = db.rawQuery(query, null);
            return data;
        } catch (Exception e) {

            return null;
        }
    }


    public float getFilteredNetExpense(String startDate, String endDate) {

        SQLiteDatabase db = expenseDBHelper.getReadableDatabase();
        String query = "select sum(TotalPrice) as Total from expense_table where date(ShownDate) between date('" + startDate + "') and date('" + endDate + "') order by date(ShownDate) desc";


        try {

            float sum = 0f;

            Cursor cursor = db.rawQuery(query, null);

            if (cursor != null) {

                if (cursor.moveToFirst()) {
                    sum = cursor.getFloat(cursor.getColumnIndex("Total"));

                }
                cursor.close();
            }
            return sum;

        }
        catch (Exception e) {

            return -1f;
        }

    }

    public float getExpenseperMonth(int month) {

        SQLiteDatabase db = getInstance(context).getReadableDatabase();

        String query;

        if(month==-1){
            query = "select sum(TotalPrice) as Total from expense_table";
        }
        else {

            query = "select sum(TotalPrice) as Total from expense_table where Month="+month;
        }

        try {

            float sum =0f;

            Cursor cursor =  db.rawQuery(query,null);

            if(cursor!=null) {

                if (cursor.moveToFirst()) {
                    sum = cursor.getFloat(cursor.getColumnIndex("Total"));

                }
                cursor.close();

            }
            return sum;



        } catch (Exception e) {

            return -1f;
        }

    }

    public Cursor getExpenseDataPerMonth(int month) {

        SQLiteDatabase db = expenseDBHelper.getReadableDatabase();

        String query;

        query = "select * from expense_table where Month="+month;


        try {


            Cursor cursor =  db.rawQuery(query,null);


            return cursor;



        } catch (Exception e) {

            return null;
        }

    }




}
