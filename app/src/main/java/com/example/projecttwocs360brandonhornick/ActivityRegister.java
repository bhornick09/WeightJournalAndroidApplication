package com.example.projecttwocs360brandonhornick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityRegister extends AppCompatActivity {
    EditText etUser, etPwd, etRePwd;
    Button btnRegister, btnBackToLogin;
    DBHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etUser = findViewById(R.id.etUsername);
        etPwd = findViewById(R.id.etPwd);
        etRePwd = findViewById(R.id.etRePwd);
        btnRegister = findViewById(R.id.btnRegister);
        dbHelper = new DBHelper(this);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
        // register new account to database
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user, pwd, rePwd;
                user = etUser.getText().toString();
                pwd = etPwd.getText().toString();
                rePwd = etRePwd.getText().toString();
                // if fields aren't filled in fully, return invalid
                if (user.equals("") || pwd.equals("") || rePwd.equals("")){
                    Toast.makeText(ActivityRegister.this, "Please fill all fields.", Toast.LENGTH_LONG).show();
                }
                else{
                    // check password was same in both fields
                    if(pwd.equals(rePwd)){
                        // check if user name is taken
                        if(dbHelper.checkUserName(user)){
                            Toast.makeText(ActivityRegister.this, "Username taken!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        // finally check if account creation is successful and return to login page
                        boolean registeredSuccess = dbHelper.insertData(user, pwd);
                        if(registeredSuccess){
                            Toast.makeText(ActivityRegister.this, "User registered!", Toast.LENGTH_LONG).show();
                            // go to login
                            Intent intent = new Intent(ActivityRegister.this, ActivityLogin.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(ActivityRegister.this, "Registration failed!", Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(ActivityRegister.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        // back button to return to login screen
        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityRegister.this, ActivityLogin.class);
                startActivity(intent);
            }
        });
    }
}
