package lk.javainstitute.swiftex.admin.account_management;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.admin.driver_management.AdminCreateNewDriverFragment;
import lk.javainstitute.swiftex.databinding.FragmentAdminAccountManagementBinding;

public class AccountManagementFragment extends Fragment {

    private FragmentAdminAccountManagementBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminAccountManagementBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}