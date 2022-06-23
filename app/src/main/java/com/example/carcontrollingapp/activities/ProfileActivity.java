package com.example.carcontrollingapp.activities;

import static com.example.carcontrollingapp.models.User.deleteUserCredentials;
import static com.example.carcontrollingapp.retrofit.CarControllerAPIHelper.getAuthToken;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.adapters.ScoreHistoryAdapter;
import com.example.carcontrollingapp.daos.GameDao;
import com.example.carcontrollingapp.daos.ScoreDao;
import com.example.carcontrollingapp.interfaces.OnResultListener;
import com.example.carcontrollingapp.models.Game;
import com.example.carcontrollingapp.models.Score;
import com.example.carcontrollingapp.retrofit.CarControllerAPIHelper;
import com.example.carcontrollingapp.room.AppDatabase;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView usernameTextView;
    private TextView emailTextView;
    private Button updateAccountButton;
    private Button deleteAccountButton;
    private Spinner gameSpinner;
    private RecyclerView historyScoreRecyclerView;

    private GameDao gameDao;
    private ScoreDao scoreDao;

    private String username;
    private String password;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usernameTextView = findViewById(R.id.username_text_view);
        emailTextView = findViewById(R.id.email_text_view);
        updateAccountButton = findViewById(R.id.update_account_button);
        deleteAccountButton = findViewById(R.id.delete_account_button);
        gameSpinner = findViewById(R.id.game_spinner);
        historyScoreRecyclerView = findViewById(R.id.history_scores_recycler_view);

        gameDao = AppDatabase.getInstance(this).getGameDao();
        scoreDao = AppDatabase.getInstance(this).getScoreDao();

        sp = getSharedPreferences(getString(R.string.sp_filename), MODE_PRIVATE);

        onClickDeleteButton();
        onClickUpdateButton();

        gameDao.getAllRecordsTask(new OnResultListener<List<Game>>() {
            @Override
            public void onResult(List<Game> result) {
                ArrayAdapter<Game> adapter = new ArrayAdapter<>(ProfileActivity.this,
                        android.R.layout.simple_spinner_item, result);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                gameSpinner.setOnItemSelectedListener(ProfileActivity.this);
                gameSpinner.setAdapter(adapter);

                Game defaultGame = (Game) gameSpinner.getSelectedItem();
                scoreDao.getScoresByGameAndByUserTask(defaultGame.getId(), username,
                        new OnResultListener<List<Score>>() {
                            @Override
                            public void onResult(List<Score> result) {
                                historyScoreRecyclerView.setAdapter(new ScoreHistoryAdapter(result));
                            }
                        }).execute();
            }
        }).execute();
    }

    private void onClickUpdateButton() {
        updateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, FormUserActivity.class);
                intent.putExtra("isUpdate", true);
                startActivity(intent);
            }
        });
    }

    private void onClickDeleteButton() {
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle(R.string.confirm_deletion_dialog_title)
                        .setMessage(R.string.confirm_deletion_dialog_message)
                        .setPositiveButton(R.string.yes_button_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                deleteAccount();
                            }
                        })
                        .setNegativeButton(R.string.cancel_button_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false);
                builder.create().show();
            }
        });
    }

    private void deleteAccount() {
        ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setTitle(getString(R.string.deletion_dialog_title));
        progressDialog.setMessage(getString(R.string.deletion_dialog_message));
        progressDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<Void> call = CarControllerAPIHelper.getApiObject()
                        .deleteUser(getAuthToken(username, password));
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            deleteUserCredentials(ProfileActivity.this);
                            Toast.makeText(ProfileActivity.this,
                                    R.string.success_account_deletion,
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(ProfileActivity.this,
                                    R.string.warning_connexion_error,
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ProfileActivity.this,
                                R.string.warning_connexion_error,
                                Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        },3000);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Game selectedGame = (Game) parent.getItemAtPosition(position);
        scoreDao.getScoresByGameAndByUserTask(selectedGame.getId(), username, new OnResultListener<List<Score>>() {
            @Override
            public void onResult(List<Score> result) {
                historyScoreRecyclerView.setAdapter(new ScoreHistoryAdapter(result));
            }
        }).execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        username = sp.getString(getString(R.string.sp_username), "");
        String email = sp.getString(getString(R.string.sp_email), "");
        password = sp.getString(getString(R.string.sp_password), "");

        usernameTextView.setText(getString(R.string.username_label)+": " + this.username);
        emailTextView.setText(getString(R.string.email_label) + ": " + email);
    }
}