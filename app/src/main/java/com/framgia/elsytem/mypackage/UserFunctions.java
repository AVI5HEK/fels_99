package com.framgia.elsytem.mypackage;

import android.util.Log;

import com.framgia.elsytem.model.User;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

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
    public String signIn(String url, User user, boolean isRememberMeChecked) {
        String result = "";
        try {
            /**
             * OkHttp
             */
            String json;
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
            RequestBody requestBody = RequestBody.create(JSON, json);
            Request request = new Request.Builder().url(url).post(requestBody).build();
            Response response = okHttpClient.newCall(request).execute();
            result = response.body().string();
            Log.e("JSON Object: ", json);
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        // 11. return result
        return result;
    }

    /**
     * Sign Up
     */
    public String signUp(String url, User user) {
        String result = "";
        try {
            /**
             * OkHttp
             */
            String json;
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
            /**
             * Normal http
             */
            /*// 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("name", user.getName());
            jsonObject.accumulate("email", user.getEmail());
            jsonObject.accumulate("password", user.getPassword());
            jsonObject.accumulate("password_confirmation", user.getPassword_confirmation());
            Log.e("Name: ", user.getName());
            Log.e("Email: ", user.getEmail());
            Log.e("Pass: ", user.getPassword());
            Log.e("Re Pass: ", user.getPassword_confirmation());
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            Log.e("JSON Object: ", json);
            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);
            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);
            // 6. set httpPost Entity
            httpPost.setEntity(se);
            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);
            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            // 10. convert inputstream to string
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else {
                result = "Did not work!";
            }*/
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        // 11. return result
        return result;
    }
}
