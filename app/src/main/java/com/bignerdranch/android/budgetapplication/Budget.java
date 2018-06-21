package com.bignerdranch.android.budgetapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bignerdranch.android.budgetapplication.database.BudgetApplicationBaseHelper;
import com.bignerdranch.android.budgetapplication.database.BudgetApplicationDbSchema;
import com.bignerdranch.android.budgetapplication.database.BudgetApplicationDbSchema.ItemTable;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class Budget {
    private static Budget sBudget;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static Budget get(Context context){
        if (sBudget == null) {
            sBudget = new Budget(context);
        }

        return sBudget;
    }

    private Budget(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new BudgetApplicationBaseHelper(mContext)
                .getWritableDatabase();
    }

    public void addItem(Item item){
        ContentValues values = getContentValues(item);

        mDatabase.insert(ItemTable.NAME, null, values);
    }


    public List<Item> getItems(){
        List<Item> items = new ArrayList<>();

        ItemCursorWrapper cursor = queryItems(null, null);

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                items.add(cursor.getItem());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return items;
    }

    public List<Item> getItems(Date beginDate, Date endDate){
        String beginDateClause = String.format("%d", beginDate.getTime());
        String endDateClause = String.format("%d", endDate.getTime());

        List<Item> items = new ArrayList<>();

        String sql = "select * from " + ItemTable.NAME + " where " + ItemTable.Cols.DATE + " between " + beginDateClause + " and " + endDateClause;
        Log.d("BudgetApplication: ",sql);
        ItemCursorWrapper cursor = new ItemCursorWrapper(mDatabase
                .rawQuery(sql, null));

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                items.add(cursor.getItem());
                cursor.moveToNext();
            }
            Log.d("BudgetApplication: ", "Fetched!");
        } finally {
            cursor.close();
        }

        return items;
    }

    public Item getItem(UUID id){
        ItemCursorWrapper cursor = queryItems(
                ItemTable.Cols.UUID + " = ? ",
                new String[] { id.toString() });

        try {
            if(cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getItem();
        } finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Item item) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, item.getPhotoFilename());
    }

    public void updateItem(Item item) {
        String uuidString = item.getId().toString();
        ContentValues values = getContentValues(item);

        mDatabase.update(ItemTable.NAME, values,
                ItemTable.Cols.UUID + " = ?",
                new String[] { uuidString});
    }

    private ItemCursorWrapper queryItems(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ItemTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new ItemCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Item item) {
        ContentValues values = new ContentValues();
        values.put(ItemTable.Cols.UUID, item.getId().toString());
        values.put(ItemTable.Cols.TITLE, item.getTitle());
        values.put(ItemTable.Cols.DATE, item.getDate().getTime());
        values.put(ItemTable.Cols.VALUE, item.getValue());
        values.put(ItemTable.Cols.QUANTITY, item.getQuantity());

        return values;
    }
}
