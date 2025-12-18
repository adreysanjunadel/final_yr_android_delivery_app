package lk.javainstitute.swiftex.admin.account_management;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import backend.Routes;
import dto.Driver_DTO;
import dto.Response_DTO;
import dto.UserHomeDetailsDTO;
import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.databinding.FragmentAdminUserManagementBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdminUserManagementFragment extends Fragment {

    private FragmentAdminUserManagementBinding binding;
    private boolean isCreateNewUserCollapsed;
    private boolean isExistingUsersCollapsed;
    private ConstraintLayout rootContainer;

    private RecyclerView userRecyclerView;
    private ArrayList<UserHomeDetailsDTO> userList;
    private UserAdapter userAdapter;
    private OkHttpClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminUserManagementBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        rootContainer = binding.adminUserManagementConstraintLayout;

        ImageView adminManagementCreateUserCollapsible = binding.adminManagementCreateUserCollapsible;
        Button adminManagementCreateNewUserButton = binding.adminManagementCreateNewUserButton;
        ImageView adminManagementExistingUsersCollapsible = binding.adminManagementExistingUsersCollapsible;
        ConstraintLayout adminManagementExistingUserConstraintLayout = binding.adminManagementExistingUserConstraintLayout;


        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int initialHeight1 = adminManagementCreateNewUserButton.getHeight();
                adminManagementCreateUserCollapsible.setOnClickListener(v->{
                    if(isCreateNewUserCollapsed) {
                        expand(adminManagementCreateNewUserButton, initialHeight1);
                        adminManagementCreateUserCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(adminManagementCreateNewUserButton, initialHeight1);
                        adminManagementCreateUserCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isCreateNewUserCollapsed = !isCreateNewUserCollapsed;
                });

                int initialHeight2 = adminManagementExistingUserConstraintLayout.getHeight();
                adminManagementExistingUsersCollapsible.setOnClickListener(v->{
                    if(isExistingUsersCollapsed) {
                        expand(adminManagementExistingUserConstraintLayout, initialHeight2);
                        adminManagementExistingUsersCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(adminManagementExistingUserConstraintLayout, initialHeight2);
                        adminManagementExistingUsersCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isExistingUsersCollapsed = !isExistingUsersCollapsed;
                });
            }
        });

        adminManagementCreateNewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentAccountManagementFragmentContainerView, new AdminUserManageAccountFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        TextView adminManagementExistingUserTextView = binding.adminManagementExistingUserTextView;
        adminManagementExistingUserTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentAccountManagementFragmentContainerView, new AdminUserManageExistingAccount())
                        .addToBackStack(null)
                        .commit();
            }
        });

        userRecyclerView = binding.adminManagementExistingUsersRecycleView;
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        userRecyclerView.setAdapter(userAdapter);

        client = new OkHttpClient();

        loadUserData();

        return view;
    }

    private void loadUserData() {
        String url = Routes.getEnv_url()+ "LoadUsers";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    Gson gson = new Gson();
                    Type responseType = new TypeToken<Response_DTO>() {}.getType();
                    Response_DTO responseDto = gson.fromJson(responseBody, responseType);

                    if (responseDto.isSuccess()) {
                        // No casting needed here
                        String contentJson = gson.toJson(responseDto.getContent());
                        JsonObject jsonObject = gson.fromJson(contentJson, JsonObject.class);
                        JsonArray userListJsonArray = jsonObject.getAsJsonArray("userList");

                        Type listType = new TypeToken<List<UserHomeDetailsDTO>>() {}.getType();
                        List<UserHomeDetailsDTO> userHomeDataList = gson.fromJson(userListJsonArray.toString(), listType);

                        requireActivity().runOnUiThread(() -> {
                            userList.clear();
                            userList.addAll(userHomeDataList);
                            userAdapter.notifyDataSetChanged();
                        });
                    } else {
                        // false case
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Error loading users", Toast.LENGTH_SHORT).show();

                        });
                    }

                } else {
                    // unsuccessful response handling
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Unsuccessful response", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void collapse(final View v, final int initialHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(initialHeight, 0);
        animator.addUpdateListener(animation -> {
            v.getLayoutParams().height = (int) animation.getAnimatedValue();
            v.requestLayout();
        });
        animator.setDuration(1000);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                v.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    private void expand(final View v, final int targetHeight) {
        v.setVisibility(View.VISIBLE);
        ValueAnimator animator = ValueAnimator.ofInt(0, targetHeight);
        animator.addUpdateListener(animation -> {
            v.getLayoutParams().height = (int) animation.getAnimatedValue();
            v.requestLayout();
        });
        animator.setDuration(1000);
        animator.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

class UserAdapter extends RecyclerView.Adapter<UserViewHolder>{

    ArrayList<UserHomeDetailsDTO> userArrayList;

    public UserAdapter(ArrayList<UserHomeDetailsDTO> userArrayList) {
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View userView = layoutInflater.inflate(R.layout.user_item, parent, false);
        UserViewHolder userViewHolder = new UserViewHolder(userView);

        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserHomeDetailsDTO userHomeDetailsDTO = userArrayList.get(position);
        holder.userItemFnameLnameTextView.setText(String.valueOf(userHomeDetailsDTO.getFname() + " " + userHomeDetailsDTO.getLname()));
        holder.userItemEmailTextView.setText(userHomeDetailsDTO.getEmail());
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }
}

class UserViewHolder extends RecyclerView.ViewHolder{
    TextView userItemFnameLnameTextView;
    TextView userItemEmailTextView;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        userItemFnameLnameTextView = itemView.findViewById(R.id.userItemFnameLnameTextView);
        userItemEmailTextView = itemView.findViewById(R.id.userItemEmailTextView);
    }
}