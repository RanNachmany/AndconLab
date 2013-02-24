package com.gdg.andconlab;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemsListAdapter extends BaseAdapter {

	private ArrayList<Item> mItems;
	private Context mContext;
	
	public ItemsListAdapter(Context context, List<Item> tweets) {
        mContext = context;
        mItems = (ArrayList<Item>) ((tweets != null) ? tweets : new ArrayList<Item>());
        notifyDataSetChanged();
    }
	
	@Override
	public int getCount() {
		try{
			return mItems.size();
		}catch(Exception e){
			
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		try{
			return mItems.get(position);
		}catch(Exception e){
			
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Item current = mItems.get(position);
		
		if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            
            holder = new ViewHolder();
            holder.mThumbnailHolder = (ImageView) convertView.findViewById(R.id.thumbnail);
            holder.mTitleHolder = (TextView) convertView.findViewById(R.id.title);
            holder.mDescriptionHolder = (TextView) convertView.findViewById(R.id.description);
            
            convertView.setTag(holder);
		}else {
            holder = (ViewHolder) convertView.getTag();
        }
		
		if(current.getLecturerProfileImageUrl().length() != 0){
			holder.mThumbnailHolder.setTag(current.getLecturerProfileImageUrl()+"$sep$"+current.getLectureYoutubeAssetId());
			ServerCommunicationManager.getInstance(mContext).getBitmap(holder.mThumbnailHolder);
		}else{
			holder.mThumbnailHolder.setImageResource(R.drawable.ic_launcher);
		}
		holder.mTitleHolder.setText(current.getLectureTitle());
		holder.mDescriptionHolder.setText(current.getLectureDescription());
		
		return convertView;
	}
	
	
	static class ViewHolder {
        ImageView mThumbnailHolder;
        TextView mTitleHolder;
        TextView mDescriptionHolder;
    }
	

}
