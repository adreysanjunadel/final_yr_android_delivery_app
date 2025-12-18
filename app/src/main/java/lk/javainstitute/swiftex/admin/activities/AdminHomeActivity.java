package lk.javainstitute.swiftex.admin.activities;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
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
import java.util.Map;

import backend.Routes;
import dto.Admin_DTO;
import dto.Response_DTO;
import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.admin.profile.AdminProfileViewModel;
import lk.javainstitute.swiftex.user.activities.LogInActivity;
import lk.javainstitute.swiftex.user.activities.UserHomeActivity;
import lk.javainstitute.swiftex.user.profile.UserProfileViewModel;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import view_model.SharedDropdownDataViewModel;

public class AdminHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private NavigationView navigationView;
    private Toolbar adminToolbar;
    private DrawerLayout drawer;
    private String adminFname;
    private String adminLname;
    private String adminEmail;

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
        Log.i("SwiftExLog", "AdminHomeActivity onCreate() started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        Log.i("SwiftExLog", "AdminHomeActivity setContentView() complete");

        adminToolbar = findViewById(R.id.adminToolbar);
        setSupportActionBar(adminToolbar);
        Log.i("SwiftExLog", "Toolbar setup");

        drawer = findViewById(R.id.admin_drawer_layout1);
        Log.i("SwiftExLog", "DrawerLayout was set up");

        navigationView = findViewById(R.id.admin_navigation_view1);
        Log.i("SwiftExLog", "Navigation View was set up");

        AdminProfileViewModel viewModel = new ViewModelProvider(this).get(AdminProfileViewModel.class);

        sharedViewModel = new ViewModelProvider(this).get(SharedDropdownDataViewModel.class);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_admin_fragment_content_main);
        if (navHostFragment != null) {
            Log.i("SwiftExLog", "NavHostFragment was found.");

            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_admin_home, R.id.nav_admin_account_management, R.id.nav_admin_driver_management, R.id.nav_admin_security, R.id.nav_admin_profile)
                    .setOpenableLayout(drawer)
                    .build();
            Log.i("SwiftExLog", "AppBarConfiguration was set up");
            navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
            Log.i("SwiftExLog", "Navigation UI(actionbar and navigation view) are now set up");
            navigationView.setNavigationItemSelectedListener(this);
            Log.i("SwiftExLog", "Default Menu item is set.");

        } else {
            Log.e("SwiftExLog", "ERROR : NavHostFragment was NOT found.");
        }

        SharedPreferences adminEmailSP = getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
        adminEmail = adminEmailSP.getString("Admin Email", "");

        if (adminEmail.isEmpty()) {
            Intent i = new Intent(AdminHomeActivity.this, AdminLoginActivity.class);
            startActivity(i);
            finish();
        } else {
            fetchAdminData(viewModel, new DataFetchCallback() {
                @Override
                public void onDataFetched(Admin_DTO adminDetails) {
                    if (adminDetails != null) {
                        adminFname = adminDetails.getFname();
                        adminLname = adminDetails.getLname();
                        adminEmail = adminDetails.getEmail();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewModel.setAdminProfileData(adminDetails);

                                navigationView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        View headerView = navigationView.getHeaderView(0);
                                        if (headerView != null) {
                                            TextView adminFnameLnameTextView = headerView.findViewById(R.id.adminFnameLnameTextView);
                                            TextView adminEmailTextView = headerView.findViewById(R.id.adminEmailTextView);

                                            if (adminFnameLnameTextView != null && adminEmailTextView != null && adminFname != null && adminLname != null && adminEmail != null) {
                                                String name = adminFname + " " + adminLname;
                                                adminFnameLnameTextView.setText(name);
                                                adminEmailTextView.setText(adminEmail);
                                            } else {
                                                Log.e("HeaderError", "TextViews or data not found in header");
                                                if (adminFnameLnameTextView == null || adminEmailTextView == null) {
                                                    Log.e("HeaderError", "TextViews are null");
                                                }
                                                if (adminFname == null || adminLname == null || adminEmail == null) {
                                                    Log.e("HeaderError", "Admin data is null");
                                                }
                                            }
                                        } else {
                                            Log.e("HeaderError", "Header view is null");
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        Log.e("AdminHomeActivity", "adminDetails is null after data fetch");
                        runOnUiThread(() -> {
                            Toast.makeText(AdminHomeActivity.this, "Failed to fetch admin data", Toast.LENGTH_SHORT).show();
                        });

                    }
                }
            });
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
                            Toast.makeText(AdminHomeActivity.this, "Error loading dropdown data.", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    Log.e("API Error", "Error fetching dropdown data: " + response_dto.getContent());
                    runOnUiThread(() -> {
                        Toast.makeText(AdminHomeActivity.this, "Error loading dropdown data.", Toast.LENGTH_SHORT).show();
                    });
                }

            } catch (IOException e) {
                Log.e("Network Error", "Error fetching dropdown data: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(AdminHomeActivity.this, "Network error loading dropdown data.", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();

        SharedPreferences adminDetailsSP = getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
        adminDetailsSP.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);


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
                    if (!value.isEmpty())
                        map.put(name, value);
                }
            }
        }
        return map;
    }

    // get string
    private String getJsonElementValueAsString(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            return element.getAsString();
        }
        return "";
    }

    // get int
    private String getJsonElementValueAsInt(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            try {
                return String.valueOf(element.getAsInt());
            } catch (NumberFormatException e) {
                try {
                    return String.valueOf((int) element.getAsDouble());
                } catch (NumberFormatException ex) {
                    return "";
                }
            }
        }
        return "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_home_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_log_out) {
            SharedPreferences adminDetailsSP = getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = adminDetailsSP.edit();
            editor.clear();
            editor.apply();

            adminDetailsSP.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);

            Intent i = new Intent(AdminHomeActivity.this, AdminLoginActivity.class);
            startActivity(i);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // preference changes to handle
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
        if (id == R.id.admin_menu_item_home) {
            Log.i("SwiftExLog", "Clicked Admin Home");
            navController.navigate(R.id.nav_admin_home);
        } else if (id == R.id.admin_menu_item_account_management) {
            Log.i("SwiftExLog", "Clicked Account Management");
            navController.navigate(R.id.nav_admin_account_management);
        } else if (id == R.id.admin_menu_item_driver_management) {
            Log.i("SwiftExLog", "Clicked Driver Management");
            navController.navigate(R.id.nav_admin_driver_management);
        } else if (id == R.id.admin_menu_item_security) {
            Log.i("SwiftExLog", "Clicked Security");
            navController.navigate(R.id.nav_admin_security);
        } else if (id == R.id.admin_menu_item_profile) {
            Log.i("SwiftExLog", "Clicked Admin Profile");
            navController.navigate(R.id.nav_admin_profile);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void fetchAdminData(AdminProfileViewModel viewModel, DataFetchCallback callback) {
        SharedPreferences adminEmailSP = getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
        adminEmail = adminEmailSP.getString("Admin Email", "");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Gson gson = new Gson();
                    Admin_DTO admin_DTO = new Admin_DTO();
                    admin_DTO.setEmail(adminEmail);

                    OkHttpClient okHttpClient = new OkHttpClient();
                    Routes routes = new Routes();
                    String path = routes.getEnv_url();

                    RequestBody requestBody = RequestBody.create(gson.toJson(admin_DTO), MediaType.get("application/json"));
                    Request request = new Request.Builder()
                            .url(path + "AdminGetHomeDetails")
                            .post(requestBody)
                            .build();

                    Response response = okHttpClient.newCall(request).execute();
                    String responseText = response.body().string();
                    Response_DTO response_dto = gson.fromJson(responseText, Response_DTO.class);

                    if (response_dto.isSuccess()) {
                        SharedPreferences adminDetailsSP = getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = adminDetailsSP.edit();
                        Admin_DTO adminDetails = gson.fromJson(gson.toJson(response_dto.getContent()), Admin_DTO.class);

                        if (adminDetails != null) {
                            adminDetailsSP.edit().clear().apply();
                            editor.putString("Admin ID", adminDetails.getId());
                            editor.putString("Admin First Name", adminDetails.getFname());
                            editor.putString("Admin Last Name", adminDetails.getLname());
                            editor.putString("Admin Mobile", adminDetails.getMobile());
                            editor.putString("Admin NIC", adminDetails.getNic());
                            editor.putString("Admin Email", adminDetails.getEmail());
                            editor.putString("Admin Joined Datetime", String.valueOf(adminDetails.getJoined_datetime()));
                            editor.apply();

                            callback.onDataFetched(adminDetails);
                        } else {
                            Log.e("Deserialization Error", "Failed to deserialize JSON");
                            runOnUiThread(() -> {
                                Toast.makeText(AdminHomeActivity.this, "Failed to get admin data", Toast.LENGTH_SHORT).show();
                            });
                            callback.onDataFetched(null);
                        }
                    } else {
                        Log.e("API Error", response_dto.getContent().toString());
                        runOnUiThread(() -> {
                            Toast.makeText(AdminHomeActivity.this, response_dto.getContent().toString(), Toast.LENGTH_SHORT).show();
                        });
                        callback.onDataFetched(null);
                    }

                } catch (IOException e) {
                    Log.e("Network Error", e.getMessage());
                    runOnUiThread(() -> {
                        Toast.makeText(AdminHomeActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    });
                    callback.onDataFetched(null);
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences adminDetailsSP = getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
        adminDetailsSP.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener); // Unregister here!

    }

    interface DataFetchCallback {
        void onDataFetched(Admin_DTO adminDetails);
    }
}