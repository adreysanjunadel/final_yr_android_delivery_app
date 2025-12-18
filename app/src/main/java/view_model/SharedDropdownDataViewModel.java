package view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.Map;

public class SharedDropdownDataViewModel extends ViewModel {
    private final MutableLiveData<Map<String, String>> provinceMap = new MutableLiveData<>();
    private final MutableLiveData<Map<String, String>> cityMap = new MutableLiveData<>();

    private final MutableLiveData<Map<String, String>> vehicleTypeMap = new MutableLiveData<>();
    private final MutableLiveData<Map<String, String>> vehicleBrandMap = new MutableLiveData<>();
    private final MutableLiveData<Map<String, String>> vehicleModelMap = new MutableLiveData<>();

    private final MutableLiveData<Map<String, String>> categoryMap = new MutableLiveData<>();
    private final MutableLiveData<Map<String, String>> itemBrandMap = new MutableLiveData<>();
    private final MutableLiveData<Map<String, String>> itemModelMap = new MutableLiveData<>();

    private final MutableLiveData<Map<String, String>> industryMap = new MutableLiveData<>();

    public LiveData<Map<String, String>> getProvinceMap() {
        return provinceMap;
    }

    public void setProvinceMap(Map<String, String> map) {
        provinceMap.setValue(map);
    }

    public LiveData<Map<String, String>> getCityMap() {
        return cityMap;
    }

    public void setCityMap(Map<String, String> map) {
        cityMap.setValue(map);
    }

    public LiveData<Map<String, String>> getVehicleTypeMap() {
        return vehicleTypeMap;
    }

    public void setVehicleTypeMap(Map<String, String> map) {
        vehicleTypeMap.setValue(map);
    }

    public LiveData<Map<String, String>> getVehicleBrandMap() {
        return vehicleBrandMap;
    }

    public void setVehicleBrandMap(Map<String, String> map) {
        vehicleBrandMap.setValue(map);
    }

    public LiveData<Map<String, String>> getVehicleModelMap() {
        return vehicleModelMap;
    }

    public void setVehicleModelMap(Map<String, String> map) {
        vehicleModelMap.setValue(map);
    }

    public LiveData<Map<String, String>> getCategoryMap() {
        return categoryMap;
    }

    public void setCategoryMap(Map<String, String> map) {
        categoryMap.setValue(map);
    }

    public LiveData<Map<String, String>> getItemBrandMap() {
        return itemBrandMap;
    }

    public void setItemBrandMap(Map<String, String> map) {
        itemBrandMap.setValue(map);
    }

    public LiveData<Map<String, String>> getItemModelMap() {
        return itemModelMap;
    }

    public void setItemModelMap(Map<String, String> map) {
        itemModelMap.setValue(map);
    }

    public LiveData<Map<String, String>> getIndustryMap() {
        return industryMap;
    }

    public void setIndustryMap(Map<String, String> map) {
        industryMap.setValue(map);
    }
}
