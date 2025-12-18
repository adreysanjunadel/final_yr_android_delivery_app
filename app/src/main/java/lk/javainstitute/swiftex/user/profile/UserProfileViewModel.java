package lk.javainstitute.swiftex.user.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import dto.UserHomeDetailsDTO;

public class UserProfileViewModel extends ViewModel {

    private final MutableLiveData<UserHomeDetailsDTO> userProfileData = new MutableLiveData<>();

    public LiveData<UserHomeDetailsDTO> getUserProfileData() {
        return userProfileData;
    }

    public void setUserProfileData(UserHomeDetailsDTO userHomeDetails) {
        userProfileData.setValue(userHomeDetails);
    }
}
