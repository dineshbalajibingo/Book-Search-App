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
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.btn_search)
    Button mSearchButton;
    @BindView(R.id.text_view_information)
    TextView mInfoTextView;
    @BindView(R.id.edit_text_search)
    EditText mSearchEditText;
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String BOOK_REQUEST_URL ="https://www.googleapis.com/books/v1/volumes?q=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new BookAdapter(new ArrayList<Book>(), new BookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
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
    private boolean isInternetConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork.isConnectedOrConnecting();
    }
    private class BookAsyncTask extends AsyncTask<URL, Void, ArrayList<Book>> {
        private String searchInput = mSearchEditText.getText().toString();
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
            Log.i(LOG_TAG, "URL is " + url);
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
                mAdapter = new BookAdapter(new ArrayList<Book>(), new BookAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Book book) {
                    }
                });
                mRecyclerView.setAdapter(mAdapter);
                mInfoTextView.setVisibility(View.VISIBLE);
                return;
            }
            mAdapter = new BookAdapter(bookList, new BookAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Book book) {
                    String url = book.getmBookInfoLink();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
            mRecyclerView.setAdapter(mAdapter);
            mInfoTextView.setVisibility(View.GONE);
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
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
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
            ArrayList<Book> books = new ArrayList<Book>();

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
                JSONArray itemArray = baseJsonResponse.getJSONArray("items");
                for (int i = 0; i < itemArray.length(); i++) {
                    JSONObject cuurentItem = itemArray.getJSONObject(i);
                    JSONObject bookInfo = cuurentItem.getJSONObject("volumeInfo");
                    String title = bookInfo.getString("title");

                    String[] authors = new String[]{};
                    JSONArray authorJsonArray = bookInfo.optJSONArray("authors");
                    if (authorJsonArray != null) {
                        ArrayList<String> authorList = new ArrayList<String>();
                        for (int j = 0; j < authorJsonArray.length(); j++) {
                            authorList.add(authorJsonArray.get(j).toString());
                        }
                        authors = authorList.toArray(new String[authorList.size()]);
                        Log.i(LOG_TAG, "Authors are " + authors[0]);
                    }
                    String description = "";
                    if (bookInfo.optString("description") != null)
                        description = bookInfo.optString("description");
                    String infoLink = "";
                    if (bookInfo.optString("infoLink") != null)
                        infoLink = bookInfo.optString("infoLink");
                    books.add(new Book(title, authors, description, infoLink));
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
            }
            return books;
        }
    }
}

