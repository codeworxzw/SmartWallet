package com.rbsoftware.pfm.personalfinancemanager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseFragment extends Fragment {
    private EditText food;
    private EditText carPayment;
    private EditText personal;
    private EditText activities;
    private EditText utilities;
    private EditText creditCard;
    private EditText taxes;
    private EditText mortgage;
    private EditText otherExpense;

    public ExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_expense, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        food = (EditText) getActivity().findViewById(R.id.et_expense_food);
        carPayment = (EditText) getActivity().findViewById(R.id.et_expense_car_payment);
        personal = (EditText) getActivity().findViewById(R.id.et_expense_personal);
        activities = (EditText) getActivity().findViewById(R.id.et_expense_activities);
        utilities = (EditText) getActivity().findViewById(R.id.et_expense_utilities);
        creditCard = (EditText) getActivity().findViewById(R.id.et_expense_credit_card);
        taxes = (EditText) getActivity().findViewById(R.id.et_expense_taxes);
        mortgage = (EditText) getActivity().findViewById(R.id.et_expense_mortgage);
        otherExpense = (EditText) getActivity().findViewById(R.id.et_expense_other);
    }
}
