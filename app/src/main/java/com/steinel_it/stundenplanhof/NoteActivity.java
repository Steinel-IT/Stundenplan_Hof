package com.steinel_it.stundenplanhof;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.steinel_it.stundenplanhof.adapter.NoteListAdapter;
import com.steinel_it.stundenplanhof.data_manager.StorageManager;
import com.steinel_it.stundenplanhof.objects.Note;

import java.util.ArrayList;
import java.util.Objects;

public class NoteActivity extends AppCompatActivity {

    private String fileName;
    private StorageManager storageManager;
    private LinearLayout linearLayoutNothingToSee;
    private ArrayList<Note> noteArrayList = new ArrayList<>();
    private NoteListAdapter noteListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        storageManager = new StorageManager();
        linearLayoutNothingToSee = findViewById(R.id.linearLayoutNothingToSee);
        Bundle extras = getIntent().getExtras();
        fileName = String.format("Notes_S%1$s_%2$s",
                extras.getString(MainActivity.EXTRA_MESSAGE_SEMESTER),
                extras.getString(MainActivity.EXTRA_MESSAGE_NAME));
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.notes));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getAllNotes();
        setupNotes();
        checkNothingLabel();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupNotes() {
        RecyclerView recyclerViewNotes = findViewById(R.id.recyclerViewNotes);
        noteListAdapter = new NoteListAdapter(noteArrayList, (Note note, int position, View view) ->
                showDialog(view, note), (Note note, int position, View view) -> deleteSavedNote(note));
        recyclerViewNotes.setAdapter(noteListAdapter);
    }

    private void getAllNotes() {
        noteArrayList = storageManager.getAllNotes(getApplicationContext(), fileName);
    }

    private void deleteSavedNote(Note note) {
        noteArrayList.remove(note);
        noteListAdapter.notifyDataSetChanged();
        storageManager.deleteNote(note, getApplicationContext(), fileName);
        checkNothingLabel();
    }

    public void addNote(View view) {
        showDialog(view, null);
    }

    private void checkNothingLabel() {
        if (noteArrayList.size() == 0)
            linearLayoutNothingToSee.setVisibility(View.VISIBLE);
        else
            linearLayoutNothingToSee.setVisibility(View.INVISIBLE);
    }

    private void showDialog(View view, Note presetNote) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_note, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView textViewTitle = dialogView.findViewById(R.id.textViewDialogNoteTitle);
        EditText editTextNote = dialogView.findViewById(R.id.editTextDialogNote);
        if (presetNote != null) {
            textViewTitle.setText(R.string.editNote);
            editTextNote.setText(presetNote.getText());
        } else {
            textViewTitle.setText(R.string.newNote);
        }
        Button buttonCancel = dialogView.findViewById(R.id.buttonDialogNoteCancel);
        Button buttonSave = dialogView.findViewById(R.id.buttonDialogNoteSave);
        buttonCancel.setOnClickListener(view1 -> alertDialog.cancel());
        buttonSave.setOnClickListener(view12 -> {
            if (!editTextNote.getText().toString().equals("")) {
                if (presetNote == null) {
                    Note newNote = new Note(editTextNote.getText().toString());
                    storageManager.saveNote(newNote, getApplicationContext(), fileName);
                    noteArrayList.add(newNote);
                } else {
                    Note editNote = new Note(editTextNote.getText().toString(), presetNote.getSaveDate());
                    storageManager.editNote(editNote, getApplicationContext(), fileName);
                    noteArrayList.get(noteArrayList.indexOf(presetNote)).setText(editNote.getText());
                }
                noteListAdapter.notifyDataSetChanged();
                checkNothingLabel();
            }
            alertDialog.cancel();
        });
        alertDialog.show();
    }
}