package intan.steelytoe.com.util;

import android.content.Context;

import intan.steelytoe.com.model.User;

/**
 * Created by fadlymunandar on 7/10/17.
 */

public class UserManager {

    private static UserManager userManager;
    private Context context;
    private PrefManager prefManager;

    private UserManager(Context context) {
        this.context = context;
        prefManager = PrefManager.getInstance(context);
    }

    public static UserManager getInstance(Context context) {
        if (userManager == null) {
            userManager = new UserManager(context);
        }
        return userManager;
    }

    public User getUser() {
        User user = new User();

        String email = prefManager.getUserEmail();
        String code = prefManager.getUserCode();

        if (email == null || code == null) {
            //email = "digikomdev@gmail.com";
            //code = "pa9was";
            email = "fadlimunandar99@gmail.com";
            code = "b7rhs7";
            //email = "dki.hadihermawan@gmail.com";
            //code = "6f4il1";
        }

        user.setEmail(email);
        user.setCode(code);

        return user;
    }
}
