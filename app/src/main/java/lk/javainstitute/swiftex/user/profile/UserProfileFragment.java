package lk.javainstitute.swiftex.user.profile;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import backend.Routes;
import dto.City_DTO;
import dto.Response_DTO;
import dto.UserHomeDetailsDTO;
import dto.User_DTO;
import dto.Vehicle_Model;
import lk.javainstitute.swiftex.MainActivity;
import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.databinding.FragmentUserProfileBinding;
import lk.javainstitute.swiftex.user.activities.LogInActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import view_model.SharedDropdownDataViewModel;

public class UserProfileFragment extends Fragment {

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    private final int IMG_REQUEST_ID = 10;
    private Uri imgUri;

    private FragmentUserProfileBinding binding;
    private boolean isPersonalCollapsed = false;
    private boolean isAddressCollapsed = false;
    private ConstraintLayout rootContainer;
    private SharedDropdownDataViewModel sharedViewModel;
    private Spinner provinceSpinner;
    private Spinner citySpinner;
    private ImageView profileImageView;

    private Map<String, String> provinceMap;
    private Map<String, String> cityMap;

    private Button saveUserAddressDetailsButton;
    private Button updateUserAddressDetailsButton;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private String selectedCityId;

    private boolean initialSelection = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("UserProfileFragment", "onCreate() called");
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        profileImageView = binding.userProfileImageView;

        cityMap = new HashMap<>();

        saveUserAddressDetailsButton = binding.saveUserAddressDetailsButton;
        updateUserAddressDetailsButton = binding.updateUserAddressButton;

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        imgUri = data.getData();

                        try {
                            Bitmap bitmapImg = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imgUri);
                            profileImageView.setImageBitmap(bitmapImg);
                            saveInFirebase();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error loading image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        profileImageView.setOnClickListener(v -> requestImage());

        provinceSpinner = binding.provinceSpinner;
        citySpinner = binding.citySpinner;

