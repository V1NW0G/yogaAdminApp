package com.yogaapplication.adminapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.yogaapplication.adminapp.R;
import com.yogaapplication.adminapp.models.Course;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courseList;
    private static final SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat outputDateFormat = new SimpleDateFormat("EEEE, yyyy-MM-dd", Locale.getDefault());

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

        // Set Course Type
        holder.courseTypeText.setText("Type: " + course.getType());

        // Format date to include day of the week
        String formattedDate = formatDayAndDate(course.getDay());
        holder.courseDayDateText.setText(formattedDate);

        // Set Course Time
        holder.courseTimeText.setText("Time: " + course.getTime());

        // Set Tutor Name (Handle null or empty values)
        String tutorName = course.getTutorName();
        holder.courseTutorText.setText("Tutor: " + (tutorName == null || tutorName.isEmpty() ? "N/A" : tutorName));

        // Set Duration
        holder.courseDurationText.setText("Duration: " + formatDuration(course.getDuration()));
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    private String formatDayAndDate(String dateString) {
        try {
            Date date = inputDateFormat.parse(dateString);
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString; // Return original if parsing fails
        }
    }

    private String formatDuration(int durationMinutes) {
        int hours = durationMinutes / 60;
        int minutes = durationMinutes % 60;

        if (hours > 0) {
            return hours + " hr " + minutes + " mins";
        } else {
            return minutes + " mins";
        }
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        public TextView courseTimeText, courseDayDateText, courseTutorText, courseTypeText, courseDurationText;

        public CourseViewHolder(View itemView) {
            super(itemView);
            courseTimeText = itemView.findViewById(R.id.course_time);
            courseDayDateText = itemView.findViewById(R.id.course_day_date);
            courseTutorText = itemView.findViewById(R.id.course_tutor);
            courseTypeText = itemView.findViewById(R.id.course_type);
            courseDurationText = itemView.findViewById(R.id.course_duration);
        }
    }
}