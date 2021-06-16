package com.steinel_it.stundenplanhof;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.steinel_it.stundenplanhof.adapter.MessageAdapter;
import com.steinel_it.stundenplanhof.objects.Message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private ArrayList<Message> messages = new ArrayList<>();

    private String lectureShortName;
    private String chatName;

    private FirebaseAuth mAuth;

    private DatabaseReference currChatDB;

    private FirebaseUser currentUser;

    private EditText chatEditText;
    private FloatingActionButton sendButton;

    private MessageAdapter messageAdapter;

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

        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://stundenplan-hof-429d1-default-rtdb.europe-west1.firebasedatabase.app");

        //enables the offline function of Firebase
        //It caches all downloaded data up to 10MB
        database.setPersistenceEnabled(true);

        currChatDB = database.getReference().child("chat").child(chatName);

        getSupportActionBar().setTitle(getString(R.string.chat) + ": " + lectureShortName);

        setContentView(R.layout.activity_chat);

        buildUI();
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null)
            anonymRegister();
        else {
            Toast.makeText(ChatActivity.this, getString(R.string.successfullLogin), Toast.LENGTH_SHORT).show();
            disableWriting(false);
        }
        setMessageListener();
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

    private void anonymRegister() {
        FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(task -> {
            if (task.getResult() == null) {
                Toast.makeText(ChatActivity.this, getString(R.string.errorLogin), Toast.LENGTH_LONG).show();
                disableWriting(true);
            } else {
                currentUser = task.getResult().getUser();
                Toast.makeText(ChatActivity.this, getString(R.string.successfullLogin), Toast.LENGTH_SHORT).show();
                disableWriting(false);
            }
        });
    }

    private void setMessageListener() {
        currChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Reset Array List
                messages.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String message = dataSnapshot.child("text").getValue(String.class);
                    String time = dataSnapshot.child("time").getValue(String.class);
                    messages.add(new Message(message, time));
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
        String key = currChatDB.push().getKey();

        if (key != null) {
            currChatDB.child(key).child("text").setValue(sendText);
            currChatDB.child(key).child("time").setValue(Message.getTimeAsString(LocalDateTime.now()));
        }
        chatEditText.setText("");
    }
}