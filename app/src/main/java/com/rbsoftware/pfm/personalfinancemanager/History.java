package com.rbsoftware.pfm.personalfinancemanager;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cloudant.sync.datastore.ConflictException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class History extends Fragment implements Card.OnLongCardClickListener{
    private CardRecyclerView mRecyclerView;
    private List<FinanceDocument> docList;
    private ActionMode mActionMode = null;
    private HistoryCardRecyclerViewAdapter mCardArrayAdapter;
    private HistoryCard card;
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
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


    }

    @Override
    public void onResume() {


        super.onResume();
        docList = MainActivity.financeDocumentModel.queryDocumentsByDate("thisYear", MainActivity.getUserId(), FinanceDocumentModel.ORDER_DESC);
        ArrayList<HistoryCard> cards = new ArrayList<HistoryCard>();



        for(int i=0; i < docList.size();i++){
            card = new HistoryCard(getContext(),docList.get(i));
            card.setHeader();
            card.setExpand();
            card.setOnLongClickListener(this);
            cards.add(card);
        }

        mCardArrayAdapter = new HistoryCardRecyclerViewAdapter(getActivity(), cards);
        //Staggered grid view


        //Set the empty view
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mCardArrayAdapter);
        }

    }


    @Override
    public boolean onLongClick(final Card card, final View view) {
        if (mActionMode != null) {
            view.setActivated(false);
            mActionMode.finish();
            return false;
        }
        // Start the CAB using the ActionMode.Callback defined below
        mActionMode = view.startActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate a menu resource providing context menu items
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.history_card_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.history_delete:

                        new AlertDialog.Builder(getContext())
                                .setTitle(getContext().getString(R.string.delete_dialog_title))
                                .setMessage(getContext().getString(R.string.delete_dialog_message))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            MainActivity.financeDocumentModel.deleteDocument(((HistoryCard)card).getDocument());
                                            mCardArrayAdapter.remove(card);
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


                        mode.finish(); // Action picked, so close the CAB
                        return true;

                    case R.id.history_share:
                        try {
                            ExportData.exportHistoryAsCsv(getContext(), ((HistoryCard)card).getDocument());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionMode = null;
                if (card!=null)
                    view.setActivated(false);

            }
        });
        view.setActivated(true);
        return true;

    }
}
