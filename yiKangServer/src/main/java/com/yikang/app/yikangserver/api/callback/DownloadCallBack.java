package com.yikang.app.yikangserver.api.callback;

/**
 * Created by liu on 16/3/9.
 */
public interface DownloadCallback extends ResponseCallback<Void> {
    String STATUS_DOWNLOAD_FAIL = "99997";
    void onProgress(long total, long current);
}
