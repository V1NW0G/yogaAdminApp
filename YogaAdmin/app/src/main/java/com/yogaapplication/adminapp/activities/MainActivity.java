package com.yogaapplication.adminapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yogaapplication.adminapp.R;
import com.yogaapplication.adminapp.helper.YogaDatabaseHelper;
import com.yogaapplication.adminapp.models.Course;
import com.yogaapplication.adminapp.adapters.CourseAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText searchBar;
    private Button addButton;
    private TextView noCoursesText;
    private RecyclerView courseRecyclerView;
    private YogaDatabaseHelper dbHelper;
    private CourseAdapter courseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new YogaDatabaseHelper(this);

        searchBar = findViewById(R.id.search_bar);
        addButton = findViewById(R.id.add_button);
        noCoursesText = findViewById(R.id.no_courses_text);
        courseRecyclerView = findViewById(R.id.course_recycler_view);

        // Set up RecyclerView
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load and display courses
        loadCourses();

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddCourseActivity.class);
            startActivity(intent);
        });
    }

    private void loadCourses() {
        List<Course> courseList = dbHelper.getAllCourses();
        if (courseList.isEmpty()) {
            noCoursesText.setVisibility(View.VISIBLE);
            courseRecyclerView.setVisibility(View.GONE);
        } else {
            noCoursesText.setVisibility(View.GONE);
            courseRecyclerView.setVisibility(View.VISIBLE);
            courseAdapter = new CourseAdapter(courseList);
            courseRecyclerView.setAdapter(courseAdapter);
        }
    }
}