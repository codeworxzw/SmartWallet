package com.rbsoftware.pfm.personalfinancemanager;

import com.cloudant.sync.datastore.BasicDocumentRevision;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by burzakovskiy on 11/24/2015.
 */
public class FinanceDocument {
    static final Object DOC_TYPE = "Finance document";

    private FinanceDocument() {}

    public FinanceDocument(List<Object> params) {
        /*0 - type, 1 - id, 2 - salary, 3  - rental income, 4 - interest, 5 - gifts, 6 - other income
        7 - taxes, 8 - mortgage, 9 - credit card,
        10 - utilities (Electric bill, Water bill, Gas bill, Phone bill, Internet service, Cable or satellite service),
        11 - food (Groceries, Dining out),
        12 - car payment (Fuel, Auto insurance, Tires and maintenance, Tag/registration),
        13 - personal (Clothing, Hair care, Medical expenses),
        14 - activities (Gym membership, Vacation, Charitable giving, Entertainment,Gifts),
        15 - other expenses
        */

        this.type = params.get(0);
        this.data = params.get(1);
        this.salary = params.get(2);
        this.rental_income = params.get(3);
        this.interest = params.get(4);
        this.gifts = params.get(5);
        this.other_income = params.get(6);
        this.taxes = params.get(7);
        this.mortgage = params.get(8);
        this.credit_card = params.get(9);
        this.utilities = params.get(10);
        this.food = params.get(11);
        this.car_payment = params.get(12);
        this.personal = params.get(13);
        this.activities = params.get(14);
        this.other_expenses = params.get(15);


        this.setType(DOC_TYPE);
        this.setData(data);
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
    private Object type = DOC_TYPE;
    public Object getType() {
        return type;
    }
    public void setType(Object type) {
        this.type = type;
    }
//data
    private Object data;
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
//salary
    private Object salary;
    public Object getSalary() {
        return salary;
    }
    public void setSalary(Object salary) {
        this.salary = salary;
    }
//rental income
    private Object rental_income;
    public Object getRental_income() {
        return rental_income;
    }
    public void setRental_income(Object rental_income) {
        this.rental_income = rental_income;
    }
//interest
    private Object interest;
    public Object getInterest() {
        return interest;
    }
    public void setInterest(Object interest) {
        this.interest = interest;
    }

//gifts
    private Object gifts;
    public Object getGifts() {
        return gifts;
    }
    public void setGifts(Object gifts) {
        this.gifts = gifts;
    }

//other income
    private Object other_income;
    public Object getOther_income() {
        return other_income;
    }
    public void setOther_income(Object other_income) {
        this.other_income = other_income;
    }

//7 - taxes
    private Object taxes;
    public Object getTaxes() {
        return taxes;
    }
    public void setTaxes(Object taxes) {
        this.taxes = taxes;
    }

// 8 - mortgage
    private Object mortgage;
    public Object getMortgage() {
        return mortgage;
    }
    public void setMortgage(Object mortgage) {
        this.mortgage = mortgage;
    }

// 9 - credit card
    private Object credit_card;
    public Object getCredit_card() {
        return credit_card;
    }
    public void setCredit_card(Object credit_card) {
        this.credit_card = credit_card;
    }

//10 - utilities
    private Object utilities;
    public Object getUtilities() {
        return utilities;
    }
    public void setUtilities(Object utilities) {
        this.utilities = utilities;
    }

//11 - food
    private Object food;
    public Object getFood() {
        return food;
    }
    public void setFood(Object food) {
        this.food = food;
    }

//12 - car payment
    private Object car_payment;
    public Object getCar_payment() {
        return car_payment;
    }
    public void setCar_payment(Object car_payment) {
        this.car_payment = car_payment;
    }

//13 - personal
    private Object personal;
    public Object getPersonal() {
        return personal;
    }
    public void setPersonal(Object personal) {
        this.personal = personal;
    }

//14 - activities
    private Object activities;
    public Object getActivities() {
        return activities;
    }
    public void setActivities(Object activities) {
        this.activities = activities;
    }

//15 - other expenses
    private Object other_expenses;
    public Object getOther_expenses() {
        return other_expenses;
    }
    public void setOther_expenses(Object other_expenses) {
        this.other_expenses = other_expenses;
    }

    //date
    SimpleDateFormat currDate = new SimpleDateFormat("yyyy-mm-dd hh:mm");
    String date = currDate.format(new Date());

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
            t.setType((String) map.get("type"));
            t.setData((String) map.get("id"));
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
        map.put("id", data);
        map.put("salary", salary);
        map.put("rental income", rental_income);
        map.put("interest", interest);
        map.put("gifts", gifts);
        map.put("other_income", other_income);
        map.put("taxes", taxes);
        map.put("mortgage", mortgage);
        map.put("credit_card", credit_card);
        map.put("utilities", utilities);
        map.put("food", food);
        map.put("car_payment", car_payment);
        map.put("personal", personal);
        map.put("activities", activities);
        map.put("other_expenses", other_expenses);

        return map;
    }

}