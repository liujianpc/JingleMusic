package com.example.jinglemusic.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.jinglemusic.model.LyricObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricView extends View{

    private static TreeMap<Integer, LyricObject> lrc_map;
    private float mX;       //屏幕X轴的中点，此值固定，保持歌词在X中间显示
    private float offsetY;      //歌词在Y轴上的偏移量，此值会根据歌词的滚动变小
    private static boolean blLrc=false;
    private float touchY;   //当触摸歌词View时，保存为当前触点的Y轴坐标
    private float touchX;
    private boolean blScrollView=false;
    private int lrcIndex=0; //保存歌词TreeMap的下标
    private  int SIZEWORD=0;//显示歌词文字的大小值
    private  int INTERVAL=45;//歌词每行的间隔
    Paint paint=new Paint();//画笔，用于画不是高亮的歌词
    Paint paintHL=new Paint();  //画笔，用于画高亮的歌词，即当前唱到这句歌词

    public LyricView(Context context){
        super(context);
        init();
    }

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /* (non-Javadoc)
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if(blLrc){
            paintHL.setTextSize(SIZEWORD);
            paint.setTextSize(SIZEWORD);
            LyricObject temp=lrc_map.get(lrcIndex);
            canvas.drawText(temp.lrc, mX, offsetY+(SIZEWORD+INTERVAL)*lrcIndex, paintHL);
            // 画当前歌词之前的歌词
            for(int i=lrcIndex-1;i>=0;i--){
                temp=lrc_map.get(i);
                if(offsetY+(SIZEWORD+INTERVAL)*i<0){
                    break;
                }
                canvas.drawText(temp.lrc, mX, offsetY+(SIZEWORD+INTERVAL)*i, paint);
            }
            // 画当前歌词之后的歌词
            for(int i=lrcIndex+1;i<lrc_map.size();i++){
                temp=lrc_map.get(i);
                if(offsetY+(SIZEWORD+INTERVAL)*i>600){
                    break;
                }
                canvas.drawText(temp.lrc, mX, offsetY+(SIZEWORD+INTERVAL)*i, paint);
            }
        }
        else{
            paint.setTextSize(25);
            canvas.drawText("找不到歌词", mX, 310, paint);
        }
        super.onDraw(canvas);
    }

    /* (non-Javadoc)
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        System.out.println("bllll==="+blScrollView);
        float tt=event.getY();
        if(!blLrc){
            //return super.onTouchEvent(event);

            return super.onTouchEvent(event);
        }
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchX=event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                touchY=tt-touchY;
                offsetY=offsetY+touchY;
                break;
            case MotionEvent.ACTION_UP:
                blScrollView=false;
                break;
        }
        touchY=tt;
        return true;
    }

    public void init(){
        lrc_map = new TreeMap<Integer, LyricObject>();
        offsetY=320;

        paint=new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setAlpha(180);


        paintHL=new Paint();
        paintHL.setTextAlign(Paint.Align.CENTER);

        paintHL.setColor(Color.RED);
        paintHL.setAntiAlias(true);
        paintHL.setAlpha(255);
    }

    /**
     * 根据歌词里面最长的那句来确定歌词字体的大小
     */

    public void SetTextSize(){
        if(!blLrc){
            return;
        }
        int max=lrc_map.get(0).lrc.length();
        for(int i=1;i<lrc_map.size();i++){
            LyricObject lrcStrLength=lrc_map.get(i);
            if(max<lrcStrLength.lrc.length()){
                max=lrcStrLength.lrc.length();
            }
        }
        SIZEWORD=320/max;

    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mX = w * 0.5f;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     *  歌词滚动的速度
     *
     * @return 返回歌词滚动的速度
     */
    public Float SpeedLrc(){
        float speed=0;
        if(offsetY+(SIZEWORD+INTERVAL)*lrcIndex>220){
            speed=((offsetY+(SIZEWORD+INTERVAL)*lrcIndex-220)/20);

        } else if(offsetY+(SIZEWORD+INTERVAL)*lrcIndex < 120){
            Log.i("speed", "speed is too fast!!!");
            speed = 0;
        }
//      if(speed<0.2){
//          speed=0.2f;
//      }
        return speed;
    }

    /**
     * 按当前的歌曲的播放时间，从歌词里面获得那一句
     * @param time 当前歌曲的播放时间
     * @return 返回当前歌词的索引值
     */
    public int SelectIndex(int time){
        if(!blLrc){
            return 0;
        }
        int index=0;
        for(int i=0;i<lrc_map.size();i++){
            LyricObject temp=lrc_map.get(i);
            if(temp.begintime<time){
                ++index;
            }
        }
        lrcIndex=index-1;
        if(lrcIndex<0){
            lrcIndex=0;
        }
        return lrcIndex;

    }

    /**
     * 读取歌词文件
     * @param songLrc 歌词的路径
     *
     */
    public static void read(final String songLrc) {
        final TreeMap<Integer, LyricObject> lrc_read =new TreeMap<Integer, LyricObject>();
        String data = "";
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String data = "";
                    URL url = new URL(songLrc);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.setUseCaches(false);
                    connection.setDoOutput(false);
                    connection.connect();
                    InputStream is = connection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));

                    //System.out.println("bllrc==="+blLrc);
                    int i = 0;
                    Pattern pattern = Pattern.compile("\\d{2}");
                    while ((data = br.readLine()) != null) {
                        // System.out.println("++++++++++++>>"+data);
                        data = data.replace("[","");//将前面的替换成后面的
                        data = data.replace("]","@");
                        String splitdata[] =data.split("@");//分隔
                        if(data.endsWith("@")){
                            for(int k=0;k<splitdata.length;k++){
                                String str=splitdata[k];

                                str = str.replace(":",".");
                                str = str.replace(".","@");
                                String timedata[] =str.split("@");
                                Matcher matcher = pattern.matcher(timedata[0]);
                                if(timedata.length==3 && matcher.matches()){
                                    int m = Integer.parseInt(timedata[0]);  //分
                                    int s = Integer.parseInt(timedata[1]);  //秒
                                    int ms = Integer.parseInt(timedata[2]); //毫秒
                                    int currTime = (m*60+s)*1000+ms*10;
                                    LyricObject item1= new LyricObject();
                                    item1.begintime = currTime;
                                    item1.lrc       = "";
                                    lrc_read.put(currTime,item1);
                                }
                            }

                        }
                        else{
                            String lrcContenet = splitdata[splitdata.length-1];

                            for (int j=0;j<splitdata.length-1;j++)
                            {
                                String tmpstr = splitdata[j];

                                tmpstr = tmpstr.replace(":",".");
                                tmpstr = tmpstr.replace(".","@");
                                String timedata[] =tmpstr.split("@");
                                Matcher matcher = pattern.matcher(timedata[0]);
                                if(timedata.length==3 && matcher.matches()){
                                    int m = Integer.parseInt(timedata[0]);  //分
                                    int s = Integer.parseInt(timedata[1]);  //秒
                                    int ms = Integer.parseInt(timedata[2]); //毫秒
                                    int currTime = (m*60+s)*1000+ms*10;
                                    LyricObject item1= new LyricObject();
                                    item1.begintime = currTime;
                                    item1.lrc       = lrcContenet;
                                    lrc_read.put(currTime,item1);// 将currTime当标签  item1当数据 插入TreeMap里
                                    i++;
                                }
                            }
                        }

                    }
                    is.close();
                    br.close();
                }
                catch (FileNotFoundException e) {
                }
                catch (IOException e) {
                }
            }
        }).start();

        /*
         * 遍历hashmap 计算每句歌词所需要的时间
        */
        lrc_map.clear();
        data ="";
        Iterator<Integer> iterator = lrc_read.keySet().iterator();
        LyricObject oldval  = null;
        int i =0;
        while(iterator.hasNext()) {
            Object ob =iterator.next();

            LyricObject val = (LyricObject)lrc_read.get(ob);

            if (oldval==null)
                oldval = val;
            else
            {
                LyricObject item1= new LyricObject();
                item1  = oldval;
                item1.timeline = val.begintime-oldval.begintime;
                lrc_map.put(new Integer(i), item1);
                i++;
                oldval = val;
            }
            if (!iterator.hasNext()) {
                lrc_map.put(new Integer(i), val);
            }

        }

    }

    /**
     * @return the blLrc
     */
    public static boolean isBlLrc() {
        return blLrc;
    }

    /**
     * @return the offsetY
     */
    public float getOffsetY() {
        return offsetY;
    }

    /**
     * @param offsetY the offsetY to set
     */
    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    /**
     * @return 返回歌词文字的大小
     */
    public int getSIZEWORD() {
        return SIZEWORD;
    }

    /**
     * 设置歌词文字的大小
     * @param sIZEWORD the sIZEWORD to set
     */
    public void setSIZEWORD(int sIZEWORD) {
        SIZEWORD = sIZEWORD;
    }
}