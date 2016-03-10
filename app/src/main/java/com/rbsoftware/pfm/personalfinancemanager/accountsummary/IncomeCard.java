package com.rbsoftware.pfm.personalfinancemanager.accountsummary;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rbsoftware.pfm.personalfinancemanager.MainActivity;
import com.rbsoftware.pfm.personalfinancemanager.R;

import java.util.Locale;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Holds methods for income card
 *
 * @author Roman Burzakovskiy
 */
public class IncomeCard extends Card {
    private final int[] incomeValues;

    public IncomeCard(Context context, int[] incomeValues) {
        super(context);
        this.incomeValues = incomeValues;
        IncomeHeaderInnerCard header = new IncomeHeaderInnerCard(context);
        this.addCardHeader(header);
    }

    public String getTotalIncomeValue() {
        return ((IncomeHeaderInnerCard) this.getCardHeader()).incomeTextView.getText().toString();
    }

    public String getSalaryValue() {
        return ((IncomeHeaderInnerCard) this.getCardHeader()).salaryTextView.getText().toString();
    }

    public String getRentalIncomeValue() {
        return ((IncomeHeaderInnerCard) this.getCardHeader()).rentalIncomeTextView.getText().toString();
    }

    public String getInterestValue() {
        return ((IncomeHeaderInnerCard) this.getCardHeader()).interestTextView.getText().toString();
    }

    public String getGiftsValue() {
        return ((IncomeHeaderInnerCard) this.getCardHeader()).giftsTextView.getText().toString();
    }

    public String getOtherIncomeValue() {
        return ((IncomeHeaderInnerCard) this.getCardHeader()).otherIncomeTextView.getText().toString();
    }

    private class IncomeHeaderInnerCard extends CardHeader {
        private TextView incomeTextView;
        private TextView salaryTextView;
        private TextView rentalIncomeTextView;
        private TextView interestTextView;
        private TextView giftsTextView;
        private TextView otherIncomeTextView;

        public IncomeHeaderInnerCard(Context context) {
            super(context, R.layout.account_summary_income_card_layout);

        }


        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            super.setupInnerViewElements(parent, view);
            if (incomeTextView == null) {
                incomeTextView = (TextView) view.findViewById(R.id.tv_income);
            }
            if (salaryTextView == null) {
                salaryTextView = (TextView) view.findViewById(R.id.tv_income_salary);
            }
            if (rentalIncomeTextView == null) {
                rentalIncomeTextView = (TextView) view.findViewById(R.id.tv_income_rental);
            }
            if (interestTextView == null) {
                interestTextView = (TextView) view.findViewById(R.id.tv_income_interest);
            }
            if (giftsTextView == null) {
                giftsTextView = (TextView) view.findViewById(R.id.tv_income_gifts);
            }
            if (otherIncomeTextView == null) {
                otherIncomeTextView = (TextView) view.findViewById(R.id.tv_income_other);
            }

            hideAllTextViews(view);

            String incomeString = String.format(Locale.getDefault(), "%,d", incomeValues[0]) + " " + MainActivity.defaultCurrency;
            incomeTextView.setText(incomeString);

            //check if any data fetched. If no data set empty views
            if (incomeValues[0] == 0) {
                view.findViewById(R.id.emptyIncome).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.emptyIncome).setVisibility(View.GONE);
            }

            if (incomeValues[1] != 0) {
                view.findViewById(R.id.salary_layout).setVisibility(View.VISIBLE);
                salaryTextView.setText(String.format(Locale.getDefault(), "%,d", incomeValues[1]));
            }
            if (incomeValues[2] != 0) {
                view.findViewById(R.id.rental_income_layout).setVisibility(View.VISIBLE);
                rentalIncomeTextView.setText(String.format(Locale.getDefault(), "%,d", incomeValues[2]));
            }
            if (incomeValues[3] != 0) {
                view.findViewById(R.id.interest_layout).setVisibility(View.VISIBLE);
                interestTextView.setText(String.format(Locale.getDefault(), "%,d", incomeValues[3]));
            }
            if (incomeValues[4] != 0) {
                view.findViewById(R.id.gifts_layout).setVisibility(View.VISIBLE);
                giftsTextView.setText(String.format(Locale.getDefault(), "%,d", incomeValues[4]));
            }
            if (incomeValues[5] != 0) {
                view.findViewById(R.id.other_income_layout).setVisibility(View.VISIBLE);
                otherIncomeTextView.setText(String.format(Locale.getDefault(), "%,d", incomeValues[5]));
            }
        }

        /**
         * Hides all category views
         */
        private void hideAllTextViews(View view) {
            view.findViewById(R.id.salary_layout).setVisibility(View.GONE);
            view.findViewById(R.id.rental_income_layout).setVisibility(View.GONE);
            view.findViewById(R.id.interest_layout).setVisibility(View.GONE);
            view.findViewById(R.id.gifts_layout).setVisibility(View.GONE);
            view.findViewById(R.id.other_income_layout).setVisibility(View.GONE);
        }
    }
}
