package com.example.jinglemusic;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements OnClickListener {
    EditText inpuText;
    ProgressDialog progressDialog;
    MyAdapter adapter = null;
    ArrayList<Music> songList;
    Button searchButton, leftButton, rightButton;
    private int resourceFlag;
    PopupWindow popupWindow;


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

                    adapter = new MyAdapter(MainActivity.this,
                            R.layout.my_list_item, songList);
                    ListView listView = (ListView) findViewById(R.id.list_view);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1,
                                                int arg2, long arg3) {
                            // TODO Auto-generated method stub
                            final int position = arg2;
                            String songUrl = songList.get(position).songLink;
                            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri
                                    .parse(songUrl));

                       /* intent.setDataAndType(Uri
                                .parse(songUrl),"audio*//*");
                        intent.setAction(Intent.ACTION_VIEW);*/
                       /* startActivity(intent);
                        Toast.makeText(MainActivity.this, "前往浏览器进行下载",
                                Toast.LENGTH_SHORT).show();*/
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
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
                                    Intent intent_inner = new Intent(MainActivity.this, PlayActivity.class);
                                    intent_inner.putExtra("musicList", songList);
                                    intent_inner.putExtra("position", position);
                                    startActivity(intent_inner);
                                }
                            });
                            alertDialog.show();


                        }
                    });
                    break;
                case 1:
                    Toast.makeText(MainActivity.this, "解析Json错误",
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
        setContentView(R.layout.activity_main);
        searchButton = (Button) findViewById(R.id.search);
        leftButton = (Button) findViewById(R.id.title_left);
        rightButton = (Button) findViewById(R.id.title_right);
        inpuText = (EditText) findViewById(R.id.input);
        if (savedInstanceState != null) {
            inpuText.setText(savedInstanceState.getString("musicName"));
        }

        searchButton.setOnClickListener(this);
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);


        progressDialog = new ProgressDialog(MainActivity.this);
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
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
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
                ActivityCollector.finishAllActivity();
                break;
            case R.id.title_right:
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_list_view, null);
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
                ToastUtil.showToast(this,"百度无损");
                break;
            case R.id.pop_wangyi:
                resourceFlag = 1;
                popupWindow.dismiss();
                ToastUtil.showToast(this,"网易云");
                break;
            case R.id.pop_qq:
                resourceFlag = 2;
                popupWindow.dismiss();
                ToastUtil.showToast(this,"QQ音乐");
                break;
            case R.id.pop_baidump3:
                resourceFlag = 3;
                popupWindow.dismiss();
                ToastUtil.showToast(this,"百度音乐");
                break;
            case R.id.pop_xiami:
                resourceFlag = 4;
                popupWindow.dismiss();
                ToastUtil.showToast(this,"虾米音乐");
                break;
            case R.id.pop_kugou:
                resourceFlag = 5;
                popupWindow.dismiss();
                ToastUtil.showToast(this,"酷狗音乐");
                break;
            case R.id.pop_kuwo:
                resourceFlag = 6;
                popupWindow.dismiss();
                ToastUtil.showToast(this,"酷我音乐");
                break;
            case R.id.pop_migu:
                resourceFlag = 7;
                popupWindow.dismiss();
                ToastUtil.showToast(this,"咪咕音乐");
                break;
            case R.id.pop_echo:
                resourceFlag = 8;
                popupWindow.dismiss();
                ToastUtil.showToast(this,"echo回声");
                break;
            case R.id.pop_yiting:
                resourceFlag = 9;
                popupWindow.dismiss();
                ToastUtil.showToast(this,"一听音乐");
                break;
            case R.id.search:
                String songName = inpuText.getText().toString().trim();
                if (!TextUtils.isEmpty(songName.trim())) {
                    progressDialog.show();
                    switch (resourceFlag) {
                        case 0:
                            new Thread(new BaiduMusicRunnable(mHandler,songName)).start();
                            break;
                        case 1:
                            new Thread(new WangyiMusicRunnable(mHandler, songName)).start();
                            break;
                        case 2:
                            new Thread(new QQMusicRunnable(mHandler, songName)).start();
                            break;
                        case 3:
                            new Thread(new BaiduMp3Runnable(mHandler, songName)).start();
                            break;
                        case 4:
                            new Thread(new XiamiRunnable(mHandler, songName)).start();
                            break;
                        case 5:
                            new Thread(new KugouRunnable(mHandler, songName)).start();
                            break;
                        case 6:
                            new Thread(new KuwoRunnable(mHandler, songName)).start();
                            break;
                        case 7:
                            new Thread(new MiguRunnable(mHandler, songName)).start();
                            break;
                        case 8:
                            new Thread(new EchoRunnable(mHandler, songName)).start();
                            break;
                        case 9:
                            new Thread(new YitingRunnable(mHandler, songName)).start();
                            break;
                    }

                } else {
                    ToastUtil.showToast(MainActivity.this, "请输入歌曲名称等关键字");
                }

        }
    }

}



