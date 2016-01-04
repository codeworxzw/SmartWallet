package com.rbsoftware.pfm.personalfinancemanager;

import com.cloudant.sync.datastore.BasicDocumentRevision;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by burzakovskiy on 11/24/2015.
 */
public class FinanceDocument {
    static final String DOC_TYPE = "Finance document";


    private FinanceDocument() {}

    public FinanceDocument(List<String> params) {
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
        this.userId = params.get(0);
        this.salary = params.get(1);
        this.rentalIncome = params.get(2);
        this.interest = params.get(3);
        this.gifts = params.get(4);
        this.otherIncome = params.get(5);
        this.taxes = params.get(6);
        this.mortgage = params.get(7);
        this.creditCard = params.get(8);
        this.utilities = params.get(9);
        this.food = params.get(10);
        this.carPayment = params.get(11);
        this.personal = params.get(12);
        this.activities = params.get(13);
        this.otherExpenses = params.get(14);

        Date currDate = new Date();
        this.date = Long.toString(currDate.getTime() / 1000);


        this.setType(DOC_TYPE);
        this.setUserId(userId);
        this.setSalary(salary);
        this.setRentalIncome(rentalIncome);
        this.setInterest(interest);
        this.setGifts(gifts);
        this.setOtherIncome(otherIncome);
        this.setTaxes(taxes);
        this.setMortgage(mortgage);
        this.setCreditCard(creditCard);
        this.setUtilities(utilities);
        this.setFood(food);
        this.setCarPayment(carPayment);
        this.setPersonal(personal);
        this.setActivities(activities);
        this.setOtherExpenses(otherExpenses);

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
    private String salary;
    public String getSalary() {
        return salary;
    }
    public void setSalary(String salary) {
        this.salary = salary;
    }
    //rental income
    private String rentalIncome;
    public String getRentalIncome() {
        return rentalIncome;
    }
    public void setRentalIncome(String rentalIncome) {
        this.rentalIncome = rentalIncome;
    }
    //interest
    private String interest;
    public String getInterest() {
        return interest;
    }
    public void setInterest(String interest) {
        this.interest = interest;
    }

    //gifts
    private String gifts;
    public String getGifts() {
        return gifts;
    }
    public void setGifts(String gifts) {
        this.gifts = gifts;
    }

    //other income
    private String otherIncome;
    public String getOtherIncome() {
        return otherIncome;
    }
    public void setOtherIncome(String otherIncome) {
        this.otherIncome = otherIncome;
    }

    //7 - taxes
    private String taxes;
    public String getTaxes() {
        return taxes;
    }
    public void setTaxes(String taxes) {
        this.taxes = taxes;
    }

    // 8 - mortgage
    private String mortgage;
    public String getMortgage() {
        return mortgage;
    }
    public void setMortgage(String mortgage) {
        this.mortgage = mortgage;
    }

    // 9 - credit card
    private String creditCard;
    public String getCreditCard() {
        return creditCard;
    }
    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    //10 - utilities
    private String utilities;
    public String getUtilities() {
        return utilities;
    }
    public void setUtilities(String utilities) {
        this.utilities = utilities;
    }

    //11 - food
    private String food;
    public String getFood() {
        return food;
    }
    public void setFood(String food) {
        this.food = food;
    }

    //12 - car payment
    private String carPayment;
    public String getCarPayment() {
        return carPayment;
    }
    public void setCarPayment(String carPayment) {
        this.carPayment = carPayment;
    }

    //13 - personal
    private String personal;
    public String getPersonal() {
        return personal;
    }
    public void setPersonal(String personal) {
        this.personal = personal;
    }

    //14 - activities
    private String activities;
    public String getActivities() {
        return activities;
    }
    public void setActivities(String activities) {
        this.activities = activities;
    }

    //15 - other expenses
    private String otherExpenses;
    public String getOtherExpenses() {
        return otherExpenses;
    }
    public void setOtherExpenses(String otherExpenses) {
        this.otherExpenses = otherExpenses;
    }


    //date
    private String date;
    public String getDate() {return date;}
    public void setDate (String date){this.date = date;}

    //@return human readable date
    public String getNormalDate(){
        Date formatDate = new Date(Long.valueOf(date)*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm"); // the format of your date
        sdf.setTimeZone(TimeZone.getDefault()); // give a timezone reference for formating (see comment at the bottom
        String normalDate = sdf.format(formatDate);
        return normalDate;
    }

    // @return total income
    public int getTotalIncome(){
        int totalIncome;
        totalIncome = Integer.valueOf(salary) +
                Integer.valueOf(rentalIncome) +
                Integer.valueOf(interest) +
                Integer.valueOf(gifts) +
                Integer.valueOf(otherIncome);
        return totalIncome;
    }

    // @return total expense
    public int getTotalExpense(){
        int totalExpense;
        totalExpense = Integer.valueOf(taxes) +
                Integer.valueOf(mortgage) +
                Integer.valueOf(creditCard) +
                Integer.valueOf(utilities) +
                Integer.valueOf(food)+
                Integer.valueOf(carPayment)+
                Integer.valueOf(personal)+
                Integer.valueOf(activities)+
                Integer.valueOf(otherExpenses);
        return totalExpense;
    }


    // extracts data of FinanceDocument
    public HashMap<Integer,Integer> getValuesMap(){

        HashMap<Integer, Integer> mapSum=new HashMap<>();
        mapSum.put(MainActivity.PARAM_SALARY, Integer.valueOf(this.getSalary()));
        mapSum.put(MainActivity.PARAM_RENTAL_INCOME, Integer.valueOf(this.getRentalIncome()));
        mapSum.put(MainActivity.PARAM_INTEREST, Integer.valueOf(this.getInterest()));
        mapSum.put(MainActivity.PARAM_GIFTS, Integer.valueOf(this.getGifts()));
        mapSum.put(MainActivity.PARAM_OTHER_INCOME, Integer.valueOf(this.getOtherIncome()));

        mapSum.put(MainActivity.PARAM_TAXES, Integer.valueOf(this.getTaxes()));
        mapSum.put(MainActivity.PARAM_MORTGAGE, Integer.valueOf(this.getMortgage()));
        mapSum.put(MainActivity.PARAM_CREDIT_CARD, Integer.valueOf(this.getCreditCard()));
        mapSum.put(MainActivity.PARAM_UTILITIES, Integer.valueOf(this.getUtilities()));
        mapSum.put(MainActivity.PARAM_FOOD, Integer.valueOf(this.getFood()));
        mapSum.put(MainActivity.PARAM_CAR_PAYMENT, Integer.valueOf(this.getCarPayment()));
        mapSum.put(MainActivity.PARAM_PERSONAL, Integer.valueOf(this.getPersonal()));
        mapSum.put(MainActivity.PARAM_ACTIVITIES, Integer.valueOf(this.getActivities()));
        mapSum.put(MainActivity.PARAM_OTHER_EXPENSE, Integer.valueOf(this.getOtherExpenses()));

        return mapSum;
    }



    private BasicDocumentRevision rev;
    public BasicDocumentRevision getDocumentRevision() {
        return rev;
    }
    public static FinanceDocument fromRevision(BasicDocumentRevision rev) {
        FinanceDocument t = new FinanceDocument();
        t.rev = rev;
        // this could also be done by a fancy object mapper
        Map<String, Object> map = rev.asMap();
        if(map.containsKey("type") && map.get("type").equals(FinanceDocument.DOC_TYPE)) {
            t.setDate((String) map.get("date"));
            t.setType((String) map.get("type"));
            t.setUserId((String) map.get("userId"));
            t.setSalary((String) map.get("salary"));
            t.setRentalIncome((String) map.get("rentalIncome"));
            t.setInterest((String) map.get("interest"));
            t.setGifts((String) map.get("gifts"));
            t.setOtherIncome((String) map.get("otherIncome"));
            t.setTaxes((String) map.get("taxes"));
            t.setMortgage((String) map.get("mortgage"));
            t.setCreditCard((String) map.get("creditCard"));
            t.setUtilities((String) map.get("utilities"));
            t.setFood((String) map.get("food"));
            t.setCarPayment((String) map.get("carPayment"));
            t.setPersonal((String) map.get("personal"));
            t.setActivities((String) map.get("activities"));
            t.setOtherExpenses((String) map.get("otherExpenses"));
            return t;
        }
        return null;
    }

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