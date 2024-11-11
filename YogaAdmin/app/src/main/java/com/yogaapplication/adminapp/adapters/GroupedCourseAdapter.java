package com.yogaapplication.adminapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.yogaapplication.adminapp.R;
import com.yogaapplication.adminapp.models.Course;
import java.util.List;
import java.util.Map;

public class GroupedCourseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private final Map<Integer, List<Course>> groupedCourses;

    public GroupedCourseAdapter(Map<Integer, List<Course>> groupedCourses) {
        this.groupedCourses = groupedCourses;
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_course_id_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_course, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            int courseId = getCourseIdForPosition(position);
            ((HeaderViewHolder) holder).courseIdText.setText("Course ID (" + courseId + ")");
        } else if (holder instanceof ItemViewHolder) {
            Course course = getCourseForPosition(position);
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.courseTypeText.setText("Type: " + course.getType());
            itemHolder.courseDayDateText.setText(course.getDay());
            itemHolder.courseTimeText.setText("Time: " + course.getTime());
            itemHolder.courseTutorText.setText("Tutor: " + (course.getTutorName() == null ? "N/A" : course.getTutorName()));
            itemHolder.courseDurationText.setText("Duration: " + formatDuration(course.getDuration()));
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (Map.Entry<Integer, List<Course>> entry : groupedCourses.entrySet()) {
            count += 1 + entry.getValue().size();
        }
        return count;
    }

    private boolean isHeader(int position) {
        int index = 0;
        for (Map.Entry<Integer, List<Course>> entry : groupedCourses.entrySet()) {
            if (index == position) {
                return true;
            }
            index += 1 + entry.getValue().size();
            if (position < index) {
                return false;
            }
        }
        return false;
    }

    private int getCourseIdForPosition(int position) {
        int index = 0;
        for (Map.Entry<Integer, List<Course>> entry : groupedCourses.entrySet()) {
            if (index == position) {
                return entry.getKey();
            }
            index += 1 + entry.getValue().size();
            if (position < index) {
                return entry.getKey();
            }
        }
        return -1;
    }

    private Course getCourseForPosition(int position) {
        int index = 0;
        for (Map.Entry<Integer, List<Course>> entry : groupedCourses.entrySet()) {
            index += 1;
            if (position < index + entry.getValue().size()) {
                return entry.getValue().get(position - index);
            }
            index += entry.getValue().size();
        }
        return null;
    }

    private String formatDuration(int durationMinutes) {
        int hours = durationMinutes / 60;
        int minutes = durationMinutes % 60;
        return hours > 0 ? hours + " hr " + minutes + " mins" : minutes + " mins";
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView courseIdText;

        HeaderViewHolder(View itemView) {
            super(itemView);
            courseIdText = itemView.findViewById(R.id.course_id);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView courseTypeText, courseDayDateText, courseTimeText, courseTutorText, courseDurationText;

        ItemViewHolder(View itemView) {
            super(itemView);
            courseTypeText = itemView.findViewById(R.id.course_type);
            courseDayDateText = itemView.findViewById(R.id.course_day_date);
            courseTimeText = itemView.findViewById(R.id.course_time);
            courseTutorText = itemView.findViewById(R.id.course_tutor);
            courseDurationText = itemView.findViewById(R.id.course_duration);
        }
    }
}