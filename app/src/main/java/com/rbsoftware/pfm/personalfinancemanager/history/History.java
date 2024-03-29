package com.rbsoftware.pfm.personalfinancemanager.history;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cloudant.sync.datastore.ConflictException;
import com.google.android.gms.analytics.HitBuilders;
import com.rbsoftware.pfm.personalfinancemanager.ConnectionDetector;
import com.rbsoftware.pfm.personalfinancemanager.EditDocument;
import com.rbsoftware.pfm.personalfinancemanager.ExportData;
import com.rbsoftware.pfm.personalfinancemanager.FinanceDocument;
import com.rbsoftware.pfm.personalfinancemanager.MainActivity;
import com.rbsoftware.pfm.personalfinancemanager.R;
import com.rbsoftware.pfm.personalfinancemanager.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;


/**
 * A simple {@link Fragment} subclass.
 * Holds user's history
 */
public class History extends Fragment implements CardHeader.OnClickCardHeaderPopupMenuListener {
    private final String TAG = "History";
    private final int HISTORY_LOADER_ID = 2;

    private CardRecyclerView mRecyclerView;
    private HistoryCardRecyclerViewAdapter mCardArrayAdapter;
    private HistoryCard card;
    private TextView mEmptyView;
    private Context mContext;
    private ConnectionDetector mConnectionDetector;

    public History() {
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
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getResources().getStringArray(R.array.drawer_menu)[3]);

        mRecyclerView = (CardRecyclerView) getActivity().findViewById(R.id.history_card_recycler_view);
        mEmptyView = (TextView) getActivity().findViewById(R.id.emptyHistory);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mContext = getContext();
        getLoaderManager().initLoader(HISTORY_LOADER_ID, null, loaderCallbacks);
        if (mConnectionDetector == null) {
            mConnectionDetector = new ConnectionDetector(mContext);
        }
        MainActivity.mTracker.setScreenName(TAG);

    }

    @Override
    public void onResume() {


        super.onResume();


        //check if network is available and send analytics tracker

        if (mConnectionDetector.isConnectingToInternet()) {

            MainActivity.mTracker.send(new HitBuilders.EventBuilder().setCategory("Action").setAction("Open").build());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void generateHistory(List<HistoryCard> cards) {
        for (HistoryCard historyCard : cards) {
            historyCard.getCardHeader().setPopupMenu(R.menu.history_card_menu, this);
        }

        mCardArrayAdapter = new HistoryCardRecyclerViewAdapter(getActivity(), cards);
        //Staggered grid view
        mCardArrayAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkAdapterIsEmpty();
            }
        });

        //Set the empty view
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mCardArrayAdapter);
            checkAdapterIsEmpty();


        }

    }

    /**
     * Checks whether recycler view is empty
     * And switches to empty view
     */
    private void checkAdapterIsEmpty() {
        if (mCardArrayAdapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                List<Object> params = new ArrayList<>();
                ArrayList<String> editResult = data.getStringArrayListExtra("editResult");
                String oldDocId = data.getStringExtra("oldDocId");
                params.add(MainActivity.PARAM_USERID, MainActivity.getUserId());
                params.add(MainActivity.PARAM_SALARY, Utils.getItem(editResult, 0));
                params.add(MainActivity.PARAM_RENTAL_INCOME, Utils.getItem(editResult, 1));
                params.add(MainActivity.PARAM_INTEREST, Utils.getItem(editResult, 2));
                params.add(MainActivity.PARAM_GIFTS, Utils.getItem(editResult, 3));
                params.add(MainActivity.PARAM_OTHER_INCOME, Utils.getItem(editResult, 4));
                params.add(MainActivity.PARAM_TAXES, Utils.getItem(editResult, 5));
                params.add(MainActivity.PARAM_MORTGAGE, Utils.getItem(editResult, 6));
                params.add(MainActivity.PARAM_CREDIT_CARD, Utils.getItem(editResult, 7));
                params.add(MainActivity.PARAM_UTILITIES, Utils.getItem(editResult, 8));
                params.add(MainActivity.PARAM_FOOD, Utils.getItem(editResult, 9));
                params.add(MainActivity.PARAM_CAR_PAYMENT, Utils.getItem(editResult, 10));
                params.add(MainActivity.PARAM_PERSONAL, Utils.getItem(editResult, 11));
                params.add(MainActivity.PARAM_ACTIVITIES, Utils.getItem(editResult, 12));
                params.add(MainActivity.PARAM_OTHER_EXPENSE, Utils.getItem(editResult, 13));
                try {
                    MainActivity.financeDocumentModel.updateFinanceDocument(
                            MainActivity.financeDocumentModel.getFinanceDocument(oldDocId),
                            new FinanceDocument(params));
                } catch (ConflictException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Sends broadcast intent to update history
     */
    private void updateHistory() {
        Intent intent = new Intent(HistoryLoader.ACTION);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }


    private final LoaderManager.LoaderCallbacks<List<HistoryCard>> loaderCallbacks = new LoaderManager.LoaderCallbacks<List<HistoryCard>>() {
        @Override
        public Loader<List<HistoryCard>> onCreateLoader(int id, Bundle args) {
            return new HistoryLoader(getContext());
        }

        @Override
        public void onLoadFinished(Loader<List<HistoryCard>> loader, List<HistoryCard> data) {
            generateHistory(data);
        }

        @Override
        public void onLoaderReset(Loader<List<HistoryCard>> loader) {
        }
    };

    @Override
    public void onMenuItemClick(final BaseCard card, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.history_edit:
                Intent edit = new Intent(getActivity(), EditDocument.class);
                edit.putExtra("docId", ((HistoryCard) card).getDocument().getDocumentRevision().getId());
                startActivityForResult(edit, 2);

                return;

            case R.id.history_delete:

                new AlertDialog.Builder(getContext())
                        .setTitle(getContext().getString(R.string.delete_dialog_title))
                        .setMessage(getContext().getString(R.string.delete_dialog_message))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    MainActivity.financeDocumentModel.deleteDocument(((HistoryCard) card).getDocument());
                                    mCardArrayAdapter.remove((HistoryCard) card);
                                } catch (ConflictException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


                return;

            case R.id.history_share:

                try {
                    ExportData.exportHistoryAsCsv(getContext(), ((HistoryCard) card).getDocument());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;

        }
    }
}
