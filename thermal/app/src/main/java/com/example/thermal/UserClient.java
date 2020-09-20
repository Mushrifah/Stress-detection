package com.example.thermal;

import com.example.thermal.model.Data;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserClient {
    @POST("/calculate")
    Call <ResponseBody> reviveData(@Body Data data);
    // Call<ResponseBody> sendMessage(@Body String dry);

}
