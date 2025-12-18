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
import lk.javainstitute.swiftex.databinding.FragmentCompanyProfileBinding;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import view_model.SharedDropdownDataViewModel;

public class AdminManageExistingCompanyAccountFragment extends Fragment {

    private FragmentCompanyProfileBinding binding;
    private boolean isCompanyDetailsCollapsed = false;
    private boolean isCompanyAddressDetailsCollapsed = false;
    private ConstraintLayout rootContainer;
    private SharedDropdownDataViewModel sharedViewModel;
    private Spinner companyProvinceSpinner;
    private Spinner companyCitySpinner;
    private Spinner industrySpinner;

    private Map<String, String> provinceMap;
    private Map<String, String> cityMap;
    private Map<String, String> industryMap;

    private String selectedCityId;
    private String selectedIndustryId;

    private boolean initialSelection = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCompanyProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        rootContainer = binding.companyProfileFragmentConstraintLayout;

        companyProvinceSpinner = binding.companyProvinceSpinner;
        companyCitySpinner = binding.companyCitySpinner;

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedDropdownDataViewModel.class); // Use requireActivity()

        setCitySpinnerListener();
        setIndustrySpinnerListener();

        sharedViewModel.getProvinceMap().observe(getViewLifecycleOwner(), provinceMap -> {
            if (provinceMap != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, new ArrayList<>(provinceMap.keySet()));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                companyProvinceSpinner.setAdapter(adapter);
            }
        });
        sharedViewModel.getCityMap().observe(getViewLifecycleOwner(), cityMapData -> {
            if (cityMapData != null) {
                cityMap.clear();
                cityMap.putAll(cityMapData);
                List<String> cityNames = new ArrayList<>(cityMap.keySet());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, cityNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                companyCitySpinner.setAdapter(adapter);
            } else {
                Log.w("CityMap", "City map data is null");
                Toast.makeText(requireContext(), "Error loading cities.", Toast.LENGTH_SHORT).show();
                companyCitySpinner.setAdapter(null);
            }
        });

        sharedViewModel.getIndustryMap().observe(getViewLifecycleOwner(), industryMap -> {
            if (industryMap != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, new ArrayList<>(industryMap.keySet()));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                industrySpinner.setAdapter(adapter);
            }
        });
        ImageView companyDetailsCollapsible = binding.companyDetailsCollapsible;
        ConstraintLayout companyDetailsConstraintLayout = binding.companyDetailsConstraintLayout;
        ImageView companyProfileAddressDetailsCollapsible = binding.companyProfileAddressDetailsCollapsible;
        ConstraintLayout companyAddressDetailsConstraintLayout = binding.companyAddressDetailsConstraintLayout;

//        Button createCompanyAccountButton = binding.createCompanyAccountButton;
//        createCompanyAccountButton.setVisibility(GONE);
        Button updateCompanyProfileButton = binding.updateCompanyProfileButton;
        updateCompanyProfileButton.setVisibility(GONE);
        Button companyBlockAccountButton = binding.companyBlockAccountButton;
        updateCompanyProfileButton.setVisibility(GONE);
        Button companyDeleteAccountButton = binding.companyDeleteAccountButton;
        updateCompanyProfileButton.setVisibility(GONE);


        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int initialHeight1 = companyDetailsConstraintLayout.getHeight();
                companyDetailsCollapsible.setOnClickListener(v->{
                    if(isCompanyDetailsCollapsed) {
                        expand(companyDetailsConstraintLayout, initialHeight1);
                        companyDetailsCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(companyDetailsConstraintLayout, initialHeight1);
                        companyDetailsCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isCompanyDetailsCollapsed = !isCompanyDetailsCollapsed;
                });

                int initialHeight2 = companyAddressDetailsConstraintLayout.getHeight();
                companyProfileAddressDetailsCollapsible.setOnClickListener(v->{
                    if(isCompanyAddressDetailsCollapsed) {
                        expand(companyAddressDetailsConstraintLayout, initialHeight2);
                        companyProfileAddressDetailsCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(companyAddressDetailsConstraintLayout, initialHeight2);
                        companyProfileAddressDetailsCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isCompanyAddressDetailsCollapsed = !isCompanyAddressDetailsCollapsed;
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

        if (companyProvinceSpinner.getAdapter() == null || companyProvinceSpinner.getAdapter().getCount() == 0) {
            sharedViewModel.getProvinceMap().observe(getViewLifecycleOwner(), provinceMap -> {
                if (provinceMap != null) {
                    this.provinceMap = provinceMap;
                    List<String> spinnerList = new ArrayList<>(provinceMap.keySet());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, spinnerList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    companyProvinceSpinner.setAdapter(adapter);


                    setProvinceSpinnerListener();
                }
            });
        } else {
            setProvinceSpinnerListener();
        }
    }

    private void setProvinceSpinnerListener() {
        companyProvinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedProvinceName = companyProvinceSpinner.getItemAtPosition(position).toString();
                String selectedProvinceId = provinceMap.get(selectedProvinceName);

                fetchCities(selectedProvinceId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                companyProvinceSpinner.setAdapter(null);
            }
        });
    }

    private void setCitySpinnerListener() {
        companyCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (cityMap != null) { // Check if cityMap is not null
                    String selectedCityName = companyCitySpinner.getItemAtPosition(position).toString();
                    selectedCityId = cityMap.get(selectedCityName);
                    if (selectedCityId != null) {
                        Log.d("Selected City ID", selectedCityId);
                    } else {
                        Log.w("City Selection", "Selected city ID is null. City name: " + selectedCityName);
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


    private void setIndustrySpinnerListener() {
        industrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (industryMap != null) {
                    String selectedIndustryName = industrySpinner.getItemAtPosition(position).toString();
                    selectedIndustryId = industryMap.get(selectedIndustryName);
                    if (selectedIndustryId != null) {
                        Log.d("Selected Industry ID", selectedIndustryId);
                    } else {
                        Log.w("Industry Selection", "Selected Industry ID is null. City name: " + selectedIndustryName);

                    }
                } else {
                    Log.w("City Selection", "City map is null.");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedIndustryId = null;
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

                RequestBody requestBody = RequestBody.create(selectedProvinceId, MediaType.parse("application/json"));
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
                        companyCitySpinner.setAdapter(adapter);
                        initialSelection = true;
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
                if (companyProvinceSpinner != null) {
                    companyProvinceSpinner.setAdapter(null);
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