package com.example.carcontrollingapp.no_data;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.carcontrollingapp.models.User.saveUserCredentials;
import static com.example.carcontrollingapp.utils.Tools.createUserAPIDatabase;
import static com.example.carcontrollingapp.utils.Tools.deleteUserAPIDatabase;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.activities.MainActivity;
import com.example.carcontrollingapp.models.User;
import com.example.carcontrollingapp.room.AppDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class WarningNoDataTests {
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
        AppDatabase.getInstance(context).clearAllTables();
    }

    @Test
    public void testMainActivityWithNoData(){
        mainActivityRule.launchActivity(new Intent());
        onView(withId(R.id.action_account)).check(matches(isDisplayed()));
        onView(withId(R.id.action_synchro)).check(matches(isDisplayed()));
        onView(withId(R.id.ads_recycler_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.rankings_button)).check(matches(isDisplayed()));
        onView(withId(R.id.warning_no_data_text_view)).check(matches(isDisplayed()));
    }

    @Test
    public void testAccessRankingsWithNoData(){
        mainActivityRule.launchActivity(new Intent());
        onView(allOf(withId(R.id.rankings_button), isDisplayed())).perform(click());
        onView(withId(R.id.game_spinner)).check(matches(not(isDisplayed())));
        onView(withId(R.id.ranking_recycler_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.warning_no_data_text_view)).check(matches(isDisplayed()));
    }

    @Test
    public void testAccessProfileWithNoData() throws IOException {
        createUserAPIDatabase(testUser);
        saveUserCredentials(context, testUser.getUsername(), testUser.getEmail(), testUser.getPassword());

        mainActivityRule.launchActivity(new Intent());

        onView(withId(R.id.action_account)).check(matches(isDisplayed()));
        onView(withId(R.id.action_synchro)).check(matches(isDisplayed()));
        onView(withId(R.id.action_logout)).check(matches(isDisplayed()));
        onView(withId(R.id.ads_recycler_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.rankings_button)).check(matches(isDisplayed()));
        onView(withId(R.id.warning_no_data_text_view)).check(matches(isDisplayed()));

        onView(withId(R.id.action_account)).perform(click());

        onView(allOf(withId(R.id.username_text_view), withText("Username: "+testUser.getUsername()))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.email_text_view), withText("Email: "+testUser.getEmail()))).check(matches(isDisplayed()));
        onView(withId(R.id.update_account_button)).check(matches(isDisplayed()));
        onView(withId(R.id.delete_account_button)).check(matches(isDisplayed()));
        onView(withId(R.id.warning_no_data_text_view)).check(matches(isDisplayed()));
        onView(withId(R.id.game_spinner)).check(matches(not(isDisplayed())));
        onView(withId(R.id.history_scores_recycler_view)).check(matches(not(isDisplayed())));
    }

    @After
    public void tearDown() throws IOException {
        // Deleting shared preferences
        context.deleteSharedPreferences(context.getString(R.string.sp_filename));
        // Deleting test user in the API database
        deleteUserAPIDatabase(testUser);
    }
}
