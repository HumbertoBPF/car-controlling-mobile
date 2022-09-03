package com.example.carcontrollingapp.utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.example.carcontrollingapp.retrofit.CarControllerAPIHelper.getAuthToken;

import android.content.Context;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.daos.AdsDao;
import com.example.carcontrollingapp.daos.GameDao;
import com.example.carcontrollingapp.daos.ScoreDao;
import com.example.carcontrollingapp.models.Ad;
import com.example.carcontrollingapp.models.Game;
import com.example.carcontrollingapp.models.Score;
import com.example.carcontrollingapp.models.User;
import com.example.carcontrollingapp.retrofit.CarControllerAPIHelperTest;
import com.example.carcontrollingapp.retrofit.CarControllerAPITest;
import com.example.carcontrollingapp.room.AppDatabase;

import org.junit.Assert;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class Tools {
    /**
     * Creates a user in the API database via a post request.
     * @param user User instance representing the user to be created
     * @throws IOException throws an IOException if some I/O problem due to HTTP happens
     */
    public static void createUserAPIDatabase(User user) throws IOException {
        Call<User> signupCall = new CarControllerAPIHelperTest().getApiObject().signup(user);
        signupCall.execute();
    }

    /**
     * Deletes a user in the API database via a delete request.
     * @param user User instance representing user to be deleted
     * @throws IOException throws an IOException if some I/O problem due to HTTP happens
     */
    public static void deleteUserAPIDatabase(User user) throws IOException {
        Call<Void> deleteCall = new CarControllerAPIHelperTest().getApiObject()
                .deleteUser(getAuthToken(user.getUsername(), user.getPassword()));
        deleteCall.execute();
    }

    /**
     * Verifies that certain user is not in the API database.
     * @param username username of the concerned user
     * @param password password of the concerned user
     * @throws IOException throws an IOException if some I/O problem due to HTTP happens
     */
    public static void assertUserIsNotInAPIDatabase(String username, String password) throws IOException {
        Call<User> loginCall = new CarControllerAPIHelperTest().getApiObject()
                .login(getAuthToken(username, password));
        Response<User> response = loginCall.execute();
        Assert.assertEquals(403, response.code());
    }

    /**
     * Updates the tables of the local database with data from the API.
     * @param context Context object corresponding to the test calling this method
     * @throws IOException throws an IOException if some I/O problem due to database access happens
     */
    public static void updateLocalDatabase(Context context) throws IOException {
        CarControllerAPITest carControllerAPITest = (CarControllerAPITest) new CarControllerAPIHelperTest().getApiObject();
        AppDatabase appDatabase = AppDatabase.getInstance(context);

        GameDao gameDao = appDatabase.getGameDao();
        ScoreDao scoreDao = appDatabase.getScoreDao();
        AdsDao adsDao = appDatabase.getAdsDao();

        appDatabase.clearAllTables();
        Response<List<Score>> responseScores = carControllerAPITest.getScores().execute();
        scoreDao.save(responseScores.body());
        Response<List<Game>> responseGames = carControllerAPITest.getGames().execute();
        gameDao.save(responseGames.body());
        Response<List<Ad>> responseAds = carControllerAPITest.getAds().execute();
        adsDao.save(responseAds.body());
    }

    public static void fillAndSubmitAccountForm(String username, String email, String password, String passwordConfirmation) {
        onView(withId(R.id.email_edit_text)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.username_edit_text)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.password_edit_text)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.password_confirmation_edit_text)).perform(typeText(passwordConfirmation), closeSoftKeyboard());
        onView(withId(R.id.submit_button)).perform(click());
    }
}
