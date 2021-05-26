package com.steinel_it.stundenplanhof;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.steinel_it.stundenplanhof.adapter.SlidePageAdapter;
import com.steinel_it.stundenplanhof.data_manager.StorageManager;
import com.steinel_it.stundenplanhof.interfaces.HandleArrayListStringTaskInterface;
import com.steinel_it.stundenplanhof.data_manager.SetupParseDownloadManager;
import com.steinel_it.stundenplanhof.fragments.SetupFragmentOne;
import com.steinel_it.stundenplanhof.fragments.SetupFragmentThree;
import com.steinel_it.stundenplanhof.fragments.SetupFragmentTwo;
import com.steinel_it.stundenplanhof.interfaces.SetupValueInterface;
import com.steinel_it.stundenplanhof.parts.SetupViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SetupActivity extends AppCompatActivity implements HandleArrayListStringTaskInterface, SetupValueInterface {

    public static final String EXTRA_MESSAGE_COURSE = "com.steinel_it.stundenplanhof.course";
    public static final String EXTRA_MESSAGE_SHORT_COURSE = "com.steinel_it.stundenplanhof.name";
    public static final String EXTRA_MESSAGE_SEMESTER = "com.steinel_it.stundenplanhof.semester";
    public static final String EXTRA_MESSAGE_YEAR = "com.steinel_it.stundenplanhof.year";

    private SetupParseDownloadManager setupParseDownloadManager;
    private SetupViewPager pager;
    private List<Fragment> fragmentPageList;

    private String selectedCourse;
    private String selectedShortCourse;
    private String selectedSemester;
    private String selectedYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO: Hier auch SaveInstance?
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setupPagerSlider();
        setupParseDownloadManager = new SetupParseDownloadManager(this);
        setupParseDownloadManager.getCourses();
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() > 0) {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        } else {
            this.finishAffinity();
        }
    }

    private void setupPagerSlider() {
        fragmentPageList = new ArrayList<>();
        fragmentPageList.add(SetupFragmentOne.newInstance());
        fragmentPageList.add(SetupFragmentTwo.newInstance());
        fragmentPageList.add(SetupFragmentThree.newInstance());
        pager = findViewById(R.id.viewPagerSetup);

        setupPageListener();
        SlidePageAdapter pageAdapter = new SlidePageAdapter(getSupportFragmentManager(), fragmentPageList);
        pager.setAdapter(pageAdapter);
    }

    public void onClickSetupFab(View view) {
        if (pager.getCurrentItem() == 1 && (selectedCourse == null || selectedSemester == null || selectedYear == null)) {
            pager.setSwipeEnabled(false);
            Toast.makeText(this, R.string.selectAllInSetup, Toast.LENGTH_SHORT).show();
        } else if (pager.getCurrentItem() == 2) {
            new StorageManager().saveSetupData(getApplicationContext(), MainActivity.KEY_APP_SETTINGS, selectedCourse, selectedShortCourse, selectedSemester, selectedYear);
            Intent intentToMain = new Intent();
            intentToMain.putExtra(EXTRA_MESSAGE_COURSE, selectedCourse);
            intentToMain.putExtra(EXTRA_MESSAGE_SHORT_COURSE, selectedShortCourse);
            intentToMain.putExtra(EXTRA_MESSAGE_SEMESTER, selectedSemester);
            intentToMain.putExtra(EXTRA_MESSAGE_YEAR, selectedYear);
            setResult(RESULT_OK, intentToMain);
            finish();
        } else {
            pager.setCurrentItem(pager.getCurrentItem() + 1);
        }
    }

    private void setupPageListener() {
        ExtendedFloatingActionButton setupFab = findViewById(R.id.floatingActionButtonSetupNextPage);
        setupFab.shrink();
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1 && selectedCourse == null && selectedSemester == null) {
                    pager.setSwipeEnabled(false);
                    setupFab.shrink();
                } else if (position == 2) {
                    pager.setSwipeEnabled(true);
                    setupFab.extend();
                } else {
                    pager.setSwipeEnabled(true);
                    setupFab.shrink();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onTaskFinished(int requestCode, ArrayList<String> result) {
        switch (requestCode) {
            case SetupParseDownloadManager.REQUEST_CODE_COURSES:
                ((SetupFragmentTwo) fragmentPageList.get(1)).setCourses(result);
                break;
            case SetupParseDownloadManager.REQUEST_CODE_SEMESTER:
                ((SetupFragmentTwo) fragmentPageList.get(1)).setSemester(result);
                break;
            case SetupParseDownloadManager.REQUEST_CODE_YEARS:
                ((SetupFragmentTwo) fragmentPageList.get(1)).setYears(result);
                break;
        }
    }

    @Override
    public void onSelectValue(int selectCode, int index, String item) {
        switch (selectCode) {
            case SetupFragmentTwo.SELECT_CODE_COURSES:
                if (index == -1) {
                    selectedCourse = null;
                    break;
                }
                selectedCourse = item;
                selectedShortCourse = setupParseDownloadManager.getShortCourse(index);
                setupParseDownloadManager.getSemester(index);
                setupParseDownloadManager.getYears(index);
                break;
            case SetupFragmentTwo.SELECT_CODE_SEMESTER:
                if (index == -1) {
                    selectedSemester = null;
                    break;
                }
                selectedSemester = item;
                if (selectedYear != null)
                    pager.setSwipeEnabled(true);
                break;
            case SetupFragmentTwo.SELECT_CODE_YEAR:
                if (index == -1) {
                    selectedYear = null;
                    break;
                }
                selectedYear = item;
                if (selectedSemester != null)
                    pager.setSwipeEnabled(true);
                break;
        }
    }
}