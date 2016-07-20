package com.echo.jsbridge;

import android.app.Activity;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by jiangecho on 16/7/20.
 */

enum JsMethodType {
    C_W_J_C, //Container, WebView, JSONObject, JsCallback
    C_W_J, //Container, WebView, JSONObject
    W_J_C, //WebView, JSONObject, JsCallback
    W_J, //WebView, JSONObject
    UN_SUPPORT;

    static boolean isValidJsMethod(Method method) {
        if (method.getModifiers() != (Modifier.PUBLIC | Modifier.STATIC) || method.getName() == null) {
            return false;
        }
        return getJsMethodType(method) != UN_SUPPORT;
    }

    static JsMethodType getJsMethodType(Method method) {
        Class[] parameters = method.getParameterTypes();
        JsMethodType type = UN_SUPPORT;
        if (parameters != null) {
            switch (parameters.length) {
                case 4:
                    if ((parameters[0] == Activity.class || parameters[0] == AppCompatActivity.class
                            || parameters[0] == Fragment.class || parameters[0] == android.support.v4.app.Fragment.class)
                            && parameters[1] == WebView.class && parameters[2] == JSONObject.class && parameters[3] == JsCallback.class)
                        type = C_W_J_C;
                    break;
                case 3:
                    if ((parameters[0] == Activity.class || parameters[0] == AppCompatActivity.class
                            || parameters[0] == Fragment.class || parameters[0] == android.support.v4.app.Fragment.class)
                            && parameters[1] == WebView.class && parameters[2] == JSONObject.class) {
                        type = C_W_J;
                    } else if (parameters[0] == WebView.class && parameters[1] == JSONObject.class && parameters[2] == JsCallback.class) {
                        type = W_J_C;
                    }
                    break;
                case 2:
                    if (parameters[0] == WebView.class && parameters[1] == JSONObject.class) {
                        type = W_J;
                    }
                    break;
            }
        }
        return type;
    }
}

