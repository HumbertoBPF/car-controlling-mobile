package com.example.carcontrollingapp.activities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.carcontrollingapp.utils.CustomActionViews.waitForView;
import static org.hamcrest.CoreMatchers.allOf;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.carcontrollingapp.R;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTests extends UITests{
    @Test
    public void testSuccessfulSync() {
        // Click on the sync button on the appbar
        onView(allOf(withId(R.id.action_synchro), isDisplayed())).perform(click());
        // Waiting the confirmation dialog to appear and confirming that we want to launch a sync
        onView(isRoot()).perform(waitForView(withId(android.R.id.button1), 5000, true));
        onView(allOf(withId(android.R.id.button1), isDisplayed())).perform(click());
        // Verifying if the progress dialog appeared and then that it disappears after some time
        String dialogTitle = context.getString(R.string.synchro_progress_dialog_title);
        onView(isRoot()).perform(waitForView(withText(dialogTitle), 5000, true));
        onView(isRoot()).perform(waitForView(withText(dialogTitle), 30000, false));
    }
}
