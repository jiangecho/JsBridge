package com.echo.jsbridge.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.echo.common.util.Utils;
import com.echo.jsbridge.TestJsModule;
import com.echo.jsbridge.InjectedChromeClient;
import com.echo.jsbridge.JsBridge;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webView);
        initWebView(webView);

        JsBridge.register("bridge", TestJsModule.class);
        webView.loadUrl("file:///android_asset/index.html");

    }

    private void initWebView(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:" + Utils.loadFileContentFromAsset(MainActivity.this, "JSBridge.js"));
                view.loadUrl("javascript:JSBridge.call('bridge','showToast',{'msg':'Hello JSBridge'},function(res){alert(JSON.stringify(res))});");

            }
        });

        webView.setWebChromeClient(new InjectedChromeClient());

    }
}
