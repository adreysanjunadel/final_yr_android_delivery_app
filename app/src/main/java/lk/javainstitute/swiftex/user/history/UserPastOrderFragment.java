package lk.javainstitute.swiftex.user.history;

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
import lk.javainstitute.swiftex.databinding.FragmentUserPastOrderBinding;

public class UserPastOrderFragment extends Fragment {

    private FragmentUserPastOrderBinding binding;
    private boolean isOrderDetailsCollapsed = false;
    private boolean isOrderItemDetailsCollapsed = false;
    private ConstraintLayout rootContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserPastOrderBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        ImageView pastUserOrderDetailsCollapsible = binding.pastUserOrderDetailsCollapsible;
        ConstraintLayout userPastOrderDetailsConstraintLayout = binding.userPastOrderDetailsConstraintLayout;
        ImageView pastUserOrderItemDetailsCollapsible = binding.pastUserOrderItemDetailsCollapsible;
        ConstraintLayout pastUserOrderItemDetailsConstraintLayout = binding.pastUserOrderItemDetailsConstraintLayout;

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int initialHeight1 = userPastOrderDetailsConstraintLayout.getHeight();
                pastUserOrderDetailsCollapsible.setOnClickListener(v->{
                    if(isOrderDetailsCollapsed) {
                        expand(userPastOrderDetailsConstraintLayout, initialHeight1);
                        pastUserOrderDetailsCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(userPastOrderDetailsConstraintLayout, initialHeight1);
                        pastUserOrderDetailsCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isOrderDetailsCollapsed = !isOrderDetailsCollapsed;
                });

                int initialHeight2 = pastUserOrderItemDetailsConstraintLayout.getHeight();
                pastUserOrderItemDetailsCollapsible.setOnClickListener(v->{
                    if(isOrderItemDetailsCollapsed) {
                        expand(pastUserOrderItemDetailsConstraintLayout, initialHeight2);
                        pastUserOrderItemDetailsCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(pastUserOrderItemDetailsConstraintLayout, initialHeight2);
                        pastUserOrderItemDetailsCollapsible.setImageResource(R.drawable.ic_arrow_down);
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