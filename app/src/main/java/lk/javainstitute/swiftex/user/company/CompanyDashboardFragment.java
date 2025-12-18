package lk.javainstitute.swiftex.user.company;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.databinding.FragmentCompanyDashboardBinding;

public class CompanyDashboardFragment extends Fragment {

    private FragmentCompanyDashboardBinding binding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCompanyDashboardBinding.inflate(inflater, container, false);
        Button requestCourierButton = binding.requestCourierButton;
        View view = binding.getRoot();

        requestCourierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentCompanyFragmentContainerView, new CompanyCourierRequestFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        Button ordersButton = binding.ordersButton;
        ordersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentCompanyFragmentContainerView, new CompanyOrdersDashboardFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        Button manageStaffButton = binding.manageStaffButton;
        manageStaffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentCompanyFragmentContainerView, new CompanyManageStaffFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        Button profileButton = binding.profileButton;
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentCompanyFragmentContainerView, new CompanyProfileFragment())
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