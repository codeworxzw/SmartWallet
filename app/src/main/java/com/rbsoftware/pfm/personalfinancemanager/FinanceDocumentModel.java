package com.rbsoftware.pfm.personalfinancemanager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.cloudant.sync.datastore.BasicDocumentRevision;
import com.cloudant.sync.datastore.ConflictException;
import com.cloudant.sync.datastore.Datastore;
import com.cloudant.sync.datastore.DatastoreManager;
import com.cloudant.sync.datastore.DatastoreNotCreatedException;
import com.cloudant.sync.datastore.DocumentBodyFactory;
import com.cloudant.sync.datastore.DocumentException;
import com.cloudant.sync.datastore.DocumentNotFoundException;
import com.cloudant.sync.datastore.DocumentRevision;
import com.cloudant.sync.datastore.MutableDocumentRevision;
import com.cloudant.sync.notifications.ReplicationCompleted;
import com.cloudant.sync.notifications.ReplicationErrored;
import com.cloudant.sync.query.IndexManager;
import com.cloudant.sync.query.QueryResult;
import com.cloudant.sync.replication.Replicator;
import com.cloudant.sync.replication.ReplicatorBuilder;
import com.google.common.eventbus.Subscribe;
import com.rbsoftware.pfm.personalfinancemanager.budget.BudgetDocument;
import com.rbsoftware.pfm.personalfinancemanager.utils.DateUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by burzakovskiy on 11/24/2015.
 * Holds methods for CRUD and querying finance documents
 **/
public class FinanceDocumentModel {
    public final static String CURRENCY_ID = "CurrencyID";
    private static final String TAG = "FinanceDocumentModel";
    private static final String DATASTORE_MANGER_DIR = "data";
    private static final String DOCUMENT_DATASTORE = "documents";

    private static final String SETTINGS_CLOUDANT_USER = "burzakovskiy";
    private static final String SETTINGS_CLOUDANT_DB = "data";
    private static final String SETTINGS_CLOUDANT_API_KEY = "sournictsitedincivegains";
    private static final String SETTINGS_CLOUDANT_API_SECRET = "293c6b466286c6aed7216e47f491f59a1ce6a6e0";
    private static final String FINANCE_DOCUMENT_INDEX_LIST = "FinanceDocumentIndexList";


    private Calendar cal;

    public static final String ORDER_ASC = "asc";
    public static final String ORDER_DESC = "desc";

    private Datastore mDatastore;
    private IndexManager im;
    private Replicator mPushReplicator;
    private Replicator mPullReplicator;
    private final Context mContext;
    private final Handler mHandler;
    private MainActivity mListener;

    public FinanceDocumentModel(Context context) {
        this.mContext = context;

        // Set up our tasks datastore within its own folder in the applications
        // data directory.
        File path = this.mContext.getApplicationContext().getDir(
                DATASTORE_MANGER_DIR,
                Context.MODE_PRIVATE
        );
        DatastoreManager manager = new DatastoreManager(path.getAbsolutePath());
        try {
            this.mDatastore = manager.openDatastore(DOCUMENT_DATASTORE);
        } catch (DatastoreNotCreatedException dnce) {
            Log.e(TAG, "Unable to open Datastore", dnce);
        }

        Log.d(TAG, "Set up database at " + path.getAbsolutePath());


        // Allow us to switch code called by the ReplicationListener into
        // the main thread so the UI can update safely.
        this.mHandler = new Handler(Looper.getMainLooper());

        Log.d(TAG, "FinanceDocumentModel set up " + path.getAbsolutePath());
    }

    public void setReplicationListener(MainActivity listener) {
        this.mListener = listener;
    }

