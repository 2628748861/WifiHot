package com.sdk.wifihot.http;

import android.util.Log;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.nio.charset.Charset;

public class LoggingInterceptor implements Interceptor {
    private final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        Log.e("WifiActionImp",""+request.url());

        Response response = chain.proceed(request);

        return response;

    }
}
