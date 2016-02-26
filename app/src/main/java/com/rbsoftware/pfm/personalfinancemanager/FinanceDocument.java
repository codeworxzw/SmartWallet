package com.rbsoftware.pfm.personalfinancemanager;


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
@SuppressWarnings("unchecked") // Suppressing unchecked cast: 'java.lang.Object' to 'java.util.ArrayList<java.lang.String>'
public class FinanceDocument {
    private static final String DOC_TYPE = "Finance document";
    private static final String TAG = "FinanceDocument";
    private static final String MAIN_ACCOUNT = "mainAccount";
    public static final int DATE_FORMAT_SHORT = 0;
    public static final int DATE_FORMAT_MEDIUM = 1;
    public static final int DATE_FORMAT_LONG = 2;
    public static final int NUMBER_OF_CATEGORIES = 14;

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
    private String account;
    private String type;

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
        this.setAccount(MAIN_ACCOUNT);
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

    //account
    private void setAccount(String account) {
        this.account = account;
    }

    private String getAccount() {
        return this.account;
    }
    //type


    public String getType() {
        return type;
    }

    private void setType(String type) {
        this.type = type;
    }

    //data
    private String userId;

    public String getuserId() {
        return userId;
    }

    private void setUserId(String data) {
        this.userId = data;
    }

    //salary
    public int getSalary() {
        if (salary == null) {
            return 0;
        } else if (salary.get(1).equals(MainActivity.defaultCurrency)) {

            return Integer.valueOf(salary.get(0));
        } else {

            return CurrencyConversion.convertCurrency(Integer.valueOf(salary.get(0)), salary.get(1), MainActivity.defaultCurrency);
        }
    }

    public void setSalary(String salary, String currency, String recursion) {

        this.salary.add(0, salary);
        this.salary.add(1, currency);
        this.salary.add(2, recursion);
    }

    private void setSalary(ArrayList<String> salary) {

        this.salary = salary;
    }

    //rental income

    public int getRentalIncome() {
        if (rentalIncome == null) {
            return 0;
        } else if (rentalIncome.get(1).equals(MainActivity.defaultCurrency)) {

            return Integer.valueOf(rentalIncome.get(0));
        } else {

            return CurrencyConversion.convertCurrency(Integer.valueOf(rentalIncome.get(0)), rentalIncome.get(1), MainActivity.defaultCurrency);
        }
    }

    public void setRentalIncome(String rentalIncome, String currency, String recursion) {
        this.rentalIncome.add(0, rentalIncome);
        this.rentalIncome.add(1, currency);
        this.rentalIncome.add(2, recursion);
    }

    private void setRentalIncome(ArrayList<String> rentalIncome) {
        this.rentalIncome = rentalIncome;
    }

    //interest

    public int getInterest() {
        if (interest == null) {
            return 0;
        } else if (interest.get(1).equals(MainActivity.defaultCurrency)) {

            return Integer.valueOf(interest.get(0));
        } else {

            return CurrencyConversion.convertCurrency(Integer.valueOf(interest.get(0)), interest.get(1), MainActivity.defaultCurrency);
        }
    }

    public void setInterest(String interest, String currency, String recursion) {

        this.interest.add(0, interest);
        this.interest.add(1, currency);
        this.interest.add(2, recursion);
    }

    private void setInterest(ArrayList<String> interest) {

        this.interest = interest;
    }

    //gifts

    public int getGifts() {
        if (gifts == null) {
            return 0;
        } else if (gifts.get(1).equals(MainActivity.defaultCurrency)) {

            return Integer.valueOf(gifts.get(0));
        } else {

            return CurrencyConversion.convertCurrency(Integer.valueOf(gifts.get(0)), gifts.get(1), MainActivity.defaultCurrency);
        }
    }

    public void setGifts(String gifts, String currency, String recursion) {

        this.gifts.add(0, gifts);
        this.gifts.add(1, currency);
        this.gifts.add(2, recursion);
    }

    private void setGifts(ArrayList<String> gifts) {

        this.gifts = gifts;
    }

    //other income

    public int getOtherIncome() {
        if (otherIncome == null) {
            return 0;
        } else if (otherIncome.get(1).equals(MainActivity.defaultCurrency)) {

            return Integer.valueOf(otherIncome.get(0));
        } else {

            return CurrencyConversion.convertCurrency(Integer.valueOf(otherIncome.get(0)), otherIncome.get(1), MainActivity.defaultCurrency);
        }
    }

    public void setOtherIncome(String otherIncome, String currency, String recursion) {

        this.otherIncome.add(0, otherIncome);
        this.otherIncome.add(1, currency);
        this.otherIncome.add(2, recursion);
    }

    private void setOtherIncome(ArrayList<String> otherIncome) {

        this.otherIncome = otherIncome;
    }

    //7 - taxes

    public int getTaxes() {

        if (taxes == null) {
            return 0;
        } else if (taxes.get(1).equals(MainActivity.defaultCurrency)) {

            return Integer.valueOf(taxes.get(0));
        } else {

            return CurrencyConversion.convertCurrency(Integer.valueOf(taxes.get(0)), taxes.get(1), MainActivity.defaultCurrency);
        }
    }

