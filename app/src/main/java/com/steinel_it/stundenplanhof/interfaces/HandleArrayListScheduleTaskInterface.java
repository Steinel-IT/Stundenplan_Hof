package com.steinel_it.stundenplanhof.interfaces;

import com.steinel_it.stundenplanhof.objects.ScheduleEntry;

import java.util.ArrayList;

public interface HandleArrayListScheduleTaskInterface {

    void onTaskFinished(ArrayList<ScheduleEntry> result, ArrayList<String> titel);

}
