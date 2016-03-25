package com.rbsoftware.pfm.personalfinancemanager.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.rbsoftware.pfm.personalfinancemanager.MainActivity;
import com.rbsoftware.pfm.personalfinancemanager.R;

import java.util.ArrayList;

/**
 * Holds various helper methods
 *
 * @author Roman Burzakovskiy
 */
public class Utils {


    /**
     * Detects if application runs on tablet
     *
     * @param context app context
     * @return true if app runs on tablet, false if on phone
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * Converts int key to human readable string
     *
     * @param mContext application context
     * @param key      value range 1-FinanceDocument.NUMBER_OF_CATEGORIES
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
     * @param dp       density pixels
     * @return pixels
     */
    public static int dpToPx(Context mContext, int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics));
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
     * Converts menu item title into id
     *
     * @param position of menu item
     * @return resource id
     */
    public static int findMenuItemByPosition(int position) {
        switch (position) {
            case 0:
                return R.id.popupBalance;
            case 1:
                return R.id.popupTotalIncome;
            case 2:
                return R.id.popupTotalExpense;

            case 3:
                return R.id.popupSalary;
            case 4:
                return R.id.popupRentalIncome;
            case 5:
                return R.id.popupInterest;
            case 6:
                return R.id.popupGifts;
            case 7:
                return R.id.popupOtherIncome;

            case 8:
                return R.id.popupTaxes;
            case 9:
                return R.id.popupMortgage;
            case 10:
                return R.id.popupCreditCard;
            case 11:
                return R.id.popupUtilities;
            case 12:
                return R.id.popupFood;
            case 13:
                return R.id.popupCarPayment;
            case 14:
                return R.id.popupPersonal;
            case 15:
                return R.id.popupActivities;
            case 16:
                return R.id.popupOtherExpense;

            default:
                return R.id.popupBalance;
        }
    }

    /**
     * Converts options menu id into position
     *
     * @param id of resource
     * @return resource position in menu
     */
    public static int getPositionFromId(int id) {
        switch (id) {
            case R.id.popupBalance:
                return 0;
            case R.id.popupTotalIncome:
                return 1;
            case R.id.popupTotalExpense:
                return 2;
            case R.id.popupSalary:
                return 3;
            case R.id.popupRentalIncome:
                return 4;
            case R.id.popupInterest:
                return 5;
            case R.id.popupGifts:
                return 6;
            case R.id.popupOtherIncome:
                return 7;

            case R.id.popupTaxes:
                return 8;
            case R.id.popupMortgage:
                return 9;
            case R.id.popupCreditCard:
                return 10;
            case R.id.popupUtilities:
                return 11;
            case R.id.popupFood:
                return 12;
            case R.id.popupCarPayment:
                return 13;
            case R.id.popupPersonal:
                return 14;
            case R.id.popupActivities:
                return 15;
            case R.id.popupOtherExpense:
                return 16;

            default:
                return 0;
        }
    }

    /**
     * gets chart line color by resource id
     *
     * @param i resource id
     * @return color of line
     */
    public static int getLineColorPalette(Context mContext, int i) {
        switch (i) {
            case R.id.popupBalance:
                return ContextCompat.getColor(mContext, R.color.balance);
            case R.id.popupTotalIncome:
                return ContextCompat.getColor(mContext, R.color.income);
            case R.id.popupTotalExpense:
                return ContextCompat.getColor(mContext, R.color.expense);
            case R.id.popupSalary:
                return ContextCompat.getColor(mContext, R.color.salary);
            case R.id.popupRentalIncome:
                return ContextCompat.getColor(mContext, R.color.rental_income);
            case R.id.popupInterest:
                return ContextCompat.getColor(mContext, R.color.interest);
            case R.id.popupGifts:
                return ContextCompat.getColor(mContext, R.color.gifts);
            case R.id.popupOtherIncome:
                return ContextCompat.getColor(mContext, R.color.other_income);
            case R.id.popupTaxes:
                return ContextCompat.getColor(mContext, R.color.taxes);
            case R.id.popupMortgage:
                ContextCompat.getColor(mContext, R.color.mortgage);
            case R.id.popupCreditCard:
                return ContextCompat.getColor(mContext, R.color.credit_card);
            case R.id.popupUtilities:
                return ContextCompat.getColor(mContext, R.color.utilities);
            case R.id.popupFood:
                return ContextCompat.getColor(mContext, R.color.food);
            case R.id.popupCarPayment:
                return ContextCompat.getColor(mContext, R.color.car_payment);
            case R.id.popupPersonal:
                return ContextCompat.getColor(mContext, R.color.personal);
            case R.id.popupActivities:
                return ContextCompat.getColor(mContext, R.color.activities);
            case R.id.popupOtherExpense:
                return ContextCompat.getColor(mContext, R.color.other_expense);
            default:
                return Color.WHITE;

        }
    }

    /**
     * Gets color by data type key
     *
     * @param i data type key
     * @return color
     */
    public static int getPieColorPalette(Context mContext, int i) {
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
