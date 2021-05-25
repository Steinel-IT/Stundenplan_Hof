package com.steinel_it.stundenplanhof;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
    public static final String EXTRA_MESSAGE_ROOM = "com.steinel_it.stundenplanhof.room";
    public static final String EXTRA_MESSAGE_BUILDING = "com.steinel_it.stundenplanhof.building";

    private String course;
    private String shortCourse;
    private String semester;
    private String year;

    ScheduleParseDownloadManager setupParseDownloadManager;

    LectureEntry selectedLectureEntry;

    SchedulerEntryListAdapter schedulerEntryListAdapter;

    SingletonSchedule schedule;

    StorageManager storageManager;

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
            if (checkedId == R.id.chipFilterTage) {
                schedule.setFilterType(SchedulerFilter.DAYS);
            } else if (checkedId == R.id.chipFilterVorlesung) {
                schedule.setFilterType(SchedulerFilter.VORLESUNGEN);
            } else if (checkedId == R.id.chipFilterRaeume) {
                schedule.setFilterType(SchedulerFilter.ROOMS);
            } else if (checkedId == R.id.chipFilterDozenten) {
                schedule.setFilterType(SchedulerFilter.DOZENTEN);
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
            int orientation = this.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                createBottomOptionSheet(courseEntry);
            else
                createSideOptionSheet(courseEntry);
            selectedLectureEntry = courseEntry;
        });
        recyclerViewScheduler.setAdapter(schedulerEntryListAdapter);
    }

    private void createSideOptionSheet(LectureEntry lectureEntry) {
        LinearLayout linearLayoutMainLandscape = findViewById(R.id.linearLayoutMainLandscape);
        if (linearLayoutMainLandscape.getChildCount() > 1)
            linearLayoutMainLandscape.removeViewAt(2);

        LayoutInflater inflater = LayoutInflater.from(this);

        View vericalDivider = new View(this);
        vericalDivider.setBackgroundColor(R.attr.dividerVertical);

        View viewSideBar = inflater.inflate(R.layout.modal_sheet_vorlesung, linearLayoutMainLandscape, false);

        TextView textViewVorlesungName = viewSideBar.findViewById(R.id.textViewModalSheetVorlesungName);
        textViewVorlesungName.setText(lectureEntry.getShortName());

        TextView textViewDozentName = viewSideBar.findViewById(R.id.textViewModalSheetDozentName);
        textViewDozentName.setText(lectureEntry.getDozent());

        TextView textViewRoomName = viewSideBar.findViewById(R.id.textViewModalSheetRoomName);
        textViewRoomName.setText(lectureEntry.getRoom());

        TextView textViewRoomDetail = viewSideBar.findViewById(R.id.textViewModalSheetRoomDetail);
        textViewRoomDetail.setText(lectureEntry.getBuilding());
        if (linearLayoutMainLandscape.getChildCount() == 1)
            linearLayoutMainLandscape.addView(vericalDivider, new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayoutMainLandscape.addView(viewSideBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
    }

    private void createBottomOptionSheet(LectureEntry lectureEntry) {
        BottomSheetDialog bottomSheetDialogVorlesung = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialogVorlesung.getBehavior().setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels - 100);
        View buttomSheet = getLayoutInflater().inflate(R.layout.modal_sheet_vorlesung, null);  //TODO: warum hier null?

        TextView textViewVorlesungName = buttomSheet.findViewById(R.id.textViewModalSheetVorlesungName);
        textViewVorlesungName.setText(lectureEntry.getShortName());

        TextView textViewDozentName = buttomSheet.findViewById(R.id.textViewModalSheetDozentName);
        textViewDozentName.setText(lectureEntry.getDozent());

        TextView textViewRoomName = buttomSheet.findViewById(R.id.textViewModalSheetRoomName);
        textViewRoomName.setText(lectureEntry.getRoom());

        TextView textViewRoomDetail = buttomSheet.findViewById(R.id.textViewModalSheetRoomDetail);
        textViewRoomDetail.setText(String.format("%s %s", getString(R.string.building), lectureEntry.getBuilding()));

        bottomSheetDialogVorlesung.setContentView(buttomSheet);
        bottomSheetDialogVorlesung.show();
    }

    public void onClickDozent(View view) {
        Intent intentDozent = new Intent(this, DozentActivity.class);
        intentDozent.putExtra(EXTRA_MESSAGE_DOZENT, selectedLectureEntry.getDozent());
        startActivity(intentDozent);
    }

    public void onClickRoom(View view) {
        Intent intentRoom = new Intent(this, RoomActivity.class);
        intentRoom.putExtra(EXTRA_MESSAGE_ROOM, selectedLectureEntry.getRoom());
        intentRoom.putExtra(EXTRA_MESSAGE_BUILDING, selectedLectureEntry.getBuilding());
        startActivity(intentRoom);
    }

    public void onClickNote(View view) {
        Intent intentNote = new Intent(this, NoteActivity.class);
        intentNote.putExtra(EXTRA_MESSAGE_NAME, selectedLectureEntry.getShortName());
        intentNote.putExtra(EXTRA_MESSAGE_SEMESTER, semester);
        startActivity(intentNote);
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