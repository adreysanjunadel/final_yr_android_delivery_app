package lk.javainstitute.swiftex.admin.account_management;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import backend.Routes;
import dto.CompanyDetailsDTO;
import dto.Response_DTO;
import lk.javainstitute.swiftex.R;
import lk.javainstitute.swiftex.databinding.FragmentAdminCompanyManagementBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdminCompanyManagementFragment extends Fragment {

    private FragmentAdminCompanyManagementBinding binding;
    private boolean isCreateNewCompanyCollapsed;
    private boolean isExistingCompaniesCollapsed;
    private ConstraintLayout rootContainer;
    private RecyclerView existingCompaniesRecyclerView;
    private CompanyAdapter companyAdapter;
    private ArrayList<CompanyDetailsDTO> companyArrayList = new ArrayList<>();
    private OkHttpClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminCompanyManagementBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        rootContainer = binding.adminCompanyManagementConstraintLayout;
        ImageView adminManagementCreateCompanyCollapsible = binding.adminManagementCreateCompanyCollapsible;
        Button adminManagementCreateNewCompanyButton = binding.adminManagementCreateNewCompanyButton;
        ImageView adminManagementExistingCompaniesCollapsible = binding.adminManagementExistingCompaniesCollapsible;
        ConstraintLayout adminManagementExistingCompaniesConstraintLayout = binding.adminManagementExistingCompaniesConstraintLayout;
        TextView adminManagementExistingCompanyTextView = binding.adminManagementExistingCompanyTextView;

        existingCompaniesRecyclerView = binding.existingCompaniesRecyclerView;
        existingCompaniesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        companyAdapter = new CompanyAdapter(companyArrayList);
        existingCompaniesRecyclerView.setAdapter(companyAdapter);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int initialHeight1 = adminManagementCreateNewCompanyButton.getHeight();
                adminManagementCreateCompanyCollapsible.setOnClickListener(v -> {
                    if (isCreateNewCompanyCollapsed) {
                        expand(adminManagementCreateNewCompanyButton, initialHeight1);
                        adminManagementCreateCompanyCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(adminManagementCreateNewCompanyButton, initialHeight1);
                        adminManagementCreateCompanyCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isCreateNewCompanyCollapsed = !isCreateNewCompanyCollapsed;
                });

                int initialHeight2 = adminManagementExistingCompaniesConstraintLayout.getHeight();
                adminManagementExistingCompaniesCollapsible.setOnClickListener(v -> {
                    if (isExistingCompaniesCollapsed) {
                        expand(adminManagementExistingCompaniesConstraintLayout, initialHeight2);
                        adminManagementExistingCompaniesCollapsible.setImageResource(R.drawable.ic_arrow_up);
                    } else {
                        collapse(adminManagementExistingCompaniesConstraintLayout, initialHeight2);
                        adminManagementExistingCompaniesCollapsible.setImageResource(R.drawable.ic_arrow_down);
                    }
                    isExistingCompaniesCollapsed = !isExistingCompaniesCollapsed;
                });
            }
        });

        adminManagementCreateNewCompanyButton.setOnClickListener(view1 -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentAccountManagementFragmentContainerView, new AdminManageNewCompanyAccountFragment())
                    .addToBackStack(null)
                    .commit();
        });

        adminManagementExistingCompanyTextView.setOnClickListener(view12 -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentAccountManagementFragmentContainerView, new AdminManageExistingCompanyAccountFragment())
                    .addToBackStack(null)
                    .commit();
        });

        client = new OkHttpClient();
        loadCompanies();

        return view;
    }

    private void loadCompanies() {
        String url = Routes.getEnv_url()+"LoadCompanies";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    Type responseType = new TypeToken<Response_DTO>() {}.getType();
                    Response_DTO responseDto = gson.fromJson(responseBody, responseType);

                    if (responseDto.isSuccess()) {
                        String contentJson = gson.toJson(responseDto.getContent());
                        JsonObject jsonObject = gson.fromJson(contentJson, JsonObject.class);
                        JsonArray companyListJsonArray = jsonObject.getAsJsonArray("companyList");

                        Type listType = new TypeToken<List<CompanyDetailsDTO>>() {}.getType();
                        List<CompanyDetailsDTO> companyDetailsDTOs = gson.fromJson(companyListJsonArray.toString(), listType);

                        requireActivity().runOnUiThread(() -> {
                            companyArrayList.clear();
                            companyArrayList.addAll(companyDetailsDTOs);
                            companyAdapter.notifyDataSetChanged();
                        });
                    } else {
                        requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Error loading companies", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Unsuccessful response", Toast.LENGTH_SHORT).show());
                }
            }
        });
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

class CompanyAdapter extends RecyclerView.Adapter<CompanyViewHolder> {

    ArrayList<CompanyDetailsDTO> companyArrayList;

    public CompanyAdapter(ArrayList<CompanyDetailsDTO> companyArrayList) {
        this.companyArrayList = companyArrayList;
    }

    @NonNull
    @Override
    public CompanyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View companyView = layoutInflater.inflate(R.layout.company_item, parent, false);
        return new CompanyViewHolder(companyView);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyViewHolder holder, int position) {
        CompanyDetailsDTO companyDetailsDTO = companyArrayList.get(position);
        holder.companyNameTextView.setText(companyDetailsDTO.getName());
        holder.companyIndustryTextView.setText(companyDetailsDTO.getIndustry());
    }

    @Override
    public int getItemCount() {
        return companyArrayList.size();
    }
}

class CompanyViewHolder extends RecyclerView.ViewHolder {
    TextView companyNameTextView;
    TextView companyIndustryTextView;

    public CompanyViewHolder(@NonNull View itemView) {
        super(itemView);
        companyNameTextView = itemView.findViewById(R.id.companyNameTextView);
        companyIndustryTextView = itemView.findViewById(R.id.companyIndustryTextView);
    }
}