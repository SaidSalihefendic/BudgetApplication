package com.bignerdranch.android.budgetapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainMenuFragment extends Fragment {
    private static final String DIALOG_REPORT = "DialogReport";
    private Button mBudgetButton;
    private Button mIncomesButton;
    private Button mReportButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_menu, container, false);

        mBudgetButton = (Button) v.findViewById(R.id.button_budget);
        mBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getContext(), ItemListActivity.class);
                startActivity(intent);
            }
        });

        mIncomesButton = (Button) v.findViewById(R.id.button_income);
        mIncomesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getContext(), IncomeListActivity.class);
                startActivity(intent);
            }
        });

        mReportButton = (Button) v.findViewById(R.id.button_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                FragmentManager manager = getFragmentManager();
                ReportFragment dialog = new ReportFragment();
                dialog.show(manager, DIALOG_REPORT);
            }
        });

        return v;
    }
}
