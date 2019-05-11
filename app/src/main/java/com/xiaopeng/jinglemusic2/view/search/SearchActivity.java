package com.xiaopeng.jinglemusic2.view.search;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaopeng.jinglemusic2.Config;
import com.xiaopeng.jinglemusic2.Music;
import com.xiaopeng.jinglemusic2.R;
import com.xiaopeng.jinglemusic2.control.MusicAdapter;
import com.xiaopeng.jinglemusic2.presenter.search.ISearchPresenter;
import com.xiaopeng.jinglemusic2.presenter.search.SearchPresenter;
import com.xiaopeng.jinglemusic2.ui.AboutActivity;
import com.xiaopeng.jinglemusic2.ui.ActivityCollector;
import com.xiaopeng.jinglemusic2.utils.ToastUtil;
import com.xiaopeng.jinglemusic2.view.play.PlayActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索activity页面
 *
 * @author XP-PC-XXX
 */
public class SearchActivity extends AppCompatActivity implements View.OnClickListener, ISearchView {

    private static final String TAG = "SearchActivity";

    private ISearchPresenter mSearchPresenter;
    private RecyclerView mRecyclerView;
    private EditText mEditText;
    private ProgressDialog mProgressDialog;
    private FloatingActionButton mSearchButton;
    private Button mLeftButton, mRightButton;
    private int mResourceFlag;
    private PopupWindow mPopupWindow;

    private static final int SUCCESS_FLAG = 0;

