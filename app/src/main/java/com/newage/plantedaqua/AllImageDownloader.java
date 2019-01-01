package com.newage.plantedaqua;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AllImageDownloader extends AsyncTask<String, Void, Bitmap> {

    private String err="";
    private RequestManager requestManager;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Context context;
    public AllImageDownloader(ImageView imageView, ProgressBar progressBar, Context context) {
        this.imageView=imageView;
        this.progressBar=progressBar;
        this.context = context;
        requestManager=Glide.with(context);
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(String... urls) {

        URL url;
        HttpURLConnection urlConnection;

        try {

            url = new URL(urls[0]);
            // Log.i("ParsedUrl",url.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            Bitmap bitmap=BitmapFactory.decodeStream(in);
            return bitmap;


        } catch (Exception e) {
            err=e.getMessage();
            Toast.makeText(context, err, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if(bitmap!=null) {

            if (err.isEmpty()) {
                requestManager
                        .load(bitmap)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .placeholder(R.drawable.noimage)
                                .error(R.drawable.noimage))
                        .into(imageView);
            } else {
                Toast.makeText(context, err, Toast.LENGTH_SHORT).show();
                //Log.i("Download error", err);
            }
        }//else
            //Log.i("BitmapError","null");
            progressBar.setVisibility(View.GONE);
        super.onPostExecute(bitmap);
    }
}
