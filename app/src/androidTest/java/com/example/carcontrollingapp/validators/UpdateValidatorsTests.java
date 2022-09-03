package com.example.carcontrollingapp.validators;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.example.carcontrollingapp.models.User.saveUserCredentials;
import static com.example.carcontrollingapp.utils.Tools.assertUserIsNotInAPIDatabase;
import static com.example.carcontrollingapp.utils.Tools.createUserAPIDatabase;
import static com.example.carcontrollingapp.utils.Tools.deleteUserAPIDatabase;
import static com.example.carcontrollingapp.utils.Tools.fillAndSubmitAccountForm;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.activities.UITests;
import com.example.carcontrollingapp.models.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class UpdateValidatorsTests extends UITests {
    private final User testUser
            = new User("AndroidTestUser", "android.test.user@test.com", "android-test-password");
    private final User testUser2 = new User("AndroidTestUser2", "android.test.user2@test.com", testUser.getPassword());

    @Before
    public void setup() throws IOException {
        createUserAPIDatabase(testUser);
        createUserAPIDatabase(testUser2);
        saveUserCredentials(context, testUser.getUsername(), testUser.getEmail(), testUser.getPassword());
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
        testInvalidUpdateAccount(
                testUser2.getUsername(),
                "android.test.user.updated@test.com",
                "android-test-password-updated",
                "android-test-password-updated");
    }

    @Test
    public void testUpdateAccountWithRepeatedEmail() throws IOException, InterruptedException {
        testInvalidUpdateAccount(
                "AndroidTestUserUpdated",
                testUser2.getEmail(),
                "android-test-password-updated",
                "android-test-password-updated");
    }

    /**
     * Submits the update form with the specified credentials and verifies that the update did not happen.
     * @param username value for the username field
     * @param email value for the email field
     * @param password value for the password field
     * @param passwordConfirmation value for the password confirmation field
     * @throws IOException throws an IOException if some I/O problem due to HTTP request happens
     * @throws InterruptedException throws an InterruptedException in case of some problem due to making the thread sleep
     */
    private void testInvalidUpdateAccount(String username, String email, String password, String passwordConfirmation)
            throws IOException, InterruptedException {
        // Accessing the update account form
        onView(withId(R.id.action_account)).perform(click());
        onView(withId(R.id.update_account_button)).perform(click());
        // Filling the form with the new credentials
        fillAndSubmitAccountForm(username, email, password, passwordConfirmation);
        Thread.sleep(5000);
        // Verifying if the user was not created in the API database
        assertUserIsNotInAPIDatabase(username, password);
    }

    @After
    public void tearDown() throws IOException {
        // Deleting shared preferences
        context.deleteSharedPreferences(context.getString(R.string.sp_filename));
        // Deleting test user in the API database
        deleteUserAPIDatabase(testUser);
        deleteUserAPIDatabase(testUser2);
    }

}
