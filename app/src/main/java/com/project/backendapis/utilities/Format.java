package com.project.backendapis.utilities;

import android.util.Log;

import com.project.backendapis.data.Data;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Format {
    public static String formatString(String text) {

        StringBuilder json = new StringBuilder();
        String indentString = "";

        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            switch (letter) {
                case '{':
                case '[':
                    json.append("\n" + indentString + letter + "\n");
                    indentString = indentString + "\t";
                    json.append(indentString);
                    break;
                case '}':
                case ']':
                    indentString = indentString.replaceFirst("\t", "");
                    json.append("\n" + indentString + letter);
                    break;
                case ',':
                    json.append(letter + "\n" + indentString);
                    break;

                default:
                    json.append(letter);
                    break;
            }
        }
        Log.i("APIResponse", "formatString  : " + json.toString());

        return json.toString();
    }

    public static String getPostDataString(List<Data> params) throws UnsupportedEncodingException {
        String data = "";
        for (Data entry : params) {
            if (data.isEmpty()) {
                data = URLEncoder.encode(entry.getKey(), "UTF-8")
                        + "=" + URLEncoder.encode(entry.getValue(), "UTF-8");
            } else {
                data += "&" + URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                        + URLEncoder.encode(entry.getValue(), "UTF-8");
            }
        }
         return data;
    }

    public static ArrayList<Data> parseUrl(String url) {
        ArrayList<Data> params = new ArrayList<>();

        if (url.contains("?")) {
            if (url.split("\\?").length > 1) {
                String param = url.split("\\?")[1];
                String[] p = param.split("&");
                for (String s : p) {
                    if (s.split("=").length > 1) {

                        String key = s.split("=")[0];
                        String value = s.split("=")[1];
                        params.add(new Data(key, value));
                    }
                }
            }
        }

        return params;
    }
}
