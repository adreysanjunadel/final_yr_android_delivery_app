package lk.javainstitute.swiftex.user.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import java.io.IOException;

import backend.Routes;
import dto.Response_DTO;
import dto.User_DTO;
import lk.javainstitute.swiftex.MainActivity;
import lk.javainstitute.swiftex.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_activity_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mapsActivityConstraintLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button buttonLogIn2 = findViewById(R.id.buttonLogIn2);
        buttonLogIn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Move to verify page with mail data
                EditText editTextEmail2 = findViewById(R.id.editTextEmail2);

                String userEmail = String.valueOf(editTextEmail2.getText());

                if(userEmail.isEmpty()){
                    Toast.makeText(LogInActivity.this, "Please Enter Email", Toast.LENGTH_LONG).show();
                } else {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();

                            User_DTO user_dto = new User_DTO();
                            user_dto.setEmail(userEmail);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("GsonLog", "Data Loaded");
                                }
                            });

                            OkHttpClient okHttpClient = new OkHttpClient();

                            Routes routes = new Routes();
                            String path = routes.getEnv_url();

                            RequestBody requestBody = RequestBody.create(gson.toJson(user_dto), MediaType.get("application/json"));
                            Request request = new Request.Builder()
                                    .url(path+"UserLogin")
                                    .post(requestBody)
                                    .build();

                            try{
                                Response response = okHttpClient.newCall(request).execute();
                                String responseText = response.body().string();
                                Log.i("ResponseText", responseText);
                                Response_DTO response_dto = gson.fromJson(responseText, Response_DTO.class);

                                if(response_dto.isSuccess()){
                                    Intent i = new Intent(LogInActivity.this, VerificationActivity.class);
                                    i.putExtra("userJson", gson.toJson(response_dto));
                                    SharedPreferences userEmailSP = getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = userEmailSP.edit();
                                    editor.putString("Email", userEmail);
                                    editor.apply();

                                    startActivity(i);
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LogInActivity.this, response_dto.getContent().toString(), Toast.LENGTH_SHORT).show();
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
        });

        Button buttonSignUp2 = findViewById(R.id.buttonSignUp2);
        buttonSignUp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LogInActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}