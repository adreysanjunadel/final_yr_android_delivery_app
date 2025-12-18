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
import lk.javainstitute.swiftex.databinding.FragmentCompanyPastOrderBinding;

public class CompanyPastOrderFragment extends Fragment {

    private FragmentCompanyPastOrderBinding binding;
    private boolean isOrderDetailsCollapsed = false;
    private boolean isOrderItemDetailsCollapsed = false;
    private ConstraintLayout rootContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCompanyPastOrderBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        ImageView pastCompanyOrderDetailsCollapsible = binding.pastCompanyOrderDetailsCollapsible;
        ConstraintLayout companyPastOrderDetailsConstraintLayout = binding.companyPastOrderDetailsConstraintLayout;
        ImageView companyUserOrderItemDetailsCollapsible = binding.companyUserOrderItemDetailsCollapsible;
        ConstraintLayout pastCompanyOrderItemDetailsConstraintLayout = binding.pastCompanyOrderItemDetailsConstraintLayout;

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int initialHeight1 = companyPastOrderDetailsConstraintLayout.getHeight();
                pastCompanyOrderDetailsCollapsible.setOnClickListener(v->{
                    if(isOrderDetailsCollapsed) {
                        expand(companyPastOrderDetailsConstraintLayout, initialHeight1);
                        pastCompanyOrderDetailsCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(companyPastOrderDetailsConstraintLayout, initialHeight1);
                        pastCompanyOrderDetailsCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isOrderDetailsCollapsed = !isOrderDetailsCollapsed;
                });

                int initialHeight2 = pastCompanyOrderItemDetailsConstraintLayout.getHeight();
                companyUserOrderItemDetailsCollapsible.setOnClickListener(v->{
                    if(isOrderItemDetailsCollapsed) {
                        expand(pastCompanyOrderItemDetailsConstraintLayout, initialHeight2);
                        companyUserOrderItemDetailsCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(pastCompanyOrderItemDetailsConstraintLayout, initialHeight2);
                        companyUserOrderItemDetailsCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isOrderItemDetailsCollapsed = !isOrderItemDetailsCollapsed;
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