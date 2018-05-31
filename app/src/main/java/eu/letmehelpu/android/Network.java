package eu.letmehelpu.android;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {
    private static Network instance;

    private TestApi testApi;
    private String token;
    UserRepository userRepository;
    private Network(Application application) {
        userRepository = new UserRepository(application.getSharedPreferences("userRepository", Context.MODE_PRIVATE));

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        client.networkInterceptors().add(REWRITE_CACHE_CONTROL_INTERCEPTOR);

        //setup cache
        File httpCacheDirectory = new File(application.getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        builder.cache(cache);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = builder
                .authenticator(new Authenticator() {
                    @javax.annotation.Nullable
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {

                        if(!userRepository.isLogged()) {
                            return null;
                        }

                        LoggedUser loggedUser = userRepository.getLoggedUser();
                        retrofit2.Response<Map> loginResponse;
                        if(loggedUser.getLoggedWith() == LoggedUser.LOGGED_WITH_APP) {
                            loginResponse = testApi.login(loggedUser.getUserName(), loggedUser.getPassword()).execute();
                        } else if(loggedUser.getLoggedWith() == LoggedUser.LOGGED_WITH_GOOGLE) {
                            loginResponse = testApi.loginByOAuth("google", loggedUser.getOauthId()).execute();
                        } else if(loggedUser.getLoggedWith() == LoggedUser.LOGGED_WITH_FACEBOOK) {
                            loginResponse = testApi.loginByOAuth("facebook", loggedUser.getOauthId()).execute();
                        } else {
                            throw new IllegalStateException("wrong logged with");
                        }

                        System.out.println("Authenticating for response: " + response);
                        System.out.println("Challenges: " + response.challenges());
                        Map responseBody = loginResponse.body();
                        if (loginResponse.isSuccessful() && responseBody != null) {
                            token = (String) responseBody.get("access_token");
                            return response.request().newBuilder()
                                    .header("Authorization", "Bearer " + token)
                                    .build();
                        } else {
                            //you have been logged out
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
                                .addHeader("Authorization", "Bearer " + token)
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .addInterceptor(logging)
                .build();



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://letmehelpu-v2.preview.cloudart.pl/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        testApi = retrofit.create(TestApi.class);
    }

    public void claerToken() {
        token = null;
    }

    public static void init(Application application) {
        instance = new Network(application);
    }

    public static Network getInstance() {
        return instance;
    }
    public TestApi getTestApi() {
        return testApi;
    }

}
