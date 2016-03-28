package com.rbsoftware.pfm.personalfinancemanager.budget;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.rbsoftware.pfm.personalfinancemanager.MainActivity;
import com.rbsoftware.pfm.personalfinancemanager.R;
import com.rbsoftware.pfm.personalfinancemanager.utils.Utils;

import java.util.Locale;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Holds method for budget card interface
 *
 * @author Roman Burzakovskiy
 */
public class BudgetCard extends Card {

    private final BudgetDocument doc;
    private int[] totalExpenseIncomeData;

    /**
     * Counstructor of budget card
     *
     * @param context          of application
     * @param doc              budget document
     * @param totalExpenseData expense data
     */
    public BudgetCard(Context context, BudgetDocument doc, int[] totalExpenseData) {

        super(context, R.layout.budget_card_main_inner_layout);
        this.doc = doc;
        this.totalExpenseIncomeData = totalExpenseData;
        this.setHeader(totalExpenseData);

    }

    /**
     * Gets budget document
     *
     * @return budget document
     */
    public BudgetDocument getDocument() {
        return doc;
    }

    /**
     * Adds header to card
     *
     * @param totalExpenseData total expense array
     */
    public void setHeader(int[] totalExpenseData) {
        //Create a CardHeader
        BudgetHeaderInnerCard header = new BudgetHeaderInnerCard(mContext, doc.getName(), doc.getPeriod(), doc.getValue(), doc.getThreshold(), totalExpenseData);
        this.addCardHeader(header);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.budget_card_content_wrapper);
        if (linearLayout != null) {
            linearLayout.removeAllViewsInLayout();
            addProgressIndicators(linearLayout);
        }
    }


    /**
     * Adds budget execution indicators
     *
     * @param linearLayout of wrapper
     */
    private void addProgressIndicators(LinearLayout linearLayout) {
        if (doc.getPeriod().equals(getContext().getResources().getStringArray(R.array.budget_period_spinner)[0])) {
            for (int i = 0; i < 3; i++) {
                if (totalExpenseIncomeData[i] != 0) {
                    linearLayout.addView(createNewProgressRow(mContext.getResources().getStringArray(R.array.budget_card_periods)[i], totalExpenseIncomeData[i]));
                }
            }
        } else {
            for (int i = 4; i < 7; i++) {
                if (totalExpenseIncomeData[i] != 0) {
                    linearLayout.addView(createNewProgressRow(mContext.getResources().getStringArray(R.array.budget_card_periods)[i-1], totalExpenseIncomeData[i]));
                }
            }
        }

    }

    /**
     * Generates budget execution row
     *
     * @param period   of budget
     * @param progress of budget
     * @return layout with progress views
     */
    private LinearLayout createNewProgressRow(String period, int progress) {
        final LinearLayout linearLayout = new LinearLayout(mContext);
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);

        final LinearLayout.LayoutParams layoutParamsPeriod = new LinearLayout.LayoutParams(0, Utils.dpToPx(mContext, 40), 30f);
        layoutParamsPeriod.setMargins(0, Utils.dpToPx(getContext(), 8), 0, 0);
        final TextView tvPeriod = new TextView(mContext);
        tvPeriod.setLayoutParams(layoutParamsPeriod);
        tvPeriod.setText(period);
        tvPeriod.setTextColor(Color.GRAY);
        linearLayout.addView(tvPeriod);

        final LinearLayout.LayoutParams layoutParamsProgress = new LinearLayout.LayoutParams(0, Utils.dpToPx(mContext, 40), 70f);
        if (progress <= doc.getValue()) {
            final NumberProgressBar progressBar = new NumberProgressBar(mContext);
            progressBar.setLayoutParams(layoutParamsProgress);
            progressBar.setPadding(Utils.dpToPx(getContext(), 8), 0, Utils.dpToPx(getContext(), 8), 0);
            progressBar.setMax(doc.getValue());
            progressBar.setProgress(progress);
            progressBar.setReachedBarColor(Utils.getProgressColor(getContext(), progressBar.getMax(), doc.getThreshold(), progressBar.getProgress()));
            progressBar.setProgressTextColor(Utils.getProgressColor(getContext(), progressBar.getMax(), doc.getThreshold(), progressBar.getProgress()));
            progressBar.setProgressTextSize(Utils.dpToPx(getContext(), 14));
            linearLayout.addView(progressBar);
        } else {
            layoutParamsProgress.setMargins(0, Utils.dpToPx(getContext(), 8), 0, 0);
            final TextView tvBudgetExceeded = new TextView(mContext);
            tvBudgetExceeded.setLayoutParams(layoutParamsProgress);
            tvBudgetExceeded.setPadding(Utils.dpToPx(getContext(), 8), 0, Utils.dpToPx(getContext(), 8), 0);
            int exceed = Math.round((float) progress / doc.getValue() * 100f - 100.0f);
            Log.d(TAG, exceed + "");
            String text = mContext.getString(R.string.budget_exceeded) + " " + exceed + "%";
            tvBudgetExceeded.setText(text);
            tvBudgetExceeded.setTextColor(ContextCompat.getColor(mContext, R.color.expense));
            linearLayout.addView(tvBudgetExceeded);

        }

        return linearLayout;
    }


    /**
     * Helper class to customize card header
     */

    private class BudgetHeaderInnerCard extends CardHeader {

        private final String name;
        private final String period;
        private final int budgetValue;
        private final int threshold;
        private int[] totalExpenseIncomeData;

        public BudgetHeaderInnerCard(Context context,String name, String period, int value, int threshold, int[] totalExpenseIncomeData) {
            super(context, R.layout.budget_card_header_inner_layout);
            this.name = name;
            this.period = period;
            this.budgetValue = value;
            this.threshold = threshold;
            this.totalExpenseIncomeData = totalExpenseIncomeData;


        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            super.setupInnerViewElements(parent, view);
            if (view != null) {

                TextView nameView = (TextView) view.findViewById(R.id.tv_budget_card_header_name);
                if (nameView != null)
                    nameView.setText(name);

                TextView periodView = (TextView) view.findViewById(R.id.tv_budget_card_header_period);
                if (periodView != null)
                    periodView.setText(period);

                TextView valueView = (TextView) view.findViewById(R.id.tv_budget_card_header_value);
                if (valueView != null) {
                    String valueText = String.format(Locale.getDefault(), "%,d",budgetValue) + " " + MainActivity.defaultCurrency;
                    valueView.setText(valueText);
                }

                TextView thresholdView = (TextView) view.findViewById(R.id.tv_budget_card_header_threshold);
                if (thresholdView != null) {
                    thresholdView.setText(String.format(Locale.getDefault(), "%,d",threshold));
                }

                TextView estimatedBalance = (TextView) view.findViewById(R.id.tv_budget_card_header_estimated_balance);

                if (doc.getPeriod().equals(getContext().getResources().getStringArray(R.array.budget_period_spinner)[0])) {
                    int estimatedBalanceValue = totalExpenseIncomeData[3] - budgetValue;
                    estimatedBalance.setText(String.format(Locale.getDefault(), "%,d",estimatedBalanceValue));
                }
                else{
                    int estimatedBalanceValue = totalExpenseIncomeData[7] - budgetValue;

                    estimatedBalance.setText(String.format(Locale.getDefault(), "%,d",estimatedBalanceValue));

                }

            }
        }


    }
}
