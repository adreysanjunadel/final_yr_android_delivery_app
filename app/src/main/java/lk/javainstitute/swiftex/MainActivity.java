package lk.javainstitute.swiftex;

import android.content.Intent;
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
import lk.javainstitute.swiftex.admin.activities.AdminLoginActivity;
import lk.javainstitute.swiftex.user.activities.LogInActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mapsActivityConstraintLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();

            }
        });



        Button buttonLogIn = findViewById(R.id.buttonLogIn);
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, LogInActivity.class);
                startActivity(i);
            }
        });

        Button buttonAdminLogIn = findViewById(R.id.buttonAdminLogin);
        buttonAdminLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AdminLoginActivity.class);
                startActivity(i);
            }
        });

    }

    private void registerUser(){
        EditText editTextFname = findViewById(R.id.editTextFname);
        EditText editTextLname = findViewById(R.id.editTextLname);
        EditText editTextMobile = findViewById(R.id.editTextMobile);
        EditText editTextNic = findViewById(R.id.editTextNic);
        EditText editTextEmail = findViewById(R.id.editTextEmail);

        String userFirstName = editTextFname.getText().toString();
        String userLastName = editTextLname.getText().toString();

        String userMobile = editTextMobile.getText().toString();
        String userNIC = editTextNic.getText().toString();
        String userEmail = editTextEmail.getText().toString();

        if (userFirstName.isEmpty()){
            Toast.makeText(MainActivity.this, "Please Enter First Name", Toast.LENGTH_LONG).show();
        } else if (userLastName.isEmpty()){
            Toast.makeText(MainActivity.this, "Please Enter Last Name", Toast.LENGTH_LONG).show();
        } else if (userMobile.isEmpty()){
            Toast.makeText(MainActivity.this, "Please Enter Mobile", Toast.LENGTH_LONG).show();
        } else if (userNIC.isEmpty()){
            Toast.makeText(MainActivity.this, "Please Enter NIC", Toast.LENGTH_LONG).show();
        } else if(userEmail.isEmpty()){
            Toast.makeText(MainActivity.this, "Please Enter Email", Toast.LENGTH_LONG).show();
        } else {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    User_DTO user_dto = new User_DTO();
                    user_dto.setId(0);
                    user_dto.setFname(userFirstName);
                    user_dto.setLname(userLastName);
                    user_dto.setMobile(userMobile);
                    user_dto.setNic(userNIC);
                    user_dto.setEmail(userEmail);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("SwiftExLog", "Data Loaded");
                        }
                    });

                    OkHttpClient okHttpClient = new OkHttpClient();

                    Routes routes = new Routes();
                    String path = routes.getEnv_url();

                    RequestBody requestBody = RequestBody.create(gson.toJson(user_dto), MediaType.get("application/json"));
                    Request request = new Request.Builder()
                            .url(path+"SignUp")
                            .post(requestBody)
                            .build();

                    try{
                        Response response = okHttpClient.newCall(request).execute();
                        String responseText = response.body().string();
                        Log.i("ResponseText", responseText);
                        Response_DTO response_dto = gson.fromJson(responseText, Response_DTO.class);

                        if(response_dto.isSuccess()){
                            Intent i = new Intent(MainActivity.this, LogInActivity.class);
                            i.putExtra("userJson", gson.toJson(response_dto));
                            startActivity(i);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, response_dto.getContent().toString(), Toast.LENGTH_LONG).show();
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
}