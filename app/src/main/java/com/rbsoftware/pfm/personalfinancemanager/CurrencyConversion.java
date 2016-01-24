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

public class CurrencyConversion extends AsyncTask<String, String, String> {
    private static final String TAG = "CurrencyConvertion";
    HttpURLConnection urlConnection;
    private Currency currency;


    public CurrencyConversion() {


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
        //TODO check if
        MainActivity.financeDocumentModel.createDocument(currency);

    } // protected void onPostExecute(Void v)


    /**
     * @param in
     * @param curr
     * @param defaultCurr
     * @return calcResult
     */
    public static String convertCurrency(int in, String curr, String defaultCurr) {
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
                calcResult = in * convCurr.getRURtoUSD();
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
                calcResult = in * convCurr.getRURtoEUR();
            }
            if (curr.equals("UAH")) {
                calcResult = in * convCurr.getUAHtoEUR();
            }
        }

        if (defaultCurr.equals("RUB")) {
            if (curr.equals("EUR")) {
                calcResult = in * convCurr.getEURtoRUR();
            }
            if (curr.equals("USD")) {
                calcResult = in * convCurr.getUSDtoRUR();
            }
            if (curr.equals("RUB")) {
                calcResult = (double) in;
            }
            if (curr.equals("UAH")) {
                calcResult = in * convCurr.getUAHtoRUR();
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
                calcResult = in * convCurr.getRURtoUAH();
            }
            if (curr.equals("UAH")) {
                calcResult = (double) in;
            }
        }
        return Integer.toString(calcResult.intValue());
    }


} //class MyAsyncTask extends AsyncTask<String, String, Void>
