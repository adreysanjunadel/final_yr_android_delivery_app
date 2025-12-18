package lk.javainstitute.swiftex.user.company;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.databinding.FragmentCompanyStaffBinding;

public class CompanyStaffFragment extends Fragment {

    private FragmentCompanyStaffBinding binding;
    private boolean isExistingCollapsed = false;
    private boolean isNewCollapsed = false;
    private ConstraintLayout rootContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCompanyStaffBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        rootContainer = binding.companyStaffConstraintLayout;

        ImageView currentStaffCollapsible = binding.currentStaffCollapsible;
        ConstraintLayout companyExistingStaffConstraintLayout = binding.companyExistingStaffConstraintLayout;
        ImageView searchNewStaffCollapsible = binding.searchNewStaffCollapsible;
        ConstraintLayout hireUserConstraintLayout = binding.hireUserConstraintLayout;

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int initialHeight1 = companyExistingStaffConstraintLayout.getHeight();
                currentStaffCollapsible.setOnClickListener(v->{
                    if(isExistingCollapsed) {
                        expand(companyExistingStaffConstraintLayout, initialHeight1);
                        currentStaffCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(companyExistingStaffConstraintLayout, initialHeight1);
                        currentStaffCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isExistingCollapsed = !isExistingCollapsed;
                });

                int initialHeight2 = hireUserConstraintLayout.getHeight();
                searchNewStaffCollapsible.setOnClickListener(v->{
                    if(isNewCollapsed) {
                        expand(hireUserConstraintLayout, initialHeight2);
                        searchNewStaffCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(hireUserConstraintLayout, initialHeight2);
                        searchNewStaffCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isNewCollapsed = !isNewCollapsed;
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