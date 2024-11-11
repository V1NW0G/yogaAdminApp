package com.yogaapplication.adminapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yogaapplication.adminapp.R;
import com.yogaapplication.adminapp.adapters.GroupedCourseAdapter;
import com.yogaapplication.adminapp.helper.YogaDatabaseHelper;
import com.yogaapplication.adminapp.models.Course;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GroupedCourseAdapter.OnDeleteListener {

    private EditText searchBar;
    private Button updateButton;
    private Button addActionButton, updateActionButton, deleteActionButton;
    private TextView noCoursesText;
    private RecyclerView courseRecyclerView;
    private YogaDatabaseHelper dbHelper;
    private GroupedCourseAdapter courseAdapter;
    private View actionButtonsContainer;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new YogaDatabaseHelper(this);

        searchBar = findViewById(R.id.search_bar);
        updateButton = findViewById(R.id.update_button);
        noCoursesText = findViewById(R.id.no_courses_text);
        courseRecyclerView = findViewById(R.id.course_recycler_view);
        actionButtonsContainer = findViewById(R.id.action_buttons_container);

        addActionButton = findViewById(R.id.add_button_secondary);
        updateActionButton = findViewById(R.id.update_secondary_button);
        deleteActionButton = findViewById(R.id.delete_button);

        actionButtonsContainer.setVisibility(View.GONE);

        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadCourses();

        updateButton.setOnClickListener(v -> {
            isEditMode = !isEditMode;
            actionButtonsContainer.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
            courseAdapter.setEditMode(isEditMode);
        });

        addActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddCourseActivity.class);
            startActivity(intent);
        });

        deleteActionButton.setOnClickListener(v -> {
            if (isEditMode) {
                new AlertDialog.Builder(this)
                        .setTitle("Delete Selected")
                        .setMessage("Are you sure you want to delete the selected items?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            deleteSelectedItems();
                            loadCourses();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        searchBar.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!TextUtils.isEmpty(query)) {
                    searchCourses(query);
                } else {
                    loadCourses();
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void loadCourses() {
        List<Course> courseList = dbHelper.getAllCourses();
        displayCourses(courseList);
    }

    private void searchCourses(String query) {
        List<Course> courseList = dbHelper.searchCoursesByName(query);
        displayCourses(courseList);
    }


    private void displayCourses(List<Course> courseList) {
        if (courseList.isEmpty()) {
            noCoursesText.setVisibility(View.VISIBLE);
            courseRecyclerView.setVisibility(View.GONE);
        } else {
            noCoursesText.setVisibility(View.GONE);
            courseRecyclerView.setVisibility(View.VISIBLE);

            Map<Integer, List<Course>> groupedCourses = groupCoursesByCourseId(courseList);
            courseAdapter = new GroupedCourseAdapter(groupedCourses, this);
            courseAdapter.setEditMode(isEditMode);
            courseRecyclerView.setAdapter(courseAdapter);
        }
    }

    private Map<Integer, List<Course>> groupCoursesByCourseId(List<Course> courseList) {
        Map<Integer, List<Course>> groupedCourses = new HashMap<>();
        for (Course course : courseList) {
            int courseId = course.getCourseId();
            if (!groupedCourses.containsKey(courseId)) {
                groupedCourses.put(courseId, new ArrayList<>());
            }
            groupedCourses.get(courseId).add(course);
        }
        return groupedCourses;
    }

    // Delete an individual class
    @Override
    public void onDeleteCourse(Course course) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Class")
                .setMessage("Are you sure you want to delete this class?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteCourse(course.getId());
                    loadCourses();
                    Toast.makeText(this, "Class deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Delete an entire course group
    @Override
    public void onDeleteCourseGroup(int courseId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Course")
                .setMessage("Are you sure you want to delete all classes under Course ID (" + courseId + ")?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteCourseByCourseId(courseId);
                    loadCourses();
                    Toast.makeText(this, "Course deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Delete selected items in edit mode
    private void deleteSelectedItems() {
        for (Course course : courseAdapter.getSelectedItems()) {
            dbHelper.deleteCourse(course.getId());
        }
        for (int courseId : courseAdapter.getSelectedCourseIds()) {
            dbHelper.deleteCourseByCourseId(courseId);
        }
        Toast.makeText(this, "Selected items deleted", Toast.LENGTH_SHORT).show();
        courseAdapter.clearSelections();
    }
}