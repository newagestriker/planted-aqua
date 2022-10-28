package com.newage.aquapets.activities;


import android.os.Bundle;


import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.newage.aquapets.helpers.ConnectionDetector;
import com.newage.aquapets.R;

import java.util.HashMap;

import static java.lang.Boolean.TRUE;

public class AlgaeActivity extends AppCompatActivity {



    boolean imageViewVisible=false;
    HashMap<String,Boolean> algaeImageViewVisible=new HashMap <>();
    RequestManager requestManager;
    View algaeView;



/*
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
                            .placeholder(R.drawable.loading)
                            .error(R.drawable.noimage))
                    .into(imageView);
            super.onPostExecute(bitmap);
        }
    }

    private void downloadImage(ImageView imageView, String url){

        imageView.setImageResource(R.drawable.loading);
        ImageDownloader task=new ImageDownloader(imageView);
        try{
            task.execute(url);
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    }*/

    private void downloadImage(ImageView imageView, String url){

        Glide
                .with(this)
                .load(url)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .skipMemoryCache(TRUE)
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.noimage))
                .into(imageView);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_algae);

        algaeView = findViewById(R.id.AlgaeView);

        ConnectionDetector connectionDetector = new ConnectionDetector(this,"google.com");
        if (!connectionDetector.isInternetAvailable())
            Snackbar.make(algaeView,"No Internet Connection detected",Snackbar.LENGTH_LONG).show();

        requestManager=Glide.with(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        LinearLayout linearLayoutBBA=findViewById(R.id.BBALinear);
        ImageView imageViewBBA=findViewById(R.id.imageViewbba);
        downloadImage(imageViewBBA,"https://live.staticflickr.com/65535/49556123617_42d4e30e95_o.jpg");
        algaeImageViewVisible.put("BBA",false);

        expandImage(linearLayoutBBA,imageViewBBA,"BBA");


        LinearLayout linearLayoutGSA=findViewById(R.id.GSALinear);
        ImageView imageViewGSA=findViewById(R.id.imageViewgsa);
        downloadImage(imageViewGSA,"https://live.staticflickr.com/65535/49556163717_d43f752ab5_o.jpg");
        algaeImageViewVisible.put("GSA",false);

        expandImage(linearLayoutGSA,imageViewGSA,"GSA");

        LinearLayout linearLayoutStag=findViewById(R.id.StagLinear);
        ImageView imageViewStag=findViewById(R.id.imageViewstag);
        downloadImage(imageViewStag,"https://live.staticflickr.com/65535/49588926102_b6665fda3c_o.jpg");
        algaeImageViewVisible.put("Stag",false);

        expandImage(linearLayoutStag,imageViewStag,"Stag");

        LinearLayout linearLayoutRhizo=findViewById(R.id.RhizoLinear);
        ImageView imageViewRhizo=findViewById(R.id.imageViewRhizo);
        downloadImage(imageViewRhizo,"https://live.staticflickr.com/65535/49555889091_382eff6378_o.jpg");
        algaeImageViewVisible.put("Rhizo",false);

        expandImage(linearLayoutRhizo,imageViewRhizo,"Rhizo");

        LinearLayout linearLayoutBGA=findViewById(R.id.BGALinear);
        ImageView imageViewBGA=findViewById(R.id.imageViewBGA);
        downloadImage(imageViewBGA,"https://live.staticflickr.com/65535/49588680751_18a2dde966_o.jpg");
        algaeImageViewVisible.put("BGA",false);

        expandImage(linearLayoutBGA,imageViewBGA,"BGA");


        LinearLayout linearLayoutGW=findViewById(R.id.GWLinear);
        ImageView imageViewGW=findViewById(R.id.imageViewGW);
        downloadImage(imageViewGW,"https://live.staticflickr.com/65535/49556124597_a09355fa2b_o.jpg");
        algaeImageViewVisible.put("GW",false);

        expandImage(linearLayoutGW,imageViewGW,"GW");

        LinearLayout linearLayoutClado=findViewById(R.id.CladoLinear);
        ImageView imageViewClado=findViewById(R.id.imageViewClado);
        downloadImage(imageViewClado,"https://live.staticflickr.com/65535/49555391443_07ce27ac0b_o.jpg");
        algaeImageViewVisible.put("Clado",false);

        expandImage(linearLayoutClado,imageViewClado,"Clado");

        LinearLayout linearLayoutBDA=findViewById(R.id.BDALinear);
        ImageView imageViewBDA=findViewById(R.id.imageViewBDA);
        downloadImage(imageViewBDA,"https://live.staticflickr.com/65535/49556124902_81770a6c80_o.jpg");
        algaeImageViewVisible.put("BDA",false);

        expandImage(linearLayoutBDA,imageViewBDA,"BDA");

        LinearLayout linearLayoutThread=findViewById(R.id.ThreadLinear);
        ImageView imageViewThread=findViewById(R.id.imageViewThread);
        downloadImage(imageViewThread,"https://live.staticflickr.com/65535/49556124077_755ddcc7a9_o.jpg");
        algaeImageViewVisible.put("Thread",false);

        expandImage(linearLayoutThread,imageViewThread,"Thread");

        LinearLayout linearLayoutGDA=findViewById(R.id.GDALinear);
        ImageView imageViewGDA=findViewById(R.id.imageViewGDA);
        downloadImage(imageViewGDA,"https://live.staticflickr.com/65535/49555888796_08431918ea_o.jpg");
        algaeImageViewVisible.put("GDA",false);

        expandImage(linearLayoutGDA,imageViewGDA,"GDA");


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
