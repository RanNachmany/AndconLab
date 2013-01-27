package com.gdg.andconlab;

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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class ServerCommunicationManager {
	
	public static final String TAG = "TWITTER_SEARCH_EXAMPLE_SERVER_COMMUNICATION";
	public static final String BASE_URL = "http://search.twitter.com/search.json?q=@";
	public static final String RPP = "&rpp=100";
	public static final String PAGE = "&page=";
	public static final String RESULTS_ARE_IN = "RESULTS_ARE_IN";
	
	public static final String JSON_OBJECT_RESULTS = "results";
	public static final String JSON_OBJECT_ITEM_USERNAME = "from_user";
	public static final String JSON_OBJECT_ITEM_MESSAGE = "text";
	public static final String JSON_OBJECT_ITEM_IMAGE = "profile_image_url";
	
	private static ServerCommunicationManager mThis = null;
	
	private HttpClient mHttpClient;        
	private ResponseHandler<String> mResponseHandler;

	private Context mContext;
	
	private ArrayList<Item> mItems;
	
	public ServerCommunicationManager(Context context) {
		mHttpClient = new  DefaultHttpClient();
		mResponseHandler = new BasicResponseHandler();
		
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
	    			responseBody = mHttpClient.execute(httpGet, mResponseHandler);
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

	    			  for(Object t : arr) {
	    			    Item Item = new Item(
	    			      ((JSONObject)t).get(JSON_OBJECT_ITEM_USERNAME).toString(),
	    			      ((JSONObject)t).get(JSON_OBJECT_ITEM_MESSAGE).toString(),
	    			      ((JSONObject)t).get(JSON_OBJECT_ITEM_IMAGE).toString()
	    			    );
	    			    Items.add(Item);
	    			  }
	    		}
	            return Items;
	        } catch (Exception e) {
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

        @Override
        protected Bitmap doInBackground(ImageView... params) {
            iv = params[0];
            return getBitmapFromUrl((String) iv.getTag()); 
        }

        @Override
        protected void onPostExecute( Bitmap d ) {
            iv.setImageBitmap(d);
        }
        
        public Bitmap getBitmapFromUrl(String bitmapUrl) {
        	  try {
        	    URL url = new URL(bitmapUrl);
        	    return BitmapFactory.decodeStream(url.openConnection().getInputStream()); 
        	  }
        	  catch(Exception ex) {return null;}
        	}

	}
}
