package eu.letmehelpu.android.login;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import dagger.internal.Factory;
import eu.letmehelpu.android.Network;
import eu.letmehelpu.android.TestApi;
import eu.letmehelpu.android.login.entity.AuthenticationGateway;
import eu.letmehelpu.android.network.GenericServerError;
import eu.letmehelpu.android.network.InvalidField;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticationGatewayImpl implements AuthenticationGateway {
    
    private final TestApi testApi;

    public AuthenticationGatewayImpl(TestApi testApi) {
        this.testApi = testApi;
    }

    @Override
    public Single<String> authenticate(final String username, final String password) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(final SingleEmitter<String> emitter) {
                final Call<Map> call = testApi.login(username, password);
                emitter.setDisposable(new Disposable() {
                    @Override
                    public void dispose() {
                        call.cancel();
                    }

                    @Override
                    public boolean isDisposed() {
                        return call.isCanceled();
                    }
                });
                call.enqueue(new Callback<Map>() {
                    @Override
                    public void onResponse(Call<Map> call, Response<Map> response) {
                        ResponseBody errorBody = response.errorBody();
                        if(errorBody != null) {
                            List<InvalidField> invalidFields = new Gson().fromJson(errorBody.charStream(), TypeToken.getParameterized(List.class, InvalidField.class).getType());
                            String message = invalidFields.get(0).getMessage();
                            emitter.tryOnError(new Exception(message));
                        } else {
                            emitter.onSuccess((String) response.body().get("access_token"));
                        }
                    }

                    @Override
                    public void onFailure(Call<Map> call, Throwable t) {
                        emitter.tryOnError(t);
                    }
                });
            }
        });
    }

    @Override
    public Single<String> authenticateByOAuth(final String source, final String id) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(final SingleEmitter<String> emitter) {
                final Call<Map> call = testApi.loginByOAuth(source, id);
                emitter.setDisposable(new Disposable() {
                    @Override
                    public void dispose() {
                        call.cancel();
                    }

                    @Override
                    public boolean isDisposed() {
                        return call.isCanceled();
                    }
                });
                call.enqueue(new Callback<Map>() {
                    @Override
                    public void onResponse(Call<Map> call, Response<Map> response) {
                        ResponseBody errorBody = response.errorBody();
                        if(errorBody != null) {
                            String error;
                            try {
                                error = new String(errorBody.bytes());
                            } catch (IOException e) {
                                emitter.tryOnError(e);
                                return;
                            }
                            try {
                                List<InvalidField> invalidFields = new Gson().fromJson(error, TypeToken.getParameterized(List.class, InvalidField.class).getType());
                                String message = invalidFields.get(0).getMessage();
                                emitter.tryOnError(new Exception(message));
                            } catch (Exception e) {
                                try {
                                    GenericServerError serverError = new Gson().fromJson(error, GenericServerError.class);
                                    emitter.tryOnError(new Exception(serverError.getMessage()));
                                } catch (Exception e1) {
                                    emitter.tryOnError(e1);
                                }
                            }
                        } else {
                            emitter.onSuccess((String) response.body().get("access_token"));
                        }
                    }

                    @Override
                    public void onFailure(Call<Map> call, Throwable t) {
                        emitter.tryOnError(t);
                    }
                });
            }
        });
    }
}
