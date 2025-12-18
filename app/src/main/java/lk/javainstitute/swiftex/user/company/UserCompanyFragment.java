package lk.javainstitute.swiftex.user.company;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import lk.javainstitute.swiftex.databinding.FragmentUserCompanyBinding;


public class UserCompanyFragment extends Fragment {

    private FragmentUserCompanyBinding binding;
    private boolean isCollapsed = true;
    private ConstraintLayout rootContainer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserCompanyBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        rootContainer = binding.userCompanyFragmentConstraintLayout;

        return view;
    }

    private void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        ValueAnimator animator = ValueAnimator.ofInt(initialHeight, 0);
        animator.addUpdateListener(animation -> {
            v.getLayoutParams().height = (int) animation.getAnimatedValue();
            v.requestLayout();
        });
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                v.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    private void expand(final View v) {
        v.setVisibility(View.VISIBLE);
        v.measure(View.MeasureSpec.makeMeasureSpec(rootContainer.getWidth(), View.MeasureSpec.AT_MOST),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        ValueAnimator animator = ValueAnimator.ofInt(0, targetHeight);
        animator.addUpdateListener(animation -> {
            v.getLayoutParams().height = (int) animation.getAnimatedValue();
            v.requestLayout();
        });
        animator.setDuration(300);
        animator.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}