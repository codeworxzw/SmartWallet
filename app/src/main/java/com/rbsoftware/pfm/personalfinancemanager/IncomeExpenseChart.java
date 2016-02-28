package com.rbsoftware.pfm.personalfinancemanager;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.util.SparseArray;
import android.util.SparseIntArray;
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
import java.util.Locale;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


/**
 * A simple {@link Fragment} subclass.
 * Child class of (@link Charts) class
 * Holds pie chart data
 */
public class IncomeExpenseChart extends Fragment {
    private final String TAG = "IncomeExpenseChart";
    private RelativeLayout relativeLayout;
    private List<FinanceDocument> financeDocumentList;
    private PieChartView mPieChart;
    private String selectedItem; //position of selected item in popup menu
    private ToggleButton mIncomeExpenseButton;
    private TextView mTextViewPeriod;
    private int offsetStart;
    private int offsetEnd;
    private Context mContext;
    private Activity mActivity;

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
        if (relativeLayout == null) {
            relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.incomeExpenseLayout);
        }
        if (mTextViewPeriod == null) {
            mTextViewPeriod = (TextView) getActivity().findViewById(R.id.tv_period);
        }
        if (mIncomeExpenseButton == null) {
            mIncomeExpenseButton = (ToggleButton) getActivity().findViewById(R.id.btn_income_expense);
        }


        mIncomeExpenseButton.setChecked(Boolean.valueOf(MainActivity.readFromSharedPreferences(getActivity(), "toggleButtonState", "true")));
        if (mIncomeExpenseButton.isChecked()) {
            mIncomeExpenseButton.setTextColor(ContextCompat.getColor(getContext(), R.color.income));
            offsetStart = 0;
            offsetEnd = 9;
        } else {

            mIncomeExpenseButton.setTextColor(ContextCompat.getColor(getContext(), R.color.expense));
            offsetStart = 5;
            offsetEnd = 0;
        }

        mIncomeExpenseButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mIncomeExpenseButton.setTextColor(ContextCompat.getColor(getContext(), R.color.income));
                    offsetStart = 0;
                    offsetEnd = 9;
                } else {
                    mIncomeExpenseButton.setTextColor(ContextCompat.getColor(getContext(), R.color.expense));
                    offsetStart = 5;
                    offsetEnd = 0;

                }
                MainActivity.saveToSharedPreferences(getActivity(), "toggleButtonState", Boolean.toString(mIncomeExpenseButton.isChecked()));
                generateChartData(getValues(financeDocumentList));
            }
        });

        if (mPieChart == null) {
            mPieChart = (PieChartView) getActivity().findViewById(R.id.pie_chart);
        }
        mPieChart.setOnValueTouchListener(new ValueTouchListener());

        mContext = getContext();
        mActivity = getActivity();

        int status = mContext.getSharedPreferences("material_showcaseview_prefs", Context.MODE_PRIVATE)
                .getInt("status_" + TAG, 0);
        if (status != -1) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startShowcase();
                }
            }, 1000);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate(MainActivity.readFromSharedPreferences(getActivity(), "period", "thisWeek"), MainActivity.getUserId());
        mTextViewPeriod.setText(MainActivity.readFromSharedPreferences(getActivity(), "periodText", getResources().getString(R.string.this_week)));
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
                    ExportData.exportChartAsPng(getContext(), relativeLayout);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }


    }


    //Helper methods

    /**
     * Shows chart_income_expense_menu popup menu
     */

    private void showPopup() {
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
                MainActivity.saveToSharedPreferences(getActivity(), "period", selectedItem);
                MainActivity.saveToSharedPreferences(getActivity(), "periodText", mTextViewPeriod.getText().toString());

                generateChartData(getValues(financeDocumentList));
                return false;
            }
        });
        popup.show();

    }

    /**
     * fills mPieChart with data
     *
     * @param mapSum hash map of data types and values
     */

    private void generateChartData(SparseIntArray mapSum) {
        List<SliceValue> values = new ArrayList<>();
        int total = 0;
        int size= mapSum.size();
        for (int i = 1 + offsetStart; i <= (size - offsetEnd); ++i) {
            total += mapSum.get(i);
        }
        if (total != 0) {
            mPieChart.setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.emptyIncomeExpense).setVisibility(View.GONE);
            int j=0;
            for (int i = 1 + offsetStart; i <= (size - offsetEnd); ++i) {

                if (mapSum.get(i) != 0) {
                    int value = (mapSum.get(i) * 100) / total;
                    SliceValue sliceValue = new SliceValue(1, Utils.getPieColorPalette(mContext, i));
                    sliceValue.setLabel(Utils.keyToString(mContext, i) + " " + String.format(Locale.getDefault(), "%,d", value) + "%");
                    values.add(sliceValue);

                    values.get(j).setTarget(value);
                    j++;
                }
            }
            PieChartData data = new PieChartData(values);
            data.setHasLabels(true);
            data.setHasLabelsOutside(false);
            data.setHasCenterCircle(true);
            data.setCenterText1(String.format(Locale.getDefault(), "%,d", total));
            data.setCenterText2(MainActivity.defaultCurrency);
            mPieChart.setPieChartData(data);
            mPieChart.startDataAnimation();
        }
        else{
            mPieChart.setVisibility(View.GONE);
            getActivity().findViewById(R.id.emptyIncomeExpense).setVisibility(View.VISIBLE);
        }


    }


    /**
     * extracts sums data of FinanceDocuments in the list
     *
     * @param list finance documents list
     * @return map of data types and values
     */
    private SparseIntArray getValues(List<FinanceDocument> list) {
        int salarySum = 0;
        int rentalIncomeSum = 0;
        int interestSum = 0;
        int giftsSum = 0;
        int otherIncomeSum = 0;
        int taxesSum = 0;
        int mortgageSum = 0;
        int creditCardSum = 0;
        int utilitiesSum = 0;
        int foodSum = 0;
        int carPaymentSum = 0;
        int personalSum = 0;
        int activitiesSum = 0;
        int otherExpensesSum = 0;


        for (FinanceDocument item : list) {
            salarySum += item.getSalary();
            rentalIncomeSum += item.getRentalIncome();
            interestSum += item.getInterest();
            giftsSum += item.getGifts();
            otherIncomeSum += item.getOtherIncome();
            taxesSum += item.getTaxes();
            mortgageSum += item.getMortgage();
            creditCardSum += item.getCreditCard();
            utilitiesSum += item.getUtilities();
            foodSum += item.getFood();
            carPaymentSum += item.getCarPayment();
            personalSum += item.getPersonal();
            activitiesSum += item.getActivities();
            otherExpensesSum += item.getOtherExpenses();

        }
        SparseIntArray mapSum = new SparseIntArray();
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


    /**
     * Runs showcase presentation on fragment start
     */

    private void startShowcase() {
        if (mIncomeExpenseButton != null) {
            mIncomeExpenseButton.measure(0, 0);
            Double r = mIncomeExpenseButton.getMeasuredWidth() / 1.5;
            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(500); // half second between each showcase view

            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(mActivity, TAG);
            sequence.setConfig(config);
            sequence.addSequenceItem(new MaterialShowcaseView.Builder(mActivity)
                    .setTarget(mIncomeExpenseButton)
                    .setUseAutoRadius(false)
                    .setRadius(r.intValue())
                    .setContentText(R.string.income_expense_switch)
                    .setDismissText(R.string.ok)
                    .setDismissTextColor(ContextCompat.getColor(mContext, R.color.colorAccent))
                    .build());
            sequence.start();
        }
    }

    private class ValueTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
        }

        @Override
        public void onValueDeselected() {

        }
    }
}
