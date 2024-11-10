package com.yogaapplication.adminapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.yogaapplication.adminapp.R;

public class MainActivity extends AppCompatActivity {

    private EditText searchBar;
    private Button addButton;
    private TextView noCoursesText;
    private RecyclerView courseRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBar = findViewById(R.id.search_bar);
        addButton = findViewById(R.id.add_button);
        noCoursesText = findViewById(R.id.no_courses_text);
        courseRecyclerView = findViewById(R.id.course_recycler_view);

        // Check if course list is empty (you can add logic to check database or array list here)
        boolean isCourseListEmpty = true; // Placeholder for actual check
        if (isCourseListEmpty) {
            noCoursesText.setVisibility(View.VISIBLE);
            courseRecyclerView.setVisibility(View.GONE);
        } else {
            noCoursesText.setVisibility(View.GONE);
            courseRecyclerView.setVisibility(View.VISIBLE);
            // Code to set up RecyclerView adapter and display courses
        }

        // Set up Add button to navigate to AddCourseActivity
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCourseActivity.class);
                startActivity(intent);
            }
        });
    }
}