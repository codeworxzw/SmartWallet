package com.rbsoftware.pfm.personalfinancemanager;

import android.content.Intent;
import android.os.PersistableBundle;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {


    private ImageButton addNew;
    private RelativeLayout mLayout;
    private int categorySpinnerId =1001; //IDs of categorySpinner
    private int currencySpinnerId =2001; //IDs of currencySpinner
    private int editTextValueId =3001;   //Ids of editText
    private int textViewRecursId =4001;   //Ids of TextView "Recurs"


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.report_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mLayout = (RelativeLayout) findViewById(R.id.report_item_layout);
        if(savedInstanceState ==null) {
            mLayout.addView(createNewCategorySpinner());
            mLayout.addView(createNewEditText());
            mLayout.addView(createNewCurrencySpinner());
        }
        else{
            //views states were saved in onSaveInstanceState
            int counter = savedInstanceState.getInt("counter");
            for(int i=1; i<counter; i++){
                mLayout.addView(createNewCategorySpinner());
                Spinner categorySpinner = (Spinner) findViewById(1000+i);
                categorySpinner.setSelection(savedInstanceState.getInt("categorySpinner"+i));
                mLayout.addView(createNewEditText());
                EditText editTextValue = (EditText) findViewById(3000+i);
                editTextValue.setText(savedInstanceState.getString("editTextValue" + i));
                mLayout.addView(createNewCurrencySpinner());
                Spinner currencySpinner = (Spinner) findViewById(2000+i);
                currencySpinner.setSelection(savedInstanceState.getInt("currencySpinner"+i));

            }

        }

        addNew = (ImageButton) findViewById(R.id.btn_add_new);



        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayout.addView(createNewCategorySpinner());
                mLayout.addView(createNewEditText());
                mLayout.addView(createNewCurrencySpinner());

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        int counter = categorySpinnerId - 1000;
        outState.putInt("counter", counter);
        for(int i=1;i<counter;i++){
            Spinner categorySpinner = (Spinner) findViewById(1000+i);
            outState.putInt("categorySpinner"+i, categorySpinner.getSelectedItemPosition());
            Spinner currencySpinner = (Spinner) findViewById(2000+i);
            outState.putInt("currencySpinner"+i, currencySpinner.getSelectedItemPosition());
            EditText editTextValue = (EditText) findViewById(3000+i);
            outState.putString("editTextValue"+i, editTextValue.getText().toString());
            Log.d("counter", " onSaveInstanceState"+ categorySpinner.getSelectedItemPosition() +" "+
                    currencySpinner.getSelectedItemPosition() +" "+  editTextValue.getText().toString());
        }
        Log.d("counter", counter + " onSaveInstanceState");

        super.onSaveInstanceState(outState);

    }



    //Generates operation category spinner
    private Spinner createNewCurrencySpinner() {
        final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, dpToPx(40));
        final Spinner spinner = new Spinner(this);
        lparams.addRule(RelativeLayout.END_OF, editTextValueId-1);
        if(currencySpinnerId > 2001){
            lparams.addRule(RelativeLayout.BELOW, currencySpinnerId-1);
        }
        spinner.setLayoutParams(lparams);
        ArrayAdapter<CharSequence> currencySpinnerAdapter = ArrayAdapter.createFromResource(this,R.array.report_activity_currency_spinner,android.R.layout.simple_spinner_item);
        currencySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(currencySpinnerAdapter);
        spinner.setId(currencySpinnerId);
        spinner.setSaveEnabled(true);
        Log.d("ID", spinner.getId() + "");
        currencySpinnerId++;
        return spinner;
    }

    //Generates currency type spinner

    private Spinner createNewCategorySpinner() {
        final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, dpToPx(40));
        final Spinner spinner = new Spinner(this);
        if(categorySpinnerId > 1001){
            lparams.addRule(RelativeLayout.BELOW, categorySpinnerId-1);
        }

        spinner.setLayoutParams(lparams);
        ArrayAdapter<CharSequence> categorySpinnerAdapter = ArrayAdapter.createFromResource(this,R.array.report_activity_category_spinner,android.R.layout.simple_spinner_item);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(categorySpinnerAdapter);
        spinner.setId(categorySpinnerId);
        spinner.setSaveEnabled(true);
        Log.d("ID", spinner.getId() + "");
        categorySpinnerId++;
        return spinner;
    }

    //Generates operation input value edit text

    private EditText createNewEditText() {
        final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, dpToPx(40));
        final EditText editText = new EditText(this);
        lparams.addRule(RelativeLayout.END_OF, categorySpinnerId - 1);
        if(editTextValueId > 3001){
            lparams.addRule(RelativeLayout.BELOW, editTextValueId-1);
        }
        int maxLength = 8;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        editText.setFilters(fArray);
        editText.setLayoutParams(lparams);
        editText.setHint("Value");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setId(editTextValueId);
        editText.setSaveEnabled(true);

        editTextValueId++;
        return editText;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        if (id == R.id.report_toolbar_done){
            Intent intent = new Intent();
            intent.putStringArrayListExtra("reportResult", getReportResult());
            Log.d("List", getReportResult().toString());
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    //Compiles inputs from fields
    //@return list of strings category-value-currency
    private ArrayList<String> getReportResult() {
        ArrayList<String> list = new ArrayList<>();
        int counter = categorySpinnerId-1000;
        for(int i=1; i<counter; i++){
            Spinner categorySpinner = (Spinner) findViewById(1000+i);
            Spinner currencySpinner = (Spinner) findViewById(2000+i);
            EditText editTextValue = (EditText) findViewById(3000+i);
            if(editTextValue.getText().toString().isEmpty()){
                editTextValue.setText("0");
            }

                list.add(categorySpinner.getSelectedItemPosition() + "-" + categorySpinner.getSelectedItem().toString() + "-" + editTextValue.getText().toString() + "-" + currencySpinner.getSelectedItem().toString());


        }

        return list;
    }

    // helper method to convert dp to px
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }




}
