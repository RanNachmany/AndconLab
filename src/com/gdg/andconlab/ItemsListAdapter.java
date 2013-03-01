package com.gdg.andconlab;

import android.text.TextUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.gdg.andconlab.models.Event;
import com.gdg.andconlab.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ItemsListAdapter extends BaseAdapter {

	private List<Event> mEvents;

	public ItemsListAdapter(List<Event> events) {
        mEvents = (events != null) ? events : new ArrayList<Event>();
        notifyDataSetChanged();
    }
	
	@Override
	public int getCount() {
        return CollectionUtils.getSize(mEvents);
	}

	@Override
	public Object getItem(int position) {
        return CollectionUtils.getAt(mEvents, position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Event event = mEvents.get(position);
		
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

        if (TextUtils.isEmpty(event.getLogoUrl())) {
            holder.mThumbnailHolder.setImageResource(R.drawable.ic_launcher);
        } else {
            // TODO image manager requires a different implementation
//            holder.mThumbnailHolder.setTag(event.getLecturerProfileImageUrl()+"$sep$"+event.getLectureYoutubeAssetId());
//            ServerCommunicationManager.getInstance(mContext).getBitmap(holder.mThumbnailHolder);
        }

		holder.mTitleHolder.setText(event.getName());

        StringBuilder descriptionBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(event.getStartDate())) {
            // example of one possible way to parse the date field
            Time t = new Time();
            t.parse3339(event.getStartDate());
            descriptionBuilder.append(t.format("%Y-%m-%d %H:%M"));
            descriptionBuilder.append(": ");
        }

        descriptionBuilder.append(event.getDescription());
		holder.mDescriptionHolder.setText(descriptionBuilder.toString());

		return convertView;
	}
	
	
	static class ViewHolder {
        ImageView mThumbnailHolder;
        TextView mTitleHolder;
        TextView mDescriptionHolder;
    }
}