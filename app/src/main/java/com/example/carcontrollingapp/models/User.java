package com.example.carcontrollingapp.models;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.carcontrollingapp.R;

public class User {
    private String username;
    private String email;
    private String password;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Verifies if the users are authenticated, i.e. if their credentials are in the Shared Preferences file.
     * @param context Context of the activity calling this method.
     * @return a boolean indicating if the user is authenticated.
     */
    public static boolean isUserAuthenticated(Context context){
        SharedPreferences sp = context.
                getSharedPreferences(context.getString(R.string.sp_filename), MODE_PRIVATE);

        return (sp.getString(context.getString(R.string.sp_username),null) != null) &&
                (sp.getString(context.getString(R.string.sp_email),null) != null) &&
                (sp.getString(context.getString(R.string.sp_password),null) != null);
    }
    /**
     * Saves the specified user credentials(username, email and password) in the Shared Preferences file.
     * @param context Context of the activity calling this method
     * @param username username to be saved.
     * @param email email to be saved.
     * @param password password to be saved.
     */
    public static void saveUserCredentials(Context context, String username, String email, String password){
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_filename), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(context.getString(R.string.sp_username), username);
        editor.putString(context.getString(R.string.sp_email), email);
        editor.putString(context.getString(R.string.sp_password), password);

        editor.apply();
    }
    /**
     * Deletes user credentials(username, email and password) from Shared Preferences.
     * @param context Context of the activity calling this method.
     */
    public static void deleteUserCredentials(Context context){
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_filename), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.remove(context.getString(R.string.sp_username));
        editor.remove(context.getString(R.string.sp_email));
        editor.remove(context.getString(R.string.sp_password));

        editor.apply();
    }
}