        setCitySpinnerListener();

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedDropdownDataViewModel.class);

        sharedViewModel.getProvinceMap().observe(getViewLifecycleOwner(), provinceMap -> {
            if (provinceMap != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, new ArrayList<>(provinceMap.keySet()));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                provinceSpinner.setAdapter(adapter);
            }
        });
        sharedViewModel.getCityMap().observe(getViewLifecycleOwner(), cityMapData -> {
            if (cityMapData != null) {
                cityMap.clear();
                cityMap.putAll(cityMapData);
                List<String> cityNames = new ArrayList<>(cityMap.keySet());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, cityNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                citySpinner.setAdapter(adapter);
            } else {
                Log.w("CityMap", "City map data is null");
                Toast.makeText(requireContext(), "Error loading cities.", Toast.LENGTH_SHORT).show();
                citySpinner.setAdapter(null);
            }
        });

        rootContainer = binding.userProfileFragmentConstraintLayout;

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

            Button saveAddressButton = binding.saveUserAddressDetailsButton;
            saveAddressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeAddress();
                }
            });

            Button updateAddressButton = binding.updateUserAddressButton;
            updateAddressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeAddress();
                }
            });

            Button updatePersonalDetailsButton = binding.updateUserDetailsButton;
            updatePersonalDetailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateUser();
                }
            });


        Log.i("UserProfileFragment", "View Returned");
        return view;
    }

    private void updateUser(){
        SharedPreferences userDetailsSP = requireActivity().getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
        Integer userID = userDetailsSP.getInt("User ID", -1);

        EditText editTextFname = binding.userAccountPersonalDetailsFnameEditText;
        EditText editTextLname = binding.userAccountPersonalDetailsLnameEditText;
        EditText editTextMobile = binding.userAccountPersonalDetailsMobileEditText;
        EditText editTextNic = binding.userAccountPersonalDetailsNICEditText;
        EditText editTextEmail = binding.userAccountPersonalDetailsEmailEditText;

        String userFirstName = editTextFname.getText().toString();
        String userLastName = editTextLname.getText().toString();

        String userMobile = editTextMobile.getText().toString();
        String userNIC = editTextNic.getText().toString();
        String userEmail = editTextEmail.getText().toString();

        if (userFirstName.isEmpty()){
            Toast.makeText(getContext(), "Please Enter First Name", Toast.LENGTH_LONG).show();
        } else if (userLastName.isEmpty()){
            Toast.makeText(getContext(), "Please Enter Last Name", Toast.LENGTH_LONG).show();
        } else if (userMobile.isEmpty()){
            Toast.makeText(getContext(), "Please Enter Mobile", Toast.LENGTH_LONG).show();
        } else if (userNIC.isEmpty()){
            Toast.makeText(getContext(), "Please Enter NIC", Toast.LENGTH_LONG).show();
        } else if(userEmail.isEmpty()){
            Toast.makeText(getContext(), "Please Enter Email", Toast.LENGTH_LONG).show();
        } else {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    User_DTO user_dto = new User_DTO();
                    user_dto.setId(userID);
                    user_dto.setFname(userFirstName);
                    user_dto.setLname(userLastName);
                    user_dto.setMobile(userMobile);
                    user_dto.setNic(userNIC);
                    user_dto.setEmail(userEmail);
                    requireActivity().runOnUiThread(new Runnable() {
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
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "Details updated successfully!", Toast.LENGTH_LONG).show();
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

    private void changeAddress(){
        SharedPreferences userDetailsSP = requireActivity().getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
        Integer userId = userDetailsSP.getInt("User ID", -1);

        EditText editTextAddressNo = binding.editTextAddressNo;
        EditText editTextAddressStreet1 = binding.editTextAddressStreet1;
        EditText editTextAddressStreet2 = binding.editTextAddressStreet2;

        String addressNo = editTextAddressNo.getText().toString();
        String street1 = editTextAddressStreet1.getText().toString();
        String street2 = editTextAddressStreet2.getText().toString();

        if (addressNo.isEmpty()){
            Toast.makeText(getContext(), "Please Enter Address No", Toast.LENGTH_LONG).show();
        } else if (street1.isEmpty()){
            Toast.makeText(getContext(), "Please Enter Adress Street 1", Toast.LENGTH_LONG).show();
        } else if (selectedCityId == null) {
            Toast.makeText(getContext(), "Please select a city", Toast.LENGTH_LONG).show();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();

                    UserHomeDetailsDTO userHomeDetailsDTO = new UserHomeDetailsDTO();
                    userHomeDetailsDTO.setUserId(userId);
                    userHomeDetailsDTO.setNo(addressNo);
                    userHomeDetailsDTO.setStreet1(street1);
                    userHomeDetailsDTO.setStreet2(street2);
                    userHomeDetailsDTO.setCity(selectedCityId);
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("GsonLog", "Data Loaded");
                        }
                    });

                    OkHttpClient okHttpClient = new OkHttpClient();

                    Routes routes = new Routes();
                    String path = routes.getEnv_url();

                    RequestBody requestBody = RequestBody.create(gson.toJson(userHomeDetailsDTO), MediaType.get("application/json"));
                    Request request = new Request.Builder()
                            .url(path+"UserCreateAddress")
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
                                    Toast.makeText(getContext(), "Address Saved Successfully", Toast.LENGTH_LONG).show();
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

    private void loadImageFromSharedPreferences() {
        SharedPreferences userImgSp = requireActivity().getSharedPreferences("lk.javainstitute,SwiftEx.data", Context.MODE_PRIVATE);
        String imagePath = userImgSp.getString("User Image", null);

        if (imagePath != null && !imagePath.isEmpty()) {
            // Load from Firebase Storage if path exists
            loadImageFromFirebase(imagePath);
        } else {
            // Set a default image if no path in SharedPreferences
            profileImageView.setImageResource(R.drawable.ic_menu_profile);
        }
    }

    private void loadImageFromFirebase(String imagePath) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReference().child(imagePath);

        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String downloadUrl = uri.toString();
            Glide.with(requireContext())
                    .load(downloadUrl)
                    .placeholder(R.drawable.ic_menu_profile)
                    .error(R.drawable.ic_no_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(profileImageView);
        }).addOnFailureListener(exception -> {
            Log.e("Firebase Storage", "Error getting download URL: " + exception.getMessage());
            Toast.makeText(requireContext(), "Error loading image.", Toast.LENGTH_SHORT).show();
            profileImageView.setImageResource(R.drawable.ic_menu_profile);
        });
    }

    private void saveInFirebase() {
        if (imgUri != null) {
            String imgPath = "picture/" + UUID.randomUUID().toString();
            SharedPreferences userImgSp = requireActivity().getSharedPreferences("lk.javainstitute,SwiftEx.data", Context.MODE_PRIVATE);

            @SuppressLint("CommitPrefEdits")
            SharedPreferences.Editor editor = userImgSp.edit();

            editor.putString("User Image", imgPath);
            editor.apply();

            StorageReference reference = storageReference.child(imgPath);
            reference.putFile(imgUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(requireContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            Log.d("Download URL", downloadUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Failed to upload Image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    })
                    .addOnProgressListener(snapshot -> {
                        double progressPercent = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        Toast.makeText(requireContext(), "Uploading Image... " + (int) progressPercent + "%", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void requestImage() {
        Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageIntent.setType("image/*");
        imagePickerLauncher.launch(Intent.createChooser(imageIntent, "Select Image"));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("UserProfileFragment", "onAttach() called");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d("UserProfileFragment", "onViewCreated() called");
        super.onViewCreated(view, savedInstanceState);

        Button saveUserDetailsButton = binding.saveUserDetailsButton;
        saveUserDetailsButton.setVisibility(GONE);


        Button blockUserButton = binding.blockUserButton;
        Button deleteUserButton = binding.deleteUserButton;

        blockUserButton.setVisibility(GONE);
        deleteUserButton.setVisibility(GONE);

        UserProfileViewModel viewModel = new ViewModelProvider(requireActivity()).get(UserProfileViewModel.class);

        viewModel.getUserProfileData().observe(getViewLifecycleOwner(), new Observer<UserHomeDetailsDTO>() {
            @Override
            public void onChanged(UserHomeDetailsDTO userHomeDetails) {
                if (userHomeDetails != null && binding != null) {
                    Log.i("UserProfileFragment", "Binding NOT null");

                    binding.userAccountPersonalDetailsFnameEditText.setText(userHomeDetails.getFname());
                    binding.userAccountPersonalDetailsLnameEditText.setText(userHomeDetails.getLname());
                    binding.userAccountPersonalDetailsMobileEditText.setText(userHomeDetails.getMobile());
                    binding.userAccountPersonalDetailsNICEditText.setText(userHomeDetails.getNic());
                    binding.userAccountPersonalDetailsEmailEditText.setText(userHomeDetails.getEmail());
                    binding.userProfileJoinedDateTimeValueTextView.setText(userHomeDetails.getJoined_datetime());
                    binding.editTextAddressNo.setText(userHomeDetails.getNo());
                    binding.editTextAddressStreet1.setText(userHomeDetails.getStreet1());
                    binding.editTextAddressStreet2.setText(userHomeDetails.getStreet2());

                    // *** SET CITY ***
                    if (cityMap != null) {
                        String cityName = getKeyFromValue(cityMap, userHomeDetails.getCity());
                        if (cityName != null) {
                            int position = getPositionInAdapter(citySpinner, cityName);
                            if (position != -1) {
                                citySpinner.setSelection(position);
                            } else {
                                Log.w("City", "City name not found in map: " + cityName);
                            }
                        } else {
                            Log.w("City", "City ID not found in map: " + userHomeDetails.getCity());
                        }
                    }
                    boolean hasAddress = !userHomeDetails.getNo().isEmpty() &&
                            !userHomeDetails.getStreet1().isEmpty() &&
                            !userHomeDetails.getStreet2().isEmpty() &&
                            userHomeDetails.getCity() != null; // Check city as well

                    if (hasAddress) {
                        saveUserAddressDetailsButton.setVisibility(GONE);
                        updateUserAddressDetailsButton.setVisibility(View.VISIBLE);
                    } else {
                        saveUserAddressDetailsButton.setVisibility(View.VISIBLE);
                        updateUserAddressDetailsButton.setVisibility(GONE);
                    }
                } else if (binding != null) {
                    Log.i("UserProfileFragment", "No details");
                    binding.userAccountPersonalDetailsFnameEditText.setText("");
                    binding.userAccountPersonalDetailsLnameEditText.setText("");
                    binding.userAccountPersonalDetailsMobileEditText.setText("");
                    binding.userAccountPersonalDetailsNICEditText.setText("");
                    binding.userAccountPersonalDetailsEmailEditText.setText("");
                    binding.userProfileJoinedDateTimeValueTextView.setText("");
                    binding.editTextAddressNo.setText("");
                    binding.editTextAddressStreet1.setText("");
                    binding.editTextAddressStreet2.setText("");

                    saveUserAddressDetailsButton.setVisibility(View.VISIBLE);
                    updateUserAddressDetailsButton.setVisibility(GONE);
                }
            }
        });

        populateAndSetProvinceListener();

        loadImageFromSharedPreferences();

        Log.i("UserProfileFragment", "View has been created");
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

        if (provinceSpinner.getAdapter() == null || provinceSpinner.getAdapter().getCount() == 0) {
            sharedViewModel.getProvinceMap().observe(getViewLifecycleOwner(), provinceMap -> {
                if (provinceMap != null) {
                    this.provinceMap = provinceMap;
                    List<String> spinnerList = new ArrayList<>(provinceMap.keySet());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, spinnerList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    provinceSpinner.setAdapter(adapter);

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
                if (cityMap != null) {
                    String selectedCityName = citySpinner.getItemAtPosition(position).toString();
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
                        citySpinner.setAdapter(adapter);
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
        Log.d("UserProfileFragment", "onDestroyView() called");
        super.onDestroyView();
        binding = null;
    }
}