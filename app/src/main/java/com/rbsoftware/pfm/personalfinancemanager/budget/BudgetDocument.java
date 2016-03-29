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
 * Holds method for managing budget document
 *
 * @author Roman Burzakovskiy
 */
public class BudgetDocument {
    public static final String DOC_TYPE = "Budget Document";
    private BasicDocumentRevision rev;

    private String userId;
    private String type;
    private String name;
    private ArrayList<String> value = new ArrayList<>();
    private boolean isActive;
    private String date;
    private String account;
    private String period;

    private BudgetDocument() {

    }

    /**
     * Constructor of budget document
     *
     * @param userId    id of current user
     * @param period    of budget
     * @param name      of budget
     * @param value     of budget
     * @param isActive  status of budget
     */
    public BudgetDocument(String userId, String period, String name, ArrayList<String> value,boolean isActive) {
        this.userId = userId;
        Date currDate = new Date();
        this.date = Long.toString(currDate.getTime() / 1000);
        this.type = DOC_TYPE;
        this.name = name;
        this.value = value;
        this.isActive = isActive;
        this.setAccount(FinanceDocument.MAIN_ACCOUNT);
        this.period = period;

    }

    /**
     * Gets budget name
     *
     * @return budget name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets budget name
     *
     * @param name of budget
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets budget period
     *
     * @return budget period
     */
    public String getPeriod() {
        return period;
    }

    /**
     * Sets budget period
     *
     * @param period of budget
     */
    public void setPeriod(String period) {
        this.period = period;
    }

    /**
     * Gets budget value
     *
     * @return budget value
     */
    public int getValue() {

        if (value == null) {
            return 0;
        } else if (value.get(1).equals(MainActivity.defaultCurrency)) {

            return Integer.valueOf(value.get(0));
        } else {

            return CurrencyConversion.convertCurrency(Integer.valueOf(value.get(0)), value.get(1), MainActivity.defaultCurrency);
        }
    }

    /**
     * Sets budget value
     *
     * @param value of budget
     */
    public void setValue(ArrayList<String> value) {
        this.value = value;
    }




    /**
     * Gets budget status
     *
     * @return true if budget is active
     */
    public boolean getActive() {
        return isActive;
    }

    /**
     * Sets budget status
     *
     * @param isActive budget status
     */
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Sets budget date
     *
     * @param date of budget creation or update
     */
    private void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets budget date
     *
     * @return budget date in unix format
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets document type of budget document
     *
     * @param type of document
     */
    private void setType(String type) {
        this.type = type;
    }

    public String getuserId() {
        return userId;
    }

    /**
     * Sets users account to document
     *
     * @param account of user
     */
    private void setAccount(String account) {
        this.account = account;
    }

    private String getAccount() {
        return this.account;
    }

    /**
     * Sets userid to document
     *
     * @param data of user
     */
    private void setUserId(String data) {
        this.userId = data;
    }

    /**
     * Builds map of budget document values
     *
     * @return map of values
     */
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("userId", userId);
        map.put("account", account);
        map.put("date", date);
        map.put("name", name);
        map.put("value", value);
        map.put("isActive", isActive);
        map.put("period", period);

        return map;
    }

    /**
     * Gets document revision
     *
     * @return revision of document
     */
    public BasicDocumentRevision getDocumentRevision() {
        return rev;
    }

    /**
     * Creates document from revision
     *
     * @param rev document revision
     * @return budget document
     */
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
            t.setActive((boolean) map.get("isActive"));
            return t;
        }
        return null;
    }


}
