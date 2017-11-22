package com.example.jinglemusic.control;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jinglemusic.model.DownLoadMusic;
import com.example.jinglemusic.R;

import java.util.List;

/**
 * Created by liujian on 2017/8/28.
 */

public class DownLoadMusicAdapter extends ArrayAdapter<DownLoadMusic> {
    private int resId;
    private List<DownLoadMusic> list;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public DownLoadMusicAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<DownLoadMusic> objects) {
        super(context, resource, objects);
        resId = resource;
        list = objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DownLoadMusic downLoadMusic = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(resId,null);
            viewHolder.musictitle = (TextView) view.findViewById(R.id.music_title);
            viewHolder.downLoadProgress = (TextView) view.findViewById(R.id.download_progress);
            view.setTag(viewHolder);

        }else {
            view =  convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.musictitle.setText(downLoadMusic.musicName);
        viewHolder.downLoadProgress.setText(downLoadMusic.downLoadProgress);
        return view;
    }
    class ViewHolder{
        public TextView musictitle;
        public TextView downLoadProgress;
    }

    public void updateView(int position , ListView listView, String progress){
        int firstVisibleIndex = listView.getFirstVisiblePosition();
        int lastVisibleIndex = listView.getLastVisiblePosition();
        if (position >= firstVisibleIndex && position <= lastVisibleIndex){
            View view = listView.getChildAt(position - firstVisibleIndex);
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            viewHolder.downLoadProgress.setText(progress);
            list.set(position, new DownLoadMusic(list.get(position).musicName,progress));
        }else {
            list.set(position, new DownLoadMusic(list.get(position).musicName,progress));
        }
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }
}
