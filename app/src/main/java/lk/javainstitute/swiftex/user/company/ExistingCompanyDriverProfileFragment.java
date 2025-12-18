package lk.javainstitute.swiftex.user.company;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.databinding.FragmentExistingCompanyDriverProfileBinding;

public class ExistingCompanyDriverProfileFragment extends Fragment {

    private FragmentExistingCompanyDriverProfileBinding binding;
    private boolean isCompanyDriverDetailsIsCollapsed;
    private boolean isCompanyDriverVehicleDetailsIsCollapsed;
    private ConstraintLayout rootContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExistingCompanyDriverProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        rootContainer = binding.existingCompanyDriverProfileConstraintLayout;

        ImageView existingCompanyDriverDetailsCollapsible = binding.existingCompanyDriverDetailsCollapsible;
        ConstraintLayout existingCompanyDriverDetailConstraintLayout = binding.existingCompanyDriverDetailConstraintLayout;
        ImageView existingCompanyDriverVehicleDetailsCollapsible = binding.existingCompanyDriverVehicleDetailsCollapsible;
        ConstraintLayout existingCompanyDriverVehicleDetailConstraintLayout = binding.existingCompanyDriverVehicleDetailConstraintLayout;

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int initialHeight1 = existingCompanyDriverDetailConstraintLayout.getHeight();
                existingCompanyDriverDetailsCollapsible.setOnClickListener(v->{
                    if(isCompanyDriverDetailsIsCollapsed) {
                        expand(existingCompanyDriverDetailConstraintLayout, initialHeight1);
                        existingCompanyDriverDetailsCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(existingCompanyDriverDetailConstraintLayout, initialHeight1);
                        existingCompanyDriverDetailsCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isCompanyDriverDetailsIsCollapsed = !isCompanyDriverDetailsIsCollapsed;
                });

                int initialHeight2 = existingCompanyDriverVehicleDetailConstraintLayout.getHeight();
                existingCompanyDriverVehicleDetailsCollapsible.setOnClickListener(v->{
                    if(isCompanyDriverVehicleDetailsIsCollapsed) {
                        expand(existingCompanyDriverVehicleDetailConstraintLayout, initialHeight2);
                        existingCompanyDriverVehicleDetailsCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(existingCompanyDriverVehicleDetailConstraintLayout, initialHeight2);
                        existingCompanyDriverVehicleDetailsCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isCompanyDriverVehicleDetailsIsCollapsed = !isCompanyDriverVehicleDetailsIsCollapsed;
                });
            }
        });

        return view;
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