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

    public ScheduleParseDownloadManager(HandleArrayListScheduleTaskInterface context) {
        this.context = context;
    }

    private static final Handler uiThreadHandler = new Handler(Looper.getMainLooper());

    public void resetSchedule() {
        schedulerEntries = null;
    }

    public void getSchedule(String shortCourse, String semester) {
        if (schedulerEntries == null || titelList == null) {
            String replacedSemester = semester.replace(" - ", "_").replace(" ", "_");
            OkHttpClient okClient = new OkHttpClient();
            String url = "https://www.hof-university.de/index.php?type=1421771406&id=79&tx_stundenplan_stundenplan[controller]=Ajax&tx_stundenplan_stundenplan[action]=loadVorlesungen&tx_stundenplan_stundenplan[studiengang]=" + shortCourse + "&tx_stundenplan_stundenplan[semester]=" + replacedSemester + "&tx_stundenplan_stundenplan[view]=alle";
            Request request = new Request.Builder().url(url).build();

            okClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("Module Loading", "Failed by loading schedule");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        ArrayList<SchedulerEntry> schedulerEntriesLocal = new ArrayList<>();
                        ArrayList<String> titelListLocal = new ArrayList<>();
                        try {
                            assert response.body() != null;
                            String semesterExpr = new JSONObject(response.body().string()).getString("vorlesungen");
                            Document docCompelte = Jsoup.parse(semesterExpr);
                            Elements dayContent = docCompelte.select("div[class=hide-for-small]").select("table");
                            for (int i = 1; i < dayContent.size(); i++) {
                                titelListLocal.add(dayContent.get(i).select("thead").first().text());
                                Element dayData = dayContent.get(i).select("tbody").first();
                                ArrayList<LectureEntry> vorlesungsArrayList = new ArrayList<>();
                                for (Element course : dayData.select("tr")) {
                                    String room = course.select("td").get(6).text();
                                    String building = room.contains("F") ? room.substring(1, 2) : "Virtuell";
                                    String shortName = getShortName(course.select("td").get(3).text());
                                    //TODO: Short name richtig rausfiltern
                                    vorlesungsArrayList.add(new LectureEntry(course.select("td").get(0).text(), course.select("td").get(1).text(), course.select("td").get(2).text(), course.select("td").get(3).text(), shortName, course.select("td").get(4).text(), room, building));
                                }
                                schedulerEntriesLocal.add(new SchedulerEntry(vorlesungsArrayList));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        schedulerEntries = schedulerEntriesLocal;
                        titelList = titelListLocal;
                        uiThreadHandler.post(() -> context.onTaskFinished(schedulerEntriesLocal, titelListLocal));
                    } else {
                        throw new IOException("Download not successful");
                    }
                }
            });
        } else {
            uiThreadHandler.post(() -> context.onTaskFinished(schedulerEntries, titelList));
        }
    }


    private String getShortName(String completeName) {
        String standartSplit = completeName.split(" - |\\(")[0];
        //remove Words
        return standartSplit.replace("Präsenz", "").trim();
    }

}
