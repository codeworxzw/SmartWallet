package com.rbsoftware.pfm.personalfinancemanager;

import android.util.Log;

import com.cloudant.sync.datastore.BasicDocumentRevision;
import com.cloudant.sync.datastore.ConflictException;
import com.cloudant.sync.datastore.DocumentBodyFactory;
import com.cloudant.sync.datastore.DocumentException;
import com.cloudant.sync.datastore.DocumentNotFoundException;
import com.cloudant.sync.datastore.MutableDocumentRevision;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Bohdan on 1/5/2016.
 */
public class Currency {
    static final String DOC_TYPE = "Currency  document";

    private String input;

    private String ccy;
    private String base_ccy;
    private double buy;
    private double sale;

    private double EUR;
    private double USD;
    private double RUR;

    //UAH
    private double EURtoUAH = EUR;
    private double USDtoUAH = USD;
    private double RURtoUAH = RUR;

    //USD
    private double EURtoUSD = EUR/USD;   // if I want to sell EUR and buy USD then I need to use EURtoUSD
    private double RURtoUSD = RUR/USD;
    private double UAHtoUSD = 1/USD;     // if I want to sell UAH and buy USD then I need to use UAHtoUSD

    //EUR
    private double USDtoEUR = USD/EUR;
    private double RURtoEUR = RUR/EUR;
    private double UAHtoEUR = 1/EUR;

    //RUR
    private double EURtoRUR = EUR/RUR;
    private double USDtoRUR = USD/RUR;
    private double UAHtoRUR = 1/RUR;



    public Currency(String input) {

        this.input = input;

            /*this.setCcy(ccy);
            this.setBase_ccy(base_ccy);
            this.setBuy(buy);
            this.setSale(sale);*/
    }

    /*
        public String getCcy() {return ccy;}
        public void setCcy(String ccy) {
            this.ccy = ccy;
        }

        public String getBase_ccy() {return base_ccy;}
        public void setBase_ccy(String base_ccy) {
            this.base_ccy = base_ccy;
        }

        public double getBuy() {return buy;}
        public void setBuy(double buy) {
            this.buy = buy;
        }

        public double getSale() {return sale;}
        public void setSale(double sale) {
            this.sale = sale;
        }
    */
    public void Parser() {
        try {
            JSONArray jArray = new JSONArray(input);
            for (int i = 0; i < jArray.length(); i++) {

                JSONObject jObject = jArray.getJSONObject(i);

                ccy = jObject.getString("ccy");
                base_ccy = jObject.getString("base_ccy");
                buy = jObject.getDouble("buy");
                sale = jObject.getDouble("sale");

                Log.d("TAG", ccy + "   " + base_ccy + "   " + buy + "   " + sale);

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
    } // public void Parser()


}
    /**
     * Creates a task, assigning an ID.
     * @param document task to create
     * @return new revision of the document
     */
/*
    public Currency createDocument(Currency document) {
        MutableDocumentRevision rev = new MutableDocumentRevision();

        rev.body = DocumentBodyFactory.create(document.asMap());
        try {
            BasicDocumentRevision created = this.mDatastore.createDocumentFromRevision(rev);

            return Currency.fromRevision(created);

        } catch (DocumentException de) {
            Log.e("Doc", "document was not created");
            return null;
        }
    }
*/

    /**
     * Updates a Task document within the datastore.
     * @param document document to update
     * @return the updated revision of the Task
     * @throws ConflictException if the document passed in has a rev which doesn't
     *      match the current rev in the datastore.
     */
    /*public FinanceDocument updateDocument(FinanceDocument document) throws ConflictException {
        MutableDocumentRevision rev = document.getDocumentRevision().mutableCopy();
        rev.body = DocumentBodyFactory.create(document.asMap());
        try {
            BasicDocumentRevision updated = this.mDatastore.updateDocumentFromRevision(rev);
            return FinanceDocument.fromRevision(updated);
        } catch (DocumentException de) {
            return null;
        }
    }
*/
    /**
     * Retrieves document by id.
     * @param docId task to create
     * @return  revision of the document
     */
  /*  public FinanceDocument getDocument(String docId)  {

        BasicDocumentRevision retrieved = null;
        try {
            retrieved = mDatastore.getDocument(docId);
        } catch (DocumentNotFoundException e) {
            e.printStackTrace();
            Log.e("Doc", "document was not found");
        }
        return FinanceDocument.fromRevision(retrieved);
    }
}*/