package com.rbsoftware.pfm.personalfinancemanager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.view.LineChartView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrendsChart extends Fragment {
    private List<FinanceDocument> financeDocumentList;
    private String selectedPeriod; //position of selected item in popup menu
    private LineChartView chart;
    private LineChartData data;
    private TextView mTextViewPeriod;
    PopupMenu popupLine;
    public TrendsChart() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trends_chart, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTextViewPeriod = (TextView) getActivity().findViewById(R.id.tv_period_trend);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.trends, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onResume() {
        super.onResume();
        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate(MainActivity.ReadFromSharedPreferences(getActivity(), "periodTrend", "thisWeek"), MainActivity.getUserId());
        mTextViewPeriod.setText(MainActivity.ReadFromSharedPreferences(getActivity(), "periodTextTrend", getResources().getString(R.string.this_week)));
       // generateChartData(getValues(financeDocumentList));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_filter:
                showPopupPeriod();
                return true;
            case R.id.action_line:
                showPopupLine();
                return true;

            default:return super.onOptionsItemSelected(item);
        }

    }



    //Helper methods
    //Shows period filter popup menu
    public void showPopupPeriod(){
        View menuItemView = getActivity().findViewById(R.id.action_filter);
        PopupMenu popup = new PopupMenu(getActivity(), menuItemView);
        MenuInflater inflate = popup.getMenuInflater();
        inflate.inflate(R.menu.period, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();

                switch (id) {
                    case R.id.thisWeek:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate("thisWeek", MainActivity.getUserId());
                        selectedPeriod = "thisWeek";
                        mTextViewPeriod.setText(getResources().getString(R.string.this_week));
                        break;
                    case R.id.thisMonth:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate("thisMonth", MainActivity.getUserId());
                        selectedPeriod = "thisMonth";
                        mTextViewPeriod.setText(getResources().getString(R.string.this_month));

                        break;
                    case R.id.lastWeek:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate("lastWeek", MainActivity.getUserId());
                        selectedPeriod = "lastWeek";
                        mTextViewPeriod.setText(getResources().getString(R.string.last_week));

                        break;
                    case R.id.lastMonth:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate("lastMonth", MainActivity.getUserId());
                        selectedPeriod = "lastMonth";
                        mTextViewPeriod.setText(getResources().getString(R.string.last_month));

                        break;
                    case R.id.thisYear:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate("thisYear", MainActivity.getUserId());
                        selectedPeriod = "thisYear";
                        mTextViewPeriod.setText(getResources().getString(R.string.this_year));

                        break;
                }
                MainActivity.SaveToSharedPreferences(getActivity(), "periodTrend", selectedPeriod);
                MainActivity.SaveToSharedPreferences(getActivity(), "periodTextTrend", mTextViewPeriod.getText().toString());

                //generateChartData(getValues(financeDocumentList));
                return false;
            }
        });
        popup.show();

    }
    public void showPopupLine(){
        if(popupLine == null) { //Checking if PopupMenu object was created
            popupLine = new PopupMenu(getActivity(), getActivity().findViewById(R.id.action_line));
        }
            MenuInflater inflate = popupLine.getMenuInflater();
            inflate.inflate(R.menu.lines, popupLine.getMenu());

        popupLine.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                item.setChecked(!item.isChecked());
                switch (item.getItemId()) {
                    case R.id.popupTotalIncome:

                        return true;
                }

                return false;
            }
        });
        popupLine.show();

    }

}
