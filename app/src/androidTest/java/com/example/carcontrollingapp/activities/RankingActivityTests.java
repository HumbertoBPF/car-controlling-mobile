package com.example.carcontrollingapp.activities;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.example.carcontrollingapp.utils.CustomMatchers.isRanking;
import static com.example.carcontrollingapp.utils.CustomMatchers.scoresMatchesGame;
import static com.example.carcontrollingapp.utils.Tools.updateLocalDatabase;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.models.Game;
import com.example.carcontrollingapp.retrofit.CarControllerAPIHelperTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import retrofit2.Call;

@RunWith(AndroidJUnit4.class)
public class RankingActivityTests extends UITests{
    @Test
    public void testRankingFilter() throws IOException {
        updateLocalDatabase(context);
        Game game = getGameForTest();

        onView(allOf(withId(R.id.rankings_button), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.game_spinner), isDisplayed())).perform(click());
        onData(is(game)).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.ranking_recycler_view))
                .check(matches(scoresMatchesGame(game)))
                .check(matches(isRanking()));
    }

    /**
     * @return random game among the ones present in the API database
     * @throws IOException throws IOException in case of some problem due to HTTP request
     */
    private Game getGameForTest() throws IOException {
        Call<List<Game>> gamesCall = new CarControllerAPIHelperTest().getApiObject().getGames();
        List<Game> games = gamesCall.execute().body();

        if (games == null || games.isEmpty()){
            Assert.fail("No game returned by API");
        }

        int nbGames = games.size();
        Random random = new Random();
        return games.get(random.nextInt(nbGames));
    }

}
