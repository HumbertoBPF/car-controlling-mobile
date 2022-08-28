package com.example.carcontrollingapp.validators;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.example.carcontrollingapp.models.User.saveUserCredentials;
import static com.example.carcontrollingapp.retrofit.CarControllerAPIHelper.getAuthToken;
import static com.example.carcontrollingapp.utils.Tools.createUserAPIDatabase;
import static com.example.carcontrollingapp.utils.Tools.deleteUserAPIDatabase;
import static com.example.carcontrollingapp.utils.Tools.fillAndSubmitAccountForm;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.activities.MainActivity;
import com.example.carcontrollingapp.models.User;
import com.example.carcontrollingapp.retrofit.CarControllerAPI;
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
public class UpdateValidatorsTests {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule
            = new ActivityTestRule<>(MainActivity.class, true, false);
    private final Context context = ApplicationProvider.getApplicationContext();
    private final CarControllerAPI carControllerAPI = new CarControllerAPIHelperTest().getApiObject();
    private final User testUser
            = new User("AndroidTestUser", "android.test.user@test.com", "android-test-password");
    private final User testUser2 = new User("AndroidTestUser2", "android.test.user2@test.com", testUser.getPassword());

    @Before
    public void setup() throws IOException {
        // Deleting shared preferences
        context.deleteSharedPreferences(context.getString(R.string.sp_filename));
        // Deleting test user in the API database
        deleteUserAPIDatabase(testUser);
    }

    @Test
    public void testUpdateAccountNoEmail() throws IOException, InterruptedException {
        testInvalidUpdateAccount("AndroidTestUserUpdated",
                "",
                "android-test-password-updated",
                "android-test-password-updated");
    }

    @Test
    public void testUpdateAccountNoUsername() throws IOException, InterruptedException {
        testInvalidUpdateAccount("",
                "android.test.user.updated@test.com",
                "android-test-password-updated",
                "android-test-password-updated");
    }

    @Test
    public void testUpdateAccountNoPassword() throws IOException, InterruptedException {
        testInvalidUpdateAccount("AndroidTestUserUpdated",
                "android.test.user.updated@test.com",
                "",
                "");
    }

    @Test
    public void testUpdateAccountEmailWithSpace() throws IOException, InterruptedException {
        testInvalidUpdateAccount("AndroidTestUserUpdated",
                "android .test.user.updated@test.com",
                "android-test-password-updated",
                "android-test-password-updated");
    }

    @Test
    public void testUpdateAccountUsernameWithSpace() throws IOException, InterruptedException {
        testInvalidUpdateAccount("Android TestUserUpdated",
                "android.test.user.updated@test.com",
                "android-test-password-updated",
                "android-test-password-updated");
    }

    @Test
    public void testUpdateAccountPasswordWithSpace() throws IOException, InterruptedException {
        testInvalidUpdateAccount("AndroidTestUserUpdated",
                "android.test.user.updated@test.com",
                "android-test-password updated",
                "android-test-password updated");
    }

    @Test
    public void testUpdateAccountPasswordWithLengthOne() throws IOException, InterruptedException {
        testInvalidUpdateAccount("AndroidTestUserUpdated",
                "android.test.user.updated@test.com",
                "a",
                "a");
    }

    @Test
    public void testUpdateAccountPasswordWithLengthTwo() throws IOException, InterruptedException {
        testInvalidUpdateAccount("AndroidTestUserUpdated",
                "android.test.user.updated@test.com",
                "ab",
                "ab");
    }

    @Test
    public void testUpdateAccountPasswordWithLengthThree() throws IOException, InterruptedException {
        testInvalidUpdateAccount("AndroidTestUserUpdated",
                "android.test.user.updated@test.com",
                "abc",
                "abc");
    }

    @Test
    public void testUpdateAccountPasswordWithLengthFour() throws IOException, InterruptedException {
        testInvalidUpdateAccount("AndroidTestUserUpdated",
                "android.test.user.updated@test.com",
                "abcd",
                "abcd");
    }

    @Test
    public void testUpdateAccountPasswordWithLengthFive() throws IOException, InterruptedException {
        testInvalidUpdateAccount("AndroidTestUserUpdated",
                "android.test.user.updated@test.com",
                "abcde",
                "abcde");
    }

    @Test
    public void testUpdateAccountPasswordsDoNotMatch() throws IOException, InterruptedException {
        testInvalidUpdateAccount("AndroidTestUserUpdated",
                "android.test.user.updated@test.com",
                "android-test-password-updated",
                "android-test-password-update");
    }

    @Test
    public void testUpdateAccountWithRepeatedUsername() throws IOException, InterruptedException {
        testInvalidUpdateAccountWithRepeatedCredentials(
                testUser2.getUsername(),
                "android.test.user.updated@test.com",
                "android-test-password-updated",
                "android-test-password-updated");
    }

    @Test
    public void testUpdateAccountWithRepeatedEmail() throws IOException, InterruptedException {
        testInvalidUpdateAccountWithRepeatedCredentials(
                "AndroidTestUserUpdated",
                testUser2.getEmail(),
                "android-test-password-updated",
                "android-test-password-updated");
    }

    @SuppressWarnings("SameParameterValue")
    private void testInvalidUpdateAccountWithRepeatedCredentials(String newUsername, String newEmail,
                                                                 String newPassword, String newPasswordConfirmation)
            throws IOException, InterruptedException {
        deleteUserAPIDatabase(testUser2);
        createUserAPIDatabase(testUser2);
        testInvalidUpdateAccount(newUsername, newEmail, newPassword, newPasswordConfirmation);
        deleteUserAPIDatabase(testUser2);
    }

    private void testInvalidUpdateAccount(String newUsername, String newEmail, String newPassword,
                                          String newPasswordConfirmation) throws IOException, InterruptedException {
        User newUser = new User(newUsername, newEmail, newPassword);

        deleteUserAPIDatabase(newUser);

        createUserAPIDatabase(testUser);

        saveUserCredentials(context, testUser.getUsername(), testUser.getEmail(), testUser.getPassword());
        mainActivityRule.launchActivity(new Intent());

        onView(withId(R.id.action_account)).perform(click());
        onView(withId(R.id.update_account_button)).perform(click());

        // Filling the form with the new credentials
        fillAndSubmitAccountForm(newUsername, newEmail, newPassword, newPasswordConfirmation);
        Thread.sleep(5000);
        // Verifying if the user was created in the API database
        Call<User> loginCall = carControllerAPI.login(getAuthToken(newUsername, newPassword));
        Response<User> response = loginCall.execute();
        Assert.assertEquals(403, response.code());

        deleteUserAPIDatabase(newUser);
    }

    @After
    public void tearDown() throws IOException {
        // Deleting shared preferences
        context.deleteSharedPreferences(context.getString(R.string.sp_filename));
        // Deleting test user in the API database
        deleteUserAPIDatabase(testUser);
    }

}
