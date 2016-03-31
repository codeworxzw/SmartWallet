package com.rbsoftware.pfm.personalfinancemanager.charts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.SparseIntArray;

import com.rbsoftware.pfm.personalfinancemanager.FinanceDocument;
import com.rbsoftware.pfm.personalfinancemanager.MainActivity;

import java.util.List;

/**
 * Holds method for loading income and expense data in background
 *
 * @author Roman Burzakovskiy
 */
public class IncomeExpenseChartLoader extends AsyncTaskLoader<SparseIntArray> {

    public static final String ACTION = "IncomeExpenseChartLoader.FORCELOAD";

    public IncomeExpenseChartLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        IntentFilter intentFilter = new IntentFilter(ACTION);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        forceLoad();
    }

    @Override
    public SparseIntArray loadInBackground() {

        List<FinanceDocument> financeDocumentList = MainActivity.financeDocumentModel.queryFinanceDocumentsByDate(MainActivity.readFromSharedPreferences(getContext(), "period", "thisWeek"), MainActivity.getUserId(), FinanceDocument.DOC_TYPE);

        return getValues(financeDocumentList);
    }

    @Override
    public void deliverResult(SparseIntArray data) {
        super.deliverResult(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            forceLoad();
        }
    };

    /**
     * extracts sums data of FinanceDocuments in the list
     *
     * @param list finance documents list
     * @return map of data types and values
     */
    private SparseIntArray getValues(List<FinanceDocument> list) {
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
        SparseIntArray mapSum = new SparseIntArray();
        mapSum.put(MainActivity.PARAM_SALARY, salarySum);
        mapSum.put(MainActivity.PARAM_RENTAL_INCOME, rentalIncomeSum);
        mapSum.put(MainActivity.PARAM_INTEREST, interestSum);
        mapSum.put(MainActivity.PARAM_GIFTS, giftsSum);
        mapSum.put(MainActivity.PARAM_OTHER_INCOME, otherIncomeSum);
        mapSum.put(MainActivity.PARAM_TAXES, taxesSum);
        mapSum.put(MainActivity.PARAM_MORTGAGE, mortgageSum);
        mapSum.put(MainActivity.PARAM_CREDIT_CARD, creditCardSum);
        mapSum.put(MainActivity.PARAM_UTILITIES, utilitiesSum);
        mapSum.put(MainActivity.PARAM_FOOD, foodSum);
        mapSum.put(MainActivity.PARAM_CAR_PAYMENT, carPaymentSum);
        mapSum.put(MainActivity.PARAM_PERSONAL, personalSum);
        mapSum.put(MainActivity.PARAM_ACTIVITIES, activitiesSum);
        mapSum.put(MainActivity.PARAM_OTHER_EXPENSE, otherExpensesSum);

        return mapSum;
    }
}
