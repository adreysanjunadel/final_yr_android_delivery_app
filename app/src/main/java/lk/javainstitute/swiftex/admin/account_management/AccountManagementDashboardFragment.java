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
import lk.javainstitute.swiftex.databinding.FragmentAccountManagementDashboardBinding;

public class AccountManagementDashboardFragment extends Fragment {

    private FragmentAccountManagementDashboardBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountManagementDashboardBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Button userManagementButton = binding.userManagementButton;
        userManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentAccountManagementFragmentContainerView, new AdminUserManagementFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        Button companyManagementButton = binding.companyManagementButton;
        companyManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentAccountManagementFragmentContainerView, new AdminCompanyManagementFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}