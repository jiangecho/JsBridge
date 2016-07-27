package com.echo.jsbridge;

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebView;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by jiangecho on 16/6/19.
 */
public class JsBridge {
    private static Map<String, HashSet<Method>> exposedMethods = new HashMap<>();

    public static void register(String moduleName, Class<? extends JsModule> clazz) {
        if (!exposedMethods.containsKey(moduleName)) {
            try {
                exposedMethods.put(moduleName, getAllMethod(clazz));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void unRegister(String moduleName) {
        exposedMethods.remove(moduleName);
    }

    private static HashSet<Method> getAllMethod(Class injectedCls) throws Exception {
        HashSet<Method> mMethodsMap = new HashSet<>();
        Method[] methods = injectedCls.getDeclaredMethods();
        for (Method method : methods) {
            if (JsMethodType.isValidJsMethod(method)) {
                mMethodsMap.add(method);
            }
        }
        return mMethodsMap;
    }

    public static String callJava(Object container, WebView webView, String uriString) {
        String methodName = "";
        String className = "";
        String param = "{}";
        int port = 0;
        if (!TextUtils.isEmpty(uriString) && uriString.startsWith("JSBridge")) {
            Uri uri = Uri.parse(uriString);
            className = uri.getHost();
            String tmpParam = uri.getQuery();
            port = uri.getPort();
            String path = uri.getPath();
            if (!TextUtils.isEmpty(path)) {
                methodName = path.replace("/", "");
            }
            param = TextUtils.isEmpty(tmpParam) ? param: tmpParam;
        }


        if (exposedMethods.containsKey(className)) {
            HashSet<Method> methodHashMap = exposedMethods.get(className);

            for (Method method : methodHashMap) {
                if (method.getName().equals(methodName)) {
                    try {
                        switch (JsMethodType.getJsMethodType(method)) {
                            case C_W_J_C:
                                method.invoke(null, container, webView, new JSONObject(param), new JsCallback(webView, port));
                                return null;
                            case C_W_J:
                                method.invoke(null, container, webView, new JSONObject(param));
                                return null;
                            case W_J_C:
                                method.invoke(null, webView, new JSONObject(param), new JsCallback(webView, port));
                                return null;
                            case W_J:
                                method.invoke(null, webView, new JSONObject(param));
                                return null;
                            default:
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }
}

