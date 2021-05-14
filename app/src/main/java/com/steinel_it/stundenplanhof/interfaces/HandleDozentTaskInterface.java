package com.steinel_it.stundenplanhof.interfaces;

import java.util.ArrayList;

public interface HandleDozentTaskInterface {

    void onTaskFinished(ArrayList<String> titel, ArrayList<String> contentList, String image);

}
