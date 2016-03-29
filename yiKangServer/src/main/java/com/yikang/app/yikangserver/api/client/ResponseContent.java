package com.yikang.app.yikangserver.api.client;

import org.json.JSONException;
import org.json.JSONObject;
import com.yikang.app.yikangserver.utils.AES;

public class ResponseContent<T> {
    public static final String STATUS_OK = "000000";

    private String status;
    private String message;
    private T data;


    public ResponseContent() {
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }


    public boolean isStautsOk() {
        return STATUS_OK.equals(status);
    }

    @Override
    public String toString() {
        return "ResponseContent2{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
