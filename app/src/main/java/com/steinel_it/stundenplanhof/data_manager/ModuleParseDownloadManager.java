package com.steinel_it.stundenplanhof.data_manager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.steinel_it.stundenplanhof.interfaces.HandleTitleContentTaskInterface;

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

public class ModuleParseDownloadManager {

    private HandleTitleContentTaskInterface context;

    private ArrayList<String> titelList;
    private ArrayList<String> contentList;

    private static final Handler uiThreadHandler = new Handler(Looper.getMainLooper());

    public ModuleParseDownloadManager(HandleTitleContentTaskInterface context) {
        this.context = context;
        titelList = new ArrayList<>();
        contentList = new ArrayList<>();
    }

    public void getModule(String shortCourse, String year, String shortLecture) {
        if (!titelList.isEmpty() && !contentList.isEmpty()) {
            uiThreadHandler.post(() -> context.onTaskFinished(titelList, contentList));
        } else {
            String replacedYear = year.replace(" ", "%20");
            OkHttpClient okClient = new OkHttpClient();
            String url = "https://www.hof-university.de/index.php?type=1421771407&id=167&tx_modulhandbuch_modulhandbuch[controller]=Ajax&tx_modulhandbuch_modulhandbuch[action]=loadModulhandbuecher&tx_modulhandbuch_modulhandbuch[cl]=" + shortCourse + "&tx_modulhandbuch_modulhandbuch[se]=&tx_modulhandbuch_modulhandbuch[ye]=" + replacedYear;
            Request requestAllModuleBooks = new Request.Builder().url(url).build();
            okClient.newCall(requestAllModuleBooks).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("Module Loading", "Failed by loading module books");
                    uiThreadHandler.post(() -> context.onTaskFinished(new ArrayList<>(), new ArrayList<>()));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            assert response.body() != null;
                            String AllLinksExpr = new JSONObject(response.body().string()).getString("content");
                            Document docCompelte = Jsoup.parse(AllLinksExpr);
                            Elements links = docCompelte.select("tr");
                            for (Element linkElement : links) {
                                if (linkElement.text().contains(shortLecture)) {
                                    //Build new URL
                                    String moduleURL = "https://www.hof-university.de/" + linkElement.select("a").attr("href");
                                    Request requestModuleBook = new Request.Builder().url(moduleURL).build();
                                    //Download specific ModuleBook
                                    Response responseModuleBook = okClient.newCall(requestModuleBook).execute();
                                    assert responseModuleBook.body() != null;
                                    Document moduleDoc = Jsoup.parse(responseModuleBook.body().string());
                                    Elements rows = moduleDoc.select("tr");
                                    titelList.clear();
                                    contentList.clear();
                                    for (Element row : rows) {
                                        Elements rowData = row.select("td");
                                        titelList.add(rowData.get(0).text());
                                        contentList.add(rowData.get(1).text());
                                    }
                                    uiThreadHandler.post(() -> context.onTaskFinished(titelList, contentList));
                                    return;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        uiThreadHandler.post(() -> context.onTaskFinished(new ArrayList<>(), new ArrayList<>()));
                    } else {
                        throw new IOException("Download not successful");
                    }
                }
            });
        }
    }
}
