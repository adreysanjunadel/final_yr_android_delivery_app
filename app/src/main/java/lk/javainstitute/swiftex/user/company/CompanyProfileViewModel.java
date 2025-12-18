package lk.javainstitute.swiftex.user.company;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import dto.CompanyDetailsDTO;

public class CompanyProfileViewModel extends ViewModel {

    private final MutableLiveData<CompanyDetailsDTO> companyProfileData = new MutableLiveData<>();

    public LiveData<CompanyDetailsDTO> getCompanyProfileData() {
        return companyProfileData;
    }

    public void setCompanyProfileData(CompanyDetailsDTO companyDetails) {
        companyProfileData.setValue(companyDetails);
    }
}