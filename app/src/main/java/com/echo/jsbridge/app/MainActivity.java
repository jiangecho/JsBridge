package com.echo.jsbridge.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.echo.common.util.Utils;
import com.echo.jsbridge.BridgeImpl;
import com.echo.jsbridge.JsBridge;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webView);
        initWebView(webView);

        JsBridge.register("bridge", BridgeImpl.class);
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

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                //return super.onJsPrompt(view, url, message, defaultValue, result);
                result.confirm(JsBridge.callJava(view, message));
                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                //return super.onJsAlert(view, url, message, result);
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setTitle("对话框")
                        .setMessage(message)
                        .setPositiveButton("确定", null);

                // 不需要绑定按键事件
                // 屏蔽keycode等于84之类的按键
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        Log.v("onJsAlert", "keyCode==" + keyCode + "event=" + event);
                        return true;
                    }
                });
                // 禁止响应按back键的事件
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
                result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
                return true;

            }

            @Override
            public View getVideoLoadingProgressView() {
                return super.getVideoLoadingProgressView();
            }
        });

    }
}
