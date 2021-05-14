package com.steinel_it.stundenplanhof.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.steinel_it.stundenplanhof.R;
import com.steinel_it.stundenplanhof.objects.CourseEntry;

import java.util.ArrayList;

public class CourseEntryListAdapter extends RecyclerView.Adapter<CourseEntryListAdapter.VorlesungHolder> {

    private final ArrayList<CourseEntry> courseEntryArrayList;
    private int schedulerPos;
    private final VorlesungHolder.OnItemClickListener vorlesungOnItemClickListener;


    public CourseEntryListAdapter(ArrayList<CourseEntry> courseEntryArrayList, VorlesungHolder.OnItemClickListener vorlesungOnItemClickListener) {
        this.courseEntryArrayList = courseEntryArrayList;
        this.vorlesungOnItemClickListener = vorlesungOnItemClickListener;
    }

    public void setSchedulerPos(int schedulerPos) {
        this.schedulerPos = schedulerPos;
    }

    @NonNull
    @Override
    public VorlesungHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_item_scheduler_vorlesung, viewGroup, false);
        return new VorlesungHolder(view, schedulerPos);
    }

    @Override
    public void onBindViewHolder(@NonNull VorlesungHolder vorlesungHolder, int position) {
        vorlesungHolder.bind(courseEntryArrayList.get(position), position, vorlesungOnItemClickListener);
    }

    @Override
    public int getItemCount() {
        return courseEntryArrayList.size();
    }

    public static class VorlesungHolder extends RecyclerView.ViewHolder {
        private final TextView day, time, room, name, dozent;
        private final int schedulerPos;

        public VorlesungHolder(View view, int schedulerPos) {
            super(view);
            this.schedulerPos = schedulerPos;
            day = view.findViewById(R.id.textViewVorlesungDay);
            name = view.findViewById(R.id.textViewVorlesungName);
            time = view.findViewById(R.id.textViewVorlesungTime);
            room = view.findViewById(R.id.textViewVorlesungRoom);
            dozent = view.findViewById(R.id.textViewVorlesungDozent);
        }

        public void bind(final CourseEntry courseEntry, int pos, final OnItemClickListener clickListener) {
            ConstraintLayout.LayoutParams timeLayoutParams = (ConstraintLayout.LayoutParams) time.getLayoutParams();
            if (!courseEntry.getDay().isEmpty()) {
                timeLayoutParams.setMargins(8, 0, 0, 0);
                day.setText(courseEntry.getDay());
                day.setVisibility(View.VISIBLE);
            }
            name.setText(courseEntry.getName());
            time.setText(String.format("%1$s - %2$s", courseEntry.getTimeStart(), courseEntry.getTimeEnd()));
            room.setText(courseEntry.getRoom());
            dozent.setText(courseEntry.getDozent());
            itemView.setOnClickListener(view -> clickListener.onItemClick(courseEntry, schedulerPos, pos, view));
        }

        public interface OnItemClickListener {
            void onItemClick(CourseEntry courseEntry, int posScheduler, int posVorlesung, View view);
        }
    }
}
