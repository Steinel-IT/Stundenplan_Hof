package com.steinel_it.stundenplanhof;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    String lectureShortName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            lectureShortName = savedInstanceState.getString("lectureShortName");
        } else {
            Bundle extras = getIntent().getExtras();
            lectureShortName = extras.getString(MainActivity.EXTRA_MESSAGE_NAME);
        }

        getSupportActionBar().setTitle(getString(R.string.chat) +": "+ lectureShortName);
        setContentView(R.layout.activity_chat);

        setContent();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("lectureShortName", lectureShortName);
    }

    private void setContent() {

    }

}