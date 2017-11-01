package com.xh.util.http.callback;

import java.util.List;

/**
 * HttpURLConnection网络请求返回监听器
 */
public interface  HttpCallbackModelListener<T> {
    // 网络请求成功
    void onFinish(List<T> response);

    // 网络请求失败
    void onError(Exception e);
}
