package com.example.carcontrollingapp.retrofit.synchronization;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.carcontrollingapp.daos.BaseDao;
import com.example.carcontrollingapp.interfaces.OnResultListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ResourcesSynchronization<E> {

    private Context context;
    private final String resourceName;
    private final ResourcesCallable<List<E>> resourcesCallable;
    private final BaseDao<E> dao;
    private final ProgressDialog dialog;
    private final ResourcesSynchronization nextStep;
    private final Handler handler = new Handler();

    public ResourcesSynchronization(Context context, String resourceName, ResourcesCallable<List<E>> resourcesCallable,
                                    BaseDao<E> dao, ProgressDialog dialog, ResourcesSynchronization nextStep){
        this.context = context;
        this.resourceName = resourceName;
        this.resourcesCallable = resourcesCallable;
        this.dao = dao;
        this.dialog = dialog;
        this.nextStep = nextStep;
    }

    public void execute(){
        Call<List<E>> call = resourcesCallable.getCallable();
        call.enqueue(new Callback<List<E>>() {
            @Override
            public void onResponse(Call<List<E>> call, Response<List<E>> response) {
                if (response.isSuccessful()){
                    List<E> resources = response.body();
                    dao.getSaveAsyncTask(resources, new OnResultListener<List<E>>() {
                        @Override
                        public void onResult(List<E> result) {
                            dialog.setMessage("Downloading "+resourceName+" data");
                            launchNextStep();
                        }
                    }).execute();
                }else{
                    dialog.setMessage("Fail to get "+resourceName+" data");
                    launchNextStep();
                }
            }

            @Override
            public void onFailure(Call<List<E>> call, Throwable t) {
                Log.i("HELLO", "Fail");
                dialog.setMessage("Fail to get "+resourceName+" data");
                launchNextStep();
            }
        });
    }

    private void launchNextStep(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (nextStep == null){
                    dialog.dismiss();
                }else{
                    nextStep.execute();
                }
            }
        }, 3000);
    }

    interface ResourcesCallable<E>{
        Call<E> getCallable();
    }

}
