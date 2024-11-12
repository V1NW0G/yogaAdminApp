package com.yogaapplication.adminapp.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yogaapplication.adminapp.R;
import com.yogaapplication.adminapp.adapters.UpdateCourseAdapter;
import com.yogaapplication.adminapp.helper.YogaDatabaseHelper;
import com.yogaapplication.adminapp.models.Course;
import java.util.List;

public class UpdateActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UpdateCourseAdapter updateCourseAdapter;
    private List<Course> selectedCourses;
    private YogaDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        dbHelper = new YogaDatabaseHelper(this);

        // Retrieve selected courses from the intent
        selectedCourses = getIntent().getParcelableArrayListExtra("selectedCourses");

        recyclerView = findViewById(R.id.update_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize and set adapter with the selected courses
        updateCourseAdapter = new UpdateCourseAdapter(selectedCourses);
        recyclerView.setAdapter(updateCourseAdapter);

        // Save button to update courses in the database
        findViewById(R.id.save_updates_button).setOnClickListener(v -> saveUpdates());
    }

    // UpdateActivity.java
    private void saveUpdates() {
        for (Course course : updateCourseAdapter.getUpdatedCourses()) {
            dbHelper.updateCourse(course); // Assuming this now uses the refactored `updateCourse` method
        }
        Toast.makeText(this, "Courses updated successfully", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK); // Set the result to OK
        finish(); // Close the UpdateActivity
    }
}