package com.yikang.app.yikangserver.api.callback;

/**
 * Created by liu on 16/3/9.
 */
public interface ResponseCallback<T> {
    String STATUS_OK = "000000";
    String STATUS_NET_ERROR = "99999";
    String STATUS_DATA_ERROR = "99998";

    void onSuccess(T data);
    void onFailure(String status, String message);

}
