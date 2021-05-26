package com.steinel_it.stundenplanhof.objects;

import java.time.LocalDateTime;
import java.util.Comparator;

public class Note implements Comparator<Note>{
    private final LocalDateTime saveDate;
    private String text;

    public Note(String text) {
        this.text = text;
        this.saveDate = LocalDateTime.now();
    }

    public Note(String preset, LocalDateTime presetDate) {
        text = preset;
        saveDate = presetDate;
    }

    public LocalDateTime getSaveDate() {
        return saveDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int compare(Note n1, Note n2) {
        return n1.getSaveDate().compareTo(n2.getSaveDate());
    }
}
