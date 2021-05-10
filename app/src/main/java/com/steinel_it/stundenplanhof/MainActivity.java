package com.steinel_it.stundenplanhof;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.ChipGroup;
import com.steinel_it.stundenplanhof.adapter.CourseEntryListAdapter;
import com.steinel_it.stundenplanhof.adapter.SchedulerEntryListAdapter;
import com.steinel_it.stundenplanhof.data_manager.ScheduleParseDownloadManager;
import com.steinel_it.stundenplanhof.interfaces.HandleArrayListScheduleTaskInterface;
import com.steinel_it.stundenplanhof.objects.CourseEntry;
import com.steinel_it.stundenplanhof.objects.SchedulerEntry;
import com.steinel_it.stundenplanhof.objects.SchedulerFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements HandleArrayListScheduleTaskInterface {

    public static final String KEY_APP_SETTINGS = "appSettings";
    public static final int RESULTCODE_SETUP = 1;
    public static final String EXTRA_MESSAGE_DOZENT = "com.steinel_it.stundenplanhof.dozent";
    public static final String EXTRA_MESSAGE_NAME = "com.steinel_it.stundenplanhof.name";
    public static final String EXTRA_MESSAGE_SEMESTER = "com.steinel_it.stundenplanhof.semester";

    private String course;
    private String shortCourse;
    private String semester;

    ScheduleParseDownloadManager setupParseDownloadManager;

    SchedulerFilter filterType = SchedulerFilter.DAYS;

    CourseEntry selectedCourseEntry;

    SchedulerEntryListAdapter schedulerEntryListAdapter;

    ArrayList<SchedulerEntry> scheduleList;
    ArrayList<SchedulerEntry> daySortedSchedule; //Original Sorted Schedule
    ArrayList<String> titleList;

    String[] dayTitle = {"Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"};

    CourseEntryListAdapter.VorlesungHolder.OnItemClickListener vorlesungOnItemClickListener = (courseEntry, schedulerPos, vorlesungPos, view) -> {
        createBottomSheet(courseEntry);
        selectedCourseEntry = courseEntry;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO: Überall dran denken an SavedInstanceState!!
        super.onCreate(savedInstanceState);
        if (isFirstTime()) {
            Intent intentFirstTime = new Intent(this, SetupActivity.class);
            startActivityForResult(intentFirstTime, RESULTCODE_SETUP);
        } else {
            setupContent();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULTCODE_SETUP && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                course = extras.getString(SetupActivity.EXTRA_MESSAGE_COURSE);
                shortCourse = extras.getString(SetupActivity.EXTRA_MESSAGE_SHORT_COURSE);
                semester = extras.getString(SetupActivity.EXTRA_MESSAGE_SEMESTER);
                setupContent();
            }
        }
        Toast.makeText(this, "Fehler im Setup aufgetreten", Toast.LENGTH_SHORT).show();
    }

    private void setupContent() {
        setupParseDownloadManager = new ScheduleParseDownloadManager(this);
        setContentView(R.layout.activity_main);
        setupFilterBar();
        setupParseDownloadManager.getSchedule(shortCourse, semester);
    }

    private boolean isFirstTime() {
        SharedPreferences pref = getSharedPreferences(KEY_APP_SETTINGS, MODE_PRIVATE);
        course = pref.getString("course", null);
        shortCourse = pref.getString("shortCourse", null);
        semester = pref.getString("semester", null);
        return course == null && shortCourse == null && semester == null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            //TODO: Check hin
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                Group loadingGroup = findViewById(R.id.groupLoadingScheduleMain);
                RecyclerView recyclerViewScheduler = findViewById(R.id.recyclerViewScheduler);
                recyclerViewScheduler.setVisibility(View.GONE);
                loadingGroup.setVisibility(View.VISIBLE);
                setupParseDownloadManager.resetSchedule();
                setupParseDownloadManager.getSchedule(shortCourse, semester);
                break;
        }//TODO set Menue
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskFinished(ArrayList<SchedulerEntry> result) {
        for (SchedulerEntry sEntry : result) {
            for (CourseEntry cEntry : sEntry.getCourseEntryArrayList()) {
                cEntry.setOnItemClickListener(vorlesungOnItemClickListener);
            }
        }
        daySortedSchedule = result;
        sortSchedule();
        setupRecyclerViews();
    }

    private void setupFilterBar() {
        ChipGroup filterChipGroup = findViewById(R.id.chipGroupFilter);
        filterChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.chipFilterTage:
                    filterType = SchedulerFilter.DAYS;
                    break;
                case R.id.chipFilterVorlesung:
                    filterType = SchedulerFilter.VORLESUNGEN;
                    break;
                case R.id.chipFilterRaeume:
                    filterType = SchedulerFilter.ROOMS;
                    break;
                case R.id.chipFilterDozenten:
                    filterType = SchedulerFilter.DOZENTEN;
                    break;
            }
            sortSchedule();
        });
    }

    private void sortSchedule() {
        if (daySortedSchedule != null) {
            if(titleList == null) {
                titleList = new ArrayList<>();
            } else {
                titleList.clear();
            }
            if(scheduleList == null) {
                scheduleList = new ArrayList<>();
            } else {
                scheduleList.clear();
            }
            HashMap<String, ArrayList<CourseEntry>> sortedMap = new HashMap<>();
            if (filterType == SchedulerFilter.VORLESUNGEN) {
                ArrayList<CourseEntry> allCourses = new ArrayList<>();
                getAllCourses(allCourses);
                for (CourseEntry entry : allCourses) {
                    if (!sortedMap.containsKey(entry.getShortName())) {
                        sortedMap.put(entry.getShortName(), new ArrayList<>());
                    }
                    sortedMap.get(entry.getShortName()).add(entry);
                }
                titleList.addAll(sortedMap.keySet());
                for (String key : sortedMap.keySet()) {
                    scheduleList.add(new SchedulerEntry(sortedMap.get(key)));
                }
            } else if (filterType == SchedulerFilter.ROOMS) {
                ArrayList<CourseEntry> allCourses = new ArrayList<>();
                getAllCourses(allCourses);
                for (CourseEntry entry : allCourses) {
                    if (!sortedMap.containsKey(entry.getRoom())) {
                        sortedMap.put(entry.getRoom(), new ArrayList<>());
                    }
                    sortedMap.get(entry.getRoom()).add(entry);
                }
                titleList.addAll(sortedMap.keySet());
                for (String key : sortedMap.keySet()) {
                    scheduleList.add(new SchedulerEntry(sortedMap.get(key)));
                }
            } else if (filterType == SchedulerFilter.DOZENTEN) {
                ArrayList<CourseEntry> allCourses = new ArrayList<>();
                getAllCourses(allCourses);
                for (CourseEntry entry : allCourses) {
                    if (!sortedMap.containsKey(entry.getDozent())) {
                        sortedMap.put(entry.getDozent(), new ArrayList<>());
                    }
                    sortedMap.get(entry.getDozent()).add(entry);
                }
                titleList.addAll(sortedMap.keySet());
                for (String key : sortedMap.keySet()) {
                    scheduleList.add(new SchedulerEntry(sortedMap.get(key)));
                }
            } else {
                titleList.addAll(Arrays.asList(dayTitle));
                scheduleList.addAll(daySortedSchedule);
            }
            if (schedulerEntryListAdapter != null) {
                schedulerEntryListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void getAllCourses(ArrayList<CourseEntry> allCourses) {
        for (SchedulerEntry scheduleEntry : daySortedSchedule) {
            allCourses.addAll(scheduleEntry.getCourseEntryArrayList());
        }
    }


    private void setupRecyclerViews() {
        Group loadingGroup = findViewById(R.id.groupLoadingScheduleMain);
        RecyclerView recyclerViewScheduler = findViewById(R.id.recyclerViewScheduler);
        loadingGroup.setVisibility(View.GONE);
        recyclerViewScheduler.setVisibility(View.VISIBLE);
        schedulerEntryListAdapter = new SchedulerEntryListAdapter(titleList, scheduleList);
        recyclerViewScheduler.setAdapter(schedulerEntryListAdapter);
    }

    private void createBottomSheet(CourseEntry courseEntry) {
        BottomSheetDialog bottomSheetDialogVorlesung = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialogVorlesung.getBehavior().setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels - 100);
        View buttomSheet = getLayoutInflater().inflate(R.layout.modal_sheet_vorlesung, null);

        TextView textViewVorlesungName = buttomSheet.findViewById(R.id.textViewBottomSheetVorlesungName);
        textViewVorlesungName.setText(courseEntry.getShortName());

        TextView textViewDozentName = buttomSheet.findViewById(R.id.textViewBottomSheetDozentName);
        textViewDozentName.setText(courseEntry.getDozent());

        TextView textViewRoomName = buttomSheet.findViewById(R.id.textViewBottomSheetRoomName);
        textViewRoomName.setText(courseEntry.getRoom());

        TextView textViewRoomDetail = buttomSheet.findViewById(R.id.textViewBottomSheetRoomDetail);
        textViewRoomDetail.setText(courseEntry.getGebaeude());

        bottomSheetDialogVorlesung.setContentView(buttomSheet);
        bottomSheetDialogVorlesung.show();
    }

    public void onClickDozent(View view) {
        Intent intentDozent = new Intent(this, DozentActivity.class);
        intentDozent.putExtra(EXTRA_MESSAGE_DOZENT, selectedCourseEntry.getDozent());
        startActivity(intentDozent);
    }

    public void onClickRoom(View view) {
        System.out.println("On Click Room");
    }

    public void onClickNote(View view) {
        Intent intentDozent = new Intent(this, NoteActivity.class);
        intentDozent.putExtra(EXTRA_MESSAGE_NAME, selectedCourseEntry.getName());
        intentDozent.putExtra(EXTRA_MESSAGE_SEMESTER, semester);
        startActivity(intentDozent);
    }

    public void onClickModul(View view) {
        Intent intentModule = new Intent(this, ModuleActivity.class);
        startActivity(intentModule);
    }

    public void onClickChat(View view) {
        System.out.println("On Click Chat");
    }

}