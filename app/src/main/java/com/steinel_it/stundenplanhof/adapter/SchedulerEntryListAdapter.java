package com.steinel_it.stundenplanhof.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.steinel_it.stundenplanhof.R;
import com.steinel_it.stundenplanhof.objects.LectureEntry;
import com.steinel_it.stundenplanhof.objects.ScheduleEntry;

import java.util.ArrayList;

public class SchedulerEntryListAdapter extends RecyclerView.Adapter<SchedulerEntryListAdapter.SchedulerHolder> {

    private final ArrayList<String> titelList;
    private final ArrayList<ScheduleEntry> scheduleEntryArrayList;
    private final LectureEntryListAdapter.LectureHolder.OnItemClickListener lectureOnItemClickListener;

    public SchedulerEntryListAdapter(ArrayList<String> titelList, ArrayList<ScheduleEntry> scheduleEntryArrayList, LectureEntryListAdapter.LectureHolder.OnItemClickListener lectureOnItemClickListener) {
        this.titelList = titelList;
        this.scheduleEntryArrayList = scheduleEntryArrayList;
        this.lectureOnItemClickListener = lectureOnItemClickListener;
    }

    @NonNull
    @Override
    public SchedulerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_item_scheduler_day, viewGroup, false);
        return new SchedulerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SchedulerHolder courseHolder, int position) {
        courseHolder.bind(titelList.get(position), scheduleEntryArrayList.get(position).getCourseEntryArrayList(), position, lectureOnItemClickListener);
    }

    @Override
    public int getItemCount() {
        return scheduleEntryArrayList.size();
    }

    public static class SchedulerHolder extends RecyclerView.ViewHolder {
        TextView name;
        RecyclerView lectureRecyclerView;

        public SchedulerHolder(@NonNull View view) {
            super(view);

            name = view.findViewById(R.id.textViewSchedulerName);
            lectureRecyclerView = view.findViewById(R.id.recyclerViewCourse);
            lectureRecyclerView.addItemDecoration(new DividerItemDecoration(lectureRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        }

        public void bind(final String entryName, final ArrayList<LectureEntry> lectureEntries, final int schedulePos, LectureEntryListAdapter.LectureHolder.OnItemClickListener lectureOnItemClickListener) {
            name.setText(entryName);
            LectureEntryListAdapter lectureEntryListAdapter = new LectureEntryListAdapter(lectureEntries, lectureOnItemClickListener, schedulePos);
            lectureRecyclerView.setAdapter(lectureEntryListAdapter);
        }
    }
}