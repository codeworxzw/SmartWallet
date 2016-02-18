package com.rbsoftware.pfm.personalfinancemanager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import java.util.Locale;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * A simple {@link Fragment} subclass.
 * Account summary fragment holds account data
 **/
public class AccountSummary extends Fragment {

    private final String TAG = "AccountSummary";
    private TextView salaryTextView;
    private TextView rentalIncomeTextView;
    private TextView interestTextView;
    private TextView giftsTextView;
    private TextView otherIncomeTextView;
    private TextView taxesTextView;
    private TextView mortgageTextView;
    private TextView creditCardTextView;
    private TextView utilitiesTextView;
    private TextView foodTextView;
    private TextView carPaymentTextView;
    private TextView personalTextView;
    private TextView activitiesTextView;
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
        if (mTextViewPeriod == null) {
            mTextViewPeriod = (TextView) getActivity().findViewById(R.id.tv_period);
        }
        if (salaryTextView == null) {
            salaryTextView = (TextView) getActivity().findViewById(R.id.tv_income_salary);
        }
        if (rentalIncomeTextView == null) {
            rentalIncomeTextView = (TextView) getActivity().findViewById(R.id.tv_income_rental);
        }
        if (interestTextView == null) {
            interestTextView = (TextView) getActivity().findViewById(R.id.tv_income_interest);
        }
        if (giftsTextView == null) {
            giftsTextView = (TextView) getActivity().findViewById(R.id.tv_income_gifts);
        }
        if (otherIncomeTextView == null) {
            otherIncomeTextView = (TextView) getActivity().findViewById(R.id.tv_income_other);
        }
        if (taxesTextView == null) {
            taxesTextView = (TextView) getActivity().findViewById(R.id.tv_expense_taxes);
        }
        if (mortgageTextView == null) {
            mortgageTextView = (TextView) getActivity().findViewById(R.id.tv_expense_mortgage);
        }
        if (creditCardTextView == null) {
            creditCardTextView = (TextView) getActivity().findViewById(R.id.tv_expense_credit_card);
        }
        if (utilitiesTextView == null) {
            utilitiesTextView = (TextView) getActivity().findViewById(R.id.tv_expense_utilities);
        }
        if (foodTextView == null) {
            foodTextView = (TextView) getActivity().findViewById(R.id.tv_expense_food);
        }
        if (carPaymentTextView == null) {
            carPaymentTextView = (TextView) getActivity().findViewById(R.id.tv_expense_car_payment);
        }
        if (personalTextView == null) {
            personalTextView = (TextView) getActivity().findViewById(R.id.tv_expense_personal);
        }
        if (activitiesTextView == null) {
            activitiesTextView = (TextView) getActivity().findViewById(R.id.tv_expense_activities);
        }
        if (otherExpense == null) {
            otherExpense = (TextView) getActivity().findViewById(R.id.tv_expense_other);
        }
        if (income == null) {
            income = (TextView) getActivity().findViewById(R.id.tv_income);
        }
        if (expense == null) {
            expense = (TextView) getActivity().findViewById(R.id.tv_expense);
        }

