package com.rbsoftware.pfm.personalfinancemanager.budget;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.rbsoftware.pfm.personalfinancemanager.MainActivity;
import com.rbsoftware.pfm.personalfinancemanager.R;
import com.rbsoftware.pfm.personalfinancemanager.utils.DateUtils;
import com.rbsoftware.pfm.personalfinancemanager.utils.Utils;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by burzakovskiy on 3/24/2016.
 */
public class BudgetCard extends Card {

    private final BudgetDocument doc;

    public BudgetCard(Context context, BudgetDocument doc, int[] totalExpenseData) {

        super(context);
        this.doc = doc;
        this.setHeader(totalExpenseData);

    }

    public BudgetDocument getDocument() {
        return doc;
    }

    public void setHeader(int[] totalExpenseData) {
        //Create a CardHeader
        BudgetHeaderInnerCard header = new BudgetHeaderInnerCard(mContext, doc.getDate(), doc.getName(), doc.getPeriod(), doc.getValue(), doc.getThreshold(), totalExpenseData);
        this.addCardHeader(header);
    }

    /**
     * Helper class to customize card header
     */

    private class BudgetHeaderInnerCard extends CardHeader {

        private final String date;
        private final String name;
        private final String period;
        private final int value;
        private final int threshold;
        private final int[] totalExpenseData;

        public BudgetHeaderInnerCard(Context context, String date, String name, String period, int value, int threshold, int[] totalExpenseData) {
            super(context, R.layout.budget_card_header_inner_layout);
            this.date = DateUtils.getNormalDate(DateUtils.DATE_FORMAT_LONG, date);
            this.name = name;
            this.period = period;
            this.value = value;
            this.threshold = threshold;
            this.totalExpenseData = totalExpenseData;

        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            super.setupInnerViewElements(parent, view);
            if (view != null) {
                TextView dateView = (TextView) view.findViewById(R.id.tv_budget_card_header_date);
                if (dateView != null)
                    dateView.setText(date);

                TextView nameView = (TextView) view.findViewById(R.id.tv_budget_card_header_name);
                if (nameView != null)
                    nameView.setText(name);

                TextView periodView = (TextView) view.findViewById(R.id.tv_budget_card_header_period);
                if (periodView != null)
                    periodView.setText(period);

                TextView valueView = (TextView) view.findViewById(R.id.tv_budget_card_header_value);
                if (valueView != null) {
                    String valueText = value + " " + MainActivity.defaultCurrency;
                    valueView.setText(valueText);
                }

                TextView thresholdView = (TextView) view.findViewById(R.id.tv_budget_card_header_threshold);
                if (thresholdView != null) {
                    String thresholdText = threshold + " " + MainActivity.defaultCurrency;
                    thresholdView.setText(thresholdText);
                }

                LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.budget_card_content_wrapper);
                if (linearLayout != null)
                    addProgressIndicators(linearLayout);

            }
        }

        private void addProgressIndicators(LinearLayout linearLayout) {
            if (period.equals(getContext().getResources().getStringArray(R.array.budget_period_spinner)[0])) {
                Log.d(TAG, "weekly");
                for (int i = 0; i < 3; i++) {
                    if (totalExpenseData[i] != 0) {
                        linearLayout.addView(createNewProgressRow("week " + i, totalExpenseData[i]));
                    }
                }
            } else {
                Log.d(TAG, "monthly");
                for (int i = 3; i < 5; i++) {
                    if (totalExpenseData[i] != 0) {
                        linearLayout.addView(createNewProgressRow("month " + i, totalExpenseData[i]));
                    }
                }
            }

        }

        private LinearLayout createNewProgressRow(String period, int progress) {
            final LinearLayout linearLayout = new LinearLayout(mContext);
            final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearLayout.setLayoutParams(layoutParams);

            final LinearLayout.LayoutParams layoutParamsPeriod = new LinearLayout.LayoutParams(0, Utils.dpToPx(mContext, 40), 20f);
            final TextView tvPeriod = new TextView(mContext);
            tvPeriod.setLayoutParams(layoutParamsPeriod);
            tvPeriod.setText(period);
            tvPeriod.setTextColor(Color.GRAY);

            final LinearLayout.LayoutParams layoutParamsProgress = new LinearLayout.LayoutParams(0, Utils.dpToPx(mContext, 40), 80f);
            final NumberProgressBar progressBar = new NumberProgressBar(mContext);
            progressBar.setLayoutParams(layoutParamsProgress);
            progressBar.setMax(value);
            progressBar.setProgress(progress);

            linearLayout.addView(tvPeriod);
            linearLayout.addView(progressBar);


            return linearLayout;
        }
    }
}
