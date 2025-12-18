package hardware;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public class ShakeSensorListener implements SensorEventListener {

    private static final float ALPHA = 0.8f; // filter gravity

    private static final float SHAKE_THRESHOLD = 12f;
    private static final long SHAKE_TIME_GAP = 300;
    private long previousTime;
    private float[] previousGravity = {0.0f, 0.0f, 0.0f}; // Previous reading
    private boolean shakeDetected = false;
    private final Runnable callback;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];


            float[] gravity = lowPassFilter(x, y, z);
            float filteredX = x - gravity[0];
            float filteredY = y - gravity[1];
            float filteredZ = z - gravity[2];

            float accelerationMagnitude = (float) Math.sqrt(filteredX * filteredX + filteredY * filteredY + filteredZ * filteredZ);

            // check shake
            if (accelerationMagnitude > SHAKE_THRESHOLD) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - previousTime > SHAKE_TIME_GAP) {
                    // Shake detected, set flag to true
                    previousTime = currentTime;
                    Log.i("SwiftExShake", "Shake Detected by Sensor and set flag to true and calling the callback.");
                    callback.run();

                }
            }
        }
    }


    private float[] lowPassFilter(float x, float y, float z) {
        float[] gravity = new float[3];

        gravity[0] = ALPHA * previousGravity[0] + (1 - ALPHA) * x;
        gravity[1] = ALPHA * previousGravity[1] + (1 - ALPHA) * y;
        gravity[2] = ALPHA * previousGravity[2] + (1 - ALPHA) * z;

        previousGravity[0] = gravity[0];
        previousGravity[1] = gravity[1];
        previousGravity[2] = gravity[2];

        return gravity;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Do nothing here
    }

    public ShakeSensorListener(Runnable callback) {
        previousTime = 0;
        this.callback = callback;
    }


}