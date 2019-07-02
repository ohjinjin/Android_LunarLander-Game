package com.example.lunarlandergame;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class SingletonDB {
    private static SingletonDB instance;
    public static int rowCount;
    //int rowCnt = 0;
    DBHelper helper;
    SQLiteDatabase db;

    public static SingletonDB getInstance(Context context){
        if (instance == null){
            instance = new SingletonDB(context);
        }
        return instance;
    }

    private SingletonDB(Context context){
        helper = new DBHelper(context);
        try {
            db = helper.getWritableDatabase();
        } catch(SQLiteException ex) {
            db = helper.getReadableDatabase();
        }
    }

    public void refreshRowCount(){
        Cursor c = this.db.rawQuery("select * from rankingtable",null);
        rowCount = c.getCount();
   }

   public int getRowCount(){
       return rowCount;
   }
}
