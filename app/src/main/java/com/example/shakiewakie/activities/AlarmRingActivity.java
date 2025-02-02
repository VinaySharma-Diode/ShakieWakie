package com.example.shakiewakie.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.hardware.SensorManager;
import android.util.Log;
import com.example.shakiewakie.R;
import com.example.shakiewakie.utils.ShakeDetector;

public class AlarmRingActivity extends AppCompatActivity implements ShakeDetector.ShakeListener {

    private MediaPlayer mediaPlayer;
    private Button stopButton;
    private TextView alarmText;
    private ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ring);

        // Initialize views
        stopButton = findViewById(R.id.btn_stop_alarm);
        alarmText = findViewById(R.id.alarm_text);

        // Set alarm text
        alarmText.setText("Alarm is ringing...");

        // Play alarm sound
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound);
        mediaPlayer.start();

        // Initialize shake detector and start listening for shakes
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        shakeDetector = new ShakeDetector(this);
        shakeDetector.start(sensorManager);

        // Stop the alarm when the button is clicked
        stopButton.setOnClickListener(v -> {
            stopAlarm();
        });
    }

    @Override
    public void onShake() {
        Log.d("AlarmRingActivity", "Shake detected! Stopping alarm.");
        stopAlarm();
    }

    private void stopAlarm() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            finish();  // Close the activity after stopping the alarm
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (shakeDetector != null) {
            // Stop the shake detector when the activity is stopped
            SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            shakeDetector.stop(sensorManager);
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
