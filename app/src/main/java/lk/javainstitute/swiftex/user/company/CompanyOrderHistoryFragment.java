package lk.javainstitute.swiftex.user.company;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.databinding.FragmentCompanyOrderHistoryBinding;

public class CompanyOrderHistoryFragment extends Fragment {

    private FragmentCompanyOrderHistoryBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCompanyOrderHistoryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

//        TextView companyHistoryTextView = binding.companyHistoryTextView;
//        companyHistoryTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.companyOrderHistoryConstraintLayout, new CompanyPastOrderFragment())
//                        .addToBackStack(null)
//                        .commit();
//            }
//        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}