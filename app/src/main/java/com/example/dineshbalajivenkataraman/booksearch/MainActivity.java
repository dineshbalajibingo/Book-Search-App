package com.example.dineshbalajivenkataraman.booksearch;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
public class MainActivity extends AppCompatActivity {
    
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECTION_TIMEOUT = 10000;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String BOOK_REQUEST_URL ="https://www.googleapis.com/books/v1/volumes?q=";
    private static final String KEY_TITLE = "title";
    private static final String KEY_VOLUME_INFO= "volumeInfo";
    private static final String KEY_AUTHORS = "authors";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_INFO_LINK = "infoLink";


    ArrayList<Book> books = new ArrayList<Book>();
    private RecyclerView.Adapter Adapter;
    private RecyclerView.LayoutManager LayoutManager;
    @BindView(R.id.recycler_view)
    RecyclerView RecyclerView;
    @BindView(R.id.btn_search)
    Button SearchButton;
    @BindView(R.id.text_view_information)
    TextView InfoTextView;
    @BindView(R.id.edit_text_search)
    EditText SearchEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        RecyclerView.setHasFixedSize(true);
        LayoutManager = new LinearLayoutManager(this);
        RecyclerView.setLayoutManager(LayoutManager);
        Log.i(LOG_TAG, "I am in On Create State");
        Adapter = new BookAdapter(new ArrayList<Book>(), new BookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
            }
        });
        if (savedInstanceState != null) {


            Log.i(LOG_TAG, "Devise is rotated ===============>" );
            books = savedInstanceState.getParcelableArrayList(KEY_RECYCLER_STATE);
            Log.i(LOG_TAG, "I am in Onsaved Insatnce and I am carrying Books Object " + books);
            Adapter = new BookAdapter(books, new BookAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Book book) {
                    String url = book.getBookInfoLink();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
            RecyclerView.setAdapter(Adapter);
            InfoTextView.setVisibility(View.GONE);

        }
        else
        {
            RecyclerView.setAdapter(Adapter);
            SearchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isInternetConnectionAvailable()) {
                        BookAsyncTask task = new BookAsyncTask();
                        task.execute();
                    } else
                        Toast.makeText(MainActivity.this, R.string.error_no_internet,
                                Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private boolean isInternetConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return activeNetwork.isConnected();
    }
    private class BookAsyncTask extends AsyncTask<URL, Void, ArrayList<Book>> {
        private String searchInput = SearchEditText.getText().toString();
        @Override
        protected ArrayList<Book> doInBackground(URL... urls) {
            if (searchInput.length() == 0) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.enter_search_keyword), Toast.LENGTH_SHORT).show();
                    }
                });
                return null;
            }
            searchInput = searchInput.replace(" ", "+");
            URL url = createUrl(BOOK_REQUEST_URL + searchInput);
            Log.i(LOG_TAG, "URL is " + url + "Search is " + searchInput);
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);

            } catch (IOException e) {
                Log.e(LOG_TAG, "IOException", e);
            }
            ArrayList<Book> books = extractBookInfoFromJson(jsonResponse);

            return books;
        }
        @Override
        protected void onPostExecute(ArrayList<Book> bookList) {
            if (bookList == null) {
                Adapter = new BookAdapter(new ArrayList<Book>(), new BookAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Book book) {
                    }
                });
                RecyclerView.setAdapter(Adapter);
                InfoTextView.setVisibility(View.VISIBLE);
                return;
            }
            Adapter = new BookAdapter(bookList, new BookAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Book book) {
                    String url = book.getBookInfoLink();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
            RecyclerView.setAdapter(Adapter);
            InfoTextView.setVisibility(View.GONE);
        }
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(READ_TIMEOUT /* milliseconds */);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT /* milliseconds */);
                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }
        private ArrayList<Book> extractBookInfoFromJson(String bookJSON) {
            if (TextUtils.isEmpty(bookJSON)) {
                return null;
            }
          //  ArrayList<Book> books = new ArrayList<Book>();

            try {
                JSONObject baseJsonResponse = new JSONObject(bookJSON);
                if (baseJsonResponse.getInt("totalItems") == 0) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.result_not_found), Toast.LENGTH_SHORT).show();
                        }
                    });

                    return null;
                }
                JSONArray itemArray = baseJsonResponse.getJSONArray(KEY_TITLE);
                for (int i = 0; i < itemArray.length(); i++) {
                    JSONObject cuurentItem = itemArray.getJSONObject(i);
                    JSONObject bookInfo = cuurentItem.getJSONObject(KEY_VOLUME_INFO);
                    String title = bookInfo.getString(KEY_TITLE);

                    String[] authors = new String[]{};
                    JSONArray authorJsonArray = bookInfo.optJSONArray(KEY_AUTHORS);
                    if (authorJsonArray != null) {
                        ArrayList<String> authorList = new ArrayList<String>();
                        for (int j = 0; j < authorJsonArray.length(); j++) {
                            authorList.add(authorJsonArray.get(j).toString());
                        }
                        authors = authorList.toArray(new String[authorList.size()]);

                    }
                    String description = "";
                    if (bookInfo.optString(KEY_DESCRIPTION) != null)
                        description = bookInfo.optString(KEY_DESCRIPTION);
                    String infoLink = "";
                    if (bookInfo.optString(KEY_INFO_LINK) != null)
                        infoLink = bookInfo.optString(KEY_INFO_LINK);
                    books.add(new Book(title, authors, description, infoLink));
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
            }
            return books;
        }
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(LOG_TAG, "I am from last - OnsavedInsance " + books);
       outState.putParcelableArrayList(KEY_RECYCLER_STATE, books);



    }
}

