package com.rbsoftware.pfm.personalfinancemanager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Holds method for balance card
 *
 * @author Roman Burzakovskiy
 */
public class BalanceCard extends Card {
    private Context mContext;
    private final String value;

    public BalanceCard(Context context, String value) {
        super(context);
        this.mContext = context;
        this.value = value;
        BalanceHeaderInnerCard header = new BalanceHeaderInnerCard(mContext);
        this.addCardHeader(header);
    }

    public String getBalanceValue() {
        return ((BalanceHeaderInnerCard) this.getCardHeader()).getTextViewValue().getText().toString();
    }

    public class BalanceHeaderInnerCard extends CardHeader {
        private TextView textViewValue;

        public BalanceHeaderInnerCard(Context context) {
            super(context, R.layout.account_summary_balance_card_layout);
        }

        public TextView getTextViewValue() {
            return textViewValue;
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            super.setupInnerViewElements(parent, view);
            textViewValue = (TextView) view.findViewById(R.id.textViewBalance);
            if (textViewValue != null) {
                textViewValue.setText(value);
            }
        }
    }
}