    private URI createServerURI()
            throws URISyntaxException {
        // We store this in plain text for the purposes of simple demonstration,
        // you might want to use something more secure.

        String username = SETTINGS_CLOUDANT_USER;
        String dbName = SETTINGS_CLOUDANT_DB;
        String apiKey = SETTINGS_CLOUDANT_API_KEY;
        String apiSecret = SETTINGS_CLOUDANT_API_SECRET;
        String host = username + ".cloudant.com";

        // We recommend always using HTTPS to talk to Cloudant.
        return new URI("https", apiKey + ":" + apiSecret, host, 443, "/" + dbName, null, null);
    }

    /**
     * Set index manager
     **/

    public void setIndexManager() {
        im = new IndexManager(mDatastore);
        List<Object> indexList = new ArrayList<>();
        indexList.add("type");
        indexList.add("userId");
        indexList.add("date");
        indexList.add("account");

        im.ensureIndexed(indexList, FINANCE_DOCUMENT_INDEX_LIST);
    }


    // Document query methods

    /**
     * Queries finance documents by time period
     *
     * @param timeFrame "thisWeek"
     *                  "lastWeek"
     *                  "threeWeeks"
     *                  "thisMonth"
     *                  "lastMonth"
     *                  "threeMonths"
     *                  "Jan" - "Dec"
     *                  "thisYear"
     * @param userId    id of current user
     * @param docType   document type
     * @return list of the documents
     **/


    public List<FinanceDocument> queryFinanceDocumentsByDate(String timeFrame, String userId, String docType) {
        List<FinanceDocument> list = new ArrayList<>();
        cal = Calendar.getInstance();
        long currDate = cal.getTimeInMillis() / 1000;
        Map<String, Object> query = new HashMap<>();

        Map<String, Object> gteDate = new HashMap<>();                    // Start of the period
        Map<String, Object> startClause = new HashMap<>();                //*
        gteDate.put("$gte", startDateBuilder(currDate, timeFrame));        //*
        startClause.put("date", gteDate);
        //*********
        Map<String, Object> lteDate = new HashMap<>();      // End of t/he period
        Map<String, Object> endClause = new HashMap<>();                 // *
        lteDate.put("$lte", endDateBuilder(currDate, timeFrame));         //*
        endClause.put("date", lteDate);                                   //*********
        Map<String, Object> eqUserId = new HashMap<>();       //Query by userId
        Map<String, Object> userIdClause = new HashMap<>();               //*
        eqUserId.put("$eq", userId);                                       //*
        userIdClause.put("userId", eqUserId);                              //**********************

        Map<String, Object> eqType = new HashMap<>();       //Query by type
        Map<String, Object> typeClause = new HashMap<>();               //*
        eqType.put("$eq", docType);                                       //*
        typeClause.put("type", eqType);                              //**********************


        query.put("$and", Arrays.<Object>asList(startClause, endClause, userIdClause, typeClause)); //query


        QueryResult result = im.find(query);
        if (result != null) {
            for (DocumentRevision rev : result) {
                list.add(getFinanceDocument(rev.getId()));

                // The returned revision object contains all fields for
                // the object. You cannot project certain fields in the
                // current implementation.
            }

        }
        return list;
    }

    /**
     * Queries finance documents by time period and sort
     *
     * @param timeFrame "thisWeek"
     *                  "lastWeek"
     *                  "threeWeeks"
     *                  "thisMonth"
     *                  "lastMonth"
     *                  "threeMonths"
     *                  "Jan" - "Dec"
     *                  "thisYear"
     * @param userId    id of current user
     * @param docType   document type
     * @param order     asc or desc
     * @return list of the documents
     **/

