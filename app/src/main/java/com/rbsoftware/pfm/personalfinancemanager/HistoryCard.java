package com.rbsoftware.pfm.personalfinancemanager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;

/**
 * Created by burzakovskiy on 1/16/2016.
 * Custom @link Card layout
 */
public class HistoryCard extends Card {
    private FinanceDocument doc;
    private HistoryCard card;
    private Context mContext;
    public HistoryCard(Context context, FinanceDocument doc) {
        super(context);
        this.mContext = context;
        this.doc = doc;
    }

    public FinanceDocument getDocument(){return doc;}
    public void setHeader(){
        //Create a CardHeader
        HistoryHeaderInnerCard header = new HistoryHeaderInnerCard(mContext, doc.getNormalDate(FinanceDocument.DATE_FORMAT_LONG), doc.getTotalIncome(), doc.getTotalExpense());
        this.addCardHeader(header);
    }

    public void setExpand(){
        HistoryExpandCard expand = new HistoryExpandCard(mContext, doc);

        //Set inner title in Expand Area
        //expand.setTitle("dummy text");

        //Add expand to card
        this.addCardExpand(expand);

        ViewToClickToExpand viewToClickToExpand =
                ViewToClickToExpand.builder()
                        .setupCardElement(ViewToClickToExpand.CardElementUI.CARD);
        this.setViewToClickToExpand(viewToClickToExpand);

    }


    /**
     * Helper class to customize card header
     */

    private class HistoryHeaderInnerCard extends CardHeader {
        String income;
        String expense;
        String date;
        public HistoryHeaderInnerCard(Context context, String date, int totalIncome, int totalExpense ) {
            super(context, R.layout.history_list_row_inner_layout);
            this.date = date;
            this.income = "+" + Integer.toString(totalIncome)+" "+MainActivity.defaultCurrency;
            this.expense = "-" + Integer.toString(totalExpense)+" "+MainActivity.defaultCurrency;
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

    /**
     *  Helper class to customize expand card layout
     */

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
            mLayout.removeAllViewsInLayout();
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


        /**
         *  Converts int key to human readable string
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
