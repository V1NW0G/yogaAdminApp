package com.yogaapplication.adminapp.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.yogaapplication.adminapp.R;
import com.yogaapplication.adminapp.helper.YogaDatabaseHelper;

import java.util.Calendar;

public class AddCourseActivity extends AppCompatActivity {
    private EditText editTextDate, editTextTime, editTextCapacity, editTextDuration, editTextPrice, editTextType, editTextDescription;
    private TextView textViewCurrencySymbol;
    private Button buttonDecreaseCapacity, buttonIncreaseCapacity, buttonSaveCourse;
    private YogaDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        dbHelper = new YogaDatabaseHelper(this);

        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextCapacity = findViewById(R.id.editTextCapacity);
        editTextDuration = findViewById(R.id.editTextDuration);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextType = findViewById(R.id.editTextType);
        editTextDescription = findViewById(R.id.editTextDescription);
        textViewCurrencySymbol = findViewById(R.id.textViewCurrencySymbol);
        buttonDecreaseCapacity = findViewById(R.id.buttonDecreaseCapacity);
        buttonIncreaseCapacity = findViewById(R.id.buttonIncreaseCapacity);
        buttonSaveCourse = findViewById(R.id.buttonSaveCourse);

        // Set up Date Picker
        editTextDate.setOnClickListener(v -> showDatePicker());

        // Set up Time Picker
        editTextTime.setOnClickListener(v -> showTimePicker());

        // Set up Increase/Decrease Capacity
        buttonDecreaseCapacity.setOnClickListener(v -> adjustCapacity(-1));
        buttonIncreaseCapacity.setOnClickListener(v -> adjustCapacity(1));

        // Save Course Button Listener
        buttonSaveCourse.setOnClickListener(v -> saveCourse());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String date = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
            editTextDate.setText(date);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            String time = String.format("%02d:%02d", selectedHour, selectedMinute);
            editTextTime.setText(time);
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void adjustCapacity(int delta) {
        try {
            int currentCapacity = Integer.parseInt(editTextCapacity.getText().toString());
            int newCapacity = currentCapacity + delta;
            if (newCapacity >= 0) {
                editTextCapacity.setText(String.valueOf(newCapacity));
            } else {
                Toast.makeText(this, "Capacity cannot be negative", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            editTextCapacity.setText("0");
        }
    }

    private void saveCourse() {
        // Retrieve and validate inputs
        String date = editTextDate.getText().toString().trim();
        String time = editTextTime.getText().toString().trim();
        String capacityStr = editTextCapacity.getText().toString().trim();
        String durationStr = editTextDuration.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String type = editTextType.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (date.isEmpty() || time.isEmpty() || capacityStr.isEmpty() || durationStr.isEmpty() ||
                priceStr.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        int capacity = Integer.parseInt(capacityStr);
        int duration = Integer.parseInt(durationStr);
        double price = Double.parseDouble(priceStr);

        // Insert data into the database
        long result = dbHelper.insertCourse(date, time, capacity, duration, price, type, description);

        if (result != -1) {
            Toast.makeText(this, "Course added successfully!", Toast.LENGTH_SHORT).show();
            // Return to MainActivity to display the new course
            Intent intent = new Intent(AddCourseActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close this activity
        } else {
            Toast.makeText(this, "Failed to add course. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}