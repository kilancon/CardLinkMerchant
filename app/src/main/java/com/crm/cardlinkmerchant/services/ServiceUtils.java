package com.crm.cardlinkmerchant.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.crm.cardlinkmerchant.activity.LoginActivity;
import com.crm.cardlinkmerchant.activity.SwitchBusinessActivity;
import com.crm.cardlinkmerchant.services.ResultCode;
import com.crm.cardlinkmerchant.storage.Storage;
import com.crm.cardlinkmerchant.utils.AppUtils;
import com.crm.cardlinkmerchant.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;


public class ServiceUtils {
    public static final String TAG = "CL_HTTP_LOG";
    public static JSONObject sendPostRequestWithApiKey(String urlStr, JSONObject requestBody, Context context) throws Exception{
        return sendPostRequest(urlStr, requestBody, context, true, false, false);
    }
    public static JSONObject sendPostRequestWithToken(String urlStr, JSONObject requestBody, Context context) throws Exception{
        return sendPostRequest(urlStr, requestBody, context, false, true, false);
    }
    public static JSONObject sendPostRequest(String urlStr, JSONObject requestBody, Context context) throws Exception{
        return sendPostRequest(urlStr, requestBody, context, false, false, false);
    }

    public static JSONObject sendPostRequest(String urlStr, JSONObject requestBody, Context context, Boolean isApiKey, Boolean isToken, Boolean isRefresh) throws Exception{
        JSONObject jsonResult = null;

        String logId = UUID.randomUUID().toString();
        AppUtils.console(context, TAG, logId + "-urlStr: " + urlStr);
        AppUtils.console(context, TAG, logId + "-requestBody: " + requestBody.toString());

       // Log.d(TAG, "urlStr:" + urlStr);
        //Log.d(TAG, "body:"+requestBody.toString());
        URL url;
        HttpURLConnection conn = null;
        try {
            url = new URL(Constants.CrmConfig.SERVICE_URL + urlStr);
            Log.d(TAG, "URL: " + url.toString());
            conn = (HttpURLConnection) url.openConnection();
            setHttpPost(conn, isApiKey, isToken, isRefresh);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, StandardCharsets.UTF_8));
            writer.write(requestBody.toString());
            writer.flush();
            writer.close();
            os.close();

            conn.connect();


            AppUtils.console(context, TAG, logId + "-responseCode(): " + conn.getResponseCode());

            if(conn.getResponseCode() == 200){
                String response = streamToString(conn.getInputStream());
                AppUtils.console(context, TAG, logId + "-response: " + response);
                    try {
                        jsonResult = new JSONObject(response);
                        jsonResult.put("responseCode", conn.getResponseCode());
                    } catch (JSONException e) {
                        jsonResult = new JSONObject();
                        jsonResult.put("responseCode", conn.getResponseCode());
                        AppUtils.console(context, TAG, logId + "-JSONException: " + e);
                    }

            }
            else if(conn.getResponseCode() == 400){
                String response = streamToString(conn.getErrorStream());
                AppUtils.console(context, TAG, logId + "-response: " + response);
                try {
                    jsonResult = new JSONObject(response);
                    jsonResult.put("responseCode", conn.getResponseCode());
                } catch (JSONException e) {
                    jsonResult = new JSONObject();
                    jsonResult.put("responseCode", conn.getResponseCode());
                    AppUtils.console(context, TAG, logId + "-JSONException: " + e);
                }
            }
            else if(conn.getResponseCode() == 401){
                //call API refresh token
                JSONObject refreshResult = sendRefreshToken(context);
                if(refreshResult.getString("responseCode").equals("200")){
                    String access_token = refreshResult.getString("access_token");
                    String refresh_token = refreshResult.getString("refresh_token");
                    String exp = refreshResult.getString("exp");
                    Constants.CrmConfig.TOKEN = access_token;
                    Constants.CrmConfig.REFRESH_TOKEN = refresh_token;
                    Storage.setToken(context, access_token);
                    Storage.setRefreshToken(context, refresh_token);
                    Storage.setExpTime(context, exp);
                    //call lại API với token mới được set
                    conn = (HttpURLConnection) url.openConnection();
                    setHttpPost(conn, false, true, false);
                    os = conn.getOutputStream();
                    writer = new BufferedWriter(
                            new OutputStreamWriter(os, StandardCharsets.UTF_8));
                    writer.write(requestBody.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    conn.connect();
                    AppUtils.console(context, TAG, logId +  "- call back url: " + url + "-responseCode(): " + conn.getResponseCode());

                    if(conn.getResponseCode() == 200){
                        String response = streamToString(conn.getInputStream());
                        AppUtils.console(context, TAG, logId + "-response: " + response);
                        try {
                            jsonResult = new JSONObject(response);
                            jsonResult.put("responseCode", conn.getResponseCode());
                        } catch (JSONException e) {
                            jsonResult = new JSONObject();
                            jsonResult.put("responseCode", conn.getResponseCode());
                            AppUtils.console(context, TAG, logId + "-JSONException: " + e);
                        }
                    }
                    else{
                        validateForceLogout(conn.getResponseCode() + "", context, true);
                    }
                }
            }
            else{
                jsonResult = new JSONObject();
                jsonResult.put("responseCode", conn.getResponseCode());
            }
        } catch (ConnectException e) {
            AppUtils.console(context, TAG, logId + "-IOException: " + e);
            throw new Exception(ResultCode.NOT_CONNECT_API.toString());
        } catch (TimeoutException te) {
            AppUtils.console(context, TAG, logId + "-TimeoutException: " + te);
            throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
        } catch (IOException e) {
            String stackTrace = Log.getStackTraceString(e);
            AppUtils.console(context, TAG, logId + "-Exception: " + stackTrace);
        } catch (Exception e) {
            String stackTrace = Log.getStackTraceString(e);
            AppUtils.console(context, TAG, logId + "-Exception: " + stackTrace);
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return jsonResult;
    }



