package eu.letmehelpu.android;

import android.app.Application;
import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import eu.letmehelpu.android.login.domain.ReAuthenticateUseCase;
import eu.letmehelpu.android.login.entity.LoginGateway;
import eu.letmehelpu.android.login.entity.SessionGateway;
import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {

    private final HttpLoggingInterceptor logging;
    private final Cache cache;

    public Network(
            Context application
    ) {

        //setup cache
        File httpCacheDirectory = new File(application.getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        cache = new Cache(httpCacheDirectory, cacheSize);


        logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
    }


    public <T> T createOpenApi(String baseUrl, Class<T> apiInterface) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cache(cache);

        OkHttpClient client = builder
                .followRedirects(true)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Request newRequest;

                        newRequest = request.newBuilder()
                                .addHeader("accept-language", Locale.getDefault().getLanguage())
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .addInterceptor(logging)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(apiInterface);
    }

    public <T> T createAuthenticatedOpenApi(
            String baseUrl,
            Class<T> apiInterface,
            final ReAuthenticateUseCase reAuthenticateUseCase,
            final LoginGateway loginGateway,
            final SessionGateway sessionGateway
    ) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cache(cache);

        OkHttpClient client = builder
                .authenticator(new Authenticator() {
                    @javax.annotation.Nullable
                    @Override
                    public Request authenticate(Route route, Response response) {
                        if (!loginGateway.isLoggedUser()) {
                            return null;
                        }
                        try {
                            reAuthenticateUseCase.reauthenticate().blockingAwait();

                            return response.request().newBuilder()
                                    .header("Authorization", "Bearer " + sessionGateway.getSessionToken())
                                    .build();
                        } catch (Exception e) {
                            return null;
                        }
                    }
                })
                .followRedirects(true)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Request newRequest;

                        newRequest = request.newBuilder()
                                .addHeader("accept-language", Locale.getDefault().getLanguage())
                                .addHeader("Authorization", "Bearer " + sessionGateway.getSessionToken())
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .addInterceptor(logging)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(apiInterface);
    }
}
