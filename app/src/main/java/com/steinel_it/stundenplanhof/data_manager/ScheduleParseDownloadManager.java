package com.steinel_it.stundenplanhof.data_manager;

import android.os.Handler;
import android.os.Looper;

import com.steinel_it.stundenplanhof.interfaces.HandleArrayListScheduleTaskInterface;
import com.steinel_it.stundenplanhof.objects.CourseEntry;
import com.steinel_it.stundenplanhof.objects.SchedulerEntry;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ScheduleParseDownloadManager {

    HandleArrayListScheduleTaskInterface context;

    ArrayList<SchedulerEntry> schedulerEntries;

    public ScheduleParseDownloadManager(HandleArrayListScheduleTaskInterface context) {
        this.context = context;
    }

    private static final Handler uiThreadHandler = new Handler(Looper.getMainLooper());

    public void resetSchedule() {
        schedulerEntries = null;
    }

    public void getSchedule(String shortCourse, String semester) {
        if (schedulerEntries == null) {
            String replacedSemester = semester.replace(" - ", "_").replace(" ", "_");
            OkHttpClient okClient = new OkHttpClient();
            String url = "https://www.hof-university.de/index.php?type=1421771406&id=79&tx_stundenplan_stundenplan[controller]=Ajax&tx_stundenplan_stundenplan[action]=loadVorlesungen&tx_stundenplan_stundenplan[studiengang]=" + shortCourse + "&tx_stundenplan_stundenplan[semester]=" + replacedSemester + "&tx_stundenplan_stundenplan[view]=alle";
            Request request = new Request.Builder().url(url).build();

            okClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("Fail");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        ArrayList<SchedulerEntry> schedulerEntriesLocal = new ArrayList<>();
                        try {
                            String semesterExpr = new JSONObject(response.body().string()).getString("vorlesungen");
                            Document docCompelte = Jsoup.parse(semesterExpr);
                            Elements daySelection = docCompelte.select("div[class=hide-for-small]").first().select("tbody");
                            for (Element dayData : daySelection) {
                                ArrayList<CourseEntry> vorlesungsArrayList = new ArrayList<>();
                                for (Element course : dayData.select("tr")) {
                                    String room = course.select("td").get(6).text();
                                    String building = room.contains("F") ? room.substring(1, 2) : "Virtuell";
                                    String shortName = course.select("td").get(3).text().split("[(-]")[0].trim();
                                    vorlesungsArrayList.add(new CourseEntry(course.select("td").get(1).text(), course.select("td").get(2).text(), course.select("td").get(3).text(), shortName, course.select("td").get(4).text(), room, "Gebäude " + building));
                                }
                                schedulerEntriesLocal.add(new SchedulerEntry(vorlesungsArrayList));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        schedulerEntries = schedulerEntriesLocal;
                        uiThreadHandler.post(() -> context.onTaskFinished(schedulerEntriesLocal));
                    } else {
                        throw new IOException("Download not successful");
                    }
                }
            });
        } else {
            uiThreadHandler.post(() -> context.onTaskFinished(schedulerEntries));
        }
    }

}
