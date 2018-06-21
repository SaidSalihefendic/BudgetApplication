package com.bignerdranch.android.budgetapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

public class IncomePagerActivity extends AppCompatActivity
        implements IncomeFragment.Callbacks {
    private static final String EXTRA_INCOME_ID =
            "com.bignerdranch.android.budgetapplication.income_id";

    private ViewPager mViewPager;
    private List<Income> mIncomes;

    public static Intent newIntent(Context packageContext, UUID incomeId) {
        Intent intent = new Intent(packageContext, IncomePagerActivity.class);
        intent.putExtra(EXTRA_INCOME_ID, incomeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_pager);

        UUID incomeId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_INCOME_ID);

        mViewPager = (ViewPager) findViewById(R.id.income_view_pager);

        mIncomes = IncomeBank.get(this).getIncomes();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Income income = mIncomes.get(position);
                return IncomeFragment.newInstance(income.getId());
            }

            @Override
            public int getCount() {
                return mIncomes.size();
            }
        });

        for(int i = 0; i < mIncomes.size(); i++){
            if(mIncomes.get(i).getId().equals(incomeId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    public void onIncomeUpdated(Income income) {

    }
}
