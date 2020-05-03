package com.newage.plantedaqua.helpers;

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
import com.newage.plantedaqua.R;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class AllImageDownloader extends AsyncTask<String, Void, Bitmap> {

    private String err="";
    private RequestManager requestManager;
    private WeakReference<ImageView> imageView;
    private WeakReference<ProgressBar> progressBar;
    private WeakReference<Context> context;

    public AllImageDownloader(ImageView imageView, ProgressBar progressBar, Context context) {
        this.imageView= new WeakReference<>(imageView);
        this.progressBar= new WeakReference<>(progressBar);
        this.context = new WeakReference<>(context);
        requestManager=Glide.with(context);
    }

    @Override
    protected void onPreExecute() {
        progressBar.get().setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(String... urls) {

        URL url;
        HttpURLConnection urlConnection;

        try {

            url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            Bitmap bitmap=BitmapFactory.decodeStream(in);
            return bitmap;


        } catch (Exception e) {
            err=e.getMessage();
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
                                .placeholder(R.drawable.loading)
                                .error(R.drawable.loading))
                        .into(imageView.get());
            } else {

                Toast.makeText(context.get(), err, Toast.LENGTH_SHORT).show();
            }
        }
            progressBar.get().setVisibility(View.GONE);
        super.onPostExecute(bitmap);
    }


}
