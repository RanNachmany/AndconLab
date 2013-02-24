package com.gdg.andconlab;


// TODO: Need to compare the performance of append(String, String) with append(String...)

import android.annotation.SuppressLint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.widget.TextView;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

/**
 * @author Amir Lazarovich
 * @version 1.0.6
 */
@SuppressLint("DefaultLocale")
public class StringUtils {

    private static StringBuilder sStringBuilder;
    private static Formatter sFormatter;
    public static final int ALL = -1;

    static {
        sStringBuilder = new StringBuilder();
        sFormatter = new Formatter(sStringBuilder, Locale.getDefault());
    }

    /**
     * Combine both strings into one
     *
     * @param partA
     * @param partB
     * @return
     */
    public static String append(String partA, String partB) {
        StringBuilder sb = new StringBuilder(2);
        return append(sb, partA, partB);
    }

    /**
     * Combine both strings into one
     *
     * @param sb    Clears any existing values before starting to append new values
     * @param partA
     * @param partB
     * @return
     */
    public static String append(StringBuilder sb, String partA, String partB) {
        clearBuffer(sb);
        sb.append(partA);
        sb.append(partB);
        return sb.toString();
    }

    /**
     * Append multiple parts of String values into one using a StringBuilder
     *
     * @param parts
     * @return
     */
    public static String append(String... parts) {
        StringBuilder sb = new StringBuilder(parts.length);
        return append(sb, parts);
    }

    /**
     * Append multiple parts of String values into one using a StringBuilder
     *
     * @param sb    Clears any existing values before starting to append new values
     * @param parts
     * @return
     */
    public static String append(StringBuilder sb, String... parts) {
        clearBuffer(sb);

        for (String part : parts) {
            if (part != null) {
                sb.append(part);
            }
        }

        return sb.toString();
    }

    /**
     * Append multiple parts of Object values into one using a StringBuilder
     *
     * @param parts
     * @return
     */
    public static String append(Object... parts) {
        StringBuilder sb = new StringBuilder(parts.length);
        return append(sb, parts);
    }

    /**
     * Append multiple parts of Object values into one using a StringBuilder
     *
     * @param sb    Clears any existing values before starting to append new values
     * @param parts
     * @return
     */
    public static String append(StringBuilder sb, Object... parts) {
        clearBuffer(sb);

        for (Object part : parts) {
            if (part instanceof String) {
                sb.append((String) part);
            } else if (part != null) {
                sb.append(part);
            }
        }

        return sb.toString();
    }

    /**
     * Get string representation of given time (in milliseconds)
     *
     * @param timeMs Time in milliseconds (Unix time)
     * @return
     */
    public static String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        clearBuffer(sStringBuilder);
        if (hours > 0) {
            return sFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return sFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }


    /**
     * Clears the buffer of given StringBuilder if it's not already empty
     *
     * @param sb The StringBuilder that may needs to be cleaned
     */
    public static void clearBuffer(StringBuilder sb) {
        // Clears the buffer if it's not empty
        if (sb.length() > 0) {
            sb.setLength(0);
        }
    }

    public static <T> StringBuilder getCollectionString(List<T> items, String separator) {
        return getCollectionString(items, separator, ALL);
    }


    /**
     * Append all items into a single string where each item is separated by given <i>separator</i>
     *
     * @param items
     * @param separator
     * @param <T>
     * @return
     */
    public static <T> StringBuilder getCollectionString(List<T> items, String separator, int maxItems) {
        StringBuilder sb = new StringBuilder();
        int noItems = maxItems != ALL && maxItems <= items.size() ? maxItems : items.size();
        T item;
        for (int i = 0; i < noItems; i++) {
            item = items.get(i);
            if (item != null) {
                sb.append(item.toString());
                if (i + 1 < noItems) {
                    sb.append(separator != null ? separator : " ");
                }
            }
        }

        return sb;
    }

