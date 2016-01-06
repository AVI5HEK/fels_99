package com.framgia.elsytem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.framgia.elsytem.jsonResponse.UserResponse;
import com.framgia.elsytem.model.Profile;
import com.framgia.elsytem.utils.RoundedCornersTransformation;
import com.framgia.elsytem.utils.Constants;
import com.framgia.elsytem.utils.SessionManager;
import com.framgia.elsytem.utils.Url;
import com.framgia.elsytem.utils.UserFunctions;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class UpdateProfileActivity extends AppCompatActivity {
    private static final String TAG = "UpdateProfileActivity";
    private static int RESULT_LOAD_IMG = 1;
    String filePath;
    // Session Manager Class
    SessionManager session;
    HashMap<String, String> user;
    private String mId, mEmail, mOldPassword, mNewPassword, mPasswordConfirmation, mFullName,
            mAvatar, mAuthToken;
    private EditText mEtemail, mEtOldPassword, mEtNewPassword, mEtPasswordConfirmation,
            mEtFullName;
    private ImageView mIvAvatar;
    private Button mButton_avatar;
    File file;
    String imageDataBase64String;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        setUpActionBar();
        initializeViews();
        mGetSessionData();
        mEtemail.setText(user.get(Constants.KEY_EMAIL));
        mEtFullName.setText(user.get(Constants.KEY_NAME));
        imageDataBase64String = user.get(Constants.KEY_AVATAR);
        if (!imageDataBase64String.isEmpty())
            mLoadAvatar(imageDataBase64String);
        mAuthToken = user.get(Constants.KEY_AUTH_TOKEN);
        mInitializeListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!imageDataBase64String.isEmpty())
            mButton_avatar.setVisibility(View.VISIBLE);
        else mButton_avatar.setVisibility(View.INVISIBLE);
    }

    private void mInitializeListeners() {
        mIvAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImageFromGallery(v);
            }
        });
        mEtFullName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            mUpdateProfile();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        mButton_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRemoveAvatar();
            }
        });
    }

    private void mRemoveAvatar() {
        imageDataBase64String = "";
        Picasso.with(this)
                .load(R.drawable.ic_add_a_photo_black_36dp)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .resize(Constants.AVATAR_WIDTH_HEIGHT_AND_RADIUS, Constants.AVATAR_WIDTH_HEIGHT_AND_RADIUS)
                .centerCrop()
                .into(mIvAvatar);
        mButton_avatar.setVisibility(View.INVISIBLE);
    }

    private void mGetSessionData() {
        // Session class instance
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
    }

    private void mUpdateProfile() {
        mId = session.getUserDetails().get(Constants.KEY_ID);
        mEmail = mEtemail.getText().toString();
        mOldPassword = mEtOldPassword.getText().toString();
        mNewPassword = mEtNewPassword.getText().toString();
        mPasswordConfirmation = mEtPasswordConfirmation.getText().toString();
        mFullName = mEtFullName.getText().toString();
        if (!mNewPassword.equals(mPasswordConfirmation)) Toast.makeText
                (getApplicationContext(), R.string.toast_message_password_mismatch, Toast
                        .LENGTH_LONG).show();
        else if (mValidate(mOldPassword, mNewPassword, mPasswordConfirmation, mFullName,
                imageDataBase64String)) {
            new HttpAsyncTaskUpdateProfile().execute(Url.url_update_profile + mId + ".json");
        } else {
            Toast.makeText(getApplicationContext(), R.string
                    .toast_message_nothing_updated, Toast.LENGTH_LONG).show();
            onBackPressed();
        }
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

    private void initializeViews() {
        mEtemail = (EditText) findViewById(R.id.edit_text_email);
        mEtOldPassword = (EditText) findViewById(R.id.edit_text_old_password);
        mEtNewPassword = (EditText) findViewById(R.id.edit_text_new_password);
        mEtPasswordConfirmation = (EditText) findViewById(R.id.edit_text_retype_password);
        mEtFullName = (EditText) findViewById(R.id.edit_text_full_name);
        mIvAvatar = (ImageView) findViewById(R.id.image_upload_avatar);
        mButton_avatar = (Button) findViewById(R.id.button_remove_avatar);
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
                filePath = cursor.getString(columnIndex);
                cursor.close();
                // creating file from the file path
                file = new File(filePath);
                try {
                    // Reading a Image file from file system
                    FileInputStream imageInFile = new FileInputStream(file);
                    byte imageData[] = new byte[(int) file.length()];
                    imageInFile.read(imageData);
                    // Converting Image byte array into Base64 String
                    imageDataBase64String = encodeImage(imageData);
                    imageInFile.close();
                    // load avatar in imageView
                    mLoadAvatar(imageDataBase64String);
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

    private void mLoadAvatar(String avatar) {
        if (!avatar.isEmpty()) {
            if (isUrl(avatar)) {
                Picasso.with(this)
                        .load(avatar)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .resize(Constants.AVATAR_WIDTH_HEIGHT_AND_RADIUS, Constants
                                .AVATAR_WIDTH_HEIGHT_AND_RADIUS)
                        .centerCrop()
                        .transform(new RoundedCornersTransformation(Constants
                                .AVATAR_WIDTH_HEIGHT_AND_RADIUS, Constants.ROUNDED_AVATAR_MARGIN))
                        .placeholder(R.drawable.ic_add_a_photo_black_36dp)
                        .error(R.drawable.ico_fail)
                        .into(mIvAvatar);
            } else {
                // Write image byte array from base64 string into file system
                mCreateFileFromBase64String(avatar);
                // get the uri of the created image
                Uri uri = mGetUri(getFilesDir() + Constants.KEY_AVATAR_FILE_NAME);
                Picasso.with(this)
                        .load(uri)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .resize(Constants.AVATAR_WIDTH_HEIGHT_AND_RADIUS, Constants.AVATAR_WIDTH_HEIGHT_AND_RADIUS)
                        .centerCrop()
                        .transform(new RoundedCornersTransformation(Constants
                                .AVATAR_WIDTH_HEIGHT_AND_RADIUS, Constants.ROUNDED_AVATAR_MARGIN))
                        .placeholder(R.drawable.ic_add_a_photo_black_36dp)
                        .error(R.drawable.ico_fail)
                        .into(mIvAvatar);
            }
        }
    }

    private Uri mGetUri(String filePath) {
        return Uri.fromFile(new File(filePath));
    }

    private void mCreateFileFromBase64String(String avatar) {
        FileOutputStream imageOutputFile = null;
        try {
            imageOutputFile = new FileOutputStream(getFilesDir()
                    + Constants.KEY_AVATAR_FILE_NAME, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            imageOutputFile.write(decodeImage(avatar));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            imageOutputFile.close();
        } catch (IOException e) {
            e.printStackTrace();
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
                NavUtils.navigateUpFromSameTask(this);
                return true;
            // Respond to the action bar's 'Done' button
            case R.id.action_update:
                mUpdateProfile();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean mValidate(String... params) {
        if (params == null) return false;
        if (params[0].isEmpty()
                && params[1].isEmpty()
                && params[2].isEmpty()
                && params[3].equals(user.get(Constants.KEY_NAME))
                && params[4].equals(user.get(Constants.KEY_AVATAR)))
            return false;
        return true;
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
            profile.setAvatar(imageDataBase64String);
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
            if (id == Integer.parseInt(user.get(Constants.KEY_ID))) {
                // deleting current session
                session.deleteSessionData();
                // creating new session with updated data
                session.createLoginSession(Integer.parseInt(mId), mFullName, mEmail,
                        imageDataBase64String, user.get(Constants.KEY_AUTH_TOKEN), Integer.parseInt
                                (user.get(Constants.KEY_REMEMBER_ME)));
                Toast.makeText(getBaseContext(), getString(R.string.toast_message_update_successful), Toast.LENGTH_LONG).show();
                onBackPressed();
            } else Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
        }
    }
}
