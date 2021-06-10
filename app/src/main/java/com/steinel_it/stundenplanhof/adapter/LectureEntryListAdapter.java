package com.steinel_it.stundenplanhof.adapter;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.steinel_it.stundenplanhof.R;
import com.steinel_it.stundenplanhof.objects.LectureEntry;

import java.util.ArrayList;

public class LectureEntryListAdapter extends RecyclerView.Adapter<LectureEntryListAdapter.LectureHolder> {

    private final ArrayList<LectureEntry> lectureEntryArrayList;
    private final int schedulerPos;
    private final LectureHolder.OnItemClickListener lectureOnItemClickListener;


    public LectureEntryListAdapter(ArrayList<LectureEntry> lectureEntryArrayList, LectureHolder.OnItemClickListener lectureOnItemClickListener, int schedulerPos) {
        this.lectureEntryArrayList = lectureEntryArrayList;
        this.lectureOnItemClickListener = lectureOnItemClickListener;
        this.schedulerPos = schedulerPos;
    }

    @NonNull
    @Override
    public LectureHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_item_scheduler_lecture, viewGroup, false);
        return new LectureHolder(view, schedulerPos);
    }

    @Override
    public void onBindViewHolder(@NonNull LectureHolder lectureHolder, int position) {
        lectureHolder.bind(lectureEntryArrayList.get(position), position, lectureOnItemClickListener);
    }

    @Override
    public int getItemCount() {
        return lectureEntryArrayList.size();
    }

    public static class LectureHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout mConstraintLayout;
        private final TextView day, time, room, name, dozent, type;
        private final int schedulerPos;
        private final View view;

        public LectureHolder(View view, int schedulerPos) {
            super(view);
            this.view = view;
            this.schedulerPos = schedulerPos;
            mConstraintLayout = view.findViewById(R.id.constraintLayoutLecture);
            day = view.findViewById(R.id.textViewLectureDay);
            name = view.findViewById(R.id.textViewLectureName);
            time = view.findViewById(R.id.textViewLectureTime);
            room = view.findViewById(R.id.textViewLectureRoom);
            dozent = view.findViewById(R.id.textViewLectureDozent);
            type = view.findViewById(R.id.textViewLectureType);
        }

        public void bind(final LectureEntry lectureEntry, int pos, final OnItemClickListener clickListener) {
            ConstraintLayout.LayoutParams timeLayoutParams = (ConstraintLayout.LayoutParams) time.getLayoutParams();
            if (!lectureEntry.getDay().isEmpty()) {
                timeLayoutParams.setMargins(8, 0, 0, 0);
                day.setText(lectureEntry.getDay());
                day.setVisibility(View.VISIBLE);
            }
            name.setText(lectureEntry.getName());
            if (lectureEntry.getCanceled()) {
                name.setTextColor(Color.RED);
                name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            time.setText(String.format("%1$s - %2$s", lectureEntry.getTimeStart(), lectureEntry.getTimeEnd()));
            room.setText(lectureEntry.getRoom());
            dozent.setText(lectureEntry.getLecturer());
            if (lectureEntry.getType().isEmpty()) {
                type.setVisibility(View.GONE);

                ConstraintSet set = new ConstraintSet();
                set.clone(mConstraintLayout);
                set.connect(room.getId(), ConstraintSet.BOTTOM, mConstraintLayout.getId(), ConstraintSet.BOTTOM, pixToDp(8));
                set.applyTo(mConstraintLayout);
            } else {
                type.setText(lectureEntry.getType());
            }
            itemView.setOnClickListener(view -> clickListener.onItemClick(lectureEntry, schedulerPos, pos, view));
        }

        public interface OnItemClickListener {
            void onItemClick(LectureEntry lectureEntry, int posScheduler, int posLecture, View view);
        }

        private int pixToDp(int dp) {
            Resources r = view.getResources();
            return (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dp,
                    r.getDisplayMetrics()
            );
        }
    }
}
