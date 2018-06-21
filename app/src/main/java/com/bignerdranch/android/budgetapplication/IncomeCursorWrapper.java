package com.bignerdranch.android.budgetapplication;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.budgetapplication.database.BudgetApplicationDbSchema;
import com.bignerdranch.android.budgetapplication.database.BudgetApplicationDbSchema.IncomeTable;

import java.util.Date;
import java.util.UUID;


public class IncomeCursorWrapper extends CursorWrapper {
    public IncomeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Income getIncome() {
        String uuidString = getString(getColumnIndex(IncomeTable.Cols.UUID));
        String title = getString(getColumnIndex(IncomeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(IncomeTable.Cols.DATE));
        int value = getInt(getColumnIndex(IncomeTable.Cols.VALUE));

        Income income = new Income(UUID.fromString(uuidString));
        income.setTitle(title);
        income.setDate(new Date(date));
        income.setValue(value);

        return income;
    }
}