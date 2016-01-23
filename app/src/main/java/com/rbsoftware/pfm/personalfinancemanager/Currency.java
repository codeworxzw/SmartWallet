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
 */
public class Currency {

    static final String DOC_TYPE = "CurrencyDocument";
    private String type = DOC_TYPE;
    private String input;

    private Double EUR = new Double(0);
    private Double USD = new Double(0);
    private Double RUR = new Double(0);

    //UAH
    private String EURtoUAH;
    private String USDtoUAH;
    private String RURtoUAH;

    //USD
    private String EURtoUSD;   // if I want to sell EUR and buy USD then I need to use EURtoUSD
    private String RURtoUSD;
    private String UAHtoUSD;   // if I want to sell UAH and buy USD then I need to use UAHtoUSD

    //EUR
    private String USDtoEUR;
    private String RURtoEUR;
    private String UAHtoEUR;

    //RUR
    private String EURtoRUR;
    private String USDtoRUR;
    private String UAHtoRUR;

    private Currency() {}

    public Currency(String input) {
        this.input = input;
        parser();
    }


    public  void setEURtoUSD (String EURtoUSD ){this.EURtoUSD = EURtoUSD;}
    public  void setEURtoRUR (String EURtoRUR ){this.EURtoRUR = EURtoRUR;}
    public  void setEURtoUAH (String EURtoUAH ){this.EURtoUAH = EURtoUAH;}
    public  void setUSDtoEUR (String USDtoEUR ){this.USDtoEUR = USDtoEUR;}
    public  void setUSDtoRUR (String USDtoRUR ){this.USDtoRUR = USDtoRUR;}
    public  void setUSDtoUAH (String USDtoUAH ){this.USDtoUAH = USDtoUAH;}
    public  void setRURtoEUR (String RURtoEUR ){this.RURtoEUR = RURtoEUR;}
    public  void setRURtoUSD (String RURtoUSD ){this.RURtoUSD = RURtoUSD;}
    public  void setRURtoUAH (String RURtoUAH ){this.RURtoUAH = RURtoUAH;}
    public  void setUAHtoEUR (String UAHtoEUR ){this.UAHtoEUR = UAHtoEUR;}
    public  void setUAHtoRUR (String UAHtoRUR ){this.UAHtoRUR = UAHtoRUR;}
    public  void setUAHtoUSD (String UAHtoUSD ){this.UAHtoUSD = UAHtoUSD;}


    public Double getEURtoUSD() {return Double.valueOf(EURtoUSD);}
    public Double getEURtoRUR() {return Double.valueOf(EURtoRUR);}
    public Double getEURtoUAH() {return Double.valueOf(EURtoUAH);}
    public Double getUSDtoEUR() {return Double.valueOf(USDtoEUR);}
    public Double getUSDtoRUR() {return Double.valueOf(USDtoRUR);}
    public Double getUSDtoUAH() {return Double.valueOf(USDtoUAH);}
    public Double getRURtoEUR() {return Double.valueOf(RURtoEUR);}
    public Double getRURtoUSD() {return Double.valueOf(RURtoUSD);}
    public Double getRURtoUAH() {return Double.valueOf(RURtoUAH);}
    public Double getUAHtoEUR() {return Double.valueOf(UAHtoEUR);}
    public Double getUAHtoUSD() {return Double.valueOf(UAHtoUSD);}
    public Double getUAHtoRUR() {return Double.valueOf(UAHtoRUR);}



    public void parser() {
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
                    RUR = (sale + buy) / 2;
                }
            } // End Loop
        } catch (JSONException e) {
            Log.e("JSONException", "Error: " + e.toString());
        }// catch (JSONException e) */

        Log.d("TAG", EUR + " ");
        Log.d("TAG", USD + " ");
        Log.d("TAG", RUR + " ");


        EURtoUAH = Double.toString(EUR);
        USDtoUAH = Double.toString(USD);
        RURtoUAH = Double.toString(RUR);
        EURtoUSD = Double.toString(EUR/USD);
        RURtoUSD = Double.toString(RUR/USD);
        UAHtoUSD = Double.toString(1/USD);
        USDtoEUR = Double.toString(USD/EUR);
        RURtoEUR = Double.toString(RUR/EUR);
        UAHtoEUR = Double.toString(1/EUR);
        EURtoRUR = Double.toString(EUR/RUR);
        USDtoRUR = Double.toString(USD/RUR);
        UAHtoRUR = Double.toString(1/RUR);

    } // public void parser()

    /**
     * Map
     * @return map of
     */
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("EURtoUSD", EURtoUSD);
        map.put("EURtoRUR", EURtoRUR);
        map.put("EURtoUAH", EURtoUAH);
        map.put("USDtoEUR", USDtoEUR);
        map.put("USDtoRUR", USDtoRUR);
        map.put("USDtoUAH", USDtoUAH);
        map.put("RURtoEUR", RURtoEUR);
        map.put("RURtoUSD", RURtoUSD);
        map.put("RURtoUAH", RURtoUAH);
        map.put("UAHtoEUR", UAHtoEUR);
        map.put("UAHtoUSD", UAHtoUSD);
        map.put("UAHtoRUR", UAHtoRUR);
        map.put("type", type);

        return map;
    }

    private BasicDocumentRevision rev;
    public BasicDocumentRevision getDocumentRevision() {
        return rev;
    }

    public static Currency fromRevision(BasicDocumentRevision rev) {
        Currency t = new Currency();
        t.rev = rev;
        Map<String, Object> map = rev.asMap();
        if(map.containsKey("type") && map.get("type").equals(Currency.DOC_TYPE)) {
            t.setEURtoUSD((String) map.get("EURtoUSD"));
            t.setEURtoRUR((String) map.get("EURtoRUR"));
            t.setEURtoUAH((String) map.get("EURtoUAH"));
            t.setUSDtoEUR((String) map.get("USDtoEUR"));
            t.setUSDtoRUR((String) map.get("USDtoRUR"));
            t.setUSDtoUAH((String) map.get("USDtoUAH"));
            t.setRURtoEUR((String) map.get("RURtoEUR"));
            t.setRURtoUSD((String) map.get("RURtoUSD"));
            t.setRURtoUAH((String) map.get("RURtoRUR"));
            t.setUAHtoEUR((String) map.get("UAHtoEUR"));
            t.setUAHtoUSD((String) map.get("UAHtoUSD"));
            t.setUAHtoRUR((String) map.get("UAHtoRUR"));

            return t;
        }
        return null;
    }
}