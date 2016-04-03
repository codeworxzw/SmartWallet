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
    private static final String TAG = "CurrencyConversion";
    private HttpURLConnection urlConnection;
    private final Context mContext;

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
        Currency currency = new Currency(output);
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
     * Static method for currency conversion
     *
     * @param in          input value
     * @param curr        input currency
     * @param defaultCurr default currency
     * @return converted to default currency value
     */
    public static int convertCurrency(int in, String curr, String defaultCurr) {
        Double calcResult;
        Currency convCurr = MainActivity.financeDocumentModel.getCurrencyDocument(FinanceDocumentModel.CURRENCY_ID);
        switch (defaultCurr) {
            case "USD": {
                switch (curr) {
                    case "EUR": {
                        calcResult = in * convCurr.getEURtoUSD();
                        return (int) Math.round(calcResult);
                    }
                    case "USD": {
                        calcResult = (double) in;
                        return (int) Math.round(calcResult);
                    }
                    case "RUB": {
                        calcResult = in * convCurr.getRUBtoUSD();
                        return (int) Math.round(calcResult);
                    }
                    case "UAH": {
                        calcResult = in * convCurr.getUAHtoUSD();
                        return (int) Math.round(calcResult);
                    }
                    case "BTC": {
                        calcResult = in * convCurr.getBTCtoUSD();
                        return (int) Math.round(calcResult);
                    }
                }
            }

            case "EUR": {
                switch (curr) {
                    case "EUR": {
                        calcResult = (double) in;
                        return (int) Math.round(calcResult);
                    }
                    case "USD": {
                        calcResult = in * convCurr.getUSDtoEUR();
                        return (int) Math.round(calcResult);
                    }
                    case "RUB": {
                        calcResult = in * convCurr.getRUBtoEUR();
                        return (int) Math.round(calcResult);
                    }
                    case "UAH": {
                        calcResult = in * convCurr.getUAHtoEUR();
                        return (int) Math.round(calcResult);
                    }
                    case "BTC": {
                        calcResult = in * convCurr.getBTCtoEUR();
                        return (int) Math.round(calcResult);
                    }
                }
            }

            case "RUB": {
                switch (curr) {
                    case "EUR": {
                        calcResult = in * convCurr.getEURtoRUB();
                        return (int) Math.round(calcResult);
                    }
                    case "USD": {
                        calcResult = in * convCurr.getUSDtoRUB();
                        return (int) Math.round(calcResult);
                    }
                    case "RUB": {
                        calcResult = (double) in;
                        return (int) Math.round(calcResult);
                    }
                    case "UAH": {
                        calcResult = in * convCurr.getUAHtoRUB();
                        return (int) Math.round(calcResult);
                    }
                    case "BTC": {
                        calcResult = in * convCurr.getBTCtoRUB();
                        return (int) Math.round(calcResult);
                    }
                }
            }

            case "UAH": {
                switch (curr) {
                    case "EUR": {
                        calcResult = in * convCurr.getEURtoUAH();
                        return (int) Math.round(calcResult);
                    }
                    case "USD": {
                        calcResult = in * convCurr.getUSDtoUAH();
                        return (int) Math.round(calcResult);
                    }
                    case "RUB": {
                        calcResult = in * convCurr.getRUBtoUAH();
                        return (int) Math.round(calcResult);
                    }
                    case "UAH": {
                        calcResult = (double) in;
                        return (int) Math.round(calcResult);
                    }
                    case "BTC": {
                        calcResult = in * convCurr.getBTCtoUAH();
                        return (int) Math.round(calcResult);
                    }
                }
            }

            case "BTC": {
                switch (curr) {
                    case "EUR": {
                        calcResult = in * convCurr.getEURtoBTC();
                        return (int) Math.round(calcResult);
                    }
                    case "USD": {
                        calcResult = in * convCurr.getUSDtoBTC();
                        return (int) Math.round(calcResult);
                    }
                    case "RUB": {
                        calcResult = in * convCurr.getRUBtoBTC();
                        return (int) Math.round(calcResult);
                    }
                    case "BTC": {
                        calcResult = (double) in;
                        return (int) Math.round(calcResult);
                    }
                    case "UAH": {
                        calcResult = in * convCurr.getUAHtoBTC();
                        return (int) Math.round(calcResult);
                    }
                }
            }
            default:
                return in;
        }

    }


} //class MyAsyncTask extends AsyncTask<String, String, Void>
