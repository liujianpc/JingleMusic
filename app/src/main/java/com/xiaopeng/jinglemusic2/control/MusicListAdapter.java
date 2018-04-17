package com.xiaopeng.jinglemusic2.control;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xiaopeng.jinglemusic2.Music;
import com.xiaopeng.jinglemusic2.R;

import java.util.List;

/**
 * Created by liujian on 2017/8/21.
 */

public class MusicListAdapter extends ArrayAdapter<Music> {

    private int resId;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public MusicListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);
        resId = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Music music = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(resId, null);
            TextView textView = (TextView) view.findViewById(R.id.music_list_name);
            viewHolder.musicName = textView;
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.musicName.setText(music.songTitle);
        return view;

    }

    class ViewHolder {
        TextView musicName;
    }
}
