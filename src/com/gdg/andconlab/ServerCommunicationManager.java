package com.gdg.andconlab;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.type.TypeReference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.gdg.andconlab.R.string;
import com.gdg.andconlab.models.Event;
import com.gdg.andconlab.models.Lecture;
import com.gdg.andconlab.models.Speaker;
import com.gdg.andconlab.utils.JacksonUtils;

public class ServerCommunicationManager{
	
	public static final String TAG = "COMMS";
	public static final String RESULTS_ARE_IN = "RESULTS_ARE_IN";

	private static ServerCommunicationManager mThis = null;

    private static String GET_EVENTS;
    private static String GET_EVENT;
    private static String GET_EVENT_LECTURES;

    private static String GET_LECTURES;
    private static String GET_LECTURE;
    private static String GET_LECTURE_SPEAKERS;

    private static String GET_SPEAKERS;
    private static String GET_SPEAKER;
    private static String GET_SPEAKER_LECTURES;

	private HttpClient mHttpClient;        
	private ResponseHandler<String> mResponseHandler;

	private static Context mContext;
	
	private List<Event> mEvents;
	private LruCache<String, Bitmap> mImageCache;
//	private ItemsProvider mProvider;
	
	@SuppressLint("NewApi")
	public ServerCommunicationManager(Context context) {
		mHttpClient = new  DefaultHttpClient();
        HttpParams params = mHttpClient.getParams();
        int serverConnectionTimeout = context.getResources().getInteger(R.integer.http_connection_timeout_in_seconds) * 1000;
        HttpConnectionParams.setConnectionTimeout(params, serverConnectionTimeout);
        HttpConnectionParams.setSoTimeout(params, serverConnectionTimeout);

		mResponseHandler = new BasicResponseHandler();
		mImageCache = new LruCache<String, Bitmap>(100);
//		mProvider = new ItemsProvider();
		mContext = context;
        initApiStrings(context);
    }

    private void initApiStrings(Context context) {
        String serverAddress = context.getString(string.server_address);
        GET_EVENTS = String.format("%s%s", serverAddress, context.getString(string.api_get_events));
        GET_EVENT = String.format("%s%s", serverAddress, context.getString(string.api_get_event));
        GET_EVENT_LECTURES = String.format("%s%s", serverAddress, context.getString(string.api_get_event_lectures));

        GET_LECTURES = String.format("%s%s", serverAddress, context.getString(string.api_get_lectures));
        GET_LECTURE = String.format("%s%s", serverAddress, context.getString(string.api_get_lecture));
        GET_LECTURE_SPEAKERS = String.format("%s%s", serverAddress, context.getString(string.api_get_lecture_speakers));

        GET_SPEAKERS = String.format("%s%s", serverAddress, context.getString(string.api_get_speakers));
        GET_SPEAKER = String.format("%s%s", serverAddress, context.getString(string.api_get_speaker));
        GET_SPEAKER_LECTURES = String.format("%s%s", serverAddress, context.getString(string.api_get_speaker_lectures));
    }

    public static synchronized ServerCommunicationManager getInstance(Context context){
        // TODO bad singleton pattern
        // TODO dangerous class member - context - potential leak if given context other then application context
		if(mThis == null){
			mThis = new ServerCommunicationManager(context);
		}

		return mThis;
	}
	
	
	public void startSearch(String searchTerm, int page) {
		new RetreiveFeedTask().execute(GET_EVENTS);
	}
	
	public List<Event> getEvents() {
		return mEvents;
	}
	
	public void getBitmap(ImageView iv){
		new DownloadImage().execute(iv);
	}
	
	class RetreiveFeedTask extends AsyncTask<String, Void, List<Event>> {

	    protected List<Event> doInBackground(String... urls) {
	        try {
	            HttpGet httpGet = new HttpGet(urls[0]);
	    		String responseBody = null;
	    		
	    		try {
//	    			responseBody = readTextFromFile("response", true);
                    responseBody = mHttpClient.execute(httpGet, mResponseHandler);
	    		} catch(Exception ex) {
	    		    ex.printStackTrace();
	    		}

                List<Event> events = null;
	    		if(responseBody != null) {
                    events = JacksonUtils.sReadValue(responseBody, new TypeReference<List<Event>>() {}, false);
                    if (events != null) {
                        for (Event event : events) {
                        	Log.d(TAG,"Event id: " + event.getId());
                        	Log.d(TAG,"Event name: " + event.getName());
                        	
                        	List<Lecture> lectures = event.getLectures();
                        	for (Lecture lecture : lectures) {
                        		Log.d(TAG,"Lecture id" + lecture.getId());
                        		Log.d(TAG,"Lecture name" + lecture.getName());
                        		
                        		List<Speaker> speakers = lecture.getSpeakers();
                        		
                        		for (Speaker speaker : speakers) {
                        			Log.d(TAG,"Lecture id" + speaker.getId());
                            		Log.d(TAG,"Lecture name" + speaker.getFirstName());
                        		}
                        	}
                        }
                        
                        SQLiteDatabase db = new DatabaseHelper(mContext, DatabaseHelper.DB_NAME, null, DatabaseHelper.DB_VERSION).getWritableDatabase();
                        DBUtils.storeEvents(db, events);
                    }
	    		}
	            return events;
	        } catch (Exception e) {
	        	e.printStackTrace();
	            Log.d(TAG, "exception");
	        	return null;
	        }
	    }

	    protected void onPostExecute(List<Event> events) {
	    	mEvents = events;
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
        	//TODO: [Ran] implement this
        	/*iv = params[0];
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
            return mBitmap;*/
        	
        	return null;
        }

		@Override
        protected void onPostExecute( Bitmap d ) {
            iv.setImageBitmap(d);
        }
        
        public Bitmap getBitmapFromUrl(String bitmapUrl) {
        	//TODO: [Ran] Implement this
        	  /*try {
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
*/        	    
        	    
        	    /*values.put(ItemColumns.COLUMN_NAME_LECTURER_IMAGE, bArray);
        	    Uri uri = Uri.withAppendedPath(ItemColumns.CONTENT_URI, mAssetId);
        	    
        	    mContext.getContentResolver().update(uri, values, ItemColumns.COLUMN_NAME_LECTURE_VIDEO_ID + "='" + mAssetId + "' AND ", null);*/
        	    
        	    return null;
        	  /*}
        	  catch(Exception ex) {
        		  Log.d(TAG, "Message");
        		  return null;
        	  }*/
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
