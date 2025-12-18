package lk.javainstitute.swiftex.user.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backend.Routes;
import dto.Response_DTO;
import dto.UserHomeDetailsDTO;
import dto.User_DTO;
import lk.javainstitute.swiftex.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import lk.javainstitute.swiftex.user.profile.UserProfileViewModel;
import view_model.SharedDropdownDataViewModel;

public class UserHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private NavigationView navigationView;
    private Toolbar userToolbar;
    private DrawerLayout drawer;
    private String userFname;
    private String userLname;
    private String userEmail;

    private UserProfileViewModel viewModel;
    private SharedDropdownDataViewModel sharedViewModel;

    private Map<String, String> provinceMap;
    private Map<String, String> cityMap;
    private Map<String, String> categoryMap;
    private Map<String, String> itemBrandMap;
    private Map<String, String> itemModelMap;
    private Map<String, String> vehicleTypeMap;
    private Map<String, String> vehicleBrandMap;
    private Map<String, String> vehicleModelMap;
    private Map<String, String> industryMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        userToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(userToolbar);

        drawer = findViewById(R.id.user_drawer_layout1);

        navigationView = findViewById(R.id.user_navigation_view1);

        viewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);
        sharedViewModel = new ViewModelProvider(this).get(SharedDropdownDataViewModel.class);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        if (navHostFragment != null) {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_history, R.id.nav_analytics, R.id.nav_company, R.id.nav_user_profile)
                    .setOpenableLayout(drawer)
                    .build();
            navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
            navigationView.setNavigationItemSelectedListener(this);
        } else {
            Log.e("SwiftExLog", "ERROR : NavHostFragment was NOT found.");
        }

        SharedPreferences userEmailSP = getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
        userEmail = userEmailSP.getString("Email", "");

        if (userEmail.isEmpty()) {
            Intent i = new Intent(UserHomeActivity.this, LogInActivity.class);
            startActivity(i);
            finish();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    User_DTO user_dto = new User_DTO();
                    user_dto.setEmail(userEmail);

                    OkHttpClient okHttpClient = new OkHttpClient();
                    Routes routes = new Routes();
                    String path = routes.getEnv_url();

                    RequestBody requestBody = RequestBody.create(gson.toJson(user_dto), MediaType.get("application/json"));
                    Request request = new Request.Builder()
                            .url(path + "UserGetHomeDetails")
                            .post(requestBody)
                            .build();

                    try {
                        Response response = okHttpClient.newCall(request).execute();
                        String responseText = response.body().string();
                        Response_DTO response_dto = gson.fromJson(responseText, Response_DTO.class);

                        if (response_dto.isSuccess()) {
                            SharedPreferences userDetailsSP = getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = userDetailsSP.edit();
                            UserHomeDetailsDTO userHomeDetails = gson.fromJson(gson.toJson(response_dto.getContent()), UserHomeDetailsDTO.class);

                            if (userHomeDetails != null) { // Check for null!
                                userDetailsSP.edit().clear().apply();

                                editor.putInt("User ID", Integer.parseInt(userHomeDetails.getId()));
                                editor.putString("User First Name", userHomeDetails.getFname());
                                editor.putString("User Last Name", userHomeDetails.getLname());
                                editor.putString("User Mobile", userHomeDetails.getMobile());
                                editor.putString("User NIC", userHomeDetails.getNic());
                                editor.putString("Email", userHomeDetails.getEmail());
                                editor.putString("User Joined Datetime", userHomeDetails.getJoined_datetime());
                                editor.putString("User AddressNo", userHomeDetails.getNo());
                                editor.putString("User AddressStreet1", userHomeDetails.getStreet1());
                                editor.putString("User AddressStreet2", userHomeDetails.getStreet2());
                                editor.putString("User City", userHomeDetails.getCity());
                                editor.putString("User Paid Status", userHomeDetails.getIs_user_paid());
                                editor.putInt("User Company", Integer.parseInt(userHomeDetails.getCompany_id()));
                                editor.putString("User isCompanyAdmin", userHomeDetails.getIs_company_admin());
                                editor.apply();

                                userFname = userHomeDetails.getFname();
                                userLname = userHomeDetails.getLname();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        viewModel.setUserProfileData(userHomeDetails); // This will trigger the observer in the fragment
                                    }
                                });

                            } else {
                                Log.e("Deserialization Error", "Failed to deserialize JSON");
                                Toast.makeText(UserHomeActivity.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
                                // Handle the error appropriately, perhaps show a message to the user.
                            }

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(UserHomeActivity.this, response_dto.getContent().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } catch (IOException e) {
                        Log.i("SwiftExLog", "System Error");
                    }

                }
            }).start();

            SharedPreferences userDetailsSP = getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
            userDetailsSP.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        }

        new Thread(() -> {
            try {
                Gson gson = new Gson();
                OkHttpClient client = new OkHttpClient();
                Routes routes = new Routes();
                String path = routes.getEnv_url();

                Request request = new Request.Builder()
                        .url(path + "LoadData")
                        .get()
                        .build();

                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                Log.d("Raw JSON Response", responseBody);

                Response_DTO response_dto = gson.fromJson(responseBody, Response_DTO.class);
                Log.d("Response DTO", gson.toJson(response_dto));

                if (response_dto.isSuccess()) {
                    Log.d("API Success", "API call was successful");

                    Type type = new TypeToken<Map<String, JsonElement>>() {
                    }.getType();

                    Map<String, JsonElement> data = gson.fromJson(gson.toJson(response_dto.getContent()), type);
                    Log.d("Data Map", gson.toJson(data));

                    if (data != null) {
                        Log.d("Data Not Null", "Data map is not null");

                        provinceMap = createStringMap(data.get("provinceList"), "provinceList");
                        cityMap = createStringMap(data.get("cityList"), "cityList");
                        vehicleTypeMap = createStringMap(data.get("vehicleTypeList"), "vehicleTypeList");
                        vehicleBrandMap = createStringMap(data.get("vehicleBrandList"), "vehicleBrandList");
                        vehicleModelMap = createStringMap(data.get("vehicleModelList"), "vehicleModelList");
                        categoryMap = createStringMap(data.get("categoryList"), "categoryList");
                        itemBrandMap = createStringMap(data.get("itemBrandList"), "itemBrandList");
                        itemModelMap = createStringMap(data.get("itemModelList"), "itemModelList");
                        industryMap = createStringMap(data.get("industryList"), "industryList");

                        Log.d("Province Map (After Creation)", provinceMap.toString());
                        Log.d("City Map (After Creation)", cityMap.toString());
                        Log.d("Vehicle Type Map (After Creation)", vehicleTypeMap.toString());
                        Log.d("Vehicle Brand Map (After Creation)", vehicleBrandMap.toString());
                        Log.d("Vehicle Model Map (After Creation)", vehicleModelMap.toString());
                        Log.d("Category Map (After Creation)", categoryMap.toString());
                        Log.d("Item Brand Map (After Creation)", itemBrandMap.toString());
                        Log.d("Item Model Map (After Creation)", itemModelMap.toString());
                        Log.d("Industry Map (After Creation)", industryMap.toString());

                        runOnUiThread(() -> {
                            Log.d("UI Thread", "Running on UI thread");

                            Log.d("Dropdown Data", "Province Map: " + provinceMap);
                            Log.d("Dropdown Data", "City Map: " + cityMap);
                            Log.d("Dropdown Data", "Vehicle Type Map: " + vehicleTypeMap);
                            Log.d("Dropdown Data", "Vehicle Brand Map: " + vehicleBrandMap);
                            Log.d("Dropdown Data", "Vehicle Model Map: " + vehicleModelMap);
                            Log.d("Dropdown Data", "Category Map: " + categoryMap);
                            Log.d("Dropdown Data", "Item Brand Map: " + itemBrandMap);
                            Log.d("Dropdown Data", "Item Model Map: " + itemModelMap);
                            Log.d("Dropdown Data", "Industry Map" + industryMap);

                        });

                        runOnUiThread(() -> {
                            sharedViewModel.setProvinceMap(provinceMap);
                            sharedViewModel.setCityMap(cityMap);
                            sharedViewModel.setVehicleTypeMap(vehicleTypeMap);
                            sharedViewModel.setVehicleBrandMap(vehicleBrandMap);
                            sharedViewModel.setVehicleModelMap(vehicleModelMap);
                            sharedViewModel.setCategoryMap(categoryMap);
                            sharedViewModel.setItemBrandMap(itemBrandMap);
                            sharedViewModel.setItemModelMap(itemModelMap);
                            sharedViewModel.setIndustryMap(industryMap);
                        });
                    } else {
                        Log.e("JSON Parsing Error", "Failed to parse JSON into Map<String, JsonElement>");
                        runOnUiThread(() -> {
                            Toast.makeText(UserHomeActivity.this, "Error loading dropdown data.", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    Log.e("API Error", "Error fetching dropdown data: " + response_dto.getContent());
                    runOnUiThread(() -> {
                        Toast.makeText(UserHomeActivity.this, "Error loading dropdown data.", Toast.LENGTH_SHORT).show();
                    });
                }

            } catch (IOException e) {
                Log.e("Network Error", "Error fetching dropdown data: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(UserHomeActivity.this, "Network error loading dropdown data.", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private Map<String, String> createStringMap(JsonElement listElement, String listName) {
        Map<String, String> map = new HashMap<>();

        if (listElement != null && listElement.isJsonArray()) {
            JsonArray jsonArray = listElement.getAsJsonArray();
            for (JsonElement item : jsonArray) {
                if (item != null && item.isJsonObject()) { // Check if item is not null
                    JsonObject jsonObject = item.getAsJsonObject();

                    String name = getJsonElementValueAsString(jsonObject.get("name")); // Check for null name
                    if (name == null || name.isEmpty()) continue; // Skip if name is missing


                    String value = "";

                    switch (listName) {
                        case "provinceList":
                            value = getJsonElementValueAsInt(jsonObject.get("id"));
                            break;
                        case "cityList":
                            if (jsonObject.has("province") && jsonObject.get("province").isJsonObject()) {
                                value = getJsonElementValueAsInt(jsonObject.getAsJsonObject("province").get("id"));
                            }
                            break;
                        case "vehicleTypeList":
                            value = getJsonElementValueAsInt(jsonObject.get("id"));
                            break;
                        case "vehicleBrandList":
                            value = getJsonElementValueAsInt(jsonObject.get("id"));
                            break;
                        case "vehicleModelList":
                            if (jsonObject.has("vehicle_type") && jsonObject.get("vehicle_type").isJsonObject()) {
                                value = getJsonElementValueAsInt(jsonObject.getAsJsonObject("vehicle_type").get("id"));
                            } else {
                                value = getJsonElementValueAsInt(jsonObject.get("id"));
                            }
                            break;
                        case "categoryList":
                            value = getJsonElementValueAsInt(jsonObject.get("id"));
                            break;
                        case "itemBrandList":
                            if(jsonObject.has("category") && jsonObject.get("category").isJsonObject()) {
                                value = getJsonElementValueAsInt(jsonObject.getAsJsonObject("category").get("id"));
                            } else {
                                value = getJsonElementValueAsInt(jsonObject.get("id"));
                            }
                            break;
                        case "itemModelList":
                            if(jsonObject.has("item_brand") && jsonObject.get("item_brand").isJsonObject()) {
                                value = getJsonElementValueAsInt(jsonObject.getAsJsonObject("item_brand").get("id"));
                            } else {
                                value = getJsonElementValueAsInt(jsonObject.get("id"));
                            }
                            break;
                        case "industryList":
                            value = getJsonElementValueAsInt(jsonObject.get("id"));
                            break;
                    }
                    if (!value.isEmpty()) // Only add to the map if value is not empty
                        map.put(name, value);
                }
            }
        }
        return map;
    }

    // Helper function (same as before)
    private String getJsonElementValueAsString(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            return element.getAsString();
        }
        return ""; // Return empty string if element is null or not a primitive
    }

    // New helper function to get integer value
    private String getJsonElementValueAsInt(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            try {
                return String.valueOf(element.getAsInt()); // Try to get as integer first
            } catch (NumberFormatException e) {
                try {
                    return String.valueOf((int) element.getAsDouble()); // Cast to int if it's a double
                } catch (NumberFormatException ex) {
                    return ""; // Return empty string if not a number
                }
            }
        }
        return "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_home_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_log_out) {
            SharedPreferences userDetailsSP = getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = userDetailsSP.edit();
            editor.clear();
            editor.apply();

            userDetailsSP.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);

            Intent i = new Intent(UserHomeActivity.this, LogInActivity.class);
            startActivity(i);
            finish();

            return true;
        }

        if (id == R.id.contact_support) {
            Intent i = new Intent(Intent.ACTION_DIAL);
            i.setData(Uri.parse("tel:0770243444"));
            startActivity(i);

            Log.d("OptionsMenu", "Contact Item Clicked");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // Handle preference changes
        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_home) {
            Log.i("SwiftExLog", "Clicked Home");
            navController.navigate(R.id.nav_home);
        } else if (id == R.id.menu_item_history) {
            Log.i("SwiftExLog", "Clicked History");
            navController.navigate(R.id.nav_history);
        } else if (id == R.id.menu_item_analytics) {
            Log.i("SwiftExLog", "Clicked Analytics");
            navController.navigate(R.id.nav_analytics);
        } else if (id == R.id.menu_item_company) {
            Log.i("SwiftExLog", "Clicked Company");
            navController.navigate(R.id.nav_company);
        } else if (id == R.id.menu_item_profile) {
            Log.i("SwiftExLog", "Clicked Profile");
            navController.navigate(R.id.nav_user_profile);
        }

        navigationView.post(new Runnable() {
            @Override
            public void run() {
                View headerView = navigationView.getHeaderView(0);
                if (headerView != null) {
                    TextView userFnameLnameTextView = headerView.findViewById(R.id.userFnameLnameTextView);
                    TextView userEmailTextView = headerView.findViewById(R.id.userEmailTextView);

                    if (userFnameLnameTextView != null && userEmailTextView != null) {
                        String name = userFname + " " + userLname;
                        userFnameLnameTextView.setText(name);
                        userEmailTextView.setText(userEmail);
                    } else {
                        Log.e("HeaderError", "TextViews not found in header");
                    }
                } else {
                    Log.e("HeaderError", "Header view is null");
                }
            }
        });

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences userDetailsSP = getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
        userDetailsSP.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener); // Unregister here!

    }
}