        mContext = getContext();
        mActivity = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate(MainActivity.readFromSharedPreferences(getActivity(), "periodAccSummary", "thisWeek"), MainActivity.getUserId());
        mTextViewPeriod.setText(MainActivity.readFromSharedPreferences(getActivity(), "periodTextAccSummary", getResources().getString(R.string.this_week)));
        hideAllTextViews();
        setValuesFromList(financeDocumentList);
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
                hideAllTextViews();
                setValuesFromList(financeDocumentList);
                return false;
            }
        });
        popup.show();

    }

    /**
     * Retrieves values from documents list.
     * Calculates sums and sets them to text views
     *
     * @param list FinanceDocument list
     **/

    private void setValuesFromList(List<FinanceDocument> list) {
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
        int totalIncome;
        int totalExpense;

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

        if (salarySum != 0) {
            getActivity().findViewById(R.id.salary_layout).setVisibility(View.VISIBLE);
            salaryTextView.setText(String.format(Locale.getDefault(),"%,d",salarySum));
        }
        if (rentalIncomeSum != 0) {
            getActivity().findViewById(R.id.rental_income_layout).setVisibility(View.VISIBLE);
            rentalIncomeTextView.setText(String.format(Locale.getDefault(), "%,d", rentalIncomeSum));
        }
        if (interestSum != 0) {
            getActivity().findViewById(R.id.interest_layout).setVisibility(View.VISIBLE);
            interestTextView.setText(String.format(Locale.getDefault(), "%,d", interestSum));
        }
        if (giftsSum != 0) {
            getActivity().findViewById(R.id.gifts_layout).setVisibility(View.VISIBLE);
            giftsTextView.setText(String.format(Locale.getDefault(), "%,d", giftsSum));
        }
        if (otherIncomeSum != 0) {
            getActivity().findViewById(R.id.other_income_layout).setVisibility(View.VISIBLE);
            otherIncomeTextView.setText(String.format(Locale.getDefault(), "%,d", otherIncomeSum));
        }
        if (taxesSum != 0) {
            getActivity().findViewById(R.id.taxes_layout).setVisibility(View.VISIBLE);
            taxesTextView.setText(String.format(Locale.getDefault(), "%,d", taxesSum));
        }
        if (mortgageSum != 0) {
            getActivity().findViewById(R.id.mortgage_layout).setVisibility(View.VISIBLE);
            mortgageTextView.setText(String.format(Locale.getDefault(), "%,d", mortgageSum));
        }
        if (creditCardSum != 0) {
            getActivity().findViewById(R.id.credit_card_layout).setVisibility(View.VISIBLE);
            creditCardTextView.setText(String.format(Locale.getDefault(), "%,d", creditCardSum));
        }
        if (utilitiesSum != 0) {
            getActivity().findViewById(R.id.utilities_layout).setVisibility(View.VISIBLE);
            utilitiesTextView.setText(String.format(Locale.getDefault(), "%,d", utilitiesSum));
        }
        if (foodSum != 0) {
            getActivity().findViewById(R.id.food_layout).setVisibility(View.VISIBLE);
            foodTextView.setText(String.format(Locale.getDefault(), "%,d", foodSum));
        }
        if (carPaymentSum != 0) {
            getActivity().findViewById(R.id.car_payment_layout).setVisibility(View.VISIBLE);
            carPaymentTextView.setText(String.format(Locale.getDefault(), "%,d", carPaymentSum));
        }
        if (personalSum != 0) {
            getActivity().findViewById(R.id.personal_layout).setVisibility(View.VISIBLE);
            personalTextView.setText(String.format(Locale.getDefault(), "%,d", personalSum));
        }
        if (activitiesSum != 0) {
            getActivity().findViewById(R.id.activities_layout).setVisibility(View.VISIBLE);
            activitiesTextView.setText(String.format(Locale.getDefault(), "%,d", activitiesSum));
        }
        if (otherExpensesSum != 0) {
            getActivity().findViewById(R.id.other_expenses_layout).setVisibility(View.VISIBLE);
            otherExpense.setText(String.format(Locale.getDefault(), "%,d", otherExpensesSum));
        }
        String incomeString = String.format(Locale.getDefault(), "%,d", totalIncome) + " " + MainActivity.defaultCurrency;
        income.setText(incomeString);
        String expenseString = String.format(Locale.getDefault(), "%,d", totalExpense) + " " + MainActivity.defaultCurrency;
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
        if (!salaryTextView.getText().toString().equals("000")) {
            data.add(new String[]{getString(R.string.salary), salaryTextView.getText().toString()});
        }
        if (!rentalIncomeTextView.getText().toString().equals("000")) {
            data.add(new String[]{getString(R.string.rental_income), rentalIncomeTextView.getText().toString()});
        }
        if (!interestTextView.getText().toString().equals("000")) {
            data.add(new String[]{getString(R.string.interest), interestTextView.getText().toString()});
        }
        if (!giftsTextView.getText().toString().equals("000")) {
            data.add(new String[]{getString(R.string.gifts), giftsTextView.getText().toString()});
        }
        if (!otherIncomeTextView.getText().toString().equals("000")) {
            data.add(new String[]{getString(R.string.other_income), otherIncomeTextView.getText().toString()});
        }
        data.add(new String[]{"", ""});
        data.add(new String[]{getString(R.string.expense), expense.getText().toString()});
        data.add(new String[]{"", ""});
        if (!foodTextView.getText().toString().equals("000")) {
            data.add(new String[]{getString(R.string.food), foodTextView.getText().toString()});
        }
        if (!carPaymentTextView.getText().toString().equals("000")) {
            data.add(new String[]{getString(R.string.car_payment), carPaymentTextView.getText().toString()});
        }
        if (!personalTextView.getText().toString().equals("000")) {
            data.add(new String[]{getString(R.string.personal), personalTextView.getText().toString()});
        }
        if (!activitiesTextView.getText().toString().equals("000")) {
            data.add(new String[]{getString(R.string.activities), activitiesTextView.getText().toString()});
        }
        if (!utilitiesTextView.getText().toString().equals("000")) {
            data.add(new String[]{getString(R.string.utilities), utilitiesTextView.getText().toString()});
        }
        if (!creditCardTextView.getText().toString().equals("000")) {
            data.add(new String[]{getString(R.string.credit_card), creditCardTextView.getText().toString()});
        }
        if (!taxesTextView.getText().toString().equals("000")) {
            data.add(new String[]{getString(R.string.taxes), taxesTextView.getText().toString()});
        }
        if (!mortgageTextView.getText().toString().equals("000")) {
            data.add(new String[]{getString(R.string.mortgage), mortgageTextView.getText().toString()});
        }
        if (!otherExpense.getText().toString().equals("000")) {
            data.add(new String[]{getString(R.string.other_expense), otherExpense.getText().toString()});
        }

        return data;
    }

    /**
     * Hides all category views
     */
    private void hideAllTextViews(){
        getActivity().findViewById(R.id.salary_layout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.rental_income_layout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.interest_layout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.gifts_layout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.other_income_layout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.taxes_layout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.mortgage_layout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.credit_card_layout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.utilities_layout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.food_layout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.car_payment_layout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.personal_layout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.activities_layout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.other_expenses_layout).setVisibility(View.GONE);
    }
    /**
     * Runs showcase presentation on fragment start
     **/
    private void startShowcase() {
        if (mActivity.findViewById(R.id.action_filter) != null && mActivity.findViewById(R.id.document_share) != null) {
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

}