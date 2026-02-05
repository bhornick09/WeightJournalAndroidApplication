package com.example.projecttwocs360brandonhornick;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
public class NavManager {
    public static void setupFooter(Activity activity) {
        // Find buttons within the included footer
        View journalBtn = activity.findViewById(R.id.weight_page_button);
        View settingsBtn = activity.findViewById(R.id.settings_button);
        View dashBtn = activity.findViewById(R.id.dashboard_button);

        if (journalBtn != null) {
            journalBtn.setOnClickListener(v -> {
                // Only navigate if we aren't already there
                if (!(activity instanceof ActivityTracker)) {
                    activity.startActivity(new Intent(activity, ActivityTracker.class));
                    activity.finish();
                }
            });
        }

        if (settingsBtn != null) {
            settingsBtn.setOnClickListener(v -> {
                // Only navigate if we aren't already there
                if (!(activity instanceof ActivitySettings)) {
                    activity.startActivity(new Intent(activity, ActivitySettings.class));
                    activity.finish();
                }
            });
        }

        if (dashBtn != null){
            dashBtn.setOnClickListener(v -> {

                if (!(activity instanceof ActivityDashboard)) {
                    activity.startActivity(new Intent(activity, ActivityDashboard.class));
                    activity.finish();
                }
            });
        }
    }
}
