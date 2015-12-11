package com.rbsoftware.pfm.personalfinancemanager;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


public class AccountSummary extends Fragment {
    private Spinner accountSummarySpinner;
    private TextView salary;
    private TextView rentalIncome;
    private TextView interest;
    private TextView gifts;
    private TextView otherIncome;
    private TextView taxes;
    private TextView mortgage;
    private TextView creditCard;
    private TextView utilities;
    private TextView food;
    private TextView carPayment;
    private TextView personal;
    private TextView activities;
    private TextView otherExpense;
    public AccountSummary() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_summary, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getResources().getStringArray(R.array.drawer_menu)[0]);

        accountSummarySpinner = (Spinner) getActivity().findViewById(R.id.account_summary_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.account_summary_spinner,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountSummarySpinner.setAdapter(adapter);

        salary = (TextView) getActivity().findViewById(R.id.tv_income_salary);
        rentalIncome = (TextView) getActivity().findViewById(R.id.tv_income_rental);
        interest = (TextView) getActivity().findViewById(R.id.tv_income_interest);
        gifts = (TextView) getActivity().findViewById(R.id.tv_income_gifts);
        otherIncome = (TextView) getActivity().findViewById(R.id.tv_income_other);
        taxes = (TextView) getActivity().findViewById(R.id.tv_expense_taxes);
        mortgage = (TextView) getActivity().findViewById(R.id.tv_expense_mortgage);
        creditCard = (TextView) getActivity().findViewById(R.id.tv_expense_credit_card);
        utilities = (TextView) getActivity().findViewById(R.id.tv_expense_utilities);
        food = (TextView) getActivity().findViewById(R.id.tv_expense_food);
        carPayment = (TextView) getActivity().findViewById(R.id.tv_expense_car_payment);
        personal = (TextView) getActivity().findViewById(R.id.tv_expense_personal);
        activities = (TextView) getActivity().findViewById(R.id.tv_expense_activities);
        otherExpense = (TextView) getActivity().findViewById(R.id.tv_expense_other);

    }

}
