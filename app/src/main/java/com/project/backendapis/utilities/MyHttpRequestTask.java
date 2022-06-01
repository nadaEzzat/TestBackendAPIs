package com.project.backendapis.utilities;

import android.os.AsyncTask;
import android.util.Log;

import com.project.backendapis.data.Data;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyHttpRequestTask extends AsyncTask<String, Integer, String> {

    public HTTPCallback delegate = null;
    public String requestURL = "";
    public String requestBody = "";
    public List<Data> headers;
    public int res_code = 0;
    public String response_message = "";
    public Map<String, List<String>> response_header = new HashMap<>();

    String requestMethod;

    public MyHttpRequestTask(String requestURL, String requestMethod, String requestBody, List<Data> headers, HTTPCallback asyncResponse) {
        this.headers = headers;
        this.requestURL = requestURL;
        this.requestMethod = requestMethod;
        this.requestBody = requestBody;
        this.delegate = asyncResponse;
    }

    @Override
    protected String doInBackground(String... params) {
        String response = "";

        try {

            URL url = new URL(requestURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(requestMethod);

            // adding the headers for request
            // httpURLConnection.setRequestProperty("Content-Type", "application/json");
      /*     httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
            for (Data data : headers) {
                httpURLConnection.setRequestProperty(data.getKey(), data.getValue());
            }
*/
            /*httpURLConnection.setUseCaches(true);
            httpURLConnection.setDefaultUseCaches(true);
            int maxStale = 60 * 60 * 24 * 28;
            httpURLConnection.addRequestProperty("Cache-Control", "max-stale=" + maxStale);
            httpURLConnection.addRequestProperty("Cache-Control", "no-cache");
            */





            try {
                //    httpURLConnection.setChunkedStreamingMode(0);

                if (requestMethod.equals("POST") && !requestBody.isEmpty()) {

                    OutputStream outputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                    outputStreamWriter.write(requestBody);
                    outputStreamWriter.flush();
                    outputStreamWriter.close();
                }


                Log.i("APIResponse", "MyHttpRequestTask doInBackground : " + httpURLConnection.getResponseCode());
                Log.i("APIResponse", "MyHttpRequestTask doInBackground : " + httpURLConnection.getResponseMessage());

                res_code = httpURLConnection.getResponseCode();
                response_message = httpURLConnection.getResponseMessage();
                response_header = httpURLConnection.getHeaderFields();

                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }

            } catch (Exception e) {
                delegate.requestError(e.getMessage());
                Log.i("APIResponse", "Exception : " + e.getMessage());
                e.printStackTrace();
            } finally {

                Log.i("APIResponse", "Response : " + response);
                httpURLConnection.disconnect();
            }


        } catch (Exception e) {
            Log.i("APIResponse", "Exception 2 : " + e.getMessage());
            delegate.requestError(e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void onPostExecute(String result) {

        delegate.requestFinish(res_code, response_message, response_header.toString(), result);

    }


}