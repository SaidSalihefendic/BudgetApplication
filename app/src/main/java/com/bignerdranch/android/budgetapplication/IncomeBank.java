package com.bignerdranch.android.budgetapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bignerdranch.android.budgetapplication.database.BudgetApplicationBaseHelper;
import com.bignerdranch.android.budgetapplication.database.BudgetApplicationDbSchema;
import com.bignerdranch.android.budgetapplication.database.BudgetApplicationDbSchema.IncomeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class IncomeBank {
    private static IncomeBank sIncomeBank;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static IncomeBank get(Context context){
        if (sIncomeBank == null) {
            sIncomeBank = new IncomeBank(context);
        }

        return sIncomeBank;
    }

    private IncomeBank(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new BudgetApplicationBaseHelper(mContext)
                .getWritableDatabase();
    }

    public void addIncome(Income income){
        ContentValues values = getContentValues(income);

        mDatabase.insert(IncomeTable.NAME, null, values);
    }


    public List<Income> getIncomes(){
        List<Income> incomes = new ArrayList<>();

        IncomeCursorWrapper cursor = queryIncomes(null, null);

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                incomes.add(cursor.getIncome());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return incomes;
    }

    public List<Income> getIncomes(Date beginDate, Date endDate){
        String beginDateClause = String.format("%d", beginDate.getTime());
        String endDateClause = String.format("%d", endDate.getTime());

        List<Income> incomes = new ArrayList<>();

        String sql = "select * from " + IncomeTable.NAME + " where " + IncomeTable.Cols.DATE + " between " + beginDateClause + " and " + endDateClause;
        Log.d("BudgetApplication: ",sql);
        IncomeCursorWrapper cursor = new IncomeCursorWrapper(mDatabase
                .rawQuery(sql, null));

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                incomes.add(cursor.getIncome());
                cursor.moveToNext();
            }
            Log.d("BudgetApplication: ", "Fetched!");
        } finally {
            cursor.close();
        }

        return incomes;
    }

    public Income getIncome(UUID id){
        IncomeCursorWrapper cursor = queryIncomes(
                IncomeTable.Cols.UUID + " = ? ",
                new String[] { id.toString() });

        try {
            if(cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getIncome();
        } finally {
            cursor.close();
        }
    }

    public void updateIncome(Income income) {
        String uuidString = income.getId().toString();
        ContentValues values = getContentValues(income);

        mDatabase.update(IncomeTable.NAME, values,
                IncomeTable.Cols.UUID + " = ?",
                new String[] { uuidString});
    }

    private IncomeCursorWrapper queryIncomes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                IncomeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new IncomeCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Income income) {
        ContentValues values = new ContentValues();
        values.put(IncomeTable.Cols.UUID, income.getId().toString());
        values.put(IncomeTable.Cols.TITLE, income.getTitle());
        values.put(IncomeTable.Cols.DATE, income.getDate().getTime());
        values.put(IncomeTable.Cols.VALUE, income.getValue());

        return values;
    }
}
