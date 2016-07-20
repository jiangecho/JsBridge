##How to use

1. inject js

```
// override your WebViewClient or you include the js in web page
@Override
public void onPageFinished(WebView view, String url) {
    super.onPageFinished(view, url);
    view.loadUrl("javascript:" + Utils.loadFileContentFromAsset(context, "JSBridge.js"));
}
```

2. your ```WebChromeClient``` should extend ```InjectedChromeClient```

3. make you modules implement ```JsModule```

4. call ``` public static void register(String moduleName, Class<? extends JsModule> clazz) ``` to
register to modules.

5. call java method from js in the following format
    ```
    JSBridge.call('moduleName','methodName',{'msg':'parameter in json format'},function(res){callback(JSON.stringify(res))})
    ```
    if you not care about the return values, can call
    ```
    JSBridge.get('moduleName','methodName',{'msg':'parameter in json format'})
    ```
