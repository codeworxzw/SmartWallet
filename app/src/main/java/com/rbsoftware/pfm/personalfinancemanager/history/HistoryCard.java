package com.rbsoftware.pfm.personalfinancemanager.history;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rbsoftware.pfm.personalfinancemanager.FinanceDocument;
import com.rbsoftware.pfm.personalfinancemanager.MainActivity;
import com.rbsoftware.pfm.personalfinancemanager.R;
import com.rbsoftware.pfm.personalfinancemanager.utils.DateUtils;
import com.rbsoftware.pfm.personalfinancemanager.utils.Utils;

import java.util.List;
import java.util.Locale;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;

/**
 * Created by burzakovskiy on 1/16/2016.
 * Custom @link Card layout
 */
public class HistoryCard extends Card {
    private final FinanceDocument doc;
    private final Context mContext;

    public HistoryCard(Context context, FinanceDocument doc) {
        super(context);
        this.mContext = context;
        this.doc = doc;
        this.setHeader();
        this.setExpand();
    }

    public FinanceDocument getDocument() {
        return doc;
    }

    private void setHeader() {
        //Create a CardHeader
        HistoryHeaderInnerCard header = new HistoryHeaderInnerCard(mContext, doc.getDate(), doc.getTotalIncome(), doc.getTotalExpense());
        this.addCardHeader(header);
    }

    private void setExpand() {
        HistoryExpandCard expand = new HistoryExpandCard(mContext, doc);


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
        private final String income;
        private final String expense;
        private final String date;

        public HistoryHeaderInnerCard(Context context, String date, int totalIncome, int totalExpense) {
            super(context, R.layout.history_list_row_inner_layout);
            this.date = DateUtils.getNormalDate(DateUtils.DATE_FORMAT_LONG, date);
            this.income = "+" + String.format(Locale.getDefault(), "%,d", totalIncome) + " " + MainActivity.defaultCurrency;
            this.expense = "-" + String.format(Locale.getDefault(), "%,d", totalExpense) + " " + MainActivity.defaultCurrency;
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            super.setupInnerViewElements(parent, view);
            if (view != null) {


                TextView dateView = (TextView) view.findViewById(R.id.textViewDate);
                if (dateView != null)
                    dateView.setText(date);

                TextView incomeView = (TextView) view.findViewById(R.id.textViewIncome);
                if (incomeView != null)
                    incomeView.setText(income);

                TextView expenseView = (TextView) view.findViewById(R.id.textViewExpense);
                if (expenseView != null)
                    expenseView.setText(expense);
            }
        }
    }

    /**
     * Helper class to customize expand card layout
     */

    private class HistoryExpandCard extends CardExpand {
        private LinearLayout mLayout;
        private final FinanceDocument doc;

        //Use your resource ID for your inner layout
        public HistoryExpandCard(Context context, FinanceDocument doc) {
            super(context, R.layout.history_expand_card_layout);
            this.doc = doc;
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            super.setupInnerViewElements(parent, view);
            List<String> value;
            boolean isIncomeFieldSet = false;
            boolean isExpenseFieldSet = false;

            mLayout = (LinearLayout) view.findViewById(R.id.history_expand_card_layout);
            mLayout.removeAllViewsInLayout();
            for (int i = 1; i <= FinanceDocument.NUMBER_OF_CATEGORIES; i++) {
                value = doc.getValuesMap().get(i);
                if (value != null) {
                    if (i <= 5) isIncomeFieldSet = true;
                    if (i > 5) isExpenseFieldSet = true;
                    if (isIncomeFieldSet && isExpenseFieldSet) {
                        mLayout.addView(createDivider());
                        isIncomeFieldSet = false;
                        isExpenseFieldSet = false;
                    }
                    /* Recursion disabled in version 1.0
                    TODO enable recursion in future versions
                    if (!value.get(2).equals(mContext.getString(R.string.never))){

                        output = value.get(0)+" "+value.get(1)+" "+mContext.getString(R.string.recurs)+" "+value.get(2);
                    }
                    else{
                        output =value.get(0)+" "+value.get(1);
                    } */
                    String output = String.format(Locale.getDefault(), "%,d", Integer.valueOf(value.get(0))) + " " + value.get(1);

                    mLayout.addView(createNewTextView(i, output));


                }
            }
        }

        /**
         * Generates expanded card text views
         *
         * @param i     position in hash map
         * @param value hash map value
         * @return RelativeLayout with two TextViews
         */
        private RelativeLayout createNewTextView(int i, String value) {
            final RelativeLayout mRelativeLayout = new RelativeLayout(mContext);
            final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            final RelativeLayout.LayoutParams layoutParamsCategory = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            final RelativeLayout.LayoutParams layoutParamsData = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            layoutParamsCategory.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParamsData.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            final TextView mTextViewCategory = new TextView(mContext);
            final TextView mTextViewData = new TextView(mContext);
            mRelativeLayout.setLayoutParams(layoutParams);
            mTextViewCategory.setLayoutParams(layoutParamsCategory);
            mTextViewData.setLayoutParams(layoutParamsData);
            String sign = (i < 6) ? "+" : "-";
            String rowCategory = Utils.keyToString(getContext(), i);
            String rowData = sign + value;
            mTextViewCategory.setText(rowCategory);
            mTextViewCategory.setTextColor(ContextCompat.getColor(mContext, R.color.grey));
            mTextViewData.setText(rowData);
            mTextViewData.setTextColor(ContextCompat.getColor(mContext, R.color.grey));
            mRelativeLayout.addView(mTextViewCategory);
            mRelativeLayout.addView(mTextViewData);
            return mRelativeLayout;
        }

        /**
         * Generates divider between income and expense categories
         *
         * @return divider View
         */
        private View createDivider() {
            final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utils.dpToPx(mContext, 1));
            layoutParams.setMargins(0, Utils.dpToPx(mContext, 6), 0, Utils.dpToPx(mContext, 6));
            final View divider = new View(mContext);
            divider.setLayoutParams(layoutParams);
            divider.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey));
            return divider;
        }


    }


}
