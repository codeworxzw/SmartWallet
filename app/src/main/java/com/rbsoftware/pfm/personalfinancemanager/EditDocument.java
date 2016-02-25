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

import it.gmariotti.cardslib.library.view.CardViewNative;

public class EditDocument extends AppCompatActivity {
    private final String TAG = "EditDocument";
    private String docId;
    private CardViewNative mReportCardView;
    private ReportCard reportCard;

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

        if (savedInstanceState == null) {
            docId = getIntent().getExtras().getString("docId");

        }
        mReportCardView = (CardViewNative) findViewById(R.id.report_card);
        reportCard = new ReportCard(this, savedInstanceState, docId);
        mReportCardView.setCard(reportCard);


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
            if (reportCard.validateFields() && reportCard.validateSpinner()) {
                Intent intent = new Intent();
                intent.putExtra("oldDocId", docId);
                intent.putStringArrayListExtra("editResult", reportCard.getReportResult());
                setResult(RESULT_OK, intent);
                finish();
            }

        }

        return super.onOptionsItemSelected(item);
    }






    @Override
    public void onSaveInstanceState(Bundle outState) {
       outState.putAll(reportCard.getElementsToSave());

        super.onSaveInstanceState(outState);

    }


}
