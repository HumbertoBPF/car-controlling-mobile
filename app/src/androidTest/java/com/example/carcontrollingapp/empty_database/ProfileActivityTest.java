package com.example.carcontrollingapp.empty_database;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.carcontrollingapp.models.User.saveUserCredentials;
import static com.example.carcontrollingapp.utils.Tools.createUserAPIDatabase;
import static com.example.carcontrollingapp.utils.Tools.deleteUserAPIDatabase;
import static org.hamcrest.Matchers.not;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.models.User;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class ProfileActivityTest extends EmptyDatabaseTest{
    private final User testUser
            = new User("AndroidTestUser", "android.test.user@test.com", "android-test-password");

    @Before
    public void setup() throws IOException {
        super.setup();
        saveUserCredentials(context, testUser.getUsername(), testUser.getEmail(), testUser.getPassword());
        createUserAPIDatabase(testUser);
    }

    @Test
    public void testAccessProfileWithNoData(){
        onView(withId(R.id.action_account)).check(matches(isDisplayed()));
        onView(withId(R.id.action_synchro)).check(matches(isDisplayed()));
        onView(withId(R.id.action_logout)).check(matches(isDisplayed()));
        onView(withId(R.id.ads_recycler_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.rankings_button)).check(matches(isDisplayed()));
        onView(withId(R.id.warning_no_data_text_view)).check(matches(isDisplayed()));

        onView(withId(R.id.action_account)).perform(click());

        onView(Matchers.allOf(withId(R.id.username_text_view), withText("Username: "+testUser.getUsername()))).check(matches(isDisplayed()));
        onView(Matchers.allOf(withId(R.id.email_text_view), withText("Email: "+testUser.getEmail()))).check(matches(isDisplayed()));
        onView(withId(R.id.update_account_button)).check(matches(isDisplayed()));
        onView(withId(R.id.delete_account_button)).check(matches(isDisplayed()));
        onView(withId(R.id.warning_no_data_text_view)).check(matches(isDisplayed()));
        onView(withId(R.id.game_spinner)).check(matches(not(isDisplayed())));
        onView(withId(R.id.history_scores_recycler_view)).check(matches(not(isDisplayed())));
    }

    @After
    public void tearDown() throws IOException {
        context.deleteSharedPreferences(context.getString(R.string.sp_filename));
        deleteUserAPIDatabase(testUser);
    }
}
