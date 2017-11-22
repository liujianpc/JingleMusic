package com.example.jinglemusic.utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by liujian on 2017/8/12.
 */

public class HttpUtil {
    public static String httpUrlConnectionByGet(String urlString) {
        InputStream is = null;
        InputStreamReader ir = null;
        BufferedReader bfr = null;
        StringBuffer stringBuffer = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setDoOutput(false);
            connection.connect();
            is = connection.getInputStream();
            ir = new InputStreamReader(is, "utf-8");
            bfr = new BufferedReader(ir);
            stringBuffer = new StringBuffer();
            String line;
            while ((line = bfr.readLine()) != null) {
                stringBuffer.append(line);
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (ir != null) {
                try {
                    ir.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bfr != null) {
                try {
                    bfr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return new String(stringBuffer);

        }

    }

    public static String httpUrlConnectionByPost(String urlString, String params) {
        OutputStream os = null;
        OutputStreamWriter osr = null;
        BufferedReader bfr = null;
        StringBuffer stringBuffer = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.connect();
            os = connection.getOutputStream();
            osr = new OutputStreamWriter(os, "UTF-8");
            osr.write(params);
            osr.flush();
            bfr = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            stringBuffer = new StringBuffer();
            String line = null;
            while ((line = bfr.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (osr != null) {
                try {
                    osr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bfr != null) {
                try {
                    bfr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return new String(stringBuffer);
        }

    }

    public static String httpClientByGet(String urlString) {
        String result = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(urlString);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return result;
        }

    }

    public static String httpClientByPost(String urlString, Map<String, String> params, Map<String, String> headers) {
        StringBuffer stringBuffer = null;
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(urlString);
        if (headers != null) {
            Set keySet = headers.keySet();
            for (Object key : keySet
                    ) {
                httpPost.addHeader((String) key, headers.get(key));
            }
        }

        ArrayList<BasicNameValuePair> paramsPairs = new ArrayList<>();
        if (params != null) {
            Set keySet = params.keySet();
            for (Object key : keySet
                    ) {
                paramsPairs.add(new BasicNameValuePair((String) key, params.get(key)));
            }
        }

        InputStream is = null;
        BufferedReader bfr = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(paramsPairs, "UTF-8"));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                is = httpResponse.getEntity().getContent();
                bfr = new BufferedReader(new InputStreamReader(is, "utf-8"));
                String line = null;
                stringBuffer = new StringBuffer();
                while ((line = bfr.readLine()) != null) {
                    stringBuffer.append(line);
                }
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bfr != null) {
                try {
                    bfr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return new String(stringBuffer);
    }
}
