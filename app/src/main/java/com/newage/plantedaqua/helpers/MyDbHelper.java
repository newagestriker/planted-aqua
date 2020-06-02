package com.newage.plantedaqua.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDbHelper extends SQLiteOpenHelper {

    private static MyDbHelper mydbhelper = null;
    private String DB;
    private Context context;

    private MyDbHelper(Context context, String DB) {
        super(context, DB, null, 2);
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

        //region ATTable
        try {

            db.execSQL("create table ATTable(Alarm_Name text, Alarm_Type text, alarmInMillis text, alarmID integer, dayNo integer, hour integer, min integer, Notify_Type text, Alarm_Slot integer, Alarm_day text, Alarm_time text)");

        } catch (Exception e) {
            
        }
        //endregion

        //region AlarmNames
        try {
            db.execSQL("create table AlarmNames(AlarmName text,Category text,AlarmText text,Slot integer,AlarmDays text,AlarmDate text,AlarmFreq text,AdditionalInfo text)");
        }
        catch (Exception e) {
            
        }
        //endregion

        //region AlbumDetails
        try {
            db.execSQL("create table AlbumDetails(id integer primary key autoincrement,ImageURI text,ImageDate text,ImageDay text,Notes text,Learning text)");
        }
        catch (Exception e) {
            
        }
        //endregion

        try {
            db.execSQL("create table Logs(Log_Day text,Log_Date text,Log_Task text,Log_Category text,Log_Status text, AlarmInfo text, AdditionalInfo text)");
        }
        catch (Exception e) {
            
        }
        try {
            db.execSQL("create table TankItems(I_ID text,I_Name text,I_Category text,I_URI text,I_Currency text,I_Price real,I_Quantity integer,I_BuyDate text,I_ExpDate text,I_Gender text,I_Food text,I_Care text,I_Quality text,I_Remarks text,I_Sci_Name text,I_Seller text)");
        }
        catch (Exception e) {
            
        }
        try {
            db.execSQL("create table URI(I_ID text,U_URI text,U_Date text,U_Notes text)");
        }
        catch (Exception e) {
            
        }
        try {
            db.execSQL("create table Expense(E_ID text,E_TankName text,E_Item text,E_CurrencySymbol text,E_Price real,E_Quantity integer,E_BuyDate text,E_Seller text,E_Quality text,E_Notes text)");
        }
        catch (Exception e) {
            
        }

        try {
            db.execSQL("create table MacroLogs(Date text,kPpm text default 0, nPpm text default 0, pPpm text default 0, caPpm text default 0, mgPpm text default 0, sPpm text default 0, macroStatus text, waterChangePercent text default 50, uptakeRate default 80)");
            db.execSQL("create table MicroLogs(Date text,fePpm text, mnPpm text, cuPpm text, znPpm text, bPpm text, moPpm text, clPpm text, niPpm text, coPpm text, siPpm text, vPpm text, sePpm text, microStatus text, waterChangePercent text default 50,uptakeRate default 80)");
        }

        catch (Exception e) {
            
        }


    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<2) {
            try {
                    db.execSQL("alter table Logs add column AlarmInfo text");
                    db.execSQL("alter table Logs add column AdditionalInfo text");
                    db.execSQL("alter table AlarmNames add column AlarmFreq text");
                    db.execSQL("alter table AlarmNames add column AdditionalInfo text");

                    db.execSQL("create table MacroLogs(Date text,kPpm text default 0, nPpm text default 0, pPpm text default 0, caPpm text default 0, mgPpm text default 0, sPpm text default 0, macroStatus text, waterChangePercent text default 50, uptakeRate default 80)");
                    db.execSQL("create table MicroLogs(Date text,fePpm text, mnPpm text, cuPpm text, znPpm text, bPpm text, moPpm text, clPpm text, niPpm text, coPpm text, siPpm text, vPpm text, sePpm text, microStatus text, waterChangePercent text default 50,uptakeRate default 80)");


            } catch (Exception e) {
                
            }
        }
    }

    public void clearATTable(SQLiteDatabase db){

        try {
            db.execSQL("drop table if exists ATTable");
            db.execSQL("create table ATTable(Alarm_Name text, Alarm_Type text, alarmInMillis text, alarmID integer, dayNo integer, hour integer, min integer, Notify_Type text, Alarm_Slot integer)");
        }
        catch (Exception e) {
            
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
            
            return 0;
        }


    }

    public void updateTimeInMillis(SQLiteDatabase db,String item1,String MatchText1) {




        ContentValues contentValues = new ContentValues();

        contentValues.put("alarmInMillis", item1);

        try {

            db.update("ATTable",contentValues, "alarmInMillis=? ",new String []{MatchText1});

        } catch (Exception e) {
            

        }


    }

    public Cursor getData(SQLiteDatabase db) {

        try {
            Cursor data = db.rawQuery("select * from ATTable", null);
            return data;
        } catch (Exception e) {
            
            return null;
        }

    }

    public Integer deleteTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            return db.delete("ATTable", null, null);
        } catch (Exception e) {
            
            return 0;
        }


    }

    public void deleteItem(String ColumnName1, String MatchText1,String ColumnName2,String MatchText2) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.delete("ATTable", ColumnName1+"=? AND "+ColumnName2+"=?",new String[]{MatchText1,MatchText2});

        } catch (Exception e) {
            

        }

    }

    public Cursor getData2Condition(String ColumnName1, String MatchText1,String ColumnName2, String MatchText2) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor data = db.rawQuery("select * from ATTable where "+ColumnName1+"=? AND "+ColumnName2+"=?",new String[]{MatchText1,MatchText2});
            return data;
        }
        catch (Exception e){
            
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
            
            return 0;

        }

    }

    public Cursor getDataANCondition(SQLiteDatabase db,String ColumnName, String MatchText) {

        try {
            Cursor data = db.rawQuery("select * from AlarmNames where "+ColumnName+"=?", new String[]{MatchText});
            return data;
        }
        catch (Exception e){
            
            return null;
        }

    }

    public Cursor getDataAN2Condition(SQLiteDatabase db,String ColumnName1, String MatchText1,String ColumnName2, String MatchText2) {

        try {
            Cursor data = db.rawQuery("select * from AlarmNames where "+ColumnName1+"=? AND "+ColumnName2+"=?",new String[]{MatchText1,MatchText2});
            return data;
        }
        catch (Exception e){
            
            return null;
        }

    }

    public Cursor getDataAN(SQLiteDatabase db) {

        try {
            Cursor data = db.rawQuery("select * from AlarmNames",null);
            return data;
        }
        catch (Exception e){
            
            return null;
        }

    }

    public Integer deleteDataAN(){

        SQLiteDatabase db = this.getWritableDatabase();
        try {

            return db.delete("AlarmNames",null,null);
        } catch (Exception e) {
            

            return 0;
        }

    }

    public void deleteItemAN(String ColumnName1, String MatchText1,String ColumnName2, String MatchText2) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.delete("AlarmNames", ColumnName1+"=? AND "+ColumnName2+" =?",new String []{MatchText1,MatchText2});

        } catch (Exception e) {
            

        }

    }

    public void updateItemAN(String ColumnName1, String MatchText1,String ColumnName2, String MatchText2,String UpdateCol,String UpdateText) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UpdateCol,UpdateText);

        try {

            db.update("AlarmNames",contentValues, ColumnName1+"=? AND "+ColumnName2+" =?",new String []{MatchText1,MatchText2});

        } catch (Exception e) {
            

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
            


        }

    }

    public Cursor getDataAD(SQLiteDatabase db) {

        try {
            Cursor data = db.rawQuery("select * from AlbumDetails", null);
            return data;
        }
        catch (Exception e){
            
            return null;
        }



    }

    public Integer deleteDataAD(){

        SQLiteDatabase db = this.getWritableDatabase();
        try {

            return db.delete("AlbumDetails",null,null);
        } catch (Exception e) {
            

            return 0;
        }

    }

    public void deleteItemAD(String ColumnName, String MatchText) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.delete("AlbumDetails", ColumnName+"='"+MatchText+"'", null);

        } catch (Exception e) {
            

        }

    }

    public Cursor getDataLogs() {

        SQLiteDatabase db=getWritableDatabase();

        try {
            Cursor data = db.rawQuery("select * from Logs order by  datetime(Log_Date) desc", null);
            return data;
        } catch (Exception e) {
            
            return null;
        }

    }


    public Integer deleteDataLogs(){

        SQLiteDatabase db = this.getWritableDatabase();
        try {

            return db.delete("Logs",null,null);
        } catch (Exception e) {
            

            return 0;
        }

    }

    public Cursor getDataLogs1Condition(SQLiteDatabase db,String ColumnName1, String MatchText1) {

        try {
            Cursor data = db.rawQuery("select * from Logs where "+ColumnName1+"=?",new String[]{MatchText1});
            return data;
        }
        catch (Exception e){
            
            return null;
        }

    }

    public void updateItemLogs(String ColumnName1, String MatchText1,String ColumnName2, String MatchText2,String UpdateCol,String UpdateText) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UpdateCol,UpdateText);

        try {

            db.update("Logs",contentValues, ColumnName1+"=? AND "+ColumnName2+" =?",new String []{MatchText1,MatchText2});

        } catch (Exception e) {
            

        }

    }

    //create table Logs(Log_Day text,Log_Date text,Log_Task text,Log_Category text,Log_Status text, AlarmInfo text, AdditionalInfo text)"

    public void updateItemLogsUsingDate(String date,String UpdateCol,String UpdateText) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UpdateCol,UpdateText);

        try {

            db.update("Logs",contentValues, "Log_Date=?",new String []{date});

        } catch (Exception e) {
            

        }

    }

    public void deleteItemLogs(String ColumnName1, String MatchText1,String ColumnName2, String MatchText2) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.delete("Logs", ColumnName1+"=? AND "+ColumnName2+" =?",new String []{MatchText1,MatchText2});

        } catch (Exception e) {
            

        }

    }

    public void deleteItemLogsUsingDate(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.delete("Logs", "Log_Date=?",new String []{date});

        } catch (Exception e) {
            

        }

    }

    public Cursor getDataLogsCondition(String ColumnName, String MatchText) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor data = db.rawQuery("select * from Logs where "+ColumnName+"=?", new String[]{MatchText});
            return data;
        }
        catch (Exception e){
            
            return null;
        }

    }

    //AlarmFreq text,AlarmInfo text,AdditionalInfo text

    public long addDataLogs(String dy, String dt,String task,String category,String status, String alarmInfo, String addInfo)  {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Log_Day", dy);
        contentValues.put("Log_Date", dt);
        contentValues.put("Log_Task", task);
        contentValues.put("Log_Category", category);
        contentValues.put("Log_Status", status);
        contentValues.put("AlarmInfo", alarmInfo);
        contentValues.put("AdditionalInfo", addInfo);


        try {
            long row = db.insert("Logs", null, contentValues);
            //System.out.println("row number is : "+row);
            return row;
        } catch (Exception e) {
            
            return 0;
        }


    }

    public void addDataTI(SQLiteDatabase db,String id,String name,String category,String uriName,String cur,float price,Integer quantity,String buyDate,String expDate,String gender,String food,String care,String remarks,String sciName){

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
            


        }

    }
    public Cursor getDataTI(SQLiteDatabase db) {

        try {
            Cursor data = db.rawQuery("select * from TankItems", null);
            return data;
        }
        catch (Exception e){
            
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
            
            return null;
        }

    }
    public void updateItemTI(SQLiteDatabase db,String id,String name,String category,String uriName,String cur,float price,Integer quantity,String buyDate,String expDate,String gender,String food,String care,String remarks,String sciName) {

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
            

        }

    }

    public Cursor getDataTI2Condition(SQLiteDatabase db,String ColumnName1, String MatchText1,String ColumnName2, String MatchText2) {

        try {
            Cursor data = db.rawQuery("select * from TankItems where "+ColumnName1+"=? AND "+ColumnName2+"=?",new String[]{MatchText1,MatchText2});
            return data;
        }
        catch (Exception e){
            
            return null;
        }

    }
    public void deleteItemTI(String ColumnName, String MatchText) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.delete("TankItems", ColumnName+"='"+MatchText+"'", null);

        } catch (Exception e) {
            

        }

    }
    public void updateItemTISingleItem(String id,String ColumnName,String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ColumnName, item);
        try {

            db.update("TankItems",contentValues, "I_ID=?",new String[]{id});

        } catch (Exception e) {
            

        }

    }

