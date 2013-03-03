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

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import com.gdg.andconlab.R.string;
import com.gdg.andconlab.models.Event;
import com.gdg.andconlab.models.Lecture;
import com.gdg.andconlab.models.Speaker;
import com.gdg.andconlab.utils.JacksonUtils;

public class CommunicationService extends Service{

	public static final String RESULTS_ARE_IN = "RESULTS_ARE_IN";
	
	private Thread mWorker;
	
    private static String GET_EVENTS;
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
        String.format("%s%s", serverAddress, getResources().getString(string.api_get_event));
        String.format("%s%s", serverAddress, getResources().getString(string.api_get_event_lectures));

        String.format("%s%s", serverAddress, getResources().getString(string.api_get_lectures));
        String.format("%s%s", serverAddress, getResources().getString(string.api_get_lecture));
        String.format("%s%s", serverAddress, getResources().getString(string.api_get_lecture_speakers));

        String.format("%s%s", serverAddress, getResources().getString(string.api_get_speakers));
        String.format("%s%s", serverAddress, getResources().getString(string.api_get_speaker));
        String.format("%s%s", serverAddress, getResources().getString(string.api_get_speaker_lectures));
    }
	
	private class TransactionJob implements Runnable {

		private static final String TAG = "Service";

		@Override
		public void run() {
			try {
	            HttpGet httpGet = new HttpGet(GET_EVENTS);
	    		String responseBody = null;
	    		
	    		try {
                    responseBody = mHttpClient.execute(httpGet, mResponseHandler);
	    		} catch(Exception ex) {
	    		    ex.printStackTrace();
	    		}

                List<Event> events = null;
	    		if(responseBody != null) {
                    events = JacksonUtils.sReadValue(responseBody, new TypeReference<List<Event>>() {}, false);
                    if (events != null) {
                        
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
