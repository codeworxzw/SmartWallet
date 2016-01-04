package com.rbsoftware.pfm.personalfinancemanager;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
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


    private List<FinanceDocument> documentList;
    private Context mContext;
    public HistoryRecyclerAdapter(Context context, List<FinanceDocument> documentList) {
        this.documentList = documentList;
        this.mContext = context;
    }
    @Override
    public HistoryRecyclerAdapter.HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_row, null);

        HistoryViewHolder viewHolder = new HistoryViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final HistoryRecyclerAdapter.HistoryViewHolder holder, int position) {
        FinanceDocument doc = documentList.get(position);

        //Create a Card
        Card card = new Card(mContext);

        //Create a CardHeader
        HistoryHeaderInnerCard header = new HistoryHeaderInnerCard(mContext, doc.getNormalDate(), doc.getTotalIncome(), doc.getTotalExpense());
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
            this.income = "+" + Integer.toString(totalIncome);
            this.expense = "-" + Integer.toString(totalExpense);
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
            mLayout = (LinearLayout) view.findViewById(R.id.history_expand_card_layout);
            for(int i=1;i<=doc.getValuesMap().size(); i++){
                int value = doc.getValuesMap().get(i);
                if(value != 0){
                mLayout.addView(createNewTextView(i, value));
                }
            }
        }

        private TextView createNewTextView(int i, int value){

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
