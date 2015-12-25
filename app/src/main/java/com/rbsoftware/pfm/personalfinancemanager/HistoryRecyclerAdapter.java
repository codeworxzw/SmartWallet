package com.rbsoftware.pfm.personalfinancemanager;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

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
        CardExpand expand = new CardExpand(mContext);

        //Set inner title in Expand Area
        expand.setTitle("dummy text");

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

    public class HistoryHeaderInnerCard extends CardHeader {
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


}
