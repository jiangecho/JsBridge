package com.echo.jsbridge;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by jiangecho on 16/7/19.
 */
public class InjectedChromeClient extends WebChromeClient {
    private Object container;

    public InjectedChromeClient(Activity activity) {
        this.container = activity;
    }

    public InjectedChromeClient(AppCompatActivity appCompatActivity) {
        this.container = appCompatActivity;
    }

    public InjectedChromeClient(Fragment fragment) {
        this.container = fragment;
    }

    public InjectedChromeClient(android.app.Fragment fragment) {
        this.container = fragment;
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        //return super.onJsPrompt(view, url, message, defaultValue, result);
        result.confirm(JsBridge.callJava(container, view, message));
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
}
