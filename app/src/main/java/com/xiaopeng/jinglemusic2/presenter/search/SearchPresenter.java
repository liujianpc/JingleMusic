package com.xiaopeng.jinglemusic2.presenter.search;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.xiaopeng.jinglemusic2.Music;
import com.xiaopeng.jinglemusic2.model.search.ISearchModel;
import com.xiaopeng.jinglemusic2.model.search.SearchModel;
import com.xiaopeng.jinglemusic2.view.search.ISearchView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.List;

/**
 * Date: 2019/3/23
 * Created by LiuJian
 *
 * @author LiuJian
 */

public class SearchPresenter implements ISearchPresenter, SearchModel.LoadCallback {
    private ISearchView mSearchView;

    private ISearchModel mSearchModel;

    private Handler mHandler;

    public SearchPresenter(ISearchView mSearchView) {
        this.mSearchView = mSearchView;
        this.mSearchModel = new SearchModel(this);
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void loadMusicList(String songName, int resourceFlag) {

        mSearchModel.loadMusicList(songName, resourceFlag);

    }

    @Override
    public void onSuccess(final List<Music> musicList) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mSearchView.showResult(musicList);
            }
        });

    }

    @Override
    public void onFailed(Exception exception) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mSearchView.showToast();
            }
        });

    }

    @Override
    public void onProgress() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mSearchView.showProgress();
            }
        });

    }


    public String getJson(String address) {
        String response = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(address);
            HttpResponse httpResponse = client.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = httpResponse.getEntity();
                response = EntityUtils.toString(entity, "utf-8");
            }

        } catch (Exception e) {
            // TODO: handle exception
            Log.e("exception", "json解析错误");
        }
        return response;
    }
}
