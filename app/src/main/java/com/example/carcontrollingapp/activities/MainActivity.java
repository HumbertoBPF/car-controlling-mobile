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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.retrofit.synchronization.GamesSynchronization;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton rankingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rankingsButton = findViewById(R.id.rankings_button);
        
        rankingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RankingsActivity.class));
            }
        });
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
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new GamesSynchronization(MainActivity.this, progressDialog).execute();
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
        if (!isUserAuthenticated(this)){
            menu.findItem(R.id.action_logout).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }
}