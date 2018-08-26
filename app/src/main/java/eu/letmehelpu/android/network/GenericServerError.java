package eu.letmehelpu.android.network;

import com.google.gson.annotations.SerializedName;

public class GenericServerError {
    @SerializedName("name") String name;
    @SerializedName("message") String message;
    @SerializedName("code") int code;
    @SerializedName("status") int status;

    public String getMessage() {
        return message;
    }
}
