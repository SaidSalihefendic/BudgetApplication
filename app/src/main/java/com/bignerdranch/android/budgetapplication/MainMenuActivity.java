package com.bignerdranch.android.budgetapplication;

import android.support.v4.app.Fragment;


public class MainMenuActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new MainMenuFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }
}
