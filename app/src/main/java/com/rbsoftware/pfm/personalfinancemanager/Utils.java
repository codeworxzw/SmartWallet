package com.rbsoftware.pfm.personalfinancemanager;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

import java.util.ArrayList;

/**
 * Holds various helper methods
 *
 * @author Roman Burzakovskiy
 */
public class Utils {

    /**
     * Converts int key to human readable string
     *
     * @param mContext application context
     * @param key value range 1-FinanceDocument.NUMBER_OF_CATEGORIES
     * @return string value
     */
    public static String keyToString(Context mContext, int key) {
        switch (key) {
            case 1:
                return mContext.getResources().getString(R.string.salary);
            case 2:
                return mContext.getResources().getString(R.string.rental_income);
            case 3:
                return mContext.getResources().getString(R.string.interest);
            case 4:
                return mContext.getResources().getString(R.string.gifts);
            case 5:
                return mContext.getResources().getString(R.string.other_income);
            case 6:
                return mContext.getResources().getString(R.string.taxes);
            case 7:
                return mContext.getResources().getString(R.string.mortgage);
            case 8:
                return mContext.getResources().getString(R.string.credit_card);
            case 9:
                return mContext.getResources().getString(R.string.utilities);
            case 10:
                return mContext.getResources().getString(R.string.food);
            case 11:
                return mContext.getResources().getString(R.string.car_payment);
            case 12:
                return mContext.getResources().getString(R.string.personal);
            case 13:
                return mContext.getResources().getString(R.string.activities);
            case 14:
                return mContext.getResources().getString(R.string.other_expense);
        }
        return "";
    }

    /**
     * Converts dp to px
     *
     * @param mContext application context
     * @param dp density pixels
     * @return pixels
     */
    public static int dpToPx(Context mContext, int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    /**
     * Gets currency position in array
     *
     * @param currency currency string
     * @return currency position in array
     */
    public static int getCurrencyPosition(String currency) {
        switch (currency) {
            case "USD":
                return 0;
            case "EUR":
                return 1;
            case "UAH":
                return 2;
            case "RUB":
                return 3;
            default:
                return 0;
        }
    }


    /**
     * Parsing string to retrieve document data
     */
    public static ArrayList<String> getItem(ArrayList<String> reportResult, int i) {
        ArrayList<String> item = new ArrayList<>();
        item.add(0, "0");
        item.add(1, MainActivity.defaultCurrency);
        item.add(2, "Never");
        for (String listItem : reportResult) {
            String[] parts = listItem.split("-");
            int position = Integer.valueOf(parts[0]);
            if (i == position) {
                item.clear();
                item.add(0, parts[2]);
                item.add(1, parts[3]);
                /* Recursion disabled in version 1.0
                    TODO enable recursion in future versions
                item.add(2,parts[4]);
                */
                item.add(2, "Never");
            }


        }

        return item;
    }

    /**
     * Gets color by data type key
     *
     * @param i data type key
     * @return color
     */
    public static int getColorPalette(Context mContext, int i) {
        switch (i) {
            case 1:
                return ContextCompat.getColor(mContext, R.color.salary);
            case 2:
                return ContextCompat.getColor(mContext, R.color.rental_income);
            case 3:
                return ContextCompat.getColor(mContext, R.color.interest);
            case 4:
                return ContextCompat.getColor(mContext, R.color.gifts);
            case 5:
                return ContextCompat.getColor(mContext, R.color.other_income);
            case 6:
                return ContextCompat.getColor(mContext, R.color.taxes);
            case 7:
                return ContextCompat.getColor(mContext, R.color.mortgage);
            case 8:
                return ContextCompat.getColor(mContext, R.color.credit_card);
            case 9:
                return ContextCompat.getColor(mContext, R.color.utilities);
            case 10:
                return ContextCompat.getColor(mContext, R.color.food);
            case 11:
                return ContextCompat.getColor(mContext, R.color.car_payment);
            case 12:
                return ContextCompat.getColor(mContext, R.color.personal);
            case 13:
                return ContextCompat.getColor(mContext, R.color.activities);
            case 14:
                return ContextCompat.getColor(mContext, R.color.other_expense);
            default:
                return Color.WHITE;

        }

    }
}
