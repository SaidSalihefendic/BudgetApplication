package com.bignerdranch.android.budgetapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

public class IncomeFragment extends Fragment {
    private static final String ARG_INCOME_ID = "income_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;

    private Income mIncome;
    private EditText mTitleField;
    private Button mDateButton;
    private EditText mValueField;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onIncomeUpdated(Income income);
    }

    public static IncomeFragment newInstance(UUID incomeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_INCOME_ID, incomeId);

        IncomeFragment fragment = new IncomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID incomeId = (UUID) getArguments().getSerializable(ARG_INCOME_ID);
        mIncome = IncomeBank.get(getActivity()).getIncome(incomeId);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_income, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Income");

        mTitleField = (EditText) v.findViewById(R.id.income_title);
        mTitleField.setText(mIncome.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                mIncome.setTitle(s.toString());
                updateIncome();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //
            }
        });

        mValueField = (EditText) v.findViewById(R.id.income_value);
        mValueField.setText(Integer.toString(mIncome.getValue()));
        mValueField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                try {
                    int value = Integer.parseInt(s.toString());
                    mIncome.setValue(value);
                    updateIncome();
                } catch(Exception e) {
                    Toast.makeText(getActivity(), "Can't leave this field blank!", Toast.LENGTH_LONG).show();
                    mIncome.setValue(0);
                    updateIncome();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //
            }
        });

        mDateButton = v.findViewById(R.id.income_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mIncome.getDate());
                dialog.setTargetFragment(IncomeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_DATE){
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mIncome.setDate(date);
            updateIncome();
            updateDate();
        }
    }

    private void updateIncome() {
        IncomeBank.get(getActivity()).updateIncome(mIncome);
        mCallbacks.onIncomeUpdated(mIncome);
    }

    private void updateDate() {
        mDateButton.setText(mIncome.getDate().toString());
    }
}