package com.xiaopeng.jinglemusic2.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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

import com.xiaopeng.jinglemusic2.Music;
import com.xiaopeng.jinglemusic2.R;
import com.xiaopeng.jinglemusic2.control.MyAdapter;
import com.xiaopeng.jinglemusic2.thread.BaiduFlacRunnable;
import com.xiaopeng.jinglemusic2.thread.BaiduMp3Runnable;
import com.xiaopeng.jinglemusic2.thread.EchoRunnable;
import com.xiaopeng.jinglemusic2.thread.KugouRunnable;
import com.xiaopeng.jinglemusic2.thread.KuwoRunnable;
import com.xiaopeng.jinglemusic2.thread.MiguRunnable;
import com.xiaopeng.jinglemusic2.thread.QQMusicRunnable;
import com.xiaopeng.jinglemusic2.thread.WangyiMusicRunnable;
import com.xiaopeng.jinglemusic2.thread.XiamiRunnable;
import com.xiaopeng.jinglemusic2.thread.YitingRunnable;
import com.xiaopeng.jinglemusic2.utils.ToastUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScrollingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ScrollingActivity";

    RecyclerView recyclerView;
    EditText inpuText;
    ProgressDialog progressDialog;
    MyAdapter adapter = null;
    ArrayList<Music> songList;
    FloatingActionButton searchButton;
    Button leftButton, rightButton;
    private int resourceFlag;
    PopupWindow popupWindow;
    ExecutorService executorService = Executors.newScheduledThreadPool(5);




    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            progressDialog.dismiss();
            switch (msg.what) {
                case 0:
                    songList = (ArrayList<Music>) msg.obj;
                    // SongListWrapper.MusicList = songList;

                    adapter = new MyAdapter(songList, getApplicationContext());

                    LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
                    manager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(manager);
                    adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view) {
                            // TODO Auto-generated method stub
                            final int position = recyclerView.getChildAdapterPosition(view);
                            String songUrl = songList.get(position).songLink;
                            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri
                                    .parse(songUrl));

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ScrollingActivity.this);
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
                                    Intent intent_inner = new Intent(ScrollingActivity.this, PlayActivity.class);
                                    intent_inner.putExtra("musicList", songList);
                                    intent_inner.putExtra("position", position);
                                    startActivity(intent_inner);
                                }
                            });
                            alertDialog.show();

                        }
                    });
                    recyclerView.setAdapter(adapter);

                    break;
                case 1:
                    Toast.makeText(ScrollingActivity.this, "解析Json错误",
                            Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_scrolling);
        searchButton = (FloatingActionButton) findViewById(R.id.search);
        leftButton = (Button) findViewById(R.id.title_left);
        rightButton = (Button) findViewById(R.id.title_right);
        inpuText = (EditText) findViewById(R.id.input);
        if (savedInstanceState != null) {
            inpuText.setText(savedInstanceState.getString("musicName"));
        }

        recyclerView = (RecyclerView) findViewById(R.id.list_view);
        searchButton.setOnClickListener(this);
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);


        progressDialog = new ProgressDialog(ScrollingActivity.this);
        progressDialog.setTitle("waiting");
        progressDialog.setMessage("loading resource from Internet...");
        progressDialog.setCancelable(false);
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
            Intent intent = new Intent(ScrollingActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.exit_menu) {
            ActivityCollector.finishAllActivity();
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("musicName", inpuText.getText().toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left:
                finish();
                //ActivityCollector.finishAllActivity();
                break;
            case R.id.title_right:
                View view = LayoutInflater.from(ScrollingActivity.this).inflate(R.layout.layout_list_view, null);
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
                popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                ColorDrawable colorDrawable = new ColorDrawable();
                popupWindow.setBackgroundDrawable(colorDrawable);
                popupWindow.showAsDropDown(rightButton, 0, 0);
                popupWindow.setOutsideTouchable(true);
                break;
            case R.id.pop_baidu:
                resourceFlag = 0;
                popupWindow.dismiss();
                ToastUtil.showToast(this, "百度无损");
                break;
            case R.id.pop_wangyi:
                resourceFlag = 1;
                popupWindow.dismiss();
                ToastUtil.showToast(this, "网易云");
                break;
            case R.id.pop_qq:
                resourceFlag = 2;
                popupWindow.dismiss();
                ToastUtil.showToast(this, "QQ音乐");
                break;
            case R.id.pop_baidump3:
                resourceFlag = 3;
                popupWindow.dismiss();
                ToastUtil.showToast(this, "百度音乐");
                break;
            case R.id.pop_xiami:
                resourceFlag = 4;
                popupWindow.dismiss();
                ToastUtil.showToast(this, "虾米音乐");
                break;
            case R.id.pop_kugou:
                resourceFlag = 5;
                popupWindow.dismiss();
                ToastUtil.showToast(this, "酷狗音乐");
                break;
            case R.id.pop_kuwo:
                resourceFlag = 6;
                popupWindow.dismiss();
                ToastUtil.showToast(this, "酷我音乐");
                break;
            case R.id.pop_migu:
                resourceFlag = 7;
                popupWindow.dismiss();
                ToastUtil.showToast(this, "咪咕音乐");
                break;
            case R.id.pop_echo:
                resourceFlag = 8;
                popupWindow.dismiss();
                ToastUtil.showToast(this, "echo回声");
                break;
            case R.id.pop_yiting:
                resourceFlag = 9;
                popupWindow.dismiss();
                ToastUtil.showToast(this, "一听音乐");
                break;
            case R.id.search:
                String songName = inpuText.getText().toString().trim();
                if (!TextUtils.isEmpty(songName.trim())) {
                    progressDialog.show();
                    switch (resourceFlag) {
                        case 0:
                            executorService.execute(new BaiduFlacRunnable(mHandler, songName));
                            break;
                        case 1:
                            executorService.execute(new WangyiMusicRunnable(mHandler, songName));
                            break;
                        case 2:
                            executorService.execute(new QQMusicRunnable(mHandler, songName));
                            break;
                        case 3:
                            executorService.execute(new BaiduMp3Runnable(mHandler, songName));
                            break;
                        case 4:
                            executorService.execute(new XiamiRunnable(mHandler, songName));
                            break;
                        case 5:
                            executorService.execute(new KugouRunnable(mHandler, songName));
                            break;
                        case 6:
                            executorService.execute(new KuwoRunnable(mHandler, songName));
                            break;
                        case 7:
                            executorService.execute(new MiguRunnable(mHandler, songName));
                            break;
                        case 8:
                            executorService.execute(new EchoRunnable(mHandler, songName));
                            break;
                        case 9:
                            executorService.execute(new YitingRunnable(mHandler, songName));
                            break;
                        default:
                            executorService.execute(new BaiduFlacRunnable(mHandler, songName));
                            break;
                    }

                } else {
                    ToastUtil.showToast(ScrollingActivity.this, "请输入歌曲名称等关键字");
                }
            default:
                break;
        }
    }
}
