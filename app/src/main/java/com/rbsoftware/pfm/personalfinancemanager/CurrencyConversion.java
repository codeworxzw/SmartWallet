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

public class CurrencyConversion  extends AsyncTask<String, String, String> {

    HttpURLConnection urlConnection;
    private Currency currency;
    private Double calcResult = 0.0;
    private final Context mContext;

    public CurrencyConversion(Context context, OnTaskCompleted listener) {
        this.mContext = context;
        this.listener = listener;
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
        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }

        return result.toString();
    }

    @Override
    protected void onPostExecute(String output) {

        currency = new Currency(output);
        /*if (MainActivity.financeDocumentModel.getDocument("CurrencyID") == null){
            MainActivity.financeDocumentModel.createDocument(currency);
        } else {
            Log.d("TAG", "Document exists");
            try {
                MainActivity.financeDocumentModel.updateDocument(currency);
            } catch (ConflictException e) {
                e.printStackTrace();
            }
        }*/
        listener.onTaskCompleted();
    } // protected void onPostExecute(Void v)


    /**
     *
     * @param in
     * @param curr
     * @param defaultCurr
     * @return calcResult
     */
    public Double getCalculation (Double in, String curr, String defaultCurr) {

        if (defaultCurr.equals("USD")){
            if(curr.equals("EUR")){calcResult = in*currency.getEURtoUSD();}
            if(curr.equals("USD")){calcResult = in;}
            if(curr.equals("RUR")){calcResult = in*currency.getRURtoUSD();}
            if(curr.equals("UAH")){calcResult = in*currency.getUAHtoUSD();}
        }

        if (defaultCurr.equals("EUR")){
            if(curr.equals("EUR")){calcResult = in;}
            if(curr.equals("USD")){calcResult = in*currency.getUSDtoEUR();}
            if(curr.equals("RUR")){calcResult = in*currency.getRURtoEUR();}
            if(curr.equals("UAH")){calcResult = in*currency.getUAHtoEUR();}
        }

        if (defaultCurr.equals("RUR")){
            if(curr.equals("EUR")){calcResult = in*currency.getEURtoRUR();}
            if(curr.equals("USD")){calcResult = in*currency.getUSDtoRUR();}
            if(curr.equals("RUR")){calcResult = in;}
            if(curr.equals("UAH")){calcResult = in*currency.getUAHtoRUR();}
        }

        if (defaultCurr.equals("UAH")){
            if(curr.equals("EUR")){calcResult = in*currency.getEURtoUAH();}
            if(curr.equals("USD")){calcResult = in*currency.getUSDtoUAH();}
            if(curr.equals("RUR")){calcResult = in*currency.getRURtoUAH();}
            if(curr.equals("UAH")){calcResult = in;}
        }
      return calcResult;
    }


    public interface OnTaskCompleted{
        void onTaskCompleted();
    }


    private OnTaskCompleted listener;


} //class MyAsyncTask extends AsyncTask<String, String, Void>
