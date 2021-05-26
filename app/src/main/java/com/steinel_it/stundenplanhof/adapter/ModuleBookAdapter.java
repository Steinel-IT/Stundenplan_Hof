package com.steinel_it.stundenplanhof.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.steinel_it.stundenplanhof.R;

import java.util.ArrayList;

public class ModuleBookAdapter extends RecyclerView.Adapter<ModuleBookAdapter.ModuleBookEntryHolder> {

    ArrayList<String> titelList, contentList;

    public ModuleBookAdapter(ArrayList<String> titelList, ArrayList<String> contentList) {
        this.titelList = titelList;
        this.contentList = contentList;
    }

    @NonNull
    @Override
    public ModuleBookAdapter.ModuleBookEntryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_item_module_book_entry, viewGroup, false);
        return new ModuleBookAdapter.ModuleBookEntryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleBookAdapter.ModuleBookEntryHolder moduleBookEntryHolder, int position) {
        moduleBookEntryHolder.bind(titelList.get(position), contentList.get(position));
    }

    @Override
    public int getItemCount() {
        if (titelList == null) return 0;
        return titelList.size();
    }

    public static class ModuleBookEntryHolder extends RecyclerView.ViewHolder {
        private final TextView titelView, contentView;

        public ModuleBookEntryHolder(@NonNull View itemView) {
            super(itemView);
            titelView = itemView.findViewById(R.id.textViewModuleBookEntryTitel);
            contentView = itemView.findViewById(R.id.textViewModuleBookEntryContent);
        }

        public void bind(final String titel, String content) {
            titelView.setText(titel);
            contentView.setText(content);
        }
    }

}
