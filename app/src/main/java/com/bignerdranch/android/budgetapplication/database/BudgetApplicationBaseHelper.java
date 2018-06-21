package com.bignerdranch.android.budgetapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bignerdranch.android.budgetapplication.database.BudgetApplicationDbSchema.IncomeTable;
import com.bignerdranch.android.budgetapplication.database.BudgetApplicationDbSchema.ItemTable;


public class BudgetApplicationBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "budget_application.db";

    public BudgetApplicationBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ItemTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                ItemTable.Cols.UUID + ", " +
                ItemTable.Cols.TITLE + " VARCHAR(255), " +
                ItemTable.Cols.DATE + ", " +
                ItemTable.Cols.VALUE + ", " +
                ItemTable.Cols.QUANTITY + ")"
        );

        db.execSQL("create table " + IncomeTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                IncomeTable.Cols.UUID + ", " +
                IncomeTable.Cols.TITLE + " VARCHAR(255), " +
                IncomeTable.Cols.DATE + ", " +
                IncomeTable.Cols.VALUE + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
