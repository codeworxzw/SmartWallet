package com.rbsoftware.pfm.personalfinancemanager;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
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
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


/**
 * A simple {@link Fragment} subclass.
 * Hold line chart data
 */
public class TrendsChart extends Fragment {
    private final String TAG = "TrendsChart";

    private RelativeLayout relativeLayout;
    private List<FinanceDocument> financeDocumentList;
    private String selectedPeriod; //position of selected item in popup menu
    private LineChartView mLineChart;
    private TextView mTextViewPeriod;
    private int checkedLine;
    private Context mContext;
    private Activity mActivity;

    public TrendsChart() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        checkedLine = Integer.valueOf(MainActivity.readFromSharedPreferences(getActivity(),
                "checkedLine", "0"));
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
        if (relativeLayout == null) {
            relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.trendsChartLayout);
        }
        if (mTextViewPeriod == null) {
            mTextViewPeriod = (TextView) getActivity().findViewById(R.id.tv_period_trend);
        }
        if (mLineChart == null) {
            mLineChart = (LineChartView) getActivity().findViewById(R.id.trends_chart);
        }

        mContext = getContext();
        mActivity = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Querying list of documents on fragment resume
        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate(
                MainActivity.readFromSharedPreferences(getActivity(), "periodTrend", "thisWeek"),
                MainActivity.getUserId(),
                FinanceDocumentModel.ORDER_ASC);
        mTextViewPeriod.setText(MainActivity.readFromSharedPreferences(getActivity(),
                "periodTextTrend",
                getResources().getString(R.string.this_week)));
        generateLineChartData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chart_trends_menu, menu);
        int status = mContext.getSharedPreferences("material_showcaseview_prefs", Context.MODE_PRIVATE)
                .getInt("status_" + TAG, 0);
        if (status != -1) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startShowcase();
                }
            }, 100);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_filter:
                showPopupPeriod();
                return true;
            case R.id.action_line:
                showPopupLine();
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
     * Shows period chart_trends_menu popup menu
     */

    private void showPopupPeriod() {
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
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate(
                                "thisWeek",
                                MainActivity.getUserId(),
                                FinanceDocumentModel.ORDER_ASC);
                        selectedPeriod = "thisWeek";
                        mTextViewPeriod.setText(getResources().getString(R.string.this_week));
                        break;
                    case R.id.thisMonth:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate(
                                "thisMonth",
                                MainActivity.getUserId(),
                                FinanceDocumentModel.ORDER_ASC);
                        selectedPeriod = "thisMonth";
                        mTextViewPeriod.setText(getResources().getString(R.string.this_month));

                        break;
                    case R.id.lastWeek:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate(
                                "lastWeek",
                                MainActivity.getUserId(),
                                FinanceDocumentModel.ORDER_ASC);
                        selectedPeriod = "lastWeek";
                        mTextViewPeriod.setText(getResources().getString(R.string.last_week));

                        break;
                    case R.id.lastMonth:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate(
                                "lastMonth",
                                MainActivity.getUserId(),
                                FinanceDocumentModel.ORDER_ASC);
                        selectedPeriod = "lastMonth";
                        mTextViewPeriod.setText(getResources().getString(R.string.last_month));

                        break;
                    case R.id.thisYear:
                        financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate(
                                "thisYear",
                                MainActivity.getUserId(),
                                FinanceDocumentModel.ORDER_ASC);
                        selectedPeriod = "thisYear";
                        mTextViewPeriod.setText(getResources().getString(R.string.this_year));

                        break;
                }
                MainActivity.saveToSharedPreferences(getActivity(), "periodTrend", selectedPeriod);
                MainActivity.saveToSharedPreferences(getActivity(), "periodTextTrend", mTextViewPeriod.getText().toString());
                generateLineChartData();
                return false;
            }
        });
        popup.show();

    }

    /**
     * Shows lines option menu
     */
    private void showPopupLine() {

        PopupMenu popupLine = new PopupMenu(getActivity(), getActivity().findViewById(R.id.action_line));
        MenuInflater inflate = popupLine.getMenuInflater();
        inflate.inflate(R.menu.lines, popupLine.getMenu());

        MenuItem item = popupLine.getMenu().findItem(Utils.findMenuItemByPosition(checkedLine));
        item.setChecked(true);

        popupLine.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                item.setChecked(!item.isChecked());
                if (item.isChecked()) {
                    checkedLine = Utils.getPositionFromId(item.getItemId());
                }
                MainActivity.saveToSharedPreferences(getActivity(), "checkedLine",
                        Integer.toString(checkedLine));
                generateLineChartData();

                return true;
            }
        });
        popupLine.show();

    }

    /**
     * Fills LineChart with data
     */

    private void generateLineChartData() {
        List<AxisValue> axisValues = new ArrayList<>();
        List<Line> lines = new ArrayList<>();

        List<String[]> docData = getDataFromDocument(Utils.findMenuItemByPosition(checkedLine),
                financeDocumentList);
        if(docData.size()>1) {
            mLineChart.setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.emptyTrends).setVisibility(View.GONE);
            List<PointValue> values = new ArrayList<>();
            axisValues.clear();
            for (int j = 0; j < docData.size(); ++j) {
                values.add(new PointValue(j, Integer.valueOf(docData.get(j)[0])));
                axisValues.add(new AxisValue(j).setLabel(docData.get(j)[1]));
            }

            Line line = new Line(values);
            line.setColor(Utils.getLineColorPalette
                    (mContext, Utils.findMenuItemByPosition(checkedLine)));
            line.setShape(ValueShape.CIRCLE);
            line.setCubic(true);

            line.setHasLabels(true);
            line.setHasLines(true);
            line.setHasPoints(true);

            lines.add(line);


            LineChartData data = new LineChartData(lines);


            Axis axisX = new Axis(axisValues);
            Axis axisY = new Axis().setHasLines(true);
            axisX.setName(getResources().getString(R.string.period));
            axisY.setName(getResources().getString(R.string.value));

            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
            mLineChart.setLineChartData(data);
            Viewport v = new Viewport(mLineChart.getMaximumViewport());

            v.bottom = v.bottom -Math.abs(v.bottom)*0.2f;
            v.top = v.top+Math.abs(v.top)*0.2f;
            mLineChart.setMaximumViewport(v);
            mLineChart.setCurrentViewportWithAnimation(v);

        }
        else{
            mLineChart.setVisibility(View.GONE);
            getActivity().findViewById(R.id.emptyTrends).setVisibility(View.VISIBLE);
        }
    }


    /**
     * Fetches values from document fro line chart
     *
     * @param lineId  chart type
     * @param docList list of finance documents
     * @return data values and dates list of arrays
     */
    private List<String[]> getDataFromDocument(int lineId, List<FinanceDocument> docList) {
        int value;
        List<String[]> data = new ArrayList<>();
        switch (lineId) {
            case R.id.popupBalance:
                for (FinanceDocument doc : docList) {
                    data.add(new String[]{
                            Integer.toString(doc.getTotalIncome()-doc.getTotalExpense()),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)});

                }
                break;
            case R.id.popupTotalIncome:
                for (FinanceDocument doc : docList) {
                    value = doc.getTotalIncome();
                    if (value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)});

                }
                break;
            case R.id.popupTotalExpense:
                for (FinanceDocument doc : docList) {
                    value = doc.getTotalExpense();
                    if (value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupSalary:
                for (FinanceDocument doc : docList) {
                    value = doc.getSalary();
                    if (value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupRentalIncome:
                for (FinanceDocument doc : docList) {
                    value = doc.getRentalIncome();
                    if (value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupInterest:
                for (FinanceDocument doc : docList) {
                    value = doc.getInterest();
                    if (value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupGifts:
                for (FinanceDocument doc : docList) {
                    value = doc.getGifts();
                    if (value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupOtherIncome:
                for (FinanceDocument doc : docList) {
                    value = doc.getOtherIncome();
                    if (value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupTaxes:
                for (FinanceDocument doc : docList) {
                    value = doc.getTaxes();
                    if (value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupMortgage:
                for (FinanceDocument doc : docList) {
                    value = doc.getMortgage();
                    if (value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupCreditCard:
                for (FinanceDocument doc : docList) {
                    value = doc.getCreditCard();
                    if (value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupUtilities:
                for (FinanceDocument doc : docList) {
                    value = doc.getUtilities();
                    if (value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupFood:
                for (FinanceDocument doc : docList) {
                    value = doc.getFood();
                    if (value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupCarPayment:
                for (FinanceDocument doc : docList) {
                    value = doc.getCarPayment();
                    if (value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupPersonal:
                for (FinanceDocument doc : docList) {
                    value = doc.getPersonal();
                    if (value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupActivities:
                for (FinanceDocument doc : docList) {
                    value = doc.getActivities();
                    if (value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;
            case R.id.popupOtherExpense:
                for (FinanceDocument doc : docList) {
                    value = doc.getOtherExpenses();
                    if (value != 0) data.add(new String[]{
                            Integer.toString(value),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                    });
                }
                break;

        }
        return data;
    }



    /**
     * Runs showcase presentation on fragment start
     */

    private void startShowcase() {
        if (mActivity.findViewById(R.id.action_line) != null) {
            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(500); // half second between each showcase view
            config.setDismissTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(mActivity, TAG);
            sequence.setConfig(config);
            sequence.addSequenceItem(mActivity.findViewById(R.id.action_line), getString(R.string.action_line), getString(R.string.ok));
            sequence.start();
        }
    }
}
