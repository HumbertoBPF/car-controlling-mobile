package com.example.carcontrollingapp.activities;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.carcontrollingapp.models.User.isUserAuthenticated;
import static com.example.carcontrollingapp.models.User.saveUserCredentials;
import static com.example.carcontrollingapp.retrofit.CarControllerAPIHelper.getAuthToken;
import static com.example.carcontrollingapp.utils.CustomActionViews.waitForView;
import static com.example.carcontrollingapp.utils.CustomMatchers.isScoreHistoryItemView;
import static com.example.carcontrollingapp.utils.CustomMatchers.scoresMatchesGame;
import static com.example.carcontrollingapp.utils.CustomMatchers.scoresMatchesUser;
import static com.example.carcontrollingapp.utils.Tools.createUserAPIDatabase;
import static com.example.carcontrollingapp.utils.Tools.deleteUserAPIDatabase;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.models.Ad;
import com.example.carcontrollingapp.models.Game;
import com.example.carcontrollingapp.models.Score;
import com.example.carcontrollingapp.models.User;
import com.example.carcontrollingapp.retrofit.CarControllerAPIHelperTest;
import com.example.carcontrollingapp.retrofit.CarControllerAPITest;
import com.example.carcontrollingapp.room.AppDatabase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Response;

@RunWith(AndroidJUnit4.class)
public class ProfileActivityTests {
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
        // Saving authenticated user in Shared Preferences
        saveUserCredentials(context, testUser.getUsername(), testUser.getEmail(), testUser.getPassword());
    }

    @Test
    public void testProfileAccess(){
        mainActivityRule.launchActivity(new Intent());
        onView(withId(R.id.action_account)).perform(click());
        onView(allOf(withId(R.id.username_text_view), withText(context.getString(R.string.username_label) + ": " + testUser.getUsername())))
                .check(matches(isDisplayed()));
        onView(allOf(withId(R.id.email_text_view), withText(context.getString(R.string.email_label) + ": " + testUser.getEmail())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testScoreFilter() throws IOException {
        createUserAPIDatabase(testUser);

        CarControllerAPITest carControllerAPITest = (CarControllerAPITest) new CarControllerAPIHelperTest().getApiObject();
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        List<Game> games = appDatabase.getGameDao().getAllRecords();

        Random random = new Random();
        int nbScores = 15;

        for (Game game : games){
            for (int i = 0;i < nbScores;i++){
                carControllerAPITest.postScores(getAuthToken(testUser.getUsername(), testUser.getPassword()),
                        new Score((long) random.nextInt(1000), game.getId())).execute();
            }
        }
        // Refresh the local database
        appDatabase.clearAllTables();
        Response<List<Score>> responseScores = carControllerAPITest.getScores().execute();
        appDatabase.getScoreDao().save(responseScores.body());
        Response<List<Game>> responseGames = carControllerAPITest.getGames().execute();
        appDatabase.getGameDao().save(responseGames.body());
        Response<List<Ad>> responseAds = carControllerAPITest.getAds().execute();
        appDatabase.getAdsDao().save(responseAds.body());

        mainActivityRule.launchActivity(new Intent());
        onView(withId(R.id.action_account)).perform(click());

        for (Game game: games){
            onView(allOf(withId(R.id.game_spinner), isDisplayed())).perform(click());
            onData(is(game)).inRoot(isPlatformPopup()).perform(click());
            onView(withId(R.id.history_scores_recycler_view))
                    .check(matches(scoresMatchesGame(game)))
                    .check(matches(scoresMatchesUser(testUser.getUsername())));

            for (int i=0;i<nbScores;i++){
                onView(withId(R.id.history_scores_recycler_view))
                        .perform(RecyclerViewActions.scrollToPosition(i))
                        .check(matches(isScoreHistoryItemView(i)));
            }
        }
    }

    @Test
    public void testDeleteExistingAccount() throws IOException {
        createUserAPIDatabase(testUser);

        testDeleteAccount();
        // Verifies if the user was deleted from the API database
        Call<User> loginCall = new CarControllerAPIHelperTest().getApiObject()
                .login(getAuthToken(testUser.getUsername(), testUser.getPassword()));
        Response<User> response = loginCall.execute();
        if (response.code() != 403){
            Assert.fail("Login call did not return 403 status code");
        }
    }

    public void testDeleteAccount() {
        mainActivityRule.launchActivity(new Intent());

        onView(withId(R.id.action_account)).perform(click());
        onView(withId(R.id.delete_account_button)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        String dialogTitle = context.getString(R.string.deletion_dialog_title);

        onView(isRoot()).perform(waitForView(withText(dialogTitle), 5000, true));
        onView(isRoot()).perform(waitForView(withText(dialogTitle), 10000, false));
        // Verifies if the user returned to the MainActivity
        onView(withId(R.id.action_synchro)).check(matches(isDisplayed()));
        onView(withId(R.id.action_account)).check(matches(isDisplayed()));
        onView(withId(R.id.action_logout)).check(doesNotExist());
        // Verifies that the credentials were deleted from SharedPreferences
        Assert.assertFalse(isUserAuthenticated(context));
    }

    @Test
    public void testDeleteNonExistingUser() {
        testDeleteAccount();
    }

    @After
    public void tearDown() throws IOException {
        // Deleting shared preferences
        context.deleteSharedPreferences(context.getString(R.string.sp_filename));
        // Deleting test user in the API database
        deleteUserAPIDatabase(testUser);
    }
}
