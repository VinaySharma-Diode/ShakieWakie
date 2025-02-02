package com.example.shakiewakie.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.example.shakiewakie.activities.AlarmRingActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Log to confirm the receiver is triggered
        Log.d("AlarmReceiver", "Alarm triggered by broadcast");

        // Start AlarmRingActivity when the alarm is triggered
        Intent alarmIntent = new Intent(context, AlarmRingActivity.class);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  // Needed to start activity outside of an activity context
        context.startActivity(alarmIntent);
    }
}
