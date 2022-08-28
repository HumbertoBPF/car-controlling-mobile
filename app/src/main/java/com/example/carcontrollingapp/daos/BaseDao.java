package com.example.carcontrollingapp.daos;

import static androidx.room.OnConflictStrategy.REPLACE;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.RawQuery;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.example.carcontrollingapp.interfaces.OnResultListener;

import java.util.List;

@Dao
public abstract class BaseDao<E> {

    protected String tableName;

    public BaseDao(String tableName) {
        this.tableName = tableName;
    }

    @RawQuery
    protected abstract List<E> getAllRecords(SupportSQLiteQuery sqLiteQuery);

    /**
     * Executes the query to get the list of all the records of the entity <b>E</b>.
     * @return All the records of the concerned table.
     */
    public List<E> getAllRecords(){
        return getAllRecords(new SimpleSQLiteQuery("SELECT * FROM "+tableName));
    }

    public AsyncTask<Void, Void, List<E>> getAllRecordsTask(OnResultListener<List<E>> onResultListener){
        return new AsyncTask<Void, Void, List<E>>() {
            @Override
            protected List<E> doInBackground(Void... voids) {
                return getAllRecords();
            }

            @Override
            protected void onPostExecute(List<E> result) {
                super.onPostExecute(result);
                onResultListener.onResult(result);
            }
        };
    }

    @Insert(onConflict = REPLACE)
    public abstract void save(List<E> entity);

    public AsyncTask<Void, Void, List<E>> getSaveAsyncTask(List<E> entities, OnResultListener<List<E>> onResultListener){
        return new AsyncTask<Void, Void, List<E>>() {
            @Override
            protected List<E> doInBackground(Void... voids) {
                save(entities);
                return entities;
            }

            @Override
            protected void onPostExecute(List<E> list) {
                super.onPostExecute(list);
                if (onResultListener != null){
                    onResultListener.onResult(list);
                }
            }
        };
    }

    @RawQuery
    protected abstract E getRecordById(SupportSQLiteQuery sqLiteQuery);

    /**
     * Executes the query to get the element among all the records that match with the specified id.
     * @return The entity corresponded to the specified id.
     */
    public E getRecordById(long id) {
        return getRecordById(new SimpleSQLiteQuery("SELECT * FROM "+tableName+" WHERE id = "+id));
    }

    public AsyncTask<Void, Void, E> getRecordByIdTask(long id, OnResultListener<E> onResultListener){
        return new AsyncTask<Void, Void, E>(){

            @Override
            protected E doInBackground(Void... voids) {
                return getRecordById(id);
            }

            @Override
            protected void onPostExecute(E e) {
                super.onPostExecute(e);
                onResultListener.onResult(e);
            }
        };
    }
}
