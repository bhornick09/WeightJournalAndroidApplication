package com.example.projecttwocs360brandonhornick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ActivitySettings extends AppCompatActivity {

    private Switch smsSwitch;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        NavManager.setupFooter(this);

//        // Setup the Header for this specific page
//        TextView headerTitle = findViewById(R.id.weight_journal_header);
//        if (headerTitle != null) {
//            headerTitle.setText("Settings");
//        }

        // Initialize Views
        smsSwitch = findViewById(R.id.switch_sms);
        btnLogout = findViewById(R.id.btn_logout);


        // SMS Switch Logic
        smsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // check for Manifest.permission.SEND_SMS
                Toast.makeText(this, "SMS Notifications Enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS Notifications Disabled", Toast.LENGTH_SHORT).show();
            }
        });

        // Logout Logic
        btnLogout.setOnClickListener(v -> {
            // Redirect back to Login Activity
            Intent intent = new Intent(ActivitySettings.this, ActivityLogin.class);
            // Clear the activity stack so user can't go back to tracker without logging in
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}