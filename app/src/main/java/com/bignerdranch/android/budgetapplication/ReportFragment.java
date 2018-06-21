package com.bignerdranch.android.budgetapplication;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ReportFragment extends DialogFragment {
    private TextView mPeriod;
    private TextView mBudgetCost;
    private TextView mIncome;
    private TextView mResult;
    private GraphView mGraphView;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_report, null);
        Date beginDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(beginDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int budgetCost = getBudgetCost();
        int income = getIncome();

        String period = String.format("%d.%d.%d - %d.%d.%d", 1, month, year, day, month, year);

        mPeriod = (TextView) v.findViewById(R.id.period);
        mPeriod.setText(period);

        mBudgetCost = (TextView) v.findViewById(R.id.budget_cost);
        mBudgetCost.setText(Integer.toString(budgetCost) + " KM");

        mIncome = (TextView) v.findViewById(R.id.income_total);
        mIncome.setText(Integer.toString(income) + " KM");

        mResult = (TextView) v.findViewById(R.id.report_status);
        mResult.setText(Integer.toString(income - budgetCost) + " KM");

        mGraphView = v.findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(getDataPoints());
        mGraphView.addSeries(series);

        mGraphView.getViewport().setMinX(1);
        mGraphView.getViewport().setMaxX(31);
        mGraphView.getViewport().setXAxisBoundsManual(true);


        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.report_title)
                .create();
    }

    private int getBudgetCost() {
        Date endDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        int beginYear = cal.get(Calendar.YEAR);
        int beginMonth = cal.get(Calendar.MONTH);

        Date beginDate = new GregorianCalendar(beginYear, beginMonth, 1).getTime();

        List<Item> items = Budget.get(getActivity()).getItems(beginDate, endDate);

        int budgetCost = 0;

        for(int i = 0; i < items.size(); i++) {
            budgetCost += items.get(i).getValue();
        }

        return budgetCost;
    }

    private int getIncome() {
        Date endDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        int beginYear = cal.get(Calendar.YEAR);
        int beginMonth = cal.get(Calendar.MONTH);

        Date beginDate = new GregorianCalendar(beginYear, beginMonth, 1).getTime();

        List<Income> incomes = IncomeBank.get(getActivity()).getIncomes(beginDate, endDate);

        int income = 0;

        for(int i = 0; i < incomes.size(); i++) {
            income += incomes.get(i).getValue();
        }

        return income;
    }

    private DataPoint[] getDataPoints() {
        List<DataPoint> points = new ArrayList<>();
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        List<Income> incomes = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        IncomeBank incomeBank = IncomeBank.get(getActivity());
        Budget budget = Budget.get(getActivity());
        Date beginDate;
        Date endDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

        int income = 0;
        int cost;

        for(int i = 1; i <= day; i++) {
            cost = 0;

            try {
                beginDate = dateFormat.parse(String.format("%d/%d/%d %d:%d:%d", i, month + 1, year, 0, 0, 0));
                endDate = dateFormat.parse(String.format("%d/%d/%d %d:%d:%d", i, month + 1, year, 23, 59, 59));
                incomes = incomeBank.getIncomes(beginDate, endDate);
                items = budget.getItems(beginDate, endDate);

                Log.d("ReportFragment: ", Integer.toString(incomes.size()));
                Log.d("ReportFragment: ", Integer.toString(items.size()));
                for (int j = 0; j < incomes.size(); j++) {
                    income += incomes.get(j).getValue();
                }

                for (int j = 0; j < items.size(); j++) {
                    cost += items.get(j).getValue();
                }
            } catch(ParseException e) {
                Log.d("ParseException: ", e.toString());
            }

            income -= cost;
            DataPoint point = new DataPoint(i, income);
            points.add(point);
        }

        DataPoint[] pointsArray = new DataPoint[points.size()];
        points.toArray(pointsArray);
        return pointsArray;
    }
}
