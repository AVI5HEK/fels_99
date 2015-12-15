package com.framgia.elsytem.mypackage;

import android.util.Log;

import com.framgia.elsytem.model.User;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by avishek on 12/11/15.
 */
public class UserFunctions {
    User user;
    static OkHttpClient okHttpClient;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public UserFunctions() {
        okHttpClient = new OkHttpClient();
    }

    /**
     * Function to Login
     **/
    public String signIn(String url, User user, int isRememberMeChecked) {
        String result = "";
        String json = "";
        try {
            /**
             * OkHttp
             */
            // 1. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", user.getEmail());
            jsonObject.put("password", user.getPassword());
            jsonObject.put("remember_me", isRememberMeChecked);
            // 2. build parentJsonObject and put the previous object into this one
            JSONObject parentJsonObject = new JSONObject();
            parentJsonObject.put("session", jsonObject);
            // 3. convert JSONObject to JSON to String
            json = parentJsonObject.toString();
            Log.e("chehara", json);
            RequestBody requestBody = RequestBody.create(JSON, json);
            Request request = new Request.Builder().url(url).post(requestBody).build();
            Response response = okHttpClient.newCall(request).execute();
            result = response.body().string();
            Log.e("JSON Object: ", json);
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        // 4. return result
        return result;
    }

    /**
     * Sign Up
     */
    public String signUp(String url, User user) {
        String result = "";
        String json = "";
        try {
            /**
             * OkHttp
             */
            // 1. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", user.getName());
            jsonObject.put("email", user.getEmail());
            jsonObject.put("password", user.getPassword());
            jsonObject.put("password_confirmation", user.getPassword_confirmation());
            // 2. build parentJsonObject and put the previous object into this one
            JSONObject parentJsonObject = new JSONObject();
            parentJsonObject.put("user", jsonObject);
            // 3. convert JSONObject to JSON to String
            json = parentJsonObject.toString();
            RequestBody requestBody = RequestBody.create(JSON, json);
            Request request = new Request.Builder().url(url).post(requestBody).build();
            Response response = okHttpClient.newCall(request).execute();
            String jsonData = response.body().string();
            // 4. get the message from response
            result = new JSONObject(jsonData).getString("message");
            Log.e("JSON Object: ", json);
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    /**
     * function to sign out
     */
    public String signOut(String url) {
        String result = "";
        String json = "";
        try {
            JSONObject jsonObject = new JSONObject();
            json = jsonObject.toString();
            RequestBody requestBody = RequestBody.create(JSON, json);
            Request request = new Request.Builder().url(url).delete(requestBody).build();
            Response response = okHttpClient.newCall(request).execute();
            String jsonData = response.body().string();
            result = new JSONObject(jsonData).getString("message");
            Log.e("JSON Object: ", json);
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    public String updateProfile(String url) {
        String result = "";
        try {
            String json;
            // 1. build jsonObject
            JSONObject parent = new JSONObject();
            JSONObject lesson = new JSONObject();
            lesson.put("id", 1);
            lesson.put("name", "#1");
            JSONObject word = new JSONObject();
            word.put("id", 1);
            word.put("content", "Framgia");
            JSONArray answer = new JSONArray();
            JSONObject answer_1 = new JSONObject();
            answer_1.put("id", 1);
            answer_1.put("content", "framui");
            answer_1.put("is_correct", false);
            JSONObject answer_2 = new JSONObject();
            answer_2.put("id", 2);
            answer_2.put("content", "framgia");
            answer_2.put("is_correct", true);
            JSONObject answer_3 = new JSONObject();
            answer_3.put("id", 3);
            answer_3.put("content", "frmgia");
            answer_3.put("is_correct", false);
            JSONObject answer_4 = new JSONObject();
            answer_4.put("id", 4);
            answer_4.put("content", "gtgia");
            answer_4.put("is_correct", false);
            answer.put(answer_1);
            answer.put(answer_2);
            answer.put(answer_3);
            answer.put(answer_4);
            word.put("answers", answer);
            lesson.put("words", word);
            parent.put("lesson", lesson);
            json = parent.toString();
            Log.e("JSON Object: ", json);
            RequestBody requestBody = RequestBody.create(JSON, json);
            Request request = new Request.Builder().url(url).post(requestBody).build();
            Response response = okHttpClient.newCall(request).execute();
            result = response.body().string();
        } catch (Exception e) {
        }
        return result;
    }
}
