package lk.javainstitute.swiftex.user.company;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.databinding.FragmentExistingStaffProfileBinding;

public class ExistingStaffProfileFragment extends Fragment {

    private FragmentExistingStaffProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExistingStaffProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}