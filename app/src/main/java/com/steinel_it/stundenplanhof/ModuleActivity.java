package com.steinel_it.stundenplanhof;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import com.steinel_it.stundenplanhof.adapter.ModuleBookAdapter;
import com.steinel_it.stundenplanhof.data_manager.ModuleParseDownloadManager;
import com.steinel_it.stundenplanhof.interfaces.HandleTitleContentTaskInterface;

import java.util.ArrayList;
import java.util.Objects;

public class ModuleActivity extends AppCompatActivity implements HandleTitleContentTaskInterface {

    private ArrayList<String> titelList;
    private ArrayList<String> contentList;

    private RecyclerView recyclerViewModule;
    private String shortCourse, shortLecture, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            titelList = savedInstanceState.getStringArrayList("titelList");
            contentList = savedInstanceState.getStringArrayList("contentList");
            shortLecture = savedInstanceState.getString("shortLecture");
        } else {
            Bundle extras = getIntent().getExtras();
            shortCourse = extras.getString(MainActivity.EXTRA_MESSAGE_NAME);
            shortLecture = extras.getString(MainActivity.EXTRA_MESSAGE_LECTURE);
            year = extras.getString(MainActivity.EXTRA_MESSAGE_YEAR);
        }

        getSupportActionBar().setTitle(getString(R.string.modulebook) + ": " + shortLecture);
        setupContent();
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
        savedInstanceState.putStringArrayList("titelList", titelList);
        savedInstanceState.putStringArrayList("contentList", contentList);
        savedInstanceState.putString("shortLecture", shortLecture);
    }

    private void setupContent() {
        if (titelList == null || contentList == null) {
            ModuleParseDownloadManager moduleParseDownloadManager = new ModuleParseDownloadManager(this);
            moduleParseDownloadManager.getModule(shortCourse, year, shortLecture);
        } else if (titelList.isEmpty() || contentList.isEmpty()) {
            Group groupLoadingScreen = findViewById(R.id.groupLoadingModuleMain);
            Group groupNoDataScreen = findViewById(R.id.groupModuleNothingToSee);
            groupNoDataScreen.setVisibility(View.VISIBLE);
            groupLoadingScreen.setVisibility(View.GONE);
        } else {
            Group groupLoadingScreen = findViewById(R.id.groupLoadingModuleMain);
            Group groupModuleContent = findViewById(R.id.groupModuleContent);

            Spinner spinnerTitle = findViewById(R.id.spinnerModuleContentSelector);

            ArrayAdapter<String> spinnerModuleTitleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, titelList);
            spinnerModuleTitleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTitle.setAdapter(spinnerModuleTitleAdapter);

            spinnerTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                    recyclerViewModule.scrollToPosition(pos);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            recyclerViewModule = findViewById(R.id.recyclerViewModule);
            ModuleBookAdapter moduleBookAdapter = new ModuleBookAdapter(titelList, contentList);
            recyclerViewModule.setAdapter(moduleBookAdapter);

            groupLoadingScreen.setVisibility(View.GONE);
            groupModuleContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTaskFinished(ArrayList<String> titel, ArrayList<String> contentList) {
        this.titelList = titel;
        this.contentList = contentList;

        setupContent();
    }
}