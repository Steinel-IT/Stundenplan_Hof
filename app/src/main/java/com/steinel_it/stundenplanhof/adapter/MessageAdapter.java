package com.steinel_it.stundenplanhof.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.steinel_it.stundenplanhof.R;
import com.steinel_it.stundenplanhof.objects.Message;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    final ArrayList<Message> messages;

    public MessageAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_item_chat_message, viewGroup, false);
        return new MessageAdapter.MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageHolder messageHolder, int position) {
        messageHolder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        if (messages == null) return 0;
        return messages.size();
    }

    public static class MessageHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTime, textViewText;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            textViewTime = itemView.findViewById(R.id.textViewChatMessageTime);
            textViewText = itemView.findViewById(R.id.textViewChatMessageText);
        }

        public void bind(Message message) {
            textViewTime.setText(message.getSendTime());
            textViewText.setText(message.getMessage());
        }
    }

}
