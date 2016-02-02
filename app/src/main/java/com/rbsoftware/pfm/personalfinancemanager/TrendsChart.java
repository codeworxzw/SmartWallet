package com.rbsoftware.pfm.personalfinancemanager;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
    private LineChartView chart;
    private LineChartData data;
    private TextView mTextViewPeriod;
    private List<Integer> checkedLines; //list of checked lines positions
    private int listLinesSize; //size of checked lines list
    private PopupMenu popupLine;
    private Context mContext;
    private Activity mActivity;

    public TrendsChart() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //Populating list from shared preferences
        listLinesSize = Integer.valueOf(MainActivity.readFromSharedPreferences(getActivity(),
                "listLinesSize", "0"));
        checkedLines = new ArrayList<>();
        for (int i = 0; i < listLinesSize; i++) {
            checkedLines.add(Integer.valueOf(MainActivity.readFromSharedPreferences(getActivity(),
                    "checkedLine" + i, null)));

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
        if(relativeLayout == null) {
            relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.trendsChartLayout);
        }
        if(mTextViewPeriod == null) {
            mTextViewPeriod = (TextView) getActivity().findViewById(R.id.tv_period_trend);
        }
        if(chart == null) {
            chart = (LineChartView) getActivity().findViewById(R.id.trends_chart);
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
            }, 1000);
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
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestWriteExternalStoragePermission();
                } else {
                    try {
                        ExportData.exportChartAsPng(getContext(), relativeLayout);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

        popupLine = new PopupMenu(getActivity(), getActivity().findViewById(R.id.action_line));
        MenuInflater inflate = popupLine.getMenuInflater();
        inflate.inflate(R.menu.lines, popupLine.getMenu());
        if (listLinesSize != 0) {
            for (int i = 0; i < listLinesSize; i++) {
                MenuItem item = popupLine.getMenu().findItem(findMenuItemByPosition(checkedLines.get(i)));
                item.setChecked(true);
            }
        }
        popupLine.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                item.setChecked(!item.isChecked());
                if (item.isChecked()) {
                    checkedLines.add(getPositionFromId(item.getItemId()));
                } else {
                    int counter = 0;
                    while (counter < checkedLines.size()) {
                        if (checkedLines.get(counter) == (getPositionFromId(item.getItemId()))) {
                            checkedLines.remove(counter);
                        } else {
                            counter++;
                        }
                    }

                }
                listLinesSize = checkedLines.size();
                MainActivity.saveToSharedPreferences(getActivity(), "listLinesSize",
                        Integer.toString(listLinesSize));
                for (int i = 0; i < checkedLines.size(); i++) {
                    MainActivity.saveToSharedPreferences(getActivity(), "checkedLine" + i,
                            Integer.toString(checkedLines.get(i)));
                }

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
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<Line> lines = new ArrayList<Line>();
        for (int i = 0; i < checkedLines.size(); ++i) {
            List<String[]> docData = getDataFromDocument(findMenuItemByPosition(checkedLines.get(i)),
                    financeDocumentList);
            List<PointValue> values = new ArrayList<PointValue>();
            axisValues.clear();
            for (int j = 0; j < docData.size(); ++j) {
                values.add(new PointValue(j, Integer.valueOf(docData.get(j)[0])));
                axisValues.add(new AxisValue(j).setLabel(docData.get(j)[1]));
            }

            Line line = new Line(values);
            line.setColor(getColorPalette
                    (findMenuItemByPosition(checkedLines.get(i))));
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
            case R.id.popupTotalIncome:
                for (FinanceDocument doc : docList) {
                    data.add(new String[]{
                            Integer.toString(doc.getSalary() +
                                   doc.getRentalIncome() +
                                    doc.getInterest() +
                                    doc.getGifts() +
                                    doc.getOtherIncome()),
                            doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)});

                }
                break;
            case R.id.popupTotalExpense:
                for (FinanceDocument doc : docList) {
                    data.add(new String[]{
                            Integer.toString(doc.getTaxes() +
                                    doc.getMortgage() +
                                    doc.getCreditCard() +
                                    doc.getUtilities() +
                                    doc.getFood() +
                                    doc.getCarPayment() +
                                    doc.getPersonal() +
                                    doc.getActivities() +
                                    doc.getOtherExpenses()),
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
     * Converts menu item title into id
     *
     * @param position of menu item
     * @return resource id
     */
    private int findMenuItemByPosition(int position) {
        switch (position) {
            case 0:
                return R.id.popupTotalIncome;
            case 1:
                return R.id.popupTotalExpense;

            case 2:
                return R.id.popupSalary;
            case 3:
                return R.id.popupRentalIncome;
            case 4:
                return R.id.popupInterest;
            case 5:
                return R.id.popupGifts;
            case 6:
                return R.id.popupOtherIncome;

            case 7:
                return R.id.popupTaxes;
            case 8:
                return R.id.popupMortgage;
            case 9:
                return R.id.popupCreditCard;
            case 10:
                return R.id.popupUtilities;
            case 11:
                return R.id.popupFood;
            case 12:
                return R.id.popupCarPayment;
            case 13:
                return R.id.popupPersonal;
            case 14:
                return R.id.popupActivities;
            case 15:
                return R.id.popupOtherExpense;

            default:
                return R.id.popupTotalIncome;
        }
    }

    /**
     * Converts options menu id into position
     *
     * @param id of resource
     * @return resource position in menu
     */
    private int getPositionFromId(int id) {
        switch (id) {
            case R.id.popupTotalIncome:
                return 0;
            case R.id.popupTotalExpense:
                return 1;
            case R.id.popupSalary:
                return 2;
            case R.id.popupRentalIncome:
                return 3;
            case R.id.popupInterest:
                return 4;
            case R.id.popupGifts:
                return 5;
            case R.id.popupOtherIncome:
                return 6;

            case R.id.popupTaxes:
                return 7;
            case R.id.popupMortgage:
                return 8;
            case R.id.popupCreditCard:
                return 9;
            case R.id.popupUtilities:
                return 10;
            case R.id.popupFood:
                return 11;
            case R.id.popupCarPayment:
                return 12;
            case R.id.popupPersonal:
                return 13;
            case R.id.popupActivities:
                return 14;
            case R.id.popupOtherExpense:
                return 15;

            default:
                return 0;
        }
    }

    /**
     * gets chart line color by resource id
     *
     * @param i resource id
     * @return color of line
     */
    private int getColorPalette(int i) {
        switch (i) {
            case R.id.popupTotalIncome:
                return ContextCompat.getColor(getContext(), R.color.income);
            case R.id.popupTotalExpense:
                return ContextCompat.getColor(getContext(), R.color.expense);
            case R.id.popupSalary:
                return ContextCompat.getColor(getContext(), R.color.salary);
            case R.id.popupRentalIncome:
                return ContextCompat.getColor(getContext(), R.color.rental_income);
            case R.id.popupInterest:
                return ContextCompat.getColor(getContext(), R.color.interest);
            case R.id.popupGifts:
                return ContextCompat.getColor(getContext(), R.color.gifts);
            case R.id.popupOtherIncome:
                return ContextCompat.getColor(getContext(), R.color.other_income);
            case R.id.popupTaxes:
                return ContextCompat.getColor(getContext(), R.color.taxes);
            case R.id.popupMortgage:
                ContextCompat.getColor(getContext(), R.color.mortgage);
            case R.id.popupCreditCard:
                return ContextCompat.getColor(getContext(), R.color.credit_card);
            case R.id.popupUtilities:
                return ContextCompat.getColor(getContext(), R.color.utilities);
            case R.id.popupFood:
                return ContextCompat.getColor(getContext(), R.color.food);
            case R.id.popupCarPayment:
                return ContextCompat.getColor(getContext(), R.color.car_payment);
            case R.id.popupPersonal:
                return ContextCompat.getColor(getContext(), R.color.personal);
            case R.id.popupActivities:
                return ContextCompat.getColor(getContext(), R.color.activities);
            case R.id.popupOtherExpense:
                return ContextCompat.getColor(getContext(), R.color.other_expense);
            default:
                return Color.WHITE;

        }
    }


    /**
     * Requests WRITE_EXTERNAL_STORAGE permission
     */
    private void requestWriteExternalStoragePermission() {

        // No explanation needed, we can request the permission.

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                0);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    try {
                        ExportData.exportChartAsPng(getContext(), relativeLayout);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {

                    showExplanationDialog();

                }
                break;
            }


        }
    }

    /**
     * Shows explanation why WRITE_EXTERNAL_STORAGE required
     */
    private void showExplanationDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle(getString(R.string.permission_required));
        alertDialog.setMessage(getString(R.string.permission_write_external_storage_explanation));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * Runs showcase presentation on fragment start
     */

    private void startShowcase() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view
        config.setDismissTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(mActivity, TAG);
        sequence.setConfig(config);
        sequence.addSequenceItem(mActivity.findViewById(R.id.action_line), getString(R.string.action_line), getString(R.string.ok));
        sequence.start();

    }
}
