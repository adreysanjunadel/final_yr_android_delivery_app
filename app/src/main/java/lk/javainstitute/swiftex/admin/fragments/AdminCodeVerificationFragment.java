package lk.javainstitute.swiftex.admin.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.IOException;

import backend.Routes;
import dto.Admin_DTO;
import dto.Response_DTO;
import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.admin.activities.AdminHomeActivity;
import lk.javainstitute.swiftex.databinding.AdminFragmentCodeVerificationBinding;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdminCodeVerificationFragment extends Fragment {

    private AdminFragmentCodeVerificationBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = AdminFragmentCodeVerificationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Button buttonAdminVerify = view.findViewById(R.id.buttonAdminVerify);
        buttonAdminVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EditText codeVerificationTextView = binding.adminCodeVerificationEditText;
                        String verification = String.valueOf(codeVerificationTextView.getText());

                        Gson gson = new Gson();

                        Admin_DTO admin_dto = new Admin_DTO();
                        SharedPreferences adminEmailSP = getActivity().getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
                        String adminEmail = adminEmailSP.getString("Admin Email", "");
                        admin_dto.setEmail(adminEmail);
                        admin_dto.setVerification(verification);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("GsonLog", adminEmail + " " + verification);
                            }
                        });

                        OkHttpClient okHttpClient = new OkHttpClient();

                        Routes routes = new Routes();
                        String path = routes.getEnv_url();

                        RequestBody requestBody = RequestBody.create(gson.toJson(admin_dto), MediaType.get("application/json"));
                        Request request = new Request.Builder()
                                .url(path + "VerifyingAdmin")
                                .post(requestBody)
                                .build();

                        try {
                            Response response = okHttpClient.newCall(request).execute();
                            String responseText = response.body().string();
                            Log.i("ResponseText", responseText);
                            Response_DTO response_dto = gson.fromJson(responseText, Response_DTO.class);

                            if (response_dto.isSuccess()) {
                                //successful verification
                                Snackbar.make(getActivity().findViewById(R.id.mapsActivityConstraintLayout), "Admin Verification Successful", Snackbar.LENGTH_SHORT).show();

                                Intent intent = new Intent(requireContext(), AdminHomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                requireActivity().finish();

                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Snackbar.make(getActivity().findViewById(R.id.mapsActivityConstraintLayout), response_dto.getContent().toString(), Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        } catch (IOException e) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("SwiftExLog", e.toString());
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        return view;
    }
}