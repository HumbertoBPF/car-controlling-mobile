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
import static com.example.carcontrollingapp.utils.Tools.assertUserIsNotInAPIDatabase;
import static com.example.carcontrollingapp.utils.Tools.createUserAPIDatabase;
import static com.example.carcontrollingapp.utils.Tools.deleteUserAPIDatabase;
import static com.example.carcontrollingapp.utils.Tools.updateLocalDatabase;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.daos.GameDao;
import com.example.carcontrollingapp.models.Game;
import com.example.carcontrollingapp.models.Score;
import com.example.carcontrollingapp.models.User;
import com.example.carcontrollingapp.retrofit.CarControllerAPIHelperTest;
import com.example.carcontrollingapp.retrofit.CarControllerAPITest;
import com.example.carcontrollingapp.room.AppDatabase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.Random;

@RunWith(AndroidJUnit4.class)
public class ProfileActivityTests extends UITests{
    private final User testUser
            = new User("AndroidTestUser", "android.test.user@test.com", "android-test-password");

    @Before
    public void setup() {
        saveUserCredentials(context, testUser.getUsername(), testUser.getEmail(), testUser.getPassword());
    }

    @Test
    public void testProfileAccess(){
        onView(withId(R.id.action_account)).perform(click());
        onView(allOf(withId(R.id.username_text_view), withText(context.getString(R.string.username_label) + ": " + testUser.getUsername())))
                .check(matches(isDisplayed()));
        onView(allOf(withId(R.id.email_text_view), withText(context.getString(R.string.email_label) + ": " + testUser.getEmail())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testScoreFilter() throws IOException {
        createUserAPIDatabase(testUser);

        GameDao gameDao = AppDatabase.getInstance(context).getGameDao();

        List<Game> games = gameDao.getAllRecords();
        int nbScores = 15;

        createScoreForTestUser(games,nbScores);
        updateLocalDatabase(context);

        onView(withId(R.id.action_account)).perform(click());

        testGameSelection(games, nbScores);
    }

    @Test
    public void testDeleteExistingAccount() throws IOException {
        createUserAPIDatabase(testUser);

        testDeleteAccount();
        // Verifies if the user was deleted from the API database
        assertUserIsNotInAPIDatabase(testUser.getUsername(), testUser.getPassword());
    }

    @Test
    public void testDeleteNonExistingUser() {
        testDeleteAccount();
    }

    @Test
    public void testLogout(){
        onView(withId(R.id.action_logout)).perform(click());
        Assert.assertFalse(isUserAuthenticated(context));
        onView(withId(R.id.action_logout)).check(doesNotExist());
    }

    /**
     * Tests FOR EACH game that when a game is selected on the spinner, only the scores associated to that game are shown.
     * @param games list of games that must be covered in the test
     * @param nbScores number of scores of each game of the list
     */
    private void testGameSelection(List<Game> games, int nbScores) {
        for (Game game: games){
            onView(allOf(withId(R.id.game_spinner), isDisplayed())).perform(click());
            onData(is(game)).inRoot(isPlatformPopup()).perform(click());
            onView(withId(R.id.history_scores_recycler_view))
                    .check(matches(scoresMatchesGame(game)))
                    .check(matches(scoresMatchesUser(testUser.getUsername())));

            for (int i = 0; i< nbScores; i++){
                onView(withId(R.id.history_scores_recycler_view))
                        .perform(RecyclerViewActions.scrollToPosition(i))
                        .check(matches(isScoreHistoryItemView(i)));
            }
        }
    }

    /**
     * Creates the specified number of scores for FOR EACH game for the test user.
     * @param games list of Game objects for which the scores are going to be created
     * @param nbScores number of scores that must be created for each game
     * @throws IOException throws an IOException if some I/O problem due to HTTP request happens
     */
    private void createScoreForTestUser(List<Game> games, int nbScores) throws IOException {
        Random random = new Random();
        CarControllerAPITest carControllerAPITest = (CarControllerAPITest) new CarControllerAPIHelperTest().getApiObject();

        for (Game game : games){
            for (int i = 0;i < nbScores;i++){
                carControllerAPITest.postScores(getAuthToken(testUser.getUsername(), testUser.getPassword()),
                        new Score((long) random.nextInt(1000), game.getId())).execute();
            }
        }
    }

    /**
     * Tests what must happen when an account is deleted (independently if the account exists in the API database or not).
     */
    private void testDeleteAccount() {
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

    @After
    public void tearDown() throws IOException {
        context.deleteSharedPreferences(context.getString(R.string.sp_filename));
        deleteUserAPIDatabase(testUser);
    }
}
