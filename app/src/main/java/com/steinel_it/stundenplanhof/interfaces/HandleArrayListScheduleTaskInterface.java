package com.steinel_it.stundenplanhof.interfaces;

import com.steinel_it.stundenplanhof.objects.SchedulerEntry;

import java.util.ArrayList;

public interface HandleArrayListScheduleTaskInterface {

    void onTaskFinished(ArrayList<SchedulerEntry> result);

}
