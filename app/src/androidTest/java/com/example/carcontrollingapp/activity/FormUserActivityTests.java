package com.example.carcontrollingapp.activity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.carcontrollingapp.models.User.isUserAuthenticated;
import static com.example.carcontrollingapp.models.User.saveUserCredentials;
import static com.example.carcontrollingapp.retrofit.CarControllerAPIHelper.getAuthToken;
import static com.example.carcontrollingapp.utils.Tools.createUserAPIDatabase;
import static com.example.carcontrollingapp.utils.Tools.deleteUserAPIDatabase;
import static com.example.carcontrollingapp.utils.Tools.fillAndSubmitAccountForm;
import static org.hamcrest.CoreMatchers.allOf;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.activities.MainActivity;
import com.example.carcontrollingapp.models.User;
import com.example.carcontrollingapp.retrofit.CarControllerAPIHelperTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

@RunWith(AndroidJUnit4.class)
public class FormUserActivityTests {
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
    }

    @Test
    public void testSignup() throws IOException, InterruptedException {
        mainActivityRule.launchActivity(new Intent());

        onView(withId(R.id.action_account)).perform(click());
        onView(withId(R.id.create_account_link)).perform(click());
        // Filling the signup form
        fillAndSubmitAccountForm(testUser.getUsername(), testUser.getEmail(), testUser.getPassword(), testUser.getPassword());
        Thread.sleep(5000);
        // Verifying if the user was created in the API database
        Call<User> loginCall = new CarControllerAPIHelperTest().getApiObject()
                .login(getAuthToken(testUser.getUsername(), testUser.getPassword()));
        Response<User> response = loginCall.execute();
        if (!response.isSuccessful()){
            Assert.fail("Login call did not return 200 status code");
        }

        User returnedUser = response.body();

        if (returnedUser == null){
            Assert.fail("No user returned by login request");
        }

        Assert.assertEquals(testUser.getEmail(), returnedUser.getEmail());
        Assert.assertEquals(testUser.getUsername(), returnedUser.getUsername());
    }

    @Test
    public void testUpdateAccount() throws IOException, InterruptedException {
        String newUsername = "AndroidTestUserUpdated";
        String newEmail = "android.test.user.updated@test.com";
        String newPassword = "android-test-password-updated";

        User newUser = new User(newUsername, newEmail, newPassword);

        deleteUserAPIDatabase(newUser);
        createUserAPIDatabase(testUser);

        saveUserCredentials(context, testUser.getUsername(), testUser.getEmail(), testUser.getPassword());

        mainActivityRule.launchActivity(new Intent());

        onView(withId(R.id.action_account)).perform(click());
        onView(withId(R.id.update_account_button)).perform(click());

        // Filling the form with the new credentials
        fillAndSubmitAccountForm(newUsername, newEmail, newPassword, newPassword);
        // Waiting for FormUserActivity to be dismissed
        Thread.sleep(5000);
        // Verifying if the updated credentials are shown in the ProfileActivity
        onView(allOf(withId(R.id.username_text_view), withText(context.getString(R.string.username_label) + ": " + newUsername)))
                .check(matches(isDisplayed()));
        onView(allOf(withId(R.id.email_text_view), withText(context.getString(R.string.email_label) + ": " + newEmail)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testUpdateNonExistingUser() throws InterruptedException {
        String newUsername = "AndroidTestUserUpdated";
        String newEmail = "android.test.user.updated@test.com";
        String newPassword = "android-test-password-updated";

        saveUserCredentials(context, testUser.getUsername(), testUser.getEmail(), testUser.getPassword());

        mainActivityRule.launchActivity(new Intent());

        onView(withId(R.id.action_account)).perform(click());
        onView(withId(R.id.update_account_button)).perform(click());

        // Filling the form with the new credentials
        fillAndSubmitAccountForm(newUsername, newEmail, newPassword, newPassword);
        // Waiting for FormUserActivity to be dismissed
        Thread.sleep(5000);
        // Verifies if the user returned to the MainActivity
        onView(withId(R.id.action_synchro)).check(matches(isDisplayed()));
        onView(withId(R.id.action_account)).check(matches(isDisplayed()));
        onView(withId(R.id.action_logout)).check(doesNotExist());
        // Verifies that the credentials were deleted from SharedPreferences
        Assert.assertFalse(isUserAuthenticated(context));
    }

    @After
    public void tearDown() throws IOException {
        // Deleting shared preferences
        context.deleteSharedPreferences(context.getString(R.string.sp_filename));
        // Deleting test user in the API database
        deleteUserAPIDatabase(testUser);
    }
}
