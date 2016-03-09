package com.rbsoftware.pfm.personalfinancemanager.history;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;

import com.rbsoftware.pfm.personalfinancemanager.FinanceDocument;
import com.rbsoftware.pfm.personalfinancemanager.FinanceDocumentModel;
import com.rbsoftware.pfm.personalfinancemanager.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds method for loading account summary data in background
 *
 * @author Roman Burzakovskiy
 */
public class HistoryLoader extends AsyncTaskLoader<List<HistoryCard>> {

    public static final String ACTION = "HistoryLoader.FORCELOAD";

    public HistoryLoader(Context context) {
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
    public List<HistoryCard> loadInBackground() {
        ArrayList<HistoryCard> cards = new ArrayList<>();
        List<FinanceDocument> docList = MainActivity.financeDocumentModel.queryDocumentsByDate("thisYear", MainActivity.getUserId(), FinanceDocumentModel.ORDER_DESC);
        for (int i = 0; i < docList.size(); i++) {
            HistoryCard card = new HistoryCard(getContext(), docList.get(i));
            cards.add(card);
        }
        return cards;
    }

    @Override
    public void deliverResult(List<HistoryCard> data) {
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
}
