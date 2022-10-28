package com.newage.aquapets.dbhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ChatDBHelper extends SQLiteOpenHelper {


    private Context context;

    private ChatDBHelper(Context context) {
        super(context, "ChatDB", null  , 1);
        this.context=context;
    }

    private static ChatDBHelper chatDBHelper=null;

    public static  ChatDBHelper newInstance(Context context){

        if(chatDBHelper==null) {

            chatDBHelper = new ChatDBHelper(context.getApplicationContext());
        }
        return  chatDBHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {

            db.execSQL("create table ChatDB(id integer primary key,SN integer,DN text,MSG text,PU text,TS text,UID text)");
        }
        catch (Exception e) {
            
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public long addData(SQLiteDatabase db,Long SN,String DN,String MSG,String PU, String TS, String UID){

        ContentValues contentValues = new ContentValues();
        contentValues.put("SN",SN);
        contentValues.put("DN",DN);
        contentValues.put("MSG",MSG);
        contentValues.put("PU",PU);
        contentValues.put("TS",TS);
        contentValues.put("UID",UID);




        try {
            long row = db.insert("ChatDB", null, contentValues);
            return row;
        }
        catch (Exception e)
        {
            
            return 0;

        }

    }
    public Cursor getData(SQLiteDatabase db) {

        try {
            Cursor data = db.rawQuery("select * from ChatDB", null);
            return data;
        } catch (Exception e) {
            
            return null;
        }
    }

    public void updateItem(String ColumnName, Long UpdateText) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {

            ContentValues contentValues = new ContentValues();
            contentValues.put(ColumnName,UpdateText);

            db.update("ChatDB", contentValues,null,null);

        } catch (Exception e) {
            

        }

    }

    public Cursor getDataCondition(String ColumnName, String MatchText) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor data = db.rawQuery("select * from ChatDB where "+ColumnName+"=?",new String[]{MatchText});
            return data;
        }
        catch (Exception e){
            
            return null;
        }


    }
    public Integer deleteData(){

        SQLiteDatabase db = this.getWritableDatabase();
        try {

            return db.delete("ChatDB",null,null);
        } catch (Exception e) {
            

            return 0;
        }

    }
    public void deleteItem(String ColumnName, String MatchText) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.delete("ChatDB", ColumnName+"='"+MatchText+"'", null);

        } catch (Exception e) {
            

        }

    }


}
