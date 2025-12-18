package lk.javainstitute.swiftex.admin.activities;

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
import dto.Admin_DTO;
import dto.Response_DTO;
import lk.javainstitute.swiftex.MainActivity;
import lk.javainstitute.swiftex.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdminLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.admin_activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mapsActivityConstraintLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button buttonAdminAdminLogin = findViewById(R.id.buttonAdminAdminLogin);
        buttonAdminAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextEmail3 = findViewById(R.id.editTextEmail3);

                String adminEmail = String.valueOf(editTextEmail3.getText());

                if(adminEmail.isEmpty()){
                    Toast.makeText(AdminLoginActivity.this, "Please Enter Email", Toast.LENGTH_LONG).show();
                } else {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();

                            Admin_DTO admin_dto = new Admin_DTO();
                            admin_dto.setEmail(adminEmail);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("GsonLog", adminEmail);
                                }
                            });

                            OkHttpClient okHttpClient = new OkHttpClient();

                            Routes routes = new Routes();
                            String path = routes.getEnv_url();

                            RequestBody requestBody = RequestBody.create(gson.toJson(admin_dto), MediaType.get("application/json"));
                            Request request = new Request.Builder()
                                    .url(path+"AdminLogin")
                                    .post(requestBody)
                                    .build();

                            try{
                                Response response = okHttpClient.newCall(request).execute();
                                String responseText = response.body().string();
                                Log.i("ResponseText", responseText);
                                Response_DTO response_dto = gson.fromJson(responseText, Response_DTO.class);

                                if(response_dto.isSuccess()){
                                    Intent i = new Intent(AdminLoginActivity.this, AdminVerificationActivity.class);
                                    i.putExtra("adminJson", gson.toJson(response_dto));
                                    SharedPreferences adminEmailSP = getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = adminEmailSP.edit();
                                    editor.putString("Admin Email", adminEmail);
                                    editor.apply();

                                    startActivity(i);
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(AdminLoginActivity.this, response_dto.getContent().toString(), Toast.LENGTH_SHORT).show();
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

        Button buttonAdminBack = findViewById(R.id.buttonAdminBack);
        buttonAdminBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AdminLoginActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

}