    public List<FinanceDocument> queryFinanceDocumentsByDate(String timeFrame, String userId, String docType, String order) {
        List<FinanceDocument> list = new ArrayList<>();
        cal = Calendar.getInstance();
        long currDate = cal.getTimeInMillis() / 1000;
        Map<String, Object> query = new HashMap<>();

        Map<String, Object> gteDate = new HashMap<>();                    // Start of the period
        Map<String, Object> startClause = new HashMap<>();                //*
        gteDate.put("$gte", startDateBuilder(currDate, timeFrame));        //*
        startClause.put("date", gteDate);
        //*********
        Map<String, Object> lteDate = new HashMap<>();      // End of t/he period
        Map<String, Object> endClause = new HashMap<>();                 // *
        lteDate.put("$lte", endDateBuilder(currDate, timeFrame));         //*
        endClause.put("date", lteDate);                                   //*********
        Map<String, Object> eqUserId = new HashMap<>();       //Query by userId
        Map<String, Object> userIdClause = new HashMap<>();               //*
        eqUserId.put("$eq", userId);                                       //*
        userIdClause.put("userId", eqUserId);                              //**********************


        Map<String, Object> eqType = new HashMap<>();       //Query by type
        Map<String, Object> typeClause = new HashMap<>();               //*
        eqType.put("$eq", docType);                                       //*
        typeClause.put("type", eqType);                              //**********************


        query.put("$and", Arrays.<Object>asList(startClause, endClause, userIdClause, typeClause)); //query

        //Sorting documents
        List<Map<String, String>> sortDocument = new ArrayList<>();
        Map<String, String> sortByDate = new HashMap<>();
        if (order.equals(ORDER_ASC)) {

            sortByDate.put("date", "asc");  //sorting by date

        } else {
            sortByDate.put("date", "desc");  //sorting by date
        }
        sortDocument.add(sortByDate);
        QueryResult result = im.find(query, 0, 0, null, sortDocument);
        if (result != null) {
            for (DocumentRevision rev : result) {
                list.add(getFinanceDocument(rev.getId()));

                // The returned revision object contains all fields for
                // the object. You cannot project certain fields in the
                // current implementation.
            }

        }
        return list;
    }

    /**
     * Queries budget documents by time period and sort
     *
     * @param timeFrame "thisWeek"
     *                  "lastWeek"
     *                  "threeWeeks"
     *                  "thisMonth"
     *                  "lastMonth"
     *                  "threeMonths"
     *                  "Jan" - "Dec"
     *                  "thisYear"
     * @param userId    id of current user
     * @param docType   document type
     * @param order     asc or desc
     * @return list of the documents
     **/

    public List<BudgetDocument> queryBudgetDocumentsByDate(String timeFrame, String userId, String docType, String order) {
        List<BudgetDocument> list = new ArrayList<>();
        cal = Calendar.getInstance();
        long currDate = cal.getTimeInMillis() / 1000;
        Map<String, Object> query = new HashMap<>();

        Map<String, Object> gteDate = new HashMap<>();                    // Start of the period
        Map<String, Object> startClause = new HashMap<>();                //*
        gteDate.put("$gte", startDateBuilder(currDate, timeFrame));        //*
        startClause.put("date", gteDate);
        //*********
        Map<String, Object> lteDate = new HashMap<>();      // End of t/he period
        Map<String, Object> endClause = new HashMap<>();                 // *
        lteDate.put("$lte", endDateBuilder(currDate, timeFrame));         //*
        endClause.put("date", lteDate);                                   //*********
        Map<String, Object> eqUserId = new HashMap<>();       //Query by userId
        Map<String, Object> userIdClause = new HashMap<>();               //*
        eqUserId.put("$eq", userId);                                       //*
        userIdClause.put("userId", eqUserId);                              //**********************


        Map<String, Object> eqType = new HashMap<>();       //Query by type
        Map<String, Object> typeClause = new HashMap<>();               //*
        eqType.put("$eq", docType);                                       //*
        typeClause.put("type", eqType);                              //**********************


        query.put("$and", Arrays.<Object>asList(startClause, endClause, userIdClause, typeClause)); //query

        //Sorting documents
        List<Map<String, String>> sortDocument = new ArrayList<>();
        Map<String, String> sortByDate = new HashMap<>();
        if (order.equals(ORDER_ASC)) {

            sortByDate.put("date", "asc");  //sorting by date

        } else {
            sortByDate.put("date", "desc");  //sorting by date
        }
        sortDocument.add(sortByDate);
        QueryResult result = im.find(query, 0, 0, null, sortDocument);
        if (result != null) {
            for (DocumentRevision rev : result) {
                list.add(getBudgetDocument(rev.getId()));

                // The returned revision object contains all fields for
                // the object. You cannot project certain fields in the
                // current implementation.
            }

        }
        return list;
    }


