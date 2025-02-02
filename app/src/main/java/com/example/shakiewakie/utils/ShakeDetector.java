package com.example.shakiewakie.utils;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {

    private static final float SHAKE_THRESHOLD = 900.0f; // Customize this value
    private float lastX, lastY, lastZ;
    private long lastTime;

    private ShakeListener shakeListener;

    public interface ShakeListener {
        void onShake();
    }

    public ShakeDetector(ShakeListener listener) {
        this.shakeListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastTime) > 100) {
                long diffTime = currentTime - lastTime;
                lastTime = currentTime;

                float deltaX = event.values[0] - lastX;
                float deltaY = event.values[1] - lastY;
                float deltaZ = event.values[2] - lastZ;

                lastX = event.values[0];
                lastY = event.values[1];
                lastZ = event.values[2];

                float shake = Math.abs(deltaX + deltaY + deltaZ) / diffTime * 10000;

                if (shake > SHAKE_THRESHOLD) {
                    shakeListener.onShake();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void start(SensorManager sensorManager) {
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void stop(SensorManager sensorManager) {
        sensorManager.unregisterListener(this);
    }
}
