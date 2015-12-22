package com.framgia.elsytem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.framgia.elsytem.model.Profile;
import com.framgia.elsytem.mypackage.Constants;
import com.framgia.elsytem.mypackage.SessionManager;
import com.framgia.elsytem.mypackage.UserFunctions;

import java.util.HashMap;

public class UpdateProfileActivity extends AppCompatActivity {
    private static final String TAG = "UpdateProfileActivity";
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    // Session Manager Class
    SessionManager session;
    HashMap<String, String> user;
    private String mEmail, mOldPassword, mNewPassword, mPasswordConfirmation, mFullName, mAvatar,
            mRememberToken;
    private EditText mEtemail, mEtOldPassword, mEtNewPassword, mEtPasswordConfirmation,
            mEtFullName;
    private ImageView mIvAvatar;
    private Constants mConstant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        setUpActionBar();
        initializeViews();
        // Session class instance
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        mEtemail.setText(user.get(mConstant.KEY_EMAIL));
        mEtFullName.setText(user.get(mConstant.KEY_NAME));
        if (!user.get(mConstant.KEY_AVATAR).isEmpty())
            mIvAvatar.setImageBitmap(BitmapFactory.decodeFile
                    (user.get(mConstant.KEY_AVATAR)));
        imgDecodableString = user.get(mConstant.KEY_AVATAR);
        mRememberToken = user.get(mConstant.KEY_REMEMBER_TOKEN);
        mIvAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImageFromGallery(v);
            }
        });
    }

    private void initializeViews() {
        mEtemail = (EditText) findViewById(R.id.edit_text_email);
        mEtOldPassword = (EditText) findViewById(R.id.edit_text_old_password);
        mEtNewPassword = (EditText) findViewById(R.id.edit_text_new_password);
        mEtPasswordConfirmation = (EditText) findViewById(R.id.edit_text_retype_password);
        mEtFullName = (EditText) findViewById(R.id.edit_text_full_name);
        mIvAvatar = (ImageView) findViewById(R.id.image_upload_avatar);
    }

    private void setUpActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                // Set the Image in ImageView after decoding the String
                mIvAvatar.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));
            } else {
                Toast.makeText(this, getString(R.string.toast_message_pick_image),
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.toast_message_pick_image_error), Toast
                    .LENGTH_LONG).show();
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
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
                return true;
            // Respond to the action bar's 'Done' button
            case R.id.action_update:
                mEmail = mEtemail.getText().toString();
                mOldPassword = mEtOldPassword.getText().toString();
                mNewPassword = mEtNewPassword.getText().toString();
                mPasswordConfirmation = mEtPasswordConfirmation.getText().toString();
                mFullName = mEtFullName.getText().toString();
                new HttpAsyncTaskUpdateProfile().execute(getString(R.string.url_update_profile));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    private class HttpAsyncTaskUpdateProfile extends AsyncTask<String, Void, String> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(UpdateProfileActivity.this);
            mDialog.setTitle(getString(R.string.contacting_servers));
            mDialog.setMessage(getString(R.string.updating_profile));
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(true);
            mDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            Profile profile = new Profile();
            profile.setName(mFullName);
            profile.setOld_password(mOldPassword);
            profile.setNew_password(mNewPassword);
            profile.setAvatar(imgDecodableString);
            profile.setRememberToken(mRememberToken);
            UserFunctions userFunction = new UserFunctions();
            return userFunction.updateProfile(urls[0], profile);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            mDialog.dismiss();
            // deleting current session
            session.deleteSessionData();
            // creating new session with updated data
            session.createLoginSession(mFullName, mEmail, imgDecodableString, user.get
                    (mConstant.KEY_REMEMBER_TOKEN), Integer.parseInt(user.get(mConstant
                    .KEY_REMEMBER_ME)));
            Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
        }
    }
}
