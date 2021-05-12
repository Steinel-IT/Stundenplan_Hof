package com.steinel_it.stundenplanhof.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.steinel_it.stundenplanhof.R;
import com.steinel_it.stundenplanhof.objects.CourseEntry;
import com.steinel_it.stundenplanhof.objects.SchedulerEntry;

import java.util.ArrayList;

public class SchedulerEntryListAdapter extends RecyclerView.Adapter<SchedulerEntryListAdapter.SchedulerHolder> {

    private final ArrayList<String> titelList;
    private final ArrayList<SchedulerEntry> schedulerEntryArrayList;
    private final CourseEntryListAdapter.VorlesungHolder.OnItemClickListener vorlesungOnItemClickListener;

    public SchedulerEntryListAdapter(ArrayList<String> titelList, ArrayList<SchedulerEntry> schedulerEntryArrayList, CourseEntryListAdapter.VorlesungHolder.OnItemClickListener vorlesungOnItemClickListener) {
        this.titelList = titelList;
        this.schedulerEntryArrayList = schedulerEntryArrayList;
        this.vorlesungOnItemClickListener = vorlesungOnItemClickListener;
    }

    @NonNull
    @Override
    public SchedulerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_item_scheduler_day, viewGroup, false);
        return new SchedulerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SchedulerHolder vorlesungHolder, int position) {
        vorlesungHolder.bind(titelList.get(position), schedulerEntryArrayList.get(position).getCourseEntryArrayList(), position, vorlesungOnItemClickListener);
    }

    @Override
    public int getItemCount() {
        return schedulerEntryArrayList.size();
    }

    public static class SchedulerHolder extends RecyclerView.ViewHolder {
        TextView name;
        RecyclerView vorlesungRecyclerView;

        public SchedulerHolder(View view) {
            super(view);

            name = view.findViewById(R.id.textViewSchedulerName);
            vorlesungRecyclerView = view.findViewById(R.id.recyclerViewVorlesungen);
            vorlesungRecyclerView.addItemDecoration(new DividerItemDecoration(vorlesungRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        }

        public void bind(final String entryName, final ArrayList<CourseEntry> courseEntryArrayList, final int schedulerPos, CourseEntryListAdapter.VorlesungHolder.OnItemClickListener vorlesungOnItemClickListener) {
            name.setText(entryName);
            CourseEntryListAdapter courseEntryListAdapter = new CourseEntryListAdapter(courseEntryArrayList, vorlesungOnItemClickListener);
            courseEntryListAdapter.setSchedulerPos(schedulerPos);
            vorlesungRecyclerView.setAdapter(courseEntryListAdapter);
        }
    }
}