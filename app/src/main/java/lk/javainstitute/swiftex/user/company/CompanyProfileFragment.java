package lk.javainstitute.swiftex.user.company;

import static android.view.View.GONE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import dto.CompanyDetailsDTO;
import dto.Response_DTO;
import dto.UserHomeDetailsDTO;
import dto.User_DTO;
import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.databinding.FragmentCompanyProfileBinding;
import lk.javainstitute.swiftex.user.activities.LogInActivity;
import lk.javainstitute.swiftex.user.activities.UserHomeActivity;
import lk.javainstitute.swiftex.user.profile.UserProfileViewModel;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import view_model.SharedDropdownDataViewModel;

public class CompanyProfileFragment extends Fragment {

    private FragmentCompanyProfileBinding binding;
    private boolean isCompanyDetailsCollapsed = false;
    private boolean isCompanyAddressDetailsCollapsed = false;
    private ConstraintLayout rootContainer;
    private SharedDropdownDataViewModel sharedViewModel;
    private Spinner companyProvinceSpinner;
    private Spinner companyCitySpinner;
    private Spinner industrySpinner;

    private CompanyProfileViewModel viewModel;

    private Map<String, String> industryMap;
    private Map<String, String> provinceMap;
    private Map<String, String> cityMap;

    private String selectedCityId;
    private String selectedIndustryId;

    private Button saveCompanyDetailsButton;
    private Button updateCompanyDetailsButton;

    private boolean initialSelection = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCompanyProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        industrySpinner = binding.companyIndustrySpinner;
        companyProvinceSpinner = binding.companyProvinceSpinner;
        companyCitySpinner = binding.companyCitySpinner;

        saveCompanyDetailsButton = binding.createCompanyAccountButton;
        updateCompanyDetailsButton = binding.updateCompanyProfileButton;

        viewModel = new ViewModelProvider(requireActivity()).get(CompanyProfileViewModel.class);

        setCitySpinnerListener();
        setIndustrySpinnerListener();

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedDropdownDataViewModel.class);

        sharedViewModel.getProvinceMap().observe(getViewLifecycleOwner(), provinceMap -> {
            if (provinceMap != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, new ArrayList<>(provinceMap.keySet()));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                companyProvinceSpinner.setAdapter(adapter);
            }
        });
        sharedViewModel.getCityMap().observe(getViewLifecycleOwner(), cityMap -> {
            if (cityMap != null) {
                Log.d("CityMap", "Size: " + cityMap.size());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, new ArrayList<>(cityMap.keySet()));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                companyCitySpinner.setAdapter(adapter);
            }
        });

        sharedViewModel.getIndustryMap().observe(getViewLifecycleOwner(), industryMap -> {
            if (industryMap != null) {
                Log.d("IndustryMap", "Size: " + industryMap.size());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, new ArrayList<>(industryMap.keySet()));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                industrySpinner.setAdapter(adapter);
            }
        });

        rootContainer = binding.companyProfileFragmentConstraintLayout;

        ImageView companyDetailsCollapsible = binding.companyDetailsCollapsible;
        ConstraintLayout companyDetailsConstraintLayout = binding.companyDetailsConstraintLayout;
        ImageView companyProfileAddressDetailsCollapsible = binding.companyProfileAddressDetailsCollapsible;
        ConstraintLayout companyAddressDetailsConstraintLayout = binding.companyAddressDetailsConstraintLayout;

        Button createCompanyAccountButton = binding.createCompanyAccountButton;
        createCompanyAccountButton.setVisibility(GONE);
