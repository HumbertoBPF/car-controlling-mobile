package com.example.carcontrollingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Synchronize data")
                    .setMessage("You are going to launch a synchronization. " +
                            "Please, make sure that you have an active internet connexion.")
                    .setPositiveButton("Launch", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                            progressDialog.setTitle("Resources synchronization");
                            progressDialog.setMessage("Synchronization in progress...");
                            progressDialog.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    new GamesSynchronization(MainActivity.this, progressDialog).execute();
                                    dialog.dismiss();
                                }
                            }, 2000);
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}