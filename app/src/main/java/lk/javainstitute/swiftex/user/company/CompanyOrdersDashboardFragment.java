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
import lk.javainstitute.swiftex.databinding.FragmentCompanyOrdersDashboardBinding;

public class CompanyOrdersDashboardFragment extends Fragment {

    private FragmentCompanyOrdersDashboardBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCompanyOrdersDashboardBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Button companyOrderHistoryButton = binding.companyOrderHistoryButton;
        companyOrderHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.companyOrdersDashboardConstraintLayout, new CompanyOrderHistoryFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        Button companyAnalyticsButton = binding.companyAnalyticsButton;
        companyAnalyticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.companyOrdersDashboardConstraintLayout, new CompanyAnalyticsFragment())
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