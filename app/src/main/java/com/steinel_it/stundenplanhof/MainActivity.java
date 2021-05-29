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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements HandleArrayListScheduleTaskInterface {

    public static final String KEY_APP_SETTINGS = "appSettings";
    public static final int RESULTCODE_SETUP = 1;
    public static final String EXTRA_MESSAGE_LECTURER = "com.steinel_it.stundenplanhof.lecturer";
    public static final String EXTRA_MESSAGE_NAME = "com.steinel_it.stundenplanhof.name";
    public static final String EXTRA_MESSAGE_SEMESTER = "com.steinel_it.stundenplanhof.semester";
    public static final String EXTRA_MESSAGE_LECTURE = "com.steinel_it.stundenplanhof.lecture";
    public static final String EXTRA_MESSAGE_YEAR = "com.steinel_it.stundenplanhof.year";
    public static final String EXTRA_MESSAGE_ROOM = "com.steinel_it.stundenplanhof.room";
    public static final String EXTRA_MESSAGE_BUILDING = "com.steinel_it.stundenplanhof.building";

    private String course; // Für den Chat
    private String shortCourse;
    private String semester;
    private String year;

    private ScheduleParseDownloadManager setupParseDownloadManager;

    private LectureEntry selectedLectureEntry;

    private RecyclerView recyclerViewScheduler;

    private SchedulerEntryListAdapter schedulerEntryListAdapter;

    private SingletonSchedule schedule;

    private StorageManager storageManager;

    private BottomSheetDialog bottomSheetDialogLecture;

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
        if (bottomSheetDialogLecture != null)
            bottomSheetDialogLecture.dismiss();
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
            Toast.makeText(this, getString(R.string.errorInSetup), Toast.LENGTH_SHORT).show();
    }

    private void setupContent(boolean reload) {
        setupParseDownloadManager = new ScheduleParseDownloadManager(this, getString(R.string.virtuel), getString(R.string.changes));
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
            hideContent();
            setupParseDownloadManager.resetSchedule();
            setupParseDownloadManager.getSchedule(shortCourse, semester);
        } else if (itemId == R.id.action_reset) {
            storageManager.deleteSetupData(this, KEY_APP_SETTINGS);
            schedule.setFilterType(SchedulerFilter.DAYS);
            Intent intentFirstTime = new Intent(this, SetupActivity.class);
            startActivityForResult(intentFirstTime, RESULTCODE_SETUP);
        } else if (itemId == R.id.action_sync) {
        }//TODO set Kalender Sync
        return super.onOptionsItemSelected(item);
    }

    private void hideContent() {
        Group loadingGroup = findViewById(R.id.groupLoadingScheduleMain);
        RecyclerView recyclerViewScheduler = findViewById(R.id.recyclerViewScheduler);
        recyclerViewScheduler.setVisibility(View.GONE);
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayout linearLayoutLandscape = findViewById(R.id.linearLayoutMainLandscape);
            if (linearLayoutLandscape.getChildCount() > 2) {
                linearLayoutLandscape.removeViewAt(2);
            }
        }

        loadingGroup.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTaskFinished(ArrayList<SchedulerEntry> result, ArrayList<String> titel) {
        if (result == null && titel == null) {
            Toast.makeText(MainActivity.this, getString(R.string.errorInDownloadParse), Toast.LENGTH_LONG).show();
        } else {
            schedule.setDayTitle(titel);
            schedule.setDaySortedSchedule(result);
            schedule.sortSchedule();
            if (schedulerEntryListAdapter != null) {
                schedulerEntryListAdapter.notifyDataSetChanged();
            }
            setupRecyclerViews();
        }
    }

    private void setupFilterBar() {
        ChipGroup filterChipGroup = findViewById(R.id.chipGroupFilter);
        filterChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipFilterDay) {
                schedule.setFilterType(SchedulerFilter.DAYS);
            } else if (checkedId == R.id.chipFilterLecture) {
                schedule.setFilterType(SchedulerFilter.LECTURE);
            } else if (checkedId == R.id.chipFilterRoom) {
                schedule.setFilterType(SchedulerFilter.ROOMS);
            } else if (checkedId == R.id.chipFilterLecturer) {
                schedule.setFilterType(SchedulerFilter.LECTURER);
            }
            schedule.sortSchedule();
            if (schedulerEntryListAdapter != null) {
                schedulerEntryListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setupRecyclerViews() {
        Group loadingGroup = findViewById(R.id.groupLoadingScheduleMain);
        recyclerViewScheduler = findViewById(R.id.recyclerViewScheduler);
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
        scrollToToday();
    }

    private void scrollToToday() {
        LocalDate date = LocalDate.now();
        String dayOfWeekGerm = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN);
        if(schedule.getTitleList().contains(dayOfWeekGerm)) {
            recyclerViewScheduler.scrollToPosition(schedule.getTitleList().indexOf(dayOfWeekGerm));
        }
    }

    private void createSideOptionSheet(LectureEntry lectureEntry) {
        LinearLayout linearLayoutMainLandscape = findViewById(R.id.linearLayoutMainLandscape);
        if (linearLayoutMainLandscape.getChildCount() > 2)
            linearLayoutMainLandscape.removeViewAt(2);

        LayoutInflater inflater = LayoutInflater.from(this);

        View vericalDivider = new View(this);
        vericalDivider.setBackgroundColor(R.attr.dividerVertical);

        View viewSideBar = inflater.inflate(R.layout.modal_sheet_vorlesung, linearLayoutMainLandscape, false);

        TextView textViewVorlesungName = viewSideBar.findViewById(R.id.textViewModalSheetVorlesungName);
        textViewVorlesungName.setText(lectureEntry.getShortName());

        TextView textViewLecturerName = viewSideBar.findViewById(R.id.textViewModalSheetLecturerName);
        textViewLecturerName.setText(lectureEntry.getLecturer());

        TextView textViewRoomName = viewSideBar.findViewById(R.id.textViewModalSheetRoomName);
        textViewRoomName.setText(lectureEntry.getRoom());

        TextView textViewRoomDetail = viewSideBar.findViewById(R.id.textViewModalSheetRoomDetail);
        textViewRoomDetail.setText(lectureEntry.getBuilding());
        if (linearLayoutMainLandscape.getChildCount() == 1)
            linearLayoutMainLandscape.addView(vericalDivider, new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayoutMainLandscape.addView(viewSideBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
    }

    private void createBottomOptionSheet(LectureEntry lectureEntry) {
        bottomSheetDialogLecture = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialogLecture.getBehavior().setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels - 100);
        View buttomSheet = getLayoutInflater().inflate(R.layout.modal_sheet_vorlesung, null);

        TextView textViewVorlesungName = buttomSheet.findViewById(R.id.textViewModalSheetVorlesungName);
        textViewVorlesungName.setText(lectureEntry.getShortName());

        TextView textViewLecturerName = buttomSheet.findViewById(R.id.textViewModalSheetLecturerName);
        textViewLecturerName.setText(lectureEntry.getLecturer());

        TextView textViewRoomName = buttomSheet.findViewById(R.id.textViewModalSheetRoomName);
        textViewRoomName.setText(lectureEntry.getRoom());

        TextView textViewRoomDetail = buttomSheet.findViewById(R.id.textViewModalSheetRoomDetail);
        textViewRoomDetail.setText(String.format("%s %s", getString(R.string.building), lectureEntry.getBuilding()));

        bottomSheetDialogLecture.setContentView(buttomSheet);
        bottomSheetDialogLecture.show();
    }

    public void onClickLecturer(View view) {
        Intent intentLecturer = new Intent(this, LecturerActivity.class);
        intentLecturer.putExtra(EXTRA_MESSAGE_LECTURER, selectedLectureEntry.getLecturer());
        startActivity(intentLecturer);
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
        //TODO: CHAT
    }

}