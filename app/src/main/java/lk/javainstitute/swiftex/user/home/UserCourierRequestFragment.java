package lk.javainstitute.swiftex.user.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
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
import lk.javainstitute.swiftex.databinding.FragmentUserCourierRequestBinding;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import view_model.SharedDropdownDataViewModel;

public class UserCourierRequestFragment extends Fragment {

    private FragmentUserCourierRequestBinding binding;
    private ConstraintLayout rootContainer;
    private SharedDropdownDataViewModel sharedViewModel;
    private Spinner userCourierRequestVehicleTypeSpinner;
    private Spinner userItemCategorySpinner;
    private Spinner userItemBrandSpinner;
    private Spinner userItemModelSpinner;

    private Map<String, String> categoryMap;
    private Map<String, String> itemBrandMap;
    private Map<String, String> itemModelMap;

    private boolean initialSelection = true;
    private boolean initialModelSelection = true;

    private FusedLocationProviderClient fusedLocationClient;

    private GoogleMap mMap;

    private static final int MAP_REQUEST_CODE = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserCourierRequestBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        userCourierRequestVehicleTypeSpinner = binding.userCourierRequestVehicleTypeSpinner;
        userItemCategorySpinner = binding.userItemCategorySpinner;
        userItemBrandSpinner = binding.userItemBrandSpinner;
        userItemModelSpinner = binding.userItemModelSpinner;

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedDropdownDataViewModel.class);

        sharedViewModel.getVehicleTypeMap().observe(getViewLifecycleOwner(), vehicleTypeMap -> {
            if (vehicleTypeMap != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, new ArrayList<>(vehicleTypeMap.keySet()));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                userCourierRequestVehicleTypeSpinner.setAdapter(adapter);
            }
        });
        sharedViewModel.getCategoryMap().observe(getViewLifecycleOwner(), categoryMap -> {
            if (categoryMap != null) {
                this.categoryMap = categoryMap;
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, new ArrayList<>(categoryMap.keySet()));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                userItemCategorySpinner.setAdapter(adapter);
            }
        });

        sharedViewModel.getItemBrandMap().observe(getViewLifecycleOwner(), itemBrandMap -> {
            if (itemBrandMap != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, new ArrayList<>(itemBrandMap.keySet()));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                userItemBrandSpinner.setAdapter(adapter);
            }
        });
        sharedViewModel.getItemModelMap().observe(getViewLifecycleOwner(), itemModelMap -> {
            if (itemModelMap != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, new ArrayList<>(itemModelMap.keySet()));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                userItemModelSpinner.setAdapter(adapter);
            }
        });
        rootContainer = binding.userCourierRequestConstraintLayout;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        populateAndSetCategoryListener();
        populateAndSetItemBrandListener();

        EditText startLocation = binding.userStartLocationEditText;
        startLocation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent mapsIntent = new Intent(getContext(), MapsActivity.class);
                startActivityForResult(mapsIntent, MAP_REQUEST_CODE);
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MAP_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String address = data.getStringExtra("address");

                if (binding != null) {
                    binding.userStartLocationEditText.setText(address);
                } else {
                    Log.w("Fragment", "Binding is null. Cannot update EditText.");
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(requireContext(), "Map selection cancelled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void populateAndSetCategoryListener() {
        if (userItemCategorySpinner.getAdapter() == null || userItemCategorySpinner.getAdapter().getCount() == 0) {
            sharedViewModel.getCategoryMap().observe(getViewLifecycleOwner(), categoryMap -> {
                if (categoryMap != null) {
                    this.categoryMap = categoryMap;
                    List<String> spinnerList = new ArrayList<>(categoryMap.keySet());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, spinnerList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    userItemCategorySpinner.setAdapter(adapter);
                    setCategorySpinnerListener();
                }
            });
        } else {
            setCategorySpinnerListener();
        }
    }

    private void populateAndSetItemBrandListener() {
        if (userItemBrandSpinner.getAdapter() == null || userItemBrandSpinner.getAdapter().getCount() == 0) {
            sharedViewModel.getItemBrandMap().observe(getViewLifecycleOwner(), itemBrandMap -> {
                if (itemBrandMap != null) {
                    this.itemBrandMap = itemBrandMap;
                    List<String> spinnerList = new ArrayList<>(itemBrandMap.keySet());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, spinnerList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    userItemBrandSpinner.setAdapter(adapter);
                    setItemBrandSpinnerListener();
                }
            });
        } else {
            setItemBrandSpinnerListener();
        }
    }

    private void setCategorySpinnerListener() {
        userItemCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategoryName = userItemCategorySpinner.getItemAtPosition(position).toString();
                String selectedCategoryId = categoryMap.get(selectedCategoryName);
                fetchItemBrands(selectedCategoryId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                userItemBrandSpinner.setAdapter(null);
            }
        });
    }

    private void setItemBrandSpinnerListener() {
        userItemBrandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemBrandName = userItemBrandSpinner.getItemAtPosition(position).toString();
                String selectedItemBrandId = itemBrandMap.get(selectedItemBrandName);
                fetchItemModels(selectedItemBrandId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                userItemBrandSpinner.setAdapter(null);
            }
        });
    }

    private void fetchItemBrands(String selectedCategoryId) {
        new Thread(() -> {
            try {
                Gson gson = new Gson();
                OkHttpClient okHttpClient = new OkHttpClient();
                Routes routes = new Routes();
                String path = routes.getEnv_url();

                RequestBody requestBody = RequestBody.create(selectedCategoryId, MediaType.parse("application/json"));
                Request request = new Request.Builder()
                        .url(path + "CategorySetsBrand")
                        .post(requestBody)
                        .build();

                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();

                Response_DTO response_dto = gson.fromJson(responseBody, Response_DTO.class);

                if (response_dto.isSuccess()) {
                    Type type = new TypeToken<List<Item_Brand>>() {
                    }.getType();
                    List<Item_Brand> itemBrandList = gson.fromJson(gson.toJson(response_dto.getContent()), type);

                    itemBrandMap = createItemBrandMap(itemBrandList);

                    getActivity().runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>(itemBrandMap.keySet()));
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        userItemBrandSpinner.setAdapter(adapter);
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

    private void fetchItemModels(String selectedItemBrandId) {
        new Thread(() -> {
            try {
                Gson gson = new Gson();
                OkHttpClient okHttpClient = new OkHttpClient();
                Routes routes = new Routes();
                String path = routes.getEnv_url();

                RequestBody requestBody = RequestBody.create(selectedItemBrandId, MediaType.parse("application/json"));
                Request request = new Request.Builder()
                        .url(path + "ItemBrandSetsItemModel")
                        .post(requestBody)
                        .build();

                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();

                Response_DTO response_dto = gson.fromJson(responseBody, Response_DTO.class);

                if (response_dto.isSuccess()) {
                    Type type = new TypeToken<List<Item_Model>>() {
                    }.getType();
                    List<Item_Model> itemModelList = gson.fromJson(gson.toJson(response_dto.getContent()), type);

                    itemModelMap = createItemModelMap(itemModelList);

                    getActivity().runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>(itemModelMap.keySet()));
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        userItemModelSpinner.setAdapter(adapter);
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
                if (userItemBrandSpinner != null) {
                    userItemBrandSpinner.setAdapter(null);
                }
                if (userItemModelSpinner != null) {
                    userItemModelSpinner.setAdapter(null);
                }
            });
        }
    }

    private Map<String, String> createItemBrandMap(List<Item_Brand> itemBrandList) {
        Map<String, String> map = new HashMap<>();
        if (itemBrandList != null) {
            for (Item_Brand itemBrand : itemBrandList) {
                map.put(itemBrand.getName(), String.valueOf(itemBrand.getId()));
            }
        }
        return map;
    }

    private Map<String, String> createItemModelMap(List<Item_Model> itemModelList) {
        Map<String, String> map = new HashMap<>();
        if (itemModelList != null) {
            for (Item_Model itemModel : itemModelList) {
                map.put(itemModel.getName(), String.valueOf(itemModel.getId()));
            }
        }
        return map;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}