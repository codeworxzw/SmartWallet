package com.rbsoftware.pfm.personalfinancemanager.budget;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.rbsoftware.pfm.personalfinancemanager.ConnectionDetector;
import com.rbsoftware.pfm.personalfinancemanager.MainActivity;
import com.rbsoftware.pfm.personalfinancemanager.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Budget extends Fragment {
    private final static String TAG = "Budget";
    private PopupWindow popupWindow;

    private final int BUDGET_LOADER_ID = 3;

    private CardRecyclerView mRecyclerView;
    private BudgetCardRecyclerViewAdapter mCardArrayAdapter;
    private TextView mEmptyView;
    private ConnectionDetector mConnectionDetector;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle(getResources().getStringArray(R.array.drawer_menu)[3]);

        mRecyclerView = (CardRecyclerView) getActivity().findViewById(R.id.budget_card_recycler_view);
        mEmptyView = (TextView) getActivity().findViewById(R.id.emptyBudget);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getLoaderManager().initLoader(BUDGET_LOADER_ID, null, loaderCallbacks);
        if (mConnectionDetector == null) {
            mConnectionDetector = new ConnectionDetector(getContext());
        }
        MainActivity.mTracker.setScreenName(TAG);
        final Button btnCreateBudget = (Button) getActivity().findViewById(R.id.btn_create_budget);

        btnCreateBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();

            }
        });
    }

    @Override
    public void onResume() {


        super.onResume();


        //check if network is available and send analytics tracker

        if (mConnectionDetector.isConnectingToInternet()) {

            MainActivity.mTracker.send(new HitBuilders.EventBuilder().setCategory("Action").setAction("Open").build());
        }
    }

    private void generateBudget(List<BudgetCard> cards) {


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

    private LoaderManager.LoaderCallbacks<List<BudgetCard>> loaderCallbacks = new LoaderManager.LoaderCallbacks<List<BudgetCard>>() {
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

    private void showPopupWindow() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View wrapperBudget = layoutInflater.inflate(R.layout.budget_create_card_layout, null);

        popupWindow = new PopupWindow(wrapperBudget, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        ArrayAdapter<CharSequence> budgetPeriodSpinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.budget_period_spinner, android.R.layout.simple_spinner_item);
        budgetPeriodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner budgetPeriodSpinner = (Spinner) popupWindow.getContentView().findViewById(R.id.budget_period_spinner);
        budgetPeriodSpinner.setAdapter(budgetPeriodSpinnerAdapter);
        final SeekBar seekBar = (SeekBar) popupWindow.getContentView().findViewById(R.id.seekBar);
        final EditText editTextBudgetName = (EditText) popupWindow.getContentView().findViewById(R.id.et_budget_name);
        final EditText editTextBudgetValue = (EditText) popupWindow.getContentView().findViewById(R.id.et_budget_value);
        editTextBudgetValue.addTextChangedListener(new TextWatcher() {
            TextView textViewBudgetValue = (TextView) popupWindow.getContentView().findViewById(R.id.tv_budget_value);

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    seekBar.setMax(Integer.parseInt(s.toString()));
                    seekBar.setProgress(Math.round(seekBar.getMax() * 0.75f));
                    textViewBudgetValue.setText(s);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            TextView seekBarProgress = (TextView) popupWindow.getContentView().findViewById(R.id.tv_seekbar_progress);

            @Override
            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {

                int xPos = (((seekbar.getRight() - seekbar.getLeft()) * progress) /
                        seekbar.getMax()) + seekbar.getLeft();
                seekBarProgress.setX(xPos);

                seekBarProgress.setText(Integer.toString(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

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
                popupWindow.dismiss();
                Log.d(TAG, "period " + budgetPeriodSpinner.getSelectedItem().toString());
                Log.d(TAG, "value " + editTextBudgetValue.getText().toString());
                Log.d(TAG, "threshold " + seekBar.getProgress());
                createNewBudgetDocument(MainActivity.getUserId(),
                        budgetPeriodSpinner.getSelectedItem().toString(),

                        editTextBudgetName.getText().toString(),

                        new ArrayList<String>(Arrays.asList(
                                editTextBudgetValue.getText().toString(),
                                MainActivity.defaultCurrency)
                        ),
                        new ArrayList<String>(Arrays.asList(
                                String.valueOf(seekBar.getProgress()),
                                MainActivity.defaultCurrency
                        )),
                    true);
        }
    });

        popupWindow.showAtLocation(wrapperBudget, Gravity.CENTER, 0, 0);


    }


    /**
     * Creation new document from data
     */
    private void createNewBudgetDocument(String userId, String period, String name, ArrayList<String> value, ArrayList<String> threshold, boolean isActive) {
        BudgetDocument budgetDocument = new BudgetDocument(userId, period, name, value, threshold, isActive);
        MainActivity.financeDocumentModel.createDocument(budgetDocument);
        updateBudget();
       // MainActivity.financeDocumentModel.startPushReplication();

    }
}
