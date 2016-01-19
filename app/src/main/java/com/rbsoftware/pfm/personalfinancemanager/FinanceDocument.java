package com.rbsoftware.pfm.personalfinancemanager;

import android.util.Log;

import com.cloudant.sync.datastore.BasicDocumentRevision;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by burzakovskiy on 11/24/2015.
 * Holds structure of finance document
 */
public class FinanceDocument {
    private static final String DOC_TYPE = "Finance document";
    public static final int DATE_FORMAT_SHORT = 0;
    public static final int DATE_FORMAT_MEDIUM = 1;
    public static final int DATE_FORMAT_LONG = 2;
    private List<String> salary = new ArrayList<>();
    private List<String> rentalIncome = new ArrayList<>();
    private List<String> interest = new ArrayList<>();
    private List<String> gifts = new ArrayList<>();
    private List<String> otherIncome = new ArrayList<>();
    private List<String> taxes = new ArrayList<>();
    private List<String> mortgage = new ArrayList<>();
    private List<String> creditCard = new ArrayList<>();
    private List<String> utilities = new ArrayList<>();
    private List<String> food = new ArrayList<>();
    private List<String> carPayment = new ArrayList<>();
    private List<String> personal = new ArrayList<>();
    private List<String> activities = new ArrayList<>();
    private List<String> otherExpenses = new ArrayList<>();
    private BasicDocumentRevision rev;
    private String date;

    private FinanceDocument() {
    }

    public FinanceDocument(List<Object> params) {
        /*  type, 0 - id, 1 - salary, 2  - rental income, 3 - interest, 4 - gifts, 5 - other income
        6 - taxes, 7 - mortgage, 8 - credit card,
        9 - utilities (Electric bill, Water bill, Gas bill, Phone bill, Internet service, Cable or satellite service),
        10 - food (Groceries, Dining out),
        11 - car payment (Fuel, Auto insurance, Tires and maintenance, Tag/registration),
        12 - personal (Clothing, Hair care, Medical expenses),
        13 - activities (Gym membership, Vacation, Charitable giving, Entertainment,Gifts),
        14 - other expenses
        */

        this.type = DOC_TYPE;
        this.userId = (String) params.get(0);


        Date currDate = new Date();
        this.date = Long.toString(currDate.getTime() / 1000);


        this.setType(DOC_TYPE);
        this.setUserId(userId);
        this.setSalary((ArrayList<String>) params.get(1));
        this.setRentalIncome((ArrayList<String>) params.get(2));
        this.setInterest((ArrayList<String>) params.get(3));
        this.setGifts((ArrayList<String>) params.get(4));
        this.setOtherIncome((ArrayList<String>) params.get(5));
        this.setTaxes((ArrayList<String>) params.get(6));
        this.setMortgage((ArrayList<String>) params.get(7));
        this.setCreditCard((ArrayList<String>) params.get(8));
        this.setUtilities((ArrayList<String>) params.get(9));
        this.setFood((ArrayList<String>) params.get(10));
        this.setCarPayment((ArrayList<String>) params.get(11));
        this.setPersonal((ArrayList<String>) params.get(12));
        this.setActivities((ArrayList<String>) params.get(13));
        this.setOtherExpenses((ArrayList<String>) params.get(14));

    }

    //type
    private String type = DOC_TYPE;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    //data
    private String userId;

    public String getuserId() {
        return userId;
    }

    public void setUserId(String data) {
        this.userId = data;
    }

    //salary
    public String getSalary() {
        return salary.get(0);
    }

    public void setSalary(String salary, String currency, String recursion) {

        this.salary.add(0, salary);
        this.salary.add(1, currency);
        this.salary.add(2, recursion);
    }

    public void setSalary(ArrayList<String> salary) {

        this.salary = salary;
    }

    //rental income

    public String getRentalIncome() {
        return rentalIncome.get(0);
    }

    public void setRentalIncome(String rentalIncome, String currency, String recursion) {
        this.rentalIncome.add(0, rentalIncome);
        this.rentalIncome.add(1, currency);
        this.rentalIncome.add(2, recursion);
    }

    public void setRentalIncome(ArrayList<String> rentalIncome) {
        this.rentalIncome = rentalIncome;
    }

    //interest

    public String getInterest() {
        return interest.get(0);
    }

    public void setInterest(String interest, String currency, String recursion) {

        this.interest.add(0, interest);
        this.interest.add(1, currency);
        this.interest.add(2, recursion);
    }

    public void setInterest(ArrayList<String> interest) {

        this.interest = interest;
    }

    //gifts

    public String getGifts() {

        return gifts.get(0);
    }

    public void setGifts(String gifts, String currency, String recursion) {

        this.gifts.add(0, gifts);
        this.gifts.add(1, currency);
        this.gifts.add(2, recursion);
    }

    public void setGifts(ArrayList<String> gifts) {

        this.gifts = gifts;
    }

