package com.gdg.andconlab;

import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.type.TypeReference;

import com.gdg.andconlab.R.string;
import com.gdg.andconlab.models.Event;
import com.gdg.andconlab.models.Lecture;
import com.gdg.andconlab.models.Speaker;
import com.gdg.andconlab.utils.JacksonUtils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Log;
import android.util.LruCache;

public class CommunicationService extends Service{

	public static final String RESULTS_ARE_IN = "RESULTS_ARE_IN";
	
	private Thread mWorker;
	
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

	
	@Override
	public void onCreate() {
		super.onCreate();
		mHttpClient = new  DefaultHttpClient();
        HttpParams params = mHttpClient.getParams();
        int serverConnectionTimeout = getResources().getInteger(R.integer.http_connection_timeout_in_seconds) * 1000;
        HttpConnectionParams.setConnectionTimeout(params, serverConnectionTimeout);
        HttpConnectionParams.setSoTimeout(params, serverConnectionTimeout);

		mResponseHandler = new BasicResponseHandler();
//		mImageCache = new LruCache<String, Bitmap>(100);
//		mProvider = new ItemsProvider();
//		mContext = context;
        initApiStrings();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (null != mWorker && mWorker.isAlive()) {
			//we are already running a transaction, nothing to do here
			return Service.START_STICKY;
		}
		
		//start new job
		mWorker = new Thread(new TransactionJob());
		mWorker.start();
		return Service.START_STICKY;
	}

	private void initApiStrings() {
        String serverAddress = getResources().getString(string.server_address);
        GET_EVENTS = String.format("%s%s", serverAddress, getResources().getString(string.api_get_events));
        GET_EVENT = String.format("%s%s", serverAddress, getResources().getString(string.api_get_event));
        GET_EVENT_LECTURES = String.format("%s%s", serverAddress, getResources().getString(string.api_get_event_lectures));

        GET_LECTURES = String.format("%s%s", serverAddress, getResources().getString(string.api_get_lectures));
        GET_LECTURE = String.format("%s%s", serverAddress, getResources().getString(string.api_get_lecture));
        GET_LECTURE_SPEAKERS = String.format("%s%s", serverAddress, getResources().getString(string.api_get_lecture_speakers));

        GET_SPEAKERS = String.format("%s%s", serverAddress, getResources().getString(string.api_get_speakers));
        GET_SPEAKER = String.format("%s%s", serverAddress, getResources().getString(string.api_get_speaker));
        GET_SPEAKER_LECTURES = String.format("%s%s", serverAddress, getResources().getString(string.api_get_speaker_lectures));
    }
	
	private class TransactionJob implements Runnable {

		private static final String TAG = "Service";

		@Override
		public void run() {
			try {
	            HttpGet httpGet = new HttpGet(GET_EVENTS);
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
                        
                        SQLiteDatabase db = new DatabaseHelper(CommunicationService.this.getApplicationContext(), DatabaseHelper.DB_NAME, null, DatabaseHelper.DB_VERSION).getWritableDatabase();
                        DBUtils.storeEvents(db, events);
                        
                        //fire an intent
                        Intent intent = new Intent();
            	        intent.setAction(RESULTS_ARE_IN);
            	          
            	        CommunicationService.this.sendBroadcast(intent);
                    }
	    		}
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
		}
	}
}
