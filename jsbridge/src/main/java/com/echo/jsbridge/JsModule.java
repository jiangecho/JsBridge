package com.echo.jsbridge;

/**
 * Created by jiangecho on 16/6/19.
 * <p>
 * <pre>
 * how
 *
 * This interface is just for proguard
 *
 * You must implement this interface
 * any method you want to be called from js, please follow the following method signature format
 *
 * <code> public static void showToast(Activity activity, WebView webView, JSONObject param, final JsCallback jsCallback) </code>
 * <code> </code>public static void showToast(Fragment fragment, WebView webView, JSONObject param) </code>
 * <code> public static void showToast(WebView webView, JSONObject param, final JsCallback jsCallback) </code>
 * <code> </code>public static void showToast(WebView webView, JSONObject param) </code>
 * </pre>
 */

public interface JsModule {
}
