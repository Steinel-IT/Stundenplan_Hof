package com.steinel_it.stundenplanhof.data_manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.steinel_it.stundenplanhof.objects.Note;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class StorageManager {

    public ArrayList<Note> getAllNotes(Context context, String fileName) {
        SharedPreferences sharedPreferencesNote = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        ArrayList<Note> resultNotes = new ArrayList<>();
        Map<String, ?> savedMap = sharedPreferencesNote.getAll();
        for (Map.Entry<String, ?> entry : savedMap.entrySet()) {
            resultNotes.add(new Note(entry.getValue().toString(), LocalDateTime.parse(entry.getKey())));
        }
        resultNotes.stream().sorted();
        return resultNotes;
    }

    public void saveNote(Note note, Context context, String fileName) {
        SharedPreferences sharedPreferencesNote = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesNoteEditor = sharedPreferencesNote.edit();
        sharedPreferencesNoteEditor.putString(note.getSaveDate().toString(), note.getText());
        sharedPreferencesNoteEditor.apply();
    }

    public void editNote(Note note, Context context, String fileName) {
        SharedPreferences sharedPreferencesNote = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesNoteEditor = sharedPreferencesNote.edit();
        deleteNote(note, context, fileName);
        saveNote(note, context, fileName);
        sharedPreferencesNoteEditor.apply();
    }

    public void deleteNote(Note notes, Context context, String fileName) {
        SharedPreferences sharedPreferencesNote = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesNoteEditor = sharedPreferencesNote.edit();
        sharedPreferencesNoteEditor.remove(notes.getSaveDate().toString());
        sharedPreferencesNoteEditor.apply();
    }

    public void saveSetupData(Context context, String fileName, String course, String shortCourse, String semester) {
        SharedPreferences sharedPreferencesNote = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesNoteEditor = sharedPreferencesNote.edit();
        sharedPreferencesNoteEditor.putString("course", course);
        sharedPreferencesNoteEditor.putString("shortCourse", shortCourse);
        sharedPreferencesNoteEditor.putString("semester", semester);
        sharedPreferencesNoteEditor.apply();
    }

    public String[] getSetupData(Context context, String fileName) {
        String[] resultData = new String[3];
        SharedPreferences sharedPreferencesNote = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        resultData[0] = sharedPreferencesNote.getString("course", null);
        resultData[1] = sharedPreferencesNote.getString("shortCourse", null);
        resultData[2] = sharedPreferencesNote.getString("semester", null);
        return resultData;
    }

    public void deleteSetupData(Context context, String fileName) {
        SharedPreferences sharedPreferencesNote = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesNoteEditor = sharedPreferencesNote.edit();
        sharedPreferencesNoteEditor.clear();
        sharedPreferencesNoteEditor.apply();
    }
}
