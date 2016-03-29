package com.yikang.app.yikangserver.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;

import com.yikang.app.yikangserver.R;

/**
 * Created by liu on 16/3/28.
 */
public class WebActivity extends BaseActivity {
    public static final String EXTRA_TITLE ="title";
    public static final String EXTRA_URL ="url";
    private String title;
    private String url;

    private WebView webContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getIntent().getStringExtra(EXTRA_TITLE);
        url = getIntent().getStringExtra(EXTRA_URL);
        if(TextUtils.isEmpty(title)|| TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("必须传入一个title和url");
        }
        initContent();
        initTitleBar(title);

    }

    @Override
    protected void findViews() {
        webContent = ((WebView) findViewById(R.id.web_content));
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_web);
    }

    @Override
    protected void getData() {

    }

    @Override
    protected void initViewContent() {
        webContent.loadUrl(url);
    }
}
