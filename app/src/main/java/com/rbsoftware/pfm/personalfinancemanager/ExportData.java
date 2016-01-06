package com.rbsoftware.pfm.personalfinancemanager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by burzakovskiy on 1/6/2016.
 */
public class ExportData {

    public static void exportHistoryAsCsv(Context mContext, FinanceDocument document) throws IOException {
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String fileName = "doc-"+ df.format(calendar.getTimeInMillis())+".csv";

        String filePath = baseDir + File.separator + fileName;
        File historyFile = new File(filePath );
        CSVWriter writer;
// File exist
        if(historyFile.exists() && !historyFile.isDirectory()){
            FileWriter mFileWriter = new FileWriter(filePath , true);
            writer = new CSVWriter(mFileWriter);
        }
        else {
            writer = new CSVWriter(new FileWriter(filePath));
        }
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{mContext.getResources().getString(R.string.document_date), document.getNormalDate()});
        data.add(new String[]{"", ""});
        for(int i=1;i<=document.getValuesMap().size(); i++){
            int value = document.getValuesMap().get(i);
            if(value != 0){
              data.add(new String[] {keyToString(mContext, i), Integer.toString(value)});
            }
        }
        writer.writeAll(data);

        writer.close();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(historyFile));
        sendIntent.setType("text/comma-separated-values");
        mContext.startActivity(Intent.createChooser(sendIntent, "share"));
    }



    public static void exportSummaryAsCsv(Context mContext, List<String[]> inputData) throws IOException {
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

        String fileName = "summary-"+ df.format(calendar.getTimeInMillis())+".csv";
        String filePath = baseDir + File.separator + fileName;
        File summaryFile = new File(filePath );
        CSVWriter writer;
// File exist
        if(summaryFile.exists() && !summaryFile.isDirectory()){
            FileWriter mFileWriter = new FileWriter(filePath , true);
            writer = new CSVWriter(mFileWriter);
        }
        else {
            writer = new CSVWriter(new FileWriter(filePath));
        }

        writer.writeAll(inputData);

        writer.close();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(summaryFile));
        sendIntent.setType("text/comma-separated-values");
        mContext.startActivity(Intent.createChooser(sendIntent, "share"));
    }

    /* Converts int key to human readable string
        * @param key value range 1-14
        * @return string value
        */
    private static String keyToString(Context mContext, int key){
        switch (key){
            case 1: return mContext.getResources().getString(R.string.salary);
            case 2: return mContext.getResources().getString(R.string.rental_income);
            case 3: return mContext.getResources().getString(R.string.interest);
            case 4: return mContext.getResources().getString(R.string.gifts);
            case 5: return mContext.getResources().getString(R.string.other_income);
            case 6: return mContext.getResources().getString(R.string.taxes);
            case 7: return mContext.getResources().getString(R.string.mortgage);
            case 8: return mContext.getResources().getString(R.string.credit_card);
            case 9: return mContext.getResources().getString(R.string.utilities);
            case 10: return mContext.getResources().getString(R.string.food);
            case 11: return mContext.getResources().getString(R.string.car_payment);
            case 12: return mContext.getResources().getString(R.string.personal);
            case 13: return mContext.getResources().getString(R.string.activities);
            case 14: return mContext.getResources().getString(R.string.other_expense);
        }
        return "";
    }
}
