package com.newage.plantedaqua;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class AlgaeActivity extends AppCompatActivity {



    boolean imageViewVisible=false;
    HashMap<String,Boolean> algaeImageViewVisible=new HashMap <>();
    String err="";
    RequestManager requestManager;

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        private ImageView imageView;
        public ImageDownloader(ImageView imageView) {
           this.imageView=imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                Bitmap algaeBitmap= BitmapFactory.decodeStream(in);
                return algaeBitmap;


            } catch (Exception e) {
                err=e.getMessage();
               return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            requestManager
                    .load(bitmap)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.noimage)
                            .error(R.drawable.noimage))
                    .into(imageView);
            super.onPostExecute(bitmap);
        }
    }

    private void downloadImage(ImageView imageView, String url){
        ImageDownloader task=new ImageDownloader(imageView);
        try{
            task.execute(url);
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_algae);

        requestManager=Glide.with(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayout linearLayoutBBA=findViewById(R.id.BBALinear);
        ImageView imageViewBBA=findViewById(R.id.imageViewbba);
        downloadImage(imageViewBBA,"https://gdurl.com/Ubpi");
        algaeImageViewVisible.put("BBA",false);

        expandImage(linearLayoutBBA,imageViewBBA,"BBA");


        LinearLayout linearLayoutGSA=findViewById(R.id.GSALinear);
        ImageView imageViewGSA=findViewById(R.id.imageViewgsa);
        downloadImage(imageViewGSA,"https://gdurl.com/3-bG");
        algaeImageViewVisible.put("GSA",false);

        expandImage(linearLayoutGSA,imageViewGSA,"GSA");

        LinearLayout linearLayoutStag=findViewById(R.id.StagLinear);
        ImageView imageViewStag=findViewById(R.id.imageViewstag);
        downloadImage(imageViewStag," https://gdurl.com/lODS");
        algaeImageViewVisible.put("Stag",false);

        expandImage(linearLayoutStag,imageViewStag,"Stag");

        LinearLayout linearLayoutRhizo=findViewById(R.id.RhizoLinear);
        ImageView imageViewRhizo=findViewById(R.id.imageViewRhizo);
        downloadImage(imageViewRhizo,"https://gdurl.com/DGMD");
        algaeImageViewVisible.put("Rhizo",false);

        expandImage(linearLayoutRhizo,imageViewRhizo,"Rhizo");

        LinearLayout linearLayoutBGA=findViewById(R.id.BGALinear);
        ImageView imageViewBGA=findViewById(R.id.imageViewBGA);
        downloadImage(imageViewBGA,"https://gdurl.com/M8lc");
        algaeImageViewVisible.put("BGA",false);

        expandImage(linearLayoutBGA,imageViewBGA,"BGA");


        LinearLayout linearLayoutGW=findViewById(R.id.GWLinear);
        ImageView imageViewGW=findViewById(R.id.imageViewGW);
        downloadImage(imageViewGW,"https://gdurl.com/rTxg");
        algaeImageViewVisible.put("GW",false);

        expandImage(linearLayoutGW,imageViewGW,"GW");

        LinearLayout linearLayoutClado=findViewById(R.id.CladoLinear);
        ImageView imageViewClado=findViewById(R.id.imageViewClado);
        downloadImage(imageViewClado,"https://gdurl.com/XcKt");
        algaeImageViewVisible.put("Clado",false);

        expandImage(linearLayoutClado,imageViewClado,"Clado");

        LinearLayout linearLayoutBDA=findViewById(R.id.BDALinear);
        ImageView imageViewBDA=findViewById(R.id.imageViewBDA);
        downloadImage(imageViewBDA," https://gdurl.com/PSKNB");
        algaeImageViewVisible.put("BDA",false);

        expandImage(linearLayoutBDA,imageViewBDA,"BDA");

        LinearLayout linearLayoutThread=findViewById(R.id.ThreadLinear);
        ImageView imageViewThread=findViewById(R.id.imageViewThread);
        downloadImage(imageViewThread," https://gdurl.com/Ohlp");
        algaeImageViewVisible.put("Thread",false);

        expandImage(linearLayoutThread,imageViewThread,"Thread");


    }
    void expandImage(final LinearLayout linearLayout, ImageView imageView, final String algae){



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewVisible=algaeImageViewVisible.get(algae);
                if(imageViewVisible){
                    linearLayout.setVisibility(View.VISIBLE);
                    algaeImageViewVisible.put(algae,false);
                }
                else {
                    linearLayout.setVisibility(View.GONE);
                    imageViewVisible=true;
                    algaeImageViewVisible.put(algae,true);
                }
            }
        });
    }

}
