package com.rbsoftware.pfm.personalfinancemanager;


import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
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
    private List<Integer> checkedLines; //list of checked lines Ids
    private  int listLinesSize; //size of checked lines list
    PopupMenu popupLine;
    public TrendsChart() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //Populating list from shared preferences
        listLinesSize = Integer.valueOf(MainActivity.ReadFromSharedPreferences(getActivity(), "listLinesSize", "0"));
        checkedLines= new ArrayList<>();
        for(int i=0; i<listLinesSize; i++){
            checkedLines.add(Integer.valueOf(MainActivity.ReadFromSharedPreferences(getActivity(),"checkedLine"+i,null)));

        }
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
        chart = (LineChartView) getActivity().findViewById(R.id.trends_chart);


    }

    @Override
    public void onResume() {
        super.onResume();
        //Querying list of documents on fragment resume
        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate(MainActivity.ReadFromSharedPreferences(getActivity(), "periodTrend", "thisWeek"), MainActivity.getUserId());
        mTextViewPeriod.setText(MainActivity.ReadFromSharedPreferences(getActivity(), "periodTextTrend", getResources().getString(R.string.this_week)));
        //generateChartData(getValues(financeDocumentList));
        generateLineChartData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.trends, menu);

        super.onCreateOptionsMenu(menu, inflater);
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
                return false;

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
                generateLineChartData();
                return false;
            }
        });
        popup.show();

    }
    public void showPopupLine(){

            popupLine = new PopupMenu(getActivity(), getActivity().findViewById(R.id.action_line));
            MenuInflater inflate = popupLine.getMenuInflater();
            inflate.inflate(R.menu.lines, popupLine.getMenu());
            if(listLinesSize != 0) {
                for (int i = 0; i < listLinesSize; i++) {
                    MenuItem item = popupLine.getMenu().findItem(checkedLines.get(i));
                    item.setChecked(true);
                }
            }
        popupLine.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                item.setChecked(!item.isChecked());
                if (item.isChecked()) {
                    checkedLines.add(item.getItemId());
                } else {
                    int counter = 0;
                    while (counter < checkedLines.size()) {
                        if (checkedLines.get(counter) == item.getItemId()) {
                            checkedLines.remove(counter);
                        } else {
                            counter++;
                        }
                    }

                }
                listLinesSize = checkedLines.size();
                MainActivity.SaveToSharedPreferences(getActivity(), "listLinesSize", Integer.toString(listLinesSize));
                for (int i = 0; i < checkedLines.size(); i++) {
                    MainActivity.SaveToSharedPreferences(getActivity(), "checkedLine" + i, Integer.toString(checkedLines.get(i)));
                }

                generateLineChartData();

                return true;
            }
        });
        popupLine.show();

    }

    //Fills LineChart with data
    private void generateLineChartData() {

        List<Line> lines = new ArrayList<Line>();
        for (int i = 0; i < checkedLines.size(); ++i) {
            List<Integer> docData = getDataFromDocument(checkedLines.get(i), financeDocumentList);
            List<PointValue> values = new ArrayList<PointValue>();
            for (int j = 0; j < docData.size(); ++j) {
                values.add(new PointValue(j, docData.get(j)));
            }

            Line line = new Line(values);
            line.setColor(Color.rgb(150 - i * 4, 90 + i * 8, 200 - i * 5));
            line.setShape(ValueShape.CIRCLE);

            line.setFilled(true);
            line.setHasLabels(true);
            line.setHasLines(true);
            line.setHasPoints(true);

            lines.add(line);
        }


        data = new LineChartData(lines);


            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
              //  axisX.setHasTiltedLabels(true);
              //  axisX.setValues(getDates(financeDocumentList));
                axisX.setName(getResources().getString(R.string.period));
                axisY.setName(getResources().getString(R.string.value));

            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);


        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart.setLineChartData(data);

    }

    public List<AxisValue> getDates(List<FinanceDocument> docList){
        List<AxisValue> dates = new ArrayList<>();
        for(FinanceDocument doc : docList){
            AxisValue value = new AxisValue(Float.valueOf(doc.getDate()));
            Log.d("Charts data", value.getValue()+"");
            dates.add(value);
        }
        Log.d("Charts data", dates.toString());
        return dates;
    }

    public List<Integer> getDataFromDocument(int lineId, List<FinanceDocument> docList){
        List<Integer> data = new ArrayList<>();
        switch (lineId){
            case R.id.popupTotalIncome:
                for(FinanceDocument doc : docList){
                    data.add(Integer.valueOf(doc.getSalary())+
                            Integer.valueOf(doc.getRentalIncome())+
                            Integer.valueOf(doc.getInterest())+
                            Integer.valueOf(doc.getGifts())+
                            Integer.valueOf(doc.getOtherIncome()));

                }
                break;
            case R.id.popupTotalExpense:
                for(FinanceDocument doc : docList){
                    data.add(Integer.valueOf(doc.getTaxes())+
                            Integer.valueOf(doc.getMortgage())+
                            Integer.valueOf(doc.getCreditCard())+
                            Integer.valueOf(doc.getUtilities())+
                            Integer.valueOf(doc.getFood())+
                            Integer.valueOf(doc.getCarPayment())+
                            Integer.valueOf(doc.getPersonal())+
                            Integer.valueOf(doc.getActivities())+
                            Integer.valueOf(doc.getOtherExpenses()));
                }
                break;
            case R.id.popupSalary:
                for(FinanceDocument doc : docList){
                    data.add(Integer.valueOf(doc.getSalary()));
                }
                break;
            case R.id.popupRentalIncome:
                for(FinanceDocument doc : docList){
                    data.add(Integer.valueOf(doc.getRentalIncome()));
                }
                break;
            case R.id.popupInterest:
                for(FinanceDocument doc : docList){
                    data.add(Integer.valueOf(doc.getInterest()));
                }
                break;
            case R.id.popupGifts:
                for(FinanceDocument doc : docList){
                    data.add(Integer.valueOf(doc.getGifts()));
                }
                break;
            case R.id.popupOtherIncome:
                for(FinanceDocument doc : docList){
                    data.add(Integer.valueOf(doc.getOtherIncome()));
                }
                break;
            case R.id.popupTaxes:
                for(FinanceDocument doc : docList){
                    data.add(Integer.valueOf(doc.getTaxes()));
                }
                break;
            case R.id.popupMortgage:
                for(FinanceDocument doc : docList){
                    data.add(Integer.valueOf(doc.getMortgage()));
                }
                break;
            case R.id.popupCreditCard:
                for(FinanceDocument doc : docList){
                    data.add(Integer.valueOf(doc.getCreditCard()));
                }
                break;
            case R.id.popupUtilities:
                for(FinanceDocument doc : docList){
                    data.add(Integer.valueOf(doc.getUtilities()));
                }
                break;
            case R.id.popupFood:
                for(FinanceDocument doc : docList){
                    data.add(Integer.valueOf(doc.getFood()));
                }
                break;
            case R.id.popupCarPayment:
                for(FinanceDocument doc : docList){
                    data.add(Integer.valueOf(doc.getCarPayment()));
                }
                break;
            case R.id.popupPersonal:
                for(FinanceDocument doc : docList){
                    data.add(Integer.valueOf(doc.getPersonal()));
                }
                break;
            case R.id.popupActivities:
                for(FinanceDocument doc : docList){
                    data.add(Integer.valueOf(doc.getActivities()));
                }
                break;
            case R.id.popupOtherExpense:
                for(FinanceDocument doc : docList){
                    data.add(Integer.valueOf(doc.getOtherExpenses()));
                }
                break;

        }
        return data;
    }
}
