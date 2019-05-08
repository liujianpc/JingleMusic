package com.xiaopeng.jinglemusic2.view.search;

import com.xiaopeng.jinglemusic2.Music;

import java.util.List;

/**
 * Date: 2019/3/23
 * Created by LiuJian
 *
 * @author LiuJian
 */

public interface ISearchView {

    void showToast(Exception e);

    void showResult(List<Music> musics);


    void showProgress();

}
