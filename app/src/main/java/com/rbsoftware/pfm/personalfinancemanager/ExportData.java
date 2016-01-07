package com.rbsoftware.pfm.personalfinancemanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by burzakovskiy on 1/6/2016.
 */
public class ExportData {

    //Static method to export history data
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


    //Static method to export account summary data
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

    //Static method to export charts data
    public static void exportChartAsPng(Context mContext, View view) throws IOException {
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

        String fileName = "chart-"+ df.format(calendar.getTimeInMillis())+".png";
        String filePath = baseDir + File.separator + fileName;
        File chartFile = new File(filePath );

        OutputStream outStream =new FileOutputStream(chartFile);
        getBitmapFromView(view).compress(Bitmap.CompressFormat.PNG,100, outStream);
        outStream.flush();
        outStream.close();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(chartFile));
        sendIntent.setType("image/png");
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


    //Converts view into bitmap
    private static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }
}
