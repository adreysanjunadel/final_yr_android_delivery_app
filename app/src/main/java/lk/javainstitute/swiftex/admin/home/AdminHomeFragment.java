package lk.javainstitute.swiftex.admin.home;

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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.databinding.FragmentAdminHomeBinding;

public class AdminHomeFragment extends Fragment {

    private FragmentAdminHomeBinding binding;
    private boolean isStatisticsCollapsed;
    private boolean isChartsCollapsed;
    private boolean isOrderStatusPieChartCollapsed;
    private boolean isUsersRegisteredBarChartCollapsed;
    private boolean isCompaniesRegisteredLineChartCollapsed;
    private boolean isDownTimeReportsBarChartCollapsed;

    private ConstraintLayout rootContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        rootContainer = binding.fragmentAdminHomeConstraintLayout;

        ImageView statisticsCollapsible = binding.statisticsCollapsible;
        ImageView adminChartsCollapsible = binding.adminChartsCollapsible;
        ImageView orderStatusPieChartCollapsible = binding.orderStatusPieChartCollapsible;
        ImageView usersRegisteredBarChartCollapsible = binding.usersRegisteredBarChartCollapsible;
        ImageView companiesRegisteredLineChartCollapsible = binding.companiesRegisteredLineChartCollapsible;
        ImageView downTimeReportsBarChartCollapsible = binding.downTimeReportsBarChartCollapsible;

        ConstraintLayout statisticsConstraintLayout = binding.statisticsConstraintLayout;
        ConstraintLayout adminChartsConstraintLayout = binding.adminChartsConstraintLayout;
        ConstraintLayout orderStatusPieChartConstraintLayout = binding.orderStatusPieChartConstraintLayout;
        ConstraintLayout usersRegisteredBarChartConstraintLayout = binding.usersRegisteredBarChartConstraintLayout;
        ConstraintLayout companiesRegisteredLineChartConstraintLayout = binding.companiesRegisteredLineChartConstraintLayout;
        ConstraintLayout downTimeReportsBarChartConstraintLayout = binding.downTimeReportsBarChartConstraintLayout;

        PieChart orderStatusPieChart = binding.orderStatusPieChart;
        BarChart usersRegisteredBarChart = binding.usersRegisteredBarChart;
        LineChart companiesRegisteredLineChart = binding.companiesRegisteredLineChart;
        BarChart downTimeReportsBarChart = binding.downTimeReportsBarChart;

        //pie chart
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

        orderStatusPieChart.setData(pieData);

        orderStatusPieChart.animateY(1000, Easing.EaseInCirc);

        orderStatusPieChart.setCenterText("Delivery Status");
        orderStatusPieChart.setCenterTextColor(getActivity().getColor(R.color.lightbluegreen));
        orderStatusPieChart.setCenterTextSize(20);

        orderStatusPieChart.setDescription(null);
        orderStatusPieChart.invalidate();

        //user registered barchart
        ArrayList<BarEntry> barEntryArrayList = new ArrayList<>();
        barEntryArrayList.add(new BarEntry(10, 100));
        barEntryArrayList.add(new BarEntry(20,120));
        barEntryArrayList.add(new BarEntry(30,105));
        barEntryArrayList.add(new BarEntry(40,90));

        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Users Registered");

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

        usersRegisteredBarChart.setPinchZoom(false);
        usersRegisteredBarChart.setScaleEnabled(false);
        usersRegisteredBarChart.animateY(1000, Easing.EaseInBounce);

        usersRegisteredBarChart.setFitBars(true);
        usersRegisteredBarChart.setData(barData);

