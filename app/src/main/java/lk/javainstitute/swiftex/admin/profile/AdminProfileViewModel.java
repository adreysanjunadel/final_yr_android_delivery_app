package lk.javainstitute.swiftex.admin.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import dto.Admin_DTO;

public class AdminProfileViewModel extends ViewModel {

    private final MutableLiveData<Admin_DTO> adminProfileData = new MutableLiveData<>();

    public LiveData<Admin_DTO> getAdminProfileData() {
        return adminProfileData;
    }

    public void setAdminProfileData(Admin_DTO admin_dto) {
        adminProfileData.setValue(admin_dto);
    }
}
