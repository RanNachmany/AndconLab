package com.gdg.andconlab;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockListActivity;
import com.gdg.andconlab.models.Event;

import java.util.ArrayList;
import java.util.List;

public class ItemsListActivity extends SherlockListActivity {

    public boolean isReceiverRegistered = false;

    private ListView mList;
    private List<Event> mEvents;

    private ProgressDialog mProgressDialog;
    private BroadcastReceiver mUpdateReceiver;
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);
        if (hasConnectivity()) {
            mProgressDialog = ProgressDialog.show(ItemsListActivity.this, getString(R.string.progress_dialog_starting_title), getString(R.string.progress_dialog_starting_message));
            mList = (ListView) findViewById(android.R.id.list);

            mList.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    startActivity(new Intent(ItemsListActivity.this.getApplicationContext(), SingleItemDetailsScreen.class).putExtra("CURRENT_ITEM", mEvents.get(arg2)));
                }
            });

            final IntentFilter mFilter = new IntentFilter();
            mFilter.addAction(ServerCommunicationManager.RESULTS_ARE_IN);

            mUpdateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equalsIgnoreCase(ServerCommunicationManager.RESULTS_ARE_IN)) {
                        mEvents = ServerCommunicationManager.getInstance(ItemsListActivity.this.getApplicationContext()).getEvents();
                        if (mEvents != null && mEvents.size() != 0) {
                            mList.setAdapter(new ItemsListAdapter(mEvents));
                            mList.requestLayout();
                        } else {
                            if (mEvents == null) {
                                mEvents = new ArrayList<Event>();
                            }

                            mList.setAdapter(new ItemsListAdapter(mEvents));
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
        } else {
            launchDialog();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isReceiverRegistered) {
            unregisterReceiver(mUpdateReceiver);
            isReceiverRegistered = false;
        }
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public boolean hasConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public void launchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ItemsListActivity.this);

        builder.setMessage(R.string.connectivity_dialog_message).setTitle(R.string.connectivity_dialog_title);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ItemsListActivity.this.finish();
            }

        });

        mDialog = builder.create();
        mDialog.setCancelable(true);
        mDialog.show();
    }
}
