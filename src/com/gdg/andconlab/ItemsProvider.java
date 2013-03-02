//package com.gdg.andconlab;
//
//import android.content.ContentProvider;
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.UriMatcher;
//import android.database.Cursor;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.database.sqlite.SQLiteQueryBuilder;
//import android.net.Uri;
//import android.provider.BaseColumns;
//import android.util.Log;
//
//import java.util.HashMap;
//
///**
// * @author Gen. Schultz
// * @version 0.1
// */
//public class ItemsProvider extends ContentProvider {
//
//    ///////////////////////////////////////////////
//    // Constants
//    ///////////////////////////////////////////////
//
//    /**
//     * The authority for this provider
//     */
//    public static final String AUTHORITY = "com.gdg.andconlab.ItemColumns";
//
//    private static final String TAG = "ItemColumns_PROVIDER";
//
//    private static final String DATABASE_NAME = "andconlab.ItemColumns.db";
//    private static final int DATABASE_VERSION = 2;
//
//    /*
//    * Constants used by the Uri matcher to choose an action based on the pattern
//    * of the incoming URI
//    */
//    private static final int ITEMS = 1;
//    private static final int VIDEO_ID = 2;
//
//    ///////////////////////////////////////////////
//    // Members
//    ///////////////////////////////////////////////
//    private static final UriMatcher sUriMatcher;
//    private static HashMap<String, String> sItemColumnsProjectionMap;
//    private DatabaseHelper mDbHelper;
//
//    /**
//     * Static constructor. <br/>
//     * Initializes all static members
//     */
//    static {
//        // Define URI matcher
//        StringBuilder sb = new StringBuilder(2);
//        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
//        sUriMatcher.addURI(AUTHORITY, ItemColumns.TABLE_NAME, ITEMS);
//        sUriMatcher.addURI(AUTHORITY, StringUtils.append(sb, ItemColumns.TABLE_NAME, "/*"), VIDEO_ID);
//
//        // Define PROFILES table projection
//        sItemColumnsProjectionMap = new HashMap<String, String>();
//        sItemColumnsProjectionMap.put(ItemColumns._ID, ItemColumns._ID);
//        sItemColumnsProjectionMap.put(ItemColumns.COLUMN_NAME_LECTURE_VIDEO_ID, ItemColumns.COLUMN_NAME_LECTURE_VIDEO_ID);
//
//        sItemColumnsProjectionMap.put(ItemColumns.COLUMN_NAME_EVENT_NAME, ItemColumns.COLUMN_NAME_EVENT_NAME);
//        sItemColumnsProjectionMap.put(ItemColumns.COLUMN_NAME_EVENT_DESCRIPTION, ItemColumns.COLUMN_NAME_EVENT_DESCRIPTION);
//        sItemColumnsProjectionMap.put(ItemColumns.COLUMN_NAME_EVENT_LOGO_URL, ItemColumns.COLUMN_NAME_EVENT_LOGO_URL);
//        sItemColumnsProjectionMap.put(ItemColumns.COLUMN_NAME_EVENT_WEBSITE_URL, ItemColumns.COLUMN_NAME_EVENT_WEBSITE_URL);
//        sItemColumnsProjectionMap.put(ItemColumns.COLUMN_NAME_EVENT_START_DATE, ItemColumns.COLUMN_NAME_EVENT_START_DATE);
//        sItemColumnsProjectionMap.put(ItemColumns.COLUMN_NAME_EVENT_END_DATE, ItemColumns.COLUMN_NAME_EVENT_END_DATE);
//
//        sItemColumnsProjectionMap.put(ItemColumns.COLUMN_NAME_LECTURE_TITLE, ItemColumns.COLUMN_NAME_LECTURE_TITLE);
//        sItemColumnsProjectionMap.put(ItemColumns.COLUMN_NAME_LECTURER_NAME, ItemColumns.COLUMN_NAME_LECTURER_NAME);
//        sItemColumnsProjectionMap.put(ItemColumns.COLUMN_NAME_LECTURER_IMAGE_URL, ItemColumns.COLUMN_NAME_LECTURER_IMAGE_URL);
//        sItemColumnsProjectionMap.put(ItemColumns.COLUMN_NAME_LECTURER_BIO, ItemColumns.COLUMN_NAME_LECTURER_BIO);
//        sItemColumnsProjectionMap.put(ItemColumns.COLUMN_NAME_LECTURER_IMAGE, ItemColumns.COLUMN_NAME_LECTURER_IMAGE);
//        sItemColumnsProjectionMap.put(ItemColumns.COLUMN_NAME_LECTURE_VIDEO_URL, ItemColumns.COLUMN_NAME_LECTURE_VIDEO_URL);
//        sItemColumnsProjectionMap.put(ItemColumns.COLUMN_NAME_LECTURE_SLIDES_URL, ItemColumns.COLUMN_NAME_LECTURE_SLIDES_URL);
//        sItemColumnsProjectionMap.put(ItemColumns.COLUMN_NAME_LECTURE_DESCRIPTION, ItemColumns.COLUMN_NAME_LECTURE_DESCRIPTION);
//        sItemColumnsProjectionMap.put(ItemColumns.COLUMN_NAME_LECTURE_DURATION, ItemColumns.COLUMN_NAME_LECTURE_DURATION);
//        sItemColumnsProjectionMap.put(ItemColumns.COLUMN_NAME_TIMESTAMP, ItemColumns.COLUMN_NAME_TIMESTAMP);
//    }
//
//    ///////////////////////////////////////////////
//    // Initialization Process
//    ///////////////////////////////////////////////
//
//    @Override
//    public boolean onCreate() {
//        // Start initialization process
//        init();
//        return true;
//    }
//
//    /**
//     * Initialization process
//     */
//    private void init() {
//        initMembers();
//    }
//
//    /**
//     * Initialize all members
//     */
//    private void initMembers() {
//        mDbHelper = new DatabaseHelper(getContext());
//    }
//
//    ///////////////////////////////////////////////
//    // Public
//    ///////////////////////////////////////////////
//
//    /**
//     * Delete a specific item
//     *
//     * @param uri
//     * @return
//     */
//    public int delete(Uri uri) {
//        return delete(uri, null, null);
//    }
//
//    /**
//     * Update values for specific item
//     *
//     * @param uri
//     * @param values
//     * @return
//     */
//    public int update(Uri uri, ContentValues values, String where) {
//        return update(uri, values, where, null);
//    }
//
//    ///////////////////////////////////////////////
//    // Private
//    ///////////////////////////////////////////////
//
//    /**
//     * Get the database query filter corresponding to given uri and where clause
//     *
//     * @param uri
//     * @param where
//     * @return
//     */
//    private QueryFilter getQueryFilter(Uri uri, String where) {
//        QueryFilter model = new QueryFilter();
//
//        // Prepare model
//        switch (sUriMatcher.match(uri)) {
//
//            case VIDEO_ID:
//                if (where != null) {
//                    where = StringUtils.append(where, " ");
//                }
//                model.where = StringUtils.append(
//                        where,
//                        ItemColumns.COLUMN_NAME_LECTURE_VIDEO_ID, " = '",
//                        uri.getPathSegments().get(ItemColumns.VIDEO_ID_PATH_POSITION), "'");
//            case ITEMS:
//                model.tableName = ItemColumns.TABLE_NAME;
//                break;
//
//            // If the incoming pattern is invalid, throws an exception.
//            default:
//                throw new IllegalArgumentException(StringUtils.append("Unknown URI ", uri));
//        }
//
//        return model;
//    }
//
//    /**
//     * Set table, choose the projection and adjust the "where" clause based on URI pattern-matching.
//     *
//     * @param qb
//     * @param uri
//     */
//    private void defineTableAndProjection(SQLiteQueryBuilder qb, Uri uri) {
//        switch (sUriMatcher.match(uri)) {
//            case VIDEO_ID:
//                qb.appendWhere(StringUtils.append(
//                        // the name of the ID column
//                        ItemColumns.COLUMN_NAME_LECTURE_VIDEO_ID,
//                        "='",
//                        // the position of the profile ID itself in the incoming URI
//                        uri.getPathSegments().get(ItemColumns.VIDEO_ID_PATH_POSITION),
//                        "'")
//
//                );
//                // If the incoming URI is for profiles, chooses the Profile projection
//            case ITEMS:
//                qb.setTables(ItemColumns.TABLE_NAME);
//                qb.setProjectionMap(sItemColumnsProjectionMap);
//                break;
//
//            default:
//                // If the URI doesn't match any of the known patterns, throw an exception.
//                throw new IllegalArgumentException("Unknown URI " + uri);
//        }
//    }
//
//    ///////////////////////////////////////////////
//    // Overrides & Implementations
//    ///////////////////////////////////////////////
//
//    @Override
//    public String getType(Uri uri) {
//        switch (sUriMatcher.match(uri)) {
//            case ITEMS:
//                return ItemColumns.CONTENT_TYPE;
//
//            case VIDEO_ID:
//                return ItemColumns.CONTENT_ITEM_TYPE;
//
//            default:
//                throw new IllegalArgumentException(StringUtils.append("Unknown URI ", uri.toString()));
//        }
//    }
//
//    @Override
//    public Cursor query(Uri uri, String[] projection, String where, String[] whereArgs, String groupByWithOrderByAndLimit) {
//        // Constructs a new query builder
//        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
//
//        // Set table, choose the projection and adjust the "where" clause based on URI pattern-matching.
//        defineTableAndProjection(qb, uri);
//
//        // Opens the database object in "read" mode, since no writes need to be done.
//        SQLiteDatabase db = mDbHelper.getReadableDatabase();
//
//        /**
//         * Performs the query. If no problems occur trying to read the database, then a Cursor
//         * object is returned; otherwise, the cursor variable contains null. If no records were
//         * selected, then the Cursor object is empty, and Cursor.getCount() returns 0.
//         */
//        Cursor c = qb.query(
//                db,                             // The database to query
//                projection,                     // The columns to return from the query
//                where,                          // The columns for the where clause
//                whereArgs,                      // The values for the where clause
//                null,          // potentially group the rows
//                null,                           // don't filter by row groups
//                null,         // potentially sort the order
//                null           // potentially limit the number of rows
//        );
//
//        // Tells the Cursor what URI to watch, so it knows when its source data changes
//        c.setNotificationUri(getContext().getContentResolver(), uri);
//        return c;
//    }
//
//    @Override
//    public Uri insert(Uri uri, ContentValues values) {
//        String tableName;
//        String nullColumnHack;
//        Uri contentIdUriBase;
//
//        // Opens the database object in "write" mode.
//        SQLiteDatabase db = mDbHelper.getWritableDatabase();
//
//        // Decide on which table we operate on
//        switch (sUriMatcher.match(uri)) {
//            case ITEMS:
//                tableName = ItemColumns.TABLE_NAME;
//                nullColumnHack = ItemColumns.COLUMN_NAME_LECTURE_VIDEO_ID;
//                contentIdUriBase = ItemColumns.CONTENT_ID_URI_BASE;
//                break;
//
//            default:
//                throw new IllegalArgumentException(StringUtils.append("Unknown URI ", uri));
//        }
//
//        /*// Insert timestamp
//        // Gets the current system time in milliseconds
//        Long now = Long.valueOf(System.currentTimeMillis());
//        if (values == null) {
//            values = new ContentValues();
//        }
//        if (!values.containsKey(ItemColumns.COLUMN_NAME_TIMESTAMP)) {
//            values.put(ItemColumns.COLUMN_NAME_TIMESTAMP, now);
//        }*/
//
//        // Insert into database
//        long rowId = db.insert(
//                tableName,      // The table to insert into.
//                nullColumnHack, // A hack, SQLite sets this column value to null if values is empty.
//                values          // A map of column names, and the values to insert into the columns.
//        );
//
//        // If the insert succeeded, the row ID exists.
//        if (rowId > 0) {
//            // Creates a URI with the note ID pattern and the new row ID appended to it.
//            Uri noteUri = DBUtils.withAppendedId(contentIdUriBase, String.valueOf(values.get(ItemColumns.COLUMN_NAME_LECTURE_VIDEO_ID)));
//
//            // Notifies observers registered against this provider that the data changed.
//            getContext().getContentResolver().notifyChange(noteUri, null);
//            return noteUri;
//        }
//
//        // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
//        throw new SQLException(StringUtils.append("Failed to insert row into ", uri));
//    }
//
//    @Override
//    public int delete(Uri uri, String where, String[] whereArgs) {
//        // Opens the database object in "write" mode.
//        SQLiteDatabase db = mDbHelper.getWritableDatabase();
//
//        QueryFilter model = getQueryFilter(uri, where);
//
//        // Delete from database
//        int count = db.delete(
//                model.tableName,  // The database table name
//                model.where,      // The incoming where clause column names
//                whereArgs         // The incoming where clause values
//        );
//
//        /**
//         * Gets a handle to the content resolver object for the current context, and notifies it
//         * that the incoming URI changed. The object passes this along to the resolver framework,
//         * and observers that have registered themselves for the provider are notified.
//         */
//        getContext().getContentResolver().notifyChange(uri, null);
//
//        // Returns the number of rows deleted
//        return count;
//    }
//
//    @Override
//    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
//        // Opens the database object in "write" mode.
//        SQLiteDatabase db = mDbHelper.getWritableDatabase();
//
//        QueryFilter model = getQueryFilter(uri, where);
//
//        // update database
//        int count = db.update(
//                model.tableName,  // The database table name
//                values,           // A map of column names and new values to use.
//                model.where,      // The incoming where clause column names
//                whereArgs         // The incoming where clause values
//        );
//
//        /**
//         * Gets a handle to the content resolver object for the current context, and notifies it
//         * that the incoming URI changed. The object passes this along to the resolver framework,
//         * and observers that have registered themselves for the provider are notified.
//         */
//        getContext().getContentResolver().notifyChange(uri, null);
//
//        // Returns the number of rows updated
//        return count;
//    }
//
//    ///////////////////////////////////////////////
//    // Inner classes
//    ///////////////////////////////////////////////
//
//    /**
//     * Helper class for SQLite
//     */
//    private static class DatabaseHelper extends SQLiteOpenHelper {
//
//        DatabaseHelper(Context context) {
//            super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        }
//
//        @Override
//        public void onCreate(SQLiteDatabase db) {
//            StringBuilder sb = new StringBuilder();
//
//            DBUtils.createTable(db, sb, ItemColumns.TABLE_NAME,
//                    ItemColumns._ID, "INTEGER PRIMARY KEY AUTOINCREMENT",
//                    ItemColumns.COLUMN_NAME_LECTURE_VIDEO_ID, "TEXT",
//                    ItemColumns.COLUMN_NAME_EVENT_NAME, "TEXT",
//                    ItemColumns.COLUMN_NAME_EVENT_DESCRIPTION, "TEXT",
//                    ItemColumns.COLUMN_NAME_EVENT_LOGO_URL, "TEXT",
//                    ItemColumns.COLUMN_NAME_EVENT_WEBSITE_URL, "TEXT",
//                    ItemColumns.COLUMN_NAME_EVENT_START_DATE, "TEXT",
//                    ItemColumns.COLUMN_NAME_EVENT_END_DATE, "TEXT",
//                    ItemColumns.COLUMN_NAME_LECTURE_TITLE, "TEXT",
//                    ItemColumns.COLUMN_NAME_LECTURER_NAME, "TEXT",
//                    ItemColumns.COLUMN_NAME_LECTURER_IMAGE_URL, "TEXT",
//                    ItemColumns.COLUMN_NAME_LECTURER_BIO, "TEXT",
//                    ItemColumns.COLUMN_NAME_LECTURER_IMAGE, "BLOB",
//                    ItemColumns.COLUMN_NAME_LECTURE_VIDEO_URL, "TEXT",
//                    ItemColumns.COLUMN_NAME_LECTURE_SLIDES_URL, "TEXT",
//                    ItemColumns.COLUMN_NAME_LECTURE_DESCRIPTION, "TEXT",
//                    ItemColumns.COLUMN_NAME_LECTURE_DURATION, "TEXT",
//                    ItemColumns.COLUMN_NAME_TIMESTAMP, "INTEGER");
//        }
//
//        @Override
//        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            Log.w(TAG, StringUtils.append("Upgrading database from version ",
//                    oldVersion, " to ", newVersion, ", which will destroy all old data"));
//            StringBuilder sb = new StringBuilder(2);
//
//            DBUtils.dropTable(db, sb, ItemColumns.TABLE_NAME);
//            onCreate(db);
//        }
//    }
//
//    public static final class ItemColumns implements BaseColumns {
//        // This class cannot be instantiated
//        private ItemColumns() {
//        }
//
//        //This table name
//        public static final String TABLE_NAME = "assets";
//        //The content:// style URL for this table
//        public static final Uri CONTENT_URI;
//        //The content URI base for a single item. Callers must append a numeric video id id to this Uri to retrieve a asset
//        public static final Uri CONTENT_ID_URI_BASE;
//        //The MIME type of {@link #CONTENT_URI} providing a directory of Assets.
//        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.andconlab.ItemColumns";
//        //The MIME type of a {@link #CONTENT_URI} sub-directory of a single Asset.
//        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.andconlab.ItemColumns";
//
//        static final String COLUMN_NAME_TIMESTAMP = "timestamp";
//        
//        public static final String COLUMN_NAME_EVENT_NAME = "eventName";
//        public static final String COLUMN_NAME_EVENT_DESCRIPTION = "eventDescription";
//        public static final String COLUMN_NAME_EVENT_LOGO_URL = "eventLogoUrl";
//        public static final String COLUMN_NAME_EVENT_WEBSITE_URL = "eventWebsiteUrl";
//        public static final String COLUMN_NAME_EVENT_START_DATE = "eventStartDate";
//        public static final String COLUMN_NAME_EVENT_END_DATE = "eventEndDate";
//        public static final String COLUMN_NAME_LECTURE_VIDEO_ID = "lectureVideoId";
//        public static final String COLUMN_NAME_LECTURE_TITLE = "lectureTitle";
//        public static final String COLUMN_NAME_LECTURE_VIDEO_URL = "lectureVideoUrl";
//        public static final String COLUMN_NAME_LECTURE_SLIDES_URL = "lectureSlidesUrl";
//        public static final String COLUMN_NAME_LECTURE_DESCRIPTION = "lectureDescription";
//        public static final String COLUMN_NAME_LECTURE_DURATION = "lectureDuration";
//        
//        public static final String COLUMN_NAME_LECTURER_NAME = "lecturerName";
//        public static final String COLUMN_NAME_LECTURER_IMAGE_URL = "lecturerImageUrl";
//        public static final String COLUMN_NAME_LECTURER_BIO = "lecturerBio";
//        public static final String COLUMN_NAME_LECTURER_IMAGE = "lecturerImage";
//        
//
//        public static final int VIDEO_ID_PATH_POSITION = 1;
//        public static final String DEFAULT_SORT_ORDER = "timestamp ASC";
//
//        static {
//            StringBuilder sb = new StringBuilder(5);
//            CONTENT_ID_URI_BASE = DBUtils.createContentUri(sb, ItemsProvider.AUTHORITY, ItemColumns.TABLE_NAME, "/");
//            CONTENT_URI = DBUtils.createContentUri(sb, ItemsProvider.AUTHORITY, ItemColumns.TABLE_NAME);
//        }
//    }
//}