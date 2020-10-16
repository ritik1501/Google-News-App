package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    Context context;
    ArrayList<NewsItem> newsItems;

    public NewsAdapter(Context context, ArrayList<NewsItem> newsItems) {
        this.context = context;
        this.newsItems = newsItems;
    }

    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {
        NewsItem newsItem = newsItems.get(position);

        holder.title.setText(newsItem.getTitle().trim());

        String parsedHtml = removeHtml(newsItem.getDesc()).trim();
        if(parsedHtml.length() >= 150){
            holder.desc.setText(parsedHtml.substring(0,150)+"...");
        }
        else {
            holder.desc.setText(parsedHtml);
        }

        holder.date.setText("\n"+newsItem.getDate());
        holder.src.setText("Posted By : " + newsItem.getSource());
    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title, desc, src, date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.news_title);
            desc = itemView.findViewById(R.id.news_description);
            src = itemView.findViewById(R.id.news_src);
            date = itemView.findViewById(R.id.news_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsItems.get(getAdapterPosition()).getLink()));
            context.startActivity(intent);
        }
    }

    public static String removeHtml(String html){
        html = html.replaceAll("&nbsp"," ");
        html = html.replaceAll("&amp","");
        html = html.replaceAll("<(.*?)\\>"," ");
        html = html.replaceAll("<(.*?)\\n"," ");
        html = html.replaceFirst("<(.*?)\\>"," ");
        return html;
    }
}
