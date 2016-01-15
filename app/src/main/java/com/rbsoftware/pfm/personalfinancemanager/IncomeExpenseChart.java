package com.rbsoftware.pfm.personalfinancemanager;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;


/**
 * A simple {@link Fragment} subclass.
 */
public class IncomeExpenseChart extends Fragment {
    private RelativeLayout relativeLayout;
    private List<FinanceDocument> financeDocumentList;
    private PieChartView mPieChart;
    private PieChartData data;
    private String selectedItem; //position of selected item in popup menu
    private ToggleButton mIncomeExpenseButton;
    private TextView mTextViewPeriod;
    private int offsetStart;
    private int offsetEnd;

    public IncomeExpenseChart() {
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

        return inflater.inflate(R.layout.fragment_income_expense_chart, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.incomeExpenseLayout);

        mTextViewPeriod = (TextView) getActivity().findViewById(R.id.tv_period);
        mIncomeExpenseButton = (ToggleButton) getActivity().findViewById(R.id.btn_income_expense);


            mIncomeExpenseButton.setChecked(Boolean.valueOf(MainActivity.ReadFromSharedPreferences(getActivity(), "toggleButtonState", "true")));
            if(mIncomeExpenseButton.isChecked()){
                mIncomeExpenseButton.setTextColor(ContextCompat.getColor(getContext(),R.color.income));
                offsetStart=0;
                offsetEnd=9;
            }
            else{

                mIncomeExpenseButton.setTextColor(ContextCompat.getColor(getContext(), R.color.expense));
                offsetStart=5;
                offsetEnd=0;
            }

        mIncomeExpenseButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mIncomeExpenseButton.setTextColor(ContextCompat.getColor(getContext(), R.color.income));
                    offsetStart=0;
                    offsetEnd=9;
                }
                else{
                    mIncomeExpenseButton.setTextColor(ContextCompat.getColor(getContext(), R.color.expense));
                    offsetStart=5;
                    offsetEnd=0;

                }
                MainActivity.SaveToSharedPreferences(getActivity(),"toggleButtonState", Boolean.toString(mIncomeExpenseButton.isChecked()));
                generateChartData(getValues(financeDocumentList));
            }
        });


        mPieChart = (PieChartView) getActivity().findViewById(R.id.pie_chart);
        mPieChart.setOnValueTouchListener(new ValueTouchListener());


    }

    @Override
    public void onResume() {
        super.onResume();
        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate(MainActivity.ReadFromSharedPreferences(getActivity(), "period", "thisWeek"), MainActivity.getUserId());
        mTextViewPeriod.setText(MainActivity.ReadFromSharedPreferences(getActivity(), "periodText", getResources().getString(R.string.this_week)));
        generateChartData(getValues(financeDocumentList));
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chart_income_expense_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_filter:
                showPopup();
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
    //Shows chart_income_expense_menu popup menu
    public void showPopup(){
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
                        selectedItem = "thisWeek";
                        mTextViewPeriod.setText(getResources().getString(R.string.this_week));
                        break;
                    case R.id.thisMonth:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate("thisMonth", MainActivity.getUserId());
                        selectedItem = "thisMonth";
                        mTextViewPeriod.setText(getResources().getString(R.string.this_month));

                        break;
                    case R.id.lastWeek:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate("lastWeek", MainActivity.getUserId());
                        selectedItem = "lastWeek";
                        mTextViewPeriod.setText(getResources().getString(R.string.last_week));

                        break;
                    case R.id.lastMonth:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate("lastMonth", MainActivity.getUserId());
                        selectedItem = "lastMonth";
                        mTextViewPeriod.setText(getResources().getString(R.string.last_month));

                        break;
                    case R.id.thisYear:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate("thisYear", MainActivity.getUserId());
                        selectedItem = "thisYear";
                        mTextViewPeriod.setText(getResources().getString(R.string.this_year));

                        break;
                }
                MainActivity.SaveToSharedPreferences(getActivity(), "period", selectedItem);
                MainActivity.SaveToSharedPreferences(getActivity(), "periodText", mTextViewPeriod.getText().toString());

                generateChartData(getValues(financeDocumentList));
                return false;
            }
        });
        popup.show();

    }
    //fills mPieChart with data
    private void generateChartData(HashMap<Integer, Integer> mapSum) {
        List<SliceValue> values = new ArrayList<SliceValue>();
        int total=0;
        for (int i = 1+offsetStart; i <= (mapSum.size()-offsetEnd); ++i) {
            int value=mapSum.get(i);
            total +=value;
            if(value !=0) {
                //SliceValue sliceValue = new SliceValue(value, Color.rgb(150 - i * 4, 90 + i * 8, 200 - i*5));
                SliceValue sliceValue = new SliceValue(value, getColorPalette(i));
                sliceValue.setLabel(keyToString(i)+" "+ value);
                values.add(sliceValue);
            }
        }
        data = new PieChartData(values);
        data.setHasLabels(true);
        //data.setHasLabelsOnlyForSelected(true);
        data.setHasLabelsOutside(false);
        data.setHasCenterCircle(true);
        data.setCenterText1(Integer.toString(total));
        data.setCenterText2(MainActivity.defaultCurrency);
        if(mIncomeExpenseButton.isChecked()) {
            data.setCenterCircleColor(getResources().getColor(R.color.income));
        }
        else {
            data.setCenterCircleColor(getResources().getColor(R.color.expense));
        }
        mPieChart.setPieChartData(data);

    }





    // extracts sums data of FinanceDocuments in the list
    public HashMap<Integer,Integer> getValues(List<FinanceDocument> list){
        int salarySum =0;
        int rentalIncomeSum =0;
        int interestSum =0;
        int giftsSum =0;
        int otherIncomeSum =0;
        int taxesSum =0;
        int mortgageSum =0;
        int creditCardSum =0;
        int utilitiesSum =0;
        int foodSum =0;
        int carPaymentSum =0;
        int personalSum =0;
        int activitiesSum =0;
        int otherExpensesSum =0;


        for(FinanceDocument item : list){
            salarySum += Integer.valueOf(item.getSalary());
            rentalIncomeSum += Integer.valueOf(item.getRentalIncome());
            interestSum +=Integer.valueOf(item.getInterest());
            giftsSum +=Integer.valueOf(item.getGifts());
            otherIncomeSum +=Integer.valueOf(item.getOtherIncome());
            taxesSum += Integer.valueOf(item.getTaxes());
            mortgageSum += Integer.valueOf(item.getMortgage());
            creditCardSum += Integer.valueOf(item.getCreditCard());
            utilitiesSum += Integer.valueOf(item.getUtilities());
            foodSum +=Integer.valueOf(item.getFood());
            carPaymentSum += Integer.valueOf(item.getCarPayment());
            personalSum += Integer.valueOf(item.getPersonal());
            activitiesSum += Integer.valueOf(item.getActivities());
            otherExpensesSum += Integer.valueOf(item.getOtherExpenses());

        }
        HashMap<Integer, Integer> mapSum=new HashMap<>();
        mapSum.put(MainActivity.PARAM_SALARY, salarySum);
        mapSum.put(MainActivity.PARAM_RENTAL_INCOME, rentalIncomeSum);
        mapSum.put(MainActivity.PARAM_INTEREST, interestSum);
        mapSum.put(MainActivity.PARAM_GIFTS, giftsSum);
        mapSum.put(MainActivity.PARAM_OTHER_INCOME, otherIncomeSum);
        mapSum.put(MainActivity.PARAM_TAXES, taxesSum);
        mapSum.put(MainActivity.PARAM_MORTGAGE, mortgageSum);
        mapSum.put(MainActivity.PARAM_CREDIT_CARD, creditCardSum);
        mapSum.put(MainActivity.PARAM_UTILITIES, utilitiesSum);
        mapSum.put(MainActivity.PARAM_FOOD, foodSum);
        mapSum.put(MainActivity.PARAM_CAR_PAYMENT, carPaymentSum);
        mapSum.put(MainActivity.PARAM_PERSONAL, personalSum);
        mapSum.put(MainActivity.PARAM_ACTIVITIES, activitiesSum);
        mapSum.put(MainActivity.PARAM_OTHER_EXPENSE, otherExpensesSum);

        return mapSum;
    }

    /* Converts int key to human readable string
    * @param key value range 1-14
    * @return string value
    */
    public String keyToString(int key){
        switch (key){
            case 1: return getResources().getString(R.string.salary);
            case 2: return getResources().getString(R.string.rental_income);
            case 3: return getResources().getString(R.string.interest);
            case 4: return getResources().getString(R.string.gifts);
            case 5: return getResources().getString(R.string.other_income);
            case 6: return getResources().getString(R.string.taxes);
            case 7: return getResources().getString(R.string.mortgage);
            case 8: return getResources().getString(R.string.credit_card);
            case 9: return getResources().getString(R.string.utilities);
            case 10: return getResources().getString(R.string.food);
            case 11: return getResources().getString(R.string.car_payment);
            case 12: return getResources().getString(R.string.personal);
            case 13: return getResources().getString(R.string.activities);
            case 14: return getResources().getString(R.string.other_expense);
        }
        return "";
    }

    private int getColorPalette(int i){
        switch (i){
            case 1: return ContextCompat.getColor(getContext(),R.color.salary);
            case 2: return ContextCompat.getColor(getContext(), R.color.rental_income);
            case 3: return ContextCompat.getColor(getContext(), R.color.interest);
            case 4: return ContextCompat.getColor(getContext(), R.color.gifts);
            case 5: return ContextCompat.getColor(getContext(), R.color.other_income);
            case 6: return ContextCompat.getColor(getContext(), R.color.taxes);
            case 7: return ContextCompat.getColor(getContext(), R.color.mortgage);
            case 8: return ContextCompat.getColor(getContext(), R.color.credit_card);
            case 9: return ContextCompat.getColor(getContext(), R.color.utilities);
            case 10: return ContextCompat.getColor(getContext(),R.color.food);
            case 11: return ContextCompat.getColor(getContext(),R.color.car_payment);
            case 12: return ContextCompat.getColor(getContext(),R.color.personal);
            case 13: return ContextCompat.getColor(getContext(),R.color.activities);
            case 14: return ContextCompat.getColor(getContext(),R.color.other_expense);
            default: return Color.WHITE;

        }

    }
    private class ValueTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
           // Toast.makeText(getActivity(), value.getLabelAsChars() + " " + value.getValue(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }
    }
}
