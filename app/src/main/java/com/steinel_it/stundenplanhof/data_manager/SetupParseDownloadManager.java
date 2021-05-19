package com.steinel_it.stundenplanhof.data_manager;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.steinel_it.stundenplanhof.interfaces.HandleArrayListStringTaskInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SetupParseDownloadManager {

    private static final Handler uiThreadHandler = new Handler(Looper.getMainLooper());

    public static final int REQUEST_CODE_COURSES = 0;
    public static final int REQUEST_CODE_SEMESTER = 1;
    public static final int REQUEST_CODE_YEARS = 2;

    HandleArrayListStringTaskInterface context;

    ArrayList<String> shortCourses = new ArrayList<>();
    String coursesResponse;
    String semesterResponse;

    public SetupParseDownloadManager(HandleArrayListStringTaskInterface context) {
        this.context = context;
    }

    public String getShortCourse(int index) {
        return shortCourses.get(index);
    }

    public void getCourses() {
        final ArrayList<String> courses = new ArrayList<>();
        if (coursesResponse == null) {
            downloadSetupForm(REQUEST_CODE_COURSES, "https://www.hof-university.de/studierende/info-service/stundenplaene/");
            return;
        }
        try {
            Document doc = Jsoup.parse(coursesResponse);
            Element select = doc.select("select[name=tx_stundenplan_stundenplan[studiengang]]").first();

            for (Element element : select.children()) {
                if (!element.text().contains("wählen"))
                    shortCourses.add(element.val());
                courses.add(element.text());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        uiThreadHandler.post(() -> context.onTaskFinished(REQUEST_CODE_COURSES, new ArrayList<>(courses)));
    }

    public void getSemester(int... selectedCourseIndex) {
        final ArrayList<String> semester = new ArrayList<>();
        if (selectedCourseIndex.length != 0) {
            System.out.println(shortCourses.get(selectedCourseIndex[0]));
            downloadSetupForm(REQUEST_CODE_SEMESTER, "https://www.hof-university.de/index.php?type=1421771406&id=79&tx_stundenplan_stundenplan[controller]=Ajax&tx_stundenplan_stundenplan[action]=loadSemester&tx_stundenplan_stundenplan[studiengang]=" + shortCourses.get(selectedCourseIndex[0]));
            return;
        }
        try {
            String semesterExpr = new JSONObject(semesterResponse).getString("semester");
            Document doc = Jsoup.parse(semesterExpr);
            Element select = doc.select("select[name=tx_stundenplan_stundenplan[semester]]").first();

            for (Element element : select.children()) {
                semester.add(element.text());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        uiThreadHandler.post(() -> context.onTaskFinished(REQUEST_CODE_SEMESTER, new ArrayList<>(semester)));
    }

    public void getYears(int... selectedCourseIndex) {
        final ArrayList<String> years = new ArrayList<>();
        if (selectedCourseIndex.length != 0) {
            System.out.println(shortCourses.get(selectedCourseIndex[0]));
            downloadSetupForm(REQUEST_CODE_YEARS, "https://www.hof-university.de/index.php?type=1421771407&id=167&tx_modulhandbuch_modulhandbuch[controller]=Ajax&tx_modulhandbuch_modulhandbuch[action]=loadYear&tx_modulhandbuch_modulhandbuch[cl]=" + shortCourses.get(selectedCourseIndex[0]));
            return;
        }
        try {
            String semesterExpr = new JSONObject(semesterResponse).getString("year");
            Document doc = Jsoup.parse(semesterExpr);
            Element select = doc.select("select[name=tx_modulhandbuch_modulhandbuch[ye]]").first();
            for (Element element : select.children()) {
                years.add(element.text());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        uiThreadHandler.post(() -> context.onTaskFinished(REQUEST_CODE_YEARS, new ArrayList<>(years)));
    }

    private void downloadSetupForm(int requestCode, String url) {
        OkHttpClient okClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        okClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Module Loading", "Failed by loading setup data");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    switch (requestCode) {
                        case REQUEST_CODE_COURSES:
                            coursesResponse = response.body().string();
                            getCourses();
                            break;
                        case REQUEST_CODE_SEMESTER:
                            semesterResponse = response.body().string();
                            getSemester();
                            break;
                        case REQUEST_CODE_YEARS:
                            semesterResponse = response.body().string();
                            getYears();
                            break;
                    }
                } else {
                    throw new IOException("Download not successful");
                }
            }
        });
    }
}
