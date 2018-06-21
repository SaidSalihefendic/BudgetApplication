package com.bignerdranch.android.budgetapplication;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.budgetapplication.database.BudgetApplicationDbSchema;
import com.bignerdranch.android.budgetapplication.database.BudgetApplicationDbSchema.ItemTable;

import java.util.Date;
import java.util.UUID;

public class ItemCursorWrapper extends CursorWrapper {
    public ItemCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Item getItem() {
        String uuidString = getString(getColumnIndex(ItemTable.Cols.UUID));
        String title = getString(getColumnIndex(ItemTable.Cols.TITLE));
        long date = getLong(getColumnIndex(ItemTable.Cols.DATE));
        int value = getInt(getColumnIndex(ItemTable.Cols.VALUE));
        int quantity = getInt(getColumnIndex(ItemTable.Cols.QUANTITY));

        Item item = new Item(UUID.fromString(uuidString));
        item.setTitle(title);
        item.setDate(new Date(date));
        item.setValue(value);
        item.setQuantity(quantity);

        return item;
    }
}