        ArrayList<LegendEntry> legendEntryArrayList = new ArrayList<>();
        legendEntryArrayList.add(new LegendEntry("Week 1", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.bluegreen)));
        legendEntryArrayList.add(new LegendEntry("Week 2", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.orange_premium)));
        legendEntryArrayList.add(new LegendEntry("Week 3", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.scarlet)));
        legendEntryArrayList.add(new LegendEntry("Week 4", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.yellow)));

        usersRegisteredBarChart.getLegend().setCustom(legendEntryArrayList);
        usersRegisteredBarChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        usersRegisteredBarChart.getLegend().setXEntrySpace(10);

        Description barChartDescription = new Description();
        barChartDescription.setText("Logins per week");
        barChartDescription.setTextSize(18);
        barChartDescription.setTextColor(getActivity().getColor(R.color.white));
        usersRegisteredBarChart.setDescription(barChartDescription);

        usersRegisteredBarChart.invalidate();

        //company registrations LineChart

        ArrayList<Entry> lineEntryArrayList = new ArrayList<>();
        lineEntryArrayList.add(new Entry(35, 25));
        lineEntryArrayList.add(new Entry(45, 65));
        lineEntryArrayList.add(new Entry(55, 35));
        lineEntryArrayList.add(new Entry(65, 85));

        LineDataSet lineDataSet = new LineDataSet(lineEntryArrayList, "Companies Registered");
        LineData lineData = new LineData();
        lineData.addDataSet(lineDataSet);

        ArrayList<Integer> lineChartColorArrayList = new ArrayList<>();
        lineChartColorArrayList.add(getActivity().getColor(R.color.yellow));
        lineChartColorArrayList.add(getActivity().getColor(R.color.lightred));
        lineChartColorArrayList.add(getActivity().getColor(R.color.scarlet));
        lineChartColorArrayList.add(getActivity().getColor(R.color.orange_premium));
        lineDataSet.setColors(lineChartColorArrayList);

        ArrayList<LegendEntry> legendEntryCompanyArrayList = new ArrayList<>();
        legendEntryCompanyArrayList.add(new LegendEntry("Week 1", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.yellow)));
        legendEntryCompanyArrayList.add(new LegendEntry("Week 2", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.lightred)));
        legendEntryCompanyArrayList.add(new LegendEntry("Week 3", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.scarlet)));
        legendEntryCompanyArrayList.add(new LegendEntry("Week 4", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.orange_premium)));

        companiesRegisteredLineChart.getLegend().setCustom(legendEntryCompanyArrayList);

        companiesRegisteredLineChart.animateY(1000, Easing.EaseInCirc);

        companiesRegisteredLineChart.setData(lineData);

        companiesRegisteredLineChart.setDescription(null);
        companiesRegisteredLineChart.invalidate();


        //downtime reports
        ArrayList<BarEntry> downTimeBarEntryArrayList = new ArrayList<>();
        downTimeBarEntryArrayList.add(new BarEntry(10, 8));
        downTimeBarEntryArrayList.add(new BarEntry(20,7));
        downTimeBarEntryArrayList.add(new BarEntry(30,15));
        downTimeBarEntryArrayList.add(new BarEntry(40,3));

        BarDataSet downTimeBarDataSet = new BarDataSet(downTimeBarEntryArrayList, "Monthly Downtime Reports");

        BarData downTimeBarData = new BarData();
        downTimeBarData.addDataSet(downTimeBarDataSet);
        downTimeBarData.setBarWidth(5);

        ArrayList<Integer> downTimeColorArrayList = new ArrayList<>();
        downTimeColorArrayList.add(getActivity().getColor(R.color.bluegreen));
        downTimeColorArrayList.add(getActivity().getColor(R.color.orange_premium));
        downTimeColorArrayList.add(getActivity().getColor(R.color.scarlet));
        downTimeColorArrayList.add(getActivity().getColor(R.color.yellow));
        downTimeBarDataSet.setColors(downTimeColorArrayList);

        downTimeReportsBarChart.setPinchZoom(false);
        downTimeReportsBarChart.setScaleEnabled(false);
        downTimeReportsBarChart.animateY(1000, Easing.EaseInBounce);

        downTimeReportsBarChart.setFitBars(true);
        downTimeReportsBarChart.setData(downTimeBarData);

        ArrayList<LegendEntry> downtimeLegendEntryArrayList = new ArrayList<>();
        downtimeLegendEntryArrayList.add(new LegendEntry("Week 1", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.bluegreen)));
        downtimeLegendEntryArrayList.add(new LegendEntry("Week 2", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.orange_premium)));
        downtimeLegendEntryArrayList.add(new LegendEntry("Week 3", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.scarlet)));
        downtimeLegendEntryArrayList.add(new LegendEntry("Week 4", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.yellow)));

        downTimeReportsBarChart.getLegend().setCustom(downtimeLegendEntryArrayList);
        downTimeReportsBarChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        downTimeReportsBarChart.getLegend().setXEntrySpace(10);

        Description downTimeBarChartDescription = new Description();
        downTimeBarChartDescription.setText("Downtime Reports per Week");
        downTimeBarChartDescription.setTextSize(18);
        downTimeBarChartDescription.setTextColor(getActivity().getColor(R.color.white));
        downTimeReportsBarChart.setDescription(downTimeBarChartDescription);

        downTimeReportsBarChart.invalidate();

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int initialHeight1 = statisticsConstraintLayout.getHeight();
                statisticsCollapsible.setOnClickListener(v->{
                    if(isStatisticsCollapsed) {
                        expand(statisticsConstraintLayout, initialHeight1);
                        statisticsCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(statisticsConstraintLayout, initialHeight1);
                        statisticsCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isStatisticsCollapsed = !isStatisticsCollapsed;
                });

                int initialHeight2 = adminChartsConstraintLayout.getHeight();
                adminChartsCollapsible.setOnClickListener(v->{
                    if(isChartsCollapsed) {
                        expand(adminChartsConstraintLayout, initialHeight2);
                        adminChartsCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(adminChartsConstraintLayout, initialHeight2);
                        adminChartsCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isChartsCollapsed = !isChartsCollapsed;
                });

                int initialHeight3 = orderStatusPieChartConstraintLayout.getHeight();
                int initialHeight3c = orderStatusPieChart.getHeight();
                orderStatusPieChartCollapsible.setOnClickListener(v->{
                    if(isOrderStatusPieChartCollapsed) {
                        expand(orderStatusPieChartConstraintLayout, initialHeight3);
                        expand(orderStatusPieChart, initialHeight3c);
                        orderStatusPieChartCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(orderStatusPieChartConstraintLayout, initialHeight3);
                        collapse(orderStatusPieChart, initialHeight3c);
                        orderStatusPieChartCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isOrderStatusPieChartCollapsed = !isOrderStatusPieChartCollapsed;
                });

                int initialHeight4 = usersRegisteredBarChartConstraintLayout.getHeight();
                int initialHeight4c = usersRegisteredBarChart.getHeight();
                usersRegisteredBarChartCollapsible.setOnClickListener(v->{
                    if(isUsersRegisteredBarChartCollapsed) {
                        expand(usersRegisteredBarChartConstraintLayout, initialHeight4);
                        expand(usersRegisteredBarChart, initialHeight4c);
                        usersRegisteredBarChartCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(usersRegisteredBarChartConstraintLayout, initialHeight4);
                        collapse(usersRegisteredBarChart, initialHeight4c);
                        usersRegisteredBarChartCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isUsersRegisteredBarChartCollapsed = !isUsersRegisteredBarChartCollapsed;
                });

                int initialHeight5 = companiesRegisteredLineChartConstraintLayout.getHeight();
                int initialHeight5c = companiesRegisteredLineChart.getHeight();
                companiesRegisteredLineChartCollapsible.setOnClickListener(v->{
                    if(isCompaniesRegisteredLineChartCollapsed) {
                        expand(companiesRegisteredLineChartConstraintLayout, initialHeight5);
                        expand(companiesRegisteredLineChart, initialHeight5c);
                        companiesRegisteredLineChartCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(companiesRegisteredLineChartConstraintLayout, initialHeight5);
                        collapse(companiesRegisteredLineChart, initialHeight5c);
                        companiesRegisteredLineChartCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isCompaniesRegisteredLineChartCollapsed = !isCompaniesRegisteredLineChartCollapsed;
                });

                int initialHeight6 = downTimeReportsBarChartConstraintLayout.getHeight();
                int initialHeight6c = downTimeReportsBarChart.getHeight();
                downTimeReportsBarChartCollapsible.setOnClickListener(v->{
                    if(isDownTimeReportsBarChartCollapsed) {
                        expand(downTimeReportsBarChartConstraintLayout, initialHeight6);
                        expand(downTimeReportsBarChart, initialHeight6c);
                        downTimeReportsBarChartCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(downTimeReportsBarChartConstraintLayout, initialHeight6);
                        collapse(downTimeReportsBarChart, initialHeight6c);
                        downTimeReportsBarChartCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isDownTimeReportsBarChartCollapsed = !isDownTimeReportsBarChartCollapsed;
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