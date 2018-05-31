package eu.letmehelpu.android;

import java.util.List;
import java.util.Map;

import eu.letmehelpu.android.network.OfferItem;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface TestApi {
       //https://letmehelpu-v2.preview.cloudart.pl/api/v1/transactions/hire?access-token=PmHASDL_8a2lqfD-bEoG2TL0gnwW5tTMSjwiq4yBnq3pZje_EKQb2c7xJwZ14NAT

    @GET("v1/transactions/work?expand=contractor")
    Call<List<OfferItem>> work();

    @POST("v1/auth/login")
    @FormUrlEncoded
    Call<Map> login(@Field("username") String userName, @Field("password") String password);

    @POST("v1/auth/login-by-oauth")
    @FormUrlEncoded
    Call<Map> loginByOAuth(@Field("source") String source, @Field("source_id") String sourceId);

}