    public void setTaxes(String taxes, String currency, String recursion) {

        this.taxes.add(0, taxes);
        this.taxes.add(1, currency);
        this.taxes.add(2, recursion);
    }

    private void setTaxes(ArrayList<String> taxes) {

        this.taxes = taxes;
    }

    // 8 - mortgage

    public int getMortgage() {
        if (mortgage == null) {
            return 0;
        } else if (mortgage.get(1).equals(MainActivity.defaultCurrency)) {

            return Integer.valueOf(mortgage.get(0));
        } else {
            return CurrencyConversion.convertCurrency(Integer.valueOf(mortgage.get(0)), mortgage.get(1), MainActivity.defaultCurrency);
        }
    }

    public void setMortgage(String mortgage, String currency, String recursion) {

        this.mortgage.add(0, mortgage);
        this.mortgage.add(1, currency);
        this.mortgage.add(2, recursion);
    }

    private void setMortgage(ArrayList<String> mortgage) {

        this.mortgage = mortgage;
    }


    // 9 - credit card
    public int getCreditCard() {
        if (creditCard == null) {
            return 0;
        } else if (creditCard.get(1).equals(MainActivity.defaultCurrency)) {

            return Integer.valueOf(creditCard.get(0));
        } else {
            return CurrencyConversion.convertCurrency(Integer.valueOf(creditCard.get(0)), creditCard.get(1), MainActivity.defaultCurrency);
        }
    }

    public void setCreditCard(String creditCard, String currency, String recursion) {

        this.creditCard.add(0, creditCard);
        this.creditCard.add(1, currency);
        this.creditCard.add(2, recursion);
    }

    private void setCreditCard(ArrayList<String> creditCard) {

        this.creditCard = creditCard;
    }

    //10 - utilities

    public int getUtilities() {
        if (utilities == null) {
            return 0;
        } else if (utilities.get(1).equals(MainActivity.defaultCurrency)) {

            return Integer.valueOf(utilities.get(0));
        } else {
            return CurrencyConversion.convertCurrency(Integer.valueOf(utilities.get(0)), utilities.get(1), MainActivity.defaultCurrency);
        }
    }

    public void setUtilities(String utilities, String currency, String recursion) {

        this.utilities.add(0, utilities);
        this.utilities.add(1, currency);
        this.utilities.add(2, recursion);
    }

    private void setUtilities(ArrayList<String> utilities) {

        this.utilities = utilities;
    }

    //11 - food

    public int getFood() {
        if (food == null) {
            return 0;
        } else if (food.get(1).equals(MainActivity.defaultCurrency)) {

            return Integer.valueOf(food.get(0));
        } else {
            return CurrencyConversion.convertCurrency(Integer.valueOf(food.get(0)), food.get(1), MainActivity.defaultCurrency);
        }
    }

    public void setFood(String food, String currency, String recursion) {

        this.food.add(0, food);
        this.food.add(1, currency);
        this.food.add(2, recursion);
    }

    private void setFood(ArrayList<String> food) {

        this.food = food;
    }

    //12 - car payment

    public int getCarPayment() {
        if (carPayment == null) {
            return 0;
        } else if (carPayment.get(1).equals(MainActivity.defaultCurrency)) {

            return Integer.valueOf(carPayment.get(0));
        } else {
            return CurrencyConversion.convertCurrency(Integer.valueOf(carPayment.get(0)), carPayment.get(1), MainActivity.defaultCurrency);
        }
    }

    public void setCarPayment(String carPayment, String currency, String recursion) {

        this.carPayment.add(0, carPayment);
        this.carPayment.add(1, currency);
        this.carPayment.add(2, recursion);
    }

    private void setCarPayment(ArrayList<String> carPayment) {

        this.carPayment = carPayment;
    }

    //13 - personal

    public int getPersonal() {
        if (personal == null) {
            return 0;
        } else if (personal.get(1).equals(MainActivity.defaultCurrency)) {

            return Integer.valueOf(personal.get(0));
        } else {
            return CurrencyConversion.convertCurrency(Integer.valueOf(personal.get(0)), personal.get(1), MainActivity.defaultCurrency);
        }
    }

    public void setPersonal(String personal, String currency, String recursion) {

        this.personal.add(0, personal);
        this.personal.add(1, currency);
        this.personal.add(2, recursion);
    }

    private void setPersonal(ArrayList<String> personal) {

        this.personal = personal;
    }

    //14 - activities

    public int getActivities() {
        if (activities == null) {
            return 0;
        } else if (activities.get(1).equals(MainActivity.defaultCurrency)) {

            return Integer.valueOf(activities.get(0));
        } else {
            return CurrencyConversion.convertCurrency(Integer.valueOf(activities.get(0)), activities.get(1), MainActivity.defaultCurrency);
        }
    }

    public void setActivities(String activities, String currency, String recursion) {

        this.activities.add(0, activities);
        this.activities.add(1, currency);
        this.activities.add(2, recursion);
    }

