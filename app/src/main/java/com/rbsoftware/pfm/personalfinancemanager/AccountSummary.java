package com.rbsoftware.pfm.personalfinancemanager;

import android.content.Context;
import android.net.Uri;
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
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
    //private TextView totalIncome;
    //private TextView totalExpense;

   // TextView totalIncome = new TextView(String);

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

       //--------------------------------------------
       //    for( FinanceDocument item : docList){
       //    sum=Integer.parseInt(item.getSalary());
       //    Log.d("data", "sum = " + sum);
       //    }
       //--------------------------------------------


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

        //totalIncome = (TextView) getActivity().findViewById(R.id.tv_income);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.filter, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_filter){
            showPopup();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
    //Helper methods
    //Shows filter popup menu
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
                        getValue(financeDocumentList);


                        break;
                    case R.id.thisMonth:
                        financeDocumentList= MainActivity.financeDocumentModel.queryDocumentsByDate("thisMonth", MainActivity.getUserId());
                        getValue(financeDocumentList);


                        break;
                    case R.id.lastWeek:
                        Log.d("popup menu", "Last week");
                        financeDocumentList= MainActivity.financeDocumentModel.queryDocumentsByDate("lastWeek", MainActivity.getUserId());
                        getValue(financeDocumentList);


                        break;
                    case R.id.lastMonth:
                        financeDocumentList= MainActivity.financeDocumentModel.queryDocumentsByDate("lastMonth", MainActivity.getUserId());
                        getValue(financeDocumentList);


                        break;
                    case R.id.thisYear:
                        financeDocumentList= MainActivity.financeDocumentModel.queryDocumentsByDate("thisYear", MainActivity.getUserId());
                        getValue(financeDocumentList);


                        break;
                    default: financeDocumentList= MainActivity.financeDocumentModel.queryDocumentsByDate("thisWeek", MainActivity.getUserId());
                        getValue(financeDocumentList);

                        break;
                }
                Log.d("popup menu", financeDocumentList.toString());
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
        //int totalIncome = 0;
        //int totalExpense = 0;

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

        //totalIncome = salarySum + rentalIncomeSum +interestSum + giftsSum + otherIncomeSum;
        //totalExpense = taxesSum + mortgageSum + creditCardSum + utilitiesSum + foodSum + carPaymentSum + personalSum + activitiesSum + otherIncomeSum;

        salary.setText(salary.getText() +"  " + Integer.toString(salarySum));
        rentalIncome.setText(rentalIncome.getText() +"  " + Integer.toString(rentalIncomeSum));
        interest.setText(interest.getText() +"  " + Integer.toString(interestSum));
        gifts.setText(gifts.getText() +"  " + Integer.toString(giftsSum));
        otherIncome.setText(otherIncome.getText() +"  " + Integer.toString(otherIncomeSum));
        taxes.setText(taxes.getText() +"  " + Integer.toString(taxesSum));
        mortgage.setText(mortgage.getText() +"  " + Integer.toString(mortgageSum));
        creditCard.setText(creditCard.getText() +"  " + Integer.toString(creditCardSum));
        utilities.setText(utilities.getText() +"  " + Integer.toString(utilitiesSum));
        food.setText(food.getText() +"  " + Integer.toString(foodSum));
        carPayment.setText(carPayment.getText() +"  " + Integer.toString(carPaymentSum));
        personal.setText(personal.getText() +"  " + Integer.toString(personalSum));
        activities.setText(activities.getText() +"  " + Integer.toString(activitiesSum));
        otherExpense.setText(otherExpense.getText() +"  " + Integer.toString(otherExpensesSum));
        //totalIncome.setText(totalIncome.getText() + Integer.toString(totalIncome));
    }

}