    /**
     * Append all items into a single string where each item is separated by given <i>separator</i>
     *
     * @param items
     * @param separator
     * @param <T>
     * @return
     */
    public static <T> StringBuilder getEnumCollectionString(List<Object> items, String separator, int maxItems) {
        StringBuilder sb = new StringBuilder();
        int noItems = maxItems != ALL && maxItems <= items.size() ? maxItems : items.size();
        Object item;
        for (int i = 0; i < noItems; i++) {
            item = items.get(i);
            if (item != null) {
                sb.append(item.toString());
                if (i + 1 < noItems) {
                    sb.append(separator != null ? separator : " ");
                }
            }
        }

        return sb;
    }

    public static void setListText(TextView view, String single, String many, List<String> items, String separator) {
        setListText(view, single, many, items, separator, ALL);
    }

    /**
     * Append all items into a single string where each item is separated by given <i>separator</i>. <br/> Update the TextView
     * text as follows: <li>In case no string was found inside the items, set it to the empty string</li> <li>In case there was
     * only a single item, set: "single": "item"</li> <li>Otherwise, (in case there was more than one item), set: "many": "item
     * [separator] item ..."</li>
     *
     * @param view
     * @param single
     * @param many
     * @param items
     * @param separator
     */
    public static void setListText(TextView view, String single, String many, List<String> items, String separator, int maxItems) {
        if (items != null && items.size() > 0) {
            StringBuilder buffer = new StringBuilder();
            if (items.size() == 1 && single != null && single != "") {
                buffer.append(single).append(":");
            } else if (many != null && many != "") {
                buffer.append(many).append(":");
            }

            buffer.append(getCollectionString(items, separator));
            view.setText(buffer);

        } else {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * Append all items into a single string where each item is separated by a comma. <br/> Update the TextView text as follows:
     * <li>In case no string was found inside the items, set it to the empty string</li> <li>In case there was only a single item,
     * set: "single": "item"</li> <li>Otherwise, (in case there was more than one item), set: "many": "item, item ..."</li>
     *
     * @param view
     * @param single
     * @param many
     * @param items
     */
    public static void setListText(TextView view, String single, String many, List<String> items) {
        setListText(view, single, many, items, ", ");
    }

    public static CharSequence getSuperscriptString(CharSequence base) {
        return getSuperscriptString(base, 0, base.length() - 1);
    }

    public static CharSequence getSuperscriptString(CharSequence base, int startFromIdx, int endAtIdx) {
        SpannableString str = new SpannableString(base);
        str.setSpan(new SuperscriptSpan(), startFromIdx, endAtIdx, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return str;
    }

    public static CharSequence getSubscriptString(CharSequence base) {
        return getSubscriptString(base, 0, base.length() - 1);
    }

    public static CharSequence getSubscriptString(CharSequence base, int startFromIdx, int endAtIdx) {
        SpannableString str = new SpannableString(base);
        str.setSpan(new SubscriptSpan(), startFromIdx, endAtIdx, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return str;
    }

    /**
     * Check if given string value is empty or null
     *
     * @param value
     * @return
     */
    public static boolean isEmptyOrNull(String value) {
        return ((value == null) || (value.equals("")));
    }

    public static boolean isEmptyOrNull(CharSequence value) {
        return ((value == null) || (value.equals("")));
    }

    /**
     * Check if given string value is NOT empty or null
     *
     * @param value
     * @return
     */
    public static boolean isNotEmptyOrNull(String value) {
        return !isEmptyOrNull(value);
    }

    /**
     * Get the text value inside given {@link android.widget.TextView}
     *
     * @param textView
     * @return
     */
    public static String getText(TextView textView) {
        return (textView != null) ? textView.getText().toString() : "";
    }

    @SuppressLint("DefaultLocale")
	public static String toCamelString(String source) {
        if (source == null) {
            return source;
        }

        source = source.toLowerCase();
        String charAt = String.valueOf(source.charAt(0));
        return source.replaceFirst(charAt, charAt.toUpperCase());
    }
}
