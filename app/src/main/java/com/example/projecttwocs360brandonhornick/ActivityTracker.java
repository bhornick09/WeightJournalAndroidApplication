package com.example.projecttwocs360brandonhornick;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Locale;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
// Calendar
import android.app.DatePickerDialog;
import java.util.Calendar;




public class ActivityTracker extends AppCompatActivity {
    private DBHelper dbHelper;
    private TableLayout tableLayout;
    private EditText etDate, etWeight, etNotes;
    private Button btnAddEntry, btnAI;
    private LinearLayout dateStripContainer;
    private TextView displayDate, displayWeight, displayNotes;
    private Button btnDeleteEntry;
    private Button btnAddImage;
    private ImageView displayImage;
    private String selectedImageUri = "";

    // date generation
    private RecyclerView calendarRecyclerView;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());
    private SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


    AIService aiService = new AIService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_journal);
        NavManager.setupFooter(this);

        //date generation
        calendarRecyclerView = findViewById(R.id.calendar_recycler_view);
        calendarRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        dateStripContainer = findViewById(R.id.date_strip_container);displayDate = findViewById(R.id.display_selected_date);
        displayWeight = findViewById(R.id.display_weight);
        displayNotes = findViewById(R.id.display_notes);
        btnDeleteEntry = findViewById(R.id.delete_entry_button);

        dbHelper = new DBHelper(this);
        tableLayout = findViewById(R.id.weight_table);

        etDate = findViewById(R.id.editTextDate);
        etWeight = findViewById(R.id.editTextWeight);
        etNotes = findViewById(R.id.editTextNotes);
        btnAddEntry = findViewById(R.id.add_entry_button);

        // ai implementation
        EditText promptInput = findViewById(R.id.ai_insights_text);
        TextView resultText = findViewById(R.id.resultText);
        btnAI = findViewById(R.id.ai_insights_button); // new ai button

        // image implementation
        displayImage = findViewById(R.id.display_image);
        btnAddImage = findViewById(R.id.btn_select_image);

        btnAddImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });

        loadTableData();

        // add functionality to button for adding entry to table
        btnAddEntry.setOnClickListener(v -> {
            String date = etDate.getText().toString();
            String weight = etWeight.getText().toString();
            String notes = etNotes.getText().toString();

            // if fields are empty (except notes field)
            if (date.isEmpty() || weight.isEmpty()) {
                Toast.makeText(this, "Please fill in the date and weight fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // create boolean to check for successful entry to table/database
            boolean inserted = dbHelper.insertWeightEntry(date, weight, notes, selectedImageUri);
            if (inserted) {
                Toast.makeText(this, "Entry added!", Toast.LENGTH_SHORT).show();
                // reset ET boxes
                etDate.setText("");
                etWeight.setText("");
                etNotes.setText("");
                selectedImageUri = "";
                loadTableData(); // refresh display
            } else {
                Toast.makeText(this, "Entry failed", Toast.LENGTH_SHORT).show();
            }
        });

        // nav button to switch to settings page

        // new ai button functionality
        btnAI.setOnClickListener(v -> {
            aiService.setPrompt(promptInput.getText().toString()); // get user input
            aiService.setResultText(resultText);
            aiService.sendRequest();
        });

        // Calendar functionality for date
        etDate.setOnClickListener(v -> {
            // Get the current date to set as default in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create and show the DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ActivityTracker.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // Format the date to yyyy-MM-dd to match your DB expectations
                        String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d",
                                year1, (monthOfYear + 1), dayOfMonth);
                        etDate.setText(selectedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

    }

    // used to display the table for all entries in database
//    private void loadTableData() {
//        // clear current display
//        int childCount = tableLayout.getChildCount();
//        if (childCount > 1) {
//            tableLayout.removeViews(1, childCount - 1);
//        }
//
//        Cursor cursor = dbHelper.getAllWeightEntries();
//        if (cursor.getCount() == 0) {
//            return;
//        }
//
//        // reload all entries to the table
//        while (cursor.moveToNext()) {
//            int id = cursor.getInt(0);
//            String date = cursor.getString(1);
//            String weight = cursor.getString(2);
//            String notes = cursor.getString(3);
//
//            //start new row
//            TableRow row = new TableRow(this);
//            row.setPadding(0, 4, 0, 4);
//
//            // add date text for current row
//            TextView tvDate = new TextView(this);
//            tvDate.setText(date);
//            tvDate.setTextColor(android.graphics.Color.WHITE);
//            tvDate.setPadding(32, 32, 32, 32);
//            row.addView(tvDate);
//
//            // add weight text for current row
//            TextView tvWeight = new TextView(this);
//            tvWeight.setText(weight);
//            tvWeight.setTextColor(android.graphics.Color.WHITE);
//            tvWeight.setPadding(32, 32, 32, 32);
//            row.addView(tvWeight);
//
//            // add notes if needed for current row
//            TextView tvNotes = new TextView(this);
//            tvNotes.setText(notes);
//            tvNotes.setTextColor(android.graphics.Color.parseColor("#B0B0B0"));
//            tvNotes.setPadding(32, 32, 32, 32);
//            row.addView(tvNotes);
//
//            // add delete button to each row
//            Button btnDelete = new Button(this);
//            btnDelete.setText("Delete");
//            btnDelete.setAllCaps(false);
//            btnDelete.setTextColor(android.graphics.Color.parseColor("#CF6679"));
//
//            btnDelete.setOnClickListener(v -> {
//                boolean deleted = dbHelper.deleteWeightEntry(id);
//                if (deleted) {
//                    Toast.makeText(this, "Entry deleted!", Toast.LENGTH_SHORT).show();
//                    loadTableData();
//                } else {
//                    Toast.makeText(this, "Error deleting.", Toast.LENGTH_SHORT).show();
//                }
//            });
//            row.addView(btnDelete);
//
//            tableLayout.addView(row);
//        }
//        cursor.close();
//    }
// Updated loadTableData
    private void loadTableData() {
        dateStripContainer.removeAllViews(); // Clear the horizontal list

        Cursor cursor = dbHelper.getAllWeightEntries();
        if (cursor.getCount() == 0) {
            displayWeight.setText("--");
            displayDate.setText("No entries yet");
            displayNotes.setText("");
            displayImage.setVisibility(View.GONE);
            btnDeleteEntry.setVisibility(View.GONE);
            return;
        }

        while (cursor.moveToNext()) {
            final int id = cursor.getInt(0);
            final String date = cursor.getString(1);
            final String weight = cursor.getString(2);
            final String notes = cursor.getString(3);
            final String image = cursor.getString(4);

            // Create a small "Date Circle" item for the horizontal scroll
            View dateView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
            TextView tv = dateView.findViewById(android.R.id.text1);

            // Style the date item
            tv.setText(date.length() > 5 ? date.substring(5) : date); // Show just MM-DD
            tv.setTextColor(android.graphics.Color.WHITE);
            tv.setBackgroundResource(android.R.drawable.editbox_dropdown_dark_frame); // Quick background
            tv.setPadding(40, 20, 40, 20);

            // When user clicks a date in the top bar
            tv.setOnClickListener(v -> {
                displayEntryDetails(id, date, weight, notes, image);
            });

            dateStripContainer.addView(dateView);
        }

        // Automatically select the last (most recent) entry
        cursor.moveToLast();
        displayEntryDetails(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        cursor.close();
    }

    private void displayEntryDetails(int id, String date, String weight, String notes, String imageUri) {
        displayDate.setText(date);
        displayWeight.setText(weight + " lbs");
        displayNotes.setText(notes.isEmpty() ? "No notes for this day" : notes);
        displayImage.setImageDrawable(null);

        if (imageUri != null && !imageUri.isEmpty()) {
            displayImage.setVisibility(View.VISIBLE);
            displayImage.setImageURI(android.net.Uri.parse(imageUri));
        } else {
            displayImage.setVisibility(View.GONE);
        }

        btnDeleteEntry.setVisibility(View.VISIBLE);

        btnDeleteEntry.setOnClickListener(v -> {
            if (dbHelper.deleteWeightEntry(id)) {
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                loadTableData();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData().toString();
            // Persist permissions if using long-term storage
            getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

}

