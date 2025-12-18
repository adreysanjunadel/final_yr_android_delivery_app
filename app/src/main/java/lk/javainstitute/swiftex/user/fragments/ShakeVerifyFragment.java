package lk.javainstitute.swiftex.user.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import lk.javainstitute.swiftex.R;

public class ShakeVerifyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment_shake_verify, container, false);

        ImageView imageView = view.findViewById(R.id.gifView);

        Glide.with(this)
                .asGif()
                .load(R.drawable.phone_shake_unscreen)
                .into(imageView);

        return view;
    }
}