package com.xiaopeng.jinglemusic2.utils;

/**
 * Created by liujian on 2017/8/13.
 */

public class JsonpUtil {

        public static String parseJSONP(String jsonp){

            int startIndex = jsonp.indexOf("(");
            int endIndex = jsonp.lastIndexOf(")");
            return jsonp.substring(startIndex+1, endIndex);
        }
}
