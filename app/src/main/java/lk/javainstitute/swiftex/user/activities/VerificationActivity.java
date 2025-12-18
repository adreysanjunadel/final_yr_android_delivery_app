package lk.javainstitute.swiftex.user.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.IOException;

import backend.Routes;
import dto.Response_DTO;
import dto.User_DTO;
import hardware.ShakeSensorListener;
import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.user.fragments.CodeVerificationFragment;
import lk.javainstitute.swiftex.user.fragments.ShakeVerifyFragment;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VerificationActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private ShakeSensorListener sensorListener;
    private boolean shakeDetected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_activity_verification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mapsActivityConstraintLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction
                .replace(R.id.shakeFragmentContainerView, ShakeVerifyFragment.class, null)
                .setReorderingAllowed(true)
                .commit();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null){
            Log.i("SwiftExLog", "TYPE_ACCELEROMETER Sensor Found");
        } else {
            Log.i("SwiftExLog", "TYPE_ACCELEROMETER Sensor Not Found");
            return;
        }

        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(sensor != null){
            sensorListener = new ShakeSensorListener(() ->  checkShakeDetection());
            sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            Log.i("SwiftExLog", "Sensor Registered");
        } else {
            Log.i("SwiftExLog", "Sensor is null");
        }

        Button buttonVerifyGoBack = findViewById(R.id.buttonVerifyGoBack);
        buttonVerifyGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VerificationActivity.this, LogInActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void checkShakeDetection() {
        if (!shakeDetected) {
            shakeDetected = true;
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.findFragmentById(R.id.shakeFragmentContainerView) instanceof ShakeVerifyFragment) {
                Log.i("SwiftEx", "Shake detected, moving to next view");

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction
                        .replace(R.id.shakeFragmentContainerView, CodeVerificationFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                            Gson gson = new Gson();

                            User_DTO user_dto = new User_DTO();
                            SharedPreferences userEmailSP = getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
                            String userEmail = userEmailSP.getString("Email", "");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("GsonLog", userEmail);
                                }
                            });
                            user_dto.setEmail(userEmail);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("GsonLog", "Sent Email");
                                }
                            });

                            OkHttpClient okHttpClient = new OkHttpClient();

                            Routes routes = new Routes();
                            String path = routes.getEnv_url();

                            RequestBody requestBody = RequestBody.create(gson.toJson(user_dto), MediaType.get("application/json"));
                            Request request = new Request.Builder()
                                    .url(path + "UserVerification")
                                    .post(requestBody)
                                    .build();

                            try {
                                Response response = okHttpClient.newCall(request).execute();
                                String responseText = response.body().string();
                                Log.i("ResponseText", responseText);
                                Response_DTO response_dto = gson.fromJson(responseText, Response_DTO.class);

                                if (response_dto.isSuccess()) {
                                    //successfully sent
                                    Snackbar.make(findViewById(R.id.mapsActivityConstraintLayout), "Mail sent successfully", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Snackbar.make(findViewById(R.id.mapsActivityConstraintLayout), response_dto.getContent().toString(), Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            } catch (IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.i("SwiftExLog", e.toString());
                                    }
                                });
                            }
                        }
                }).start();



            }
        }
        if (sensorManager != null && sensorListener != null) {
            sensorManager.unregisterListener(sensorListener);
            sensorListener = null;
            Log.i("SwiftExLog", "Unregistering sensor Listener");
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the listener when the activity is destroyed
        if (sensorManager != null && sensorListener != null) {
            sensorManager.unregisterListener(sensorListener);
            Log.i("SwiftExLog", "onDestroy Unregistering sensor Listener");
        }
    }
}