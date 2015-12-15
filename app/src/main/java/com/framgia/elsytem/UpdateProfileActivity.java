package com.framgia.elsytem;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.framgia.elsytem.model.Profile;
import com.framgia.elsytem.mypackage.SessionManager;
import com.framgia.elsytem.mypackage.UserFunctions;

public class UpdateProfileActivity extends AppCompatActivity {
    private static final String TAG = "UpdateProfileActivity";
    ImageView avatar;
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    // Session Manager Class
    //SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Session class instance
        /*session = new SessionManager(getApplicationContext());
        session.checkLogin();*/
        avatar = (ImageView) findViewById(R.id.image_upload_avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImageFromGallery(v);
            }
        });
    }

    public void loadImageFromGallery(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                ImageView imgView = (ImageView) findViewById(R.id.image_upload_avatar);
                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_update_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                logOut();
                return true;
            // Respond to the action bar's 'Done' button
            case R.id.action_update:
                /*Toast.makeText(getApplicationContext(), "Will be implemented later", Toast
                        .LENGTH_LONG).show();*/
                new HttpAsyncTaskUpdateProfile().execute("https://manh-nt.herokuapp.com/categories/1/lessons.json");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        logOut();
    }

    private class HttpAsyncTaskUpdateProfile extends AsyncTask<String, Void, String> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(UpdateProfileActivity.this);
            mDialog.setTitle("Contacting Servers");
            mDialog.setMessage("Updating profile ...");
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(true);
            mDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            Profile profile = new Profile();
            UserFunctions userFunction = new UserFunctions();
            return userFunction.updateProfile(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            mDialog.dismiss();
            Log.e(TAG, result);
            Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
        }
    }

    private class HttpAsyncTaskSignOut extends AsyncTask<String, Void, String> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(UpdateProfileActivity.this);
            mDialog.setTitle("Contacting Servers");
            mDialog.setMessage("Signing out ...");
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(true);
            mDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            UserFunctions userFunction = new UserFunctions();
            return userFunction.signOut(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            mDialog.dismiss();
            if (result.equals("Logout success")) {
                SessionManager session = new SessionManager(getApplicationContext());
                session.logoutUser();
            }
            Log.e(TAG, result);
            Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
        }
    }

    public void logOut() {
        // dialog box
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.activity_main_alert_dialog_title)
                .setMessage(R.string.activity_main_alert_dialog_message)
                .setPositiveButton(R.string.activity_main_alert_dialog_yes, new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new HttpAsyncTaskSignOut().execute("https://manh-nt.herokuapp.com/logout.json");
                    }
                })
                .setNegativeButton(R.string.activity_main_alert_dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }
}
