package com.rbsoftware.pfm.personalfinancemanager;


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

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Charts extends Fragment {
    private List<FinanceDocument> financeDocumentList;

    public Charts() {
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
        return inflater.inflate(R.layout.fragment_charts, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getResources().getStringArray(R.array.drawer_menu)[1]);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.filter, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_filter){
            showPopup();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
    //Helper methods
    //Shows filter popup menu
    public void showPopup(){
        View menuItemView = getActivity().findViewById(R.id.action_filter);
        PopupMenu popup = new PopupMenu(getActivity(), menuItemView);
        MenuInflater inflate = popup.getMenuInflater();
        inflate.inflate(R.menu.period, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                Log.d("popup menu", item.getTitle().toString());
                switch (id){
                    case R.id.thisWeek:
                       financeDocumentList= MainActivity.financeDocumentModel.queryDocumentsByDate("thisWeek", MainActivity.getUserId());

                        break;
                    case R.id.thisMonth:
                        financeDocumentList= MainActivity.financeDocumentModel.queryDocumentsByDate("thisMonth", MainActivity.getUserId());


                        break;
                    case R.id.lastWeek:
                        Log.d("popup menu", "Last week");
                        financeDocumentList= MainActivity.financeDocumentModel.queryDocumentsByDate("lastWeek", MainActivity.getUserId());


                        break;
                    case R.id.lastMonth:
                        financeDocumentList= MainActivity.financeDocumentModel.queryDocumentsByDate("lastMonth", MainActivity.getUserId());

                        break;
                    case R.id.thisYear:
                        financeDocumentList= MainActivity.financeDocumentModel.queryDocumentsByDate("thisYear", MainActivity.getUserId());

                        break;
                }
                return false;
            }
        });
        popup.show();

    }


}
