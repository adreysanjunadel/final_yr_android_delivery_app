package lk.javainstitute.swiftex.user.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import backend.Routes;
import dto.Response_DTO;
import dto.User_DTO;
import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.databinding.FragmentUserHomeBinding;
import lk.javainstitute.swiftex.user.activities.LogInActivity;
import lk.javainstitute.swiftex.user.activities.UserHomeActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserHomeFragment extends Fragment {

    private FragmentUserHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();



        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}