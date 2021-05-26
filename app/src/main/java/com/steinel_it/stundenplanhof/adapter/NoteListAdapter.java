package com.steinel_it.stundenplanhof.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.steinel_it.stundenplanhof.R;
import com.steinel_it.stundenplanhof.objects.Note;

import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteHolder> {

    ArrayList<Note> noteArrayList;
    NoteHolder.OnItemClickListener onItemClickListenerEdit;
    NoteHolder.OnItemClickListener onItemClickListenerDelete;

    public NoteListAdapter(ArrayList<Note> noteArrayList, NoteHolder.OnItemClickListener onItemClickListenerEdit, NoteHolder.OnItemClickListener onItemClickListenerDelete) {
        this.noteArrayList = noteArrayList;
        this.onItemClickListenerEdit = onItemClickListenerEdit;
        this.onItemClickListenerDelete = onItemClickListenerDelete;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_item_note, viewGroup, false);
        return new NoteListAdapter.NoteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder noteHolder, int position) {
        noteHolder.bind(noteArrayList.get(position), position, onItemClickListenerEdit, onItemClickListenerDelete);
    }

    @Override
    public int getItemCount() {
        if (noteArrayList == null) return 0;
        return noteArrayList.size();
    }

    public static class NoteHolder extends RecyclerView.ViewHolder {
        private final TextView date, text;
        private final ImageButton imageButtonEdit, imageButtonDelete;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.textViewNoteDate);
            text = itemView.findViewById(R.id.textViewNoteText);
            imageButtonEdit = itemView.findViewById(R.id.imageButtonNoteEdit);
            imageButtonDelete = itemView.findViewById(R.id.imageButtonNoteDelete);
        }

        public void bind(final Note note, int pos, final OnItemClickListener clickListenerEdit, final OnItemClickListener clickListenerDelete) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            date.setText(note.getSaveDate().format(formatter));
            text.setText(note.getText());
            imageButtonEdit.setOnClickListener(view -> clickListenerEdit.onItemClick(note, pos, view));
            imageButtonDelete.setOnClickListener(view -> clickListenerDelete.onItemClick(note, pos, view));
        }

        public interface OnItemClickListener {
            void onItemClick(Note note, int position, View view);
        }
    }
}