    /**
     * Calculates start date for query
     *
     * @param currDate  current date in milliseconds
     * @param timeFrame time frame
     * @return start date for query
     */
    private long startDateBuilder(long currDate, String timeFrame) {
        switch (timeFrame) {
            case DateUtils.THIS_WEEK:
                return DateUtils.getFirstDateOfCurrentWeek();

            case DateUtils.THIS_MONTH:
                return DateUtils.getFirstDateOfCurrentMonth();

            case DateUtils.THIS_YEAR:
                return DateUtils.getFirstDateOfCurrentYear();

            case DateUtils.LAST_WEEK:
                return DateUtils.getFirstDateOfPreviousWeek();

            case DateUtils.THREE_WEEKS:
                return DateUtils.getFirstDateOfTwoWeeksAgo();

            case DateUtils.LAST_MONTH:
                return DateUtils.getFirstDateOfPreviousMonth();

            case DateUtils.THREE_MONTHS:
                return DateUtils.getFirstDateOfTwoMonthsAgo();

            default:
                return 0;

        }
    }

    /**
     * Calculates end date for query
     *
     * @param currDate  current date in milliseconds
     * @param timeFrame time frame
     * @return end date for query
     */
    private long endDateBuilder(long currDate, String timeFrame) {


        switch (timeFrame) {
            case DateUtils.THIS_WEEK:
                return currDate;
            case DateUtils.THIS_MONTH:
                return currDate;
            case DateUtils.THIS_YEAR:
                return currDate;
            case DateUtils.LAST_WEEK:
                return DateUtils.getLastDateOfPreviousWeek();
            case DateUtils.THREE_WEEKS:
                return currDate;
            case DateUtils.LAST_MONTH:
                return DateUtils.getLastDateOfPreviousMonth();
            case DateUtils.THREE_MONTHS:
                return currDate;
            default:
                return currDate;

        }


    }


    //
    // DOCUMENT CRUD
    //

    /**
     * Creates a finance document, assigning an ID.
     *
     * @param document task to create
     * @return new revision of the document
     **/
    public FinanceDocument createDocument(FinanceDocument document) {
        MutableDocumentRevision rev = new MutableDocumentRevision();

        rev.body = DocumentBodyFactory.create(document.asMap());
        try {
            BasicDocumentRevision created = this.mDatastore.createDocumentFromRevision(rev);

            return FinanceDocument.fromRevision(created);

        } catch (DocumentException de) {
            Log.e("Doc", "document was not created");
            return null;
        }
    }

    /**
     * Creates a currency document, assigning an ID.
     *
     * @param document task to create
     * @return new revision of the document
     */
    public Currency createDocument(Currency document) {
        MutableDocumentRevision rev = new MutableDocumentRevision();
        rev.docId = CURRENCY_ID;
        rev.body = DocumentBodyFactory.create(document.asMap());
        try {
            BasicDocumentRevision created = this.mDatastore.createDocumentFromRevision(rev);

            return Currency.fromRevision(created);

        } catch (DocumentException de) {
            Log.e("Doc", "document was not created");
            return null;
        }
    }

    /**
     * Creates a budget document, assigning an ID.
     *
     * @param document budget to create
     * @return new revision of the document
     */
    public BudgetDocument createDocument(BudgetDocument document) {
        MutableDocumentRevision rev = new MutableDocumentRevision();

        rev.body = DocumentBodyFactory.create(document.asMap());
        try {
            BasicDocumentRevision created = this.mDatastore.createDocumentFromRevision(rev);

            return BudgetDocument.fromRevision(created);

        } catch (DocumentException de) {
            Log.e("Doc", "document was not created");
            return null;
        }
    }

