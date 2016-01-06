package com.framgia.elsytem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.framgia.elsytem.adapters.WordAdapter;
import com.framgia.elsytem.jsonResponse.CategoryResponse;
import com.framgia.elsytem.jsonResponse.WordResponse;
import com.framgia.elsytem.utils.Constants;
import com.framgia.elsytem.utils.Libs;
import com.framgia.elsytem.utils.SessionManager;
import com.framgia.elsytem.utils.Url;
import com.framgia.elsytem.utils.WordReturnByCategory;
import com.google.gson.Gson;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LearnedActivity extends AppCompatActivity {
    ListView list;
    String url = "";
    String cattegory_id = "1";
    String option = Constants.all;
    String page = "1";
    Request request;
    Response response = null;
    String responseData = null;
    WordResponse wa;
    String newURL = "";
    int totalpage = 0;
    Gson gson;
    private com.framgia.elsytem.utils.Constants mConstant;
    OkHttpClient okHttpClient;
    String token;
    SessionManager sessionManager;
    HashMap<String, String> user;
    ArrayList<WordResponse.WordsEntity> wordName = new ArrayList<>();
    ArrayList<WordReturnByCategory> item = new ArrayList<>();
    Spinner spinnerCategory;
    ArrayList<CategoryResponse.CategoriesEntity> categoriesName = new ArrayList<CategoryResponse.CategoriesEntity>();
    ArrayList<CategoriesReturnFromPages> catitem = new ArrayList<>();
    int cattotalpage = 0;
    Response catresponse = null;
    String catresponseData = null;
    ArrayList<String> catal = new ArrayList<>();
    String catpage = "1";
    String catnewURL = "";
    String curl = "";
    ArrayAdapter<String> dataAdapter;
    LinkedHashMap<String, String> catclick;
    WordAdapter cad;
    Boolean mIsSpinnerFirstCall,mIsSpinnerOption;
    ArrayList<WordResponse.WordsEntity.AnswersEntity> wordAns;
    Url ur;
    File pdfFolder;
    File myFile;
    private String filepath = "MyInvoices";
    public String word_list = "all word";
    Spinner spinnerOption;
    private ProgressDialog mDialog;
    private int mWidth;

    static int m=0;
    static int n=0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learned);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        catclick = new LinkedHashMap<>();
        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        spinnerOption = (Spinner) findViewById(R.id.spinnerOption);
        sessionManager = new SessionManager(this);
        user = sessionManager.getUserDetails();
        token = user.get(mConstant.KEY_AUTH_TOKEN);
        list = (ListView) findViewById(R.id.learned);
        okHttpClient = new OkHttpClient();
        url = Url.wordFetchURL;
        String newURL = null;
        curl = Url.categoryFetchURL;
        try {
            String catnewURL = catURLBody(catpage);
            new HttpAsyncCategory().execute(catnewURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mIsSpinnerOption=true;
        spinnerOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!mIsSpinnerOption){
                    item.clear();
                    option = spinnerOption.getSelectedItem().toString();
                if (option.equalsIgnoreCase(Constants.OPTION_ALL)) {
                    option = Constants.all;
                } else if (option.equalsIgnoreCase(Constants.OPTION_LEARN)) {
                    option = Constants.learned;
                } else if (option.equalsIgnoreCase(Constants.OPTION_NOT_LEARN)) {
                    option = Constants.not_learned;
                }
                    String text = spinnerCategory.getSelectedItem().toString();
                    String value = catclick.get(text);
                    cattegory_id = value;
                    url = Url.wordFetchURL;
                    try {
                        cad.notifyDataSetChanged();
                        String newURL = wordURLBody(page, cattegory_id, option);
                        new HttpAsyncWord().execute(newURL);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
               else {
                    mIsSpinnerOption = false;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mIsSpinnerFirstCall = true;
        dataAdapter = new ArrayAdapter<>(this, android.R.layout
                .simple_spinner_item, catal);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(dataAdapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!mIsSpinnerFirstCall) {
                    item.clear();
                    String text = spinnerCategory.getSelectedItem().toString();
                    String value = catclick.get(text);
                    cattegory_id = value;

                    url = Url.wordFetchURL;
                    try {
                        String newURL = wordURLBody(page, cattegory_id, option);
                        new HttpAsyncWord().execute(newURL);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    n++;
                }
                else {
                    mIsSpinnerFirstCall = false;
                    n++;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        Display mDisplay = getWindowManager().getDefaultDisplay();
        mWidth = mDisplay.getWidth();
        mWidth /= 2;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
            // Respond to the action bar's 'Done' button
            case R.id.action_pdf:
                try {
                    createpdf();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(myFile), getString(R.string.pdf_type));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    Intent intent1 = Intent.createChooser(intent, getString(R.string.pdf_open));
                    startActivity(intent1);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_learned, menu);
        return true;
    }

    public void createpdf() throws IOException, DocumentException {
        if (!Libs.isExternalStorageAvailable() || Libs.isExternalStorageReadOnly()) {
            Log.e("no access", "External Storage not available or you don't have permission to write");
        } else {
            File sdCard = Environment.getExternalStorageDirectory();
            pdfFolder = new File(sdCard.getAbsolutePath() + "/" + filepath + "/");
            File newFolder = new File(Environment.getExternalStorageDirectory(), "TestFolder");
            if (!newFolder.exists()) {
                boolean t = newFolder.mkdir();
                Log.e("file create ", " " + t);
            }
            if (!pdfFolder.exists()) {
                pdfFolder.mkdir();
                Log.e("check", "Pdf Directory created" + sdCard.getAbsolutePath() + filepath);
            }
            myFile = new File(newFolder, word_list + "_" + System.currentTimeMillis() + ".pdf");
            if (!myFile.exists())
                myFile.createNewFile();
            OutputStream output = new FileOutputStream(myFile);
            Document document = new Document();
            PdfWriter pdfWriter = PdfWriter.getInstance(document, output);
            document.open();
            Font black = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
            String text = "";
            for (int i = 0; i < item.size(); i++) {
                text = text + item.get(i).getSingleWord() + "\n-------------------------------------------------------------------------\n";
            }
            Chunk greenText = new Chunk(text, black);
            Paragraph paragraph = new Paragraph(greenText);
            paragraph.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(paragraph);
            document.close();
        }
    }

    public String catURLBody(String page) throws Exception {
        LinkedHashMap<String, String> catpara = new LinkedHashMap<>();
        catpara.put(Constants.AUTH_TOKEN, token);
        catpara.put(Constants.PAGE, page);
        catnewURL = makeUrlWithParams(curl, catpara);
        return catnewURL;
    }

    private class HttpAsyncCategory extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... urls) {
            try {
                Request request = new Request.Builder().url(urls[0]).get().build();
                Log.e("token ", token);
                catresponse = okHttpClient.newCall(request).execute();
                catresponseData = catresponse.body().string();
                Log.e("response ", catresponseData);
                gson = new Gson();
                CategoryResponse ca = gson.fromJson(catresponseData, CategoryResponse.class);
                cattotalpage = ca.getTotal_pages();
                Log.e("totalpage", "" + cattotalpage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return cattotalpage;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Integer i) {
            while (i >= 1) {
                String catrl = null;
                try {
                    catrl = catURLBody(String.valueOf(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                new HttpAsyncCategoryshow().execute(catrl);
                i--;
            }
        }
    }

    private class HttpAsyncCategoryshow extends AsyncTask<String, Void, ArrayList<CategoryResponse
            .CategoriesEntity>> {


        @Override
        protected ArrayList<CategoryResponse.CategoriesEntity> doInBackground(String... urls) {
            try {
                Request request = new Request.Builder().url(urls[0]).get().build();
                catresponse = okHttpClient.newCall(request).execute();
                catresponseData = catresponse.body().string();
                gson = new Gson();
                CategoryResponse ca = gson.fromJson(catresponseData, CategoryResponse.class);
                categoriesName = (ArrayList<CategoryResponse.CategoriesEntity>) ca
                        .getCategories();
                for (int j = 0; j < categoriesName.size(); j++)
                    Log.e("value", "" + categoriesName.get(j).getName());
                cattotalpage = ca.getTotal_pages();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return categoriesName;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(ArrayList<CategoryResponse.CategoriesEntity> categoriesName) {
            for (int i = 0; i < categoriesName.size(); i++) {
                String key = categoriesName.get(i).getName();
                Log.e("key", key);
                Integer tmpvalue = categoriesName.get(i).getId();
                String value = String.valueOf(tmpvalue);
                catitem.add(new CategoriesReturnFromPages(key, tmpvalue));
                catal.add(key);
                catclick.put(key, value);
            }
            spinnerCategory.setAdapter(dataAdapter);
            dataAdapter.notifyDataSetChanged();
        }
    }

    public String wordURLBody(String page, String category_id, String option) throws Exception {
        LinkedHashMap<String, String> para = new LinkedHashMap<>();
        para.put(Constants.CCATEGORY_ID, category_id);
        para.put(Constants.OPTION, option);
        para.put(Constants.PAGE, page);
        para.put(Constants.AUTH_TOKEN, token);
        newURL = makeUrlWithParams(url, para);
        return newURL;
    }

    public static String makeUrlWithParams(String url, LinkedHashMap<String, String> params)
            throws Exception {
        if (TextUtils.isEmpty(url) || params == null || params.isEmpty())
            return url;
        String newUrl = new String(url);
        for (Map.Entry entry : params.entrySet()) {
            if (TextUtils.isEmpty(entry.getValue().toString()))
                continue;
            newUrl = concat_url(newUrl, entry.getKey().toString(), entry.getValue().toString());
        }
        return newUrl;
    }

    private static String concat_url(String url, String k, String p)
            throws UnsupportedEncodingException {
        String new_url = new String(url);
        if (url.indexOf("?") == -1) {
            new_url =
                    new_url.concat("?").concat(k).concat("=").concat(URLEncoder.encode(p,
                            "UTF-8"));
        } else if (url.endsWith("?") || url.endsWith("&")) {
            new_url = new_url.concat(k).concat("=").concat(URLEncoder.encode(p,
                    "UTF-8"));
        } else {
            new_url =
                    new_url.concat("&").concat(k).concat("=").concat(URLEncoder.encode(p,
                            "UTF-8"));
        }
        return new_url;
    }

    private class HttpAsyncWord extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... urls) {
            try {
                request = new Request.Builder().url(urls[0]).get().build();
                Log.e("token ", token);
                response = okHttpClient.newCall(request).execute();
                responseData = response.body().string();
                Log.e("response ", responseData);
                gson = new Gson();
                wa = gson.fromJson(responseData, WordResponse.class);
                totalpage = wa.getTotal_pages();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return totalpage;
        }

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(LearnedActivity.this);
            mDialog.setTitle("Learned word loading");
            mDialog.setMessage("please wait");
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(true);
            mDialog.show();
        }

        @Override
        protected void onPostExecute(Integer i) {
            while (i >= 1) {
                String newURL = null;
                try {
                    newURL = wordURLBody(String.valueOf(i), cattegory_id, option);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new HttpAsyncWordShow().execute(newURL);
                i--;
            }
            mDialog.dismiss();
        }
    }

    private class HttpAsyncWordShow extends AsyncTask<String, Void, ArrayList<WordResponse
            .WordsEntity>> {

        @Override
        protected ArrayList<WordResponse
                .WordsEntity> doInBackground(String... urls) {
            try {
                request = new Request.Builder().url(urls[0]).get().build();
                response = okHttpClient.newCall(request).execute();
                responseData = response.body().string();
                gson = new Gson();
                wa = gson.fromJson(responseData, WordResponse.class);
                wordName = (ArrayList<WordResponse.WordsEntity>) wa.getWords();
                for (int i = 0; i < wordName.size(); i++) {
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return wordName;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(ArrayList<WordResponse.WordsEntity> newWordName) {
            for (int i = 0; i < newWordName.size(); i++) {
                String key = newWordName.get(i).getContent();
                Log.e("content ", key);
                String value = null;
                wordAns = (ArrayList<WordResponse
                        .WordsEntity.AnswersEntity>) wordName.get(i).getAnswers();
                for (int j = 0; j < wordAns.size(); j++) {
                    boolean b = wordAns.get(j).isIs_correct();
                    if (b) {
                        value = wordAns.get(j).getContent();
                        break;
                    }
                }
                item.add(new WordReturnByCategory(key, getString(R.string.language)));
            }
            cad = new WordAdapter(getApplication(), item, mWidth);
            list.setAdapter(cad);
            cad.notifyDataSetChanged();
        }
    }
}