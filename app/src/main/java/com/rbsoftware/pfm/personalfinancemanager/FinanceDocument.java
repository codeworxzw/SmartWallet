package com.rbsoftware.pfm.personalfinancemanager;

import com.cloudant.sync.datastore.BasicDocumentRevision;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by burzakovskiy on 11/24/2015.
 */
public class FinanceDocument {
    static final String DOC_TYPE = "Finance document";
    private FinanceDocument() {}

    public FinanceDocument(String data) {

        this.setType(DOC_TYPE);
        this.setData(data);
    }
    private String type = DOC_TYPE;
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    private String data;
    public String getData() {
        return data;
    }
    public void setData(String data) {this.data = data;}

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
            return t;
        }
        return null;
    }

    public Map<String, Object> asMap() {
        // this could also be done by a fancy object mapper
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("type", type);
        map.put("id", data);

        return map;
    }
}
