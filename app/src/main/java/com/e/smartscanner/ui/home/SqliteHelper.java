package com.e.smartscanner.ui.home;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

public class SqliteHelper extends SQLiteOpenHelper {

    public SqliteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }
    public void insertData(String name,String impath,String path ,String date){

        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO smart VALUES (?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();;

        statement.bindString(1, name);
        statement.bindString(2, impath);
        statement.bindString(3, path);
        statement.bindString(4, date);

        statement.executeInsert();

    }
}
