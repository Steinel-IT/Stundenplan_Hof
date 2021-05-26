package com.steinel_it.stundenplanhof.objects;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class SchedulerEntry {
    ArrayList<LectureEntry> lectureEntryArrayList;

    public SchedulerEntry(ArrayList<LectureEntry> lectureEntryArrayList) {
        this.lectureEntryArrayList = lectureEntryArrayList;
    }

    public ArrayList<LectureEntry> getCourseEntryArrayList() {
        return lectureEntryArrayList;
    }

    @NonNull
    @Override
    public String toString() {
        return "SchedulerEntry{" +
                "courseEntryArrayList=" + lectureEntryArrayList +
                '}';
    }
}
