package com.example.carcontrollingapp.activities;

import static com.example.carcontrollingapp.models.User.deleteUserCredentials;
import static com.example.carcontrollingapp.models.User.isUserAuthenticated;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.adapters.AdAdapter;
import com.example.carcontrollingapp.async_tasks.ClearTablesTask;
import com.example.carcontrollingapp.daos.AdsDao;
import com.example.carcontrollingapp.interfaces.OnResultListener;
import com.example.carcontrollingapp.models.Ad;
import com.example.carcontrollingapp.retrofit.synchronization.GamesSynchronization;
import com.example.carcontrollingapp.room.AppDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public RecyclerView adsRecyclerView;
    private TextView warningNoDataTextView;
    private FloatingActionButton rankingsButton;
    private AdsDao adsDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adsRecyclerView = findViewById(R.id.ads_recycler_view);
        warningNoDataTextView = findViewById(R.id.warning_no_data_text_view);
        rankingsButton = findViewById(R.id.rankings_button);

        adsDao = AppDatabase.getInstance(this).getAdsDao();

        rankingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RankingsActivity.class));
            }
        });
    }

    public void loadAds() {
        adsDao.getAllRecordsTask(new OnResultListener<List<Ad>>() {
            @Override
            public void onResult(List<Ad> result) {
                if (result.size() == 0){
                    warningNoDataTextView.setVisibility(View.VISIBLE);
                }else{
                    warningNoDataTextView.setVisibility(View.GONE);
                }
                adsRecyclerView.setAdapter(new AdAdapter(MainActivity.this, result));
            }
        }).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_synchro) {
            launchSynchroConfirmation();
            return true;
        }else if (item.getItemId() == R.id.action_account){
            redirectToProfile();
            return true;
        }else if (item.getItemId() == R.id.action_logout){
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void redirectToProfile() {
        if (!isUserAuthenticated(this)){
            startActivity(new Intent(this, LoginActivity.class));
        }else{
            startActivity(new Intent(this, ProfileActivity.class));
        }
    }

    private void launchSynchroConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.synchro_confirmation_dialog_title)
                .setMessage(getString(R.string.synchro_confirmation_dialog_message))
                .setPositiveButton(R.string.synchro_confirmation_dialog_yes_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        launchSynchro(dialog);
                    }
                })
                .setNegativeButton(R.string.synchro_confirmation_dialog_no_btn, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void launchSynchro(DialogInterface dialog) {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle(getString(R.string.synchro_progress_dialog_title));
        progressDialog.setMessage(getString(R.string.synchro_progress_dialog_message));
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                loadAds();
            }
        });
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Clear all tables before launching a synchro in order to make the local databases to
                // be completely identical to the databases on the server
                new ClearTablesTask(MainActivity.this, new OnResultListener<Void>() {
                    @Override
                    public void onResult(Void result) {
                        new GamesSynchronization(MainActivity.this, progressDialog).execute();
                    }
                }).execute();
                dialog.dismiss();
            }
        }, 2000);
    }

    private void logout() {
        deleteUserCredentials(this);
        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_logout).setVisible(isUserAuthenticated(this));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAds();
        invalidateOptionsMenu();
    }
}