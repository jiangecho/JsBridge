package com.echo.jsbridge;

import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jiangecho on 16/6/19.
 */
public class TestJsModule implements JsModule {
    public static void showToast(WebView webView, JSONObject param) {
        Toast.makeText(webView.getContext(), "get hello", Toast.LENGTH_SHORT).show();
    }

    public static void showToast(WebView webView, JSONObject param, final JsCallback jsCallback) {
        String message = param.optString("msg");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (null != jsCallback) {
                    try {
                        JSONObject object = new JSONObject();
                        object.put("key", "value");
                        object.put("key1", "value1");
                        jsCallback.apply(getJSONObject(0, "ok", object));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        Toast.makeText(webView.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private static JSONObject getJSONObject(int code, String msg, JSONObject result) {
        JSONObject object = new JSONObject();
        try {
            object.put("code", code);
            object.put("msg", msg);
            object.putOpt("result", result);
            return object;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
