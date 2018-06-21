package com.bignerdranch.android.budgetapplication;

import java.util.Date;
import java.util.UUID;

public class Income {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private int mValue;

    public Income() {
        this(UUID.randomUUID());
    }

    public Income(UUID id) {
        mId = id;
        mDate = new Date();
        mValue = 0;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        mValue = value;
    }
}
