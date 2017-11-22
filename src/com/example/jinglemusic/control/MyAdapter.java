package com.example.jinglemusic.control;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.jinglemusic.model.Music;
import com.example.jinglemusic.R;

import java.util.List;

public class MyAdapter extends ArrayAdapter<Music> {
	private int resourceId;
	public MyAdapter(Context context, int resource, List<Music> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		resourceId = resource;
	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Music music = getItem(position);
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder.songTitle = (TextView) view
					.findViewById(R.id.song_title);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.songTitle.setText(music.songTitle);

		return view;

	}

	class ViewHolder {
		TextView songTitle;
	}
}
