package com.steinel_it.stundenplanhof.interfaces;

import android.graphics.Bitmap;

import java.util.ArrayList;

public interface HandleDozentTaskInterface {

    void onTaskFinished(ArrayList<String> titel, ArrayList<String> contentList, Bitmap image);

}
