package com.example.carcontrollingapp.validators;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.example.carcontrollingapp.utils.Tools.assertUserIsNotInAPIDatabase;
import static com.example.carcontrollingapp.utils.Tools.fillAndSubmitAccountForm;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.activities.UITests;
import com.example.carcontrollingapp.models.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class SignupValidatorsTests extends UITests {
    private final User testUser
            = new User("AndroidTestUser", "android.test.user@test.com", "android-test-password");

    @Test
    public void testSignupNoEmail() throws IOException, InterruptedException {
        testInvalidSignup(testUser.getUsername(), "", testUser.getPassword(), testUser.getPassword());
    }

    @Test
    public void testSignupNoUsername() throws IOException, InterruptedException {
        testInvalidSignup("", testUser.getEmail(), testUser.getPassword(), testUser.getPassword());
    }

    @Test
    public void testSignupNoPassword() throws IOException, InterruptedException {
        testInvalidSignup(testUser.getUsername(), testUser.getEmail(), "", "");
    }

    @Test
    public void testSignupUsernameWithSpace() throws IOException, InterruptedException {
        testInvalidSignup("t "+testUser.getUsername(), testUser.getEmail(), testUser.getPassword(), testUser.getPassword());
    }

    @Test
    public void testSignupEmailWithSpace() throws IOException, InterruptedException {
        testInvalidSignup(testUser.getUsername(), "t "+testUser.getEmail(), testUser.getPassword(), testUser.getPassword());
    }

    @Test
    public void testSignupPasswordWithSpace() throws IOException, InterruptedException {
        testInvalidSignup(testUser.getUsername(), testUser.getEmail(), "t "+testUser.getPassword(), "t "+testUser.getPassword());
    }

    @Test
    public void testSignupPasswordWithLengthOne() throws IOException, InterruptedException {
        testInvalidSignup(testUser.getUsername(), testUser.getEmail(), "a", "a");
    }

    @Test
    public void testSignupPasswordWithLengthTwo() throws IOException, InterruptedException {
        testInvalidSignup(testUser.getUsername(), testUser.getEmail(), "ab", "ab");
    }

    @Test
    public void testSignupPasswordWithLengthThree() throws IOException, InterruptedException {
        testInvalidSignup(testUser.getUsername(), testUser.getEmail(), "abc", "abc");
    }

    @Test
    public void testSignupPasswordWithLengthFour() throws IOException, InterruptedException {
        testInvalidSignup(testUser.getUsername(), testUser.getEmail(), "abcd", "abcd");
    }

    @Test
    public void testSignupPasswordWithLengthFive() throws IOException, InterruptedException {
        testInvalidSignup(testUser.getUsername(), testUser.getEmail(), "abcde", "abcde");
    }

    @Test
    public void testSignupPasswordDoNotMatch() throws IOException, InterruptedException {
        testInvalidSignup(testUser.getUsername(), testUser.getEmail(), testUser.getPassword(), "wrong-password");
    }

    private void testInvalidSignup(String username, String email, String password, String passwordConfirmation) throws IOException, InterruptedException {
        // Accessing the signup form
        onView(withId(R.id.action_account)).perform(click());
        onView(withId(R.id.create_account_link)).perform(click());

        fillAndSubmitAccountForm(username, email, password, passwordConfirmation);
        Thread.sleep(5000);
        // Verifying if the user was not created in the API database
        assertUserIsNotInAPIDatabase(username, password);
    }

}