    private static void setHttpPost(HttpURLConnection conn, Boolean isApiKey, Boolean isToken, Boolean isRefresh) throws ProtocolException, TimeoutException {
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(5000);
        conn.setUseCaches(false);
        conn.setRequestProperty("Content-Type", "application/json");
        if(isApiKey){
            Log.d(TAG, "Using api_key: " + Constants.CrmConfig.API_KEY);
            conn.setRequestProperty("api_key", Constants.CrmConfig.API_KEY);
        } else if(isToken){
            if(isRefresh){
                Log.d(TAG, "Using REFRESH_TOKEN: " + Constants.CrmConfig.REFRESH_TOKEN);
                conn.setRequestProperty("Authorization",  "Bearer " + Constants.CrmConfig.REFRESH_TOKEN);
            }else {
                Log.d(TAG, "Using Token: " + Constants.CrmConfig.TOKEN);
                conn.setRequestProperty("Authorization",  "Bearer " + Constants.CrmConfig.TOKEN);
            }
        }

        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
    }

    public static JSONObject sendGetRequestWithApiKey(String urlStr, Context context) throws Exception{
        return sendGetRequest(context, urlStr, true, false);
    }
    public static JSONObject sendGetRequestWithToken(String urlStr, Context context) throws Exception{
        return sendGetRequest(context, urlStr, false, true);
    }
    public static JSONObject sendGetRequest(String urlStr, Context context) throws Exception{
        return sendGetRequest(context, urlStr, false, false);
    }

