package com.steinel_it.stundenplanhof.singleton;

import com.steinel_it.stundenplanhof.objects.LectureEntry;
import com.steinel_it.stundenplanhof.objects.ScheduleEntry;
import com.steinel_it.stundenplanhof.objects.ScheduleFilter;

import java.util.ArrayList;
import java.util.HashMap;

public class SingletonSchedule {

    private ScheduleFilter filterType = ScheduleFilter.DAYS;

    private ArrayList<String> titleList;
    private ArrayList<ScheduleEntry> scheduleList;

    //Original Sorted Schedule
    private ArrayList<String> dayTitle;
    private ArrayList<ScheduleEntry> daySortedSchedule;

    private static final SingletonSchedule instance = new SingletonSchedule();

    public static SingletonSchedule getInstance() {
        return instance;
    }

    private SingletonSchedule() {}

    public ArrayList<ScheduleEntry> getScheduleList() {
        return scheduleList;
    }

    public void setDaySortedSchedule(ArrayList<ScheduleEntry> daySortedSchedule) {
        this.daySortedSchedule = daySortedSchedule;
    }

    public ArrayList<String> getTitleList() {
        return titleList;
    }

    public void setFilterType(ScheduleFilter filterType) {
        this.filterType = filterType;
    }

    public ArrayList<String> getDayTitle() {
        return dayTitle;
    }

    public ArrayList<ScheduleEntry> getDaySortedSchedule() {
        return daySortedSchedule;
    }

    public void setDayTitle(ArrayList<String> dayTitle) {
        this.dayTitle = dayTitle;
    }

    public void sortSchedule() {
        if (daySortedSchedule != null) { //Data available
            //Setup Lists
            if (titleList == null) {
                titleList = new ArrayList<>();
            } else {
                titleList.clear();
            }
            if (scheduleList == null) {
                scheduleList = new ArrayList<>();
            } else {
                scheduleList.clear();
            }

            //Map for sorting
            HashMap<String, ArrayList<LectureEntry>> sortedMap = new HashMap<>();

            //Default Sort: DAYS
            if (filterType == ScheduleFilter.DAYS) {
                titleList.addAll(dayTitle);
                scheduleList.addAll(daySortedSchedule);
                return;
            }
            explicitSort(sortedMap);
            titleList.addAll(sortedMap.keySet());
            for (String key : sortedMap.keySet()) {
                scheduleList.add(new ScheduleEntry(sortedMap.get(key)));
            }
        }
    }

    private void explicitSort(HashMap<String, ArrayList<LectureEntry>> sortedMap) {
        for (LectureEntry entry : getAllCourses()) {

            String sortItem = "";

            //Set sorting Type
            if (filterType == ScheduleFilter.LECTURE)
                sortItem = entry.getShortName();
            else if (filterType == ScheduleFilter.ROOMS)
                sortItem = entry.getRoom();
            else if (filterType == ScheduleFilter.LECTURER)
                sortItem = entry.getLecturer();


            if (!sortedMap.containsKey(sortItem)) {
                sortedMap.put(sortItem, new ArrayList<>());
            }
            sortedMap.get(sortItem).add(entry);
        }
    }

    private ArrayList<LectureEntry> getAllCourses() {
        ArrayList<LectureEntry> allCourses = new ArrayList<>();
        for (ScheduleEntry scheduleEntry : daySortedSchedule) {
            allCourses.addAll(scheduleEntry.getCourseEntryArrayList());
        }
        return allCourses;
    }

}
