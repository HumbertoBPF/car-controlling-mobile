package com.example.carcontrollingapp.activities;

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

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.models.User;
import com.example.carcontrollingapp.retrofit.CarControllerAPIHelperTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

@RunWith(AndroidJUnit4.class)
public class FormUserActivityTests extends UITests{
    private final User testUser
            = new User("AndroidTestUser", "android.test.user@test.com", "android-test-password");
    private final User updatedUser =
            new User("AndroidTestUserUpdated", "android.test.user.updated@test.com", "android-test-password-updated");

    @Test
    public void testSignup() throws IOException, InterruptedException {
        onView(withId(R.id.action_account)).perform(click());
        onView(withId(R.id.create_account_link)).perform(click());

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
        // Checking email and username returned by the API
        Assert.assertEquals(testUser.getEmail(), returnedUser.getEmail());
        Assert.assertEquals(testUser.getUsername(), returnedUser.getUsername());
    }

    @Test
    public void testUpdateExistingAccount() throws IOException, InterruptedException {
        createUserAPIDatabase(testUser);

        saveUserCredentials(context, testUser.getUsername(), testUser.getEmail(), testUser.getPassword());

        onView(withId(R.id.action_account)).perform(click());
        onView(withId(R.id.update_account_button)).perform(click());

        fillAndSubmitAccountForm(updatedUser.getUsername(), updatedUser.getEmail(), updatedUser.getPassword(), updatedUser.getPassword());
        // Waiting for FormUserActivity to be dismissed
        Thread.sleep(5000);
        // Verifying if the updated credentials are shown in the ProfileActivity
        onView(allOf(withId(R.id.username_text_view), withText(context.getString(R.string.username_label) + ": " + updatedUser.getUsername())))
                .check(matches(isDisplayed()));
        onView(allOf(withId(R.id.email_text_view), withText(context.getString(R.string.email_label) + ": " + updatedUser.getEmail())))
                .check(matches(isDisplayed()));

        deleteUserAPIDatabase(updatedUser);
    }

    @Test
    public void testUpdateNonExistingUser() throws InterruptedException {
        saveUserCredentials(context, testUser.getUsername(), testUser.getEmail(), testUser.getPassword());

        onView(withId(R.id.action_account)).perform(click());
        onView(withId(R.id.update_account_button)).perform(click());

        fillAndSubmitAccountForm(updatedUser.getUsername(), updatedUser.getEmail(), updatedUser.getPassword(), updatedUser.getPassword());
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
