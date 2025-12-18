package lk.javainstitute.swiftex.admin.driver_management;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import backend.Routes;
import dto.DriverDetailsDTO;
import dto.Response_DTO;
import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.databinding.FragmentAdminDriverManagementHomeBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdminDriverManagementHomeFragment extends Fragment {

    private FragmentAdminDriverManagementHomeBinding binding;
    private boolean isCreateNewDriverCollapsed = false;
    private boolean isExistingDriverCollapsed = false;
    private ConstraintLayout rootContainer;
    private RecyclerView driverRecyclerView;
    private ArrayList<DriverDetailsDTO> driverList;
    private DriverAdapter driverAdapter;
    private OkHttpClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminDriverManagementHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        rootContainer = binding.adminDriverManagementConstraintLayout;

        ImageView driverManagementCreateDriverCollapsible = binding.driverManagementCreateDriverCollapsible;
        Button driverManagementCreateNewDriverButton = binding.driverManagementCreateNewDriverButton;
        ImageView driverManagementExistingDriversCollapsible = binding.driverManagementExistingDriversCollapsible;
        ConstraintLayout driverManagementExistingDriverConstraintLayout = binding.driverManagementExistingDriverConstraintLayout;

        driverManagementCreateNewDriverButton.setOnClickListener(view1 -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentDriverManagementFragmentContainerView, new AdminCreateNewDriverFragment())
                    .addToBackStack(null)
                    .commit();
        });

        TextView driverManagementExistingDriverTextView = binding.driverManagementExistingDriverTextView;
        driverManagementExistingDriverTextView.setOnClickListener(view12 -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentDriverManagementFragmentContainerView, new AdminExistingDriverFragment())
                    .addToBackStack(null)
                    .commit();
        });

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int initialHeight1 = driverManagementCreateNewDriverButton.getHeight();
                driverManagementCreateDriverCollapsible.setOnClickListener(v -> {
                    if (isCreateNewDriverCollapsed) {
                        expand(driverManagementCreateNewDriverButton, initialHeight1);
                        driverManagementCreateDriverCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(driverManagementCreateNewDriverButton, initialHeight1);
                        driverManagementCreateDriverCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isCreateNewDriverCollapsed = !isCreateNewDriverCollapsed;
                });

                int initialHeight2 = driverManagementExistingDriverConstraintLayout.getHeight();
                driverManagementExistingDriversCollapsible.setOnClickListener(v -> {
                    if (isExistingDriverCollapsed) {
                        expand(driverManagementExistingDriverConstraintLayout, initialHeight2);
                        driverManagementExistingDriversCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(driverManagementExistingDriverConstraintLayout, initialHeight2);
                        driverManagementExistingDriversCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isExistingDriverCollapsed = !isExistingDriverCollapsed;
                });
            }
        });

        driverRecyclerView = binding.driverManagementExistingDriversRecycleView;
        driverList = new ArrayList<>();
        driverAdapter = new DriverAdapter(driverList);
        driverRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        driverRecyclerView.setAdapter(driverAdapter);

        client = new OkHttpClient();

        loadDriverData();

        return view;
    }

    private void loadDriverData() {
        String url = Routes.getEnv_url()+"LoadDrivers";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    Type responseType = new TypeToken<Response_DTO>() {}.getType();
                    Response_DTO responseDto = gson.fromJson(responseBody, responseType);

                    if (responseDto.isSuccess()) {
                        String contentJson = gson.toJson(responseDto.getContent());
                        JsonObject jsonObject = gson.fromJson(contentJson, JsonObject.class);
                        JsonArray driverListJsonArray = jsonObject.getAsJsonArray("driverList");

                        Type listType = new TypeToken<List<DriverDetailsDTO>>() {}.getType();
                        List<DriverDetailsDTO> driverDetailsDTOs = gson.fromJson(driverListJsonArray.toString(), listType);

                        requireActivity().runOnUiThread(() -> {
                            driverList.clear();
                            driverList.addAll(driverDetailsDTOs);
                            driverAdapter.notifyDataSetChanged();
                        });
                    } else {
                        requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Error loading drivers", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Unsuccessful response", Toast.LENGTH_SHORT).show());
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

class DriverAdapter extends RecyclerView.Adapter<DriverViewHolder> {

    ArrayList<DriverDetailsDTO> driverDtoArrayList;

    public DriverAdapter(ArrayList<DriverDetailsDTO> driverDtoArrayList) {
        this.driverDtoArrayList = driverDtoArrayList;
    }

    @NonNull
    @Override
    public DriverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View driverView = layoutInflater.inflate(R.layout.driver_item, parent, false);
        return new DriverViewHolder(driverView);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverViewHolder holder, int position) {
        DriverDetailsDTO driver_dto = driverDtoArrayList.get(position);
        holder.driverItemFnameLnameTextView.setText(driver_dto.getFname() + " " + driver_dto.getLname());
        holder.driverItemEmailTextView.setText(driver_dto.getEmail());

        holder.driverCallButton.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_DIAL);
            i.setData(Uri.parse("tel:" + driver_dto.getMobile()));
            view.getContext().startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return driverDtoArrayList.size();
    }
}

class DriverViewHolder extends RecyclerView.ViewHolder {
    TextView driverItemFnameLnameTextView;
    TextView driverItemEmailTextView;
    ImageButton driverCallButton;

    public DriverViewHolder(@NonNull View itemView) {
        super(itemView);
        driverItemFnameLnameTextView = itemView.findViewById(R.id.driverItemFnameLnameTextView);
        driverItemEmailTextView = itemView.findViewById(R.id.driverItemEmailTextView);
        driverCallButton = itemView.findViewById(R.id.driverCallButton);
    }
}