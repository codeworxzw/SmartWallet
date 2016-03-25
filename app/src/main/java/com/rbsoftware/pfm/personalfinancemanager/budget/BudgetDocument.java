package com.rbsoftware.pfm.personalfinancemanager.budget;

import com.cloudant.sync.datastore.BasicDocumentRevision;
import com.rbsoftware.pfm.personalfinancemanager.CurrencyConversion;
import com.rbsoftware.pfm.personalfinancemanager.FinanceDocument;
import com.rbsoftware.pfm.personalfinancemanager.MainActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by burzakovskiy on 3/22/2016.
 */
public class BudgetDocument {
    public static final String DOC_TYPE = "Budget Document";
    private BasicDocumentRevision rev;

    private String userId;
    private String type;
    private String name;
    private ArrayList<String> value = new ArrayList<>();
    private ArrayList<String> threshold = new ArrayList<>();
    private boolean isActive;
    private String date;
    private String account;
    private String period;

    private BudgetDocument() {

    }

    public BudgetDocument(String userId, String period, String name, ArrayList<String> value, ArrayList<String> threshold, boolean isActive) {
        this.userId = userId;
        Date currDate = new Date();
        this.date = Long.toString(currDate.getTime() / 1000);
        this.type = DOC_TYPE;
        this.name = name;
        this.value = value;
        this.threshold = threshold;
        this.isActive = isActive;
        this.setAccount(FinanceDocument.MAIN_ACCOUNT);
        this.period = period;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getValue() {

        if (value == null) {
            return 0;
        } else if (value.get(1).equals(MainActivity.defaultCurrency)) {

            return Integer.valueOf(value.get(0));
        } else {

            return CurrencyConversion.convertCurrency(Integer.valueOf(value.get(0)), value.get(1), MainActivity.defaultCurrency);
        }
    }

    public void setValue(ArrayList<String> value) {
        this.value = value;
    }

    public int getThreshold() {
        if (threshold == null) {
            return 0;
        } else if (threshold.get(1).equals(MainActivity.defaultCurrency)) {

            return Integer.valueOf(threshold.get(0));
        } else {

            return CurrencyConversion.convertCurrency(Integer.valueOf(threshold.get(0)), threshold.get(1), MainActivity.defaultCurrency);
        }
    }

    public void setThreshold(ArrayList<String> threshold) {
        this.threshold = threshold;
    }

    public boolean getActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    private void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    private void setType(String type) {
        this.type = type;
    }

    public String getuserId() {
        return userId;
    }

    //account
    private void setAccount(String account) {
        this.account = account;
    }

    private String getAccount() {
        return this.account;
    }

    private void setUserId(String data) {
        this.userId = data;
    }

    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("userId", userId);
        map.put("account", account);
        map.put("date", date);
        map.put("name", name);
        map.put("value", value);
        map.put("threshold", threshold);
        map.put("isActive", isActive);
        map.put("period", period);

        return map;
    }

    public BasicDocumentRevision getDocumentRevision() {
        return rev;
    }

    public static BudgetDocument fromRevision(BasicDocumentRevision rev) {
        BudgetDocument t = new BudgetDocument();
        t.rev = rev;
        Map<String, Object> map = rev.asMap();
        if (map.containsKey("type") && map.get("type").equals(DOC_TYPE)) {
            t.setUserId((String) map.get("userId"));
            t.setDate((String) map.get("date"));
            t.setType((String) map.get("type"));
            t.setAccount((String) map.get("account"));
            t.setName((String) map.get("name"));
            t.setPeriod((String) map.get("period"));
            t.setValue((ArrayList<String>) map.get("value"));
            t.setThreshold((ArrayList<String>) map.get("threshold"));
            t.setActive((boolean) map.get("isActive"));
            return t;
        }
        return null;
    }


}