//        Button updateCompanyProfileButton = binding.updateCompanyProfileButton;
//        updateCompanyProfileButton.setVisibility(GONE);
        Button companyBlockAccountButton = binding.companyBlockAccountButton;
        companyBlockAccountButton.setVisibility(GONE);
        Button companyDeleteAccountButton = binding.companyDeleteAccountButton;
        companyDeleteAccountButton.setVisibility(GONE);


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

        SharedPreferences userDetailsSP = requireActivity().getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
        Integer userCompany = userDetailsSP.getInt("User Company", -1);

        if (userCompany == 0) {
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(requireContext(), "User has no company", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    CompanyDetailsDTO companyDetailsDTO = new CompanyDetailsDTO();
                    companyDetailsDTO.setId(userCompany); // Assuming userCompany is defined

                    OkHttpClient okHttpClient = new OkHttpClient();
                    Routes routes = new Routes();
                    String path = routes.getEnv_url();

                    RequestBody requestBody = RequestBody.create(gson.toJson(companyDetailsDTO), MediaType.get("application/json"));
                    Request request = new Request.Builder()
                            .url(path + "GetCompanyDetails")
                            .post(requestBody)
                            .build();

                    try {
                        Response response = okHttpClient.newCall(request).execute();
                        String responseText = response.body().string();
                        Response_DTO response_dto = gson.fromJson(responseText, Response_DTO.class);

                        if (response_dto.isSuccess()) {
                            CompanyDetailsDTO companyDetails = gson.fromJson(gson.toJson(response_dto.getContent()), CompanyDetailsDTO.class);

                            if (companyDetails != null) {
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isAdded()) {
                                            viewModel.setCompanyProfileData(companyDetails);
                                            Log.d("CompanyDetails", "Company Details Object: " + gson.toJson(companyDetails));

                                            SharedPreferences companyDetailsSP = requireActivity().getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = companyDetailsSP.edit();

                                            editor.putInt("Company ID", companyDetails.getId());
                                            if (companyDetails.getName() != null) {  // Check for null before putting
                                                editor.putString("Company Name", companyDetails.getName());
                                            } else {
                                                Log.w("SharedPreferences", "Company Name is null!");
                                            }
                                            editor.putString("Company Industry", companyDetails.getIndustry());
                                            editor.putString("Company AddressNo", companyDetails.getNo());
                                            editor.putString("Company Street1", companyDetails.getStreet1());
                                            editor.putString("Company Street2", companyDetails.getStreet2());
                                            editor.putString("Company City", companyDetails.getCity());
                                            editor.putString("Company Paid Status", companyDetails.getIs_company_paid());
                                            editor.putString("Company isApproved", companyDetails.getIs_company_approved());
                                            editor.apply();
                                            String storedName = companyDetailsSP.getString("Company Name", null);
                                            Log.d("SharedPreferences", "Stored Company Name: " + storedName);
                                        }
                                    }
                                });

                            } else {
                                Log.e("Deserialization Error", "Failed to deserialize JSON");
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), "Failed to get company data", Toast.LENGTH_SHORT).show();
                                });
                            }

                        } else {
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), response_dto.getContent().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } catch (IOException e) {
                        Log.e("SwiftExLog", "System Error: " + e.getMessage()); // Include the exception message
                        requireActivity().runOnUiThread(() -> { // Show error message on main thread
                            Toast.makeText(getContext(), "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }).start();
        }

        Button updateCompanyButton = binding.updateCompanyProfileButton;
        updateCompanyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCompany();
            }
        });

        Log.i("CompanyProfileFragment", "View Retuned");
        return view;
    }

    private void updateCompany(){
        SharedPreferences userDetailsSP = requireActivity().getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
        Integer userID = userDetailsSP.getInt("User Company", -1);

        EditText companyNameEditText = binding.editTextCompanyName;
        EditText companyAddressNo = binding.editTextCompanyAddressNo;
        EditText companyAddressStreet1 = binding.editTextCompanyAddressStreet1;
        EditText companyAddressStreet2 = binding.editTextCompanyAddressStreet2;

        String companyName = companyNameEditText.getText().toString();
        String addressNo = companyAddressNo.getText().toString();
        String street1 = companyAddressStreet1.getText().toString();
        String street2 = companyAddressStreet2.getText().toString();

        if (companyName.isEmpty()){
            Toast.makeText(getContext(), "Please Enter Company Name", Toast.LENGTH_LONG).show();
        } else if (selectedIndustryId == null) {
            Toast.makeText(getContext(), "Please select an Industry", Toast.LENGTH_LONG).show();
        } else if (addressNo.isEmpty()){
            Toast.makeText(getContext(), "Please Enter Address No", Toast.LENGTH_LONG).show();
        } else if (street1.isEmpty()){
            Toast.makeText(getContext(), "Please Enter Company Address Street 1", Toast.LENGTH_LONG).show();
        } {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    CompanyDetailsDTO companyDetailsDTO = new CompanyDetailsDTO();
                    companyDetailsDTO.setId(userID);
                    companyDetailsDTO.setName(companyName);
                    companyDetailsDTO.setIndustry(selectedIndustryId);
                    companyDetailsDTO.setNo(addressNo);
                    companyDetailsDTO.setStreet1(street1);
                    companyDetailsDTO.setStreet2(street2);
                    companyDetailsDTO.setCity(selectedCityId);
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("SwiftExLog", "Data Loaded");
                        }
                    });

                    OkHttpClient okHttpClient = new OkHttpClient();

                    Routes routes = new Routes();
                    String path = routes.getEnv_url();

                    if (selectedIndustryId != null && !selectedIndustryId.isEmpty()) {
                        companyDetailsDTO.setIndustry(selectedIndustryId);
                    } else {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Please select an Industry", Toast.LENGTH_LONG).show();

                            }
                        });
                        return;
                    }

                    if (selectedCityId != null && !selectedCityId.isEmpty()) {
                        companyDetailsDTO.setCity(selectedCityId);
                    } else {
                        Toast.makeText(getContext(), "Please select a City", Toast.LENGTH_LONG).show();
                        return;
                    }

                    RequestBody requestBody = RequestBody.create(gson.toJson(companyDetailsDTO), MediaType.get("application/json"));
                    Request request = new Request.Builder()
                            .url(path+"CompanyManagement")
                            .post(requestBody)
                            .build();

                    try{
                        Response response = okHttpClient.newCall(request).execute();
                        String responseText = response.body().string();
                        Log.i("ResponseText", responseText);
                        Response_DTO response_dto = gson.fromJson(responseText, Response_DTO.class);

                        if(response_dto.isSuccess()){
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "Company Updated Successfully", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), response_dto.getContent().toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                    } catch (IOException e) {
                        requireActivity().runOnUiThread(new Runnable() {
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateAndSetProvinceListener();
        populateAndSetIndustryListener();

        CompanyProfileViewModel viewModel = new ViewModelProvider(requireActivity()).get(CompanyProfileViewModel.class);

        viewModel.getCompanyProfileData().observe(getViewLifecycleOwner(), new Observer<CompanyDetailsDTO>() {
            @Override
            public void onChanged(CompanyDetailsDTO companyDetails) {

                if(companyDetails != null && binding != null){
                    Log.i("CompanyProfileFragment", "Binding NOT null");

                    binding.editTextCompanyName.setText(companyDetails.getName());

                    // *** SET INDUSTRY ***
                    if (industryMap != null) {
                        String industryName = getKeyFromValue(industryMap, companyDetails.getIndustry());
                        if (industryName != null) {
                            int position = getPositionInAdapter(industrySpinner, industryName);
                            if (position != -1) {
                                industrySpinner.setSelection(position);
                            } else {
                                Log.w("Industry", "Industry name not found in map: " + industryName);
                            }
                        } else {
                            Log.w("Industry", "Industry ID not found in map: " + companyDetails.getIndustry());
                        }
                    }

                    // *** SET CITY ***
                    if (cityMap != null) {
                        String cityName = getKeyFromValue(cityMap, companyDetails.getCity());
                        if (cityName != null) {
                            int position = getPositionInAdapter(companyCitySpinner, cityName);
                            if (position != -1) {
                                companyCitySpinner.setSelection(position);
                            } else {
                                Log.w("City", "City name not found in map: " + cityName);
                            }
                        } else {
                            Log.w("City", "City ID not found in map: " + companyDetails.getCity());
                        }
                    }

                    binding.editTextCompanyAddressNo.setText(companyDetails.getNo());
                    binding.editTextCompanyAddressStreet1.setText(companyDetails.getStreet1());
                    binding.editTextCompanyAddressStreet2.setText(companyDetails.getStreet2());

                } else if (binding != null) {
                    Log.i("CompanyProfileFragment", "No details");
                    binding.editTextCompanyName.setText("");
                    binding.editTextCompanyAddressNo.setText("");
                    binding.editTextCompanyAddressStreet1.setText("");
                    binding.editTextCompanyAddressStreet2.setText("");

                    saveCompanyDetailsButton.setVisibility(View.VISIBLE);
                    updateCompanyDetailsButton.setVisibility(GONE);
                }
            }
        });
    }

    private String getKeyFromValue(Map<String, String> map, String value) {
        for (String key : map.keySet()) {
            if (map.get(key).equals(value)) {
                return key;
            }
        }
        return null;
    }

    private int getPositionInAdapter(Spinner spinner, String itemName) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).equals(itemName)) {
                    return i;
                }
            }
        }
        return -1;
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

    private void populateAndSetIndustryListener() {
        if (industrySpinner.getAdapter() == null || industrySpinner.getAdapter().getCount() == 0) {
            sharedViewModel.getIndustryMap().observe(getViewLifecycleOwner(), industryMap -> {
                if (industryMap != null) {
                    this.industryMap = industryMap;
                    List<String> spinnerList = new ArrayList<>(industryMap.keySet());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, spinnerList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    industrySpinner.setAdapter(adapter);

                    setIndustrySpinnerListener();
                }
            });
        } else {
            setIndustrySpinnerListener();
        }
    }

    private void setCitySpinnerListener() {
        companyCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (cityMap != null) {
                    String selectedCityName = companyCitySpinner.getItemAtPosition(position).toString();
                    selectedCityId = cityMap.get(selectedCityName);

                    if (selectedCityId != null) {
                        Log.d("Selected City ID", selectedCityId);
                    } else {
                        Log.w("City Selection", "Selected city ID is null.");
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
                        Log.w("Industry Selection", "Selected Industry ID is null.");
                    }
                } else {
                    Log.w("Industry Selection", "Industry map is null.");
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