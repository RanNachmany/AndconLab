
package com.gdg.andconlab.utils;

import android.text.TextUtils;
import android.util.Log;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.List;


/**
 * @author Amir Lazarovich
 * @version 0.1
 */
public class JacksonUtils
{

	// /////////////////////////////////////////////
	// Constants
	// /////////////////////////////////////////////
	private static final String TAG = "JacksonUtils";
	public static final boolean DEFAULT_WRAP_ROOT = true;

	// /////////////////////////////////////////////
	// Public
	// /////////////////////////////////////////////

	/**
	 * deserialize <i>value</i> to <i>valueType</i>. <br/>
	 * Sets configuration feature {@link DeserializationConfig.Feature#UNWRAP_ROOT_VALUE} to {@link #DEFAULT_WRAP_ROOT}
	 * 
	 * @param value
	 * @param valueType
	 * @param <T>
	 * @return Deserialized object or throws an exception in case of a failure
	 * @throws java.io.IOException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 */
	public static <T> T readValue(String value, Class<T> valueType) throws IOException, JsonParseException,
			JsonMappingException
	{
		return readValue(value, valueType, DEFAULT_WRAP_ROOT);
	}


	/**
	 * deserialize <i>value</i> to <i>valueType</i>
	 * 
	 * @param value
	 * @param valueType
	 * @param wrapRoot
	 *            Configures {@link DeserializationConfig.Feature#UNWRAP_ROOT_VALUE}
	 * @param <T>
	 * @return Deserialized object or throws an exception in case of a failure
	 * @throws java.io.IOException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 */
	public static <T> T readValue(String value, Class<T> valueType, boolean wrapRoot) throws IOException, JsonParseException,
			JsonMappingException
	{
        if (TextUtils.isEmpty(value)) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, wrapRoot);
        return mapper.readValue(value, valueType);
	}

    /**
     * deserialize <i>value</i> to list of <i>valueType</i>
     *
     * @param value
     * @param valueType
     * @param wrapRoot
     * @param <T>
     * @return
     * @throws java.io.IOException
     * @throws JsonParseException
     * @throws JsonMappingException
     */
    public static <T> List<T> readValues(String value, Class<T> valueType, boolean wrapRoot) throws IOException, JsonParseException,
            JsonMappingException {
        if (TextUtils.isEmpty(value)) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, wrapRoot);
        JavaType t = mapper.getTypeFactory().constructCollectionType(List.class, valueType);
        return mapper.readValue(value, t);
    }


	/**
	 * deserialize <i>value</i> to <i>valueType</i>. <br/>
	 * Sets configuration feature {@link DeserializationConfig.Feature#UNWRAP_ROOT_VALUE} to {@link #DEFAULT_WRAP_ROOT}
	 * 
	 * @param value
	 * @param valueType
	 * @param <T>
	 * @return Deserialized object or <i>null</i> in case of a failure
	 */
	public static <T> T sReadValue(String value, Class<T> valueType)
	{
		return sReadValue(value, valueType, DEFAULT_WRAP_ROOT);
	}

    /**
     * deserialize <i>value</i> to <i>valueType</i>. <br/>
     * Sets configuration feature {@link DeserializationConfig.Feature#UNWRAP_ROOT_VALUE} to {@link #DEFAULT_WRAP_ROOT}
     *
     * @param value
     * @param type
     * @param <T>
     * @return Deserialized object or <i>null</i> in case of a failure
     */
    public static <T> T sReadValue(String value, TypeReference<T> type) {
        return sReadValue(value, type, DEFAULT_WRAP_ROOT);
    }


	/**
	 * Safely deserialize <i>value</i> to <i>valueType</i>
	 * 
	 * @param value
	 * @param valueType
	 * @param wrapRoot
	 *            Configures {@link DeserializationConfig.Feature#UNWRAP_ROOT_VALUE}
	 * @param <T>
	 * @return Deserialized object or <i>null</i> in case of a failure
	 */
	public static <T> T sReadValue(String value, Class<T> valueType, boolean wrapRoot)
	{
        if (TextUtils.isEmpty(value)) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, wrapRoot);
		T result = null;
		try {
			result = mapper.readValue(value, valueType);
		}
		catch (Exception e) {
			Log.e(TAG, String.format("Couldn't parse value: %s", value), e);
		}

		return result;
	}

    /**
     * Safely deserialize <i>value</i> to <i>valueType</i>
     *
     * @param value
     * @param type
     * @param wrapRoot
     *            Configures {@link DeserializationConfig.Feature#UNWRAP_ROOT_VALUE}
     * @param <T>
     * @return Deserialized object or <i>null</i> in case of a failure
     */
    public static <T> T sReadValue(String value, TypeReference<T> type, boolean wrapRoot)
    {
        if (TextUtils.isEmpty(value)) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, wrapRoot);
        T result = null;
        try {
            result = mapper.readValue(value, type);
        }
        catch (Exception e) {
            Log.e(TAG, String.format("Couldn't parse value: %s", value), e);
        }

        return result;
    }


	/**
	 * Safely deserialize <i>value</i> to <i>valueType</i>.<br/>
	 * Sets configuration feature {@link DeserializationConfig.Feature#UNWRAP_ROOT_VALUE} to {@link #DEFAULT_WRAP_ROOT}
	 * 
	 * @param value
	 * @param valueType
	 * @param <T>
	 * @return Deserialized object or an empty instance of type <i>valueType</i> in case of a failure. <br/>
	 *         If <i>valueType</i> class does not contain an empty default constructor or restricts access to it, the return
	 *         value is null
	 */
	public static <T> T readValueWithDefault(String value, Class<T> valueType)
	{
		return readValueWithDefault(value, valueType, DEFAULT_WRAP_ROOT);
	}


	/**
	 * Safely deserialize <i>value</i> to <i>valueType</i>
	 * 
	 * @param value
	 * @param valueType
	 * @param wrapRoot
	 *            Configures {@link DeserializationConfig.Feature#UNWRAP_ROOT_VALUE}
	 * @param <T>
	 * @return Deserialized object or an empty instance of type <i>valueType</i> in case of a failure. <br/>
	 *         If <i>valueType</i> class does not contain an empty default constructor or restricts access to it, the return
	 *         value is null
	 */
	public static <T> T readValueWithDefault(String value, Class<T> valueType, boolean wrapRoot)
	{
        if (TextUtils.isEmpty(value)) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, wrapRoot);
		T result = null;
		try {
			result = mapper.readValue(value, valueType);

		}
		catch (Exception e) {
            Log.e(TAG, String.format("Couldn't parse value: %s", value), e);
			try {
				result = valueType.newInstance();
			}
			catch (Exception e1) {
				Log.e(TAG, String.format("Couldn't get access to default constructor of [%s]", valueType.toString()), e1);
			}
		}

		return result;
	}

    /**
     * Serialize <code>obj</code> to json string
     *
     * @param obj
     * @param wrapRoot
     * @return JSON string representation of <code>obj</code>
     */
    public static String sToJson(Object obj, boolean wrapRoot) {
        String result = "";
        if (obj == null) {
            return result;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false);
            mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, wrapRoot);
            mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
            result = mapper.writeValueAsString(obj);
        } catch (Exception e) {
            Log.e(TAG, "Couldn't serialize object to JSON", e);
        }

        return result;
    }
}
