package com.ravish.quicknews.Adapter;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.ravish.quicknews.R;
import com.ravish.quicknews.model.News;

import androidx.appcompat.view.ActionMode;

import java.util.ArrayList;
import java.util.Locale;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private ArrayList<News> mNewsList;
    private Context mContext;
    private ActionMode mActionMode;
    private TextToSpeech mTextToSpeech;

    public NewsAdapter(Context context, ArrayList<News> newsList) {
        mContext = context;
        mNewsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.news_list_item,parent,false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = mNewsList.get(position);
        holder.title.setText(news.getTitle());
        holder.description.setText(news.getDescription());
        holder.name.setText(news.getName());
        holder.date.setText(news.getDate());
        Glide.with(mContext).load(news.getImageUrl()).into(holder.imgIcon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = news.getUrl();
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(mContext, Uri.parse(url));
            }
        });

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.bottom_sheet);
                String title = news.getTitle();

                TextView titleTextView = dialog.findViewById(R.id.titleTextView);
                ImageView newsImage = dialog.findViewById(R.id.newsImage);
                LinearLayout listenNews = dialog.findViewById(R.id.listen_news);
                LinearLayout saveArticle = dialog.findViewById(R.id.save_article);
                LinearLayout shareArticle = dialog.findViewById(R.id.share_article);
                LinearLayout copyLink = dialog.findViewById(R.id.copy_article_link);
                titleTextView.setText(title);
                Glide.with(mContext).load(news.getImageUrl()).into(newsImage);

                listenNews.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, mContext.getString(R.string.listen_news), Toast.LENGTH_SHORT).show();
                        String description = news.getDescription();
                        mTextToSpeech = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if (status == TextToSpeech.SUCCESS){
                                    int result =mTextToSpeech.setLanguage(Locale.ENGLISH);

                                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                                        Log.e("TTS", "Language Not Supported");
                                    }else {
                                        mTextToSpeech.speak(description, TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                }else {
                                    Log.e("TTS", "Initialization Failed");
                                }
                            }
                        });
                        dialog.dismiss();
                    }
                });

                saveArticle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, mContext.getString(R.string.save_article), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                shareArticle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, mContext.getString(R.string.share), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, "Hi, Checkout this latest news article\n\n" + news.getUrl());
                        mContext.startActivity(Intent.createChooser(intent, "Sending This News Article Using..."));
                        dialog.dismiss();
                    }
                });

                copyLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData data = (ClipData) ClipData.newRawUri("News Url", Uri.parse(news.getUrl()));
                        clipboardManager.setPrimaryClip(data);
                        Toast.makeText(mContext, "Copied To Clipboard", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mActionMode != null){
                    return false;
                }
                ((AppCompatActivity)mContext).startSupportActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        mode.getMenuInflater().inflate(R.menu.menu_item, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.share:
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                intent.putExtra(Intent.EXTRA_TEXT, "Hi, Checkout this latest news article\n\n" + news.getUrl());
                                mContext.startActivity(Intent.createChooser(intent, "Sending This News Article Using..."));
                                mode.finish();
                                return true;
                            case R.id.copy:
                                ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData data = (ClipData) ClipData.newRawUri("News Url", Uri.parse(news.getUrl()));
                                clipboardManager.setPrimaryClip(data);

                                Toast.makeText(mContext, "Copied To Clipboard", Toast.LENGTH_SHORT).show();
                                mode.finish();
                                return true;
                            case R.id.speak:
                                String description = news.getDescription();
                                mTextToSpeech = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
                                    @Override
                                    public void onInit(int status) {
                                        if (status == TextToSpeech.SUCCESS){
                                            int result =mTextToSpeech.setLanguage(Locale.ENGLISH);

                                            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                                                Log.e("TTS", "Language Not Supported");
                                            }else {
                                                mTextToSpeech.speak(description, TextToSpeech.QUEUE_FLUSH, null);
                                            }
                                        }else {
                                            Log.e("TTS", "Initialization Failed");
                                        }
                                    }
                                });
                                mode.finish();
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        mActionMode = null;
                    }
                });
                return true;
            }

        });
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgIcon;
        public TextView title;
        public TextView description;
        public TextView name;
        public TextView date;
        public ImageView menu;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = (ImageView) itemView.findViewById(R.id.imageView);
            title = (TextView) itemView.findViewById(R.id.titleView);
            description = (TextView) itemView.findViewById(R.id.description);
            name = (TextView) itemView.findViewById(R.id.nameTextView);
            date = (TextView) itemView.findViewById(R.id.date);
            menu = itemView.findViewById(R.id.menu);
        }

    }

}
