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

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.databinding.FragmentCompanyAnalyticsBinding;

public class CompanyAnalyticsFragment extends Fragment {

    private FragmentCompanyAnalyticsBinding binding;
    private boolean pieChartIsCollapsed = false;
    private boolean barChartIsCollapsed = false;
    private ConstraintLayout rootContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCompanyAnalyticsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        rootContainer = binding.companyAnalyticsFragmentConstraintLayout;

        PieChart companyPieChart = binding.companyPieChart;

        ArrayList<PieEntry> pieEntryArrayList = new ArrayList<>();
        pieEntryArrayList.add(new PieEntry(35, "Early"));
        pieEntryArrayList.add(new PieEntry(10, "On-Time"));
        pieEntryArrayList.add(new PieEntry(25, "Late"));
        pieEntryArrayList.add(new PieEntry(30, "Cancelled"));

        PieDataSet pieDataSet = new PieDataSet(pieEntryArrayList, "Order Status");

        ArrayList<Integer> colorArraylist = new ArrayList<>();
        colorArraylist.add(getActivity().getColor(R.color.lighterblue));
        colorArraylist.add(getActivity().getColor(R.color.yellow));
        colorArraylist.add(getActivity().getColor(R.color.bluegreen));
        colorArraylist.add(getActivity().getColor(R.color.lightred));
        pieDataSet.setColors(colorArraylist);

        PieData pieData = new PieData();
        pieData.setDataSet(pieDataSet);
        pieData.setValueTextSize(18);

        companyPieChart.setData(pieData);

        companyPieChart.animateY(1000, Easing.EaseInCirc);

        companyPieChart.setCenterText("Delivery Status");
        companyPieChart.setCenterTextColor(getActivity().getColor(R.color.lightbluegreen));
        companyPieChart.setCenterTextSize(20);

        companyPieChart.setDescription(null);
        companyPieChart.invalidate();

        BarChart companyBarChart = binding.companyBarChart;

        ArrayList<BarEntry> barEntryArrayList = new ArrayList<>();
        barEntryArrayList.add(new BarEntry(10, 100));
        barEntryArrayList.add(new BarEntry(20,120));
        barEntryArrayList.add(new BarEntry(30,105));
        barEntryArrayList.add(new BarEntry(40,90));
        barEntryArrayList.add(new BarEntry(50,60));

        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Student Attendance");

        BarData barData = new BarData();
        barData.addDataSet(barDataSet);
        barData.setBarWidth(5);

        ArrayList<Integer> colorArrayList = new ArrayList<>();
        colorArrayList.add(getActivity().getColor(R.color.bluegreen));
        colorArrayList.add(getActivity().getColor(R.color.orange_premium));
        colorArrayList.add(getActivity().getColor(R.color.scarlet));
        colorArrayList.add(getActivity().getColor(R.color.yellow));
        colorArrayList.add(getActivity().getColor(R.color.marine));
        barDataSet.setColors(colorArrayList);

        companyBarChart.setPinchZoom(false);
        companyBarChart.setScaleEnabled(false);
        companyBarChart.animateY(1000, Easing.EaseInBounce);

        companyBarChart.setFitBars(true);
        companyBarChart.setData(barData);

        ArrayList<LegendEntry> legendEntryArrayList = new ArrayList<>();
        legendEntryArrayList.add(new LegendEntry("Monday", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.bluegreen)));
        legendEntryArrayList.add(new LegendEntry("Tuesday", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.orange_premium)));
        legendEntryArrayList.add(new LegendEntry("Wednesday", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.scarlet)));
        legendEntryArrayList.add(new LegendEntry("Thursday", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.yellow)));
        legendEntryArrayList.add(new LegendEntry("Friday", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.marine)));

        companyBarChart.getLegend().setCustom(legendEntryArrayList);
        companyBarChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        companyBarChart.getLegend().setXEntrySpace(10);

        Description barChartDescription = new Description();
        barChartDescription.setText("Logins per week");
        barChartDescription.setTextSize(18);
        barChartDescription.setTextColor(getActivity().getColor(R.color.white));
        companyBarChart.setDescription(barChartDescription);

        companyBarChart.invalidate();

        ImageView pieChartCollapsible = binding.companyPieChartCollapsible;
        ConstraintLayout companyPieChartConstraintLayout = binding.companyPieChartConstraintLayout;
        ImageView companyBarChartCollapsible = binding.companyBarChartCollapsible;
        ConstraintLayout companyBarChartConstraintLayout = binding.companyBarChartConstraintLayout;

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int initialHeight1 = companyPieChartConstraintLayout.getHeight();
                int initialHeightc1 = companyPieChart.getHeight();
                pieChartCollapsible.setOnClickListener(v->{
                    if(pieChartIsCollapsed) {
                        expand(companyPieChartConstraintLayout, initialHeight1);
                        expand(companyPieChart, initialHeightc1);
                        pieChartCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(companyPieChartConstraintLayout, initialHeight1);
                        collapse(companyPieChart, initialHeightc1);
                        pieChartCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    pieChartIsCollapsed = !pieChartIsCollapsed;
                });
                int initialHeight2 = companyBarChartConstraintLayout.getHeight();
                int initialHeightc2 = companyBarChart.getHeight();
                companyBarChartCollapsible.setOnClickListener(v->{
                    if(barChartIsCollapsed) {
                        expand(companyBarChartConstraintLayout, initialHeight2);
                        expand(companyBarChart, initialHeightc2);
                        companyBarChartCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(companyBarChartConstraintLayout, initialHeight2);
                        collapse(companyBarChart, initialHeightc2);
                        companyBarChartCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    barChartIsCollapsed = !barChartIsCollapsed;
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