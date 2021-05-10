package com.steinel_it.stundenplanhof.objects;

import java.time.LocalDateTime;
import java.util.Comparator;

public class Note {
    private final LocalDateTime saveDate;
    private String text;

    public Note(String text) {
        this.text = new String(text);
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

    //TODO: Geht hier static?
    public static Comparator<Note> sortComp = (note, note2) -> note.getSaveDate().compareTo(note2.getSaveDate());
}
