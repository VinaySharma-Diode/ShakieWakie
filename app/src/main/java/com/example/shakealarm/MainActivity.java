package com.example.shakealarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "MainActivity";
    private static final String PREFS_NAME = "AlarmPrefs";
    private static final String ALARMS_KEY = "savedAlarms";

    // Sensor and Alarm Variables
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isAlarmActive = false;
    private static final float SHAKE_THRESHOLD = 50.0f;
    private static final int SHAKE_TIME_LAPSE = 500;
    private long lastShakeTime = 0;

    // UI Components
    private TextView alarmText;
    private TimePicker timePicker;
    private AlarmManager alarmManager;
    private MediaPlayer mediaPlayer;

    // Alarm List
    private List<AlarmItem> alarmList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Components
        initializeComponents();

        // Load Saved Alarms
        loadAlarms();

        // Check if alarm is triggered
        if (getIntent().getBooleanExtra("ALARM_TRIGGERED", false)) {
            startAlarm();
        }
    }

    private void initializeComponents() {
        // Find UI Components
        alarmText = findViewById(R.id.alarm_text);
        Button setAlarmButton = findViewById(R.id.set_alarm);
        Button startAlarmButton = findViewById(R.id.start_alarm);
        Button viewAlarmsButton = findViewById(R.id.view_alarms);
        timePicker = findViewById(R.id.time_picker);

        // Initialize Sensor Manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Initialize Alarm Manager
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Initialize Media Player
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound);

        // Set Button Listeners
        setAlarmButton.setOnClickListener(v -> setAlarm());
        startAlarmButton.setOnClickListener(v -> startAlarm());
        viewAlarmsButton.setOnClickListener(v -> viewAlarms());
    }

    private void loadAlarms() {
        try {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String alarmsJson = prefs.getString(ALARMS_KEY, null);

            if (alarmsJson != null) {
                Type type = new TypeToken<ArrayList<AlarmItem>>() {}.getType();
                List<AlarmItem> loadedAlarms = new Gson().fromJson(alarmsJson, type);

                if (loadedAlarms != null) {
                    alarmList.clear();
                    alarmList.addAll(loadedAlarms);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading alarms", e);
        }
    }

    private void saveAlarms() {
        try {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            String alarmsJson = new Gson().toJson(alarmList);
            editor.putString(ALARMS_KEY, alarmsJson);
            editor.apply();
        } catch (Exception e) {
            Log.e(TAG, "Error saving alarms", e);
        }
    }

    private void setAlarm() {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // Create Alarm Intent
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Set Alarm Time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Set Exact Alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        // Create and Save Alarm Item
        AlarmItem newAlarm = new AlarmItem(hour, minute);
        alarmList.add(newAlarm);
        saveAlarms();

        // Show Confirmation
        String alarmSetText = String.format("Alarm set for %02d:%02d", hour, minute);
        Toast.makeText(this, alarmSetText, Toast.LENGTH_SHORT).show();
    }

    private void startAlarm() {
        isAlarmActive = true;
        alarmText.setText("Shake to turn off alarm");
        Toast.makeText(this, "Alarm Ringing!", Toast.LENGTH_SHORT).show();

        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    private void viewAlarms() {
        try {
            // Ensure we have the latest alarms
            loadAlarms();

            // Check if alarm list is empty
            if (alarmList == null || alarmList.isEmpty()) {
                Toast.makeText(this, "No alarms set", Toast.LENGTH_SHORT).show();

                // Directly create and start the AlarmListActivity
                Intent intent = new Intent(this, AlarmListActivity.class);
                intent.putParcelableArrayListExtra("alarmList", new ArrayList<>());
                startActivity(intent);
                return;
            }

            // Create intent to start AlarmListActivity
            Intent intent = new Intent(this, AlarmListActivity.class);

            // Create a new ArrayList to pass to the intent
            ArrayList<AlarmItem> alarmArrayList = new ArrayList<>(alarmList);

            // Add the list to the intent
            intent.putParcelableArrayListExtra("alarmList", alarmArrayList);

            // Log the number of alarms for debugging
            Log.d(TAG, "Number of alarms to display: " + alarmArrayList.size());

            // Start the activity
            startActivity(intent);

        } catch (Exception e) {
            // Log the full stack trace for detailed error information
            Log.e(TAG, "Detailed error in viewAlarms", e);

            // Show a more informative error message
            Toast.makeText(this, "Error viewing alarms: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isAlarmActive) return;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float acceleration = (float) Math.sqrt(x * x + y * y + z * z);

        long currentTime = System.currentTimeMillis();

        if (acceleration > SHAKE_THRESHOLD && (currentTime - lastShakeTime) > SHAKE_TIME_LAPSE) {
            lastShakeTime = currentTime;
            alarmText.setText("Alarm Turned Off");
            isAlarmActive = false;
            Toast.makeText(this, "Alarm Off", Toast.LENGTH_SHORT).show();

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.prepareAsync();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not implemented
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
