package com.yogaapplication.adminapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
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

        // Load all courses initially
        loadCourses();

        // Set up Add button to navigate to AddCourseActivity
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddCourseActivity.class);
            startActivity(intent);
        });

        // Search bar functionality
        searchBar.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!TextUtils.isEmpty(query)) {
                    searchCourses(query); // Call search function with the query
                } else {
                    loadCourses(); // Reload all courses if search query is empty
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
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

    private void searchCourses(String name) {
        List<Course> courseList = dbHelper.searchCoursesByName(name);
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