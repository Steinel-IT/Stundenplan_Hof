package com.steinel_it.stundenplanhof.objects;

public enum SchedulerFilter {
    DAYS("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"),
    LECTURE,
    ROOMS,
    LECTURER;

    private final String[] titel;

    SchedulerFilter(String... titel) {
        this.titel = titel;
    }

    //Deprecated
//    public ArrayList<String> getTitel() {
//        ArrayList<String> resultList = new ArrayList<>();
//        Collections.addAll(resultList, titel);
//        return resultList;
//    }
}
