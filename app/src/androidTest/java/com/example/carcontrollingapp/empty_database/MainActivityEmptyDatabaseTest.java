package com.example.carcontrollingapp.empty_database;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.carcontrollingapp.R;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityEmptyDatabaseTest extends EmptyDatabaseTest{
    @Test
    public void testMainActivityWithNoData(){
        onView(withId(R.id.action_account)).check(matches(isDisplayed()));
        onView(withId(R.id.action_synchro)).check(matches(isDisplayed()));
        onView(withId(R.id.ads_recycler_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.rankings_button)).check(matches(isDisplayed()));
        onView(withId(R.id.warning_no_data_text_view)).check(matches(isDisplayed()));
    }
}
