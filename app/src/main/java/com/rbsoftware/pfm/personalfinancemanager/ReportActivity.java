package com.rbsoftware.pfm.personalfinancemanager;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class ReportActivity extends AppCompatActivity {

    private final String TAG = "ReportActivity";
    private Button addNew;
    private RelativeLayout mLayout;
    private int categorySpinnerId = 1001; //IDs of categorySpinner
    private int currencySpinnerId = 2001; //IDs of currencySpinner
    private int editTextValueId = 3001;   //Ids of editText
     /* Recursion disabled in version 1.0
                    TODO enable recursion in future versions
    private int textViewRecursId =4001;   //Ids of TextView "Recurs"
    private int spinnerRecursId =5001;   //Ids of spinner "Recurs"
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.report_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mLayout = (RelativeLayout) findViewById(R.id.report_item_layout);
        if (savedInstanceState == null) {
            mLayout.addView(createNewCategorySpinner());
            mLayout.addView(createNewEditText());
            mLayout.addView(createNewCurrencySpinner());
             /* Recursion disabled in version 1.0
                    TODO enable recursion in future versions
            mLayout.addView(createNewRecursTextView());
            mLayout.addView(createNewRecursSpinner());
            */
        } else {
            //views states were saved in onSaveInstanceState
            int counter = savedInstanceState.getInt("counter");
            for (int i = 1; i < counter; i++) {
                mLayout.addView(createNewCategorySpinner());
                Spinner categorySpinner = (Spinner) findViewById(1000 + i);
                categorySpinner.setSelection(savedInstanceState.getInt("categorySpinner" + i));
                mLayout.addView(createNewEditText());
                EditText editTextValue = (EditText) findViewById(3000 + i);
                editTextValue.setText(savedInstanceState.getString("editTextValue" + i));
                mLayout.addView(createNewCurrencySpinner());
                Spinner currencySpinner = (Spinner) findViewById(2000 + i);
                currencySpinner.setSelection(savedInstanceState.getInt("currencySpinner" + i));
                 /* Recursion disabled in version 1.0
                    TODO enable recursion in future versions
                mLayout.addView(createNewRecursTextView());
                mLayout.addView(createNewRecursSpinner());
                Spinner recursSpinner = (Spinner) findViewById(5000+i);
                recursSpinner.setSelection(savedInstanceState.getInt("recursSpinner" + i));
                */
            }

        }

        addNew = (Button) findViewById(R.id.btn_add_new);


        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayout.addView(createNewCategorySpinner());
                mLayout.addView(createNewEditText());
                mLayout.addView(createNewCurrencySpinner());
                 /* Recursion disabled in version 1.0
                    TODO enable recursion in future versions
                mLayout.addView(createNewRecursTextView());
                mLayout.addView(createNewRecursSpinner());
                */


            }
        });

        int status = getSharedPreferences("material_showcaseview_prefs", Context.MODE_PRIVATE)
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
    public void onSaveInstanceState(Bundle outState) {
        int counter = categorySpinnerId - 1000;
        outState.putInt("counter", counter);
        for (int i = 1; i < counter; i++) {
            Spinner categorySpinner = (Spinner) findViewById(1000 + i);
            outState.putInt("categorySpinner" + i, categorySpinner.getSelectedItemPosition());
            Spinner currencySpinner = (Spinner) findViewById(2000 + i);
            outState.putInt("currencySpinner" + i, currencySpinner.getSelectedItemPosition());
            EditText editTextValue = (EditText) findViewById(3000 + i);
            outState.putString("editTextValue" + i, editTextValue.getText().toString());
             /* Recursion disabled in version 1.0
                    TODO enable recursion in future versions
            Spinner recursSpinner = (Spinner) findViewById(5000+i);
            outState.putInt("recursSpinner"+i, recursSpinner.getSelectedItemPosition());
            */

        }

        super.onSaveInstanceState(outState);

    }


    /**
     * Generates operation category spinner
     *
     * @return currency spinner
     */

    private Spinner createNewCurrencySpinner() {
        final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, dpToPx(40));
        final Spinner spinner = new Spinner(this);
        int position = 0;
        lparams.addRule(RelativeLayout.RIGHT_OF, editTextValueId - 1);

        if (categorySpinnerId > 1001) {
             /* Recursion disabled in version 1.0
                    TODO enable recursion in future versions
            lparams.addRule(RelativeLayout.BELOW, textViewRecursId-1);
             */
            lparams.addRule(RelativeLayout.BELOW, currencySpinnerId - 1);
        }

        spinner.setLayoutParams(lparams);
        ArrayAdapter<CharSequence> currencySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.report_activity_currency_spinner, android.R.layout.simple_spinner_item);
        currencySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String[] currencyList = getResources().getStringArray(R.array.report_activity_currency_spinner);
        for (int i = 0; i < currencyList.length; i++) {
            if (currencyList[i].equals(MainActivity.defaultCurrency)) {
                position = i;
            }
        }
        spinner.setAdapter(currencySpinnerAdapter);
        spinner.setSelection(position);
        spinner.setId(currencySpinnerId);
        spinner.setSaveEnabled(true);
        currencySpinnerId++;
        return spinner;
    }

    /**
     * Generates currency type spinner
     *
     * @return category spinner
     */


    private Spinner createNewCategorySpinner() {
        final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, dpToPx(40));
        final Spinner spinner = new Spinner(this);

        if (categorySpinnerId > 1001) {
             /* Recursion disabled in version 1.0
                    TODO enable recursion in future versions
            lparams.addRule(RelativeLayout.BELOW, textViewRecursId-1);
             */
            lparams.addRule(RelativeLayout.BELOW, categorySpinnerId - 1);
        }

        spinner.setLayoutParams(lparams);
        ArrayAdapter<CharSequence> categorySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.report_activity_category_spinner, android.R.layout.simple_spinner_item);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(categorySpinnerAdapter);
        spinner.setId(categorySpinnerId);
        spinner.setSaveEnabled(true);

        if (findViewById(categorySpinnerId - 1) != null) {
            int prevPosition = ((Spinner) findViewById(categorySpinnerId - 1)).getSelectedItemPosition();
            if (prevPosition == 13) {
                spinner.setSelection(0);
            } else {
                spinner.setSelection(prevPosition + 1);
            }

        }
        categorySpinnerId++;
        return spinner;
    }

    /**
     * Generates operation input value edit text
     *
     * @return edit text field
     */


    private EditText createNewEditText() {
        final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, dpToPx(40));
        final EditText editText = new EditText(this);
        lparams.addRule(RelativeLayout.RIGHT_OF, categorySpinnerId - 1);

        if (categorySpinnerId > 1001) {
            /* Recursion disabled in version 1.0
                    TODO enable recursion in future versions
            lparams.addRule(RelativeLayout.BELOW, textViewRecursId-1);
             */
            lparams.addRule(RelativeLayout.BELOW, editTextValueId - 1);
        }

        int maxLength = 8;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        editText.setFilters(fArray);
        editText.setLayoutParams(lparams);
        editText.setHint(getResources().getString(R.string.value));
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setId(editTextValueId);
        editText.setSaveEnabled(true);
        editText.requestFocus();
        editTextValueId++;
        return editText;
    }

     /* Recursion disabled in version 1.0
                    TODO enable recursion in future versions
    //Generates recurs text view
    private TextView createNewRecursTextView(){
        final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, dpToPx(40));
        lparams.addRule(RelativeLayout.BELOW, categorySpinnerId - 1);
        lparams.setMargins(dpToPx(12),dpToPx(8),0,0);
        final TextView textView = new TextView(this);
        textView.setLayoutParams(lparams);
;       textView.setId(textViewRecursId);
        textView.setText(getResources().getString(R.string.recurs));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.recurs_text_size));
        textView.setSaveEnabled(true);
        textViewRecursId++;
        return textView;
    }
    */

    /* Recursion disabled in version 1.0
                   TODO enable recursion in future versions
   //Generates recurring spinner
   private Spinner createNewRecursSpinner() {
       final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, dpToPx(40));
       final Spinner spinner = new Spinner(this);
       lparams.addRule(RelativeLayout.END_OF, textViewRecursId-1);
       lparams.addRule(RelativeLayout.BELOW, categorySpinnerId - 1);
       lparams.addRule(RelativeLayout.ALIGN_RIGHT, currencySpinnerId - 1);

       spinner.setLayoutParams(lparams);
       ArrayAdapter<CharSequence> recursSpinnerAdapter = ArrayAdapter.createFromResource(this,R.array.report_activity_recurs_spinner,android.R.layout.simple_spinner_item);
       recursSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       spinner.setAdapter(recursSpinnerAdapter);
       spinner.setId(spinnerRecursId);
       spinner.setSaveEnabled(true);
       spinnerRecursId++;
       return spinner;
   }
   */
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
        if (id == R.id.report_toolbar_done) {
            if (validateFields()) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra("reportResult", getReportResult());
                setResult(RESULT_OK, intent);
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Validates editText fields
     *
     * @return false if field is empty or contains only zeros
     */
    private boolean validateFields() {
        int counter = categorySpinnerId - 1000;
        for (int i = 1; i < counter; i++) {
            EditText editTextValue = (EditText) findViewById(3000 + i);
            if (editTextValue.getText().toString().isEmpty()) {
                Log.d(TAG, i + "");
                editTextValue.requestFocus();
                editTextValue.setError(getString(R.string.set_value));

                return false;
            }
            if (editTextValue.getText().toString().matches("0+")) {
                editTextValue.requestFocus();
                editTextValue.setError(getString(R.string.set_non_zero_value));
                return false;
            }
        }
        return true;
    }

    /**
     * Compiles inputs from fields
     *
     * @return list of strings category-value-currency
     */

    private ArrayList<String> getReportResult() {
        ArrayList<String> list = new ArrayList<>();
        int counter = categorySpinnerId - 1000;
        for (int i = 1; i < counter; i++) {
            Spinner categorySpinner = (Spinner) findViewById(1000 + i);
            Spinner currencySpinner = (Spinner) findViewById(2000 + i);
             /* Recursion disabled in version 1.0
                    TODO enable recursion in future versions
            Spinner recursSpinner = (Spinner) findViewById(5000+i);
            */
            EditText editTextValue = (EditText) findViewById(3000 + i);


            list.add(categorySpinner.getSelectedItemPosition() + "-"
                    + categorySpinner.getSelectedItem().toString() + "-"
                    + editTextValue.getText().toString().replaceFirst("^0+(?!$)", "") + "-"
                    + currencySpinner.getSelectedItem().toString());
             /* Recursion disabled in version 1.0
                    TODO enable recursion in future versions
                        + recursSpinner.getSelectedItem().toString());
             */

        }

        return list;
    }


    /**
     * Converts dp to px
     *
     * @param dp density pixels
     * @return pixels
     */
    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    /**
     * Runs showcase presentation on fragment start
     */

    private void startShowcase() {
        if (addNew != null && findViewById(R.id.report_toolbar_done) != null) {
            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(500); // half second between each showcase view
            config.setDismissTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, TAG);
            sequence.setConfig(config);

            sequence.addSequenceItem(addNew, getString(R.string.data_add_new), getString(R.string.got_it));
            sequence.addSequenceItem(findViewById(R.id.report_toolbar_done), getString(R.string.data_done), getString(R.string.ok));
            sequence.start();
        }
    }


}
