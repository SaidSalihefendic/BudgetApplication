package com.bignerdranch.android.budgetapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class IncomeListFragment extends Fragment {
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mIncomeRecyclerView;
    private IncomeAdapter mAdapter;
    private boolean mSubtitleVisible;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onIncomeSelected(Income income);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income_list, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Income List");
        mIncomeRecyclerView = (RecyclerView) view.findViewById(R.id.income_recycler_view);
        mIncomeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_income_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle_income);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.new_income:
                Income income = new Income();
                IncomeBank.get(getActivity()).addIncome(income);
                updateUI();
                mCallbacks.onIncomeSelected(income);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        IncomeBank incomeBank = IncomeBank.get(getActivity());
        int incomeCount = incomeBank.getIncomes().size();
        String subtitle = getString(R.string.subtitle_format, incomeCount);

        if(!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    public void updateUI(){
        IncomeBank incomeBank = IncomeBank.get(getActivity());
        List<Income> incomes = incomeBank.getIncomes();

        if(mAdapter == null) {
            mAdapter = new IncomeAdapter(incomes);
            mIncomeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setItems(incomes);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    private class IncomeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Income mIncome;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mValue;

        public IncomeHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_income, parent, false));

            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_income_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_income_date);
            mValue = (TextView) itemView.findViewById(R.id.list_income_value);
        }

        public void bind(Income income){
            mIncome = income;
            mTitleTextView.setText(income.getTitle());
            mDateTextView.setText(income.getDate().toString());
            mValue.setText(String.format("%d KM", income.getValue()));
        }

        @Override
        public void onClick(View v) {
            mCallbacks.onIncomeSelected(mIncome);
        }
    }

    private class IncomeAdapter extends RecyclerView.Adapter<IncomeHolder>{
        private List<Income> mIncomes;

        public IncomeAdapter(List<Income> incomes){
            mIncomes = incomes;
        }

        @Override
        public IncomeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new IncomeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(IncomeHolder holder, int position) {
            Income income = mIncomes.get(position);
            holder.bind(income);
        }

        @Override
        public int getItemCount() {
            return mIncomes.size();
        }

        public void setItems(List<Income> incomes) {
            mIncomes = incomes;
        }
    }
}
