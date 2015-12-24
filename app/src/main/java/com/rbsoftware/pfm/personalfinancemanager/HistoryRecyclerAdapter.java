package com.rbsoftware.pfm.personalfinancemanager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

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
    public void onBindViewHolder(HistoryRecyclerAdapter.HistoryViewHolder holder, int position) {
        FinanceDocument doc = documentList.get(position);
        holder.mDate.setText(doc.getNormalDate());
        String incomeExpense = "+"+doc.getTotalIncome()+" / -"+doc.getTotalExpense();
        holder.mIncomeExpense.setText(incomeExpense);
    }

    @Override
    public int getItemCount() {
        return (null != documentList ? documentList.size() : 0);
    }


    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        protected TextView mDate;
        protected TextView mIncomeExpense;

        public HistoryViewHolder(View view) {
            super(view);
            this.mDate = (TextView) view.findViewById(R.id.textViewDate);
            this.mIncomeExpense = (TextView) view.findViewById(R.id.textViewIncomeExpense);

        }
    }
}
