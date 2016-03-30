package com.rbsoftware.pfm.personalfinancemanager.accountsummary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.rbsoftware.pfm.personalfinancemanager.FinanceDocument;
import com.rbsoftware.pfm.personalfinancemanager.FinanceDocumentModel;
import com.rbsoftware.pfm.personalfinancemanager.MainActivity;
import com.rbsoftware.pfm.personalfinancemanager.R;
import com.rbsoftware.pfm.personalfinancemanager.budget.BudgetDocument;
import com.rbsoftware.pfm.personalfinancemanager.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by burzakovskiy on 3/30/2016.
 */
public class BudgetAlertLoader extends AsyncTaskLoader<BudgetAlertCard> {
    private static final String TAG = "BudgetAlertLoader";
    public static final String ACTION = "BudgetAlertLoader.FORCELOAD";
    public BudgetAlertLoader(Context context) {
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
    public BudgetAlertCard loadInBackground() {
        List<BudgetDocument> docListBudget = MainActivity.financeDocumentModel.queryBudgetDocumentsByDate(DateUtils.THIS_YEAR, MainActivity.getUserId(), BudgetDocument.DOC_TYPE, FinanceDocumentModel.ORDER_DESC);
        Log.d(TAG, "query "+ docListBudget.size());
        List<FinanceDocument> docListFinance = MainActivity.financeDocumentModel.queryFinanceDocumentsByDate(DateUtils.THREE_MONTHS, MainActivity.getUserId(), FinanceDocument.DOC_TYPE, FinanceDocumentModel.ORDER_DESC);
        int[] expenses = getExpenses(docListFinance);
        return new BudgetAlertCard(getContext(), getAlertDocsList(docListBudget, expenses), expenses);
    }

    @Override
    public void deliverResult(BudgetAlertCard data) {
        super.deliverResult(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            forceLoad();
        }
    };

    private List<BudgetDocument> getAlertDocsList(List<BudgetDocument> docList, int[] expenses){
        List<BudgetDocument> data = new ArrayList<>();
        for(BudgetDocument doc : docList){
            if (doc.getPeriod().equals(getContext().getResources().getStringArray(R.array.budget_period_spinner)[0]) && ((float)expenses[0] >=doc.getValue()*0.75f)) {
                data.add(doc);
            }
            if (doc.getPeriod().equals(getContext().getResources().getStringArray(R.array.budget_period_spinner)[1]) && ((float)expenses[1] >=doc.getValue()*0.75f)){
                data.add(doc);
            }
        }
        Log.d(TAG, "output "+ data.size());
        return data;
    }
    private int[] getExpenses(List<FinanceDocument> docList){
        int[] data = {0, 0};

        for (FinanceDocument doc : docList) {
            long date = Long.valueOf(doc.getDate());
            if (date >= DateUtils.getFirstDateOfCurrentWeek()) {
                data[0] += doc.getTotalExpense();
            }

            if (date >= DateUtils.getFirstDateOfCurrentMonth()) {
                data[1] += doc.getTotalExpense();
            }


        }
        Log.d(TAG, data[0]+ " "+data[1]);
        return data;

    }

}
