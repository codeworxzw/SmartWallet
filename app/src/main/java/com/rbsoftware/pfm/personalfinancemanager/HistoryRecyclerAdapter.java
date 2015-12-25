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

/**
 * Created by burzakovskiy on 12/24/2015.
 */
public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.HistoryViewHolder> {


    private List<FinanceDocument> documentList;
    private Context mContext;
    private int mCardHeight;

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
        holder.mDate.setText(doc.getNormalDate());
        String income = "+"+doc.getTotalIncome();
        String expense = "-"+doc.getTotalExpense();
        holder.mIncome.setText(income);
        holder.mExpense.setText(expense);

        holder.mCard.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                holder.mCard.getViewTreeObserver().removeOnPreDrawListener(this);
                mCardHeight = (int) mContext.getResources().getDimension(R.dimen
                        .history_card_max_height);
                ViewGroup.LayoutParams layoutParams = holder.mCard.getLayoutParams();

                layoutParams.height = (int) mContext.getResources().getDimension(R.dimen
                        .history_card_min_height);
                holder.mCard.setLayoutParams(layoutParams);
                return true;
            }

        });
        holder.mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleHeight(holder.mCard);
            }
        });

    }

    private void toggleHeight(final CardView mCard){
        int descriptionViewMinHeight = (int) mContext.getResources().getDimension(R.dimen
                .history_card_min_height);
        if (mCard.getHeight() == descriptionViewMinHeight) {
            // expand
            ValueAnimator anim = ValueAnimator.ofInt(mCard.getMeasuredHeightAndState(),
                    mCardHeight);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = mCard.getLayoutParams();
                    layoutParams.height = val;
                    mCard.setLayoutParams(layoutParams);
                }
            });
            anim.start();
        } else {
            // collapse
            ValueAnimator anim = ValueAnimator.ofInt(mCard.getMeasuredHeightAndState(),
                    descriptionViewMinHeight);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = mCard.getLayoutParams();
                    layoutParams.height = val;
                    mCard.setLayoutParams(layoutParams);
                }
            });
            anim.start();
        }
    }
    @Override
    public int getItemCount() {
        return (null != documentList ? documentList.size() : 0);
    }


    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        protected TextView mDate;
        protected TextView mIncome;
        protected TextView mExpense;
        private CardView mCard;

        public HistoryViewHolder(View view) {
            super(view);
            this.mDate = (TextView) view.findViewById(R.id.textViewDate);
            this.mIncome = (TextView) view.findViewById(R.id.textViewIncome);
            this.mExpense = (TextView) view.findViewById(R.id.textViewExpense);
            this.mCard = (CardView) view.findViewById(R.id.cardViewHistory);
        }





    }
}
