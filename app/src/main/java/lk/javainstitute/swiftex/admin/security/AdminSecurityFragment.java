package lk.javainstitute.swiftex.admin.security;

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
import lk.javainstitute.swiftex.databinding.FragmentAdminSecurityBinding;

public class AdminSecurityFragment extends Fragment {

    private FragmentAdminSecurityBinding binding;
    private boolean isApprovalsCollapsed;
    private boolean isNewCompanyApprovalsCollapsed;
    private boolean isCompanyHireUserApprovalsCollapsed;
    private boolean isCompanyHireDriverApprovalsCollapsed;

    private ConstraintLayout rootContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminSecurityBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        rootContainer = binding.adminSecurityConstraintLayout;

        ImageView approvalsCollapsible = binding.approvalsCollapsible;
        ImageView newCompanyApprovalsCollapsible = binding.newCompanyApprovalsCollapsible;
        ImageView companyHireUserApprovalCollapsible = binding.companyHireUserApprovalCollapsible;
        ImageView companyHireDriverApprovalsCollapsible = binding.companyHireDriverApprovalsCollapsible;

        ConstraintLayout approvalsConstraintLayout = binding.approvalsConstraintLayout;
        ConstraintLayout newApprovalsConstraintLayout = binding.newApprovalsConstraintLayout;
        ConstraintLayout companyHireUserApprovalConstraintLayout = binding.companyHireUserApprovalConstraintLayout;
        ConstraintLayout companyHireDriverApprovalsConstraintLayout = binding.companyHireDriverApprovalsConstraintLayout;

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int initialHeight1 = approvalsConstraintLayout.getHeight();
                approvalsCollapsible.setOnClickListener(v->{
                    if(isApprovalsCollapsed) {
                        expand(approvalsConstraintLayout, initialHeight1);
                        approvalsCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(approvalsConstraintLayout, initialHeight1);
                        approvalsCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isApprovalsCollapsed = !isApprovalsCollapsed;
                });

                int initialHeight2 = newApprovalsConstraintLayout.getHeight();
                newCompanyApprovalsCollapsible.setOnClickListener(v->{
                    if(isNewCompanyApprovalsCollapsed) {
                        expand(newApprovalsConstraintLayout, initialHeight2);
                        newCompanyApprovalsCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(newApprovalsConstraintLayout, initialHeight2);
                        newCompanyApprovalsCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isNewCompanyApprovalsCollapsed = !isNewCompanyApprovalsCollapsed;
                });

                int initialHeight3 = companyHireUserApprovalConstraintLayout.getHeight();
                companyHireUserApprovalCollapsible.setOnClickListener(v->{
                    if(isCompanyHireUserApprovalsCollapsed) {
                        expand(companyHireUserApprovalConstraintLayout, initialHeight3);
                        companyHireUserApprovalCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(companyHireUserApprovalConstraintLayout, initialHeight3);
                        companyHireUserApprovalCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isCompanyHireUserApprovalsCollapsed = !isCompanyHireUserApprovalsCollapsed;
                });

                int initialHeight4 = companyHireDriverApprovalsConstraintLayout.getHeight();
                companyHireDriverApprovalsCollapsible.setOnClickListener(v->{
                    if(isCompanyHireDriverApprovalsCollapsed) {
                        expand(companyHireDriverApprovalsConstraintLayout, initialHeight4);
                        companyHireDriverApprovalsCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(companyHireDriverApprovalsConstraintLayout, initialHeight4);
                        companyHireDriverApprovalsCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isCompanyHireDriverApprovalsCollapsed = !isCompanyHireDriverApprovalsCollapsed;
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