    public static JSONObject sendGetRequest(Context context, String urlStr, Boolean isApiKey, Boolean isToken) throws Exception{
        JSONObject jsonResult = new JSONObject();

        Log.d(TAG, urlStr);
        URL url;
        HttpURLConnection conn = null;

        String logId = UUID.randomUUID().toString();
        AppUtils.console(context, TAG, logId + "-urlStr: " + urlStr);


        try {
            url = new URL(Constants.CrmConfig.SERVICE_URL + urlStr);
            Log.d(TAG, "URL: " + url.toString());
            conn = (HttpURLConnection) url.openConnection();
            setHttpGet(conn, isApiKey, isToken, false);
            conn.connect();

            AppUtils.console(context, TAG, logId + "-getResponseCode(): " + conn.getResponseCode());
            if(conn.getResponseCode() == 200){
                String response = streamToString(conn.getInputStream());
                //Log.d(TAG, "Response: " + response);
                AppUtils.console(context, TAG, logId + "-Response: " + response);

                try {
                    jsonResult = new JSONObject(response);
                    jsonResult.put("responseCode", conn.getResponseCode());

                } catch (JSONException e) {
                    Log.d(TAG, "JSONException: " + e);
                    jsonResult = new JSONObject();
                    jsonResult.put("responseCode", conn.getResponseCode());
                    AppUtils.console(context, TAG, logId + "-JSONException: " + e);
                }
            }
            else if(conn.getResponseCode() == 401){
                //call API refresh token
                JSONObject refreshResult = sendRefreshToken(context);
                if(refreshResult.getString("responseCode").equals("200")){
                    String access_token = refreshResult.getString("access_token");
                    String refresh_token = refreshResult.getString("refresh_token");
                    String exp = refreshResult.getString("exp");
                    Constants.CrmConfig.TOKEN = access_token;
                    Constants.CrmConfig.REFRESH_TOKEN = refresh_token;
                    Storage.setToken(context, access_token);
                    Storage.setRefreshToken(context, refresh_token);
                    Storage.setExpTime(context, exp);
                    //call lại API với token mới được set
                    conn = (HttpURLConnection) url.openConnection();
                    setHttpGet(conn, false, true, false);
                    conn.connect();

                    AppUtils.console(context, TAG, logId +  "- call back url: " + url + "-responseCode(): " + conn.getResponseCode());

                    if(conn.getResponseCode() == 200){
                        String response = streamToString(conn.getInputStream());
                        AppUtils.console(context, TAG, logId + "-response: " + response);
                        try {
                            jsonResult = new JSONObject(response);
                            jsonResult.put("responseCode", conn.getResponseCode());
                        } catch (JSONException e) {
                            jsonResult = new JSONObject();
                            jsonResult.put("responseCode", conn.getResponseCode());
                            AppUtils.console(context, TAG, logId + "-JSONException: " + e);
                        }
                    }
                    else{
                        validateForceLogout(conn.getResponseCode() + "", context, true);
                    }
                }
            }
            else{
                jsonResult = new JSONObject();
                jsonResult.put("responseCode", conn.getResponseCode());
            }
        } catch (IOException e) {
           // Log.d(TAG, "IOException: " + e);
            AppUtils.console(context, TAG, logId + "-IOException: " + e);
            throw new Exception(ResultCode.NOT_CONNECT_API.toString());

        } catch (TimeoutException te) {
           // Log.d(TAG, "TimeoutException: " + te);
            AppUtils.console(context, TAG, logId + "-TimeoutException: " + te);
            throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
        } catch (Exception e){
            AppUtils.console(context, TAG, logId + "-Exception: " + e);
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return jsonResult;
    }


    private static void setHttpGet(HttpURLConnection conn, Boolean useApiKey, Boolean isToken, Boolean isRefresh) throws ProtocolException, TimeoutException {
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(25000);
        conn.setUseCaches(false);
        conn.setRequestProperty("Content-Type", "application/json");
        if(useApiKey){
            Log.d(TAG, "Using api_key: " + Constants.CrmConfig.API_KEY);
            conn.setRequestProperty("api_key", Constants.CrmConfig.API_KEY);
        } else if(isToken){
            if(isRefresh){
                Log.d(TAG, "Using REFRESH_TOKEN: " + Constants.CrmConfig.REFRESH_TOKEN);
                conn.setRequestProperty("Authorization",  "Bearer " + Constants.CrmConfig.REFRESH_TOKEN);
            }else{
                Log.d(TAG, "Using Token: " + Constants.CrmConfig.TOKEN);
                conn.setRequestProperty("Authorization",  "Bearer " + Constants.CrmConfig.TOKEN);
            }
        }
        conn.setRequestMethod("GET");
//        conn.setDoInput(true);
//        conn.setDoOutput(true);
    }

    public static String streamToString(InputStream inputStream) {
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder total = new StringBuilder();
        String line;
        try {
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total.toString();
    }

    public static JSONObject sendRefreshToken(Context context) throws Exception{
        String logId = UUID.randomUUID().toString();
        JSONObject jsonResult = null;
        JSONObject requestBody = new JSONObject();

        URL url;
        HttpURLConnection conn = null;
        try {
            url = new URL(Constants.CrmConfig.SERVICE_URL + Constants.CrmConfig.SERVICE_URL_REFRESH_TOKEN);
            Log.d(TAG, "URL: " + url.toString());
            conn = (HttpURLConnection) url.openConnection();
            setHttpPost(conn, false, true, true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, StandardCharsets.UTF_8));
            writer.write(requestBody.toString());
            writer.flush();
            writer.close();
            os.close();

            conn.connect();
            AppUtils.console(context, TAG, logId + "- refresh token result: " + conn.getResponseCode());

            if(conn.getResponseCode() == 200){
                String response = streamToString(conn.getInputStream());
                jsonResult = new JSONObject(response);
                jsonResult.put("responseCode", conn.getResponseCode());
            }
            else{
                validateForceLogout(conn.getResponseCode() + "", context, true);
            }
        } catch (Exception e) {

        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return jsonResult;
    }

    public static void validateForceLogout(String code, Context context, Boolean isLogOut){
        String logId = UUID.randomUUID().toString();
        try{
            if(isLogOut && code.equals("401")){
                Storage.setToken(context, "");
                Storage.setOrganisations(context, "");
                Storage.setBusiness(context, "");

                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            AppUtils.console(context, TAG, logId + "-Exception: " + ex);
        }
    }

    private static void setHttpPostCardlinkSms(HttpURLConnection conn) throws ProtocolException, TimeoutException {
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(5000);
        conn.setUseCaches(false);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("X-IBM-Client-Id", Constants.SdkConfig.SERVICE_SEND_SMS_CLIENT_ID);
        conn.setRequestProperty("X-IBM-Client-Secret", Constants.SdkConfig.SERVICE_SEND_SMS_SECRET);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
    }

    public static String sendCardlinkSmsPostRequest(Context context, String sendTo, String message) throws Exception {
        String rtn = "";
        URL url;
        HttpURLConnection conn = null;
        String logId = UUID.randomUUID().toString();
        AppUtils.console(context, TAG, logId + "-sendTo=" + sendTo + ", message=" + message);
        try {
            url = new URL(Constants.SdkConfig.SERVICE_SEND_SMS_URL);
            AppUtils.console(context, TAG, logId + "-sendCardlinkSmsPostRequest URL=" + url.toString());
            conn = (HttpURLConnection) url.openConnection();
            setHttpPostCardlinkSms(conn);
            String id = UUID.randomUUID().toString();
            String urlParameters  = "id=" + id +"&to=" + sendTo + "&text=" + message;
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("id", id);
            queryParams.put("to", sendTo);
            queryParams.put("text", message);
            byte[] postDataBytes = getParamsByte(queryParams);
            try {
                conn.getOutputStream().write(postDataBytes);
            } catch (Exception e){
                e.printStackTrace();
            }
            AppUtils.console(context, TAG, logId + "-sendCardlinkSmsPostRequest Response code: " + conn.getResponseCode());
            if(conn.getResponseCode() == 200){
                rtn = "OK";

            }
            else {
                String response = streamToString(conn.getErrorStream());

                AppUtils.console(context, TAG, logId + "-sendCardlinkSmsPostRequest Response: " + response);
                return response;
            }
        } catch (IOException e) {
            AppUtils.console(context, TAG, logId + "-sendCardlinkSmsPostRequest IOException: " + e);
            throw new Exception(ResultCode.NOT_CONNECT_API.toString());

        } catch (TimeoutException te) {
            AppUtils.console(context, TAG, logId + "-sendCardlinkSmsPostRequest TimeoutException: " + te);
            throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());

        }
        catch (Exception e) {
            AppUtils.console(context, TAG, logId + "-Exception: " + e);
        }finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return rtn;
    }

    private static byte[] getParamsByte(Map<String, Object> params) {
        byte[] result = null;
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) {
                postData.append('&');
            }
            postData.append(encodeParam(param.getKey()));
            postData.append('=');
            postData.append(encodeParam(String.valueOf(param.getValue())));
        }
        try {
            result = postData.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String encodeParam(String data) {
        String result = "";
        try {
            result = URLEncoder.encode(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

}