    //other income

    public String getOtherIncome() {

        return otherIncome.get(0);
    }

    public void setOtherIncome(String otherIncome, String currency, String recursion) {

        this.otherIncome.add(0, otherIncome);
        this.otherIncome.add(1, currency);
        this.otherIncome.add(2, recursion);
    }

    public void setOtherIncome(ArrayList<String> otherIncome) {

        this.otherIncome = otherIncome;
    }

    //7 - taxes

    public String getTaxes() {

        return taxes.get(0);
    }

    public void setTaxes(String taxes, String currency, String recursion) {

        this.taxes.add(0, taxes);
        this.taxes.add(1, currency);
        this.taxes.add(2, recursion);
    }

    public void setTaxes(ArrayList<String> taxes) {

        this.taxes = taxes;
    }

    // 8 - mortgage

    public String getMortgage() {

        return mortgage.get(0);
    }

    public void setMortgage(String mortgage, String currency, String recursion) {

        this.mortgage.add(0, mortgage);
        this.mortgage.add(1, currency);
        this.mortgage.add(2, recursion);
    }

    public void setMortgage(ArrayList<String> mortgage) {

        this.mortgage = mortgage;
    }


    // 9 - credit card
    public String getCreditCard() {

        return creditCard.get(0);
    }

    public void setCreditCard(String creditCard, String currency, String recursion) {

        this.creditCard.add(0, creditCard);
        this.creditCard.add(1, currency);
        this.creditCard.add(2, recursion);
    }

    public void setCreditCard(ArrayList<String> creditCard) {

        this.creditCard = creditCard;
    }

    //10 - utilities

    public String getUtilities() {

        return utilities.get(0);
    }

    public void setUtilities(String utilities, String currency, String recursion) {

        this.utilities.add(0, utilities);
        this.utilities.add(1, currency);
        this.utilities.add(2, recursion);
    }

    public void setUtilities(ArrayList<String> utilities) {

        this.utilities = utilities;
    }

    //11 - food

    public String getFood() {

        return food.get(0);
    }

    public void setFood(String food, String currency, String recursion) {

        this.food.add(0, food);
        this.food.add(1, currency);
        this.food.add(2, recursion);
    }

    public void setFood(ArrayList<String> food) {

        this.food = food;
    }

    //12 - car payment

    public String getCarPayment() {

        return carPayment.get(0);
    }

    public void setCarPayment(String carPayment, String currency, String recursion) {

        this.carPayment.add(0, carPayment);
        this.carPayment.add(1, currency);
        this.carPayment.add(2, recursion);
    }

    public void setCarPayment(ArrayList<String> carPayment) {

        this.carPayment = carPayment;
    }

    //13 - personal

    public String getPersonal() {

        return personal.get(0);
    }

    public void setPersonal(String personal, String currency, String recursion) {

        this.personal.add(0, personal);
        this.personal.add(1, currency);
        this.personal.add(2, recursion);
    }

    public void setPersonal(ArrayList<String> personal) {

        this.personal = personal;
    }

    //14 - activities

    public String getActivities() {

        return activities.get(0);
    }

    public void setActivities(String activities, String currency, String recursion) {

        this.activities.add(0, activities);
        this.activities.add(1, currency);
        this.activities.add(2, recursion);
    }

    public void setActivities(ArrayList<String> activities) {

        this.activities = activities;
    }
    //15 - other expenses

    public String getOtherExpenses() {

        return otherExpenses.get(0);
    }

    public void setOtherExpenses(String otherExpenses, String currency, String recursion) {

        this.otherExpenses.add(0, otherExpenses);
        this.otherExpenses.add(1, currency);
        this.otherExpenses.add(2, recursion);
    }

    public void setOtherExpenses(ArrayList<String> otherExpenses) {

        this.otherExpenses = otherExpenses;
    }


    //date

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Converts unix date inot human readable
     *
     * @param format date of the date
     * @return human readable date
     */

    public String getNormalDate(int format) {
        Date formatDate = new Date(Long.valueOf(date) * 1000L); // *1000 is to convert seconds to milliseconds
        DateFormat sdf;
        switch (format) {
            case 0: //short
                if (!Locale.getDefault().equals(Locale.US)) {
                    sdf = new SimpleDateFormat("dd.MM", Locale.getDefault()); // the format of your date
                } else {
                    sdf = new SimpleDateFormat("MM.dd", Locale.getDefault()); // the format of your date
                }
                break;
            case 1: //medium
                sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // the format of your date
                break;
            case 2: // long
                sdf = DateFormat.getDateInstance(DATE_FORMAT_LONG, Locale.getDefault());
                break;
            default:
                sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // the format of your date
                break;
        }
        sdf.setTimeZone(TimeZone.getDefault()); // give a timezone reference for formating (see comment at the bottom
        String normalDate = sdf.format(formatDate);
        return normalDate;
    }

