package lk.javainstitute.swiftex.admin.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import lk.javainstitute.swiftex.R;

public class AdminShakeVerifyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_shake_verify, container, false);

        ImageView imageView = view.findViewById(R.id.gifView2);

        Glide.with(this)
                .asGif()
                .load(R.drawable.phone_shake_unscreen)
                .into(imageView);

        return view;
    }
}