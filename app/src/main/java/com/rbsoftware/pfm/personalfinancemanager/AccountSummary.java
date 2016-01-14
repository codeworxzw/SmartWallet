package com.rbsoftware.pfm.personalfinancemanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AccountSummary extends Fragment {
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
    private TextView income;
    private TextView expense;
    private String selectedItem;
    private TextView mTextViewPeriod;

    private List<FinanceDocument> financeDocumentList;
    public AccountSummary() {
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
        return inflater.inflate(R.layout.fragment_account_summary, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getResources().getStringArray(R.array.drawer_menu)[0]);

        mTextViewPeriod = (TextView) getActivity().findViewById(R.id.tv_period);
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
        income = (TextView) getActivity().findViewById(R.id.tv_income);
        expense = (TextView) getActivity().findViewById(R.id.tv_expense);

    }

    @Override
    public void onResume() {
        super.onResume();
        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate(MainActivity.ReadFromSharedPreferences(getActivity(), "periodAccSummary", "thisWeek"), MainActivity.getUserId());
        mTextViewPeriod.setText(MainActivity.ReadFromSharedPreferences(getActivity(), "periodTextAccSummary", getResources().getString(R.string.this_week)));
        getValue(financeDocumentList);
    }

    //Create options menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.account_summary_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_filter:
                showPopup();
                return true;

            case R.id.document_share:
                try {
                    ExportData.exportSummaryAsCsv(getContext(), prepareCsvData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            default:
                return super.onOptionsItemSelected(item);
        }



    }
    //Helper methods
    //Shows account_summary_menu popup menu
    public void showPopup(){
        View menuItemView = getActivity().findViewById(R.id.action_filter);
        PopupMenu popup = new PopupMenu(getActivity(), menuItemView);
        MenuInflater inflate = popup.getMenuInflater();
        inflate.inflate(R.menu.period, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                Log.d("popup menu", item.getTitle().toString());


                switch (id){
                    case R.id.thisWeek:
                        financeDocumentList= MainActivity.financeDocumentModel.queryDocumentsByDate("thisWeek", MainActivity.getUserId());
                        selectedItem = "thisWeek";
                        mTextViewPeriod.setText(getResources().getString(R.string.this_week));

                        break;
                    case R.id.thisMonth:
                        financeDocumentList= MainActivity.financeDocumentModel.queryDocumentsByDate("thisMonth", MainActivity.getUserId());
                        selectedItem = "thisMonth";
                        mTextViewPeriod.setText(getResources().getString(R.string.this_month));

                        break;
                    case R.id.lastWeek:
                        financeDocumentList= MainActivity.financeDocumentModel.queryDocumentsByDate("lastWeek", MainActivity.getUserId());
                        selectedItem = "lastWeek";
                        mTextViewPeriod.setText(getResources().getString(R.string.last_week));

                        break;
                    case R.id.lastMonth:
                        financeDocumentList= MainActivity.financeDocumentModel.queryDocumentsByDate("lastMonth", MainActivity.getUserId());
                        selectedItem = "lastMonth";
                        mTextViewPeriod.setText(getResources().getString(R.string.last_month));

                        break;
                    case R.id.thisYear:
                        financeDocumentList= MainActivity.financeDocumentModel.queryDocumentsByDate("thisYear", MainActivity.getUserId());
                        selectedItem = "thisYear";
                        mTextViewPeriod.setText(getResources().getString(R.string.this_year));

                        break;
                }
                MainActivity.SaveToSharedPreferences(getActivity(), "periodAccSummary", selectedItem);
                MainActivity.SaveToSharedPreferences(getActivity(), "periodTextAccSummary", mTextViewPeriod.getText().toString());
                getValue(financeDocumentList);
                return false;
            }
        });
        popup.show();

    }

    public void getValue(List<FinanceDocument> list) {
        int salarySum = 0;
        int rentalIncomeSum = 0;
        int interestSum = 0;
        int giftsSum = 0;
        int otherIncomeSum = 0;
        int taxesSum = 0;
        int mortgageSum = 0;
        int creditCardSum = 0;
        int utilitiesSum = 0;
        int foodSum = 0;
        int carPaymentSum = 0;
        int personalSum = 0;
        int activitiesSum = 0;
        int otherExpensesSum = 0;
        int totalIncome = 0;
        int totalExpense = 0;

        for (FinanceDocument item : list) {
            salarySum += Integer.parseInt(item.getSalary());
            rentalIncomeSum += Integer.parseInt(item.getRentalIncome());
            interestSum += Integer.parseInt(item.getInterest());
            giftsSum += Integer.parseInt(item.getGifts());
            otherIncomeSum += Integer.parseInt(item.getOtherIncome());
            taxesSum += Integer.parseInt(item.getTaxes());
            mortgageSum += Integer.parseInt(item.getMortgage());
            creditCardSum += Integer.parseInt(item.getCreditCard());
            utilitiesSum += Integer.parseInt(item.getUtilities());
            foodSum += Integer.parseInt(item.getFood());
            carPaymentSum += Integer.parseInt(item.getCarPayment());
            personalSum += Integer.parseInt(item.getPersonal());
            activitiesSum += Integer.parseInt(item.getActivities());
            otherExpensesSum += Integer.parseInt(item.getOtherExpenses());
        }

        totalIncome = salarySum + rentalIncomeSum +interestSum + giftsSum + otherIncomeSum;
        totalExpense = taxesSum + mortgageSum + creditCardSum + utilitiesSum + foodSum + carPaymentSum + personalSum + activitiesSum + otherExpensesSum;

        salary.setText(Integer.toString(salarySum));
        rentalIncome.setText(Integer.toString(rentalIncomeSum));
        interest.setText(Integer.toString(interestSum));
        gifts.setText(Integer.toString(giftsSum));
        otherIncome.setText(Integer.toString(otherIncomeSum));
        taxes.setText(Integer.toString(taxesSum));
        mortgage.setText(Integer.toString(mortgageSum));
        creditCard.setText(Integer.toString(creditCardSum));
        utilities.setText(Integer.toString(utilitiesSum));
        food.setText(Integer.toString(foodSum));
        carPayment.setText(Integer.toString(carPaymentSum));
        personal.setText(Integer.toString(personalSum));
        activities.setText(Integer.toString(activitiesSum));
        otherExpense.setText(Integer.toString(otherExpensesSum));
        String incomeString = Integer.toString(totalIncome) +" "+MainActivity.defaultCurrency;
        income.setText(incomeString);
        String expenseString = Integer.toString(totalExpense) +" "+MainActivity.defaultCurrency;
        expense.setText(expenseString);
    }

    // compiles all views data into export ready list
    private List<String[]> prepareCsvData(){
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{getString(R.string.period), mTextViewPeriod.getText().toString()});
        data.add(new String[]{"",""});
        data.add(new String[]{getString(R.string.income), income.getText().toString()});
        data.add(new String[]{"",""});
        data.add(new String[]{getString(R.string.salary), salary.getText().toString()});
        data.add(new String[]{getString(R.string.rental_income), rentalIncome.getText().toString()});
        data.add(new String[]{getString(R.string.interest), interest.getText().toString()});
        data.add(new String[]{getString(R.string.gifts), gifts.getText().toString()});
        data.add(new String[]{getString(R.string.other_income), otherIncome.getText().toString()});
        data.add(new String[]{"",""});
        data.add(new String[]{getString(R.string.expense), expense.getText().toString()});
        data.add(new String[]{"",""});
        data.add(new String[]{getString(R.string.food), food.getText().toString()});
        data.add(new String[]{getString(R.string.car_payment), carPayment.getText().toString()});
        data.add(new String[]{getString(R.string.personal), personal.getText().toString()});
        data.add(new String[]{getString(R.string.activities), activities.getText().toString()});
        data.add(new String[]{getString(R.string.utilities), utilities.getText().toString()});
        data.add(new String[]{getString(R.string.credit_card), creditCard.getText().toString()});
        data.add(new String[]{getString(R.string.taxes), taxes.getText().toString()});
        data.add(new String[]{getString(R.string.mortgage), mortgage.getText().toString()});
        data.add(new String[]{getString(R.string.other_expense), otherExpense.getText().toString()});

        return data;
    }

}