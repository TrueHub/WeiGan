package com.youyi.weigan.net;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by user on 2017/4/21.
 */

public interface RetrofitItfc {

    //http://192.168.0.232:8080/weigan/upload/uploadData
    @POST("weigan/upload/uploadData")
    Call<ResponseBody> postUser(@Body RequestBody body);
}
