package com.rbsoftware.pfm.personalfinancemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by burzakovskiy on 11/24/2015.
 */
public class FinanceDocumentModel {
    private static final String LOG_TAG = "FinanceDocumentModel";
    private static final String DATASTORE_MANGER_DIR = "data";
    private static final String DOCUMENT_DATASTORE = "documents";

    private static final String SETTINGS_CLOUDANT_USER = "burzakovskiy";
    private static final String SETTINGS_CLOUDANT_DB = "data";
    private static final String SETTINGS_CLOUDANT_API_KEY = "sournictsitedincivegains";
    private static final String SETTINGS_CLOUDANT_API_SECRET = "293c6b466286c6aed7216e47f491f59a1ce6a6e0";
    private static final String FINANCE_DOCUMENT_INDEX_LIST= "FinanceDocumentIndexList";

    //Query time frames
    private static final String THIS_WEEK = "thisWeek";
    private static final String LAST_WEEK = "lastWeek";
    private static final String THIS_MONTH = "thisMonth";
    private static final String LAST_MONTH = "lastMonth";
    private static final String THIS_YEAR = "thisYear";
    private Calendar cal;


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
            Log.e(LOG_TAG, "Unable to open Datastore", dnce);
        }

        Log.d(LOG_TAG, "Set up database at " + path.getAbsolutePath());



        // Allow us to switch code called by the ReplicationListener into
        // the main thread so the UI can update safely.
        this.mHandler = new Handler(Looper.getMainLooper());

        Log.d(LOG_TAG, "FinanceDocumentModel set up " + path.getAbsolutePath());
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

    //Set index manager

    public void setIndexManager(){
        im = new IndexManager(mDatastore);
        List<Object> indexList = new ArrayList<>();
        indexList.add("type");
        indexList.add("userId");
        indexList.add("date");
        indexList.add("salary");
        indexList.add("rentalIncome");
        indexList.add("interest");
        indexList.add("gifts");
        indexList.add("otherIncome");
        indexList.add("taxes");
        indexList.add("mortgage");
        indexList.add("creditCard");
        indexList.add("utilities");
        indexList.add("food");
        indexList.add("carPayment");
        indexList.add("personal");
        indexList.add("activities");
        indexList.add("otherExpenses");


        im.ensureIndexed(indexList, FINANCE_DOCUMENT_INDEX_LIST);
        Log.d("index manager", "List of indexes " + im.listIndexes().toString());
    }


    // Document query methods

    /**
     * Queries docuuments by time period
     * @param timeFrame
     * "thisWeek"
     * "lastWeek"
     * "thisMonth"
     * "lastMonth"
     * "Jan" - "Dec"
     * "thisYear"
     * @return list of the documents
     */
    public List<FinanceDocument> queryDocumentsByDate(String timeFrame, String userId){
        List<FinanceDocument> list= new ArrayList<>();
        cal = Calendar.getInstance();
        long currDate =  cal.getTimeInMillis()/1000;
        Map<String, Object> query = new HashMap<String, Object>();

        Map<String, Object> gteDate = new HashMap<String, Object>();                    // Start of the period
        Map<String, Object> startClause = new HashMap<String, Object>();                //*
        gteDate.put("$gte", startDateBuilder(currDate, timeFrame));        //*
        startClause.put("date", gteDate);                                  //*********

        Map<String, Object> lteDate = new HashMap<String, Object>();      // End of t/he period
        Map<String, Object> endClause = new HashMap<String, Object>();                 // *
        lteDate.put("$lte", endDateBuilder(currDate, timeFrame));         //*
        endClause.put("date", lteDate);                                   //*********


        Map<String, Object> eqUserId = new HashMap<String, Object>();       //Query by userId
        Map<String, Object> userIdClause = new HashMap<String, Object>();               //*
        eqUserId.put("$eq", userId);                                       //*
        userIdClause.put("userId", eqUserId);                              //**********************

        query.put("$and", Arrays.<Object>asList(startClause, endClause, userIdClause)); //query

        //Sorting documents
        List<Map<String, String>> sortDocument = new ArrayList<Map<String, String>>();
        Map<String, String> sortByDate = new HashMap<String, String>();
        sortByDate.put("date", "asc");  //sorting by date
        sortDocument.add(sortByDate);

        QueryResult result = im.find(query,0,0,null,sortDocument);
        for (DocumentRevision rev : result) {
            list.add(getDocument(rev.getId()));

            // The returned revision object contains all fields for
            // the object. You cannot project certain fields in the
            // current implementation.
        }
        return list;
    }

    //methods to get first/last day
    private Long getFirstDateOfCurrentMonth() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis()/1000;
    }

    private Long getFirstDateOfCurrentWeek() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.set(Calendar.DAY_OF_WEEK, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_WEEK));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis()/1000;
    }

    private Long getFirstDateOfCurrentYear() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTimeZone(cal.getTimeZone());
        cal.set(Calendar.DAY_OF_YEAR, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_YEAR));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis()/1000;
    }

    private Long getFirstDateOfPreviousMonth() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.set(Calendar.DATE, 1);
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis()/1000;
    }

    private Long getLastDateOfPreviousMonth() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.set(Calendar.DATE, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis()/1000;
    }


    private Long getFirstDateOfPreviousWeek() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.add(Calendar.WEEK_OF_MONTH, -1);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_WEEK));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis()/1000;
    }

    private Long getLastDateOfPreviousWeek() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.set(Calendar.DAY_OF_WEEK, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_WEEK));
        cal.add(Calendar.DAY_OF_YEAR, -1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis()/1000;
    }

    //helper method for setting start date
    private  long startDateBuilder(long currDate, String timeFrame){
        long startDate=0;
        switch (timeFrame){
            case THIS_WEEK: startDate = getFirstDateOfCurrentWeek();
                break;
            case THIS_MONTH: startDate = getFirstDateOfCurrentMonth();
                break;
            case THIS_YEAR: startDate = getFirstDateOfCurrentYear();
                break;
            case LAST_WEEK: startDate = getFirstDateOfPreviousWeek();
                break;
            case LAST_MONTH: startDate = getFirstDateOfPreviousMonth();
                break;

        }
        return startDate;
    }

    //helper method for setting end date
    private  long endDateBuilder(long currDate, String timeFrame){
        long endDate=0;

        switch (timeFrame){
            case THIS_WEEK: endDate = currDate;
                break;
            case THIS_MONTH: endDate = currDate;
                break;
            case THIS_YEAR: endDate = currDate;
                break;
            case LAST_WEEK: endDate = getLastDateOfPreviousWeek();
                break;
            case LAST_MONTH: endDate = getLastDateOfPreviousMonth();
                break;

        }

        return endDate;
    }


    //
    // DOCUMENT CRUD
    //

    /**
     * Creates a task, assigning an ID.
     * @param document task to create
     * @return new revision of the document
     */
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
     * Retrieves document by id.
     * @param docId task to create
     * @return  revision of the document
     */
    public FinanceDocument getDocument(String docId)  {

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
     * Updates a Task document within the datastore.
     * @param document document to update
     * @return the updated revision of the Task
     * @throws ConflictException if the document passed in has a rev which doesn't
     *      match the current rev in the datastore.
     */
    public FinanceDocument updateDocument(FinanceDocument document) throws ConflictException {
        MutableDocumentRevision rev = document.getDocumentRevision().mutableCopy();
        rev.body = DocumentBodyFactory.create(document.asMap());
        try {
            BasicDocumentRevision updated = this.mDatastore.updateDocumentFromRevision(rev);
            return FinanceDocument.fromRevision(updated);
        } catch (DocumentException de) {
            return null;
        }
    }

    /**
     * Deletes a Task document within the datastore.
     * @param task task to delete
     * @throws ConflictException if the task passed in has a rev which doesn't
     *      match the current rev in the datastore.
     */
    public void deleteDocument(FinanceDocument task) throws ConflictException {
        this.mDatastore.deleteDocumentFromRevision(task.getDocumentRevision());
    }

    /**
     * <p>Returns all {@code Task} documents in the datastore.</p>
     */
    public List<FinanceDocument> allTasks() {
        int nDocs = this.mDatastore.getDocumentCount();
        List<BasicDocumentRevision> all = this.mDatastore.getAllDocuments(0, nDocs, true);
        List<FinanceDocument> tasks = new ArrayList<FinanceDocument>();

        // Filter all documents down to those of type Task.
        for(BasicDocumentRevision rev : all) {
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
     *
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

        Log.d(LOG_TAG, "Set up replicators for URI:" + uri.toString());
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
        Log.e(LOG_TAG, "Replication error:", re.errorInfo.getException());
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
