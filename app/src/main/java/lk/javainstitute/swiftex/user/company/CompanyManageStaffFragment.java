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
import lk.javainstitute.swiftex.databinding.FragmentCompanyManageStaffBinding;

public class CompanyManageStaffFragment extends Fragment {

    private FragmentCompanyManageStaffBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCompanyManageStaffBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Button companyManageStaffButton = binding.companyManageStaffButton;
        companyManageStaffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentCompanyFragmentContainerView, new CompanyStaffFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        Button companyManageDriversButton = binding.companyManageDriversButton;
        companyManageDriversButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentCompanyFragmentContainerView, new FragmentCompanyDrivers())
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