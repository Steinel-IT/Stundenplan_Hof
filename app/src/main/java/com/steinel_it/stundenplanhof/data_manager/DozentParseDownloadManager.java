package com.steinel_it.stundenplanhof.data_manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.steinel_it.stundenplanhof.interfaces.HandleDozentTaskInterface;

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

public class DozentParseDownloadManager {

    HandleDozentTaskInterface context;

    ArrayList<String> contentList = new ArrayList<>();
    ArrayList<String> titelList = new ArrayList<>();
    Bitmap image;

    public DozentParseDownloadManager(HandleDozentTaskInterface context) {
        this.context = context;
    }

    private static final Handler uiThreadHandler = new Handler(Looper.getMainLooper());

    public void getDozent(String dozent) {
        if (contentList.isEmpty() || titelList.isEmpty() || image == null) {
            String replacedDozent = dozent.replace(".", "").toLowerCase();
            replacedDozent = replacedDozent.replace("msc ", "").replace(" ", "-");
            replacedDozent = replacedDozent.replace("ö", "oe").replace("ü", "ue").replace("ä", "ae");
            OkHttpClient okClient = new OkHttpClient();
            String url = "https://www.hof-university.de/ueber-uns/personen/professoren/" + replacedDozent + "/";
            Request request = new Request.Builder().url(url).build();

            okClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    System.out.println("Fail");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        assert response.body() != null;

                        Document docCompelte = Jsoup.parse(response.body().string());

                        Elements dozentContentRaw = docCompelte.select("div[class=row contact_persons]").select("div[class=bg]");

                        //Falls keine Dozenten vorhanden
                        if (dozentContentRaw.isEmpty()) {
                            uiThreadHandler.post(() -> context.onTaskFinished(new ArrayList<>(), new ArrayList<>(), null));
                            return;
                        }
                        //Parse Image src
                        String imageURL = null;
                        if (dozentContentRaw.first().select("img").first() != null)
                            imageURL = dozentContentRaw.first().select("img").first().attr("src");

                        for (int i = 0; i < dozentContentRaw.size(); i++) {
                            titelList.add(dozentContentRaw.get(i).select("h4").text());
                            dozentContentRaw.get(i).select("h4").remove();
                            if (i < 2) {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (Element line : dozentContentRaw.get(i).select("p")) {
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
                                contentList.add(dozentContentRaw.get(i).text());
                            }
                        }

                        //Parse Description //TODO: Peter Stöhr hat Problem wegen Aufzählung
                        Element dozentDescRaw = docCompelte.select("div[class=six mobile-one columns]").first();
                        titelList.add(dozentDescRaw.select("div[class=row sitesubtitle]").text());
                        contentList.add(dozentDescRaw.select("p").text());

                        //Load Image
                        if (imageURL != null) {
                            try {
                                InputStream in = new java.net.URL(imageURL).openStream();
                                image = BitmapFactory.decodeStream(in);
                            } catch (Exception e) {
                                Log.e("Error Message by Loading Dozent Image", e.getMessage());
                                e.printStackTrace();
                            }
                        }

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

}
