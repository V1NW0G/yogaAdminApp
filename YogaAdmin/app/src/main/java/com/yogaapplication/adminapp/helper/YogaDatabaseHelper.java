package com.yogaapplication.adminapp.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.yogaapplication.adminapp.models.Course;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class YogaDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "YogaAdmin.db";
    private static final int DATABASE_VERSION = 3;

    // Table and column names
    public static final String TABLE_COURSES = "courses";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COURSE_ID = "course_id";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_CAPACITY = "capacity";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_TUTOR_NAME = "tutor_name";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_COURSES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_COURSE_ID + " INTEGER NOT NULL, " +
                    COLUMN_DAY + " TEXT NOT NULL, " +
                    COLUMN_TIME + " TEXT NOT NULL, " +
                    COLUMN_CAPACITY + " INTEGER NOT NULL, " +
                    COLUMN_DURATION + " INTEGER NOT NULL, " +
                    COLUMN_PRICE + " REAL NOT NULL, " +
                    COLUMN_TYPE + " TEXT NOT NULL, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_TUTOR_NAME + " TEXT);";

    public YogaDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_COURSES + " ADD COLUMN " + COLUMN_TUTOR_NAME + " TEXT;");
        }
    }

    // Helper method to get the day of the week from a date string
    private String getDayOfWeek(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        try {
            return dayFormat.format(dateFormat.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
            return ""; // Return empty if parsing fails
        }
    }

    // Insert a new course
    public long insertCourse(int courseId, String day, String time, int capacity, int duration, double price, String type, String description, String tutorName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_COURSE_ID, courseId);
        values.put(COLUMN_DAY, day);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_CAPACITY, capacity);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_TUTOR_NAME, tutorName);

        long result = db.insert(TABLE_COURSES, null, values);
        db.close();
        return result;
    }

    // Search courses by Course Type or Tutor Name
    public List<Course> searchCoursesByName(String query) {
        List<Course> courseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String searchQuery = "SELECT * FROM " + TABLE_COURSES +
                " WHERE " + COLUMN_TYPE + " LIKE ? OR " + COLUMN_TUTOR_NAME + " LIKE ?";
        String likeQuery = "%" + query + "%";
        Cursor cursor = db.rawQuery(searchQuery, new String[]{likeQuery, likeQuery});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                courseList.add(createCourseFromCursor(cursor));
            }
            cursor.close();
        }
        db.close();
        return courseList;
    }

    // Search courses by day of the week
    public List<Course> searchCoursesByDayOfWeek(String dayOfWeek) {
        List<Course> courseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_COURSES;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Course course = createCourseFromCursor(cursor);
                if (getDayOfWeek(course.getDay()).equalsIgnoreCase(dayOfWeek)) {
                    courseList.add(course);
                }
            }
            cursor.close();
        }
        db.close();
        return courseList;
    }

    // Search courses by day of the week and name
    public List<Course> searchCoursesByDayOfWeekAndName(String dayOfWeek, String query) {
        List<Course> courseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String likeQuery = "%" + query + "%";
        String searchQuery = "SELECT * FROM " + TABLE_COURSES;
        Cursor cursor = db.rawQuery(searchQuery, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Course course = createCourseFromCursor(cursor);
                if (getDayOfWeek(course.getDay()).equalsIgnoreCase(dayOfWeek) &&
                        (course.getType().contains(query) || course.getTutorName().contains(query))) {
                    courseList.add(course);
                }
            }
            cursor.close();
        }
        db.close();
        return courseList;
    }

    // Retrieve all courses
    public List<Course> getAllCourses() {
        List<Course> courseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_COURSES, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                courseList.add(createCourseFromCursor(cursor));
            } while (cursor.moveToNext());

            cursor.close();
        }
        db.close();
        return courseList;
    }

    // Helper to create a Course object from a cursor
    private Course createCourseFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        int courseId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COURSE_ID));
        String day = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY));
        String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME));
        int capacity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY));
        int duration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
        String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
        String tutorName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TUTOR_NAME));

        return new Course(id, courseId, day, time, capacity, duration, price, type, description, tutorName);
    }

    // Delete a specific course by its unique ID
    public int deleteCourse(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_COURSES, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted;
    }

    // Delete all classes under a specific Course ID
    public int deleteCourseByCourseId(int courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_COURSES, COLUMN_COURSE_ID + "=?", new String[]{String.valueOf(courseId)});
        db.close();
        return rowsDeleted;
    }

    // Update a course using a Course object
    public int updateCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_COURSE_ID, course.getCourseId());
        values.put(COLUMN_DAY, course.getDay());
        values.put(COLUMN_TIME, course.getTime());
        values.put(COLUMN_CAPACITY, course.getCapacity());
        values.put(COLUMN_DURATION, course.getDuration());
        values.put(COLUMN_PRICE, course.getPrice());
        values.put(COLUMN_TYPE, course.getType());
        values.put(COLUMN_DESCRIPTION, course.getDescription());
        values.put(COLUMN_TUTOR_NAME, course.getTutorName());

        int rowsUpdated = db.update(TABLE_COURSES, values, COLUMN_ID + "=?", new String[]{String.valueOf(course.getId())});
        db.close();
        return rowsUpdated;
    }
}