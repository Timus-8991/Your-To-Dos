package com.example.sumit.todo3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sumit on 07/05/2017.
 */

public class ToDoAppOpenHelper extends SQLiteOpenHelper {

    public final static String TABLE_NAME = "ToDoAppRecords";
    public final static String ENTRY_ID = "_id";
    public final static String ENTRY_TITLE = "Title";
    public final static String ENTRY_TIME = "Time";
    public final static String ENTRY_DATE = "Date";
    public final static String ENTRY_CATEGORY = "Category";
    public final static String ENTRY_DESCRIPTION = "Description";

    public static ToDoAppOpenHelper toDoAppOpenHelper;

    public static ToDoAppOpenHelper getToDoAppOpenHelper(Context context){

        if(toDoAppOpenHelper == null)
        {
            toDoAppOpenHelper = new ToDoAppOpenHelper(context);
        }
        return toDoAppOpenHelper;
    }

    private ToDoAppOpenHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String Query ="create table " + TABLE_NAME +" ( " + ENTRY_ID +
                " integer primary key autoincrement, " + ENTRY_TITLE +" text, "
                + ENTRY_DATE + " text, " + ENTRY_CATEGORY + " text, " + ENTRY_TIME + " text, "
                + ENTRY_DESCRIPTION + " text);";
        db.execSQL(Query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
