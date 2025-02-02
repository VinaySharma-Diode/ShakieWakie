package com.example.shakiewakie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.util.Log; // <-- Add this import
import androidx.appcompat.app.AppCompatActivity;
import com.example.shakiewakie.R;
import com.example.shakiewakie.utils.AlarmReceiver;

public class HomeActivity extends AppCompatActivity {

    private Button startAlarmButton, addAlarmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize the buttons
        startAlarmButton = findViewById(R.id.btn_start_alarm);
        addAlarmButton = findViewById(R.id.btn_add_alarm);

        // Set click listener to start alarm manually for testing
        startAlarmButton.setOnClickListener(v -> startAlarm());

        // Set click listener to open AddAlarmActivity
        addAlarmButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AddAlarmActivity.class);
            startActivity(intent);
        });
    }

    // Method to start the alarm manually (for testing purposes)
    private void startAlarm() {
        // Trigger the alarm immediately by sending a broadcast
        Intent intent = new Intent(HomeActivity.this, AlarmReceiver.class);
        sendBroadcast(intent);

        // Log to ensure the button click is detected
        Log.d("HomeActivity", "Manual alarm triggered");
    }
}
