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
    private String input;

    private Double EUR = 1.0;
    private Double USD = 1.0;
    private Double RUB = 1.0;
    private Double BTC = 1.0;

    //UAH
    private String EURtoUAH;
    private String USDtoUAH;
    private String RUBtoUAH;
    private String BTCtoUAH;

    //USD
    private String EURtoUSD;   // if I want to sell EUR and buy USD then I need to use EURtoUSD
    private String RUBtoUSD;
    private String UAHtoUSD;   // if I want to sell UAH and buy USD then I need to use UAHtoUSD
    private String BTCtoUSD;

    //EUR
    private String USDtoEUR;
    private String RUBtoEUR;
    private String UAHtoEUR;
    private String BTCtoEUR;

    //RUB
    private String EURtoRUB;
    private String USDtoRUB;
    private String UAHtoRUB;
    private String BTCtoRUB;

    //BTC
    private String EURtoBTC;
    private String USDtoBTC;
    private String UAHtoBTC;
    private String RUBtoBTC;

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

    private void setEURtoBTC(String EURtoBTC) {
        this.EURtoBTC = EURtoBTC;
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

    private void setUSDtoBTC(String USDtoBTC) {
        this.USDtoBTC = USDtoBTC;
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

    private void setRUBtoBTC(String RUBtoBTC) {
        this.RUBtoBTC = RUBtoBTC;
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

    private void setUAHtoBTC(String UAHtoBTC) {
        this.UAHtoBTC = UAHtoBTC;
    }

    private void setBTCtoEUR(String BTCtoEUR) {
        this.BTCtoEUR = BTCtoEUR;
    }

    private void setBTCtoUSD(String BTCtoUSD) {
        this.BTCtoUSD = BTCtoUSD;
    }

    private void setBTCtoRUB(String BTCtoRUB) {
        this.BTCtoRUB = BTCtoRUB;
    }

    private void setBTCtoUAH(String BTCtoUAH) {
        this.BTCtoUAH = BTCtoUAH;
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

    public Double getEURtoBTC() {
        return Double.valueOf(EURtoBTC);
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

    public Double getUSDtoBTC() {
        return Double.valueOf(USDtoBTC);
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

    public Double getRUBtoBTC() {
        return Double.valueOf(RUBtoBTC);
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

    public Double getUAHtoBTC() {
        return Double.valueOf(UAHtoBTC);
    }

    public Double getBTCtoEUR() {
        return Double.valueOf(BTCtoEUR);
    }

    public Double getBTCtoUSD() {
        return Double.valueOf(BTCtoUSD);
    }

    public Double getBTCtoRUB() {
        return Double.valueOf(BTCtoRUB);
    }

    public Double getBTCtoUAH() {
        return Double.valueOf(BTCtoUAH);
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
                if (ccy.equals("BTC")) {
                    BTC = (sale + buy) / 2;
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

        //BTC
        BTCtoUSD = Double.toString(BTC);
        USDtoBTC = Double.toString(1 / BTC);

        EURtoBTC = Double.toString((1 / BTC)*(USD / EUR));
        BTCtoEUR = Double.toString((BTC)*(USD / EUR));

        RUBtoBTC = Double.toString((1 /BTC)*(RUB / USD));
        BTCtoRUB = Double.toString((BTC)*(USD / RUB));

        UAHtoBTC = Double.toString((1 / (BTC * USD))); // 1/(410*26)
        BTCtoUAH = Double.toString((BTC)*(USD)); // 1 BTC = 410*26 = 10K UAH

        Log.d("TAG", "USDtoBTC" + USDtoBTC + "\n"
        + "EURtoBTC" + EURtoBTC + "\n"
        + "RUBtoBTC" + RUBtoBTC + "\n"
        + "UAHtoBTC" + UAHtoBTC + "\n"
        + "BTCtoUSD" + BTCtoUSD + "\n"
        + "BTCtoEUR" + BTCtoEUR + "\n"
        + "BTCtoRUB" + BTCtoRUB + "\n"
        + "BTCtoUAH" + BTCtoUAH + "\n");
    } // public void parser()

    /**
     * Map
     *
     * @return map of
     */
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("EURtoUSD", EURtoUSD);
        map.put("EURtoRUB", EURtoRUB);
        map.put("EURtoUAH", EURtoUAH);
        map.put("EURtoBTC", EURtoBTC);
        map.put("USDtoEUR", USDtoEUR);
        map.put("USDtoRUB", USDtoRUB);
        map.put("USDtoUAH", USDtoUAH);
        map.put("USDtoBTC", USDtoBTC);
        map.put("RUBtoEUR", RUBtoEUR);
        map.put("RUBtoUSD", RUBtoUSD);
        map.put("RUBtoUAH", RUBtoUAH);
        map.put("RUBtoBTC", RUBtoBTC);
        map.put("UAHtoEUR", UAHtoEUR);
        map.put("UAHtoUSD", UAHtoUSD);
        map.put("UAHtoRUB", UAHtoRUB);
        map.put("UAHtoBTC", UAHtoBTC);
        map.put("BTCtoEUR", BTCtoEUR);
        map.put("BTCtoUSD", BTCtoUSD);
        map.put("BTCtoRUB", BTCtoRUB);
        map.put("BTCtoUAH", BTCtoUAH);
        map.put("type", DOC_TYPE);

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
            t.setEURtoBTC((String) map.get("EURtoBTC"));
            t.setUSDtoEUR((String) map.get("USDtoEUR"));
            t.setUSDtoRUB((String) map.get("USDtoRUB"));
            t.setUSDtoUAH((String) map.get("USDtoUAH"));
            t.setUSDtoBTC((String) map.get("USDtoBTC"));
            t.setRUBtoEUR((String) map.get("RUBtoEUR"));
            t.setRUBtoUSD((String) map.get("RUBtoUSD"));
            t.setRUBtoUAH((String) map.get("RUBtoUAH"));
            t.setRUBtoBTC((String) map.get("RUBtoBTC"));
            t.setUAHtoEUR((String) map.get("UAHtoEUR"));
            t.setUAHtoUSD((String) map.get("UAHtoUSD"));
            t.setUAHtoRUB((String) map.get("UAHtoRUB"));
            t.setUAHtoBTC((String) map.get("UAHtoBTC"));
            t.setBTCtoEUR((String) map.get("BTCtoEUR"));
            t.setBTCtoUSD((String) map.get("BTCtoUSD"));
            t.setBTCtoRUB((String) map.get("BTCtoRUB"));
            t.setBTCtoUAH((String) map.get("BTCtoUAH"));

            return t;
        }
        return null;
    }
}