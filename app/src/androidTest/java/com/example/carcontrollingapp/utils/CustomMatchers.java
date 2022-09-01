package com.example.carcontrollingapp.utils;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.adapters.RankingAdapter;
import com.example.carcontrollingapp.adapters.ScoreAdapter;
import com.example.carcontrollingapp.adapters.ScoreHistoryAdapter;
import com.example.carcontrollingapp.models.Game;
import com.example.carcontrollingapp.models.Score;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.HashMap;
import java.util.Objects;

public class CustomMatchers {

    public static Matcher<? super View> scoresMatchesGame(Game game){
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView with scores matching " + game);
            }

            @Override
            protected boolean matchesSafely(RecyclerView item) {
                ScoreAdapter adapter = (ScoreAdapter) item.getAdapter();

                if (adapter == null){
                    throw new NullPointerException("RecyclerView does not have any adapter associated with it");
                }

                int nbItems = adapter.getItemCount();

                for (int i = 0; i < nbItems; i++){
                    Score score = adapter.getItem(i);

                    if (!Objects.equals(score.getGameId(), game.getId())){
                        return false;
                    }
                }

                return true;
            }
        };
    }

    public static Matcher<? super View> isRanking(){
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Checks if the list held by a RecyclerView has a ranking structure");
            }

            @Override
            protected boolean matchesSafely(RecyclerView item) {
                ScoreAdapter adapter = (ScoreAdapter) item.getAdapter();

                if (adapter == null){
                    throw new NullPointerException("RecyclerView does not have any adapter associated with it");
                }

                int nbItems = adapter.getItemCount();

                if (nbItems > 0){
                    Score score = adapter.getItem(0);

                    long maxScore = score.getScore();
                    String username = score.getUser();
                    HashMap<String, Boolean> usernameMap = new HashMap<>();
                    usernameMap.put(username, true);

                    for (int i = 1; i < nbItems; i++){
                        score = adapter.getItem(i);

                        Long currentScore = score.getScore();
                        String currentUsername = score.getUser();
                        // Verifies if the items are disposed in the increasing order
                        if (currentScore< maxScore){
                            return false;
                        }
                        // Verifies that a member does not participate twice on a ranking
                        if (usernameMap.get(currentUsername) != null){
                            return false;
                        }

                        maxScore = currentScore;
                        usernameMap.put(currentUsername, true);
                    }
                }

                return true;
            }
        };
    }

    public static Matcher<? super View> isRankingItemViewHolder(int position){
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Checks if a ViewHolder has the layout of a ranking item");
            }

            @Override
            protected boolean matchesSafely(RecyclerView item) {
                RankingAdapter adapter = (RankingAdapter) item.getAdapter();

                if (adapter == null){
                    throw new NullPointerException("RecyclerView does not have any adapter associated with it");
                }

                RecyclerView.ViewHolder viewHolder = item.findViewHolderForAdapterPosition(position);

                if (viewHolder == null){
                    throw new NullPointerException("Item does not have a ViewHolder associated with it");
                }

                return isRankingViewHolder(viewHolder, adapter.getItem(position));
            }

            private boolean isRankingViewHolder(RecyclerView.ViewHolder viewHolder, Score score){
                TextView value = viewHolder.itemView.findViewById(R.id.score_text_view);
                TextView username = viewHolder.itemView.findViewById(R.id.username_text_view);

                return value.getText().toString().equals(score.getScore().toString()) &&
                        username.getText().toString().equals(score.getUser());
            }
        };
    }

    public static Matcher<? super View> scoresMatchesUser(String username){
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Checks if the all the scores held by a RecyclerView belong to the specified user");
            }

            @Override
            protected boolean matchesSafely(RecyclerView item) {
                ScoreAdapter adapter = (ScoreAdapter) item.getAdapter();

                if (adapter == null){
                    throw new NullPointerException("RecyclerView does not have any adapter associated with it");
                }

                int nbItems = adapter.getItemCount();

                for (int i = 0; i < nbItems; i++){
                    Score score = adapter.getItem(i);

                    if (!score.getUser().equals(username)){
                        return false;
                    }
                }

                return true;
            }
        };
    }

    public static Matcher<? super View> isScoreHistoryItemView(int position){
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Checks if a ViewHolder has the layout of a score history item");
            }

            @Override
            protected boolean matchesSafely(RecyclerView item) {
                ScoreHistoryAdapter adapter = (ScoreHistoryAdapter) item.getAdapter();

                if (adapter == null){
                    throw new NullPointerException("RecyclerView does not have any adapter associated with it");
                }

                RecyclerView.ViewHolder viewHolder = item.findViewHolderForAdapterPosition(position);

                if (viewHolder == null){
                    throw new NullPointerException("Item does not have a ViewHolder associated with it");
                }

                return isScoreViewHolder(viewHolder, adapter.getItem(position));
            }

            private boolean isScoreViewHolder(RecyclerView.ViewHolder viewHolder, Score score){
                TextView value = viewHolder.itemView.findViewById(R.id.score_text_view);
                TextView date = viewHolder.itemView.findViewById(R.id.date_text_view);

                return value.getText().toString().equals(score.getScore().toString()) &&
                        date.getText().toString().equals(score.getDate());
            }
        };
    }

}
