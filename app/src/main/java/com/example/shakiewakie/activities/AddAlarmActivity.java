package com.example.shakiewakie.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shakiewakie.R;
import com.example.shakiewakie.models.Alarm;
import com.example.shakiewakie.utils.AlarmReceiver;
import com.example.shakiewakie.utils.DatabaseHelper;
import java.util.Calendar;

public class AddAlarmActivity extends AppCompatActivity {
    private Button pickTimeButton, saveAlarmButton;
    private String selectedTime;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        pickTimeButton = findViewById(R.id.btn_pick_time);
        saveAlarmButton = findViewById(R.id.btn_save_alarm);
        dbHelper = new DatabaseHelper(this);

        pickTimeButton.setOnClickListener(v -> showTimePicker());

        saveAlarmButton.setOnClickListener(v -> {
            if (selectedTime != null) {
                Alarm newAlarm = new Alarm(0, selectedTime, true);
                int alarmId = (int) dbHelper.addAlarm(newAlarm);
                scheduleAlarm(selectedTime, alarmId);

                Toast.makeText(AddAlarmActivity.this, "Alarm set for " + selectedTime, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddAlarmActivity.this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(AddAlarmActivity.this, "Please pick a time first!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
            pickTimeButton.setText("Picked Time: " + selectedTime);
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void scheduleAlarm(String time, int alarmId) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
