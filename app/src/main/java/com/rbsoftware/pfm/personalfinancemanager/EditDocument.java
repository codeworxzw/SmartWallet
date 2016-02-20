package com.rbsoftware.pfm.personalfinancemanager;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EditDocument extends AppCompatActivity {
    private final String TAG = "EditDocument";
    private String docId;
    private RelativeLayout mLayout;
    private Button addNew;
    private int categorySpinnerId = 1001; //IDs of categorySpinner
    private int currencySpinnerId = 2001; //IDs of currencySpinner
    private int editTextValueId = 3001;   //Ids of editText
    private int deleteButtonId = 4001;   //Ids of deleteButton
    private int buttonCounter; //counter to make button "Add Line" invisible

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.report_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() !=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        mLayout = (RelativeLayout) findViewById(R.id.report_item_layout);

        if (savedInstanceState == null) {
            docId = getIntent().getExtras().getString("docId");
            FinanceDocument financeDocument = MainActivity.financeDocumentModel.getFinanceDocument(docId);
            List<String> value;

            for (int i = 1; i <= FinanceDocument.NUMBER_OF_CATEGORIES; i++) {
                value = financeDocument.getValuesMap().get(i);
                if (value != null) {
                    mLayout.addView(createNewCategorySpinner());
                    mLayout.addView(createNewEditText());
                    mLayout.addView(createNewCurrencySpinner());
                    mLayout.addView(createNewDeleteButton());
                    ((Spinner) findViewById(categorySpinnerId - 1)).setSelection(i-1);
                    ((EditText) findViewById(editTextValueId - 1)).setText(value.get(0));
                    ((Spinner) findViewById(currencySpinnerId - 1)).setSelection(Utils.getCurrencyPosition(value.get(1)));
                }
            }
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
                mLayout.addView(createNewDeleteButton());
            }
        }

        addNew = (Button) findViewById(R.id.btn_add_new);
        if (savedInstanceState != null) {
            buttonCounter = savedInstanceState.getInt("buttonCounter");
        } else {
            buttonCounter = categorySpinnerId - 1002;
        }

        if (buttonCounter >= FinanceDocument.NUMBER_OF_CATEGORIES - 1) {
            addNew.setVisibility(View.GONE);
        }
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayout.addView(createNewCategorySpinner());
                mLayout.addView(createNewEditText());
                mLayout.addView(createNewCurrencySpinner());
                mLayout.addView(createNewDeleteButton());
                buttonCounter++;

                if (buttonCounter >= FinanceDocument.NUMBER_OF_CATEGORIES - 1) {
                    addNew.setVisibility(View.GONE);
                }


            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        if (id == R.id.report_toolbar_done) {
            if (validateFields() && validateSpinner()) {
                Intent intent = new Intent();
                intent.putExtra("oldDocId", docId);
                intent.putStringArrayListExtra("editResult", getEditResult());
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
                editTextValue.requestFocus();
//                editTextValue.setError(getString(R.string.set_value));
                Toast.makeText(this, getString(R.string.set_value), Toast.LENGTH_LONG).show();

                return false;
            }
            if (editTextValue.getText().toString().matches("0+")) {
                editTextValue.requestFocus();
//                editTextValue.setError(getString(R.string.set_non_zero_value));
                Toast.makeText(this, getString(R.string.set_non_zero_value), Toast.LENGTH_LONG).show();

                return false;
            }
        }
        return true;
    }


    /**
     * Validates spinners
     *
     * @return false if category is duplicated
     */
    private boolean validateSpinner() {
        int counter = categorySpinnerId - 1000;
        for (int i = counter - 1; i > 1; i--) {
            for (int j = i - 1; j >= 1; j--) {
                if (i != j && ((Spinner) findViewById(1000 + i)).getSelectedItem().equals(((Spinner) findViewById(1000 + j)).getSelectedItem())) {

                    Toast.makeText(this, getString(R.string.duplicate_entry) + " : " + ((Spinner) findViewById(1000 + i)).getSelectedItem().toString(), Toast.LENGTH_LONG).show();

                    return false;

                }

            }
        }

        return true;
    }

    /**
     * Compiles inputs from fields
     *
     * @return list of strings category-value-currency
     */

    private ArrayList<String> getEditResult() {
        ArrayList<String> list = new ArrayList<>();
        int counter = categorySpinnerId - 1000;
        for (int i = 1; i < counter; i++) {
            Spinner categorySpinner = (Spinner) findViewById(1000 + i);
            Spinner currencySpinner = (Spinner) findViewById(2000 + i);

            EditText editTextValue = (EditText) findViewById(3000 + i);


            list.add(categorySpinner.getSelectedItemPosition() + "-"
                    + categorySpinner.getSelectedItem().toString() + "-"
                    + editTextValue.getText().toString().replaceFirst("^0+(?!$)", "") + "-"
                    + currencySpinner.getSelectedItem().toString());


        }

        return list;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        int counter = categorySpinnerId - 1000;
        outState.putInt("counter", counter);
        outState.putInt("buttonCounter", buttonCounter);
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
        final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, Utils.dpToPx(getApplicationContext(), 40));
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
        final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, Utils.dpToPx(getApplicationContext(), 40));
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
        final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, Utils.dpToPx(getApplicationContext(), 40));
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

    /**
     * Generates delete button
     *
     * @return delete button
     */
    private ImageButton createNewDeleteButton() {
        final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, Utils.dpToPx(getApplicationContext(), 40));
        final ImageButton deleteButton = new ImageButton(this);
        lparams.addRule(RelativeLayout.RIGHT_OF, currencySpinnerId - 1);
        deleteButton.setVisibility(View.INVISIBLE);
        if (deleteButtonId > 4001) {
            lparams.addRule(RelativeLayout.BELOW, deleteButtonId - 1);
            deleteButton.setVisibility(View.VISIBLE);
            findViewById(deleteButtonId - 1).setVisibility(View.INVISIBLE);
        }

        deleteButton.setLayoutParams(lparams);
        deleteButton.setBackgroundColor(Color.TRANSPARENT);
        deleteButton.setImageResource(R.drawable.ic_remove_grey_24dp);
        deleteButton.setId(deleteButtonId);
        deleteButtonId++;
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteButtonId - 2 != 4001) {
                    findViewById(deleteButtonId - 2).setVisibility(View.VISIBLE);
                }
                mLayout.removeView(findViewById(categorySpinnerId - 1));
                mLayout.removeView(findViewById(currencySpinnerId - 1));
                mLayout.removeView(findViewById(editTextValueId - 1));
                mLayout.removeView(findViewById(deleteButtonId - 1));
                categorySpinnerId--;
                currencySpinnerId--;
                editTextValueId--;
                deleteButtonId--;
                buttonCounter--;
                if (buttonCounter < FinanceDocument.NUMBER_OF_CATEGORIES - 1)
                    addNew.setVisibility(View.VISIBLE);
            }
        });

        return deleteButton;
    }
}
