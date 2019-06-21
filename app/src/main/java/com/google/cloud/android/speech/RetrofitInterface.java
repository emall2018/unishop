package com.google.cloud.android.speech;

/**
 * Created by ASUS on 11/05/2019.
 */

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

public interface RetrofitInterface {

    @POST("users")
    Observable<Response> register(@Body User user);

    @POST("authenticate")
    Observable<Response> login();

    @GET("users/{email}")
    Observable<User> getProfile(@Path("email") String email);

    @GET("userProduct/{email}")
    Observable<User> getProduct(@Path("email") String email);

    @GET("/users/search/{category}")
    Observable<User> searchByCategory(@Path("category") String category);

     @PUT("users/{email}")

    Observable<Response> changePassword(@Path("email") String email, @Body User user);

    @PUT("users/settings/{email}")
    Observable<Response> ChangeSettings(@Path("email") String email, @Body User user);

    @POST("users/{email}/password")
    Observable<Response> resetPasswordInit(@Path("email") String email);

    @POST("users/{email}/password")
    Observable<Response> resetPasswordFinish(@Path("email") String email, @Body User user);

    @POST("AddProduct/{email}")
    Observable<Response> UploadProduct(@Path("email") String email,@Body Product product );

    @Multipart
    @POST("AddProduct/Image/{email}")
    Observable<Response> UploadImage(@Path("email") String email, @Part MultipartBody.Part image);
}