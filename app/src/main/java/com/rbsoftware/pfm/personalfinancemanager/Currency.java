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
    private Double GBP = 1.0;

    //UAH
    private String EURtoUAH;
    private String USDtoUAH;
    private String RUBtoUAH;
    private String BTCtoUAH;
    private String GBPtoUAH;

    //USD
    private String EURtoUSD;   // if I want to sell EUR and buy USD then I need to use EURtoUSD
    private String RUBtoUSD;
    private String UAHtoUSD;   // if I want to sell UAH and buy USD then I need to use UAHtoUSD
    private String BTCtoUSD;
    private String GBPtoUSD;

    //EUR
    private String USDtoEUR;
    private String RUBtoEUR;
    private String UAHtoEUR;
    private String BTCtoEUR;
    private String GBPtoEUR;

    //RUB
    private String EURtoRUB;
    private String USDtoRUB;
    private String UAHtoRUB;
    private String BTCtoRUB;
    private String GBPtoRUB;

    //BTC
    private String EURtoBTC;
    private String USDtoBTC;
    private String UAHtoBTC;
    private String RUBtoBTC;
    private String GBPtoBTC;

    //GBP
    private String EURtoGBP;
    private String USDtoGBP;
    private String UAHtoGBP;
    private String RUBtoGBP;
    private String BTCtoGBP;



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

    private void setEURtoGBP(String EURtoGBP) {
        this.EURtoGBP = EURtoGBP;
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

    private void setUSDtoGBP(String USDtoGBP) {
        this.USDtoGBP = USDtoGBP;
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

    private void setRUBtoGBP(String RUBtoGBP) {
        this.RUBtoGBP = RUBtoGBP;
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

    private void setUAHtoGBP(String UAHtoGBP) {
        this.UAHtoGBP = UAHtoGBP;
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

    private void setBTCtoGBP(String BTCtoGBP) {
        this.BTCtoGBP = BTCtoGBP;
    }

    private void setGBPtoUSD(String GBPtoUSD) {
        this.GBPtoUSD = GBPtoUSD;
    }

    private void setGBPtoEUR(String GBPtoEUR) {
        this.GBPtoEUR = GBPtoEUR;
    }

    private void setGBPtoRUB(String GBPtoRUB) {
        this.GBPtoRUB = GBPtoRUB;
    }

    private void setGBPtoUAH(String GBPtoUAH) {
        this.GBPtoUAH = GBPtoUAH;
    }

    private void setGBPtoBTC(String GBPtoBTC) {
        this.GBPtoBTC = GBPtoBTC;
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

    public Double getEURtoGBP() {
        return Double.valueOf(EURtoGBP);
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

    public Double getUSDtoGBP() {
        return Double.valueOf(USDtoGBP);
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

    public Double getRUBtoGBP() {
        return Double.valueOf(RUBtoGBP);
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

    public Double getUAHtoGBP() {
        return Double.valueOf(UAHtoGBP);
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

    public Double getBTCtoGBP() {
        return Double.valueOf(BTCtoGBP);
    }

    public Double getGBPtoUSD() {
        return Double.valueOf(GBPtoUSD);
    }

    public Double getGBPtoEUR() {
        return Double.valueOf(GBPtoEUR);
    }

    public Double getGBPtoRUB() {
        return Double.valueOf(GBPtoRUB);
    }

    public Double getGBPtoUAH() {
        return Double.valueOf(GBPtoUAH);
    }

    public Double getGBPtoBTC() {
        return Double.valueOf(GBPtoBTC);
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
                if (ccy.equals("GBP")) {
                    GBP = (sale + buy) / 2;
                }
            } // End Loop
        } catch (JSONException e) {
            Log.e("JSONException", "Error: " + e.toString());
        }// catch (JSONException e) */



        EURtoUAH = Double.toString(EUR);
        USDtoUAH = Double.toString(USD);
        RUBtoUAH = Double.toString(RUB);
        GBPtoUAH = Double.toString(GBP);
        EURtoUSD = Double.toString(EUR / USD);
        RUBtoUSD = Double.toString(RUB / USD);
        GBPtoUSD = Double.toString(GBP / USD);
        UAHtoUSD = Double.toString(1 / USD);
        USDtoEUR = Double.toString(USD / EUR);
        RUBtoEUR = Double.toString(RUB / EUR);
        GBPtoEUR = Double.toString(GBP / EUR);
        UAHtoEUR = Double.toString(1 / EUR);
        EURtoRUB = Double.toString(EUR / RUB);
        USDtoRUB = Double.toString(USD / RUB);
        GBPtoRUB = Double.toString(GBP / RUB);
        UAHtoRUB = Double.toString(1 / RUB);
        USDtoGBP = Double.toString(USD / GBP);
        RUBtoGBP = Double.toString(RUB / GBP);
        EURtoGBP = Double.toString(EUR / GBP);
        UAHtoGBP = Double.toString(1 / GBP);

        //BTC
        BTCtoUSD = Double.toString(BTC);
        USDtoBTC = Double.toString(1 / BTC);
        EURtoBTC = Double.toString((1 / BTC)*(USD / EUR));
        BTCtoEUR = Double.toString((BTC)*(USD / EUR));
        GBPtoBTC = Double.toString((1 / BTC)*(USD / GBP));
        BTCtoGBP = Double.toString((BTC)*(USD / GBP));
        RUBtoBTC = Double.toString((1 /BTC)*(RUB / USD));
        BTCtoRUB = Double.toString((BTC)*(USD / RUB));
        UAHtoBTC = Double.toString((1 / (BTC * USD))); // 1/(410*26)
        BTCtoUAH = Double.toString((BTC)*(USD)); // 1 BTC = 410*26 = 10K UAH

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
        map.put("EURtoGBP", EURtoGBP);
        map.put("USDtoEUR", USDtoEUR);
        map.put("USDtoRUB", USDtoRUB);
        map.put("USDtoUAH", USDtoUAH);
        map.put("USDtoBTC", USDtoBTC);
        map.put("USDtoGBP", USDtoGBP);
        map.put("RUBtoEUR", RUBtoEUR);
        map.put("RUBtoUSD", RUBtoUSD);
        map.put("RUBtoUAH", RUBtoUAH);
        map.put("RUBtoBTC", RUBtoBTC);
        map.put("RUBtoGBP", RUBtoGBP);
        map.put("UAHtoEUR", UAHtoEUR);
        map.put("UAHtoUSD", UAHtoUSD);
        map.put("UAHtoRUB", UAHtoRUB);
        map.put("UAHtoBTC", UAHtoBTC);
        map.put("UAHtoGBP", UAHtoGBP);
        map.put("BTCtoEUR", BTCtoEUR);
        map.put("BTCtoUSD", BTCtoUSD);
        map.put("BTCtoRUB", BTCtoRUB);
        map.put("BTCtoUAH", BTCtoUAH);
        map.put("BTCtoGBP", BTCtoGBP);
        map.put("GBPtoUSD", GBPtoUSD);
        map.put("GBPtoEUR", GBPtoEUR);
        map.put("GBPtoRUB", GBPtoRUB);
        map.put("GBPtoUAH", GBPtoUAH);
        map.put("GBPtoBTC", GBPtoBTC);
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
            t.setEURtoGBP((String) map.get("EURtoGBP"));
            t.setUSDtoEUR((String) map.get("USDtoEUR"));
            t.setUSDtoRUB((String) map.get("USDtoRUB"));
            t.setUSDtoUAH((String) map.get("USDtoUAH"));
            t.setUSDtoBTC((String) map.get("USDtoBTC"));
            t.setUSDtoGBP((String) map.get("USDtoGBP"));
            t.setRUBtoEUR((String) map.get("RUBtoEUR"));
            t.setRUBtoUSD((String) map.get("RUBtoUSD"));
            t.setRUBtoUAH((String) map.get("RUBtoUAH"));
            t.setRUBtoBTC((String) map.get("RUBtoBTC"));
            t.setRUBtoGBP((String) map.get("RUBtoGBP"));
            t.setUAHtoEUR((String) map.get("UAHtoEUR"));
            t.setUAHtoUSD((String) map.get("UAHtoUSD"));
            t.setUAHtoRUB((String) map.get("UAHtoRUB"));
            t.setUAHtoBTC((String) map.get("UAHtoBTC"));
            t.setUAHtoGBP((String) map.get("UAHtoGBP"));
            t.setBTCtoEUR((String) map.get("BTCtoEUR"));
            t.setBTCtoUSD((String) map.get("BTCtoUSD"));
            t.setBTCtoRUB((String) map.get("BTCtoRUB"));
            t.setBTCtoUAH((String) map.get("BTCtoUAH"));
            t.setBTCtoGBP((String) map.get("BTCtoGBP"));
            t.setGBPtoEUR((String) map.get("GBPtoEUR"));
            t.setGBPtoUSD((String) map.get("GBPtoUSD"));
            t.setGBPtoRUB((String) map.get("GBPtoRUB"));
            t.setGBPtoUAH((String) map.get("GBPtoUAH"));
            t.setGBPtoBTC((String) map.get("GBPtoBTC"));


            return t;
        }
        return null;
    }
}