package com.steinel_it.stundenplanhof.objects;

public class LectureEntry {
    private final String day, timeStart, timeEnd, name, shortName, lecturer, room, building, type;
    private final boolean canceled;

    public LectureEntry(String day, String timeStart, String timeEnd, String name, String shortName, String lecturer, String room, String building, String type, boolean canceled) {
        this.day = day;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.name = name;
        this.shortName = shortName;
        this.lecturer = lecturer;
        this.room = room;
        this.building = building;
        this.type = type;
        this.canceled = canceled;
    }

    public String getDay() {
        return day;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLecturer() {
        return lecturer;
    }

    public String getRoom() {
        return room;
    }

    public String getBuilding() {
        return building;
    }

    public String getType() {
        return type;
    }

    public boolean getCanceled() {
        return canceled;
    }

    @Override
    public String toString() {
        return "LectureEntry{" +
                "day='" + day + '\'' +
                ", timeStart='" + timeStart + '\'' +
                ", timeEnd='" + timeEnd + '\'' +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", lecturer='" + lecturer + '\'' +
                ", room='" + room + '\'' +
                ", building='" + building + '\'' +
                ", type='" + type + '\'' +
                ", canceled=" + canceled +
                '}';
    }
}
