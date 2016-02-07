package com.rbsoftware.pfm.personalfinancemanager;

import android.util.Log;

import com.cloudant.sync.datastore.BasicDocumentRevision;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bogdan on 1/5/2016.
 * Holds method to work with currency
 */
public class Currency {
    private BasicDocumentRevision rev;
    private static final String DOC_TYPE = "CurrencyDocument";
    private final String type = DOC_TYPE;
    private String input;

    private Double EUR = 1.0;
    private Double USD = 1.0;
    private Double RUB = 1.0;

    //UAH
    private String EURtoUAH;
    private String USDtoUAH;
    private String RUBtoUAH;

    //USD
    private String EURtoUSD;   // if I want to sell EUR and buy USD then I need to use EURtoUSD
    private String RUBtoUSD;
    private String UAHtoUSD;   // if I want to sell UAH and buy USD then I need to use UAHtoUSD

    //EUR
    private String USDtoEUR;
    private String RUBtoEUR;
    private String UAHtoEUR;

    //RUB
    private String EURtoRUB;
    private String USDtoRUB;
    private String UAHtoRUB;

    private Currency() {
    }

    public Currency(String input) {
        this.input = input;
        parser();
    }


    private void setEURtoUSD(String EURtoUSD) {
        this.EURtoUSD = EURtoUSD;
    }

    private void setEURtoRUB(String EURtoRUB) {
        this.EURtoRUB = EURtoRUB;
    }

    private void setEURtoUAH(String EURtoUAH) {
        this.EURtoUAH = EURtoUAH;
    }

    private void setUSDtoEUR(String USDtoEUR) {
        this.USDtoEUR = USDtoEUR;
    }

    private void setUSDtoRUB(String USDtoRUB) {
        this.USDtoRUB = USDtoRUB;
    }

    private void setUSDtoUAH(String USDtoUAH) {
        this.USDtoUAH = USDtoUAH;
    }

    private void setRUBtoEUR(String RUBtoEUR) {
        this.RUBtoEUR = RUBtoEUR;
    }

    private void setRUBtoUSD(String RUBtoUSD) {
        this.RUBtoUSD = RUBtoUSD;
    }

    private void setRUBtoUAH(String RUBtoUAH) {
        this.RUBtoUAH = RUBtoUAH;
    }

    private void setUAHtoEUR(String UAHtoEUR) {
        this.UAHtoEUR = UAHtoEUR;
    }

    private void setUAHtoRUB(String UAHtoRUB) {
        this.UAHtoRUB = UAHtoRUB;
    }

    private void setUAHtoUSD(String UAHtoUSD) {
        this.UAHtoUSD = UAHtoUSD;
    }


    public Double getEURtoUSD() {
        return Double.valueOf(EURtoUSD);
    }

    public Double getEURtoRUB() {
        return Double.valueOf(EURtoRUB);
    }

    public Double getEURtoUAH() {
        return Double.valueOf(EURtoUAH);
    }

    public Double getUSDtoEUR() {
        return Double.valueOf(USDtoEUR);
    }

    public Double getUSDtoRUB() {
        return Double.valueOf(USDtoRUB);
    }

    public Double getUSDtoUAH() {
        return Double.valueOf(USDtoUAH);
    }

    public Double getRUBtoEUR() {
        return Double.valueOf(RUBtoEUR);
    }

    public Double getRUBtoUSD() {
        return Double.valueOf(RUBtoUSD);
    }

    public Double getRUBtoUAH() {
        return Double.valueOf(RUBtoUAH);
    }

    public Double getUAHtoEUR() {
        return Double.valueOf(UAHtoEUR);
    }

    public Double getUAHtoUSD() {
        return Double.valueOf(UAHtoUSD);
    }

    public Double getUAHtoRUB() {
        return Double.valueOf(UAHtoRUB);
    }


    private void parser() {
        String ccy;
//        String base_ccy;
        double buy;
        double sale;

        try {
            JSONArray jArray = new JSONArray(input);
            for (int i = 0; i < jArray.length(); i++) {

                JSONObject jObject = jArray.getJSONObject(i);

                ccy = jObject.getString("ccy");
//                base_ccy = jObject.getString("base_ccy");
                buy = jObject.getDouble("buy");
                sale = jObject.getDouble("sale");

                if (ccy.equals("EUR")) {
                    EUR = (sale + buy) / 2;
                }
                if (ccy.equals("USD")) {
                    USD = (sale + buy) / 2;
                }
                if (ccy.equals("RUR")) {
                    RUB = (sale + buy) / 2;
                }
            } // End Loop
        } catch (JSONException e) {
            Log.e("JSONException", "Error: " + e.toString());
        }// catch (JSONException e) */




        EURtoUAH = Double.toString(EUR);
        USDtoUAH = Double.toString(USD);
        RUBtoUAH = Double.toString(RUB);
        EURtoUSD = Double.toString(EUR / USD);
        RUBtoUSD = Double.toString(RUB / USD);
        UAHtoUSD = Double.toString(1 / USD);
        USDtoEUR = Double.toString(USD / EUR);
        RUBtoEUR = Double.toString(RUB / EUR);
        UAHtoEUR = Double.toString(1 / EUR);
        EURtoRUB = Double.toString(EUR / RUB);
        USDtoRUB = Double.toString(USD / RUB);
        UAHtoRUB = Double.toString(1 / RUB);

    } // public void parser()

    /**
     * Map
     *
     * @return map of
     */
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("EURtoUSD", EURtoUSD);
        map.put("EURtoRUB", EURtoRUB);
        map.put("EURtoUAH", EURtoUAH);
        map.put("USDtoEUR", USDtoEUR);
        map.put("USDtoRUB", USDtoRUB);
        map.put("USDtoUAH", USDtoUAH);
        map.put("RUBtoEUR", RUBtoEUR);
        map.put("RUBtoUSD", RUBtoUSD);
        map.put("RUBtoUAH", RUBtoUAH);
        map.put("UAHtoEUR", UAHtoEUR);
        map.put("UAHtoUSD", UAHtoUSD);
        map.put("UAHtoRUB", UAHtoRUB);
        map.put("type", type);

        return map;
    }



    public BasicDocumentRevision getDocumentRevision() {
        return rev;
    }

    public static Currency fromRevision(BasicDocumentRevision rev) {
        Currency t = new Currency();
        t.rev = rev;
        Map<String, Object> map = rev.asMap();
        if (map.containsKey("type") && map.get("type").equals(Currency.DOC_TYPE)) {
            t.setEURtoUSD((String) map.get("EURtoUSD"));
            t.setEURtoRUB((String) map.get("EURtoRUB"));
            t.setEURtoUAH((String) map.get("EURtoUAH"));
            t.setUSDtoEUR((String) map.get("USDtoEUR"));
            t.setUSDtoRUB((String) map.get("USDtoRUB"));
            t.setUSDtoUAH((String) map.get("USDtoUAH"));
            t.setRUBtoEUR((String) map.get("RUBtoEUR"));
            t.setRUBtoUSD((String) map.get("RUBtoUSD"));
            t.setRUBtoUAH((String) map.get("RUBtoUAH"));
            t.setUAHtoEUR((String) map.get("UAHtoEUR"));
            t.setUAHtoUSD((String) map.get("UAHtoUSD"));
            t.setUAHtoRUB((String) map.get("UAHtoRUB"));

            return t;
        }
        return null;
    }
}