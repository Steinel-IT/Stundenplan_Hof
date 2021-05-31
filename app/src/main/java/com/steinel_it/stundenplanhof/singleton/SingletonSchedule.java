package com.steinel_it.stundenplanhof.singleton;

import com.steinel_it.stundenplanhof.objects.LectureEntry;
import com.steinel_it.stundenplanhof.objects.SchedulerEntry;
import com.steinel_it.stundenplanhof.objects.SchedulerFilter;

import java.util.ArrayList;
import java.util.HashMap;

public class SingletonSchedule {

    private SchedulerFilter filterType = SchedulerFilter.DAYS;

    private ArrayList<String> titleList;
    private ArrayList<SchedulerEntry> scheduleList;

    //Original Sorted Schedule
    private ArrayList<String> dayTitle;
    private ArrayList<SchedulerEntry> daySortedSchedule;

    private static final SingletonSchedule instance = new SingletonSchedule();

    public static SingletonSchedule getInstance() {
        return instance;
    }

    private SingletonSchedule() {
    }

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

    public ArrayList<SchedulerEntry> getDaySortedSchedule() {
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
            if (filterType == SchedulerFilter.DAYS) {
                titleList.addAll(dayTitle);
                scheduleList.addAll(daySortedSchedule);
                return;
            }
            //TODO: In Methode extrahieren
            //Explicit Sort: LECTURE, ROOMS, LECTURER
            for (LectureEntry entry : getAllCourses()) {

                String sortItem = "";

                //Set sorting Type
                if (filterType == SchedulerFilter.LECTURE)
                    sortItem = entry.getShortName();
                else if (filterType == SchedulerFilter.ROOMS)
                    sortItem = entry.getRoom();
                else if (filterType == SchedulerFilter.LECTURER)
                    sortItem = entry.getLecturer();


                if (!sortedMap.containsKey(sortItem)) {
                    sortedMap.put(sortItem, new ArrayList<>());
                }
                sortedMap.get(sortItem).add(entry);
            }
            titleList.addAll(sortedMap.keySet());
            for (String key : sortedMap.keySet()) {
                scheduleList.add(new SchedulerEntry(sortedMap.get(key)));
            }
        }
    }

    private ArrayList<LectureEntry> getAllCourses() {
        ArrayList<LectureEntry> allCourses = new ArrayList<>();
        for (SchedulerEntry scheduleEntry : daySortedSchedule) {
            allCourses.addAll(scheduleEntry.getCourseEntryArrayList());
        }
        return allCourses;
    }

}
