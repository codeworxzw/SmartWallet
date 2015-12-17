package com.rbsoftware.pfm.personalfinancemanager;

import com.cloudant.sync.datastore.BasicDocumentRevision;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        this.rental_income = params.get(2);
        this.interest = params.get(3);
        this.gifts = params.get(4);
        this.other_income = params.get(5);
        this.taxes = params.get(6);
        this.mortgage = params.get(7);
        this.credit_card = params.get(8);
        this.utilities = params.get(9);
        this.food = params.get(10);
        this.car_payment = params.get(11);
        this.personal = params.get(12);
        this.activities = params.get(13);
        this.other_expenses = params.get(14);

        Date currDate = new Date();
        this.date = Long.toString(currDate.getTime() / 1000);


        this.setType(DOC_TYPE);
        this.setUserId(userId);
        this.setSalary(salary);
        this.setRental_income(rental_income);
        this.setInterest(interest);
        this.setGifts(gifts);
        this.setOther_income(other_income);
        this.setTaxes(taxes);
        this.setMortgage(mortgage);
        this.setCredit_card(credit_card);
        this.setUtilities(utilities);
        this.setFood(food);
        this.setCar_payment(car_payment);
        this.setPersonal(personal);
        this.setActivities(activities);
        this.setOther_expenses(other_expenses);

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
    private String rental_income;
    public String getRental_income() {
        return rental_income;
    }
    public void setRental_income(String rental_income) {
        this.rental_income = rental_income;
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
    private String other_income;
    public String getOther_income() {
        return other_income;
    }
    public void setOther_income(String other_income) {
        this.other_income = other_income;
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
    private String credit_card;
    public String getCredit_card() {
        return credit_card;
    }
    public void setCredit_card(String credit_card) {
        this.credit_card = credit_card;
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
    private String car_payment;
    public String getCar_payment() {
        return car_payment;
    }
    public void setCar_payment(String car_payment) {
        this.car_payment = car_payment;
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
    private String other_expenses;
    public String getOther_expenses() {
        return other_expenses;
    }
    public void setOther_expenses(String other_expenses) {
        this.other_expenses = other_expenses;
    }


    //date
    private String date;
    public String getDate() {return date;}
    public void setDate (String date){this.date = date;}



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
            t.setRental_income((String) map.get("rental income"));
            t.setInterest((String) map.get("interest"));
            t.setGifts((String) map.get("gifts"));
            t.setOther_income((String) map.get("other income"));
            t.setTaxes((String) map.get("taxes"));
            t.setMortgage((String) map.get("mortgage"));
            t.setCredit_card((String) map.get("credit card"));
            t.setUtilities((String) map.get("utilities"));
            t.setFood((String) map.get("food"));
            t.setCar_payment((String) map.get("car payment"));
            t.setPersonal((String) map.get("personal"));
            t.setActivities((String) map.get("activities"));
            t.setOther_expenses((String) map.get("other expenses"));
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
        map.put("rental income", rental_income);
        map.put("interest", interest);
        map.put("gifts", gifts);
        map.put("other income", other_income);
        map.put("taxes", taxes);
        map.put("mortgage", mortgage);
        map.put("credit card", credit_card);
        map.put("utilities", utilities);
        map.put("food", food);
        map.put("car payment", car_payment);
        map.put("personal", personal);
        map.put("activities", activities);
        map.put("other expenses", other_expenses);
        map.put("date", date);

        return map;
    }

}