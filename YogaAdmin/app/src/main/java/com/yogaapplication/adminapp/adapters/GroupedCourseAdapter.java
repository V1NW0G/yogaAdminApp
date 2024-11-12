package com.yogaapplication.adminapp.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;
import com.yogaapplication.adminapp.R;
import com.yogaapplication.adminapp.activities.AddCourseActivity;
import com.yogaapplication.adminapp.models.Course;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class GroupedCourseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private final Map<Integer, List<Course>> groupedCourses;
    private final Set<Integer> selectedCourseIds = new HashSet<>();
    private final Set<Course> selectedItems = new HashSet<>();
    private boolean isEditMode = false;
    private final OnDeleteListener onDeleteListener;
    private OnAddClassClickListener onAddClassClickListener;

    public GroupedCourseAdapter(Map<Integer, List<Course>> groupedCourses, OnDeleteListener onDeleteListener) {
        this.groupedCourses = groupedCourses;
        this.onDeleteListener = onDeleteListener;
    }

    public interface OnDeleteListener {
        void onDeleteCourse(Course course);
        void onDeleteCourseGroup(int courseId);
    }

    public interface OnAddClassClickListener {
        void onAddClass(int courseId);
    }

    public void setOnAddClassClickListener(OnAddClassClickListener listener) {
        this.onAddClassClickListener = listener;
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
        if (!isEditMode) {
            clearSelections();
        }
        notifyDataSetChanged();
    }

    public void clearSelections() {
        selectedCourseIds.clear();
        selectedItems.clear();
    }

    public Set<Course> getSelectedItems() {
        return new HashSet<>(selectedItems);
    }

    public Set<Integer> getSelectedCourseIds() {
        return new HashSet<>(selectedCourseIds);
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
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

            headerHolder.courseIdText.setText("Course ID (" + courseId + ")");
            headerHolder.courseIdCheckBox.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
            headerHolder.addClassButton.setVisibility(isEditMode ? View.VISIBLE : View.GONE);

            headerHolder.courseIdCheckBox.setOnCheckedChangeListener(null);
            headerHolder.courseIdCheckBox.setChecked(selectedCourseIds.contains(courseId));
            headerHolder.courseIdCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectAllUnderCourseId(courseId);
                } else {
                    deselectAllUnderCourseId(courseId);
                }
                notifyDataSetChanged();
            });

            headerHolder.addClassButton.setOnClickListener(v -> {
                if (onAddClassClickListener != null) {
                    onAddClassClickListener.onAddClass(courseId);
                }
            });

            headerHolder.itemView.setOnLongClickListener(v -> {
                onDeleteListener.onDeleteCourseGroup(courseId);
                return true;
            });

        } else if (holder instanceof ItemViewHolder) {
            Course course = getCourseForPosition(position);
            ItemViewHolder itemHolder = (ItemViewHolder) holder;

            itemHolder.courseTypeText.setText("Type: " + course.getType());
            itemHolder.courseDayDateText.setText(getDayAndDate(course.getDay()));
            itemHolder.courseTimeText.setText("Time: " + course.getTime());
            itemHolder.courseTutorText.setText("Tutor: " + (course.getTutorName() == null ? "N/A" : course.getTutorName()));
            itemHolder.courseDurationText.setText("Duration: " + formatDuration(course.getDuration()));

            itemHolder.courseCheckBox.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
            itemHolder.courseCheckBox.setOnCheckedChangeListener(null);
            itemHolder.courseCheckBox.setChecked(selectedItems.contains(course));
            itemHolder.courseCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedItems.add(course);
                } else {
                    selectedItems.remove(course);
                }
                notifyDataSetChanged();
            });

            int borderColor = selectedItems.contains(course)
                    ? ContextCompat.getColor(itemHolder.itemView.getContext(), R.color.primaryColor)
                    : Color.LTGRAY;

            setCardBorderColor(itemHolder.cardView, borderColor);

            itemHolder.itemView.setOnLongClickListener(v -> {
                onDeleteListener.onDeleteCourse(course);
                return true;
            });
        }
    }

    private void setCardBorderColor(CardView cardView, int color) {
        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setCornerRadius(8);
        borderDrawable.setStroke(4, color);
        borderDrawable.setColor(Color.WHITE);
        cardView.setBackground(borderDrawable);
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

    private void selectAllUnderCourseId(int courseId) {
        selectedCourseIds.add(courseId);
        selectedItems.addAll(groupedCourses.get(courseId));
    }

    private void deselectAllUnderCourseId(int courseId) {
        selectedCourseIds.remove(courseId);
        selectedItems.removeAll(groupedCourses.get(courseId));
    }

    // Helper to format the date as "Day of the Week\nYYYY-MM-DD"
    private String getDayAndDate(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE\nyyyy-MM-dd", Locale.getDefault());

        try {
            return outputFormat.format(inputFormat.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString; // fallback to raw date if parsing fails
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView courseIdText;
        CheckBox courseIdCheckBox;
        ImageButton addClassButton;

        HeaderViewHolder(View itemView) {
            super(itemView);
            courseIdText = itemView.findViewById(R.id.course_id);
            courseIdCheckBox = itemView.findViewById(R.id.course_id_checkbox);
            addClassButton = itemView.findViewById(R.id.add_class_button);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView courseTypeText, courseDayDateText, courseTimeText, courseTutorText, courseDurationText;
        CheckBox courseCheckBox;
        CardView cardView;

        ItemViewHolder(View itemView) {
            super(itemView);
            courseTypeText = itemView.findViewById(R.id.course_type);
            courseDayDateText = itemView.findViewById(R.id.course_day_date);
            courseTimeText = itemView.findViewById(R.id.course_time);
            courseTutorText = itemView.findViewById(R.id.course_tutor);
            courseDurationText = itemView.findViewById(R.id.course_duration);
            courseCheckBox = itemView.findViewById(R.id.course_checkbox);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}