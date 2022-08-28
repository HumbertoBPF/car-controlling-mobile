package com.example.carcontrollingapp.utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.example.carcontrollingapp.retrofit.CarControllerAPIHelper.getAuthToken;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.models.User;
import com.example.carcontrollingapp.retrofit.CarControllerAPIHelperTest;

import java.io.IOException;

import retrofit2.Call;

public class Tools {
    public static void createUserAPIDatabase(User user) throws IOException {
        Call<User> signupCall = new CarControllerAPIHelperTest().getApiObject().signup(user);
        signupCall.execute();
    }

    public static void deleteUserAPIDatabase(User user) throws IOException {
        Call<Void> deleteCall = new CarControllerAPIHelperTest().getApiObject()
                .deleteUser(getAuthToken(user.getUsername(), user.getPassword()));
        deleteCall.execute();
    }

    public static void fillAndSubmitAccountForm(String username, String email, String password, String passwordConfirmation) {
        onView(withId(R.id.email_edit_text)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.username_edit_text)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.password_edit_text)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.password_confirmation_edit_text)).perform(typeText(passwordConfirmation), closeSoftKeyboard());
        onView(withId(R.id.submit_button)).perform(click());
    }
}
