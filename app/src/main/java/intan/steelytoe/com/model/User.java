package intan.steelytoe.com.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fadlymunandar on 7/10/17.
 */

public class User {

    @SerializedName("email")
    private String email;
    @SerializedName("code")
    private String code;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
