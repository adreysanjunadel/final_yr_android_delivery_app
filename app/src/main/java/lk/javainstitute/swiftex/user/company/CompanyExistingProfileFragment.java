package lk.javainstitute.swiftex.user.company;

import static android.view.View.GONE;

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
import android.widget.Button;
import android.widget.ImageView;

import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.databinding.FragmentCompanyProfileBinding;

public class CompanyExistingProfileFragment extends Fragment {

    private FragmentCompanyProfileBinding binding;
    private boolean isCompanyDetailsCollapsed = false;
    private boolean isCompanyAddressDetailsCollapsed = false;
    private ConstraintLayout rootContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            binding = FragmentCompanyProfileBinding.inflate(inflater, container, false);
            View view = binding.getRoot();

            rootContainer = binding.companyProfileFragmentConstraintLayout;

            ImageView companyDetailsCollapsible = binding.companyDetailsCollapsible;
            ConstraintLayout companyDetailsConstraintLayout = binding.companyDetailsConstraintLayout;
            ImageView companyProfileAddressDetailsCollapsible = binding.companyProfileAddressDetailsCollapsible;
            ConstraintLayout companyAddressDetailsConstraintLayout = binding.companyAddressDetailsConstraintLayout;

//        Button createCompanyAccountButton = binding.createCompanyAccountButton;
//        createCompanyAccountButton.setVisibility(GONE);
            Button updateCompanyProfileButton = binding.updateCompanyProfileButton;
            updateCompanyProfileButton.setVisibility(GONE);
//            Button companyBlockAccountButton = binding.companyBlockAccountButton;
//            updateCompanyProfileButton.setVisibility(GONE);
//            Button companyDeleteAccountButton = binding.companyDeleteAccountButton;
//            updateCompanyProfileButton.setVisibility(GONE);


            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int initialHeight1 = companyDetailsConstraintLayout.getHeight();
                    companyDetailsCollapsible.setOnClickListener(v->{
                        if(isCompanyDetailsCollapsed) {
                            expand(companyDetailsConstraintLayout, initialHeight1);
                            companyDetailsCollapsible.setImageResource(R.drawable.ic_arrow_up);
                        } else {
                            collapse(companyDetailsConstraintLayout, initialHeight1);
                            companyDetailsCollapsible.setImageResource(R.drawable.ic_arrow_down);
                        }
                        isCompanyDetailsCollapsed = !isCompanyDetailsCollapsed;
                    });

                    int initialHeight2 = companyAddressDetailsConstraintLayout.getHeight();
                    companyProfileAddressDetailsCollapsible.setOnClickListener(v->{
                        if(isCompanyAddressDetailsCollapsed) {
                            expand(companyAddressDetailsConstraintLayout, initialHeight2);
                            companyProfileAddressDetailsCollapsible.setImageResource(R.drawable.ic_arrow_up);
                        } else {
                            collapse(companyAddressDetailsConstraintLayout, initialHeight2);
                            companyProfileAddressDetailsCollapsible.setImageResource(R.drawable.ic_arrow_down);
                        }
                        isCompanyAddressDetailsCollapsed = !isCompanyAddressDetailsCollapsed;
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
                v.setVisibility(GONE);
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