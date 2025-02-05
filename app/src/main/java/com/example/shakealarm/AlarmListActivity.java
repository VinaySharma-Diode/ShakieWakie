package com.example.shakealarm;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AlarmListActivity extends AppCompatActivity {
    private static final String TAG = "AlarmListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        RecyclerView recyclerView = findViewById(R.id.alarm_list_recycler_view);
        TextView emptyView = findViewById(R.id.empty_view);

        // Retrieve alarm list from intent
        List<AlarmItem> alarmList = getIntent().getParcelableArrayListExtra("alarmList");

        // Log for debugging
        Log.d(TAG, "Received alarms: " + (alarmList != null ? alarmList.size() : "null"));

        // Ensure alarmList is not null
        if (alarmList == null) {
            alarmList = new ArrayList<>();
        }

        // Setup RecyclerView
        AlarmAdapter adapter = new AlarmAdapter(alarmList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Show/hide empty view
        if (alarmList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}