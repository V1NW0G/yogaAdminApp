package com.yogaapplication.adminapp.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yogaapplication.adminapp.R;
import com.yogaapplication.adminapp.models.Course;

import java.util.List;

public class UpdateCourseAdapter extends RecyclerView.Adapter<UpdateCourseAdapter.UpdateViewHolder> {

    private final List<Course> courses;

    public UpdateCourseAdapter(List<Course> courses) {
        this.courses = courses;
    }

    @NonNull
    @Override
    public UpdateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_update_course, parent, false);
        return new UpdateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateViewHolder holder, int position) {
        Course course = courses.get(position);

        // Set course details in the input fields
        holder.courseTypeInput.setText(course.getType());
        holder.courseDateInput.setText(course.getDay());
        holder.courseTimeInput.setText(course.getTime());
        holder.priceInput.setText(String.valueOf(course.getPrice()));
        holder.durationInput.setText(String.valueOf(course.getDuration()));
        holder.tutorInput.setText(course.getTutorName());

        // Watch for changes in each field and update the course object accordingly
        holder.courseTypeInput.addTextChangedListener(createTextWatcher(text -> course.setType(text)));
        holder.courseDateInput.addTextChangedListener(createTextWatcher(text -> course.setDay(text)));
        holder.courseTimeInput.addTextChangedListener(createTextWatcher(text -> course.setTime(text)));
        holder.priceInput.addTextChangedListener(createTextWatcher(text -> {
            double price = text.isEmpty() ? 0 : Double.parseDouble(text);
            course.setPrice(price);
        }));
        holder.durationInput.addTextChangedListener(createTextWatcher(text -> {
            int duration = text.isEmpty() ? 0 : Integer.parseInt(text);
            course.setDuration(duration);
        }));
        holder.tutorInput.addTextChangedListener(createTextWatcher(text -> course.setTutorName(text)));

        // Expand/collapse functionality
        holder.expandableLayout.setVisibility(View.GONE);
        holder.expandToggle.setOnClickListener(v -> {
            boolean isExpanded = holder.expandableLayout.getVisibility() == View.VISIBLE;
            holder.expandableLayout.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    // Helper to create a TextWatcher for capturing text changes
    private TextWatcher createTextWatcher(OnTextChanged onTextChanged) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onTextChanged.onTextChanged(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    // Functional interface for handling text changes
    private interface OnTextChanged {
        void onTextChanged(String text);
    }

    // Method to retrieve updated courses with the modified details
    public List<Course> getUpdatedCourses() {
        return courses;
    }

    public static class UpdateViewHolder extends RecyclerView.ViewHolder {
        EditText courseTypeInput, courseDateInput, courseTimeInput, priceInput, durationInput, tutorInput;
        View expandableLayout;
        ImageButton expandToggle;

        public UpdateViewHolder(View itemView) {
            super(itemView);

            // Initialize view components
            courseTypeInput = itemView.findViewById(R.id.course_type_input);
            courseDateInput = itemView.findViewById(R.id.course_date_input);
            courseTimeInput = itemView.findViewById(R.id.course_time_input);
            priceInput = itemView.findViewById(R.id.price_input);
            durationInput = itemView.findViewById(R.id.duration_input);
            tutorInput = itemView.findViewById(R.id.tutor_input);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);
            expandToggle = itemView.findViewById(R.id.expand_toggle);
        }
    }
}