//db.execSQL("create table MacroLogs(Date text,kPpm text default 0, nPpm text default 0, pPpm text default 0, caPpm text default 0, mgPpm text default 0, sPpm text default 0, macroStatus text, waterChangePercent text default 50, uptakeRate default 80)");

    public void addDataMaL(String Date, String kPPM, String nPPM, String pPPM, String caPPM, String mgPPM, String sPPM, String macroStatus, String waterChangePercent, String uptakeRate ){

        SQLiteDatabase db = mydbhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Date",Date);
        contentValues.put("kPPM",kPPM);
        contentValues.put("nPPM", nPPM);
        contentValues.put("pPPM",pPPM);
        contentValues.put("caPPM",caPPM);
        contentValues.put("mgPPM",mgPPM);
        contentValues.put("sPPM",sPPM);
        contentValues.put("macroStatus",macroStatus);
        contentValues.put("waterChangePercent",waterChangePercent);
        contentValues.put("uptakeRate",uptakeRate);



        try {
            db.insert("MacroLogs", null, contentValues);

        }
        catch (Exception e)
        {
            


        }

    }

    public void updateDataMaL(String Date, String kPPM, String nPPM, String pPPM, String caPPM, String mgPPM, String sPPM, String macroStatus, String waterChangePercent, String uptakeRate){

        SQLiteDatabase db = mydbhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("kPPM",kPPM);
        contentValues.put("nPPM", nPPM);
        contentValues.put("pPPM",pPPM);
        contentValues.put("caPPM",caPPM);
        contentValues.put("mgPPM",mgPPM);
        contentValues.put("sPPM",sPPM);
        contentValues.put("macroStatus",macroStatus);
        contentValues.put("waterChangePercent",waterChangePercent);
        contentValues.put("uptakeRate",uptakeRate);



        try {
            db.update("MacroLogs",contentValues,"Date=?",new String[]{Date});

        }
        catch (Exception e)
        {
            


        }

    }

    public Cursor getDataMaL(){

        SQLiteDatabase db = mydbhelper.getReadableDatabase();



        try {
              return db.rawQuery("Select * from MacroLogs",null,null);

        }
        catch (Exception e)
        {
            

            return null;
        }

    }
    public Cursor getDataMaLDayWise(String timeStamp){

        SQLiteDatabase db = mydbhelper.getReadableDatabase();



        try {
            return db.rawQuery("Select * from MacroLogs where Date=?",new String[]{timeStamp});

        }
        catch (Exception e)
        {
            

            return null;
        }

    }

    public void updateStatusMaL(String macroStatus, String matchDate){

        SQLiteDatabase db = mydbhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("macroStatus",macroStatus);



        try {
            db.update("MacroLogs",contentValues,"Date=?",new String[]{matchDate});

        }
        catch (Exception e)
        {
            


        }

    }

    public void deleteItemMaL(String matchDate){

        SQLiteDatabase db = mydbhelper.getWritableDatabase();


        try {
            db.delete("MacroLogs","Date=?",new String[]{matchDate});

        }
        catch (Exception e)
        {
            


        }

    }


    public void deleteAllMaL(){

        SQLiteDatabase db = mydbhelper.getWritableDatabase();


        try {
            db.delete("MacroLogs",null,null);

        }
        catch (Exception e)
        {
            


        }

    }

    public Cursor last20MacroTable(){

        SQLiteDatabase db = mydbhelper.getReadableDatabase();

        try {
            return db.rawQuery("SELECT * FROM MacroLogs ORDER BY date(Date) ASC LIMIT 32", null);
        }
        catch (Exception e)
        {
            
            return null;

        }
    }
    public void deleteRowsMacroTable(long row){

        SQLiteDatabase db = mydbhelper.getReadableDatabase();
        String query = "DELETE FROM MacroLogs WHERE Date IN (SELECT Date FROM MacroLogs ORDER BY date(Date) ASC LIMIT "+row+")";
        try {
                db.execSQL(query);

        }
        catch (Exception e)
        {

        }
    }
    public void copyPreviousRowMal(String preTimeStamp,String postTimeStamp){

        SQLiteDatabase db = mydbhelper.getReadableDatabase();



        try {
            db.execSQL("insert into MacroLogs(Date,kPpm,nPpm,pPpm,caPpm,mgPpm,sPpm) select ?,kPpm,nPpm,pPpm,caPpm,mgPpm,sPpm from your_table where Date = ?",new String[]{postTimeStamp,preTimeStamp});



        }
        catch (Exception e)
        {
            

        }

    }


}





