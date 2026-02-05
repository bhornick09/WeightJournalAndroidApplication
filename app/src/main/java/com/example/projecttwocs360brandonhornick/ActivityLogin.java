package com.example.projecttwocs360brandonhornick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityLogin extends AppCompatActivity {
    Button btnLogin;
    Button btnRegister;
    EditText etUsername, etPwd;
    DBHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(this);
        etUsername = findViewById(R.id.etUsername);
        etPwd = findViewById(R.id.etPwd);
        btnLogin =  findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        // login functionality
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // boolean to check user credentials, if valid then switch to journal screen and log user in
                boolean isLoggedIn = dbHelper.checkUser(etUsername.getText().toString(), etPwd.getText().toString()); //checks password in plaintext
                if(isLoggedIn){
                    Intent intent = new Intent(ActivityLogin.this, ActivityDashboard.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(ActivityLogin.this, "Login Failed!", Toast.LENGTH_LONG).show();
                }
            }
        });
        // Switch to register activity when sign up button is clicked
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityLogin.this, ActivityRegister.class);
                startActivity(intent);
            }
        });
    }
}
