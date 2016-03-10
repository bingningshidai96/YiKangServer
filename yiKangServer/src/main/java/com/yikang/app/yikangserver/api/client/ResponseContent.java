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

    public static ResponseContent toResponseContent(String result)
            throws Exception {
        return toResponseContent(result, true);
    }

    /**
     *
     * @param result
     *            结果字符串
     * @param isAES
     *            结果是否加密
     * @return
     */
    protected static ResponseContent toResponseContent(String result, boolean isAES)
            throws Exception {
        JSONObject object;
        ResponseContent resposeContent = null;
        try {
            object = new JSONObject(result);
            String status = object.getString("status");
            String message = object.getString("message");
            resposeContent = new ResponseContent();
            resposeContent.setStatus(status);
            resposeContent.setMessage(message);
        } catch (Exception e) {
            throw new Exception("retult 格式不正确");
        }
        if (object != null) { // 当object不为空，即前面不发生异常
            String srcData = null;
            try {
                if (!object.isNull("data")) {
                    srcData = object.getString("data");
                    String data = isAES ? AES.decrypt(srcData, AES.getKey()): srcData;
                    resposeContent.setData(data);
                }
            } catch (JSONException e) {
                new Exception("数据加解密出现错误", e).printStackTrace();
            }
        }
        return resposeContent;
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
