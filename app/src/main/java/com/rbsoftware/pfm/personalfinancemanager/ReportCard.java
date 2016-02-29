package com.rbsoftware.pfm.personalfinancemanager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Holds methods for edit and report layout and operations
 *
 * @author Roman Burzakovskiy
 */
public class ReportCard {
    private final static String TAG = "ReportLayout";
    Context mContext;
    private Bundle savedInstanceState;
    private Button addNew;
    private RelativeLayout mLayout;
    private Activity mActivity;
    private int categorySpinnerId = 1001; //IDs of categorySpinner
    private int currencySpinnerId = 2001; //IDs of currencySpinner
    private int editTextValueId = 3001;   //Ids of editText
    private int deleteButtonId = 4001;   //Ids of deleteButton
    private int buttonCounter; //counter to make button "Add Line" invisible

    private FinanceDocument mFinanceDocument;

    public ReportCard(Context context) {
        this.mContext = context;
    }


    public void setup(Activity activity, Bundle savedInstanceState, String docId) {
        this.mActivity = activity;
        this.savedInstanceState = savedInstanceState;
        if (docId == null) {
            mFinanceDocument = null;
        } else {
            mFinanceDocument = MainActivity.financeDocumentModel.getFinanceDocument(docId);
        }

        createLayout();
    }

    /**
     * Gets getCategorySpinnerId value
     *
     * @return int getCategorySpinnerId
     */
    public int getCategorySpinnerId() {
        return categorySpinnerId;
    }

    /**
     * Gets buttonCounter value
     *
     * @return int buttonCounter
     */
    public int getButtonCounter() {
        return buttonCounter;
    }

    /**
     * Prepares layout for onSaveInstanceState
     *
     * @return Bundle of layout state
     */
    public Bundle getElementsToSave() {
        Bundle outState = new Bundle();
        int counter = this.getCategorySpinnerId() - 1000;
        outState.putInt("counter", counter);
        outState.putInt("buttonCounter", this.getButtonCounter());
        for (int i = 1; i < counter; i++) {
            Spinner categorySpinner = (Spinner) mActivity.findViewById(1000 + i);
            outState.putInt("categorySpinner" + i, categorySpinner.getSelectedItemPosition());
            Spinner currencySpinner = (Spinner) mActivity.findViewById(2000 + i);
            outState.putInt("currencySpinner" + i, currencySpinner.getSelectedItemPosition());
            EditText editTextValue = (EditText) mActivity.findViewById(3000 + i);
            outState.putString("editTextValue" + i, editTextValue.getText().toString());
        }
        return outState;
    }


