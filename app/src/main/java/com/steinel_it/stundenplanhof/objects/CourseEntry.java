package com.steinel_it.stundenplanhof.objects;;

public class CourseEntry {
    private final String day, timeStart, timeEnd, name, shortName, dozent, room, gebaeude;

    public CourseEntry(String day, String timeStart, String timeEnd, String name, String shortName, String dozent, String room, String gebaeude) {
        this.day = day;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.name = name;
        this.shortName = shortName;
        this.dozent = dozent;
        this.room = room;
        this.gebaeude = gebaeude;
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

    public String getGebaeude() {
        return gebaeude;
    }

    @Override
    public String toString() {
        return "CourseEntry{" +
                "timeStart='" + timeStart + '\'' +
                ", timeEnd='" + timeEnd + '\'' +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", dozent='" + dozent + '\'' +
                ", room='" + room + '\'' +
                ", gebaeude='" + gebaeude + '\'' +
                '}';
    }
}
