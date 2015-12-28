package com.framgia.elsytem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.framgia.elsytem.jsonResponse.UserResponse;
import com.framgia.elsytem.model.Profile;
import com.framgia.elsytem.mypackage.Constants;
import com.framgia.elsytem.mypackage.SessionManager;
import com.framgia.elsytem.mypackage.UserFunctions;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class UpdateProfileActivity extends AppCompatActivity {
    private static final String TAG = "UpdateProfileActivity";
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    // Session Manager Class
    SessionManager session;
    HashMap<String, String> user;
    private String mId, mEmail, mOldPassword, mNewPassword, mPasswordConfirmation, mFullName,
            mAvatar, mAuthToken;
    private EditText mEtemail, mEtOldPassword, mEtNewPassword, mEtPasswordConfirmation,
            mEtFullName;
    private ImageView mIvAvatar;
    private Constants mConstant;
    Bitmap bitmap;
    ProgressDialog pDialog;
    File file;
    String imageDataString;

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
        imageDataString = user.get(mConstant.KEY_AVATAR);
        if (!imageDataString.isEmpty()) {
            if (isUrl(imageDataString)) {
                new LoadImage().execute(imageDataString);
            } else {
                byte[] decodedString = decodeImage(imageDataString);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,
                        decodedString.length);
                mIvAvatar.setImageBitmap(decodedByte);
            }
        }
        mAuthToken = user.get(mConstant.KEY_AUTH_TOKEN);
        mIvAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImageFromGallery(v);
            }
        });
    }

    /**
     * Checks if avatar is a url
     */
    public boolean isUrl(String avatar) {
        URL url = null;
        try {
            url = new URL(avatar);
        } catch (MalformedURLException e) {
            Log.v(TAG, e.toString());
        }
        return url != null;
    }

    /**
     * AsyncTask class for Loading Image from a url
     */
    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UpdateProfileActivity.this);
            pDialog.setMessage(getString(R.string.please_wait));
            pDialog.show();
        }

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            if (image != null) {
                mIvAvatar.setImageBitmap(image);
                pDialog.dismiss();
            } else {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string
                        .toast_message_load_image_error), Toast.LENGTH_LONG).show();
            }
        }
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

                // creating file from the file path
                file = new File(imgDecodableString);
                try {
                    // Reading a Image file from file system
                    FileInputStream imageInFile = new FileInputStream(file);
                    byte imageData[] = new byte[(int) file.length()];
                    imageInFile.read(imageData);
                    // Converting Image byte array into Base64 String
                    imageDataString = encodeImage(imageData);
                    imageInFile.close();
                    // Converting a Base64 String into Image byte array
                    byte[] decodedString = decodeImage(imageDataString);
                    // Set the Image in ImageView after decoding the String
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,
                            decodedString.length);
                    mIvAvatar.setImageBitmap(decodedByte);
                } catch (FileNotFoundException e) {
                    Log.e(TAG, e.toString());
                } catch (IOException ioe) {
                    Log.e(TAG, ioe.toString());
                }
            } else {
                Toast.makeText(this, getString(R.string.toast_message_pick_image),
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.toast_message_pick_image_error), Toast
                    .LENGTH_LONG).show();
        }
    }


    /**
     * Encodes the byte array into base64 string
     *
     * @param imageByteArray - byte array
     * @return String a {@link java.lang.String}
     */
    public static String encodeImage(byte[] imageByteArray) {
        return Base64.encodeToString(imageByteArray, Base64.DEFAULT);
    }

    /**
     * Decodes the base64 string into byte array
     *
     * @param imageDataString - a {@link java.lang.String}
     * @return byte array
     */
    public static byte[] decodeImage(String imageDataString) {
        return Base64.decode(imageDataString, Base64.DEFAULT);
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
                mId = session.getUserDetails().get(mConstant.KEY_ID);
                mEmail = mEtemail.getText().toString();
                mOldPassword = mEtOldPassword.getText().toString();
                mNewPassword = mEtNewPassword.getText().toString();
                mPasswordConfirmation = mEtPasswordConfirmation.getText().toString();
                mFullName = mEtFullName.getText().toString();
                new HttpAsyncTaskUpdateProfile().execute(getString(R.string.url_update_profile)
                        + mId + ".json");
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
            profile.setEmail(mEmail);
            profile.setNew_password(mNewPassword);
            profile.setPassword_confirmation(mPasswordConfirmation);
            profile.setAvatar(imageDataString);
            profile.setAuthToken(mAuthToken);
            UserFunctions userFunction = new UserFunctions();
            return userFunction.updateProfile(urls[0], profile);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            mDialog.dismiss();
            UserResponse userResponse;
            Gson gson = new Gson();
            userResponse = gson.fromJson(result, UserResponse.class);
            int id = 0;
            try {
                id = userResponse.getUser().getId();
            } catch (Exception e) {
            }
            if (id == Integer.parseInt(user.get(mConstant.KEY_ID))) {
                // deleting current session
                session.deleteSessionData();
                // creating new session with updated data
                session.createLoginSession(Integer.parseInt(mId), mFullName, mEmail,
                        imageDataString, user.get(mConstant.KEY_AUTH_TOKEN), Integer.parseInt
                                (user.get(mConstant.KEY_REMEMBER_ME)));
                Toast.makeText(getBaseContext(), getString(R.string.toast_message_update_successful), Toast.LENGTH_LONG).show();
            } else Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
        }
    }
}
