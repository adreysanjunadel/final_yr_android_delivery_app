package lk.javainstitute.swiftex.user.analytics;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

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
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import backend.Routes;
import dto.PaymentValidationRequestDTO;
import dto.PaymentValidationResponseDTO;
import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.databinding.FragmentUserAnalyticsBinding;
import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.Item; // Import Item
import lk.payhere.androidsdk.model.StatusResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserAnalyticsFragment extends Fragment {

    private FragmentUserAnalyticsBinding binding;
    private boolean isUserPieChartCollapsed = false;
    private boolean isUserBarChartCollapsed = false;
    private ConstraintLayout rootContainer;
    private StatusResponse paymentStatusResponse;

    private static final int PAYHERE_REQUEST = 11001;
    private static final String TAG = "UserAnalyticsFragment";

    private ActivityResultLauncher<Intent> payHereLauncher;

    private static final String CHANNEL_ID = "payment_status_channel";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createNotificationChannel();

        payHereLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
                            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
                            if (response != null && response.isSuccess()) {
                                String msg = "Payment successful: " + response.getData().toString();
                                Log.d(TAG, msg);
                                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

                                savePaymentStatus(true);
                                paymentStatusResponse = response.getData();

                                sendPaymentResponseToBackend();

                                int initialHeight2 = binding.userBarChartConstraintLayout.getHeight();
                                int initialHeightc2 = binding.userBarChart.getHeight();
                                expand(binding.userBarChartConstraintLayout, initialHeight2);
                                expand(binding.userBarChart, initialHeightc2);
                                binding.userBarChartCollapsible.setImageResource(R.drawable.ic_arrow_up);
                                isUserBarChartCollapsed = false;

                            } else {
                                String msg = "Payment failed: " + (response != null ? response.toString() : "No response");
                                Log.e(TAG, msg);
                                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        Intent data = result.getData();
                        String msg = "Payment canceled: " + (data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT) ? ((PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT)).toString() : "User canceled");
                        Log.w(TAG, msg);
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserAnalyticsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        rootContainer = binding.userAnalyticsFragmentConstraintLayout;

        PieChart userPieChart = binding.userPieChart;

        ArrayList<PieEntry> pieEntryArrayList = new ArrayList<>();
        pieEntryArrayList.add(new PieEntry(35, "Android"));
        pieEntryArrayList.add(new PieEntry(10, "Apple"));
        pieEntryArrayList.add(new PieEntry(25, "Windows"));
        pieEntryArrayList.add(new PieEntry(30, "Linux"));

        PieDataSet pieDataSet = new PieDataSet(pieEntryArrayList, "Devices");

        ArrayList<Integer> colorArraylist = new ArrayList<>();
        colorArraylist.add(getActivity().getColor(R.color.marine));
        colorArraylist.add(getActivity().getColor(R.color.scarlet));
        colorArraylist.add(getActivity().getColor(R.color.bluegreen));
        colorArraylist.add(getActivity().getColor(R.color.lighterblue));
        pieDataSet.setColors(colorArraylist);

        PieData pieData = new PieData();
        pieData.setDataSet(pieDataSet);
        pieData.setValueTextSize(18);

        userPieChart.setData(pieData);

        userPieChart.animateY(1000, Easing.EaseInCirc);

        userPieChart.setCenterText("Offers Clicked");
        userPieChart.setCenterTextColor(getActivity().getColor(R.color.lightbluegreen));
        userPieChart.setCenterTextSize(20);

        userPieChart.setDescription(null);
        userPieChart.invalidate();

        BarChart userBarChart = binding.userBarChart;

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

        userBarChart.setPinchZoom(false);
        userBarChart.setScaleEnabled(false);
        userBarChart.animateY(1000, Easing.EaseInBounce);

        userBarChart.setFitBars(true);
        userBarChart.setData(barData);

        ArrayList<LegendEntry> legendEntryArrayList = new ArrayList<>();
        legendEntryArrayList.add(new LegendEntry("Monday", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.bluegreen)));
        legendEntryArrayList.add(new LegendEntry("Tuesday", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.orange_premium)));
        legendEntryArrayList.add(new LegendEntry("Wednesday", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.scarlet)));
        legendEntryArrayList.add(new LegendEntry("Thursday", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.yellow)));
        legendEntryArrayList.add(new LegendEntry("Friday", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getActivity().getColor(R.color.marine)));

        userBarChart.getLegend().setCustom(legendEntryArrayList);
        userBarChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        userBarChart.getLegend().setXEntrySpace(10);

        Description barChartDescription = new Description();
        barChartDescription.setText("Logins per week");
        barChartDescription.setTextSize(18);
        barChartDescription.setTextColor(getActivity().getColor(R.color.white));
        userBarChart.setDescription(barChartDescription);

        userBarChart.invalidate();

        ImageView userPieChartCollapsible = binding.userPieChartCollapsible;
        ImageView userBarChartCollapsible = binding.userBarChartCollapsible;

        ConstraintLayout userPieChartConstraintLayout = binding.userPieChartConstraintLayout;
        ConstraintLayout userBarChartConstraintLayout = binding.userBarChartConstraintLayout;

        binding.userBarChartConstraintLayout.setVisibility(View.GONE);
        binding.userBarChart.setVisibility(View.GONE);
        binding.userBarChartCollapsible.setImageResource(R.drawable.ic_arrow_down);
        isUserBarChartCollapsed = true;

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int initialHeight1 = userPieChartConstraintLayout.getHeight();
                int initialHeightc1 = binding.userPieChart.getHeight();
                userPieChartCollapsible.setOnClickListener(v -> {
                    if (isUserPieChartCollapsed) {
                        expand(userPieChartConstraintLayout, initialHeight1);
                        expand(binding.userPieChart, initialHeightc1);
                        userPieChartCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(userPieChartConstraintLayout, initialHeight1);
                        collapse(binding.userPieChart, initialHeightc1);
                        userPieChartCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isUserPieChartCollapsed = !isUserPieChartCollapsed;
                });

                int initialHeight2 = userBarChartConstraintLayout.getHeight();
                int initialHeightc2 = binding.userBarChart.getHeight();

                userBarChartCollapsible.setOnClickListener(v -> {
                    if (isUserBarChartCollapsed) {
                        if (isPaymentMade()) {
                            expand(userBarChartConstraintLayout, initialHeight2);
                            expand(binding.userBarChart, initialHeightc2);
                            userBarChartCollapsible.setImageResource(R.drawable.ic_arrow_up);
                            isUserBarChartCollapsed = false;
                        } else {
                            double amount = 900;
                            initiatePayment(amount);
                        }
                    } else {
                        collapse(userBarChartConstraintLayout, initialHeight2);
                        collapse(binding.userBarChart, initialHeightc2);
                        userBarChartCollapsible.setImageResource(R.drawable.ic_arrow_down);
                        isUserBarChartCollapsed = true;
                    }
                });
            }
        });
        return view;
    }


    private void initiatePayment(double amount) {
        String orderId = UUID.randomUUID().toString();
        String itemDescription = "User Premium";
        String userCountry = "Sri Lanka";
        String merchantId = "1229612";

        SharedPreferences userDetailsSP = requireContext().getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
        Integer userId = userDetailsSP.getInt("User ID", -1);
        String firstName = userDetailsSP.getString("User First Name", "");
        String lastName = userDetailsSP.getString("User Last Name", "");
        String email = userDetailsSP.getString("Email", "");
        String mobile = userDetailsSP.getString("Mobile", "");
        String addressNo = userDetailsSP.getString("User AddressNo", "");
        String addressStreet1 = userDetailsSP.getString("User AddressStreet1", "");
        String addressStreet2 = userDetailsSP.getString("User AddressStreet2", "");
        String city = userDetailsSP.getString("User City", "");
        String address = addressNo + " " + addressStreet1 + " " + addressStreet2;

        InitRequest req = new InitRequest();
        req.getItems().clear();
        req.getItems().add(new Item(null, "Item Name", 1, amount));
        req.setMerchantId(merchantId);
        req.setCurrency("LKR");
        req.setAmount(amount);
        req.setOrderId(orderId);
        req.setItemsDescription(itemDescription);
        req.getCustomer().setFirstName(firstName);
        req.getCustomer().setLastName(lastName);
        req.getCustomer().setEmail(email);
        req.getCustomer().setPhone(mobile);
        req.getCustomer().getAddress().setAddress(address);
        req.getCustomer().getAddress().setCity(city);
        req.getCustomer().getAddress().setCountry(userCountry);

        Log.d(TAG, "Initiating payment: Amount = " + amount);
        Log.d(TAG, "Item price = " + amount);

        Intent intent = new Intent(getActivity(), PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
        payHereLauncher.launch(intent);
    }

    private void sendPaymentResponseToBackend() {
        SharedPreferences userDetailsSP = requireContext().getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
        int userId = userDetailsSP.getInt("User ID", -1);

        if (userId == -1) {
            Log.e(TAG, "User ID not found in SharedPreferences");
            Toast.makeText(getContext(), "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        StatusResponse paymentStatusResponse = getPaymentStatusResponse();

        if (paymentStatusResponse == null) {
            Log.e(TAG, "Payment data not available yet.");
            Toast.makeText(getContext(), "Payment data not available yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        String merchant_id = "1229612";
        String order_id = String.valueOf(paymentStatusResponse.getPaymentNo());
        long amountLong = paymentStatusResponse.getPrice();

        double amountDouble = (double) amountLong / 100.0;
        String payhere_amount = String.format(Locale.US, "%.2f", amountDouble).trim();

        String payhere_currency = paymentStatusResponse.getCurrency().trim();
        String status_code = String.valueOf(paymentStatusResponse.getStatus()).trim();
        String md5sig = paymentStatusResponse.getSign().trim();

        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("userId", String.valueOf(userId));
        paymentData.put("merchant_id", merchant_id.trim());
        paymentData.put("order_id", order_id.trim());
        paymentData.put("payhere_amount", payhere_amount);
        paymentData.put("payhere_currency", payhere_currency);
        paymentData.put("status_code", status_code);
        paymentData.put("md5sig", md5sig);

        Gson gson = new Gson();
        String json = gson.toJson(paymentData);

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, json);

        String url = Routes.getEnv_url() + "IsUserPaymentValid";
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "OkHttp error: " + e.getMessage());
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error communicating with server", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                showPaymentSuccessNotification();
                String responseBody = response.body().string();
                try {
                    PaymentValidationResponseDTO responseDTO = gson.fromJson(responseBody, PaymentValidationResponseDTO.class);
                    if (responseDTO.isValid()) {
                        savePaymentStatus(true);

                        requireActivity().runOnUiThread(() -> {
                            int initialHeight2 = binding.userBarChartConstraintLayout.getHeight();
                            int initialHeightc2 = binding.userBarChart.getHeight();
                            expand(binding.userBarChartConstraintLayout, initialHeight2);
                            expand(binding.userBarChart, initialHeightc2);
                            binding.userBarChartCollapsible.setImageResource(R.drawable.ic_arrow_up);
                            isUserBarChartCollapsed = false;


                        });

                    } else {
                        Log.e(TAG, "Payment validation failed: " + responseDTO.getMessage());
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Payment validation failed: " + responseDTO.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (JsonSyntaxException e) {
                    Log.e(TAG, "Error parsing JSON response: " + e.getMessage());
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error validating payment (JSON)", Toast.LENGTH_SHORT).show();
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error validating payment: " + e.getMessage());
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error validating payment", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }


    private StatusResponse getPaymentStatusResponse() {
        return paymentStatusResponse;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Payment Status"; // Channel name
            String description = "Channel for payment status updates";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showPaymentSuccessNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_courier_request)
                .setContentTitle("Payment Status")
                .setContentText("User Premium Package Payment Successful")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            return;
        }

        notificationManager.notify(generateNotificationId(), builder.build());
    }

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 100; //

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showPaymentSuccessNotification(); // Call the notification function again
            } else {
                Toast.makeText(requireContext(), "Notification permission denied.", Toast.LENGTH_SHORT).show();
                // if so grant permission
            }
        }
    }

    private int generateNotificationId() {
        return (int) System.currentTimeMillis();
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

    private boolean isPaymentMade() {
        if (getContext() == null) return false;

        SharedPreferences prefs = getContext().getSharedPreferences("lk.javainstitute.SwiftEx.data.paymentStatus", Context.MODE_PRIVATE);
        return prefs.getBoolean("barChartUnlocked", false);
    }

    private void savePaymentStatus(boolean status) {
        if (getContext() == null) return;

        SharedPreferences prefs = getContext().getSharedPreferences("lk.javainstitute.SwiftEx.data.paymentStatus", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("barChartUnlocked", status);
        editor.apply();

        SharedPreferences userDetailsSP = getContext().getSharedPreferences("lk.javainstitute.SwiftEx.data", Context.MODE_PRIVATE);
        SharedPreferences.Editor userEdit = userDetailsSP.edit();
        userEdit.putString("User Paid Status", status ? "true" : "false");
        userEdit.apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}