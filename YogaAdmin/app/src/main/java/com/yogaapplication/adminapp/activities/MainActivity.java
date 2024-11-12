package com.yogaapplication.adminapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
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
    private Button updateButton, addActionButton, updateActionButton, deleteActionButton;
    private TextView noCoursesText, advancedSearchToggle, clearText;
    private RecyclerView courseRecyclerView;
    private Spinner dayFilterSpinner;
    private YogaDatabaseHelper dbHelper;
    private GroupedCourseAdapter courseAdapter;
    private View actionButtonsContainer, advancedSearchLayout;
    private boolean isEditMode = false;
    private String selectedDay = ""; // Holds selected day filter

    private static final int REQUEST_CODE_UPDATE = 1;
    private static final int REQUEST_CODE_ADD_COURSE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new YogaDatabaseHelper(this);

        // Initialize UI elements
        searchBar = findViewById(R.id.search_bar);
        updateButton = findViewById(R.id.update_button);
        clearText = findViewById(R.id.clear_text); // Clear text for search
        noCoursesText = findViewById(R.id.no_courses_text);
        courseRecyclerView = findViewById(R.id.course_recycler_view);
        actionButtonsContainer = findViewById(R.id.action_buttons_container);
        addActionButton = findViewById(R.id.add_button_secondary);
        updateActionButton = findViewById(R.id.update_secondary_button);
        deleteActionButton = findViewById(R.id.delete_button);
        advancedSearchToggle = findViewById(R.id.advanced_search_toggle);
        advancedSearchLayout = findViewById(R.id.advanced_search_layout);
        dayFilterSpinner = findViewById(R.id.day_filter_spinner);

        actionButtonsContainer.setVisibility(View.GONE);
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadCourses();
        setupAdvancedSearch();

        updateButton.setOnClickListener(v -> toggleEditMode());

        addActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddCourseActivity.class);
            startActivity(intent);
        });

        updateActionButton.setOnClickListener(v -> updateSelectedCourses());

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
                performSearch();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Clear text functionality
        clearText.setOnClickListener(v -> {
            searchBar.setText("");             // Clear search bar
            dayFilterSpinner.setSelection(0);  // Reset spinner to the default option
            selectedDay = "";                  // Reset the selected day filter
            loadCourses();                     // Reload all courses
        });
    }

    private void setupAdvancedSearch() {
        // Set the initial text with "Advanced Search ▼"
        advancedSearchToggle.setText("Advanced Search ▼");

        // Toggle visibility of the advanced search layout
        advancedSearchToggle.setOnClickListener(v -> {
            if (advancedSearchLayout.getVisibility() == View.GONE) {
                advancedSearchLayout.setVisibility(View.VISIBLE);
                advancedSearchToggle.setText("Advanced Search ▲"); // Show "▲" when expanded
            } else {
                advancedSearchLayout.setVisibility(View.GONE);
                advancedSearchToggle.setText("Advanced Search ▼"); // Show "▼" when collapsed
            }
        });

        // Set up the day filter spinner with days of the week
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.days_of_week, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayFilterSpinner.setAdapter(adapter);

        // Set selected day when a day is chosen
        dayFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDay = position == 0 ? "" : parent.getItemAtPosition(position).toString();
                performSearch(); // Refresh search results when day filter changes
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;
        actionButtonsContainer.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        courseAdapter.setEditMode(isEditMode);
    }

    private void loadCourses() {
        List<Course> courseList = dbHelper.getAllCourses();
        displayCourses(courseList);
    }

    private void performSearch() {
        String query = searchBar.getText().toString().trim();
        List<Course> filteredCourses;

        if (!TextUtils.isEmpty(selectedDay) && !TextUtils.isEmpty(query)) {
            filteredCourses = dbHelper.searchCoursesByDayOfWeekAndName(selectedDay, query);
        } else if (!TextUtils.isEmpty(selectedDay)) {
            filteredCourses = dbHelper.searchCoursesByDayOfWeek(selectedDay);
        } else {
            filteredCourses = dbHelper.searchCoursesByName(query);
        }

        displayCourses(filteredCourses);
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
            courseAdapter.setOnAddClassClickListener(courseId -> navigateToAddClassWithCourseId(courseId));
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

    private void navigateToAddClassWithCourseId(int courseId) {
        Intent intent = new Intent(MainActivity.this, AddCourseActivity.class);
        intent.putExtra("courseId", courseId);
        startActivityForResult(intent, REQUEST_CODE_ADD_COURSE);
    }

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

    private void updateSelectedCourses() {
        List<Course> selectedCourses = new ArrayList<>(courseAdapter.getSelectedItems());
        if (!selectedCourses.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
            intent.putParcelableArrayListExtra("selectedCourses", new ArrayList<>(selectedCourses));
            startActivityForResult(intent, REQUEST_CODE_UPDATE);
        } else {
            Toast.makeText(this, "Please select courses to update", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE && resultCode == RESULT_OK) {
            loadCourses();
        } else if (requestCode == REQUEST_CODE_ADD_COURSE && resultCode == RESULT_OK) {
            loadCourses();
        }
    }
}