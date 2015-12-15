package com.framgia.elsytem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.framgia.elsytem.model.User;
import com.framgia.elsytem.mypackage.AlertDialogManager;
import com.framgia.elsytem.mypackage.SessionManager;
import com.framgia.elsytem.mypackage.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    EditText email, password;
    TextView validation;
    Button buttonLogin;
    CheckBox checkBoxRememberMe;
    private String mEmail, mPassword;
    private int mRememberMe;
    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        session = new SessionManager(getApplicationContext());
        // check in session if user is logged in. If so, go to profile activity
        if (session.isLoggedIn()) {
            //go to ProfileActivity
            // going to UpdateProfileActivity for testing purpose
            Intent i = new Intent(getApplicationContext(), UpdateProfileActivity.class);
            startActivity(i);
            finish();
        } else {
            TextView register = (TextView) findViewById(R.id.text_create_account);
            email = (EditText) findViewById(R.id.edit_text_email);
            password = (EditText) findViewById(R.id.edit_text_password);
            validation = (TextView) findViewById(R.id.text_validation);
            buttonLogin = (Button) findViewById(R.id.button_login);
            checkBoxRememberMe = (CheckBox) findViewById(R.id.check_remember);
            alert = new AlertDialogManager();
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(i);
                }
            });
            buttonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEmail = email.getText().toString();
                    mPassword = password.getText().toString();
                    if (checkBoxRememberMe.isChecked()) mRememberMe = 1;
                    else mRememberMe = 0;
                    if (!isConnected())
                        alert.showAlertDialog(getApplicationContext(), getString(R.string
                                        .connection_error_title_activity_login),
                                getString(R.string.connection_error_message_activity_login), false);
                    if (!mEmail.equals("") && !mPassword.equals("")) {
                        new HttpAsyncTaskSignIn().execute("https://manh-nt.herokuapp.com/login.json");
                    } else if (mEmail.equals("")) {
                        Toast.makeText(getApplicationContext(), getString(R.string
                                        .empty_email_activity_login), Toast.LENGTH_SHORT).show();
                    } else if (mPassword.equals("")) {
                        Toast.makeText(getApplicationContext(), getString(R.string
                                .empty_password_activity_login), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string
                                        .empty_email_password_activity_login), Toast
                                .LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private class HttpAsyncTaskSignIn extends AsyncTask<String, Void, String> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(LoginActivity.this);
            mDialog.setTitle("Contacting Servers");
            mDialog.setMessage("Signing in ...");
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(true);
            mDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            user = new User();
            user.setEmail(mEmail);
            user.setPassword(mPassword);
            UserFunctions userFunction = new UserFunctions();
            return userFunction.signIn(urls[0], user, mRememberMe);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            mDialog.dismiss();
            String msg = "";
            try {
                msg = new JSONObject(result).getString("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                // showing the validation message got from json response
                if (msg.equals(getString(R.string.invalid_username_password_message))) {
                    validation.setVisibility(View.VISIBLE);
                    Log.e(TAG + " message: ", msg);
                } else {
                    //creating session
                    if (mRememberMe == 1) {
                        String name = new JSONObject(new JSONObject(result).getString("user")).getString
                                ("name");
                        String email = new JSONObject(new JSONObject(result).getString("user")).getString
                                ("email");
                        if (!name.isEmpty() && !email.isEmpty())
                            session.createLoginSession(name, email);
                        Log.e(TAG + " Name: ", name);
                        Log.e(TAG + " Email: ", email);
                    }
                    //now finish this activity and go to next activity
                    //for testing purpose, I'm going to UpdateProfileActivity
                    Intent i = new Intent(getApplicationContext(), UpdateProfileActivity.class);
                    startActivity(i);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e(TAG + " Message: ", result);
            if (result.isEmpty())
                Toast.makeText(getBaseContext(), "Keep calm and try again!", Toast.LENGTH_LONG)
                        .show();
            else try {
                Toast.makeText(getApplicationContext(), new JSONObject(result).getString
                        ("message"), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
