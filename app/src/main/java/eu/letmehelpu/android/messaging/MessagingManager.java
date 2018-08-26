package eu.letmehelpu.android.messaging;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import eu.letmehelpu.android.login.UserState;
import eu.letmehelpu.android.login.entity.LoginGateway;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagingManager implements MessagingTokenStoreage.MessagingTokenChanged {

    private final MessagingTokenStoreage messagingTokenStoreage;
    private Long userId;

    @SuppressLint("CheckResult")
    public MessagingManager(LoginGateway loginGateway, MessagingTokenStoreage messagingTokenStoreage) {
        loginGateway.loggedUser().subscribe(new Consumer<UserState>() {
            @Override
            public void accept(UserState userState) {
                if(userState.loggedIn) {
                    userId = userState.loggedUser.getUserDetails().getId();
                } else {
                    userId = null;
                }
                onUserIdChanged(userId);
            }
        });
        this.messagingTokenStoreage = messagingTokenStoreage;
    }

    public void putMessagingToken(@Nullable String token) {
        messagingTokenStoreage.updateMessagingToken(token, this);
    }

    private void onUserIdChanged(Long newUserId) {
        if(newUserId != null) {
            String token = messagingTokenStoreage.getMessagingToken();
            if(token != null) {
                sendUpdateToServer(newUserId, null, token);
            }
        }
    }

    @Override
    public void messagingTokenChanged(@Nullable String oldMessagingToken, String newMessagingToken) {
        if(newMessagingToken != null) {
            if(userId != null) {
                sendUpdateToServer(userId, oldMessagingToken, newMessagingToken);
            }
        }
    }

    private void sendUpdateToServer(@NonNull Long newUserId, @Nullable String oldToken, @NonNull String token) {
        new Helper().service.send(newUserId, token, oldToken).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("RADEK", "onResponse");
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d("RADEK", "ERROR");
            }
        });
    }
}
