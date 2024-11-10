package com.yogaapplication.adminapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.yogaapplication.adminapp.R;
import com.yogaapplication.adminapp.models.Course;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courseList;

    public CourseAdapter(List<Course> courseList) {
        this.courseList = courseList;
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseIdText.setText("Course ID: " + course.getCourseId());  // Set CourseID text
        holder.courseNameText.setText(course.getType());
        holder.courseDateText.setText(course.getDay());
        holder.courseTimeText.setText(course.getTime());
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        public TextView courseIdText, courseNameText, courseDateText, courseTimeText;

        public CourseViewHolder(View itemView) {
            super(itemView);
            courseIdText = itemView.findViewById(R.id.course_id);  // Initialize CourseID TextView
            courseNameText = itemView.findViewById(R.id.course_name);
            courseDateText = itemView.findViewById(R.id.course_date);
            courseTimeText = itemView.findViewById(R.id.course_time);
        }
    }
}