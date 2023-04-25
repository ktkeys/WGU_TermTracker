package com.wgu.termtracker;

import java.util.Date;

public class Course {
    String title;
    int ID;
    Date startDate;
    Date endDate;
    CourseStatus status;

public Course(){}

public enum CourseStatus {
    INPROGESS,
    COMPLETED,
    DROPPED,
    PLANTOTAKE;
}

public Course (String title, int ID, Date startDate, Date endDate, CourseStatus status){
    this.title = title;
    this.ID = ID;
    this.startDate = startDate;
    this.endDate = endDate;
    this.status = status;
}

    public Course (String title, Date startDate, Date endDate, CourseStatus status){
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }
}
