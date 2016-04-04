package com.rbsoftware.pfm.personalfinancemanager.budget;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudant.sync.datastore.ConflictException;
import com.google.android.gms.analytics.HitBuilders;
import com.rbsoftware.pfm.personalfinancemanager.ConnectionDetector;
import com.rbsoftware.pfm.personalfinancemanager.ExportData;
import com.rbsoftware.pfm.personalfinancemanager.MainActivity;
import com.rbsoftware.pfm.personalfinancemanager.R;
import com.rbsoftware.pfm.personalfinancemanager.utils.DateUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * Holds method for displaying creation and budget editing
 *
 * @author Roman Burzakovskiy
 */
public class Budget extends Fragment {
    private final static String TAG = "Budget";
    private final int BUDGET_LOADER_ID = 3;
    private PopupWindow popupWindow;
    private boolean isCreateBudgetPopupWindowOpen;
    private boolean isEditBudgetPopupWindowOpen;
    private String docId;
    private CardRecyclerView mRecyclerView;
    private BudgetCardRecyclerViewAdapter mCardArrayAdapter;
    private TextView mEmptyView;
    private ConnectionDetector mConnectionDetector;
    private EditText editTextBudgetValue;

    public Budget() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_budget, container, false);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle(getResources().getStringArray(R.array.drawer_menu)[1]);

        mRecyclerView = (CardRecyclerView) getActivity().findViewById(R.id.budget_card_recycler_view);
        mEmptyView = (TextView) getActivity().findViewById(R.id.emptyBudget);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getLoaderManager().initLoader(BUDGET_LOADER_ID, null, loaderCallbacks);

        isCreateBudgetPopupWindowOpen = savedInstanceState != null && savedInstanceState.getBoolean("isCreateBudgetPopupWindowOpen");
        isEditBudgetPopupWindowOpen = savedInstanceState != null && savedInstanceState.getBoolean("isEditBudgetPopupWindowOpen");
        if (savedInstanceState != null) {
            docId = savedInstanceState.getString("docId");
        }
        if (isCreateBudgetPopupWindowOpen) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showCreateBudgetPopupWindow(savedInstanceState);
                }
            }, 100);

        }
        if (isEditBudgetPopupWindowOpen) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showEditBudgetPopupWindow(savedInstanceState, docId);
                }
            }, 100);

        }


        if (mConnectionDetector == null) {
            mConnectionDetector = new ConnectionDetector(getContext());
        }
        MainActivity.mTracker.setScreenName(TAG);
        FloatingActionButton btnCreateBudget = (FloatingActionButton) getActivity().findViewById(R.id.btn_create_budget);

        btnCreateBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateBudgetPopupWindow(null);

            }
        });

        int status = getContext().getSharedPreferences("material_showcaseview_prefs", Context.MODE_PRIVATE)
                .getInt("status_" + TAG, 0);
        if (status != -1) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startShowcase();
                }
            }, 500);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //check if network is available and send analytics tracker

        if (mConnectionDetector.isConnectingToInternet()) {

            MainActivity.mTracker.send(new HitBuilders.EventBuilder().setCategory("Action").setAction("Open").build());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isCreateBudgetPopupWindowOpen || isEditBudgetPopupWindowOpen) {
            popupWindow.dismiss();
            isCreateBudgetPopupWindowOpen = true;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isCreateBudgetPopupWindowOpen", isCreateBudgetPopupWindowOpen);
        outState.putBoolean("isEditBudgetPopupWindowOpen", isEditBudgetPopupWindowOpen);
        outState.putString("docId", docId);
        if (popupWindow != null) {
            outState.putInt("budgetPeriodSpinner", ((Spinner) popupWindow.getContentView().findViewById(R.id.budget_period_spinner)).getSelectedItemPosition());
            outState.putString("editTextBudgetValue", editTextBudgetValue.getText().toString());
            outState.putString("editTextBudgetName", ((EditText) popupWindow.getContentView().findViewById(R.id.et_budget_name)).getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Generates budget cards from asynctaskloader
     *
     * @param cards BudgetCards
     */
    private void generateBudget(List<BudgetCard> cards) {
        for (BudgetCard historyCard : cards) {
            historyCard.getCardHeader().setPopupMenu(R.menu.history_card_menu, new CardHeader.OnClickCardHeaderPopupMenuListener() {
                @Override
                public void onMenuItemClick(final BaseCard card, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.history_edit:
                            docId = ((BudgetCard) card).getDocument().getDocumentRevision().getId();
                            showEditBudgetPopupWindow(null, docId);
                            return;

                        case R.id.history_delete:

                            new AlertDialog.Builder(getContext())
                                    .setTitle(getContext().getString(R.string.delete_dialog_title))
                                    .setMessage(getContext().getString(R.string.delete_dialog_message))
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            try {
                                                MainActivity.financeDocumentModel.deleteDocument(((BudgetCard) card).getDocument());
                                                mCardArrayAdapter.remove((BudgetCard) card);
                                            } catch (ConflictException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();


                            return;

                        case R.id.history_share:

                            try {
                                ExportData.exportBudgetAsCsv(getContext(), prepareCsvData(((BudgetCard) card)));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return;

                    }
                }
            });
        }

        mCardArrayAdapter = new BudgetCardRecyclerViewAdapter(getActivity(), cards);
        //Staggered grid view
        mCardArrayAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkAdapterIsEmpty();
            }
        });

        //Set the empty view
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mCardArrayAdapter);
            checkAdapterIsEmpty();


        }

    }

    /**
     * Checks whether recycler view is empty
     * And switches to empty view
     */
    private void checkAdapterIsEmpty() {
        if (mCardArrayAdapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);

        }
    }

    /**
     * Sends broadcast intent to update history
     */
    private void updateBudget() {
        Intent intent = new Intent(BudgetLoader.ACTION);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

    private final LoaderManager.LoaderCallbacks<List<BudgetCard>> loaderCallbacks = new LoaderManager.LoaderCallbacks<List<BudgetCard>>() {
        @Override
        public Loader<List<BudgetCard>> onCreateLoader(int id, Bundle args) {
            return new BudgetLoader(getContext());
        }

        @Override
        public void onLoadFinished(Loader<List<BudgetCard>> loader, List<BudgetCard> data) {
            generateBudget(data);
        }

        @Override
        public void onLoaderReset(Loader<List<BudgetCard>> loader) {
        }
    };

    /**
     * Generates create budget popup window
     */
    private void showCreateBudgetPopupWindow(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View wrapperBudget = layoutInflater.inflate(R.layout.budget_create_card_layout, null);

        popupWindow = new PopupWindow(wrapperBudget, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        ArrayAdapter<CharSequence> budgetPeriodSpinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.budget_period_spinner, android.R.layout.simple_spinner_item);
        budgetPeriodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner budgetPeriodSpinner = (Spinner) popupWindow.getContentView().findViewById(R.id.budget_period_spinner);
        budgetPeriodSpinner.setAdapter(budgetPeriodSpinnerAdapter);
        final EditText editTextBudgetName = (EditText) popupWindow.getContentView().findViewById(R.id.et_budget_name);
        ((TextView) popupWindow.getContentView().findViewById(R.id.tv_budget_currency)).setText(MainActivity.defaultCurrency);
        editTextBudgetValue = (EditText) popupWindow.getContentView().findViewById(R.id.et_budget_value);
        if (savedInstanceState != null) {
            budgetPeriodSpinner.setSelection(savedInstanceState.getInt("budgetPeriodSpinner"));
            editTextBudgetName.setText(savedInstanceState.getString("editTextBudgetName"));
            editTextBudgetValue.setText(savedInstanceState.getString("editTextBudgetValue"));
        }


        Button buttonCancel = (Button) popupWindow.getContentView().findViewById(R.id.btn_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        Button buttonCreateBudget = (Button) popupWindow.getContentView().findViewById(R.id.btn_create);
        buttonCreateBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    popupWindow.dismiss();
                    createNewBudgetDocument(MainActivity.getUserId(),
                            transformPeriodValue(budgetPeriodSpinner.getSelectedItemPosition()),

                            transformBudgetName(editTextBudgetName.getText().toString()),

                            new ArrayList<>(Arrays.asList(
                                    editTextBudgetValue.getText().toString().replaceFirst("^0+(?!$)", ""),
                                    MainActivity.defaultCurrency)
                            ),

                            true);
                }
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isCreateBudgetPopupWindowOpen = false;
            }
        });
        popupWindow.showAtLocation(wrapperBudget, Gravity.CENTER, 0, 0);
        isCreateBudgetPopupWindowOpen = true;


    }

    /**
     * generates edit budget popup window
     *
     * @param docId id of budget document
     */
    private void showEditBudgetPopupWindow(Bundle savedInstanceState, final String docId) {
        BudgetDocument doc = MainActivity.financeDocumentModel.getBudgetDocument(docId);
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View wrapperBudget = layoutInflater.inflate(R.layout.budget_create_card_layout, null);

        popupWindow = new PopupWindow(wrapperBudget, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        ((TextView) popupWindow.getContentView().findViewById(R.id.tv_budget_create_card_title)).setText(getString(R.string.edit_budget));

        ArrayAdapter<CharSequence> budgetPeriodSpinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.budget_period_spinner, android.R.layout.simple_spinner_item);
        budgetPeriodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner budgetPeriodSpinner = (Spinner) popupWindow.getContentView().findViewById(R.id.budget_period_spinner);
        budgetPeriodSpinner.setAdapter(budgetPeriodSpinnerAdapter);


        final EditText editTextBudgetName = (EditText) popupWindow.getContentView().findViewById(R.id.et_budget_name);

        ((TextView) popupWindow.getContentView().findViewById(R.id.tv_budget_currency)).setText(MainActivity.defaultCurrency);
        editTextBudgetValue = (EditText) popupWindow.getContentView().findViewById(R.id.et_budget_value);


        if (savedInstanceState != null) {
            budgetPeriodSpinner.setSelection(savedInstanceState.getInt("budgetPeriodSpinner"));
            editTextBudgetName.setText(savedInstanceState.getString("editTextBudgetName"));
            editTextBudgetValue.setText(savedInstanceState.getString("editTextBudgetValue"));
        } else {
            if (doc.getPeriod().equals(BudgetDocument.PERIOD_WEEKLY)) {
                budgetPeriodSpinner.setSelection(0);
            } else {
                budgetPeriodSpinner.setSelection(1);
            }
            editTextBudgetName.setText(doc.getName());
            editTextBudgetValue.setText(Integer.toString(doc.getValue()));
        }

        Button buttonCancel = (Button) popupWindow.getContentView().findViewById(R.id.btn_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        Button buttonCreateBudget = (Button) popupWindow.getContentView().findViewById(R.id.btn_create);
        buttonCreateBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    popupWindow.dismiss();
                    updateBudgetDocument(docId, MainActivity.getUserId(),
                            transformPeriodValue(budgetPeriodSpinner.getSelectedItemPosition()),

                            transformBudgetName(editTextBudgetName.getText().toString()),

                            new ArrayList<>(Arrays.asList(
                                    editTextBudgetValue.getText().toString().replaceFirst("^0+(?!$)", ""),
                                    MainActivity.defaultCurrency)
                            ),

                            true);
                }
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isEditBudgetPopupWindowOpen = false;
            }
        });
        popupWindow.showAtLocation(wrapperBudget, Gravity.CENTER, 0, 0);
        isEditBudgetPopupWindowOpen = true;


    }

    /**
     * Checks if entered data is ok
     *
     * @return true if edit text value is ok
     */
    private boolean validateFields() {
        if (editTextBudgetValue.getText().toString().isEmpty()) {
            editTextBudgetValue.requestFocus();
            Toast.makeText(getContext(), getContext().getString(R.string.set_value), Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextBudgetValue.getText().toString().matches("0+")) {
            editTextBudgetValue.requestFocus();
            Toast.makeText(getContext(), getContext().getString(R.string.set_non_zero_value), Toast.LENGTH_LONG).show();

            return false;
        }
        return true;
    }

    /**
     * Generates name for budget if field was empty
     *
     * @param name of budget
     * @return string of budget name
     */
    private String transformBudgetName(String name) {
        if (name.isEmpty()) {
            int count = mCardArrayAdapter.getItemCount() + 1;
            return getContext().getString(R.string.my_budget) + " " + count;
        } else {
            return name;
        }
    }

    /**
     * Converts spinner position into string
     *
     * @param position position of spinner
     * @return string value depending on spinner position
     */
    private String transformPeriodValue(int position) {
        switch (position) {
            case 0:
                return BudgetDocument.PERIOD_WEEKLY;
            case 1:
                return BudgetDocument.PERIOD_MONTHLY;
            default:
                return BudgetDocument.PERIOD_WEEKLY;
        }
    }

    /**
     * Translates english value from document into local language
     *
     * @param value in document
     * @return value in local language
     */
    private String convertPeriodValueFromEnglish(String value) {
        switch (value) {
            case BudgetDocument.PERIOD_WEEKLY:
                return getContext().getResources().getStringArray(R.array.budget_period_spinner)[0];
            case BudgetDocument.PERIOD_MONTHLY:
                return getContext().getResources().getStringArray(R.array.budget_period_spinner)[1];
            default:
                return getContext().getResources().getStringArray(R.array.budget_period_spinner)[0];
        }
    }

    /**
     * Creates new budget document
     *
     * @param userId   id of current user
     * @param period   of budget
     * @param name     of budget
     * @param value    of budget
     * @param isActive status of budget
     */
    private void createNewBudgetDocument(String userId, String period, String name, ArrayList<String> value, boolean isActive) {
        BudgetDocument budgetDocument = new BudgetDocument(userId, period, name, value, isActive);
        MainActivity.financeDocumentModel.createDocument(budgetDocument);
        updateBudget();
        // MainActivity.financeDocumentModel.startPushReplication();

    }

    /**
     * Updates budget document
     *
     * @param docId    id of old document
     * @param userId   id of current user
     * @param period   of budget
     * @param name     of budget
     * @param value    of budget
     * @param isActive status of budget
     */
    private void updateBudgetDocument(String docId, String userId, String period, String name, ArrayList<String> value, boolean isActive) {
        try {
            MainActivity.financeDocumentModel.updateBudgetDocument(MainActivity.financeDocumentModel.getBudgetDocument(docId),
                    new BudgetDocument(userId, period, name, value, isActive));
            updateBudget();
        } catch (ConflictException e) {
            e.printStackTrace();
        }

    }

    /**
     * Generates data for csv file
     *
     * @param card selected budget card
     * @return data for csv file
     */
    private List<String[]> prepareCsvData(BudgetCard card) {
        BudgetDocument doc = card.getDocument();
        List<String[]> data = new ArrayList<>();
        int[] totalExpenseIncomeData = card.getTotalExpenseIncomeData();
        data.add(new String[]{getString(R.string.document_date), DateUtils.getNormalDate(DateUtils.DATE_FORMAT_LONG, doc.getDate())});
        data.add(new String[]{"", ""});
        data.add(new String[]{doc.getName(), ""});
        data.add(new String[]{getString(R.string.period), convertPeriodValueFromEnglish(doc.getPeriod())});
        data.add(new String[]{getString(R.string.value), String.format(Locale.getDefault(), "%,d", doc.getValue()) + " " + MainActivity.defaultCurrency});
        data.add(new String[]{getString(R.string.estimated_balance), card.getEstimatedBudgetValue()});
        if (doc.getPeriod().equals(BudgetDocument.PERIOD_WEEKLY)) {
            for (int i = 0; i < 3; i++) {
                if (totalExpenseIncomeData[i] != 0) {
                    int percent = Math.round((float) totalExpenseIncomeData[i] / doc.getValue() * 100f);
                    data.add(new String[]{getContext().getResources().getStringArray(R.array.budget_card_periods)[i], percent + "%"});
                }
            }
        } else {
            for (int i = 4; i < 7; i++) {
                if (totalExpenseIncomeData[i] != 0) {
                    int percent = Math.round((float) totalExpenseIncomeData[i - 1] / doc.getValue() * 100f);
                    data.add(new String[]{getContext().getResources().getStringArray(R.array.budget_card_periods)[i], percent + "%"});
                }
            }
        }


        return data;
    }

    /**
     * Runs showcase presentation on fragment start
     **/
    private void startShowcase() {
        if (getActivity().findViewById(R.id.btn_create_budget) != null) {
            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(500); // half second between each showcase view
            config.setDismissTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), TAG);
            sequence.setConfig(config);
            sequence.addSequenceItem(getActivity().findViewById(R.id.btn_create_budget), getString(R.string.budget_showcase), getString(R.string.ok));
            sequence.start();
        }
    }
}
