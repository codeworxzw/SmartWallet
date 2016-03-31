package com.rbsoftware.pfm.personalfinancemanager.accountsummary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;

import com.rbsoftware.pfm.personalfinancemanager.FinanceDocument;
import com.rbsoftware.pfm.personalfinancemanager.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds method for loading account summary data in background
 *
 * @author Roman Burzakovskiy
 */
public class AccountSummaryLoader extends AsyncTaskLoader<List<Integer>> {
    public static final String ACTION = "AccountSummaryLoader.FORCELOAD";

    public AccountSummaryLoader(Context context) {
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
    public List<Integer> loadInBackground() {

        List<FinanceDocument> financeDocumentList = MainActivity.financeDocumentModel.queryFinanceDocumentsByDate(MainActivity.readFromSharedPreferences(getContext(), "periodAccSummary", "thisWeek"), MainActivity.getUserId(), FinanceDocument.DOC_TYPE);

        return getValuesFromList(financeDocumentList);
    }


    @Override
    public void deliverResult(List<Integer> data) {
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
     * Retrieves values from documents list.
     *
     * @param list FinanceDocument list
     **/

    private List<Integer> getValuesFromList(List<FinanceDocument> list) {
        List<Integer> data = new ArrayList<>();
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

        data.add(salarySum);
        data.add(rentalIncomeSum);
        data.add(interestSum);
        data.add(giftsSum);
        data.add(otherIncomeSum);

        data.add(taxesSum);
        data.add(mortgageSum);
        data.add(creditCardSum);
        data.add(utilitiesSum);
        data.add(foodSum);
        data.add(carPaymentSum);
        data.add(personalSum);
        data.add(activitiesSum);
        data.add(otherExpensesSum);

        return data;

    }
}
