package com.steinel_it.stundenplanhof.data_manager;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.steinel_it.stundenplanhof.interfaces.ChatInterface;
import com.steinel_it.stundenplanhof.objects.Message;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class FirebaseManager {

    private final ChatInterface context;

    private final FirebaseAuth mAuth;

    private final DatabaseReference currChatDB;

    private FirebaseUser currentUser;

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void anonymRegister() {
        mAuth.signInAnonymously().addOnCompleteListener(task -> {
            if (task.getResult() == null) {
                context.errorLogin();
            } else {
                currentUser = task.getResult().getUser();
                context.succLogin();
            }
        });
    }

    public FirebaseManager(ChatInterface context, String chatName) {
        this.context = context;

        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://stundenplan-hof-429d1-default-rtdb.europe-west1.firebasedatabase.app");
        currChatDB = database.getReference().child("chat").child(chatName);
        currentUser = mAuth.getCurrentUser();
    }

    public void setListener() {
        currChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Message> newMessages = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String message = dataSnapshot.child("text").getValue(String.class);
                    String time = dataSnapshot.child("time").getValue(String.class);
                    newMessages.add(new Message(message, time));
                }
                context.onUpdate(newMessages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                context.onCancelUpdate(error.getMessage());
            }
        });
    }

    public void sendMessage(String sendText) {
        String key = currChatDB.push().getKey();

        if (key != null) {
            currChatDB.child(key).child("text").setValue(sendText);
            currChatDB.child(key).child("time").setValue(Message.getTimeAsString(LocalDateTime.now()));
        }
    }

}
