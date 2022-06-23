package com.example.carcontrollingapp.activities;

import static com.example.carcontrollingapp.models.User.saveUserCredentials;
import static com.example.carcontrollingapp.retrofit.CarControllerAPIHelper.getAuthToken;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.models.User;
import com.example.carcontrollingapp.retrofit.CarControllerAPIHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView createAccountLink;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        createAccountLink = findViewById(R.id.create_account_link);
        loginButton = findViewById(R.id.login_button);
        // Underline link to create account
        SpannableString content = new SpannableString(createAccountLink.getText());
        content.setSpan(new UnderlineSpan(), 0, createAccountLink.getText().length(), 0);
        createAccountLink.setText(content);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        
        createAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, FormUserActivity.class));
            }
        });
    }

    private void login(){
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        Call<User> call = CarControllerAPIHelper.getApiObject().login(getAuthToken(username, password));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()){
                    User user = response.body();
                    if (user != null){
                        saveUserCredentials(LoginActivity.this, user.getUsername(), user.getEmail(), password);
                        Toast.makeText(LoginActivity.this, getString(R.string.welcome_message)+user.getUsername(), Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }else if (response.code() == 403){
                    Toast.makeText(LoginActivity.this, R.string.warning_wrong_credentials, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(LoginActivity.this, R.string.warning_connexion_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, R.string.warning_connexion_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}