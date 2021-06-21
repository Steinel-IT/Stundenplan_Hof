package com.steinel_it.stundenplanhof;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.steinel_it.stundenplanhof.adapter.MessageAdapter;
import com.steinel_it.stundenplanhof.data_manager.FirebaseManager;
import com.steinel_it.stundenplanhof.interfaces.ChatInterface;
import com.steinel_it.stundenplanhof.objects.Message;

import java.util.ArrayList;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity implements ChatInterface {

    private ArrayList<Message> messages = new ArrayList<>();

    private String lectureShortName;
    private String chatName;

    private EditText chatEditText;
    private FloatingActionButton sendButton;

    private MessageAdapter messageAdapter;

    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            chatName = savedInstanceState.getString("chatName");
            lectureShortName = savedInstanceState.getString("lectureShortName");
        } else {
            Bundle extras = getIntent().getExtras();
            lectureShortName = extras.getString(MainActivity.EXTRA_MESSAGE_NAME);
            chatName = lectureShortName.replace(" ", "_");
        }

        firebaseManager = new FirebaseManager(this, chatName);

        getSupportActionBar().setTitle(getString(R.string.chat) + ": " + lectureShortName);

        setContentView(R.layout.activity_chat);

        buildUI();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (firebaseManager.isLoggedIn())
            firebaseManager.anonymRegister();
        else {
            Toast.makeText(ChatActivity.this, getString(R.string.successfullLogin), Toast.LENGTH_SHORT).show();
            disableWriting(false);
        }
        messageListener();
    }

    private void buildUI() {
        chatEditText = findViewById(R.id.editTextChatField);
        sendButton = findViewById(R.id.floatingActionButtonChatSend);
        RecyclerView recyclerViewMessages = findViewById(R.id.recyclerViewChat);

        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages);
        recyclerViewMessages.setAdapter(messageAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("chatName", chatName);
        savedInstanceState.putString("lectureShortName", lectureShortName);
    }

    private void messageListener() {
        firebaseManager.setListener();
    }

    private void disableWriting(boolean disable) {
        if (disable) {
            chatEditText.setText(getString(R.string.noLogin));
            chatEditText.setEnabled(false);
            sendButton.setEnabled(false);
        } else {
            chatEditText.setHint(getString(R.string.message));
            chatEditText.setEnabled(true);
            sendButton.setEnabled(true);
        }
    }

    public void sendMessage(View view) {
        if (chatEditText.getText().length() == 0) return;
        String sendText = chatEditText.getText().toString();
        firebaseManager.sendMessage(sendText);
        chatEditText.setText("");
    }


    @Override
    public void succLogin() {
        Toast.makeText(ChatActivity.this, getString(R.string.successfullLogin), Toast.LENGTH_SHORT).show();
        disableWriting(false);
    }

    @Override
    public void errorLogin() {
        Toast.makeText(ChatActivity.this, getString(R.string.errorLogin), Toast.LENGTH_LONG).show();
        disableWriting(true);
    }

    @Override
    public void onUpdate(ArrayList<Message> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        messageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelUpdate(String errorMessage) {
        Toast.makeText(ChatActivity.this, errorMessage, Toast.LENGTH_LONG).show();
    }
}