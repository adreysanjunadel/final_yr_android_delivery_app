package lk.javainstitute.swiftex.admin.driver_management;

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
import dto.Item_Brand;
import dto.Item_Model;
import dto.Response_DTO;
import dto.Vehicle_Model;
import entity.Vehicle_Type;
import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.databinding.FragmentAdminManageDriverBinding;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import view_model.SharedDropdownDataViewModel;

public class AdminCreateNewDriverFragment extends Fragment {

    private FragmentAdminManageDriverBinding binding;
    private boolean isDriverDetailsCollapsed;
    private boolean isVehicleDetailsCollapsed;
    private ConstraintLayout rootContainer;
    private SharedDropdownDataViewModel sharedViewModel;
    private Spinner manageDriverVehicleTypeSpinner;
    private Spinner manageDriverVehicleBrandSpinner;
    private Spinner manageDriversVehicleModelSpinner;

    private Map<String, String> vehicleTypeMap;
    private Map<String, String> vehicleBrandMap;
    private Map<String, String> vehicleModelMap;

    private boolean initialSelection = true;
    private boolean initialModelSelection = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminManageDriverBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        rootContainer = binding.adminManageDriverConstraintLayout;

        manageDriverVehicleTypeSpinner = binding.manageDriverVehicleTypeSpinner;
        manageDriverVehicleBrandSpinner = binding.manageDriverVehicleBrandSpinner;
        manageDriversVehicleModelSpinner = binding.manageDriversVehicleModelSpinner;

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedDropdownDataViewModel.class);

        sharedViewModel.getVehicleTypeMap().observe(getViewLifecycleOwner(), vehicleTypeMap -> {
            if (vehicleTypeMap != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, new ArrayList<>(vehicleTypeMap.keySet()));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                manageDriverVehicleTypeSpinner.setAdapter(adapter);
            }
        });
        sharedViewModel.getVehicleBrandMap().observe(getViewLifecycleOwner(), vehicleBrandMap -> {
            if (vehicleBrandMap != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, new ArrayList<>(vehicleBrandMap.keySet()));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                manageDriverVehicleBrandSpinner.setAdapter(adapter);
            }
        });
        sharedViewModel.getVehicleModelMap().observe(getViewLifecycleOwner(), vehicleModelMap -> {
            if (vehicleModelMap != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, new ArrayList<>(vehicleModelMap.keySet()));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                manageDriversVehicleModelSpinner.setAdapter(adapter);
            }
        });

        ImageView manageDriverPersonalDetailsCollapsible = binding.manageDriverPersonalDetailsCollapsible;
        ConstraintLayout manageDriverPersonalDetailsConstraintLayout = binding.manageDriverPersonalDetailsConstraintLayout;
        ImageView manageDriverVehicleDetailsCollapsible = binding.manageDriverVehicleDetailsCollapsible;
        ConstraintLayout manageDriverVehicleDetailsConstraintLayout = binding.manageDriverVehicleDetailsConstraintLayout;

        Button manageDriverUpdateDriverButton = binding.manageDriverUpdateDriverButton;
        manageDriverUpdateDriverButton.setVisibility(GONE);

        Button manageDriversUpdateVehicleDetailsButton = binding.manageDriversUpdateVehicleDetailsButton;
        manageDriversUpdateVehicleDetailsButton.setVisibility(GONE);

        Button manageDriverBlockAccountButton = binding.manageDriverBlockAccountButton;
        manageDriverBlockAccountButton.setVisibility(GONE);

        Button manageDriverDeleteAccountButton = binding.manageDriverDeleteAccountButton;
        manageDriverDeleteAccountButton.setVisibility(GONE);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int initialHeight1 = manageDriverPersonalDetailsConstraintLayout.getHeight();
                manageDriverPersonalDetailsCollapsible.setOnClickListener(v->{
                    if(isDriverDetailsCollapsed) {
                        expand(manageDriverPersonalDetailsConstraintLayout, initialHeight1);
                        manageDriverPersonalDetailsCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(manageDriverPersonalDetailsConstraintLayout, initialHeight1);
                        manageDriverPersonalDetailsCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isDriverDetailsCollapsed = !isDriverDetailsCollapsed;
                });

                int initialHeight2 = manageDriverVehicleDetailsConstraintLayout.getHeight();
                manageDriverVehicleDetailsCollapsible.setOnClickListener(v->{
                    if(isVehicleDetailsCollapsed) {
                        expand(manageDriverVehicleDetailsConstraintLayout, initialHeight2);
                        manageDriverVehicleDetailsCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(manageDriverVehicleDetailsConstraintLayout, initialHeight2);
                        manageDriverVehicleDetailsCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isVehicleDetailsCollapsed = !isVehicleDetailsCollapsed;
                });
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        populateAndSetVehicleBrandListener();
        populateAndSetVehicleModelListener();

    }

    private void populateAndSetVehicleBrandListener() {

        if (manageDriverVehicleBrandSpinner.getAdapter() == null || manageDriverVehicleBrandSpinner.getAdapter().getCount() == 0) {
            sharedViewModel.getVehicleBrandMap().observe(getViewLifecycleOwner(), vehicleBrandMap -> {
                if (vehicleBrandMap != null) {
                    this.vehicleBrandMap = vehicleBrandMap;
                    List<String> spinnerList = new ArrayList<>(vehicleBrandMap.keySet());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, spinnerList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    manageDriverVehicleBrandSpinner.setAdapter(adapter);


                    setVehicleBrandSpinnerListener();
                }
            });
        } else {
            setVehicleBrandSpinnerListener();
        }
    }

    private void populateAndSetVehicleModelListener() {

        if (manageDriversVehicleModelSpinner.getAdapter() == null || manageDriversVehicleModelSpinner.getAdapter().getCount() == 0) {
            sharedViewModel.getVehicleModelMap().observe(getViewLifecycleOwner(), vehicleModelMap -> {
                if (vehicleModelMap != null) {
                    this.vehicleModelMap = vehicleModelMap;
                    List<String> spinnerList = new ArrayList<>(vehicleModelMap.keySet());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, spinnerList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    manageDriversVehicleModelSpinner.setAdapter(adapter);

                    setVehicleModelSpinnerListener();
                }
            });
        } else {
            setVehicleModelSpinnerListener();
        }
    }

    private void setVehicleBrandSpinnerListener() {
        manageDriverVehicleBrandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedVehicleBrandName = manageDriverVehicleBrandSpinner.getItemAtPosition(position).toString();
                String selectedVehicleBrandId = vehicleBrandMap.get(selectedVehicleBrandName);

                fetchVehicleModels(selectedVehicleBrandId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                manageDriversVehicleModelSpinner.setAdapter(null);
            }
        });
    }

    private void setVehicleModelSpinnerListener() {
        manageDriversVehicleModelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedVehicleModelName = manageDriversVehicleModelSpinner.getItemAtPosition(position).toString();
                String selectedVehicleModelId = vehicleModelMap.get(selectedVehicleModelName);

                fetchVehicleType(selectedVehicleModelId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                manageDriversVehicleModelSpinner.setAdapter(null);
            }
        });
    }

    private void fetchVehicleModels(String selectedVehicleBrandId) {
        new Thread(() -> {
            try {
                Gson gson = new Gson();
                OkHttpClient okHttpClient = new OkHttpClient();
                Routes routes = new Routes();
                String path = routes.getEnv_url();

                RequestBody requestBody = RequestBody.create(selectedVehicleBrandId, MediaType.parse("application/json"));
                Request request = new Request.Builder()
                        .url(path + "VehicleBrandSetVehicleModel")
                        .post(requestBody)
                        .build();

                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();
                Log.d("Raw JSON Response", responseBody);

                Response_DTO response_dto = gson.fromJson(responseBody, Response_DTO.class);
                Log.d("Response DTO", gson.toJson(response_dto));

                if (response_dto.isSuccess()) {
                    Type type = new TypeToken<List<Vehicle_Model>>() {
                    }.getType();
                    List<Vehicle_Model> vehicleModelList = gson.fromJson(gson.toJson(response_dto.getContent()), type);

                    vehicleModelMap = createVehicleModelMap(vehicleModelList);

                    getActivity().runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>(vehicleModelMap.keySet()));
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        manageDriversVehicleModelSpinner.setAdapter(adapter);
                        initialSelection = true;
                    });
                } else {
                    handleError("Error loading item brands: " + response_dto.getContent());
                }

            } catch (IOException e) {
                handleError("Network error loading item brands: " + e.getMessage());
            }
        }).start();
    }

    private void fetchVehicleType(String selectedVehicleModelId) {
        new Thread(() -> {
            try {
                Gson gson = new Gson();
                OkHttpClient okHttpClient = new OkHttpClient();
                Routes routes = new Routes();
                String path = routes.getEnv_url();

                RequestBody requestBody = RequestBody.create(selectedVehicleModelId, MediaType.parse("application/json")); // Send ID as string
                Request request = new Request.Builder()
                        .url(path + "VehicleModelSetVehicleType")
                        .post(requestBody)
                        .build();

                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();
                Log.d("Raw JSON Response", responseBody);

                Response_DTO response_dto = gson.fromJson(responseBody, Response_DTO.class);
                Log.d("Response DTO", gson.toJson(response_dto));

                if (response_dto.isSuccess()) {
                    Type type = new TypeToken<List<Vehicle_Type>>() {
                    }.getType();
                    List<Vehicle_Type> vehicleTypeList = gson.fromJson(gson.toJson(response_dto.getContent()), type);

                    vehicleTypeMap = createVehicleTypeMap(vehicleTypeList);

                    getActivity().runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>(vehicleTypeMap.keySet()));
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        manageDriverVehicleTypeSpinner.setAdapter(adapter);
                        initialModelSelection = true;
                    });
                } else {
                    handleError("Error loading item brands: " + response_dto.getContent());
                }

            } catch (IOException e) {
                handleError("Network error loading item brands: " + e.getMessage());
            }
        }).start();
    }

    private void handleError(String errorMessage) {
        Log.e("Error", errorMessage);
        if (isAdded()) {
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                if (manageDriversVehicleModelSpinner != null) {
                    manageDriversVehicleModelSpinner.setAdapter(null);
                }
                if (manageDriverVehicleTypeSpinner != null) {
                    manageDriverVehicleTypeSpinner.setAdapter(null);
                }
            });
        }
    }

    private Map<String, String> createVehicleModelMap(List<Vehicle_Model> vehicleModelList) {
        Map<String, String> map = new HashMap<>();
        if (vehicleModelList != null) {
            for (Vehicle_Model vehicleModel : vehicleModelList) {
                map.put(vehicleModel.getName(), String.valueOf(vehicleModel.getId()));
            }
        }
        return map;
    }

    private Map<String, String> createVehicleTypeMap(List<Vehicle_Type> vehicleTypeList) {
        Map<String, String> map = new HashMap<>();
        if (vehicleTypeList != null) {
            for (Vehicle_Type vehicleType : vehicleTypeList) {
                map.put(vehicleType.getName(), String.valueOf(vehicleType.getId()));
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