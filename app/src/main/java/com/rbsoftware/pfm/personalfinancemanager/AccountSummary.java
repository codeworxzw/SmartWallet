package com.rbsoftware.pfm.personalfinancemanager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
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

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * A simple {@link Fragment} subclass.
 * Account summary fragment holds account data
 **/
public class AccountSummary extends Fragment {

    private final String TAG = "AccountSummary";
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
    private Context mContext;
    private Activity mActivity;
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

        mContext = getContext();
        mActivity = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate(MainActivity.readFromSharedPreferences(getActivity(), "periodAccSummary", "thisWeek"), MainActivity.getUserId());
        mTextViewPeriod.setText(MainActivity.readFromSharedPreferences(getActivity(), "periodTextAccSummary", getResources().getString(R.string.this_week)));
        getValue(financeDocumentList);
    }

    //Create options menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.account_summary_menu, menu);
        int status = mContext.getSharedPreferences("material_showcaseview_prefs", Context.MODE_PRIVATE)
                .getInt("status_" + TAG, 0);
        if (status != -1) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startShowcase();
                }
            }, 1000);
        }

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
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestWriteExternalStoragePermission();
                } else {
                    try {
                        ExportData.exportSummaryAsCsv(getContext(), prepareCsvData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            default:
                return super.onOptionsItemSelected(item);
        }


    }
    //Helper methods

    /**
     * Shows account_summary_menu popup menu
     **/
    private void showPopup() {
        View menuItemView = getActivity().findViewById(R.id.action_filter);
        PopupMenu popup = new PopupMenu(getActivity(), menuItemView);
        MenuInflater inflate = popup.getMenuInflater();
        inflate.inflate(R.menu.period, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();


                switch (id) {
                    case R.id.thisWeek:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate("thisWeek", MainActivity.getUserId());
                        selectedItem = "thisWeek";
                        mTextViewPeriod.setText(getResources().getString(R.string.this_week));

                        break;
                    case R.id.thisMonth:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate("thisMonth", MainActivity.getUserId());
                        selectedItem = "thisMonth";
                        mTextViewPeriod.setText(getResources().getString(R.string.this_month));

                        break;
                    case R.id.lastWeek:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate("lastWeek", MainActivity.getUserId());
                        selectedItem = "lastWeek";
                        mTextViewPeriod.setText(getResources().getString(R.string.last_week));

                        break;
                    case R.id.lastMonth:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate("lastMonth", MainActivity.getUserId());
                        selectedItem = "lastMonth";
                        mTextViewPeriod.setText(getResources().getString(R.string.last_month));

                        break;
                    case R.id.thisYear:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate("thisYear", MainActivity.getUserId());
                        selectedItem = "thisYear";
                        mTextViewPeriod.setText(getResources().getString(R.string.this_year));

                        break;
                }
                MainActivity.saveToSharedPreferences(getActivity(), "periodAccSummary", selectedItem);
                MainActivity.saveToSharedPreferences(getActivity(), "periodTextAccSummary", mTextViewPeriod.getText().toString());
                getValue(financeDocumentList);
                return false;
            }
        });
        popup.show();

    }

    /**
     * Retrieves values from documents list.
     * Calculates sums and sets them to text views
     *
     * @param list
     **/

    private void getValue(List<FinanceDocument> list) {
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
            salarySum += item.getSalary();
            rentalIncomeSum += item.getRentalIncome();
            interestSum += item.getInterest();
            giftsSum += item.getGifts();
            otherIncomeSum += item.getOtherIncome();
            taxesSum += item.getTaxes();
            mortgageSum += item.getMortgage();
            creditCardSum += item.getCreditCard();
            utilitiesSum += item.getUtilities();
            foodSum += item.getFood();
            carPaymentSum += item.getCarPayment();
            personalSum += item.getPersonal();
            activitiesSum += item.getActivities();
            otherExpensesSum += item.getOtherExpenses();
        }

        totalIncome = salarySum + rentalIncomeSum + interestSum + giftsSum + otherIncomeSum;
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
        String incomeString = Integer.toString(totalIncome) + " " + MainActivity.defaultCurrency;
        income.setText(incomeString);
        String expenseString = Integer.toString(totalExpense) + " " + MainActivity.defaultCurrency;
        expense.setText(expenseString);
    }

    /**
     * Compiles all views data into export ready list
     *
     * @return data
     **/
    private List<String[]> prepareCsvData() {
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{getString(R.string.period), mTextViewPeriod.getText().toString()});
        data.add(new String[]{"", ""});
        data.add(new String[]{getString(R.string.income), income.getText().toString()});
        data.add(new String[]{"", ""});
        data.add(new String[]{getString(R.string.salary), salary.getText().toString()});
        data.add(new String[]{getString(R.string.rental_income), rentalIncome.getText().toString()});
        data.add(new String[]{getString(R.string.interest), interest.getText().toString()});
        data.add(new String[]{getString(R.string.gifts), gifts.getText().toString()});
        data.add(new String[]{getString(R.string.other_income), otherIncome.getText().toString()});
        data.add(new String[]{"", ""});
        data.add(new String[]{getString(R.string.expense), expense.getText().toString()});
        data.add(new String[]{"", ""});
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

    /**
     * Requests WRITE_EXTERNAL_STORAGE permission
     */
    private void requestWriteExternalStoragePermission() {

        // No explanation needed, we can request the permission.

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                0);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    try {
                        ExportData.exportSummaryAsCsv(getContext(), prepareCsvData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {

                    showExplanationDialog();

                }
                break;
            }


        }
    }

    /**
     * Shows explanation why WRITE_EXTERNAL_STORAGE required
     */
    private void showExplanationDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle(getString(R.string.permission_required));
        alertDialog.setMessage(getString(R.string.permission_write_external_storage_explanation));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * Runs showcase presentation on fragment start
     **/
    private void startShowcase() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view
        config.setDismissTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(mActivity, TAG);
        sequence.setConfig(config);
        sequence.addSequenceItem(mActivity.findViewById(R.id.action_filter), getString(R.string.action_filter), getString(R.string.got_it));
        sequence.addSequenceItem(mActivity.findViewById(R.id.document_share), getString(R.string.document_share), getString(R.string.ok));
        sequence.start();

    }

}