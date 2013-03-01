package com.gdg.andconlab.utils;

import com.android.internal.util.Predicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Amir Lazarovich
 * @version 0.1
 */
public class CollectionUtils {

    private static final String TAG = "CollectionUtils";

    /**
     * Convenient way to check if a collection is empty
     *
     * @param collection
     * @return True if the collection is empty or null
     */
    public static boolean isEmpty(Collection collection) {
        return (collection == null) || collection.isEmpty();
    }

    /**
     * Convenient way to check if a collection is NOT empty
     *
     * @param collection
     * @return True if the collection is NOT empty
     */
    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    /**
     * Convenient way to check if a give list of collections is empty
     *
     * @param collections
     * @return True if all collections are empty or null
     */
    public static boolean isEmpty(Collection... collections) {
        for (Collection collection : collections) {
            if (!isEmpty(collection)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check whether <code>collection</code> is empty
     *
     * @param collection
     * @return
     */
    public static boolean isEmpty(Iterable collection) {
        return (collection == null) || (collection.iterator() == null) || !collection.iterator().hasNext();
    }

    /**
     * Filter <code>target</code> collection using given <code>predicate</code>
     *
     * @param target
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> List<T> filter(Collection<T> target, Predicate<T> predicate) {
        List<T> result = new ArrayList<T>();
        for (T element: target) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * Get the size of <code>collection</code>
     *
     * @param collection
     * @return
     */
    public static int getSize(Collection collection) {
        return (collection != null) ? collection.size() : 0;
    }

    /**
     * Safely get an item at <code>position</code>
     *
     * @param collection
     * @param position
     * @param <T>
     * @return
     */
    public static <T> T getAt(List<T> collection, int position) {
        if (collection == null || collection.size() <= position || position < 0) {
            SLog.w(TAG, "Illegal retrieval detected at position: [%d]", position);
            return null;
        } else {
            return collection.get(position);
        }
    }
}
