package com.rbsoftware.pfm.personalfinancemanager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class IncomeFragment extends Fragment {

    private IncomeCommunicator incomeCommunicator;
    private EditText dataInput;
    public IncomeFragment() {
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
        return inflater.inflate(R.layout.fragment_income, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataInput = (EditText) getActivity().findViewById(R.id.data_input);
        incomeCommunicator = (IncomeCommunicator) getActivity(); //links interface with parent activity
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.report_toolbar_done : {
                incomeCommunicator.respond(dataInput.getText().toString());

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
//Communicator to activity

    interface IncomeCommunicator{
        public void respond(String text);
    }

}
