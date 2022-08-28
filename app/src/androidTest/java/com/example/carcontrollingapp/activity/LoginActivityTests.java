package com.example.carcontrollingapp.activity;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.example.carcontrollingapp.utils.Tools.createUserAPIDatabase;
import static com.example.carcontrollingapp.utils.Tools.deleteUserAPIDatabase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.activities.MainActivity;
import com.example.carcontrollingapp.models.User;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTests {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule
            = new ActivityTestRule<>(MainActivity.class, true, false);
    private final Context context = ApplicationProvider.getApplicationContext();
    private final User testUser
            = new User("AndroidTestUser", "android.test.user@test.com", "android-test-password");

    @Before
    public void setup() throws IOException {
        // Deleting shared preferences
        context.deleteSharedPreferences(context.getString(R.string.sp_filename));
        // Deleting test user in the API database
        deleteUserAPIDatabase(testUser);
        // Creates account which we are going to try to login
        createUserAPIDatabase(testUser);
    }

    @Test
    public void testSuccessfulLogin() throws IOException, InterruptedException {
        mainActivityRule.launchActivity(new Intent());

        onView(withId(R.id.action_account)).perform(click());
        onView(withId(R.id.username_edit_text)).perform(typeText(testUser.getUsername()), closeSoftKeyboard());
        onView(withId(R.id.password_edit_text)).perform(typeText(testUser.getPassword()), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        // Waits the LoginActivity to be dismissed
        Thread.sleep(5000);
        // Verifying if the logout button is visible
        onView(withId(R.id.action_logout)).check(matches(isDisplayed()));

        SharedPreferences sp = context.
                getSharedPreferences(context.getString(R.string.sp_filename), MODE_PRIVATE);
        String spUsername = sp.getString(context.getString(R.string.sp_username), null);
        String spEmail = sp.getString(context.getString(R.string.sp_email),null);
        String spPassword = sp.getString(context.getString(R.string.sp_password),null);

        Assert.assertEquals(testUser.getUsername(), spUsername);
        Assert.assertEquals(testUser.getEmail(), spEmail);
        Assert.assertEquals(testUser.getPassword(), spPassword);
    }

    @After
    public void tearDown() throws IOException {
        // Deleting shared preferences
        context.deleteSharedPreferences(context.getString(R.string.sp_filename));
        // Deleting test user in the API database
        deleteUserAPIDatabase(testUser);
    }

}
