package com.steinel_it.stundenplanhof;

import android.content.Intent;
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
import com.steinel_it.stundenplanhof.adapter.SchedulerEntryListAdapter;
import com.steinel_it.stundenplanhof.data_manager.ScheduleParseDownloadManager;
import com.steinel_it.stundenplanhof.data_manager.StorageManager;
import com.steinel_it.stundenplanhof.interfaces.HandleArrayListScheduleTaskInterface;
import com.steinel_it.stundenplanhof.objects.LectureEntry;
import com.steinel_it.stundenplanhof.objects.SchedulerEntry;
import com.steinel_it.stundenplanhof.objects.SchedulerFilter;
import com.steinel_it.stundenplanhof.singleton.SingletonSchedule;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements HandleArrayListScheduleTaskInterface {

    public static final String KEY_APP_SETTINGS = "appSettings";
    public static final int RESULTCODE_SETUP = 1;
    public static final String EXTRA_MESSAGE_DOZENT = "com.steinel_it.stundenplanhof.dozent";
    public static final String EXTRA_MESSAGE_NAME = "com.steinel_it.stundenplanhof.name";
    public static final String EXTRA_MESSAGE_SEMESTER = "com.steinel_it.stundenplanhof.semester";
    public static final String EXTRA_MESSAGE_LECTURE = "com.steinel_it.stundenplanhof.lecture";
    public static final String EXTRA_MESSAGE_YEAR = "com.steinel_it.stundenplanhof.year";

    private String course;
    private String shortCourse;
    private String semester;
    private String year;

    ScheduleParseDownloadManager setupParseDownloadManager;

    LectureEntry selectedLectureEntry;

    SchedulerEntryListAdapter schedulerEntryListAdapter;

    SingletonSchedule schedule;

    StorageManager storageManager;

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageManager = new StorageManager();
        if (isFirstTime()) {
            Intent intentFirstTime = new Intent(this, SetupActivity.class);
            startActivityForResult(intentFirstTime, RESULTCODE_SETUP);
        } else {
            schedule = SingletonSchedule.getInstance();
            setupContent(false);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
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
                year = extras.getString(SetupActivity.EXTRA_MESSAGE_YEAR);
                schedule = SingletonSchedule.getInstance();
                setupContent(true);
            }
        } else
            Toast.makeText(this, "Fehler im Setup aufgetreten", Toast.LENGTH_SHORT).show();
    }

    //TODO Stundenplanänderungen anzeigen

    private void setupContent(boolean reload) {
        setupParseDownloadManager = new ScheduleParseDownloadManager(this);
        setContentView(R.layout.activity_main);
        if (reload || schedule.getScheduleList() == null || schedule.getDayTitle() == null) {
            setupParseDownloadManager.getSchedule(shortCourse, semester);
        } else {
            schedule.sortSchedule();
            setupRecyclerViews();
        }
        setupFilterBar();
    }

    private boolean isFirstTime() {
        String[] setupData = storageManager.getSetupData(this, KEY_APP_SETTINGS);
        course = setupData[0];
        shortCourse = setupData[1];
        semester = setupData[2];
        year = setupData[3];
        return setupData[0] == null && setupData[1] == null && setupData[2] == null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_update) {
            Group loadingGroup = findViewById(R.id.groupLoadingScheduleMain);
            RecyclerView recyclerViewScheduler = findViewById(R.id.recyclerViewScheduler);
            recyclerViewScheduler.setVisibility(View.GONE);
            loadingGroup.setVisibility(View.VISIBLE);
            setupParseDownloadManager.resetSchedule();
            setupParseDownloadManager.getSchedule(shortCourse, semester);
        } else if (itemId == R.id.action_reset) {
            storageManager.deleteSetupData(this, KEY_APP_SETTINGS);
            schedule.setFilterType(SchedulerFilter.DAYS);
            Intent intentFirstTime = new Intent(this, SetupActivity.class);
            startActivityForResult(intentFirstTime, RESULTCODE_SETUP);
        } else if (itemId == R.id.action_sync) {
        }//TODO set Menue Sync
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskFinished(ArrayList<SchedulerEntry> result, ArrayList<String> titel) {
        schedule.setDayTitle(titel);
        schedule.setDaySortedSchedule(result);
        schedule.sortSchedule();
        if (schedulerEntryListAdapter != null) {
            schedulerEntryListAdapter.notifyDataSetChanged();
        }
        setupRecyclerViews();
    }

    private void setupFilterBar() {
        ChipGroup filterChipGroup = findViewById(R.id.chipGroupFilter);
        filterChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.chipFilterTage:
                    schedule.setFilterType(SchedulerFilter.DAYS);
                    break;
                case R.id.chipFilterVorlesung:
                    schedule.setFilterType(SchedulerFilter.VORLESUNGEN);
                    break;
                case R.id.chipFilterRaeume:
                    schedule.setFilterType(SchedulerFilter.ROOMS);
                    break;
                case R.id.chipFilterDozenten:
                    schedule.setFilterType(SchedulerFilter.DOZENTEN);
                    break;
            }
            schedule.sortSchedule();
            if (schedulerEntryListAdapter != null) {
                schedulerEntryListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setupRecyclerViews() {
        Group loadingGroup = findViewById(R.id.groupLoadingScheduleMain);
        RecyclerView recyclerViewScheduler = findViewById(R.id.recyclerViewScheduler);
        loadingGroup.setVisibility(View.GONE);
        recyclerViewScheduler.setVisibility(View.VISIBLE);
        schedulerEntryListAdapter = new SchedulerEntryListAdapter(schedule.getTitleList(), schedule.getScheduleList(), (courseEntry, schedulerPos, vorlesungPos, view) -> {
            createBottomSheet(courseEntry);//
            selectedLectureEntry = courseEntry;
        });
        recyclerViewScheduler.setAdapter(schedulerEntryListAdapter);
    }

    private void createBottomSheet(LectureEntry lectureEntry) {
        BottomSheetDialog bottomSheetDialogVorlesung = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialogVorlesung.getBehavior().setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels - 100);
        View buttomSheet = getLayoutInflater().inflate(R.layout.modal_sheet_vorlesung, null);

        TextView textViewVorlesungName = buttomSheet.findViewById(R.id.textViewBottomSheetVorlesungName);
        textViewVorlesungName.setText(lectureEntry.getShortName());

        TextView textViewDozentName = buttomSheet.findViewById(R.id.textViewBottomSheetDozentName);
        textViewDozentName.setText(lectureEntry.getDozent());

        TextView textViewRoomName = buttomSheet.findViewById(R.id.textViewBottomSheetRoomName);
        textViewRoomName.setText(lectureEntry.getRoom());

        TextView textViewRoomDetail = buttomSheet.findViewById(R.id.textViewBottomSheetRoomDetail);
        textViewRoomDetail.setText(lectureEntry.getBuilding());

        bottomSheetDialogVorlesung.setContentView(buttomSheet);
        bottomSheetDialogVorlesung.show();
    }

    public void onClickDozent(View view) {
        Intent intentDozent = new Intent(this, DozentActivity.class);
        intentDozent.putExtra(EXTRA_MESSAGE_DOZENT, selectedLectureEntry.getDozent());
        startActivity(intentDozent);
    }

    public void onClickRoom(View view) {
        System.out.println("On Click Room");
    }

    public void onClickNote(View view) {
        Intent intentDozent = new Intent(this, NoteActivity.class);
        intentDozent.putExtra(EXTRA_MESSAGE_NAME, selectedLectureEntry.getShortName());
        intentDozent.putExtra(EXTRA_MESSAGE_SEMESTER, semester);
        startActivity(intentDozent);
    }

    public void onClickModul(View view) {
        Intent intentModule = new Intent(this, ModuleActivity.class);
        intentModule.putExtra(EXTRA_MESSAGE_NAME, shortCourse);
        intentModule.putExtra(EXTRA_MESSAGE_LECTURE, selectedLectureEntry.getShortName());
        intentModule.putExtra(EXTRA_MESSAGE_YEAR, year);
        startActivity(intentModule);
    }

    public void onClickChat(View view) {
        System.out.println("On Click Chat");
    }

}