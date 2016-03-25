package com.rbsoftware.pfm.personalfinancemanager.accountsummary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.rbsoftware.pfm.personalfinancemanager.ConnectionDetector;
import com.rbsoftware.pfm.personalfinancemanager.ExportData;
import com.rbsoftware.pfm.personalfinancemanager.MainActivity;
import com.rbsoftware.pfm.personalfinancemanager.R;

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
    private final int INCOME_EXPENSE_CHART_LOADER_ID = 2;

    private String selectedItem;
    private TextView mTextViewPeriod;
    private Context mContext;
    private Activity mActivity;
    private CardViewNative mBalanceCardView;
    private CardViewNative mIncomeCardView;
    private CardViewNative mExpenseCardView;
    private ConnectionDetector mConnectionDetector;

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
        getLoaderManager().initLoader(INCOME_EXPENSE_CHART_LOADER_ID, null, loaderCallbacks);

        if (mConnectionDetector == null) {
            mConnectionDetector = new ConnectionDetector(mContext);
        }
        MainActivity.mTracker.setScreenName(TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        mTextViewPeriod.setText(MainActivity.readFromSharedPreferences(getActivity(), "periodTextAccSummary", getResources().getString(R.string.this_week)));
        //check if network is available and send analytics tracker

        if (mConnectionDetector.isConnectingToInternet()) {

            MainActivity.mTracker.send(new HitBuilders.EventBuilder().setCategory("Action").setAction("Open").build());
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.fab.hide();
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
                        selectedItem = "thisWeek";
                        mTextViewPeriod.setText(getResources().getString(R.string.this_week));

                        break;
                    case R.id.thisMonth:
                        selectedItem = "thisMonth";
                        mTextViewPeriod.setText(getResources().getString(R.string.this_month));

                        break;
                    case R.id.lastWeek:
                        selectedItem = "lastWeek";
                        mTextViewPeriod.setText(getResources().getString(R.string.last_week));

                        break;
                    case R.id.lastMonth:
                        selectedItem = "lastMonth";
                        mTextViewPeriod.setText(getResources().getString(R.string.last_month));

                        break;
                    case R.id.thisYear:
                        selectedItem = "thisYear";
                        mTextViewPeriod.setText(getResources().getString(R.string.this_year));

                        break;
                }
                MainActivity.saveToSharedPreferences(getActivity(), "periodAccSummary", selectedItem);
                MainActivity.saveToSharedPreferences(getActivity(), "periodTextAccSummary", mTextViewPeriod.getText().toString());
                updateCards();
                return false;
            }
        });
        popup.show();

    }

    private void generateCardsData(List<Integer> data) {

        int totalIncome = 0;
        for (int i = 0; i < 5; i++) {
            totalIncome += data.get(i);
        }

        int totalExpense = 0;
        for (int i = 5; i < 14; i++) {
            totalExpense += data.get(i);
        }

        String balanceString = String.format(Locale.getDefault(), "%,d", totalIncome - totalExpense) + " " + MainActivity.defaultCurrency;

        BalanceCard mBalanceCard = new BalanceCard(mContext, balanceString);
        if (mBalanceCardView.getCard() == null) {
            mBalanceCardView.setCard(mBalanceCard);
        } else {
            mBalanceCardView.replaceCard(mBalanceCard);
        }

        int[] incomeArray = {totalIncome, data.get(0), data.get(1), data.get(2), data.get(3), data.get(4)};
        IncomeCard mIncomeCard = new IncomeCard(mContext, incomeArray);
        if (mIncomeCardView.getCard() == null) {
            mIncomeCardView.setCard(mIncomeCard);
        } else {
            mIncomeCardView.replaceCard(mIncomeCard);
        }

        int[] expenseArray = {totalExpense, data.get(5), data.get(6), data.get(7), data.get(8), data.get(9), data.get(10), data.get(11), data.get(12), data.get(13)};
        ExpenseCard mExpenseCard = new ExpenseCard(mContext, expenseArray);
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
     * Sends broadcast intent to update cards
     */
    private void updateCards() {
        Intent intent = new Intent(AccountSummaryLoader.ACTION);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }


    private LoaderManager.LoaderCallbacks<List<Integer>> loaderCallbacks = new LoaderManager.LoaderCallbacks<List<Integer>>() {
        @Override
        public Loader<List<Integer>> onCreateLoader(int id, Bundle args) {
            return new AccountSummaryLoader(getContext());
        }

        @Override
        public void onLoadFinished(Loader<List<Integer>> loader, List<Integer> data) {
            generateCardsData(data);
        }

        @Override
        public void onLoaderReset(Loader<List<Integer>> loader) {
        }
    };

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