    private static final int FAILED_FLAG = 1;

    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mToolbarlayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_scrolling);
        initView(savedInstanceState);


        setListener();

        mSearchPresenter = new SearchPresenter(this);


    }

    private void setListener() {
        mSearchButton.setOnClickListener(this);
        mLeftButton.setOnClickListener(this);
        mRightButton.setOnClickListener(this);
    }

    private void initView(Bundle savedInstanceState) {
        mSearchButton = (FloatingActionButton) findViewById(R.id.search);
        mLeftButton = (Button) findViewById(R.id.title_left);
        mRightButton = (Button) findViewById(R.id.title_right);
        mEditText = (EditText) findViewById(R.id.input);
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    searchMusic();
                }
                return false;
            }
        });
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mToolbarlayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        if (savedInstanceState != null) {
            mEditText.setText(savedInstanceState.getString("musicName"));
        }


        mProgressDialog = new ProgressDialog(SearchActivity.this);
        mProgressDialog.setTitle("waiting");
        mProgressDialog.setMessage("loading resource from Internet...");
        mProgressDialog.setCancelable(false);

        mRecyclerView = (RecyclerView) findViewById(R.id.list_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.about_menu) {
            Intent intent = new Intent(SearchActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.exit_menu) {
            ActivityCollector.finishAllActivity();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("musicName", mEditText.getText().toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left:
                finish();
                //ActivityCollector.finishAllActivity();
                break;
            case R.id.title_right:
                initPopupWindow();
                break;
            case R.id.pop_baidu:
                onSourceSelected(Config.BAIDU_FLAC_FLAG, getString(R.string.baidu_flac));
                break;
            case R.id.pop_wangyi:
                onSourceSelected(Config.NETEASE_FLAG, getString(R.string.netease));
                break;
            case R.id.pop_qq:
                onSourceSelected(Config.QQ_FLAG, getString(R.string.qq));
                break;
            case R.id.pop_baidump3:
                onSourceSelected(Config.BAIDU_MP3_FLAG, getString(R.string.baidu));
                break;
            case R.id.pop_xiami:
                onSourceSelected(Config.XIAMI_FLAG, getString(R.string.xiami));
                break;
            case R.id.pop_kugou:
                onSourceSelected(Config.KUGOU_FLAG, getString(R.string.kugou));
                break;
            case R.id.pop_kuwo:
                onSourceSelected(Config.KUWO_FLAG, getString(R.string.kuwo));
                break;
            case R.id.pop_migu:
                onSourceSelected(Config.MIGU_FLAG, getString(R.string.migu));
                break;
            case R.id.pop_echo:
                onSourceSelected(Config.ECHO_FLAG, getString(R.string.echo));
                break;
            case R.id.pop_yiting:
                onSourceSelected(Config.YITING_FLAG, getString(R.string.yiting));
                break;
            case R.id.search:
                searchMusic();

                break;
            default:
                break;
        }
    }

    /**
     * 搜索音乐
     */
    private void searchMusic() {
        String songName = mEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(songName.trim())) {
            showProgress();
            mSearchPresenter.loadMusicList(songName, mResourceFlag);

        } else {
            ToastUtil.showToast(SearchActivity.this, getString(R.string.hint));
        }
    }

    /**
     * 选中歌曲来源
     *
     * @param flag
     * @param itemMSg
     */
    private void onSourceSelected(int flag, String itemMSg) {
        mResourceFlag = flag;
        int colorId;
        String source = null;
        switch (flag) {
            case Config.BAIDU_FLAC_FLAG:
                colorId = R.color.baidu_flac;
                source = getString(R.string.baidu_flac);
                break;
            case Config.NETEASE_FLAG:
                colorId = R.color.netease;
                source = getString(R.string.netease);
                break;
            case Config.BAIDU_MP3_FLAG:
                colorId = R.color.baidu;
                source = getString(R.string.baidu);
                break;
            case Config.QQ_FLAG:
                colorId = R.color.qq;
                source = getString(R.string.qq);
                break;
            case Config.XIAMI_FLAG:
                colorId = R.color.xiami;
                source = getString(R.string.xiami);
                break;
            case Config.KUGOU_FLAG:
                colorId = R.color.kugou;
                source = getString(R.string.kugou);
                break;
            case Config.KUWO_FLAG:
                colorId = R.color.kuwo;
                source = getString(R.string.kuwo);
                break;
            case Config.MIGU_FLAG:
                colorId = R.color.migu;
                source = getString(R.string.migu);
                break;
            case Config.ECHO_FLAG:
                colorId = R.color.echo;
                source = getString(R.string.echo);
                break;
            case Config.YITING_FLAG:
                colorId = R.color.yiting;
                source = getString(R.string.yiting);
                break;

            default:
                colorId = R.color.mycolor2;
                source = getString(R.string.right);

        }
        // mAppBarLayout.setBackgroundColor(getColor(colorId));
        //mCustomTitle.setBackgroundColor(getColor(colorId));
        mRightButton.setText(source);
        mToolbarlayout.setBackgroundColor(getColor(colorId));
        mPopupWindow.dismiss();
        ToastUtil.showToast(this, itemMSg);
    }

    /**
     * 初始化彈窗
     */
    private void initPopupWindow() {
        View view = LayoutInflater.from(SearchActivity.this).inflate(R.layout.layout_list_view, null);
        TextView baidu = (TextView) view.findViewById(R.id.pop_baidu);
        TextView wangyi = (TextView) view.findViewById(R.id.pop_wangyi);
        TextView qq = (TextView) view.findViewById(R.id.pop_qq);
        TextView baiduMp3 = (TextView) view.findViewById(R.id.pop_baidump3);
        TextView xiaMi = (TextView) view.findViewById(R.id.pop_xiami);
        TextView Kugou = (TextView) view.findViewById(R.id.pop_kugou);
        TextView Kuwo = (TextView) view.findViewById(R.id.pop_kuwo);
        TextView Migu = (TextView) view.findViewById(R.id.pop_migu);
        TextView Echo = (TextView) view.findViewById(R.id.pop_echo);
        TextView Yiting = (TextView) view.findViewById(R.id.pop_yiting);
        baidu.setOnClickListener(this);
        wangyi.setOnClickListener(this);
        qq.setOnClickListener(this);
        baiduMp3.setOnClickListener(this);
        xiaMi.setOnClickListener(this);
        Kugou.setOnClickListener(this);
        Kuwo.setOnClickListener(this);
        Migu.setOnClickListener(this);
        Echo.setOnClickListener(this);
        Yiting.setOnClickListener(this);
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        ColorDrawable colorDrawable = new ColorDrawable();
        mPopupWindow.setBackgroundDrawable(colorDrawable);
        mPopupWindow.showAsDropDown(mRightButton, 0, 0);
        mPopupWindow.setOutsideTouchable(true);
    }

    @Override
    public void showToast(Exception e) {

        mProgressDialog.dismiss();
        Toast.makeText(SearchActivity.this, e.getMessage(),
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showResult(final List<Music> musics) {

        mProgressDialog.dismiss();
        MusicAdapter adapter = new MusicAdapter(musics, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        adapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                // TODO Auto-generated method stub
                final int position = mRecyclerView.getChildAdapterPosition(view);
                String songUrl = musics.get(position).songLink;
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri
                        .parse(songUrl));

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SearchActivity.this);
                alertDialog.setCancelable(true);
                alertDialog.setMessage("前往听歌，还是下载歌曲？");
                alertDialog.setNegativeButton("下载歌曲", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(intent);
                    }
                });
                alertDialog.setPositiveButton("听歌", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intentInner = new Intent(SearchActivity.this, PlayActivity.class);
                        intentInner.putParcelableArrayListExtra("musicList", (ArrayList<Music>) musics);
                        intentInner.putExtra("position", position);
                        startActivity(intentInner);
                    }
                });
                alertDialog.show();

            }
        });
        mRecyclerView.setAdapter(adapter);

    }

    @Override
    public void showProgress() {
        mProgressDialog.show();
    }

}
