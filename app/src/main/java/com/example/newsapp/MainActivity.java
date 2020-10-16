package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    RecyclerView recyclerView;
    private static WeakReference<MainActivity> mainActivityWeakReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        mainActivityWeakReference = new WeakReference<>(this);

        loadPlainXmlFromServer();
    }

    private void loadPlainXmlFromServer() {
        progressBar.setVisibility(View.VISIBLE);
        new LoadXml().execute("https://news.google.com/rss?hl=en-IN&gl=IN&ceid=IN:en");
    }

    public static class LoadXml extends AsyncTask<String, Integer, ArrayList<NewsItem>>{

        @Override
        protected ArrayList<NewsItem> doInBackground(String... strings) {
            try {
                InputStream stream = getInputStream(strings[0]);
                return parseInputStream(stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<NewsItem> newsItems) {
            super.onPostExecute(newsItems);
            Activity activity = mainActivityWeakReference.get();

            if(activity==null || activity.isFinishing()) return;

            activity.findViewById(R.id.progressBar).setVisibility(View.GONE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
            RecyclerView recyclerView = activity.findViewById(R.id.recycler);
            recyclerView.setLayoutManager(linearLayoutManager);

            NewsAdapter newsAdapter = new NewsAdapter(activity, newsItems);
            recyclerView.setAdapter(newsAdapter);
        }

        private InputStream getInputStream(String string) throws IOException {
            URL url = new URL(string);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            return httpURLConnection.getInputStream();
        }

        private ArrayList<NewsItem> parseInputStream(InputStream stream) {
            XmlPullParserFactory xmlPullParserFactory = null;
            ArrayList<NewsItem> newsItems = new ArrayList<>();

            try {
                xmlPullParserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
                xmlPullParser.setInput(stream, null);

                NewsItem newsItem = null;
                int eventType = xmlPullParser.getEventType();

                while(eventType != XmlPullParser.END_DOCUMENT){

                    String tagName = xmlPullParser.getName();

                    switch (eventType){
                        case XmlPullParser.START_TAG:
                            if(tagName.equals("item")){
                                newsItem = new NewsItem();
                                newsItems.add(newsItem);
                            }
                            else if(newsItem != null){
                                switch (tagName){
                                    case "title":
                                        newsItem.setTitle(xmlPullParser.nextText());
                                        break;
                                    case "description":
                                        newsItem.setDesc(xmlPullParser.nextText());
                                        break;
                                    case "link":
                                        newsItem.setLink(xmlPullParser.nextText());
                                        break;
                                    case "source":
                                        newsItem.setSource(xmlPullParser.nextText());
                                        break;
                                    case "pubDate":
                                        newsItem.setDate(xmlPullParser.nextText());
                                        break;
                                }
                            }
                            break;
                    }

                    eventType = xmlPullParser.next();
                }
                return newsItems;

            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
            return newsItems;
        }
    }
}