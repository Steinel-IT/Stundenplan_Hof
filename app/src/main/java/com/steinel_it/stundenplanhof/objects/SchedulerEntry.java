package com.steinel_it.stundenplanhof.objects;

import java.util.ArrayList;

public class SchedulerEntry {
    ArrayList<CourseEntry> courseEntryArrayList;

    public SchedulerEntry(ArrayList<CourseEntry> courseEntryArrayList) {
        this.courseEntryArrayList = courseEntryArrayList;
    }

    public ArrayList<CourseEntry> getCourseEntryArrayList() {
        return courseEntryArrayList;
    }

    @Override
    public String toString() {
        return "SchedulerEntry{" +
                "courseEntryArrayList=" + courseEntryArrayList +
                '}';
    }
}