    /**
     * Retrieves document by id.
     *
     * @param docId task to create
     * @return revision of the document
     */
    public FinanceDocument getFinanceDocument(String docId) {

        BasicDocumentRevision retrieved = null;
        try {
            retrieved = mDatastore.getDocument(docId);
        } catch (DocumentNotFoundException e) {
            e.printStackTrace();
            Log.e("Doc", "document was not found");
        }
        return FinanceDocument.fromRevision(retrieved);
    }

    /**
     * Retrieves document by id.
     *
     * @param docId task to create
     * @return revision of the document
     */
    public Currency getCurrencyDocument(String docId) {

        BasicDocumentRevision retrieved;
        try {
            retrieved = mDatastore.getDocument(docId);
        } catch (DocumentNotFoundException e) {
            //e.printStackTrace();
            Log.e("Doc", "document was not found");
            return null;
        }
        return Currency.fromRevision(retrieved);
    }

    /**
     * Retrieves document by id.
     *
     * @param docId task to create
     * @return revision of the document
     */
    public BudgetDocument getBudgetDocument(String docId) {

        BasicDocumentRevision retrieved;
        try {
            retrieved = mDatastore.getDocument(docId);
        } catch (DocumentNotFoundException e) {
            //e.printStackTrace();
            Log.e("Doc", "document was not found");
            return null;
        }
        return BudgetDocument.fromRevision(retrieved);
    }


    /**
     * Updates a Document document within the datastore.
     *
     * @param oldDocument document to update
     * @param newDocument new document
     * @return the updated revision of the Task
     * @throws ConflictException if the document passed in has a rev which doesn't
     *                           match the current rev in the datastore.
     */
    public FinanceDocument updateFinanceDocument(FinanceDocument oldDocument, FinanceDocument newDocument) throws ConflictException {
        MutableDocumentRevision rev = oldDocument.getDocumentRevision().mutableCopy();
        rev.body = DocumentBodyFactory.create(newDocument.asMap());
        try {
            BasicDocumentRevision updated = this.mDatastore.updateDocumentFromRevision(rev);
            return FinanceDocument.fromRevision(updated);
        } catch (DocumentException de) {
            return null;
        }
    }

    /**
     * Updates a Document document within the datastore.
     *
     * @param oldDocument document to update
     * @param newDocument new document
     * @return the updated revision of the Task
     * @throws ConflictException if the document passed in has a rev which doesn't
     *                           match the current rev in the datastore.
     */
    public Currency updateCurrencyDocument(Currency oldDocument, Currency newDocument) throws ConflictException {
        MutableDocumentRevision rev = oldDocument.getDocumentRevision().mutableCopy();
        rev.body = DocumentBodyFactory.create(newDocument.asMap());
        try {
            BasicDocumentRevision updated = this.mDatastore.updateDocumentFromRevision(rev);
            return Currency.fromRevision(updated);
        } catch (DocumentException de) {
            return null;
        }
    }

    /**
     * Updates a Document document within the datastore.
     *
     * @param oldDocument document to update
     * @param newDocument new document
     * @return the updated revision of the Task
     * @throws ConflictException if the document passed in has a rev which doesn't
     *                           match the current rev in the datastore.
     */
    public BudgetDocument updateBudgetDocument(BudgetDocument oldDocument, BudgetDocument newDocument) throws ConflictException {
        MutableDocumentRevision rev = oldDocument.getDocumentRevision().mutableCopy();
        rev.body = DocumentBodyFactory.create(newDocument.asMap());
        try {
            BasicDocumentRevision updated = this.mDatastore.updateDocumentFromRevision(rev);
            return BudgetDocument.fromRevision(updated);
        } catch (DocumentException de) {
            return null;
        }
    }


