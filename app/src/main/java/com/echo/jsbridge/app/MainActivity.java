package com.echo.jsbridge.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.echo.jsbridge.InjectedChromeClient;
import com.echo.jsbridge.JsBridge;
import com.echo.jsbridge.TestJsModule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private static final String TAG = "MainActivity";

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
                view.loadUrl("javascript:" + loadFileContentFromAsset(MainActivity.this, "JSBridge.js"));
                view.loadUrl("javascript:JSBridge.call('bridge','showToast',{'msg':'Hello JSBridge'},function(res){alert(JSON.stringify(res))});");

            }
        });

        webView.setWebChromeClient(new InjectedChromeClient(this));

    }

    public static String loadFileContentFromAsset(Context context, String fileName) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = context.getAssets().open(fileName);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str = in.readLine()) != null) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error opening asset " + fileName);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing asset " + fileName);
                }
            }
        }

        return null;
    }
}
