package com.rbsoftware.pfm.personalfinancemanager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_income, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataInput = (EditText) getActivity().findViewById(R.id.data_input);
//        incomeCommunicator = (IncomeCommunicator) getActivity();
    }

//Communicator to activity

    interface IncomeCommunicator{
        public void respond();
    }

}
