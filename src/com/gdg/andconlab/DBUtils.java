package com.gdg.andconlab;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Utility class for DB related operations
 *
 * @author Amir Lazarovich
 * @version 0.1
 */
public class DBUtils {
//select lecturerImage from assets where lectureVideoId='Y4UMzOWcgGQ';
    /**
     * Create DB table
     *
     * @param db        Reference to the underlying database
     * @param sb        Clears any existing values before starting to append new values
     * @param tableName The name of the DB table
     * @param columns   Tuples of column names and their corresponding type and properties. This field must be even for that same
     *                  reason. I.e. "my_column", "INTEGER PRIMARY KEY AUTOINCREMENT", "my_second_column", "VARCHAR(255)"
     */
    public static void createTable(SQLiteDatabase db, StringBuilder sb, String tableName, String... columns) {
        if (columns.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "Columns length should be even since each column is followed by its corresponding type and properties");
        }

        StringUtils.clearBuffer(sb);
        // Prepare table
        sb.append("CREATE TABLE ");
        sb.append(tableName);
        sb.append(" (");

        // Parse all columns
        int length = columns.length;
        for (int i = 0; i < length; i += 2) {
            sb.append(columns[i]);
            sb.append(" ");
            sb.append(columns[i + 1]);

            if (i + 2 < length) {
                // Append comma only if this isn't the last column
                sb.append(", ");
            }
        }

        sb.append(");");

        // Create table
        db.execSQL(sb.toString());
    }

    /**
     * Drop table if exists in given database
     *
     * @param db        Reference to the underlying database
     * @param tableName The table name of which we try to drop
     */
    public static void dropTable(SQLiteDatabase db, String tableName) {
        dropTable(db, new StringBuilder(), tableName);
    }

    /**
     * Drop table if exists in given database
     *
     * @param db        Reference to the underlying database
     * @param sb        Clears any existing values before starting to append new values
     * @param tableName The table name of which we try to drop
     */
    public static void dropTable(SQLiteDatabase db, StringBuilder sb, String tableName) {
        StringUtils.clearBuffer(sb);
        sb.append("DROP TABLE IF EXISTS ");
        sb.append(tableName);

        // Drop table
        db.execSQL(sb.toString());
    }

    /**
     * Creates a Content Uri out of given authority and table name at the following manner: <br/>
     * content://AUTHORITY/PATH
     *
     * @param sb
     * @param authority
     * @param path
     * @return
     */
    public static Uri createContentUri(StringBuilder sb, String authority, String... path) {
        StringUtils.clearBuffer(sb);

        sb.append("content://");
        sb.append(authority);
        sb.append("/");

        for (String part : path) {
            sb.append(part);
        }

        return Uri.parse(sb.toString());
    }

    /**
     * Appends the given ID to the end of the path.
     *
     * @param builder to append the ID to
     * @param id to append
     *
     * @return the given builder
     */
    public static Uri.Builder appendId(Uri.Builder builder, String id) {
        return builder.appendEncodedPath(id);
    }

    /**
     * Appends the given ID to the end of the path.
     *
     * @param contentUri to start with
     * @param id to append
     *
     * @return a new URI with the given ID appended to the end of the path
     */
    public static Uri withAppendedId(Uri contentUri, String id) {
        return appendId(contentUri.buildUpon(), id).build();
    }

}
