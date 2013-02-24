package com.gdg.andconlab;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.gdg.andconlab.ItemsProvider.ItemColumns;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

public class ServerCommunicationManager{
	
	public static final String TAG = "TWITTER_SEARCH_EXAMPLE_SERVER_COMMUNICATION";
	public static final String BASE_URL = "http://search.twitter.com/search.json?q=@";
	public static final String RPP = "&rpp=100";
	public static final String PAGE = "&page=";
	public static final String RESULTS_ARE_IN = "RESULTS_ARE_IN";
	
	public static final String JSON_OBJECT_RESULTS = "results";
	
	private static ServerCommunicationManager mThis = null;
	
	private HttpClient mHttpClient;        
	private ResponseHandler<String> mResponseHandler;

	private static Context mContext;
	
	private ArrayList<Item> mItems;
	private LruCache<String, Bitmap> mImageCache;
	private ItemsProvider mProvider;
	
	public ServerCommunicationManager(Context context) {
		mHttpClient = new  DefaultHttpClient();
		mResponseHandler = new BasicResponseHandler();
		mImageCache = new LruCache<String, Bitmap>(100);
		mProvider = new ItemsProvider();
		mContext = context;
	}
	
	public static ServerCommunicationManager getInstance(Context context){
		if(mThis == null){
			mThis = new ServerCommunicationManager(context);
		}
		
		return mThis;
	}
	
	
	public void startSearch(String searchTerm, int page) {
		String searchUrl = BASE_URL + searchTerm + RPP + PAGE + page;
		
		new RetreiveFeedTask().execute(searchUrl);
	}
	
	public ArrayList<Item> getItems() {
		return mItems;
	}
	
	public void getBitmap(ImageView iv){
		new DownloadImage().execute(iv);
	}
	
	class RetreiveFeedTask extends AsyncTask<String, Void, ArrayList<Item>> {

	    protected ArrayList<Item> doInBackground(String... urls) {
	        try {
	            HttpGet httpGet = new HttpGet(urls[0]);
	  		  
	            ArrayList<Item> Items = new ArrayList<Item>();
	    		    
	    		  
	    		String responseBody = null;
	    		
	    		try {
	    			responseBody = readTextFromFile("response", true);//mHttpClient.execute(httpGet, mResponseHandler);
	    		} catch(Exception ex) {
	    		    ex.printStackTrace();
	    		}

	    		if(responseBody != null){
	    			JSONObject jsonObject = null;
	    			  JSONParser parser=new JSONParser();
	    			    
	    			  try {
	    			    Object obj = parser.parse(responseBody);
	    			    jsonObject=(JSONObject)obj;
	    			  }catch(Exception ex){
	    			    Log.v(TAG,"Exception: " + ex.getMessage());
	    			  }
	    			    
	    			  JSONArray arr = null;
	    			    
	    			  try {
	    			    Object j = jsonObject.get(JSON_OBJECT_RESULTS);
	    			    arr = (JSONArray)j;
	    			  } catch(Exception ex){
	    			    Log.v(TAG,"Exception: " + ex.getMessage());
	    			  }

	    			  Item item;
	    			  ContentValues values = new ContentValues();
	    			  for(Object t : arr) {
	    			    item = new Item(
	    			      ((JSONObject)t).get(Item.JSON_OBJECT_ITEM_EVENT_NAME).toString(),
	    			      ((JSONObject)t).get(Item.JSON_OBJECT_ITEM_LECTURE_TITLE).toString(),
	    			      ((JSONObject)t).get(Item.JSON_OBJECT_ITEM_VIDEO).toString(),
	    			      ((JSONObject)t).get(Item.JSON_OBJECT_ITEM_SLIDES).toString(),
	    			      ((JSONObject)t).get(Item.JSON_OBJECT_ITEM_YOUTUBE_ID).toString(),
	    			      ((JSONObject)t).get(Item.JSON_OBJECT_ITEM_DESCRIPTION).toString(),
	    			      ((JSONObject)t).get(Item.JSON_OBJECT_ITEM_LECTURER_NAME).toString(),
	    			      ((JSONObject)t).get(Item.JSON_OBJECT_ITEM_LECTURER_IMAGE).toString()
	    			    );
	    			    Items.add(item);
	    			    values.put(ItemColumns.COLUMN_NAME_EVENT_NAME, item.getEventName());
	    			    values.put(ItemColumns.COLUMN_NAME_LECTURE_TITLE, item.getLectureTitle());
	    			    values.put(ItemColumns.COLUMN_NAME_LECTURE_VIDEO_URL, item.getLectureVideoUrl());
	    			    values.put(ItemColumns.COLUMN_NAME_LECTURE_SLIDES_URL, item.getLectureSlidesUrl());
	    			    values.put(ItemColumns.COLUMN_NAME_LECTURE_VIDEO_ID, item.getLectureYoutubeAssetId());
	    			    values.put(ItemColumns.COLUMN_NAME_LECTURE_DESCRIPTION, item.getLectureDescription());
	    			    values.put(ItemColumns.COLUMN_NAME_LECTURER_NAME, item.getLecturerName());
	    			    values.put(ItemColumns.COLUMN_NAME_LECTURER_IMAGE_URL, item.getLecturerProfileImageUrl());
	    			    //mProvider.insert(ItemColumns.CONTENT_URI, values);
	    			    mContext.getContentResolver().insert(ItemColumns.CONTENT_URI, values);
	    			  }
	    		}
	            return Items;
	        } catch (Exception e) {
	            Log.d(TAG, "exception");
	        	return null;
	        }
	    }

