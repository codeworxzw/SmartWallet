package com.rbsoftware.pfm.personalfinancemanager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Holds methods for expense card
 *
 * @author Roman Burzakovskiy
 */
public class ExpenseCard extends Card {
    int[] expenseValues;

    public ExpenseCard(Context context, int[] expenseValues) {
        super(context);
        this.expenseValues = expenseValues;
        ExpenseHeaderInnerCard header = new ExpenseHeaderInnerCard(context);
        this.addCardHeader(header);
    }

    public String getTotalExpenseValue() {
        return ((ExpenseHeaderInnerCard) this.getCardHeader()).expenseTextView.getText().toString();
    }

    public String getTaxesValue() {
        return ((ExpenseHeaderInnerCard) this.getCardHeader()).taxesTextView.getText().toString();
    }

    public String getMortgageValue() {
        return ((ExpenseHeaderInnerCard) this.getCardHeader()).mortgageTextView.getText().toString();
    }

    public String getCreditCardValue() {
        return ((ExpenseHeaderInnerCard) this.getCardHeader()).creditCardTextView.getText().toString();
    }

    public String getUtilitiesValue() {
        return ((ExpenseHeaderInnerCard) this.getCardHeader()).utilitiesTextView.getText().toString();
    }

    public String getFoodValue() {
        return ((ExpenseHeaderInnerCard) this.getCardHeader()).foodTextView.getText().toString();
    }

    public String getCarPaymentValue() {
        return ((ExpenseHeaderInnerCard) this.getCardHeader()).carPaymentTextView.getText().toString();
    }

    public String getPersonalValue() {
        return ((ExpenseHeaderInnerCard) this.getCardHeader()).personalTextView.getText().toString();
    }

    public String getActivitiesValue() {
        return ((ExpenseHeaderInnerCard) this.getCardHeader()).activitiesTextView.getText().toString();
    }

    public String getOtherExpenseValue() {
        return ((ExpenseHeaderInnerCard) this.getCardHeader()).otherExpenseTextView.getText().toString();
    }

    private class ExpenseHeaderInnerCard extends CardHeader {
        private TextView expenseTextView;
        private TextView taxesTextView;
        private TextView mortgageTextView;
        private TextView creditCardTextView;
        private TextView utilitiesTextView;
        private TextView foodTextView;
        private TextView carPaymentTextView;
        private TextView personalTextView;
        private TextView activitiesTextView;
        private TextView otherExpenseTextView;


        public ExpenseHeaderInnerCard(Context context) {
            super(context, R.layout.account_summary_expense_card_layout);
        }


        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            super.setupInnerViewElements(parent, view);

            if (taxesTextView == null) {
                taxesTextView = (TextView) view.findViewById(R.id.tv_expense_taxes);
            }
            if (mortgageTextView == null) {
                mortgageTextView = (TextView) view.findViewById(R.id.tv_expense_mortgage);
            }
            if (creditCardTextView == null) {
                creditCardTextView = (TextView) view.findViewById(R.id.tv_expense_credit_card);
            }
            if (utilitiesTextView == null) {
                utilitiesTextView = (TextView) view.findViewById(R.id.tv_expense_utilities);
            }
            if (foodTextView == null) {
                foodTextView = (TextView) view.findViewById(R.id.tv_expense_food);
            }
            if (carPaymentTextView == null) {
                carPaymentTextView = (TextView) view.findViewById(R.id.tv_expense_car_payment);
            }
            if (personalTextView == null) {
                personalTextView = (TextView) view.findViewById(R.id.tv_expense_personal);
            }
            if (activitiesTextView == null) {
                activitiesTextView = (TextView) view.findViewById(R.id.tv_expense_activities);
            }
            if (otherExpenseTextView == null) {
                otherExpenseTextView = (TextView) view.findViewById(R.id.tv_expense_other);
            }
            if (expenseTextView == null) {
                expenseTextView = (TextView) view.findViewById(R.id.tv_expense);
            }

            hideAllTextViews(view);

            String expenseString = String.format(Locale.getDefault(), "%,d", expenseValues[0]) + " " + MainActivity.defaultCurrency;
            expenseTextView.setText(expenseString);


            if (expenseValues[0] == 0) {
                view.findViewById(R.id.emptyExpense).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.emptyExpense).setVisibility(View.GONE);
            }

            if (expenseValues[1] != 0) {
                view.findViewById(R.id.taxes_layout).setVisibility(View.VISIBLE);
                taxesTextView.setText(String.format(Locale.getDefault(), "%,d", expenseValues[1]));
            }
            if (expenseValues[2] != 0) {
                view.findViewById(R.id.mortgage_layout).setVisibility(View.VISIBLE);
                mortgageTextView.setText(String.format(Locale.getDefault(), "%,d", expenseValues[2]));
            }
            if (expenseValues[3] != 0) {
                view.findViewById(R.id.credit_card_layout).setVisibility(View.VISIBLE);
                creditCardTextView.setText(String.format(Locale.getDefault(), "%,d", expenseValues[3]));
            }
            if (expenseValues[4] != 0) {
                view.findViewById(R.id.utilities_layout).setVisibility(View.VISIBLE);
                utilitiesTextView.setText(String.format(Locale.getDefault(), "%,d", expenseValues[4]));
            }
            if (expenseValues[5] != 0) {
                view.findViewById(R.id.food_layout).setVisibility(View.VISIBLE);
                foodTextView.setText(String.format(Locale.getDefault(), "%,d", expenseValues[5]));
            }
            if (expenseValues[6] != 0) {
                view.findViewById(R.id.car_payment_layout).setVisibility(View.VISIBLE);
                carPaymentTextView.setText(String.format(Locale.getDefault(), "%,d", expenseValues[6]));
            }
            if (expenseValues[7] != 0) {
                view.findViewById(R.id.personal_layout).setVisibility(View.VISIBLE);
                personalTextView.setText(String.format(Locale.getDefault(), "%,d", expenseValues[7]));
            }
            if (expenseValues[8] != 0) {
                view.findViewById(R.id.activities_layout).setVisibility(View.VISIBLE);
                activitiesTextView.setText(String.format(Locale.getDefault(), "%,d", expenseValues[8]));
            }
            if (expenseValues[9] != 0) {
                view.findViewById(R.id.other_expenses_layout).setVisibility(View.VISIBLE);
                otherExpenseTextView.setText(String.format(Locale.getDefault(), "%,d", expenseValues[9]));
            }


        }

        /**
         * Hides all category views
         */
        private void hideAllTextViews(View view) {

            view.findViewById(R.id.taxes_layout).setVisibility(View.GONE);
            view.findViewById(R.id.mortgage_layout).setVisibility(View.GONE);
            view.findViewById(R.id.credit_card_layout).setVisibility(View.GONE);
            view.findViewById(R.id.utilities_layout).setVisibility(View.GONE);
            view.findViewById(R.id.food_layout).setVisibility(View.GONE);
            view.findViewById(R.id.car_payment_layout).setVisibility(View.GONE);
            view.findViewById(R.id.personal_layout).setVisibility(View.GONE);
            view.findViewById(R.id.activities_layout).setVisibility(View.GONE);
            view.findViewById(R.id.other_expenses_layout).setVisibility(View.GONE);
        }
    }
}
