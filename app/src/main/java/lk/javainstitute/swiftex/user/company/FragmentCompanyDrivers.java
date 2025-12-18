package lk.javainstitute.swiftex.user.company;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.databinding.FragmentCompanyDriversBinding;

public class FragmentCompanyDrivers extends Fragment {

    private FragmentCompanyDriversBinding binding;
    private boolean isExistingCollapsed = false;
    private boolean isNewCollapsed = false;
    private ConstraintLayout rootContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCompanyDriversBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        rootContainer = binding.companyDriversConstraintLayout;

        ImageView currentDriversCollapsible = binding.currentDriversCollapsible;
        ConstraintLayout companyExistingDriverConstraintLayout = binding.companyExistingDriverConstraintLayout;
        ImageView addCompanyDriversCollapsible = binding.addCompanyDriversCollapsible;
        ConstraintLayout existingDriversConstraintLayout = binding.existingDriversConstraintLayout;

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int initialHeight1 = companyExistingDriverConstraintLayout.getHeight();
                currentDriversCollapsible.setOnClickListener(v->{
                    if(isExistingCollapsed) {
                        expand(companyExistingDriverConstraintLayout, initialHeight1);
                        currentDriversCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(companyExistingDriverConstraintLayout, initialHeight1);
                        currentDriversCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isExistingCollapsed = !isExistingCollapsed;
                });

                int initialHeight2 = existingDriversConstraintLayout.getHeight();
                addCompanyDriversCollapsible.setOnClickListener(v->{
                    if(isNewCollapsed) {
                        expand(existingDriversConstraintLayout, initialHeight2);
                        addCompanyDriversCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(existingDriversConstraintLayout, initialHeight2);
                        addCompanyDriversCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isNewCollapsed = !isNewCollapsed;
                });
            }
        });

//        TextView companyCurrentDriverTextView = binding.companyCurrentDriverTextView;
//        companyCurrentDriverTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.companyDriversConstraintLayout, new ExistingCompanyDriverProfileFragment())
//                        .addToBackStack(null)
//                        .commit();
//            }
//        });

//        TextView addCompanyDriverTextView = binding.addCompanyDriversTextView;
//        addCompanyDriverTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.companyDriversConstraintLayout, new ExistingDriverProfileFragment())
//                        .addToBackStack(null)
//                        .commit();
//            }
//        });

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