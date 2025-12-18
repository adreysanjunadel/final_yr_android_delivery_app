package lk.javainstitute.swiftex.admin.account_management;

import static android.view.View.GONE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backend.Routes;
import dto.City_DTO;
import dto.Response_DTO;
import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.databinding.FragmentUserProfileBinding;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import view_model.SharedDropdownDataViewModel;

public class AdminUserManageExistingAccount extends Fragment {

    private FragmentUserProfileBinding binding;
    private boolean isPersonalCollapsed;
    private boolean isAddressCollapsed;
    private ConstraintLayout rootContainer;
    private SharedDropdownDataViewModel sharedViewModel;
    private Spinner provinceSpinner;
    private Spinner citySpinner;

    private Map<String, String> provinceMap;
    private Map<String, String> cityMap;

    private Button updateUsersDetailsButton;
    private Button updateUserAddressDetailsButton;

    private String selectedCityId;

    private boolean initialSelection = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        rootContainer = binding.userProfileFragmentConstraintLayout;

        cityMap = new HashMap<>();

        provinceSpinner = binding.provinceSpinner;
        citySpinner = binding.citySpinner;

        updateUsersDetailsButton = binding.updateUserDetailsButton;
        updateUserAddressDetailsButton = binding.updateUserAddressButton;

        setCitySpinnerListener();

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedDropdownDataViewModel.class); // Use requireActivity()

        sharedViewModel.getProvinceMap().observe(getViewLifecycleOwner(), provinceMap -> {
            // Populate your provinceSpinner here using the provinceMap
            if (provinceMap != null) {
                // Convert map to array adapter
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, new ArrayList<>(provinceMap.keySet()));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                provinceSpinner.setAdapter(adapter);
            }
        });
        sharedViewModel.getCityMap().observe(getViewLifecycleOwner(), cityMapData -> {
            if (cityMapData != null) {
                cityMap.clear(); // Clear existing data to avoid duplicates
                cityMap.putAll(cityMapData); // Use putAll for efficiency
                List<String> cityNames = new ArrayList<>(cityMap.keySet());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, cityNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                citySpinner.setAdapter(adapter);
            } else {
                // Handle the case where cityMapData is null (e.g., show an error message)
                Log.w("CityMap", "City map data is null");
                Toast.makeText(requireContext(), "Error loading cities.", Toast.LENGTH_SHORT).show();
                citySpinner.setAdapter(null); // Clear the spinner
            }
        });

        Button saveUserDetailsButton = binding.saveUserDetailsButton;
        Button saveUserAddressDetailsButton = binding.saveUserAddressDetailsButton;
        saveUserDetailsButton.setVisibility(GONE);
        saveUserAddressDetailsButton.setVisibility(GONE);

        ImageView userDetailsCollapsible = binding.userDetailsCollapsible;
        ConstraintLayout profilePersonalConstraintLayout = binding.profilePersonalConstraintLayout;
        ImageView addressDetailsCollapsible = binding.addressDetailsCollapsible;
        ConstraintLayout profileAddressConstraintLayout = binding.profileAddressConstraintLayout;

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int initialHeight1 = profilePersonalConstraintLayout.getHeight();
                userDetailsCollapsible.setOnClickListener(v->{
                    if(isPersonalCollapsed) {
                        expand(profilePersonalConstraintLayout, initialHeight1);
                        userDetailsCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(profilePersonalConstraintLayout, initialHeight1);
                        userDetailsCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isPersonalCollapsed = !isPersonalCollapsed;
                });

                int initialHeight2 = profileAddressConstraintLayout.getHeight();
                addressDetailsCollapsible.setOnClickListener(v->{
                    if(isAddressCollapsed) {
                        expand(profileAddressConstraintLayout, initialHeight2);
                        addressDetailsCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(profileAddressConstraintLayout, initialHeight2);
                        addressDetailsCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isAddressCollapsed = !isAddressCollapsed;
                });
            }
        });

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateAndSetProvinceListener();
    }

    private void populateAndSetProvinceListener() {

        // Check if the adapter is already set. If not, set it.
        if (provinceSpinner.getAdapter() == null || provinceSpinner.getAdapter().getCount() == 0) {
            sharedViewModel.getProvinceMap().observe(getViewLifecycleOwner(), provinceMap -> {
                if (provinceMap != null) {
                    this.provinceMap = provinceMap; // Store the map
                    List<String> spinnerList = new ArrayList<>(provinceMap.keySet());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, spinnerList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    provinceSpinner.setAdapter(adapter);

                    // Set the onItemClickListener *after* the adapter is set.
                    setProvinceSpinnerListener();
                }
            });
        } else {
            setProvinceSpinnerListener();
        }
    }

    private void setProvinceSpinnerListener() {
        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedProvinceName = provinceSpinner.getItemAtPosition(position).toString();
                String selectedProvinceId = provinceMap.get(selectedProvinceName);

                fetchCities(selectedProvinceId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                provinceSpinner.setAdapter(null);
            }
        });
    }

    private void setCitySpinnerListener() {
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (cityMap != null) { // Check if cityMap is not null
                    String selectedCityName = citySpinner.getItemAtPosition(position).toString();
                    selectedCityId = cityMap.get(selectedCityName);
                    if (selectedCityId != null) {
                        Log.d("Selected City ID", selectedCityId);
                    } else {
                        Log.w("City Selection", "Selected city ID is null. City name: " + selectedCityName);
                        // Handle the case where the ID is null (e.g., show an error)
                    }
                } else {
                    Log.w("City Selection", "City map is null.");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCityId = null;
            }
        });
    }

    private void fetchCities(String selectedProvinceId) {
        new Thread(() -> {
            try {
                Gson gson = new Gson();
                OkHttpClient okHttpClient = new OkHttpClient();
                Routes routes = new Routes();
                String path = routes.getEnv_url();

                RequestBody requestBody = RequestBody.create(selectedProvinceId, MediaType.parse("application/json")); // Send ID as string
                Log.i("ProvinceID", selectedProvinceId);
                Request request = new Request.Builder()
                        .url(path + "ProvinceSetCity")
                        .post(requestBody)
                        .build();

                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();
                Log.d("Raw JSON Response", responseBody);

                Response_DTO response_dto = gson.fromJson(responseBody, Response_DTO.class);
                Log.d("Response DTO", gson.toJson(response_dto));

                if (response_dto.isSuccess()) {
                    Type type = new TypeToken<List<City_DTO>>() {
                    }.getType();
                    List<City_DTO> cityList = gson.fromJson(gson.toJson(response_dto.getContent()), type);

                    cityMap = createCityMap(cityList);

                    getActivity().runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>(cityMap.keySet()));
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        citySpinner.setAdapter(adapter);
                        initialSelection = true; // Reset the flag
                    });
                } else {
                    handleError("Error loading cities " + response_dto.getContent());
                }

            } catch (IOException e) {
                handleError("Network error loading cities: " + e.getMessage());
            }
        }).start();
    }

    private void handleError(String errorMessage) {
        Log.e("Error", errorMessage);
        if (isAdded()) {
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                if (provinceSpinner != null) {
                    provinceSpinner.setAdapter(null);
                }
            });
        }
    }

    private Map<String, String> createCityMap(List<City_DTO> cityList) {
        Map<String, String> map = new HashMap<>();
        if (cityList != null) {
            for (City_DTO city : cityList) {
                map.put(city.getName(), String.valueOf(city.getId()));
            }
        }
        return map;
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
                v.setVisibility(GONE);
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
