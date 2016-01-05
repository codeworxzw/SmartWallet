package com.rbsoftware.pfm.personalfinancemanager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class History extends Fragment {
    private RecyclerView mRecyclerView;
    private HistoryRecyclerAdapter mAdapter;
    private List<FinanceDocument> docList;
    public History() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getResources().getStringArray(R.array.drawer_menu)[2]);
        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.history_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));



    }

    @Override
    public void onResume() {
        super.onResume();
        docList = MainActivity.financeDocumentModel.queryDocumentsByDate("thisYear", MainActivity.getUserId(), FinanceDocumentModel.ORDER_DESC);
        mAdapter = new HistoryRecyclerAdapter(getActivity(), docList);
        mRecyclerView.setAdapter(mAdapter);
    }
}
