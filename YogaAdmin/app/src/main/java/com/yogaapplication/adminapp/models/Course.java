package com.yogaapplication.adminapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Course implements Parcelable {
    private int id;
    private int courseId;
    private String day;
    private String time;
    private int capacity;
    private int duration;
    private double price;
    private String type;
    private String description;
    private String tutorName;

    // Constructor with all parameters, including tutorName
    public Course(int id, int courseId, String day, String time, int capacity, int duration, double price, String type, String description, String tutorName) {
        this.id = id;
        this.courseId = courseId;
        this.day = day;
        this.time = time;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.type = type;
        this.description = description;
        this.tutorName = tutorName;
    }

    // Parcelable constructor
    protected Course(Parcel in) {
        id = in.readInt();
        courseId = in.readInt();
        day = in.readString();
        time = in.readString();
        capacity = in.readInt();
        duration = in.readInt();
        price = in.readDouble();
        type = in.readString();
        description = in.readString();
        tutorName = in.readString();
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    // Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeInt(courseId);
        parcel.writeString(day);
        parcel.writeString(time);
        parcel.writeInt(capacity);
        parcel.writeInt(duration);
        parcel.writeDouble(price);
        parcel.writeString(type);
        parcel.writeString(description);
        parcel.writeString(tutorName);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }
}