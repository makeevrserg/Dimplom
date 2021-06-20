package org.tensorflow.lite.examples.posenet.main;

import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;

public class POST {

    public POST() {
        this.TOKEN = "";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
    private static String TAG="POST";
    public POST(String TOKEN){
        this.TOKEN = TOKEN;
        Log.d(TAG, "TOKEN: "+TOKEN);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public String urlAddress = "http://192.168.1.3:5000/";
    public URL url;
    public String TOKEN = "";


    public String GetJsonAnimsList() {
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Content-Type", "application/json; utf-8");
        properties.put("Accept", "application/json");
        properties.put("x-access-token", TOKEN);
        return PostGetRequest("GET", "anims", properties, null);
    }


    public String GetJsonAnim(Integer ID) {
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Content-Type", "application/json; utf-8");
        properties.put("Accept", "application/json");
        properties.put("x-access-token", TOKEN);
        return PostGetRequest("GET", "anim/get/" + ID.toString(), properties, null);
    }

    public String CreateAnim(String json) {
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Content-Type", "application/json; utf-8");
        properties.put("Accept", "application/json");
        properties.put("x-access-token", TOKEN);
        return PostGetRequest("POST", "anim/new", properties, json);
    }

    private String PostGetRequest(String requestMethod, String path, HashMap<String, String> properties, String json) {
        try {
            url = new URL(urlAddress + path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(requestMethod);
            con.setDoOutput(false);
            for (String key : properties.keySet())
                con.setRequestProperty(key, properties.get(key));
            if (json != null && !json.isEmpty())
                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = json.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

            InputStream content = (InputStream) con.getInputStream();
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(content));
            String line;
            String mLine = "";
            while ((line = in.readLine()) != null)
                mLine += line;
            return mLine;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public String Register(String name, String password) {

        HashMap<String, String> properties = new HashMap<>();
        properties.put("Content-Type", "application/json; utf-8");
        properties.put("Accept", "application/json");
        String json = "{\"name\":\"" + name + "\",\"password\":\"" + password + "\"}";
        return PostGetRequest("POST", "register", properties, json);
    }

    private void SetToken(String TOKEN) {
        this.TOKEN = TOKEN;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String GetToken(String name, String password) {
        String encoding = Base64.getEncoder().encodeToString((name + ":" + password).getBytes());
        final HashMap<String, String> properties = new HashMap<>();
        properties.put("Authorization", "Basic " + encoding);
        properties.put("Accept", "*/*");



        String token = PostGetRequest("GET", "login", properties, null);
        try {
            JSONObject jsonObject = new JSONObject(token);
            if (jsonObject.has("token")) {
                SetToken(jsonObject.get("token").toString());
                return TOKEN;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return "";
    }

}
