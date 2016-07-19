package com.echo.jsbridge;

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebView;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by jiangecho on 16/6/19.
 */
public class JsBridge {
    private static Map<String, HashSet<Method>> exposedMethods = new HashMap<>();

    public static void register(String exposedName, Class<? extends JsModule> clazz) {
        if (!exposedMethods.containsKey(exposedName)) {
            try {
                exposedMethods.put(exposedName, getAllMethod(clazz));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static HashSet<Method> getAllMethod(Class injectedCls) throws Exception {
        HashSet<Method> mMethodsMap = new HashSet<>();
        Method[] methods = injectedCls.getDeclaredMethods();
        for (Method method : methods) {
            String name;
            if (method.getModifiers() != (Modifier.PUBLIC | Modifier.STATIC) || (name = method.getName()) == null) {
                continue;
            }
            Class[] parameters = method.getParameterTypes();
            if (parameters != null) {
                if (parameters.length == 3 && parameters[0] == WebView.class && parameters[1] == JSONObject.class && parameters[2] == JsCallback.class) {
                    mMethodsMap.add(method);
                } else if (parameters.length == 2 && parameters[0] == WebView.class && parameters[1] == JSONObject.class) {
                    mMethodsMap.add(method);
                }

            }
        }
        return mMethodsMap;
    }

    public static String callJava(WebView webView, String uriString) {
        String methodName = "";
        String className = "";
        String param = "{}";
        int port = 0;
        if (!TextUtils.isEmpty(uriString) && uriString.startsWith("JSBridge")) {
            Uri uri = Uri.parse(uriString);
            className = uri.getHost();
            param = uri.getQuery();
            port = uri.getPort();
            String path = uri.getPath();
            if (!TextUtils.isEmpty(path)) {
                methodName = path.replace("/", "");
            }
        }


        if (exposedMethods.containsKey(className)) {
            HashSet<Method> methodHashMap = exposedMethods.get(className);

            for (Method method : methodHashMap) {
                if (method.getName().endsWith(methodName)) {
                    try {
                        if (port == 0 && method.getParameterTypes().length == 2) {
                            method.invoke(null, webView, new JSONObject(param));
                        } else {
                            method.invoke(null, webView, new JSONObject(param), new JsCallback(webView, port));
                        }
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        return null;
    }
}

