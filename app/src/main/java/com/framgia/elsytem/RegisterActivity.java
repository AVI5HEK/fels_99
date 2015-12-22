package com.framgia.elsytem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.framgia.elsytem.model.User;
import com.framgia.elsytem.mypackage.AlertDialogManager;
import com.framgia.elsytem.mypackage.UserFunctions;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private static String mEmail, mPassword, mPasswordConfirmation, mFullName;
    private static EditText mEtEmail, mEtPassword, mEtPasswordConfirmation, mEtFullName;
    private AlertDialogManager mAlert;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setUpActionBar();
        initializeViews();
        mAlert = new AlertDialogManager();
    }

    private void initializeViews() {
        mEtEmail = (EditText) findViewById(R.id.edit_text_email);
        mEtPassword = (EditText) findViewById(R.id.edit_text_password);
        mEtPasswordConfirmation = (EditText) findViewById(R.id.edit_text_retype_password);
        mEtFullName = (EditText) findViewById(R.id.edit_text_full_name);
    }

    private void setUpActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            // Respond to the action bar's 'Done' button
            case R.id.action_done:
                mEmail = mEtEmail.getText().toString();
                mPassword = mEtPassword.getText().toString();
                mPasswordConfirmation = mEtPasswordConfirmation.getText().toString();
                mFullName = mEtFullName.getText().toString();
                if (!isConnected())
                    mAlert.showAlertDialog(RegisterActivity.this, getString(R.string.connection_error_title_activity_login),
                            getString(R.string.connection_error_message_activity_login), false);
                if (!validate(mFullName, mEmail, mPassword, mPasswordConfirmation))
                    Toast.makeText(getBaseContext(), "Provide the required information!", Toast
                            .LENGTH_LONG).show();
                    // call AsyncTask to perform network operation on separate thread
                else new HttpAsyncTask().execute("https://manh-nt.herokuapp.com/users.json");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validate(String... params) {
        if (params == null) return false;
        for (String param : params) {
            if (TextUtils.isEmpty(param)) return false;
        }
        return true;
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(RegisterActivity.this);
            mDialog.setTitle("Contacting Servers");
            mDialog.setMessage("Signing up ...");
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(true);
            mDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            user = new User();
            user.setName(mFullName);
            user.setEmail(mEmail);
            user.setPassword(mPassword);
            user.setPassword_confirmation(mPasswordConfirmation);
            UserFunctions userFunction = new UserFunctions();
            return userFunction.signUp(urls[0], user);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            mDialog.dismiss();
            Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
            if (result.equals("Sign up success")) {
                finish();
            }
        }
    }
}
