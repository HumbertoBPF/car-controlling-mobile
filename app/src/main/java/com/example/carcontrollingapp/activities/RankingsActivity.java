package com.example.carcontrollingapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.adapters.RankingAdapter;
import com.example.carcontrollingapp.daos.GameDao;
import com.example.carcontrollingapp.daos.ScoreDao;
import com.example.carcontrollingapp.interfaces.OnResultListener;
import com.example.carcontrollingapp.models.Game;
import com.example.carcontrollingapp.models.Score;
import com.example.carcontrollingapp.room.AppDatabase;

import java.util.List;

public class RankingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner gameSpinner;
    private RecyclerView rankingsRecyclerView;
    private TextView warningNoDataTextView;

    private GameDao gameDao;
    private ScoreDao scoreDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rankings);

        gameSpinner = findViewById(R.id.game_spinner);
        rankingsRecyclerView = findViewById(R.id.ranking_recycler_view);
        warningNoDataTextView = findViewById(R.id.warning_no_data_text_view);

        gameDao = AppDatabase.getInstance(this).getGameDao();
        scoreDao = AppDatabase.getInstance(this).getScoreDao();

        gameDao.getAllRecordsTask(new OnResultListener<List<Game>>() {
            @Override
            public void onResult(List<Game> result) {
                if (result.size() == 0){
                    gameSpinner.setVisibility(View.GONE);
                    rankingsRecyclerView.setVisibility(View.GONE);
                    warningNoDataTextView.setVisibility(View.VISIBLE);
                }else{
                    ArrayAdapter<Game> adapter = new ArrayAdapter<>(RankingsActivity.this,
                            android.R.layout.simple_spinner_item, result);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    gameSpinner.setOnItemSelectedListener(RankingsActivity.this);
                    gameSpinner.setAdapter(adapter);

                    Game defaultGame = (Game) gameSpinner.getSelectedItem();
                    updateRanking(defaultGame);
                }
            }
        }).execute();
    }

    private void updateRanking(Game game) {
        scoreDao.getRankingByGameTask(game.getId(), new OnResultListener<List<Score>>() {
            @Override
            public void onResult(List<Score> result) {
                rankingsRecyclerView.setAdapter(new RankingAdapter(result));
            }
        }).execute();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Game selectedGame = (Game) parent.getItemAtPosition(position);
        updateRanking(selectedGame);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}