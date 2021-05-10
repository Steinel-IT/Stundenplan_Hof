package com.steinel_it.stundenplanhof.objects;

import com.steinel_it.stundenplanhof.adapter.CourseEntryListAdapter;

public class CourseEntry {
    private final String timeStart, timeEnd, name, shortName, dozent, room, gebaeude;
    private CourseEntryListAdapter.VorlesungHolder.OnItemClickListener onItemClickListener;

    public CourseEntry(String timeStart, String timeEnd, String name, String shortName, String dozent, String room, String gebaeude) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.name = name;
        this.shortName = shortName;
        this.dozent = dozent;
        this.room = room;
        this.gebaeude = gebaeude;
    }

    public void setOnItemClickListener(CourseEntryListAdapter.VorlesungHolder.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
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

    public CourseEntryListAdapter.VorlesungHolder.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
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
                ", onItemClickListener=" + onItemClickListener +
                '}';
    }
}
