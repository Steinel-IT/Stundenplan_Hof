package com.steinel_it.stundenplanhof.data_manager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.steinel_it.stundenplanhof.interfaces.HandleArrayListScheduleTaskInterface;
import com.steinel_it.stundenplanhof.objects.LectureEntry;
import com.steinel_it.stundenplanhof.objects.SchedulerEntry;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ScheduleParseDownloadManager {

    HandleArrayListScheduleTaskInterface context;

    ArrayList<SchedulerEntry> schedulerEntries;
    ArrayList<String> titelList;

    boolean isAlreadyRunning = false;

    public ScheduleParseDownloadManager(HandleArrayListScheduleTaskInterface context) {
        this.context = context;
    }

    private static final Handler uiThreadHandler = new Handler(Looper.getMainLooper());

    public void resetSchedule() {
        schedulerEntries = null;
        titelList = null;
    }

    public void getSchedule(String shortCourse, String semester) {
        if(!isAlreadyRunning) {
            isAlreadyRunning = true;
            if (titelList == null || schedulerEntries == null) {
                titelList = new ArrayList<>();
                schedulerEntries = new ArrayList<>();

                //Load Schedule
                String replacedSemester = semester.replace(" - ", "_").replace(" ", "_");
                OkHttpClient okClient = new OkHttpClient();
                String url = "https://www.hof-university.de/index.php?type=1421771406&id=79&tx_stundenplan_stundenplan[controller]=Ajax&tx_stundenplan_stundenplan[action]=loadVorlesungen&tx_stundenplan_stundenplan[studiengang]=" + shortCourse + "&tx_stundenplan_stundenplan[semester]=" + replacedSemester + "&tx_stundenplan_stundenplan[view]=alle";
                Request request = new Request.Builder().url(url).build();

                okClient.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        isAlreadyRunning = false;
                        uiThreadHandler.post(() -> context.onTaskFinished(null, null));
                        Log.e("Schedule Loading", "Failed by loading schedule");
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            //Parse Normal Schedule
                            parseSchedule(response);

                            //Load Cancelled Lecuteres
                            String newReplacedSemester = replacedSemester.replace("_", "%23");
                            String cancelledURL = "https://www.hof-university.de/index.php?type=1421771406&id=166&tx_stundenplan_stundenplan[controller]=Ajax&tx_stundenplan_stundenplan[action]=loadAenderungen&tx_stundenplan_stundenplan[studiengang]=" + shortCourse + "&tx_stundenplan_stundenplan[semester]=" + newReplacedSemester + "&tx_stundenplan_stundenplan[datum]=TT.MM.JJJJ";
                            Request requestCancelledLecuteres = new Request.Builder().url(cancelledURL).build();
                            Response responseCancelledLecuteres = okClient.newCall(requestCancelledLecuteres).execute();
                            parseCancelledLectures(responseCancelledLecuteres);
                            isAlreadyRunning = false;
                            uiThreadHandler.post(() -> context.onTaskFinished(schedulerEntries, titelList));
                        } else {
                            isAlreadyRunning = false;
                            uiThreadHandler.post(() -> context.onTaskFinished(null, null));
                        }
                    }
                });
            } else {
                isAlreadyRunning = false;
                uiThreadHandler.post(() -> context.onTaskFinished(schedulerEntries, titelList));
            }
        }
    }

    private void parseSchedule(Response response) throws IOException {
        try {
            assert response.body() != null;
            String semesterExpr = new JSONObject(response.body().string()).getString("vorlesungen");
            Document docCompelte = Jsoup.parse(semesterExpr);
            Elements dayContent = docCompelte.select("div[class=hide-for-small]").select("table");
            for (int i = 1; i < dayContent.size(); i++) {
                titelList.add(dayContent.get(i).select("thead").first().text());
                Element dayData = dayContent.get(i).select("tbody").first();
                ArrayList<LectureEntry> lectureArrayList = new ArrayList<>();
                for (Element course : dayData.select("tr")) {
                    String room = course.select("td").get(6).text();
                    String building = room.contains("F") ? room.substring(1, 2) : "Virtuell";
                    String shortName = course.select("td").get(3).textNodes().get(0).text();
                    lectureArrayList.add(new LectureEntry(course.select("td").get(0).text(), course.select("td").get(1).text(), course.select("td").get(2).text(), course.select("td").get(3).text(), shortName, course.select("td").get(4).text(), room, building, false));
                }
                schedulerEntries.add(new SchedulerEntry(lectureArrayList));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseCancelledLectures(Response response) throws IOException {
        try {
            String lecturesExpr = new JSONObject(response.body().string()).getString("vorlesungen");
            Document docCompelte = Jsoup.parse(lecturesExpr);
            Elements lectures = docCompelte.select("div[class=hide-for-small]").select("tr");
            if (!lectures.isEmpty()) {
                titelList.add("Änderungen");
                ArrayList<LectureEntry> lectureEntries = new ArrayList<>();
                for (int i = 1; i < lectures.size(); i++) {
                    Elements infos = lectures.get(i).select("td");
                    String shortName = infos.get(1).textNodes().get(0).text().substring(1).trim();
                    String day = infos.get(3).textNodes().get(0).text();
                    String timeStart = infos.get(3).textNodes().get(1).text().substring(1, 6);
                    //Clac endDate
                    LocalDateTime endDateTime = LocalDateTime.of(1970, 1, 1, Integer.parseInt(timeStart.substring(0, 2)), Integer.parseInt(timeStart.substring(3, 5))).plusMinutes(90);
                    String endDate = endDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                    String room = infos.get(3).textNodes().get(2).text().substring(1);
                    String building = room.contains("F") ? room.substring(1, 2) : "Virtuell";
                    lectureEntries.add(new LectureEntry(day, timeStart, endDate, infos.get(1).text(), shortName, infos.get(2).text(), room, building, true));

                    String newDay = infos.get(4).textNodes().get(0).text();
                    String newTimeStart = infos.get(4).textNodes().get(1).text().substring(1, 6);
                    //Clac new endDate
                    LocalDateTime newEndDateTime = LocalDateTime.of(1970, 1, 1, Integer.parseInt(newTimeStart.substring(0, 2)), Integer.parseInt(newTimeStart.substring(3, 5))).plusMinutes(90);
                    String newEndDate = newEndDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                    String newRoom = infos.get(4).textNodes().get(2).text().substring(1);
                    String newBuilding = newRoom.contains("F") ? newRoom.substring(1, 2) : "Virtuell";
                    lectureEntries.add(new LectureEntry(newDay, newTimeStart, newEndDate, infos.get(1).text(), shortName, infos.get(2).text(), newRoom, newBuilding, false));
                }
                schedulerEntries.add(new SchedulerEntry(lectureEntries));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
