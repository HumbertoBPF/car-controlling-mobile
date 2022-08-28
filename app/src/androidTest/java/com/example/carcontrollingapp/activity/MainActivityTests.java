package com.example.carcontrollingapp.activity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.carcontrollingapp.models.User.isUserAuthenticated;
import static com.example.carcontrollingapp.models.User.saveUserCredentials;
import static com.example.carcontrollingapp.room.AppDatabase.NAME_DB;
import static com.example.carcontrollingapp.utils.CustomActionViews.waitForView;
import static org.hamcrest.CoreMatchers.allOf;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.activities.MainActivity;
import com.example.carcontrollingapp.models.User;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTests {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule
            = new ActivityTestRule<>(MainActivity.class, true, false);
    private final Context context = ApplicationProvider.getApplicationContext();
    private final User testUser
            = new User("AndroidTestUser", "android.test.user@test.com", "android-test-password");

    @Test
    public void testSuccessfulSync() {
        // Deleting local database
        context.deleteDatabase(NAME_DB);
        mainActivityRule.launchActivity(new Intent());

        onView(allOf(withId(R.id.action_synchro), isDisplayed())).perform(click());
        onView(isRoot()).perform(waitForView(withId(android.R.id.button1), 5000, true));
        onView(allOf(withId(android.R.id.button1), isDisplayed())).perform(click());
        String dialogTitle = context.getString(R.string.synchro_progress_dialog_title);
        onView(isRoot()).perform(waitForView(withText(dialogTitle), 5000, true));
        onView(isRoot()).perform(waitForView(withText(dialogTitle), 30000, false));
    }

    @Test
    public void testLogout(){
        saveUserCredentials(context, testUser.getUsername(), testUser.getEmail(), testUser.getPassword());

        mainActivityRule.launchActivity(new Intent());

        onView(withId(R.id.action_logout)).perform(click());

        Assert.assertFalse(isUserAuthenticated(context));
        onView(withId(R.id.action_logout)).check(doesNotExist());
    }
}
