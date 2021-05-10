package com.steinel_it.stundenplanhof.objects;

import java.util.ArrayList;
import java.util.Collections;

public enum SchedulerFilter {
    DAYS("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"),
    VORLESUNGEN,
    ROOMS,
    DOZENTEN;

    private final String[] titel;

    SchedulerFilter(String... titel) {
        this.titel = titel;
    }

    public ArrayList<String> getTitel() {
        ArrayList<String> resultList = new ArrayList<>();
        Collections.addAll(resultList, titel);
        return resultList;
    }
}
