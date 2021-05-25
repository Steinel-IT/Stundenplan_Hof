package com.steinel_it.stundenplanhof.objects;

public class LectureEntry {
    private final String day, timeStart, timeEnd, name, shortName, dozent, room, building;
    private final boolean canceled;

    public LectureEntry(String day, String timeStart, String timeEnd, String name, String shortName, String dozent, String room, String building, boolean canceled) {
        this.day = day;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.name = name;
        this.shortName = shortName;
        this.dozent = dozent;
        this.room = room;
        this.building = building;
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

    public String getDozent() {
        return dozent;
    }

    public String getRoom() {
        return room;
    }

    public String getBuilding() {
        return building;
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
                ", dozent='" + dozent + '\'' +
                ", room='" + room + '\'' +
                ", building='" + building + '\'' +
                ", canceled=" + canceled +
                '}';
    }
}
