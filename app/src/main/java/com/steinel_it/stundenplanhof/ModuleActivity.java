package com.steinel_it.stundenplanhof;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.steinel_it.stundenplanhof.data_manager.ModuleParseDownloadManager;
import com.steinel_it.stundenplanhof.interfaces.HandleTitleContentTaskInterface;

import java.util.ArrayList;

public class ModuleActivity extends AppCompatActivity implements HandleTitleContentTaskInterface {

    ModuleParseDownloadManager moduleParseDownloadManager;

    ArrayList<String> titelList;
    ArrayList<String> contentList;

    private String shortCourse, shortLecture, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);
        getSupportActionBar().setTitle("Modulhandbuch");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        shortCourse = extras.getString(MainActivity.EXTRA_MESSAGE_NAME);
        shortLecture = extras.getString(MainActivity.EXTRA_MESSAGE_LECTURE);
        year = extras.getString(MainActivity.EXTRA_MESSAGE_YEAR);

        //TODO Wie in Dozent hier saveInstance laden

        setupContent();
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
        savedInstanceState.putStringArrayList("titelList", titelList);
        savedInstanceState.putStringArrayList("contentList", contentList);
    }
    //TODO SaveInstance noch ausfüllen

    private void setupContent() {
        if (titelList == null || contentList == null) {
            moduleParseDownloadManager = new ModuleParseDownloadManager(this);
            moduleParseDownloadManager.getModule(shortCourse, year, shortLecture);
        } else {
            //TODO: Layout befüllen
        }
    }

    @Override
    public void onTaskFinished(ArrayList<String> titel, ArrayList<String> contentList) {
        this.titelList = titel;
        this.contentList = contentList;
        System.out.println(titel);
        System.out.println(contentList);
        setupContent();
    }
}