package com.steinel_it.stundenplanhof.data_manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.steinel_it.stundenplanhof.interfaces.HandleLecturerTaskInterface;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LecturerParseDownloadManager {

    private HandleLecturerTaskInterface context;

    private ArrayList<String> titelList = new ArrayList<>();
    private ArrayList<String> contentList = new ArrayList<>();
    private Bitmap image;

    private static final Handler uiThreadHandler = new Handler(Looper.getMainLooper());

    public LecturerParseDownloadManager(HandleLecturerTaskInterface context) {
        this.context = context;
    }

    public void getLecturer(String lecturer) {
        if (contentList.isEmpty() || titelList.isEmpty() || image == null) {
            String replacedLecturer = lecturer.replace(".", "").toLowerCase();
            replacedLecturer = replacedLecturer.replace("msc ", "").replace(" ", "-");
            replacedLecturer = replacedLecturer.replace("ö", "oe").replace("ü", "ue").replace("ä", "ae");
            OkHttpClient okClient = new OkHttpClient();
            String url = "https://www.hof-university.de/ueber-uns/personen/professoren/" + replacedLecturer + "/";
            Request request = new Request.Builder().url(url).build();

            okClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("Lecturer loading", "Failed by loading lecturer");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        assert response.body() != null;

                        //true if infos are available
                        if (!parseLecturer(response)) return;

                        uiThreadHandler.post(() -> context.onTaskFinished(titelList, contentList, image));
                    } else {
                        uiThreadHandler.post(() -> context.onTaskFinished(new ArrayList<>(), new ArrayList<>(), null));
                    }
                }
            });
        } else {
            uiThreadHandler.post(() -> context.onTaskFinished(contentList, titelList, image));
        }
    }

    private boolean parseLecturer(@NonNull Response response) throws IOException {
        Document docCompelte = Jsoup.parse(response.body().string());

        Elements lecturerContentRaw = docCompelte.select("div[class=row contact_persons]").select("div[class=bg]");

        //Falls keine Dozenten vorhanden
        if (lecturerContentRaw.isEmpty()) {
            uiThreadHandler.post(() -> context.onTaskFinished(new ArrayList<>(), new ArrayList<>(), null));
            return false;
        }
        //Parse Image src
        String imageURL = null;
        if (lecturerContentRaw.first().select("img").first() != null)
            imageURL = lecturerContentRaw.first().select("img").first().attr("src");

        for (int i = 0; i < lecturerContentRaw.size(); i++) {
            titelList.add(lecturerContentRaw.get(i).select("h4").text());
            lecturerContentRaw.get(i).select("h4").remove();
            if (i < 2) {
                StringBuilder stringBuilder = new StringBuilder();
                for (Element line : lecturerContentRaw.get(i).select("p")) {
                    for (Node currNode : line.childNodes()) {
                        if (currNode.toString().equals("<br>")) {
                            stringBuilder.append("\n");
                        } else {
                            if (currNode.toString().contains("<a href")) {
                                Document linkDoc = Jsoup.parse(currNode.toString());
                                stringBuilder.append(linkDoc.text().replace(" hof-university", "@hof-university").replace("LÖSCHEN.", ""));
                            } else {
                                stringBuilder.append(currNode.toString());
                            }
                        }
                    }
                    stringBuilder.append("\n");
                }
                contentList.add(stringBuilder.toString());
            } else {
                contentList.add(lecturerContentRaw.get(i).text());
            }
        }

        //Parse Description
        Element lecturerDescRaw = docCompelte.select("div[class=six mobile-one columns]").first();
        titelList.add(lecturerDescRaw.select("div[class=row sitesubtitle]").text());
        contentList.add(lecturerDescRaw.text());

        //Load Image
        if (imageURL != null) {
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                image = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error message by loading lecturer image", e.getMessage());
                e.printStackTrace();
            }
        }
        return true;
    }
}
