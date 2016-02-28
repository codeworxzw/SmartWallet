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

import it.gmariotti.cardslib.library.view.CardViewNative;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * A simple {@link Fragment} subclass.
 * Account summary fragment holds account data
 **/
public class AccountSummary extends Fragment {

    private final String TAG = "AccountSummary";


    private String selectedItem;
    private TextView mTextViewPeriod;
    private Context mContext;
    private Activity mActivity;
    private CardViewNative mBalanceCardView;
    private CardViewNative mIncomeCardView;
    private CardViewNative mExpenseCardView;
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

        if (mBalanceCardView == null) {
            mBalanceCardView = (CardViewNative) getActivity().findViewById(R.id.account_summary_balance_card);
        }
        if (mIncomeCardView == null) {
            mIncomeCardView = (CardViewNative) getActivity().findViewById(R.id.account_summary_income_card);
        }
        if (mExpenseCardView == null) {
            mExpenseCardView = (CardViewNative) getActivity().findViewById(R.id.account_summary_expense_card);
        }

        if (mTextViewPeriod == null) {
            mTextViewPeriod = (TextView) getActivity().findViewById(R.id.tv_period);
        }


        MainActivity.fab.show();
        mContext = getContext();
        mActivity = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate(MainActivity.readFromSharedPreferences(getActivity(), "periodAccSummary", "thisWeek"), MainActivity.getUserId());
        mTextViewPeriod.setText(MainActivity.readFromSharedPreferences(getActivity(), "periodTextAccSummary", getResources().getString(R.string.this_week)));
        setValuesFromList(financeDocumentList);


    }

    //Create options menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
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

        String balanceString = String.format(Locale.getDefault(), "%,d", totalIncome - totalExpense) + " " + MainActivity.defaultCurrency;

        BalanceCard mBalanceCard = new BalanceCard(mContext, balanceString);
        mBalanceCard.setBackgroundColorResourceId(R.color.balance);
        if (mBalanceCardView.getCard() == null) {
            mBalanceCardView.setCard(mBalanceCard);
        } else {
            mBalanceCardView.replaceCard(mBalanceCard);
        }

        int[] incomeArray = {totalIncome, salarySum, rentalIncomeSum, interestSum, giftsSum, otherIncomeSum};
        IncomeCard mIncomeCard = new IncomeCard(mContext, incomeArray);
        mIncomeCard.setBackgroundColorResourceId(R.color.income);
        if (mIncomeCardView.getCard() == null) {
            mIncomeCardView.setCard(mIncomeCard);
        } else {
            mIncomeCardView.replaceCard(mIncomeCard);
        }

        int[] expenseArray = {totalExpense, taxesSum, mortgageSum, creditCardSum, utilitiesSum, foodSum, carPaymentSum, personalSum, activitiesSum, otherExpensesSum};
        ExpenseCard mExpenseCard = new ExpenseCard(mContext, expenseArray);
        mExpenseCard.setBackgroundColorResourceId(R.color.expense);
        if (mExpenseCardView.getCard() == null) {
            mExpenseCardView.setCard(mExpenseCard);
        } else {
            mExpenseCardView.replaceCard(mExpenseCard);
        }

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
        data.add(new String[]{getString(R.string.balance), ((BalanceCard) mBalanceCardView.getCard()).getBalanceValue()});
        data.add(new String[]{"", ""});

        data.add(new String[]{getString(R.string.income), ((IncomeCard) mIncomeCardView.getCard()).getTotalIncomeValue()});
        data.add(new String[]{"", ""});
        if (!((IncomeCard) mIncomeCardView.getCard()).getSalaryValue().equals("000")) {
            data.add(new String[]{getString(R.string.salary), ((IncomeCard) mIncomeCardView.getCard()).getSalaryValue()});
        }
        if (!((IncomeCard) mIncomeCardView.getCard()).getRentalIncomeValue().equals("000")) {
            data.add(new String[]{getString(R.string.rental_income), ((IncomeCard) mIncomeCardView.getCard()).getRentalIncomeValue()});
        }
        if (!((IncomeCard) mIncomeCardView.getCard()).getInterestValue().equals("000")) {
            data.add(new String[]{getString(R.string.interest), ((IncomeCard) mIncomeCardView.getCard()).getInterestValue()});
        }
        if (!((IncomeCard) mIncomeCardView.getCard()).getGiftsValue().equals("000")) {
            data.add(new String[]{getString(R.string.gifts), ((IncomeCard) mIncomeCardView.getCard()).getGiftsValue()});
        }
        if (!((IncomeCard) mIncomeCardView.getCard()).getOtherIncomeValue().equals("000")) {
            data.add(new String[]{getString(R.string.other_income), ((IncomeCard) mIncomeCardView.getCard()).getOtherIncomeValue()});
        }
        data.add(new String[]{"", ""});
        data.add(new String[]{getString(R.string.expense), ((ExpenseCard) mExpenseCardView.getCard()).getTotalExpenseValue()});
        data.add(new String[]{"", ""});
        if (!((ExpenseCard) mExpenseCardView.getCard()).getFoodValue().equals("000")) {
            data.add(new String[]{getString(R.string.food), ((ExpenseCard) mExpenseCardView.getCard()).getFoodValue()});
        }
        if (!((ExpenseCard) mExpenseCardView.getCard()).getCarPaymentValue().equals("000")) {
            data.add(new String[]{getString(R.string.car_payment), ((ExpenseCard) mExpenseCardView.getCard()).getCarPaymentValue()});
        }
        if (!((ExpenseCard) mExpenseCardView.getCard()).getPersonalValue().equals("000")) {
            data.add(new String[]{getString(R.string.personal), ((ExpenseCard) mExpenseCardView.getCard()).getPersonalValue()});
        }
        if (!((ExpenseCard) mExpenseCardView.getCard()).getUtilitiesValue().equals("000")) {
            data.add(new String[]{getString(R.string.activities), ((ExpenseCard) mExpenseCardView.getCard()).getUtilitiesValue()});
        }
        if (!((ExpenseCard) mExpenseCardView.getCard()).getActivitiesValue().equals("000")) {
            data.add(new String[]{getString(R.string.utilities), ((ExpenseCard) mExpenseCardView.getCard()).getActivitiesValue()});
        }
        if (!((ExpenseCard) mExpenseCardView.getCard()).getCreditCardValue().equals("000")) {
            data.add(new String[]{getString(R.string.credit_card), ((ExpenseCard) mExpenseCardView.getCard()).getCreditCardValue()});
        }
        if (!((ExpenseCard) mExpenseCardView.getCard()).getTaxesValue().equals("000")) {
            data.add(new String[]{getString(R.string.taxes), ((ExpenseCard) mExpenseCardView.getCard()).getTaxesValue()});
        }
        if (!((ExpenseCard) mExpenseCardView.getCard()).getMortgageValue().equals("000")) {
            data.add(new String[]{getString(R.string.mortgage), ((ExpenseCard) mExpenseCardView.getCard()).getMortgageValue()});
        }
        if (!((ExpenseCard) mExpenseCardView.getCard()).getOtherExpenseValue().equals("000")) {
            data.add(new String[]{getString(R.string.other_expense), ((ExpenseCard) mExpenseCardView.getCard()).getOtherExpenseValue()});
        }

        return data;
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