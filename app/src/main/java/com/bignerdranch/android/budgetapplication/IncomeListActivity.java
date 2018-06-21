package com.bignerdranch.android.budgetapplication;

import android.content.Intent;
import android.support.v4.app.Fragment;

public class IncomeListActivity extends SingleFragmentActivity
        implements IncomeListFragment.Callbacks, IncomeFragment.Callbacks{
    @Override
    protected Fragment createFragment(){
        return new IncomeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onIncomeSelected(Income income) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = IncomePagerActivity.newIntent(this, income.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = IncomeFragment.newInstance(income.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onIncomeUpdated(Income income) {
        IncomeListFragment listFragment = (IncomeListFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
