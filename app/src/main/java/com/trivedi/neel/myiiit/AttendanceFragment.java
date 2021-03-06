package com.trivedi.neel.myiiit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.trivedi.neel.myiiit.attendance.Attendance;
import com.trivedi.neel.myiiit.attendance.AttendanceData;
import com.trivedi.neel.myiiit.utils.Callback2;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AttendanceFragment extends Fragment {

    TextView last_update;
    ExpandableListView coursesListview;
    SwipeRefreshLayout pullToRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_attendence, container, false);

        last_update = rootView.findViewById(R.id.attd_last_update);
        coursesListview = rootView.findViewById(R.id.course_list);
        pullToRefresh = rootView.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateAttendance(true);
            }
        });
        return  rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateAttendance(false);
    }

    private void updateAttendance(boolean forceUpdate){
        pullToRefresh.setRefreshing(true);

        Attendance.getAttendance(getContext(), forceUpdate, new Callback2<List<AttendanceData>, Calendar>() {
            @Override
            public void success(List<AttendanceData> attendanceData, Calendar lastUpdated) {
                DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
                last_update.setText("Last Updated : " + dateFormat.format(lastUpdated.getTimeInMillis()));

                ArrayList<AttendanceData> currentCourse = new ArrayList<>();
                ArrayList<AttendanceData> otherCourse = new ArrayList<>();

                for (AttendanceData course : attendanceData){
                    if (course.getIsCurrent()){
                        currentCourse.add(course);
                    }
                    else{
                        otherCourse.add(course);
                    }
                }

                AttendanceAdapter attendanceAdapter = new AttendanceAdapter(getContext(), currentCourse, otherCourse);
                coursesListview.setAdapter(attendanceAdapter);

                if (attendanceAdapter.getGroupCount() > 0) {
                    coursesListview.expandGroup(0);
                }

                pullToRefresh.setRefreshing(false);
            }

            @Override
            public void error(Exception e) {
                Log.d("error", e.getLocalizedMessage());
                pullToRefresh.setRefreshing(false);
            }
        });
    }
}
