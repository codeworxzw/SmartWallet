package com.rbsoftware.pfm.personalfinancemanager;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {


    private ImageButton addNew;
    private LinearLayout mLayout;
    private int categorySpinnerId =1001;
    private int currencySpinnerId =2001;
    private int editTextValueId =3001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.report_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mLayout = (LinearLayout) findViewById(R.id.report_item_layout);
        mLayout.addView(createNewCategorySpinner());
        mLayout.addView(createNewEditText());
        mLayout.addView(createNewCurrencySpinner());

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

    //Generates operation category spinner
    private Spinner createNewCurrencySpinner() {
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final Spinner spinner = new Spinner(this);
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
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final Spinner spinner = new Spinner(this);
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
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final EditText editText = new EditText(this);
        editText.setLayoutParams(lparams);
        editText.setHint("Value");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setId(editTextValueId);
        editText.setSaveEnabled(true);
        Log.d("ID", editText.getId() + "");
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

           // setResult(RESULT_OK, intent);
           // finish();
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
            list.add(categorySpinner.getSelectedItem().toString() + "-" + editTextValue.getText().toString() + "-" + currencySpinner.getSelectedItem().toString());

        }
        Log.d("List", list.toString());

        return null;
    }


}
