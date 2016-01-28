package com.rbsoftware.pfm.personalfinancemanager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cloudant.sync.datastore.ConflictException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class CurrencyConversion extends AsyncTask<String, String, String> {
    private static final String TAG = "CurrencyConvertion";
    HttpURLConnection urlConnection;
    private Currency currency;
    private Context mContext;

    public CurrencyConversion(Context context) {

        mContext = context;
    }

    @Override
    protected String doInBackground(String... params) {

        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL("https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=3");
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return result.toString();
    }

    @Override
    protected void onPostExecute(String output) {
        currency = new Currency(output);
        Calendar c = Calendar.getInstance(TimeZone.getDefault());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String currentDate = df.format(c.getTime());

        if (MainActivity.financeDocumentModel.getCurrencyDocument(FinanceDocumentModel.CURRENCY_ID) == null) {
            MainActivity.financeDocumentModel.createDocument(currency);
            Log.d(TAG, "Currency document was created successfully");
        } else {

            try {
                MainActivity.financeDocumentModel.updateCurrencyDocument(MainActivity.financeDocumentModel.getCurrencyDocument(FinanceDocumentModel.CURRENCY_ID), currency);
                Log.d(TAG, "Currency rates were updated successfully");
            } catch (ConflictException e) {
                e.printStackTrace();
            }
        }
        MainActivity.saveToSharedPreferences(mContext, "updatedDate", currentDate);

    } // protected void onPostExecute(Void v)


    /**
     * Static method for currency convertion
     *
     * @param in          input value
     * @param curr        input currency
     * @param defaultCurr default currency
     * @return converted to default currency value
     */
    public static int convertCurrency(int in, String curr, String defaultCurr) {
        Double calcResult = (double) in;
        Currency convCurr = MainActivity.financeDocumentModel.getCurrencyDocument(FinanceDocumentModel.CURRENCY_ID);
        if (defaultCurr.equals("USD")) {
            if (curr.equals("EUR")) {
                calcResult = in * convCurr.getEURtoUSD();
            }
            if (curr.equals("USD")) {
                calcResult = (double) in;
            }
            if (curr.equals("RUB")) {
                calcResult = in * convCurr.getRUBtoUSD();
            }
            if (curr.equals("UAH")) {
                calcResult = in * convCurr.getUAHtoUSD();
            }
        }

        if (defaultCurr.equals("EUR")) {
            if (curr.equals("EUR")) {
                calcResult = (double) in;
            }
            if (curr.equals("USD")) {
                calcResult = in * convCurr.getUSDtoEUR();
            }
            if (curr.equals("RUB")) {
                calcResult = in * convCurr.getRUBtoEUR();
            }
            if (curr.equals("UAH")) {
                calcResult = in * convCurr.getUAHtoEUR();
            }
        }

        if (defaultCurr.equals("RUB")) {
            if (curr.equals("EUR")) {
                calcResult = in * convCurr.getEURtoRUB();
            }
            if (curr.equals("USD")) {
                calcResult = in * convCurr.getUSDtoRUB();
            }
            if (curr.equals("RUB")) {
                calcResult = (double) in;
            }
            if (curr.equals("UAH")) {
                calcResult = in * convCurr.getUAHtoRUB();
            }
        }

        if (defaultCurr.equals("UAH")) {
            if (curr.equals("EUR")) {
                calcResult = in * convCurr.getEURtoUAH();
            }
            if (curr.equals("USD")) {
                calcResult = in * convCurr.getUSDtoUAH();
            }
            if (curr.equals("RUB")) {
                calcResult = in * convCurr.getRUBtoUAH();
            }
            if (curr.equals("UAH")) {
                calcResult = (double) in;
            }
        }
        return (int) Math.round(calcResult);
    }


} //class MyAsyncTask extends AsyncTask<String, String, Void>
