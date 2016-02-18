package com.rbsoftware.pfm.personalfinancemanager;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cloudant.sync.datastore.ConflictException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


/**
 * A simple {@link Fragment} subclass.
 * Holds user's history
 */
public class History extends Fragment implements CardHeader.OnClickCardHeaderPopupMenuListener {
    private final String TAG = "History";
    private CardRecyclerView mRecyclerView;
    private HistoryCardRecyclerViewAdapter mCardArrayAdapter;
    private HistoryCard card;
    private TextView mEmptyView;
    private Context mContext;
    private Activity mActivity;

    public History() {
        // Required empty public constructor
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
        getActivity().setTitle(getResources().getStringArray(R.array.drawer_menu)[2]);

        mRecyclerView = (CardRecyclerView) getActivity().findViewById(R.id.history_card_recycler_view);
        mEmptyView = (TextView) getActivity().findViewById(R.id.emptyHistory);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mContext = getContext();
        mActivity = getActivity();

    }

    @Override
    public void onResume() {


        super.onResume();
        List<FinanceDocument> docList = MainActivity.financeDocumentModel.queryDocumentsByDate("thisYear", MainActivity.getUserId(), FinanceDocumentModel.ORDER_DESC);
        ArrayList<HistoryCard> cards = new ArrayList<>();


        for (int i = 0; i < docList.size(); i++) {
            card = new HistoryCard(getContext(), docList.get(i));
            card.setHeader();
            card.getCardHeader().setPopupMenu(R.menu.history_card_menu, this);
            card.setExpand();
            cards.add(card);
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
            if (!docList.isEmpty() && docList.size() == 1) {
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
            }
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

    /**
     * Runs showcase presentation on fragment start
     */

    private void startShowcase() {
        if (card.getCardView() != null) {
            ((View) card.getCardView()).measure(0, 0);
            Double r = ((View) card.getCardView()).getMeasuredWidth() / 3.0;
            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(500); // half second between each showcase view
            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(mActivity, TAG);
            sequence.setConfig(config);
            sequence.addSequenceItem(new MaterialShowcaseView.Builder(mActivity)
                    .setTarget(((View) card.getCardView()))
                    .setUseAutoRadius(false)
                    .setRadius(r.intValue())
                    .setContentText(R.string.history_card)
                    .setDismissText(R.string.ok)
                    .setDismissTextColor(ContextCompat.getColor(mContext, R.color.colorAccent))
                    .build());
            sequence.start();
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
