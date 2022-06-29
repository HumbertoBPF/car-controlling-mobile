package com.example.carcontrollingapp.retrofit.synchronization;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

import com.example.carcontrollingapp.daos.BaseDao;
import com.example.carcontrollingapp.interfaces.OnResultListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *  Class to synchronize resources, i.e. get them by using an API and then save them in the local SQLite
 *  database.
 *
 * @param <E> class corresponding to the entity concerned by the synchronization
 */
public abstract class ResourcesSynchronization<E> {

    private Context context;
    private final String resourceName;
    private final ResourcesCallable<List<E>> resourcesCallable;
    private final BaseDao<E> dao;
    private final ProgressDialog dialog;
    private final ResourcesSynchronization nextStep;
    private final Handler handler = new Handler();
    /**
     * @param context context of the activity that launches the synchronization
     * @param resourceName name that we want to use to refer to the resources that are concerned by
     *                     the synchronization
     * @param resourcesCallable Retrofit call object that will be used to get the resources from the
     *                          API server
     * @param dao Data Access Object that is going to be used to save the resources gotten in the
     *            local database
     * @param dialog Progress Dialog indicating the evolution of the synchronization process
     * @param nextStep object corresponding to the next step of the synchronization. An instance of
     *                 this object will be used to launch the next step of the synchro when the
     *                 current step is ended(when the resources are saved locally)
     */
    public ResourcesSynchronization(Context context, String resourceName, ResourcesCallable<List<E>> resourcesCallable,
                                    BaseDao<E> dao, ProgressDialog dialog, ResourcesSynchronization nextStep){
        this.context = context;
        this.resourceName = resourceName;
        this.resourcesCallable = resourcesCallable;
        this.dao = dao;
        this.dialog = dialog;
        this.nextStep = nextStep;
    }
    /**
     * Launches the synchronization, i.e. makes a call of the API and ,if the API returns a 200 response code,
     * save the returned resources in the local database. If some error happens, a warning message is shown
     * in the Progress Dialog, otherwise a message announcing the success of the operation is exhibited.
     */
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
                dialog.setMessage("Fail to get "+resourceName+" data");
                launchNextStep();
            }
        });
    }
    /**
     * Calls the next step of the synchronization delayed by a certain amount of time(time necessary
     * for the user to get a feedback about the current step of the synchronization).
     */
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
    /**
     * Interface with a unique method used to get the Retrofit Call object responsible for getting
     * the resources from the API server.
     * @param <E> class corresponding to the entity concerned by the synchronization
     */
    interface ResourcesCallable<E>{
        Call<E> getCallable();
    }

}
