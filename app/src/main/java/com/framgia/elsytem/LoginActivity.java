package com.framgia.elsytem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.framgia.elsytem.jsonResponse.UserResponse;
import com.framgia.elsytem.model.User;
import com.framgia.elsytem.mypackage.AlertDialogManager;
import com.framgia.elsytem.mypackage.SessionManager;
import com.framgia.elsytem.mypackage.Url;
import com.framgia.elsytem.mypackage.UserFunctions;
import com.google.gson.Gson;

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
    AlertDialogManager alert;
    SessionManager session;
    User user;
    TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        session = new SessionManager(getApplicationContext());
        // check in session if user is logged in. If so, go to profile activity
        if (session.isLoggedInAndRemember()) {
            //go to ProfileActivity
            mStartProfileActivity();

        } else {
            initializeViews();
            session.deleteSessionData();
            alert = new AlertDialogManager();
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(i);

                }
            });
            password.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_DPAD_CENTER:
                            case KeyEvent.KEYCODE_ENTER:
                                mSignIn();
                                return true;
                            default:
                                break;
                        }
                    }
                    return false;
                }
            });
            buttonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSignIn();
                }
            });
        }
    }

    private void mStartProfileActivity() {
        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(i);
        finish();
    }

    private void mSignIn() {
        mEmail = email.getText().toString();
        mPassword = password.getText().toString();
        if (checkBoxRememberMe.isChecked()) mRememberMe = 1;
        else mRememberMe = 0;
        if (!isConnected())
            mShowDialog(LoginActivity.this, getString(R.string
                            .connection_error_title_activity_login),
                    getString(R.string.connection_error_message_activity_login),
                    false);
        if (!mEmail.isEmpty() && !mPassword.isEmpty()) {
            new HttpAsyncTaskSignIn().execute(Url.url_sign_in);
        } else if (mEmail.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string
                    .empty_email_activity_login), Toast
                    .LENGTH_SHORT).show();
        } else if (mPassword.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string
                    .empty_password_activity_login), Toast
                    .LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string
                    .empty_email_password_activity_login), Toast.LENGTH_SHORT).show();
        }
    }

    private void mShowDialog(Context context, String title, String message,
                             Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        // Setting Dialog Title
        alertDialog.setTitle(title);
        // Setting Dialog Message
        alertDialog.setMessage(message);
        if (status != null)
            // Setting alert dialog icon
            alertDialog.setIcon((status) ? R.drawable.ico_success : R.drawable.ico_fail);
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.action_cancel), new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.action_turn_on_wifi), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string
                .action_turn_on_cellular_data), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    private void initializeViews() {
        register = (TextView) findViewById(R.id.text_create_account);
        email = (EditText) findViewById(R.id.edit_text_email);
        password = (EditText) findViewById(R.id.edit_text_password);
        validation = (TextView) findViewById(R.id.text_validation);
        buttonLogin = (Button) findViewById(R.id.button_login);
        checkBoxRememberMe = (CheckBox) findViewById(R.id.check_remember);
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity
                .CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private class HttpAsyncTaskSignIn extends AsyncTask<String, Void, String> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(LoginActivity.this);
            mDialog.setTitle(getString(R.string.contacting_servers));
            mDialog.setMessage(getString(R.string.signing_in));
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
                } else {
                    //getting gson data
                    UserResponse userResponse;
                    Gson gson = new Gson();
                    userResponse = gson.fromJson(result, UserResponse.class);
                    int id = userResponse.getUser().getId();
                    String name = userResponse.getUser().getName();
                    String email = userResponse.getUser().getEmail();
                    String avatar = userResponse.getUser().getAvatar();
                    String authToken = userResponse.getUser().getAuth_token();
                    //creating session
                    if (!name.isEmpty() && !email.isEmpty())
                        session.createLoginSession(id, name, email, avatar, authToken,
                                mRememberMe);
                    //now finish this activity and go to ProfileActivity
                    mStartProfileActivity();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (result.isEmpty())
                Toast.makeText(getBaseContext(), getString(R.string.toast_message_try_again), Toast.LENGTH_LONG)
                        .show();
        }
    }
}
