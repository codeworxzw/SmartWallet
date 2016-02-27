package com.rbsoftware.pfm.personalfinancemanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import it.gmariotti.cardslib.library.view.CardViewNative;

public class EditDocument extends AppCompatActivity {
    private final String TAG = "EditDocument";
    private String docId;
    private ReportCard reportCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.report_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        }

        if (savedInstanceState == null) {
            docId = getIntent().getExtras().getString("docId");

        }
        CardViewNative mReportCardView = (CardViewNative) findViewById(R.id.report_card);
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