    /**
     * Gets total income
     *
     * @return total income
     */

    public int getTotalIncome() {
        int totalIncome;
        totalIncome = Integer.valueOf(getSalary()) +
                Integer.valueOf(getRentalIncome()) +
                Integer.valueOf(getInterest()) +
                Integer.valueOf(getGifts()) +
                Integer.valueOf(getOtherIncome());
        return totalIncome;
    }


    /**
     * Gets total expense
     *
     * @return total expense
     */
    public int getTotalExpense() {
        int totalExpense;
        totalExpense = Integer.valueOf(getTaxes()) +
                Integer.valueOf(getMortgage()) +
                Integer.valueOf(getCreditCard()) +
                Integer.valueOf(getUtilities()) +
                Integer.valueOf(getFood()) +
                Integer.valueOf(getCarPayment()) +
                Integer.valueOf(getPersonal()) +
                Integer.valueOf(getActivities()) +
                Integer.valueOf(getOtherExpenses());
        return totalExpense;
    }


    /**
     * extracts data of FinanceDocument
     *
     * @return hashmap of data types and values
     */
    public HashMap<Integer, List<String>> getValuesMap() {

        HashMap<Integer, List<String>> mapSum = new HashMap<>();
        mapSum.put(MainActivity.PARAM_SALARY, salary);
        mapSum.put(MainActivity.PARAM_RENTAL_INCOME, rentalIncome);
        mapSum.put(MainActivity.PARAM_INTEREST, interest);
        mapSum.put(MainActivity.PARAM_GIFTS, gifts);
        mapSum.put(MainActivity.PARAM_OTHER_INCOME, otherIncome);

        mapSum.put(MainActivity.PARAM_TAXES, taxes);
        mapSum.put(MainActivity.PARAM_MORTGAGE, mortgage);
        mapSum.put(MainActivity.PARAM_CREDIT_CARD, creditCard);
        mapSum.put(MainActivity.PARAM_UTILITIES, utilities);
        mapSum.put(MainActivity.PARAM_FOOD, food);
        mapSum.put(MainActivity.PARAM_CAR_PAYMENT, carPayment);
        mapSum.put(MainActivity.PARAM_PERSONAL, personal);
        mapSum.put(MainActivity.PARAM_ACTIVITIES, activities);
        mapSum.put(MainActivity.PARAM_OTHER_EXPENSE, otherExpenses);

        return mapSum;
    }


    public BasicDocumentRevision getDocumentRevision() {
        return rev;
    }

    /**
     * Creates finaince document from revision
     *
     * @param rev document revision
     * @return finance document
     */
    public static FinanceDocument fromRevision(BasicDocumentRevision rev) {
        FinanceDocument t = new FinanceDocument();
        t.rev = rev;
        // this could also be done by a fancy object mapper
        Map<String, Object> map = rev.asMap();
        if (map.containsKey("type") && map.get("type").equals(FinanceDocument.DOC_TYPE)) {
            t.setDate((String) map.get("date"));
            t.setType((String) map.get("type"));
            t.setUserId((String) map.get("userId"));

            t.setSalary((ArrayList<String>) map.get("salary"));
            t.setRentalIncome((ArrayList<String>) map.get("rentalIncome"));
            t.setInterest((ArrayList<String>) map.get("interest"));
            t.setGifts((ArrayList<String>) map.get("gifts"));
            t.setOtherIncome((ArrayList<String>) map.get("otherIncome"));
            t.setTaxes((ArrayList<String>) map.get("taxes"));
            t.setMortgage((ArrayList<String>) map.get("mortgage"));
            t.setCreditCard((ArrayList<String>) map.get("creditCard"));
            t.setUtilities((ArrayList<String>) map.get("utilities"));
            t.setFood((ArrayList<String>) map.get("food"));
            t.setCarPayment((ArrayList<String>) map.get("carPayment"));
            t.setPersonal((ArrayList<String>) map.get("personal"));
            t.setActivities((ArrayList<String>) map.get("activities"));
            t.setOtherExpenses((ArrayList<String>) map.get("otherExpenses"));
            return t;
        }
        return null;
    }

    /**
     * Creates hash map of data types and values
     *
     * @return map of data types and values
     */
    public Map<String, Object> asMap() {
        // this could also be done by a fancy object mapper
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("type", type);
        map.put("userId", userId);
        map.put("salary", salary);
        map.put("rentalIncome", rentalIncome);
        map.put("interest", interest);
        map.put("gifts", gifts);
        map.put("otherIncome", otherIncome);
        map.put("taxes", taxes);
        map.put("mortgage", mortgage);
        map.put("creditCard", creditCard);
        map.put("utilities", utilities);
        map.put("food", food);
        map.put("carPayment", carPayment);
        map.put("personal", personal);
        map.put("activities", activities);
        map.put("otherExpenses", otherExpenses);
        map.put("date", date);

        return map;
    }

}