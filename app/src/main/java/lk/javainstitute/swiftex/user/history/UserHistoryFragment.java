package lk.javainstitute.swiftex.user.history;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.databinding.FragmentUserHistoryBinding;
import lk.javainstitute.swiftex.user.company.CompanyCourierRequestFragment;

public class UserHistoryFragment extends Fragment {

    private FragmentUserHistoryBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserHistoryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

//        TextView userCourierHistoryTextView = binding.userCourierHistoryTextView;
//        userCourierHistoryTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.userHistoryFragmentContainerView, new UserPastOrderFragment())
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