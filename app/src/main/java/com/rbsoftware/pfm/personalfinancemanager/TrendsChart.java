package com.rbsoftware.pfm.personalfinancemanager;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrendsChart extends Fragment {
    private RelativeLayout relativeLayout;
    private List<FinanceDocument> financeDocumentList;
    private String selectedPeriod; //position of selected item in popup menu
    private LineChartView chart;
    private LineChartData data;
    private TextView mTextViewPeriod;
    private List<String> checkedLines; //list of checked lines Ids
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
            checkedLines.add(MainActivity.ReadFromSharedPreferences(getActivity(),"checkedLine"+i,null));

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
        relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.trendsChartLayout);
        mTextViewPeriod = (TextView) getActivity().findViewById(R.id.tv_period_trend);
        chart = (LineChartView) getActivity().findViewById(R.id.trends_chart);


    }

    @Override
    public void onResume() {
        super.onResume();
        //Querying list of documents on fragment resume
        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate(MainActivity.ReadFromSharedPreferences(getActivity(), "periodTrend", "thisWeek"), MainActivity.getUserId(), FinanceDocumentModel.ORDER_ASC);
        mTextViewPeriod.setText(MainActivity.ReadFromSharedPreferences(getActivity(), "periodTextTrend", getResources().getString(R.string.this_week)));
        //generateChartData(getValues(financeDocumentList));
        generateLineChartData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chart_trends_menu, menu);

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
                return true;
            case R.id.document_share:
                try {
                    ExportData.exportChartAsPng(getContext(),relativeLayout);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }



    //Helper methods
    //Shows period chart_trends_menu popup menu
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
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate("thisWeek", MainActivity.getUserId(), FinanceDocumentModel.ORDER_ASC);
                        selectedPeriod = "thisWeek";
                        mTextViewPeriod.setText(getResources().getString(R.string.this_week));
                        break;
                    case R.id.thisMonth:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate("thisMonth", MainActivity.getUserId(), FinanceDocumentModel.ORDER_ASC);
                        selectedPeriod = "thisMonth";
                        mTextViewPeriod.setText(getResources().getString(R.string.this_month));

                        break;
                    case R.id.lastWeek:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate("lastWeek", MainActivity.getUserId(), FinanceDocumentModel.ORDER_ASC);
                        selectedPeriod = "lastWeek";
                        mTextViewPeriod.setText(getResources().getString(R.string.last_week));

                        break;
                    case R.id.lastMonth:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate("lastMonth", MainActivity.getUserId(), FinanceDocumentModel.ORDER_ASC);
                        selectedPeriod = "lastMonth";
                        mTextViewPeriod.setText(getResources().getString(R.string.last_month));

                        break;
                    case R.id.thisYear:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate("thisYear", MainActivity.getUserId(), FinanceDocumentModel.ORDER_ASC);
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
                    MenuItem item = popupLine.getMenu().findItem(findMenuItemByTitle(checkedLines.get(i)));
                    item.setChecked(true);
                }
            }
        popupLine.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                item.setChecked(!item.isChecked());
                if (item.isChecked()) {
                    checkedLines.add(item.getTitle().toString());
                } else {
                    int counter = 0;
                    while (counter < checkedLines.size()) {
                        if (checkedLines.get(counter).equals(item.getTitle())) {
                            checkedLines.remove(counter);
                        } else {
                            counter++;
                        }
                    }

                }
                listLinesSize = checkedLines.size();
                MainActivity.SaveToSharedPreferences(getActivity(), "listLinesSize", Integer.toString(listLinesSize));
                for (int i = 0; i < checkedLines.size(); i++) {
                    MainActivity.SaveToSharedPreferences(getActivity(), "checkedLine" + i, checkedLines.get(i));
                }

                generateLineChartData();

                return true;
            }
        });
        popupLine.show();

    }

    //Fills LineChart with data
    private void generateLineChartData() {
        List<AxisValue> axisValues =new ArrayList<AxisValue>();
        List<Line> lines = new ArrayList<Line>();
        for (int i = 0; i < checkedLines.size(); ++i) {
            List<String[]> docData = getDataFromDocument(findMenuItemByTitle(checkedLines.get(i)), financeDocumentList);
            List<PointValue> values = new ArrayList<PointValue>();
            axisValues.clear();
            for (int j = 0; j < docData.size(); ++j) {
                values.add(new PointValue(j, Integer.valueOf(docData.get(j)[0])));
                axisValues.add(new AxisValue(j).setLabel(docData.get(j)[1]));
            }

            Line line = new Line(values);
            line.setColor(getColorPalette
                    (findMenuItemByTitle(checkedLines.get(i))));
            line.setShape(ValueShape.CIRCLE);

            line.setFilled(true);
            line.setHasLabels(true);
            line.setHasLines(true);
            line.setHasPoints(true);

            lines.add(line);
        }


        data = new LineChartData(lines);


            Axis axisX = new Axis(axisValues);
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
            dates.add(value);
        }
        return dates;
    }

    public List<String[]> getDataFromDocument(int lineId, List<FinanceDocument> docList){
        int value;
        List<String[]> data = new ArrayList<>();
        switch (lineId){
            case R.id.popupTotalIncome:
                for(FinanceDocument doc : docList){
                    data.add(new String[]{
                            Integer.toString(Integer.valueOf(doc.getSalary()) +
                                Integer.valueOf(doc.getRentalIncome()) +
                                Integer.valueOf(doc.getInterest()) +
                                Integer.valueOf(doc.getGifts()) +
                                Integer.valueOf(doc.getOtherIncome())),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)});

                }
                break;
            case R.id.popupTotalExpense:
                for(FinanceDocument doc : docList){
                    data.add(new String[]{
                            Integer.toString(Integer.valueOf(doc.getTaxes())+
                                    Integer.valueOf(doc.getMortgage())+
                                    Integer.valueOf(doc.getCreditCard())+
                                    Integer.valueOf(doc.getUtilities())+
                                    Integer.valueOf(doc.getFood())+
                                    Integer.valueOf(doc.getCarPayment())+
                                    Integer.valueOf(doc.getPersonal())+
                                    Integer.valueOf(doc.getActivities())+
                                    Integer.valueOf(doc.getOtherExpenses())),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupSalary:
                for(FinanceDocument doc : docList){
                    value =Integer.valueOf(doc.getSalary());
                    if(value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupRentalIncome:
                for(FinanceDocument doc : docList){
                    value = Integer.valueOf(doc.getRentalIncome());
                    if(value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupInterest:
                for(FinanceDocument doc : docList){
                    value = Integer.valueOf(doc.getInterest());
                    if(value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupGifts:
                for(FinanceDocument doc : docList){
                    value = Integer.valueOf(doc.getGifts());
                    if(value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupOtherIncome:
                for(FinanceDocument doc : docList){
                    value = Integer.valueOf(doc.getOtherIncome());
                    if(value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupTaxes:
                for(FinanceDocument doc : docList){
                    value = Integer.valueOf(doc.getTaxes());
                    if(value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupMortgage:
                for(FinanceDocument doc : docList){
                    value = Integer.valueOf(doc.getMortgage());
                    if(value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupCreditCard:
                for(FinanceDocument doc : docList){
                    value = Integer.valueOf(doc.getCreditCard());
                    if(value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupUtilities:
                for(FinanceDocument doc : docList){
                    value = Integer.valueOf(doc.getUtilities());
                    if(value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupFood:
                for(FinanceDocument doc : docList){
                    value = Integer.valueOf(doc.getFood());
                    if(value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupCarPayment:
                for(FinanceDocument doc : docList){
                    value = Integer.valueOf(doc.getCarPayment());
                    if(value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupPersonal:
                for(FinanceDocument doc : docList){
                    value = Integer.valueOf(doc.getPersonal());
                    if(value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupActivities:
                for(FinanceDocument doc : docList){
                    value = Integer.valueOf(doc.getActivities());
                    if(value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupOtherExpense:
                for(FinanceDocument doc : docList){
                    value = Integer.valueOf(doc.getOtherExpenses());
                    if(value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;

        }
        return data;
    }


    /* Converts menu item title into id
        @param title of menu item
        @return resource id
     */
    private int findMenuItemByTitle(String title){
         if(title.equals(getString(R.string.income))) return R.id.popupTotalIncome;
         if(title.equals(getString(R.string.expense))) return R.id.popupTotalExpense;

         if(title.equals(getString(R.string.salary))) return R.id.popupSalary;
         if(title.equals(getString(R.string.rental_income))) return R.id.popupRentalIncome;
         if(title.equals(getString(R.string.interest))) return R.id.popupInterest;
         if(title.equals(getString(R.string.gifts))) return R.id.popupGifts;
         if(title.equals(getString(R.string.other_income))) return R.id.popupOtherIncome;

         if(title.equals(getString(R.string.taxes))) return R.id.popupTaxes;
         if(title.equals(getString(R.string.mortgage))) return R.id.popupMortgage;
         if(title.equals(getString(R.string.credit_card))) return R.id.popupCreditCard;
         if(title.equals(getString(R.string.utilities))) return R.id.popupUtilities;
         if(title.equals(getString(R.string.food))) return R.id.popupFood;
         if(title.equals(getString(R.string.car_payment))) return R.id.popupCarPayment;
         if(title.equals(getString(R.string.personal))) return R.id.popupPersonal;
         if(title.equals(getString(R.string.activities))) return R.id.popupActivities;
         if(title.equals(getString(R.string.other_expense))) return R.id.popupOtherExpense;



        return  0;
    }

    private int getColorPalette(int i){
        switch (i){
            case R.id.popupTotalIncome: return ContextCompat.getColor(getContext(), R.color.income);
            case R.id.popupTotalExpense: return ContextCompat.getColor(getContext(), R.color.expense);
            case R.id.popupSalary: return ContextCompat.getColor(getContext(), R.color.salary);
            case R.id.popupRentalIncome: return ContextCompat.getColor(getContext(), R.color.rental_income);
            case R.id.popupInterest: return ContextCompat.getColor(getContext(), R.color.interest);
            case R.id.popupGifts: return ContextCompat.getColor(getContext(), R.color.gifts);
            case R.id.popupOtherIncome: return ContextCompat.getColor(getContext(), R.color.other_income);
            case R.id.popupTaxes: return ContextCompat.getColor(getContext(), R.color.taxes);
            case R.id.popupMortgage: ContextCompat.getColor(getContext(),R.color.mortgage);
            case R.id.popupCreditCard: return ContextCompat.getColor(getContext(), R.color.credit_card);
            case R.id.popupUtilities: return ContextCompat.getColor(getContext(), R.color.utilities);
            case R.id.popupFood: return ContextCompat.getColor(getContext(), R.color.food);
            case R.id.popupCarPayment: return ContextCompat.getColor(getContext(), R.color.car_payment);
            case R.id.popupPersonal: return ContextCompat.getColor(getContext(), R.color.personal);
            case R.id.popupActivities: return ContextCompat.getColor(getContext(), R.color.activities);
            case R.id.popupOtherExpense: return ContextCompat.getColor(getContext(), R.color.other_expense);
            default: return Color.WHITE;

        }
    }
}
