package eu.letmehelpu.android.network;

import com.google.gson.annotations.SerializedName;

public class InvalidField {
    @SerializedName("field") String field;
    @SerializedName("message") String message;

    public String getMessage() {
        return message;
    }
}
