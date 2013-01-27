package com.gdg.andconlab;

import java.util.ArrayList;
import com.actionbarsherlock.app.SherlockListActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ItemsListActivity extends SherlockListActivity{

public boolean isReceiverRegistered = false;
	
private ListView mList;
private ArrayList<Item> mItems;
	
	private ProgressDialog mProgressDialog;
	private BroadcastReceiver mUpdateReceiver;
	private AlertDialog mDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_items_list);
		if(hasConnectivity()){
			mProgressDialog = ProgressDialog.show(ItemsListActivity.this, getString(R.string.progress_dialog_starting_title), getString(R.string.progress_dialog_starting_message));
			mList = (ListView) findViewById(android.R.id.list);
			
			mList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					startActivity(new Intent(ItemsListActivity.this.getApplicationContext(), SingleItemPlayerScreen.class));
				}
			});
			
			final IntentFilter mFilter = new IntentFilter();
	        mFilter.addAction(ServerCommunicationManager.RESULTS_ARE_IN);

	        mUpdateReceiver = new BroadcastReceiver() {
				@Override
	            public void onReceive(Context context, Intent intent) {
	                if (intent.getAction().equalsIgnoreCase(ServerCommunicationManager.RESULTS_ARE_IN)) {
	                		mItems = ServerCommunicationManager.getInstance(ItemsListActivity.this.getApplicationContext()).getItems();
	                		if(mItems != null && mItems.size() != 0){
	                			mList.setAdapter(new ItemsListAdapter(getApplicationContext(), mItems));
	                    		mList.requestLayout();
	                		}else{
	                			if(mItems == null){
	                				mItems = new ArrayList<Item>();
	                			}
	                			mItems.add(new Item(getApplicationContext()));
	                			mList.setAdapter(new ItemsListAdapter(getApplicationContext(), mItems));
	                    		mList.requestLayout();
	                			Toast.makeText(getApplicationContext(), getString(R.string.toast_no_items), Toast.LENGTH_LONG).show();
	                		}
	                		mProgressDialog.dismiss();
	                }

	            }
	        };
	        registerReceiver(mUpdateReceiver, mFilter);
	        isReceiverRegistered = true;
	        ServerCommunicationManager.getInstance(ItemsListActivity.this.getApplicationContext()).startSearch("Android", 1);
		}else{
			launchDialog();
		}
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		if(isReceiverRegistered){
			unregisterReceiver(mUpdateReceiver);
			isReceiverRegistered = false;
		}
		if(mDialog != null){
			mDialog.dismiss();
		}
	}
	
	public boolean hasConnectivity(){
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}
	
	public void launchDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(ItemsListActivity.this);
		
		builder.setMessage(R.string.connectivity_dialog_message).setTitle(R.string.connectivity_dialog_title);
		
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               ItemsListActivity.this.finish();
		           }
		       
		});
		
		mDialog = builder.create();
		mDialog.show();
	}
}
