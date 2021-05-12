package com.steinel_it.stundenplanhof.singleton;

import com.steinel_it.stundenplanhof.objects.CourseEntry;
import com.steinel_it.stundenplanhof.objects.SchedulerEntry;
import com.steinel_it.stundenplanhof.objects.SchedulerFilter;

import java.util.ArrayList;
import java.util.HashMap;

public class SingletonSchedule {

    private SchedulerFilter filterType = SchedulerFilter.DAYS;
    private ArrayList<SchedulerEntry> scheduleList;
    private ArrayList<SchedulerEntry> daySortedSchedule; //Original Sorted Schedule
    private ArrayList<String> titleList;
    private ArrayList<String> dayTitle;

    private static final SingletonSchedule instance = new SingletonSchedule();
    public static SingletonSchedule getInstance() {
        return instance;
    }
    private SingletonSchedule() { }

    public ArrayList<SchedulerEntry> getScheduleList() {
        return scheduleList;
    }

    public void setDaySortedSchedule(ArrayList<SchedulerEntry> daySortedSchedule) {
        this.daySortedSchedule = daySortedSchedule;
    }

    public ArrayList<String> getTitleList() {
        return titleList;
    }

    public void setFilterType(SchedulerFilter filterType) {
        this.filterType = filterType;
    }

    public ArrayList<String> getDayTitle() {
        return dayTitle;
    }

    public void setDayTitle(ArrayList<String> dayTitle) {
        this.dayTitle = dayTitle;
    }

    public void sortSchedule() {
        if (daySortedSchedule != null) {
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
            HashMap<String, ArrayList<CourseEntry>> sortedMap = new HashMap<>();
            if (filterType == SchedulerFilter.VORLESUNGEN) {
                ArrayList<CourseEntry> allCourses = new ArrayList<>();
                getAllCourses(allCourses);
                for (CourseEntry entry : allCourses) {
                    if (!sortedMap.containsKey(entry.getShortName())) {
                        sortedMap.put(entry.getShortName(), new ArrayList<>());
                    }
                    sortedMap.get(entry.getShortName()).add(entry);
                }
                titleList.addAll(sortedMap.keySet());
                for (String key : sortedMap.keySet()) {
                    scheduleList.add(new SchedulerEntry(sortedMap.get(key)));
                }
            } else if (filterType == SchedulerFilter.ROOMS) {
                ArrayList<CourseEntry> allCourses = new ArrayList<>();
                getAllCourses(allCourses);
                for (CourseEntry entry : allCourses) {
                    if (!sortedMap.containsKey(entry.getRoom())) {
                        sortedMap.put(entry.getRoom(), new ArrayList<>());
                    }
                    sortedMap.get(entry.getRoom()).add(entry);
                }
                titleList.addAll(sortedMap.keySet());
                for (String key : sortedMap.keySet()) {
                    scheduleList.add(new SchedulerEntry(sortedMap.get(key)));
                }
            } else if (filterType == SchedulerFilter.DOZENTEN) {
                ArrayList<CourseEntry> allCourses = new ArrayList<>();
                getAllCourses(allCourses);
                for (CourseEntry entry : allCourses) {
                    if (!sortedMap.containsKey(entry.getDozent())) {
                        sortedMap.put(entry.getDozent(), new ArrayList<>());
                    }
                    sortedMap.get(entry.getDozent()).add(entry);
                }
                titleList.addAll(sortedMap.keySet());
                for (String key : sortedMap.keySet()) {
                    scheduleList.add(new SchedulerEntry(sortedMap.get(key)));
                }
            } else {
                titleList.addAll(dayTitle);
                scheduleList.addAll(daySortedSchedule);
            }
        }
    }

    private void getAllCourses(ArrayList<CourseEntry> allCourses) {
        for (SchedulerEntry scheduleEntry : daySortedSchedule) {
            allCourses.addAll(scheduleEntry.getCourseEntryArrayList());
        }
    }

}