    private void setActivities(ArrayList<String> activities) {

        this.activities = activities;
    }
    //15 - other expenses

    public int getOtherExpenses() {
        if (otherExpenses == null) {
            return 0;
        } else if (otherExpenses.get(1).equals(MainActivity.defaultCurrency)) {

            return Integer.valueOf(otherExpenses.get(0));
        } else {
            return CurrencyConversion.convertCurrency(Integer.valueOf(otherExpenses.get(0)), otherExpenses.get(1), MainActivity.defaultCurrency);
        }
    }

    public void setOtherExpenses(String otherExpenses, String currency, String recursion) {

        this.otherExpenses.add(0, otherExpenses);
        this.otherExpenses.add(1, currency);
        this.otherExpenses.add(2, recursion);
    }

    private void setOtherExpenses(ArrayList<String> otherExpenses) {

        this.otherExpenses = otherExpenses;
    }


    //date

    public String getDate() {
        return date;
    }

    private void setDate(String date) {
        this.date = date;
    }

    /**
     * Converts unix date into human readable
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
        return sdf.format(formatDate);
    }

    /**
     * Gets total income
     *
     * @return total income
     */

    public int getTotalIncome() {
        int totalIncome;
        totalIncome = getSalary() +
                getRentalIncome() +
                getInterest() +
                getGifts() +
                getOtherIncome();
        return totalIncome;
    }


    /**
     * Gets total expense
     *
     * @return total expense
     */
    public int getTotalExpense() {
        int totalExpense;
        totalExpense = getTaxes() +
                getMortgage() +
                getCreditCard() +
                getUtilities() +
                getFood() +
                getCarPayment() +
                getPersonal() +
                getActivities() +
                getOtherExpenses();
        return totalExpense;
    }


    /**
     * extracts data of FinanceDocument
     *
     * @return hashmap of data types and values
     */
    public HashMap<Integer, List<String>> getValuesMap() {

        HashMap<Integer, List<String>> mapSum = new HashMap<>();
        if (salary != null) {
            mapSum.put(MainActivity.PARAM_SALARY, salary);
        }
        if (rentalIncome != null) {
            mapSum.put(MainActivity.PARAM_RENTAL_INCOME, rentalIncome);
        }
        if (interest != null) {
            mapSum.put(MainActivity.PARAM_INTEREST, interest);
        }
        if (gifts != null) {
            mapSum.put(MainActivity.PARAM_GIFTS, gifts);
        }
        if (otherIncome != null) {
            mapSum.put(MainActivity.PARAM_OTHER_INCOME, otherIncome);
        }
        if (taxes != null) {
            mapSum.put(MainActivity.PARAM_TAXES, taxes);
        }
        if (mortgage != null) {
            mapSum.put(MainActivity.PARAM_MORTGAGE, mortgage);
        }
        if (creditCard != null) {
            mapSum.put(MainActivity.PARAM_CREDIT_CARD, creditCard);
        }
        if (utilities != null) {
            mapSum.put(MainActivity.PARAM_UTILITIES, utilities);
        }
        if (food != null) {
            mapSum.put(MainActivity.PARAM_FOOD, food);
        }
        if (carPayment != null) {
            mapSum.put(MainActivity.PARAM_CAR_PAYMENT, carPayment);
        }
        if (personal != null) {
            mapSum.put(MainActivity.PARAM_PERSONAL, personal);
        }
        if (activities != null) {
            mapSum.put(MainActivity.PARAM_ACTIVITIES, activities);
        }
        if (otherExpenses != null) {
            mapSum.put(MainActivity.PARAM_OTHER_EXPENSE, otherExpenses);
        }

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
            t.setAccount((String) map.get("account"));
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
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("userId", userId);
        map.put("account", account);
        if (!salary.get(0).equals("0")) {
            map.put("salary", salary);
        }
        if (!rentalIncome.get(0).equals("0")) {
            map.put("rentalIncome", rentalIncome);
        }
        if (!interest.get(0).equals("0")) {
            map.put("interest", interest);
        }
        if (!gifts.get(0).equals("0")) {
            map.put("gifts", gifts);
        }
        if (!otherIncome.get(0).equals("0")) {
            map.put("otherIncome", otherIncome);
        }
        if (!taxes.get(0).equals("0")) {
            map.put("taxes", taxes);
        }
        if (!mortgage.get(0).equals("0")) {
            map.put("mortgage", mortgage);
        }
        if (!creditCard.get(0).equals("0")) {
            map.put("creditCard", creditCard);
        }
        if (!utilities.get(0).equals("0")) {
            map.put("utilities", utilities);
        }
        if (!food.get(0).equals("0")) {
            map.put("food", food);
        }
        if (!carPayment.get(0).equals("0")) {
            map.put("carPayment", carPayment);
        }
        if (!personal.get(0).equals("0")) {
            map.put("personal", personal);
        }
        if (!activities.get(0).equals("0")) {
            map.put("activities", activities);
        }
        if (!otherExpenses.get(0).equals("0")) {
            map.put("otherExpenses", otherExpenses);
        }
        map.put("date", date);

        return map;
    }

}