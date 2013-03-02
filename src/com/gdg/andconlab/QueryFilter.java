package com.gdg.andconlab;

/**
 * Query filter model.
 * Used to filter delete/update actions on a specific database
 *
 * @author Amir Lazarovich
 * @version 0.1
 */
public class QueryFilter {

    public String tableName;
    public String where;

    /**
     * Constructs an empty query filter object
     */
    public QueryFilter() {}
}

