package com.yogaapplication.adminapp.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
    private EditText editTextCourseId, editTextDate, editTextTime, editTextCapacity, editTextDuration, editTextPrice, editTextType, editTextDescription, editTextTutorName;
    private TextView textViewCurrencySymbol;
    private Button buttonDecreaseCapacity, buttonIncreaseCapacity, buttonSaveCourse;
    private YogaDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        dbHelper = new YogaDatabaseHelper(this);

        editTextCourseId = findViewById(R.id.editTextCourseId);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextCapacity = findViewById(R.id.editTextCapacity);
        editTextDuration = findViewById(R.id.editTextDuration);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextType = findViewById(R.id.editTextType);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextTutorName = findViewById(R.id.editTextTutorName);
        textViewCurrencySymbol = findViewById(R.id.textViewCurrencySymbol);
        buttonDecreaseCapacity = findViewById(R.id.buttonDecreaseCapacity);
        buttonIncreaseCapacity = findViewById(R.id.buttonIncreaseCapacity);
        buttonSaveCourse = findViewById(R.id.buttonSaveCourse);

        // Check if Course ID is provided and set it
        int prefilledCourseId = getIntent().getIntExtra("courseId", -1);
        if (prefilledCourseId != -1) {
            editTextCourseId.setText(String.valueOf(prefilledCourseId));
            editTextCourseId.setEnabled(false); // Disable editing if pre-filled
        }

        // Set up Date and Time Pickers
        editTextDate.setOnClickListener(v -> showDatePicker());
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
        String courseIdStr = editTextCourseId.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String time = editTextTime.getText().toString().trim();
        String capacityStr = editTextCapacity.getText().toString().trim();
        String durationStr = editTextDuration.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String type = editTextType.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String tutorName = editTextTutorName.getText().toString().trim();

        if (courseIdStr.isEmpty() || date.isEmpty() || time.isEmpty() || capacityStr.isEmpty() || durationStr.isEmpty() ||
                priceStr.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        int courseId = Integer.parseInt(courseIdStr);
        int capacity = Integer.parseInt(capacityStr);
        int duration = Integer.parseInt(durationStr);
        double price = Double.parseDouble(priceStr);

        // Insert data into the database
        long result = dbHelper.insertCourse(courseId, date, time, capacity, duration, price, type, description, tutorName);

        if (result != -1) {
            Toast.makeText(this, "Course added successfully!", Toast.LENGTH_SHORT).show();
            // Return to MainActivity to display the new course
            setResult(RESULT_OK); // Notify MainActivity to reload data
            finish(); // Close this activity
        } else {
            Toast.makeText(this, "Failed to add course. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}