	    protected void onPostExecute(ArrayList<Item> Items) {
	    	mItems = Items;
	        Intent intent = new Intent();
	        intent.setAction(RESULTS_ARE_IN);
	          
	        mContext.sendBroadcast(intent);
	    }
	 }
	
	class DownloadImage extends AsyncTask<ImageView, Void, Bitmap>{
        private ImageView iv;
        private Bitmap mBitmap;
        private String mAssetId;
        private String mUrl;

        @Override
        protected Bitmap doInBackground(ImageView... params) {
        	iv = params[0];
        	mAssetId = ((String) iv.getTag()).substring(((String) iv.getTag()).indexOf("$sep$")+"$sep$".length(),((String) iv.getTag()).length());
        	mUrl = ((String) iv.getTag()).substring(0, ((String) iv.getTag()).indexOf("$sep$"));
            //mBitmap = mImageCache.get(mUrl);
            if(mBitmap == null){
            	if(mProvider == null){
            		mProvider = new ItemsProvider();
            	}
            	Uri uri = Uri.withAppendedPath(ItemColumns.CONTENT_URI, mAssetId);
            	String[] mSelectionArgs = {ItemColumns.COLUMN_NAME_LECTURER_IMAGE};
            	Cursor cursor = null;
            	try{
            		cursor = mContext.getContentResolver().query(uri,mSelectionArgs, null, null, null);//mProvider.query(uri, null, null, null, null);
            		ByteArrayInputStream inputStream = new ByteArrayInputStream(cursor.getBlob(cursor.getColumnIndex(ItemColumns.COLUMN_NAME_LECTURER_IMAGE)));
            		mBitmap = BitmapFactory.decodeStream(inputStream);
            		cursor.close();
            	}catch(Exception e){
            		Log.d(TAG, "message");
            		if(cursor != (Cursor) null) cursor.close();
            	}
            	
            }
            if(mBitmap == null){
            	mBitmap = getBitmapFromUrl(mUrl); 
            }
            return mBitmap;
        }

		@Override
        protected void onPostExecute( Bitmap d ) {
            iv.setImageBitmap(d);
        }
        
        public Bitmap getBitmapFromUrl(String bitmapUrl) {
        	  try {
        	    URL url = new URL(bitmapUrl);
        	    Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream()); 
        	    mImageCache.put(bitmapUrl, bitmap);
        	    
        	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
        	    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        	    byte[] bArray = bos.toByteArray();
        	    //ContentValues values = new ContentValues();
        	    
        	    // Defines an object to contain the updated values
        	    ContentValues mUpdateValues = new ContentValues();

        	    // Defines selection criteria for the rows you want to updates
        	    String where = ItemColumns.COLUMN_NAME_LECTURER_IMAGE_URL + "='" + bitmapUrl + "' AND ";
        	    String[] mSelectionArgs = null;//{bitmapUrl};

        	    // Defines a variable to contain the number of updated rows
        	    int mRowsUpdated = 0;
        	    
        	    mUpdateValues.put(ItemColumns.COLUMN_NAME_LECTURER_IMAGE, bArray);

        	    Uri uri = Uri.withAppendedPath(ItemColumns.CONTENT_URI, mAssetId);
        	    mRowsUpdated = mContext.getContentResolver().update(
        	        uri,   		// the user dictionary content URI
        	        mUpdateValues,                  // the columns to update
        	        where,               // the column to select on
        	        mSelectionArgs                  // the value to compare to
        	    );
        	    
        	    
        	    /*values.put(ItemColumns.COLUMN_NAME_LECTURER_IMAGE, bArray);
        	    Uri uri = Uri.withAppendedPath(ItemColumns.CONTENT_URI, mAssetId);
        	    
        	    mContext.getContentResolver().update(uri, values, ItemColumns.COLUMN_NAME_LECTURE_VIDEO_ID + "='" + mAssetId + "' AND ", null);*/
        	    
        	    return bitmap;
        	  }
        	  catch(Exception ex) {
        		  Log.d(TAG, "Message");
        		  return null;
        	  }
        	}

	}
	
	 /**
     * Read text from file
     *
     * @param filename
     * @return
     */
    public static String readTextFromFile(String filename, boolean isAsset) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            InputStream in;
            if (isAsset) {
                in = mContext.getAssets().open(filename);
            } else {
                in = new FileInputStream(filename);
            }

            if (in != null) {
                InputStreamReader input = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(input);
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                in.close();
            }
        } catch (Exception e) {
            Log.e(TAG, String.format("Couldn't read text from file: %s", filename), e);
        }

        return stringBuilder.toString();
    }
}