    /**
     * Deletes a Finance document within the datastore.
     *
     * @param doc task to delete
     * @throws ConflictException if the task passed in has a rev which doesn't
     *                           match the current rev in the datastore.
     */
    public void deleteDocument(FinanceDocument doc) throws ConflictException {
        this.mDatastore.deleteDocumentFromRevision(doc.getDocumentRevision());
    }

    /**
     * Deletes a Budget document within the datastore.
     *
     * @param doc task to delete
     * @throws ConflictException if the task passed in has a rev which doesn't
     *                           match the current rev in the datastore.
     */
    public void deleteDocument(BudgetDocument doc) throws ConflictException {
        this.mDatastore.deleteDocumentFromRevision(doc.getDocumentRevision());
    }

    /**
     * <p>Returns all {@code Task} documents in the datastore.</p>
     */
    public List<FinanceDocument> allTasks() {
        int nDocs = this.mDatastore.getDocumentCount();
        List<BasicDocumentRevision> all = this.mDatastore.getAllDocuments(0, nDocs, true);
        List<FinanceDocument> tasks = new ArrayList<>();

        // Filter all documents down to those of type Task.
        for (BasicDocumentRevision rev : all) {
            FinanceDocument t = FinanceDocument.fromRevision(rev);
            if (t != null) {
                tasks.add(t);
            }
        }

        return tasks;
    }

    //
    // MANAGE REPLICATIONS
    //

    /**
     * <p>Stops running replications.</p>
     * <p/>
     * <p>The stop() methods stops the replications asynchronously, see the
     * replicator docs for more information.</p>
     */
    public void stopAllReplications() {
        if (this.mPullReplicator != null) {
            this.mPullReplicator.stop();
        }
        if (this.mPushReplicator != null) {
            this.mPushReplicator.stop();
        }
    }

    /**
     * <p>Starts the configured push replication.</p>
     */
    public void startPushReplication() {

        if (this.mPushReplicator != null) {
            this.mPushReplicator.start();
        } else {
            throw new RuntimeException("Push replication not set up correctly");
        }
    }

    /**
     * <p>Starts the configured pull replication.</p>
     */
    public void startPullReplication() {
        if (this.mPullReplicator != null) {
            this.mPullReplicator.start();
        } else {
            throw new RuntimeException("Push replication not set up correctly");
        }
    }

    /**
     * <p>Stops running replications and reloads the replication settings from
     * the app's preferences.</p>
     */
    public void reloadReplicationSettings()
            throws URISyntaxException {

        // Stop running replications before reloading the replication
        // settings.
        // The stop() method instructs the replicator to stop ongoing
        // processes, and to stop making changes to the datastore. Therefore,
        // we don't clear the listeners because their complete() methods
        // still need to be called once the replications have stopped
        // for the UI to be updated correctly with any changes made before
        // the replication was stopped.
        this.stopAllReplications();

        // Set up the new replicator objects
        URI uri = this.createServerURI();

        mPullReplicator = ReplicatorBuilder.pull().to(mDatastore).from(uri).build();
        mPushReplicator = ReplicatorBuilder.push().from(mDatastore).to(uri).build();

        mPushReplicator.getEventBus().register(this);
        mPullReplicator.getEventBus().register(this);

    }

    /**
     * Calls the TodoActivity's replicationComplete method on the main thread,
     * as the complete() callback will probably come from a replicator worker
     * thread.
     */
    @Subscribe
    public void complete(ReplicationCompleted rc) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.replicationComplete();
                }
            }
        });
    }

    /**
     * Calls the MainActivity's replicationComplete method on the main thread,
     * as the error() callback will probably come from a replicator worker
     * thread.
     */
    @Subscribe
    public void error(ReplicationErrored re) {
        Log.e(TAG, "Replication error:", re.errorInfo.getException());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.replicationError();
                }
            }
        });
    }
}
