package com.yikang.app.yikangserver.utils;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.yikang.app.yikangserver.bean.RequestParam;
import com.yikang.app.yikangserver.bean.ResponseContent;

import java.io.IOException;

/**
 * Created by liu on 15/12/27.
 */
public class ExampleApiHttpClinet {
    public static final String API_HOST = ""; //API主机
    public static final String FIEL_HOST = ""; //文件服务器主机
    private static final String TAG = "ExampleApiHttpClinet";

    private static OkHttpClient client;


    public static void post(String url, RequestParam param,
                            final ApiClient.ResponceCallBack callBack) {
        post(url, param, callBack, true);
    }

    public static void post(String url, RequestParam param,
                            final ApiClient.ResponceCallBack callBack,
                            boolean isEncript) {

        Request request = new Request.Builder()
                .post(buildBody(param))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                //处理网络错误
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String result = response.body().string();
                try {
                    ResponseContent content = ResponseContent.toResposeContent(result, true);
                    if (ResponseContent.STATUS_OK.equals(content.getStatus())) {
                        callBack.onSuccess(content);
                    } else {
                        callBack.onFialure(content.getStatus(), content.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 传入一个RequestParam，构建RequestBody
     *
     * @param param 需要构建的RequestParam
     * @return 一个构建好的RequestParam
     */
    private static RequestBody buildBody(RequestParam param) {
        MultipartBuilder builder = new MultipartBuilder();

        if (param.getAppId() != null) {
            builder.addFormDataPart(RequestParam.KEY_APPID, param.getAppId());
        }
        if (param.getAccessTicket() != null) {
            builder.addFormDataPart(RequestParam.KEY_ACCESS_TICKET, param.getAccessTicket());
        }
        if (param.getMachineCode() != null) {
            builder.addFormDataPart(RequestParam.KEY_MACHINECODE, param.getMachineCode());
        }
        if (!param.isParamEmpty()) {
            LOG.i(TAG,param.getParamJson());
            builder.addFormDataPart(RequestParam.KEY_PARAM_DATA, encript(param.getParamJson()));
        }

        return builder.build();
    }


    private static String encript(String json) {
        try {
            return AES.encrypt(json, AES.getKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
