package com.example.projecttwocs360brandonhornick;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityDashboard extends AppCompatActivity {
    private WeightGraphDSA weightGraph;
    private DBHelper dbHelper;
    ProgressBar progressBar;
    TextView currentWeightTv, goalWeightTv, remainingWeightTv, goalPercentageTv;
    Button changeGoalBtn;
    private float targetWeight = 180.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        NavManager.setupFooter(this);

        weightGraph = findViewById(R.id.weight_graph);
        dbHelper = new DBHelper(this);
        currentWeightTv = findViewById(R.id.goal_current_val);
        goalWeightTv = findViewById(R.id.goal_target_val);
        remainingWeightTv = findViewById(R.id.goal_remaining_val);
        progressBar = findViewById(R.id.goal_progress_bar);
        goalPercentageTv = findViewById(R.id.goal_percentage);
        changeGoalBtn = findViewById(R.id.change_goal_button);

        changeGoalBtn.setOnClickListener(v -> {
            showChangeGoalDialog();
        });

        loadGraphData();
    }

    private void loadGraphData() {
        Cursor cursor = dbHelper.getAllWeightEntries();
        List<Float> weights = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        float latestWeight = 0f; // used for goal metrics

        if (cursor != null && cursor.moveToFirst()) {
            //update latest weight
            try {
                latestWeight = Float.parseFloat(cursor.getString(2));
            } catch (Exception e) { latestWeight = 0f; }
            //Collect graph data
            do {
                try {
                    dates.add(cursor.getString(1)); // Date
                    weights.add(Float.parseFloat(cursor.getString(2))); // Weight
                } catch (Exception e) { e.printStackTrace(); }
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (latestWeight > 0){
            updateGoalMetrics(latestWeight);
        }

        // Reverse because our db is newest first but graph needs Oldest first
        java.util.Collections.reverse(weights);
        java.util.Collections.reverse(dates);



        if (weights.size() > 1) {
            weightGraph.setData(weights, dates);
        }
    }

    private void updateGoalMetrics(float currentWeight){
        // calculate remaining weight
        float remaining =  currentWeight - targetWeight;

        // progress percentage
        int progressPercent = (int) ((targetWeight / currentWeight ) * 100);

        //update ui
        currentWeightTv.setText(String.format("%.1f", currentWeight));
        goalWeightTv.setText(String.format("%.1f", targetWeight));
        remainingWeightTv.setText(String.format("%.1f", remaining));

        goalPercentageTv.setText(progressPercent + "%");
        progressBar.setProgress(progressPercent);

        // Change color based on percent distance from goal
        if (progressPercent < 50) {
            remainingWeightTv.setTextColor(android.graphics.Color.parseColor("#FFFFFF")); // White
        }
        else if (progressPercent <= 80 ){
            remainingWeightTv.setTextColor(android.graphics.Color.parseColor("#FFFF9F")); // Yellow
        }
        else {
            remainingWeightTv.setTextColor(android.graphics.Color.parseColor("#98FB98")); // Green
        }
    }

    private void showChangeGoalDialog() {
        // Create edit text popup
        final EditText taskEditText = new EditText(this);
        taskEditText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        taskEditText.setHint("Enter new goal (e.g. 175.0)");
        taskEditText.setText(String.valueOf(targetWeight)); // Set current goal as default

        // Create AlertDialog
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setTitle("Set Weight Goal")
                .setMessage("What is your target weight?")
                .setView(taskEditText)
                .setPositiveButton("Save", (dialogInterface, i) -> {
                    String value = taskEditText.getText().toString();
                    if (!value.isEmpty()) {
                        try {
                            // 1. Update target
                            targetWeight = Float.parseFloat(value);

                            // 2. Refresh UI with new goal
                            loadGraphData();

                            Toast.makeText(this, "Goal updated to " + targetWeight, Toast.LENGTH_SHORT).show();
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }
}