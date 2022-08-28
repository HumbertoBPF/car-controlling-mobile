package com.example.carcontrollingapp.async_tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.example.carcontrollingapp.interfaces.OnResultListener;
import com.example.carcontrollingapp.room.AppDatabase;

public class ClearTablesTask extends AsyncTask<Void,Void,Void> {

    private Context context;
    private OnResultListener<Void> onResultListener;

    public ClearTablesTask(Context context, OnResultListener<Void> onResultListener) {
        this.context = context;
        this.onResultListener = onResultListener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        AppDatabase.getInstance(context).clearAllTables();
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        onResultListener.onResult(unused);
    }
}
