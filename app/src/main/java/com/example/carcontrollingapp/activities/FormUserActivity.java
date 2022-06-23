package com.example.carcontrollingapp.activities;

import static com.example.carcontrollingapp.models.User.saveUserCredentials;
import static com.example.carcontrollingapp.retrofit.CarControllerAPIHelper.getAuthToken;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.models.User;
import com.example.carcontrollingapp.retrofit.CarControllerAPIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormUserActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmationEditText;
    private Button submitButton;
    private boolean isUpdate;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_user);

        emailEditText = findViewById(R.id.email_edit_text);
        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        passwordConfirmationEditText = findViewById(R.id.password_confirmation_edit_text);
        submitButton = findViewById(R.id.submit_button);

        isUpdate = getIntent().getBooleanExtra("isUpdate", false);

        sp = getSharedPreferences(getString(R.string.sp_filename), MODE_PRIVATE);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUpdate){
                    updateAccount();
                }else{
                    createAccount();
                }
            }
        });
    }

    private void updateAccount() {
        String newEmail = emailEditText.getText().toString();
        String newUsername = usernameEditText.getText().toString();
        String newPassword = passwordEditText.getText().toString();
        String passwordConfirmation = passwordConfirmationEditText.getText().toString();

        if (!newPassword.equals(passwordConfirmation)){
            Toast.makeText(FormUserActivity.this, R.string.warning_passwords_matching, Toast.LENGTH_SHORT).show();
        }else{
            User user = new User(newUsername, newEmail, newPassword);

            String username = sp.getString(getString(R.string.sp_username),"");
            String password = sp.getString(getString(R.string.sp_password),"");
            Call<User> call = CarControllerAPIHelper.getApiObject().updateUser(getAuthToken(username, password), user);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()){
                        Toast.makeText(FormUserActivity.this, R.string.sucess_account_update, Toast.LENGTH_SHORT).show();
                        saveUserCredentials(FormUserActivity.this, newUsername, newEmail, newPassword);
                        finish();
                    }else if (response.code() == 400){
                        String error = getValidationErrorMessage(response);

                        Toast.makeText(FormUserActivity.this, error, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(FormUserActivity.this, R.string.warning_connexion_error, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(FormUserActivity.this, R.string.warning_connexion_error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void createAccount() {
        String newEmail = emailEditText.getText().toString();
        String newUsername = usernameEditText.getText().toString();
        String newPassword = passwordEditText.getText().toString();
        String passwordConfirmation = passwordConfirmationEditText.getText().toString();

        if (!newPassword.equals(passwordConfirmation)){
            Toast.makeText(FormUserActivity.this, R.string.warning_passwords_matching, Toast.LENGTH_SHORT).show();
        }else{
            User user = new User(newUsername, newEmail, newPassword);

            Call<User> call = CarControllerAPIHelper.getApiObject().signup(user);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()){
                        Toast.makeText(FormUserActivity.this, R.string.success_create_account, Toast.LENGTH_SHORT).show();
                        finish();
                    }else if (response.code() == 400){
                        String error = getValidationErrorMessage(response);

                        Toast.makeText(FormUserActivity.this, error, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(FormUserActivity.this, R.string.warning_connexion_error, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(FormUserActivity.this, R.string.warning_connexion_error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String getValidationErrorMessage(Response<User> response) {
        String error = getString(R.string.warning_form_validation);

        if (response.errorBody() != null){
            try {
                String errorBodyString = response.errorBody().string();
                JSONObject jsonObject = new JSONObject(errorBodyString.trim());
                JSONArray errorsArray = null;
                String fieldError = null;
                if (jsonObject.has("username")){
                    errorsArray = jsonObject.getJSONArray("username");
                    fieldError = getString(R.string.username_label) + ": ";
                }else if (jsonObject.has("email")){
                    errorsArray = jsonObject.getJSONArray("email");
                    fieldError = getString(R.string.email_label) + ": ";
                }else if (jsonObject.has("password")){
                    errorsArray = jsonObject.getJSONArray("password");
                    fieldError = getString(R.string.password_label) + ": ";
                }

                if (errorsArray != null){
                    error = fieldError + errorsArray.getString(0);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        return error;
    }
}