    /**
     * Validates editText fields
     *
     * @return false if field is empty or contains only zeros
     */
    public boolean validateFields() {
        int counter = this.getCategorySpinnerId() - 1000;
        for (int i = 1; i < counter; i++) {
            EditText editTextValue = (EditText) mActivity.findViewById(3000 + i);
            if (editTextValue.getText().toString().isEmpty()) {
                editTextValue.requestFocus();
                Toast.makeText(mContext, mContext.getString(R.string.set_value), Toast.LENGTH_LONG).show();

                return false;
            }
            if (editTextValue.getText().toString().matches("0+")) {
                editTextValue.requestFocus();
                Toast.makeText(mContext, mContext.getString(R.string.set_non_zero_value), Toast.LENGTH_LONG).show();

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
    public boolean validateSpinner() {
        int counter = this.getCategorySpinnerId() - 1000;
        for (int i = counter - 1; i > 1; i--) {
            for (int j = i - 1; j >= 1; j--) {
                if (i != j && ((Spinner) mActivity.findViewById(1000 + i)).getSelectedItem().equals(((Spinner) mActivity.findViewById(1000 + j)).getSelectedItem())) {

                    Toast.makeText(mContext, mContext.getString(R.string.duplicate_entry) + " : " + ((Spinner) mActivity.findViewById(1000 + i)).getSelectedItem().toString(), Toast.LENGTH_LONG).show();

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

    public ArrayList<String> getReportResult() {
        ArrayList<String> list = new ArrayList<>();
        int counter = this.getCategorySpinnerId() - 1000;
        for (int i = 1; i < counter; i++) {
            Spinner categorySpinner = (Spinner) mActivity.findViewById(1000 + i);
            Spinner currencySpinner = (Spinner) mActivity.findViewById(2000 + i);
            EditText editTextValue = (EditText) mActivity.findViewById(3000 + i);


            list.add(categorySpinner.getSelectedItemPosition() + "-"
                    + categorySpinner.getSelectedItem().toString() + "-"
                    + editTextValue.getText().toString().replaceFirst("^0+(?!$)", "") + "-"
                    + currencySpinner.getSelectedItem().toString());

        }

        return list;
    }

    /**
     * Generates inner card layout
     */
    private void createLayout() {
        final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);


        mLayout = (RelativeLayout) mActivity.findViewById(R.id.report_item_layout);
        TextView mTextViewDate = (TextView) mActivity.findViewById(R.id.textViewDate);
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        DateFormat sdf = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        mTextViewDate.setText(sdf.format(c.getTimeInMillis()));

        if (savedInstanceState == null) { //prepare initial layout for report
            if (mFinanceDocument == null) {
                mLayout.addView(createNewCategorySpinner());
                mLayout.addView(createNewEditText());
                mLayout.addView(createNewCurrencySpinner());
                mLayout.addView(createNewDeleteButton());
                buttonCounter = 0;
            } else {   //prepare initial layout for edit
                buttonCounter = 0;
                for (int i = 1; i <= FinanceDocument.NUMBER_OF_CATEGORIES; i++) {
                    List<String> value = mFinanceDocument.getValuesMap().get(i);
                    if (value != null) {
                        mLayout.addView(createNewCategorySpinner());
                        mLayout.addView(createNewEditText());
                        mLayout.addView(createNewCurrencySpinner());
                        mLayout.addView(createNewDeleteButton());
                        ((Spinner) mActivity.findViewById(categorySpinnerId - 1)).setSelection(i - 1);
                        ((EditText) mActivity.findViewById(editTextValueId - 1)).setText(value.get(0));
                        ((Spinner) mActivity.findViewById(currencySpinnerId - 1)).setSelection(Utils.getCurrencyPosition(value.get(1)));
                        buttonCounter++;
                    }
                }
            }

        } else {
            //views states were saved in onSaveInstanceState
            int counter = savedInstanceState.getInt("counter");
            for (int i = 1; i < counter; i++) {
                mLayout.addView(createNewCategorySpinner());
                Spinner categorySpinner = (Spinner) mActivity.findViewById(1000 + i);
                categorySpinner.setSelection(savedInstanceState.getInt("categorySpinner" + i));
                mLayout.addView(createNewEditText());
                EditText editTextValue = (EditText) mActivity.findViewById(3000 + i);
                editTextValue.setText(savedInstanceState.getString("editTextValue" + i));
                mLayout.addView(createNewCurrencySpinner());
                Spinner currencySpinner = (Spinner) mActivity.findViewById(2000 + i);
                currencySpinner.setSelection(savedInstanceState.getInt("currencySpinner" + i));
                mLayout.addView(createNewDeleteButton());
                buttonCounter = savedInstanceState.getInt("buttonCounter");

            }

        }
        addNew = (Button) mActivity.findViewById(R.id.btn_add_new);

        //hide addNew button if all fields called
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


    /**
     * Generates operation category spinner
     *
     * @return currency spinner
     */

    private Spinner createNewCurrencySpinner() {
        final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, Utils.dpToPx(mContext, 40));
        final Spinner spinner = new Spinner(mContext);
        int position = 0;
        lparams.addRule(RelativeLayout.RIGHT_OF, editTextValueId - 1);

        if (categorySpinnerId > 1001) {

            lparams.addRule(RelativeLayout.BELOW, currencySpinnerId - 1);
        }

        spinner.setLayoutParams(lparams);
        ArrayAdapter<CharSequence> currencySpinnerAdapter = ArrayAdapter.createFromResource(mContext, R.array.report_activity_currency_spinner, android.R.layout.simple_spinner_item);
        currencySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String[] currencyList = mContext.getResources().getStringArray(R.array.report_activity_currency_spinner);
        for (int i = 0; i < currencyList.length; i++) {
            if (currencyList[i].equals(MainActivity.defaultCurrency)) {
                position = i;
            }
        }
        spinner.setAdapter(currencySpinnerAdapter);
        spinner.setSelection(position);
        //noinspection ResourceType
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
        final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, Utils.dpToPx(mContext, 40));
        final Spinner spinner = new Spinner(mContext);

        if (categorySpinnerId > 1001) {

            lparams.addRule(RelativeLayout.BELOW, categorySpinnerId - 1);
        }

        spinner.setLayoutParams(lparams);
        ArrayAdapter<CharSequence> categorySpinnerAdapter = ArrayAdapter.createFromResource(mContext, R.array.report_activity_category_spinner, android.R.layout.simple_spinner_item);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(categorySpinnerAdapter);
        //noinspection ResourceType
        spinner.setId(categorySpinnerId);
        spinner.setSaveEnabled(true);

        if (mActivity.findViewById(categorySpinnerId - 1) != null) {
            int prevPosition = ((Spinner) mActivity.findViewById(categorySpinnerId - 1)).getSelectedItemPosition();
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
        final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, Utils.dpToPx(mContext, 40));
        final EditText editText = new EditText(mContext);
        lparams.addRule(RelativeLayout.RIGHT_OF, categorySpinnerId - 1);

        if (categorySpinnerId > 1001) {

            lparams.addRule(RelativeLayout.BELOW, editTextValueId - 1);
        }

        int maxLength = 8;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        editText.setFilters(fArray);
        editText.setLayoutParams(lparams);
        editText.setHint(mContext.getResources().getString(R.string.value));
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        //noinspection ResourceType
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
        final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, Utils.dpToPx(mContext, 40));
        final ImageButton deleteButton = new ImageButton(mContext);
        lparams.addRule(RelativeLayout.RIGHT_OF, currencySpinnerId - 1);
        deleteButton.setVisibility(View.INVISIBLE);
        if (deleteButtonId > 4001) {
            lparams.addRule(RelativeLayout.BELOW, deleteButtonId - 1);
            deleteButton.setVisibility(View.VISIBLE);
            mActivity.findViewById(deleteButtonId - 1).setVisibility(View.INVISIBLE);
        }

        deleteButton.setLayoutParams(lparams);
        deleteButton.setBackgroundColor(Color.TRANSPARENT);
        deleteButton.setImageResource(R.drawable.ic_remove_grey_24dp);
        //noinspection ResourceType
        deleteButton.setId(deleteButtonId);
        deleteButtonId++;
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteButtonId - 2 != 4001) {
                    mActivity.findViewById(deleteButtonId - 2).setVisibility(View.VISIBLE);
                }
                mLayout.removeView(mActivity.findViewById(categorySpinnerId - 1));
                mLayout.removeView(mActivity.findViewById(currencySpinnerId - 1));
                mLayout.removeView(mActivity.findViewById(editTextValueId - 1));
                mLayout.removeView(mActivity.findViewById(deleteButtonId - 1));
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
