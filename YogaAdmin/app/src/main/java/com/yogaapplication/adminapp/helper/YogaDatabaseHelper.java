package com.yogaapplication.adminapp.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.yogaapplication.adminapp.models.Course;
import java.util.ArrayList;
import java.util.List;

public class YogaDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "YogaAdmin.db";
    private static final int DATABASE_VERSION = 2;

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

    // SQL statement to create the courses table
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
                    COLUMN_DESCRIPTION + " TEXT);";

    public YogaDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add the new course_id column to the existing table
            db.execSQL("ALTER TABLE " + TABLE_COURSES + " ADD COLUMN " + COLUMN_COURSE_ID + " INTEGER NOT NULL DEFAULT 0;");
        }
    }

    // Method to insert a new course
    public long insertCourse(int courseId, String day, String time, int capacity, int duration, double price, String type, String description) {
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

        long result = db.insert(TABLE_COURSES, null, values);
        db.close();
        return result;
    }

    // Method to search courses by name (with LIKE)
    public List<Course> searchCoursesByName(String name) {
        List<Course> courseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_COURSES + " WHERE " + COLUMN_TYPE + " LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{"%" + name + "%"});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                int courseId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COURSE_ID));
                String day = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME));
                int capacity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));

                Course course = new Course(id, courseId, day, time, capacity, duration, price, type, description);
                courseList.add(course);
            }
            cursor.close();
        }

        db.close();
        return courseList;
    }

    // Method to retrieve all courses
    public List<Course> getAllCourses() {
        List<Course> courseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_COURSES, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                int courseId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COURSE_ID));
                String day = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME));
                int capacity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));

                Course course = new Course(id, courseId, day, time, capacity, duration, price, type, description);
                courseList.add(course);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return courseList;
    }

    // Method to delete a course by ID
    public int deleteCourse(int courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_COURSES, COLUMN_ID + "=?", new String[]{String.valueOf(courseId)});
        db.close();
        return rowsDeleted;
    }

    // Method to update a course
    public int updateCourse(int id, int courseId, String day, String time, int capacity, int duration, double price, String type, String description) {
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

        int rowsUpdated = db.update(TABLE_COURSES, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsUpdated;
    }
}