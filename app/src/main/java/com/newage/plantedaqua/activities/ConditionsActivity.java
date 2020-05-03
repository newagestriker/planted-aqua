package com.newage.plantedaqua.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.newage.plantedaqua.R;

public class ConditionsActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conditions);
        Intent i=getIntent();
        String websiteUrl=i.getStringExtra("URL");
        webView =findViewById(R.id.webView);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(websiteUrl);


       /* WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);*/
    }

    @Override
    public void onBackPressed() {
            if(webView.canGoBack())
                webView.goBack();
            else
                super.onBackPressed();
    }
}
