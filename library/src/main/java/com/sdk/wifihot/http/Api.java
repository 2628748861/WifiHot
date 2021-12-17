package com.sdk.wifihot.http;

import io.reactivex.Observable;
import retrofit2.http.*;

import java.util.HashMap;

public interface Api {
    @GET("public-ap-authorize-key")
    Observable<BaseResponse<Result>> req(@QueryMap HashMap<String,String> map);
}
