package com.framgia.elsytem.mypackage;

import android.content.SharedPreferences;

/**
 * Created by avishek on 12/22/15.
 */
public class Constants {
    /**
     * SessionManager's keys
     */
    //User id
    public static final String KEY_ID = "id";
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    // Avatar's image decodable string
    public static final String KEY_AVATAR = "avatar";
    // remember token
    public static final String KEY_AUTH_TOKEN = "authToken";
    // remember me
    public static final String KEY_REMEMBER_ME = "rememberMe";
    // UserFunctions TAG
    public static final String TAG_USER_FUNCTIONS = "UserFunctions";
    public static SharedPreferences PREF_STATE = null;
    public static int ANSWER_1=0;
    public static int ANSWER_2=1;
    public static int ANSWER_3=2;
    public static int ANSWER_4=3;
    public static int STATE=0;
    public static int CATEGORY_ID=1;
    // file name of the avatar in the phone storage
    public static final String KEY_AVATAR_FILE_NAME = "_avatar.jpg";
}
