package lk.javainstitute.swiftex.admin.profile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import dto.Admin_DTO;
import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.databinding.FragmentAdminProfileBinding;

public class AdminProfileFragment extends Fragment {

    private FragmentAdminProfileBinding binding;
    private boolean adminDetailsCollapsed = false;
    private ConstraintLayout rootContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("AdminProfileFragment", "onCreate() called");
        binding = FragmentAdminProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        rootContainer = binding.adminProfileFragmentConstraintLayout;


        Log.i("AdminProfileFragment", "View Returned");
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("AdminProfileFragment", "onAttach() called");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d("AdminProfileFragment", "onViewCreated() called");
        super.onViewCreated(view, savedInstanceState);

        AdminProfileViewModel viewModel = new ViewModelProvider(requireActivity()).get(AdminProfileViewModel.class);

        viewModel.getAdminProfileData().observe(getViewLifecycleOwner(), new Observer<Admin_DTO>() {
            @Override
            public void onChanged(Admin_DTO adminDto) {
                if (adminDto != null && binding != null) {
                    binding.adminProfileFirstNameEditText.setText(adminDto.getFname());
                    binding.adminProfileLastNameEditText.setText(adminDto.getLname());
                    binding.adminProfileMobileEditText.setText(adminDto.getMobile());
                    binding.adminProfileNICEditText.setText(adminDto.getNic());
                    binding.adminProfileEmailEditText.setText(adminDto.getEmail());
                }else {
                    Log.w("AdminProfileFragment", "adminData or binding is null"); // case: null log
                    if (adminDto == null) {
                        Log.w("AdminProfileFragment", "adminData is null");
                    }
                    if (binding == null) {
                        Log.w("AdminProfileFragment", "binding is null");
                    }

                    if (binding != null) {
                        binding.adminProfileFirstNameEditText.setText("");
                        binding.adminProfileLastNameEditText.setText("");
                        binding.adminProfileMobileEditText.setText("");
                        binding.adminProfileNICEditText.setText("");
                        binding.adminProfileEmailEditText.setText("");
                    }
                }
            }
        });

        ImageView adminPersonalDetailsCollapsible = binding.adminPersonalDetailsCollapsible;
        ConstraintLayout adminProfilePersonalDetailsConstraintLayout = binding.adminProfilePersonalDetailsConstraintLayout;

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int initialHeight1 = adminProfilePersonalDetailsConstraintLayout.getHeight();
                adminPersonalDetailsCollapsible.setOnClickListener(v->{
                    if(adminDetailsCollapsed) {
                        expand(adminProfilePersonalDetailsConstraintLayout, initialHeight1);
                        adminPersonalDetailsCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(adminProfilePersonalDetailsConstraintLayout, initialHeight1);
                        adminPersonalDetailsCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    adminDetailsCollapsed = !adminDetailsCollapsed;
                });
            }
        });

    }

    private void collapse(final View v, final int initialHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(initialHeight, 0);
        animator.addUpdateListener(animation -> {
            v.getLayoutParams().height = (int) animation.getAnimatedValue();
            v.requestLayout();
        });
        animator.setDuration(1000);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                v.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    private void expand(final View v, final int targetHeight) {
        v.setVisibility(View.VISIBLE);
        ValueAnimator animator = ValueAnimator.ofInt(0, targetHeight);
        animator.addUpdateListener(animation -> {
            v.getLayoutParams().height = (int) animation.getAnimatedValue();
            v.requestLayout();
        });
        animator.setDuration(1000);
        animator.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}