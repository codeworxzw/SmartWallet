package com.rbsoftware.pfm.personalfinancemanager;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudant.sync.datastore.ConflictException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.CardViewNative;

/**
 * Created by burzakovskiy on 12/24/2015.
 */
public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.HistoryViewHolder> {

    private ActionMode mActionMode = null;
    private List<FinanceDocument> documentList;
    private Context mContext;
    public HistoryRecyclerAdapter(Context context, List<FinanceDocument> documentList) {
        this.documentList = documentList;
        this.mContext = context;
    }
    @Override
    public HistoryRecyclerAdapter.HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_row, parent, false);

        HistoryViewHolder viewHolder = new HistoryViewHolder(view);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final HistoryRecyclerAdapter.HistoryViewHolder holder, int position) {
        final FinanceDocument doc = documentList.get(position);

        //Create a Card
        final Card card = new Card(mContext);

        //Create a CardHeader
        HistoryHeaderInnerCard header = new HistoryHeaderInnerCard(mContext, doc.getNormalDate(FinanceDocument.DATE_FORMAT_LONG), doc.getTotalIncome(), doc.getTotalExpense());


        // Callback to card long click
        final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
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

                        new AlertDialog.Builder(mContext)
                                .setTitle(mContext.getString(R.string.delete_dialog_title))
                                .setMessage(mContext.getString(R.string.delete_dialog_message))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            MainActivity.financeDocumentModel.deleteDocument(doc);
                                            documentList.remove(holder.getAdapterPosition());
                                            notifyItemRemoved(holder.getAdapterPosition());
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
                            ExportData.exportHistoryAsCsv(mContext, doc);
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
                    holder.mCardView.setActivated(false);
            }
        };

        // Card long click listener
        card.setOnLongClickListener(new Card.OnLongCardClickListener() {
            @Override
            public boolean onLongClick(Card card, View view) {
                if (mActionMode != null) {
                    view.setActivated(false);
                    mActionMode.finish();
                    return false;
                }
                // Start the CAB using the ActionMode.Callback defined below
                mActionMode = view.startActionMode(mActionModeCallback);
                view.setActivated(true);
                return true;

            }
        });
        //Add Header to card

        card.addCardHeader(header);

        //This provide a simple (and useless) expand area
        HistoryExpandCard expand = new HistoryExpandCard(mContext, doc);

        //Set inner title in Expand Area
        //expand.setTitle("dummy text");

        //Add expand to card
        card.addCardExpand(expand);

        ViewToClickToExpand viewToClickToExpand =
                ViewToClickToExpand.builder()
                        .setupView(holder.mCardView);
        card.setViewToClickToExpand(viewToClickToExpand);
        holder.mCardView.setCard(card);



    }



    @Override
    public int getItemCount() {
        return (null != documentList ? documentList.size() : 0);
    }


    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        private CardViewNative mCardView ;

        public HistoryViewHolder(View view) {
            super(view);

            this.mCardView = (CardViewNative) view.findViewById(R.id.historyCardView);
        }

    }



    //Helper class to customize card header
    private class HistoryHeaderInnerCard extends CardHeader {
        String income;
        String expense;
        String date;
        public HistoryHeaderInnerCard(Context context, String date, int totalIncome, int totalExpense ) {
            super(context, R.layout.history_list_row_inner_layout);
            this.date = date;
            this.income = "+" + Integer.toString(totalIncome)+" "+MainActivity.DEFAULT_CURRENCY;
            this.expense = "-" + Integer.toString(totalExpense)+" "+MainActivity.DEFAULT_CURRENCY;
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            super.setupInnerViewElements(parent, view);
            if (view!=null){


                TextView dateView = (TextView) view.findViewById(R.id.textViewDate);
                if (dateView!=null)
                    dateView.setText(date);

                TextView incomeView = (TextView) view.findViewById(R.id.textViewIncome);
                if (incomeView!=null)
                    incomeView.setText(income);

                TextView expenseView = (TextView) view.findViewById(R.id.textViewExpense);
                if (expenseView!=null)
                    expenseView.setText(expense);
            }
        }
    }


    //Helper class to customize expand card layout
    private class HistoryExpandCard extends CardExpand {
        LinearLayout mLayout;
        FinanceDocument doc;
        //Use your resource ID for your inner layout
        public HistoryExpandCard(Context context, FinanceDocument doc) {
            super(context, R.layout.history_expand_card_layout);
            this.doc = doc;
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            super.setupInnerViewElements(parent, view);
            List<String> value = new ArrayList<>();
            mLayout = (LinearLayout) view.findViewById(R.id.history_expand_card_layout);
            for(int i=1;i<=doc.getValuesMap().size(); i++){
                value = doc.getValuesMap().get(i);
                if(Integer.valueOf(value.get(0)) != 0){
                    String output="";
                    /* Recursion disabled in version 1.0
                    TODO enable recursion in future versions
                    if (!value.get(2).equals(mContext.getString(R.string.never))){

                        output = value.get(0)+" "+value.get(1)+" "+mContext.getString(R.string.recurs)+" "+value.get(2);
                    }
                    else{
                        output =value.get(0)+" "+value.get(1);
                    } */
                    output =value.get(0)+" "+value.get(1);
                mLayout.addView(createNewTextView(i, output));
                }
            }
        }

        private TextView createNewTextView(int i, String value){

            final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            final TextView mTextView = new TextView(mContext);
            String row;
            mTextView.setLayoutParams(layoutParams);
            row = keyToString(i) + " " + value;
            mTextView.setText(row);
            return mTextView;
        }


        /* Converts int key to human readable string
        * @param key value range 1-14
        * @return string value
        */
        private String keyToString(int key){
            switch (key){
                case 1: return mContext.getResources().getString(R.string.salary);
                case 2: return mContext.getResources().getString(R.string.rental_income);
                case 3: return mContext.getResources().getString(R.string.interest);
                case 4: return mContext.getResources().getString(R.string.gifts);
                case 5: return mContext.getResources().getString(R.string.other_income);
                case 6: return mContext.getResources().getString(R.string.taxes);
                case 7: return mContext.getResources().getString(R.string.mortgage);
                case 8: return mContext.getResources().getString(R.string.credit_card);
                case 9: return mContext.getResources().getString(R.string.utilities);
                case 10: return mContext.getResources().getString(R.string.food);
                case 11: return mContext.getResources().getString(R.string.car_payment);
                case 12: return mContext.getResources().getString(R.string.personal);
                case 13: return mContext.getResources().getString(R.string.activities);
                case 14: return mContext.getResources().getString(R.string.other_expense);
            }
            return "";
        }


    }


}
